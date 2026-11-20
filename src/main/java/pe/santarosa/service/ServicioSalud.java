package pe.santarosa.service;

import pe.santarosa.factory.FabricaRegistros;
import pe.santarosa.model.*;
import pe.santarosa.repository.RepositorioSalud;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/** Casos de uso del sistema. Mantiene las reglas fuera de la interfaz gráfica. */
public class ServicioSalud {
    private static final int ITERACIONES_HASH = 120_000;
    private static final int LONGITUD_HASH = 256;
    private final RepositorioSalud repositorio;
    private final DatosSistema datos;
    private final SecureRandom random = new SecureRandom();

    public ServicioSalud(RepositorioSalud repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio, "El repositorio es obligatorio.");
        try { this.datos = repositorio.cargar(); }
        catch (IOException ex) { throw new IllegalStateException("No se pudieron cargar los datos locales.", ex); }
    }

    public Paciente registrarPaciente(String nombres, int edad, String documento, String telefono) {
        if (documento == null || !documento.matches("\\d{8}"))
            throw new IllegalArgumentException("El DNI debe contener exactamente 8 dígitos.");
        for (Paciente existente : datos.getPacientes()) {
            if (MessageDigest.isEqual(derivarHash(documento, existente.getDocumentoSalt()), existente.getDocumentoHash().getBytes(StandardCharsets.UTF_8)))
                throw new IllegalArgumentException("Ya existe un paciente registrado con ese DNI.");
        }
        byte[] sal = new byte[16]; random.nextBytes(sal);
        String salt = Base64.getEncoder().encodeToString(sal);
        String hash = new String(derivarHash(documento, salt), StandardCharsets.UTF_8);
        Paciente paciente = FabricaRegistros.crearPaciente(nombres, edad, hash, salt, documento.substring(4), telefono);
        datos.add(paciente); persistir(); return paciente;
    }

    private byte[] derivarHash(String documento, String saltBase64) {
        PBEKeySpec spec = new PBEKeySpec(documento.toCharArray(), Base64.getDecoder().decode(saltBase64), ITERACIONES_HASH, LONGITUD_HASH);
        try {
            return HexFormat.of().formatHex(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded()).getBytes(StandardCharsets.UTF_8);
        } catch (Exception ex) { throw new IllegalStateException("No se pudo proteger el documento.", ex); }
        finally { spec.clearPassword(); }
    }

    public Medicamento registrarMedicamento(String nombre, String presentacion, int stock,
                                             int minimo, LocalDate vencimiento) {
        boolean duplicado = datos.getMedicamentos().stream().anyMatch(m -> m.getNombre().equalsIgnoreCase(nombre)
                && m.getPresentacion().equalsIgnoreCase(presentacion));
        if (duplicado) throw new IllegalArgumentException("Ese medicamento ya está registrado con la misma presentación.");
        Medicamento medicamento = FabricaRegistros.crearMedicamento(nombre, presentacion, stock, minimo, vencimiento);
        datos.add(medicamento); persistir(); return medicamento;
    }

    public void ajustarInventario(String medicamentoId, TipoMovimiento tipo, int cantidad) {
        Medicamento medicamento = obtenerMedicamento(medicamentoId);
        if (tipo == TipoMovimiento.INGRESO) medicamento.ingresar(cantidad);
        else if (tipo == TipoMovimiento.SALIDA) medicamento.entregar(cantidad);
        else throw new IllegalArgumentException("Tipo de movimiento no reconocido.");
        datos.add(FabricaRegistros.crearMovimiento(medicamentoId, tipo, cantidad));
        persistir();
    }

    public Cita registrarCita(String pacienteId, String profesional, LocalDate fecha, LocalTime hora, String motivo) {
        obtenerPaciente(pacienteId);
        if (fecha == null || fecha.isBefore(LocalDate.now())) throw new IllegalArgumentException("La fecha no puede ser anterior a hoy.");
        if (profesional == null || profesional.isBlank()) throw new IllegalArgumentException("Seleccione al profesional de salud.");
        boolean ocupado = datos.getCitas().stream().anyMatch(c -> c.getEstado() != EstadoCita.CANCELADA
                && c.getProfesional().equalsIgnoreCase(profesional.trim())
                && c.getFecha().equals(fecha) && c.getHora().equals(hora));
        if (ocupado) throw new IllegalArgumentException("El profesional ya tiene una cita programada en ese horario.");
        Cita cita = FabricaRegistros.crearCita(pacienteId, profesional, fecha, hora, motivo);
        datos.add(cita); persistir(); return cita;
    }

    public Atencion registrarAtencion(String citaId, String profesional, String observacion) {
        Cita cita = obtenerCita(citaId);
        if (cita.getEstado() != EstadoCita.PROGRAMADA) throw new IllegalArgumentException("Solo se puede atender una cita programada.");
        Atencion atencion = FabricaRegistros.crearAtencion(citaId, profesional, observacion);
        cita.setEstado(EstadoCita.ATENDIDA);
        datos.add(atencion); persistir(); return atencion;
    }

    public void cambiarEstadoCita(String citaId, EstadoCita estado) {
        Cita cita = obtenerCita(citaId);
        cita.setEstado(estado); persistir();
    }

    public Paciente obtenerPaciente(String id) {
        return datos.getPacientes().stream().filter(p -> p.getId().equals(id)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado."));
    }
    public Cita obtenerCita(String id) {
        return datos.getCitas().stream().filter(c -> c.getId().equals(id)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada."));
    }
    public Medicamento obtenerMedicamento(String id) {
        return datos.getMedicamentos().stream().filter(m -> m.getId().equals(id)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Medicamento no encontrado."));
    }

    public List<Paciente> getPacientes() { return datos.getPacientes().stream().toList(); }
    public List<Cita> getCitas() { return datos.getCitas().stream().sorted(Comparator.comparing(Cita::getFecha).thenComparing(Cita::getHora)).toList(); }
    public List<Medicamento> getMedicamentos() { return datos.getMedicamentos().stream().sorted(Comparator.comparing(Medicamento::getNombre)).toList(); }
    public List<Atencion> getAtenciones() { return datos.getAtenciones().stream().toList(); }
    public List<MovimientoInventario> getMovimientos() { return datos.getMovimientos().stream().sorted(Comparator.comparing(MovimientoInventario::getFecha).reversed()).toList(); }

    /** filter: búsqueda por nombre y documento parcialmente visible. */
    public List<Paciente> buscarPacientes(String consulta) {
        String criterio = consulta == null ? "" : consulta.trim().toLowerCase(Locale.ROOT);
        return datos.getPacientes().stream()
                .filter(p -> p.getNombres().toLowerCase(Locale.ROOT).contains(criterio)
                        || p.getDocumentoEnmascarado().contains(criterio))
                .toList();
    }
    /** filter: alerta de productos cuyo stock alcanzó el mínimo. */
    public List<Medicamento> obtenerMedicamentosEnRiesgo() {
        return datos.getMedicamentos().stream().filter(m -> m.stockBajo() || m.vencido()).toList();
    }
    /** filter + map + reduce: conteo funcional de citas según estado. */
    public long contarCitas(EstadoCita estado) {
        return datos.getCitas().stream().filter(c -> c.getEstado() == estado).map(c -> 1L).reduce(0L, Long::sum);
    }
    public ReporteResumen generarResumen() {
        return new ReporteResumen(datos.getPacientes().size(), datos.getCitas().size(),
                contarCitas(EstadoCita.PROGRAMADA), contarCitas(EstadoCita.ATENDIDA),
                datos.getMedicamentos().size(), obtenerMedicamentosEnRiesgo().size());
    }

    /** El reporte exportado contiene cifras agregadas; no incluye nombres ni documentos de pacientes. */
    public String generarTextoReporte(String nombreCentro) {
        ReporteResumen r = generarResumen();
        return "REPORTE GENERAL - " + nombreCentro + System.lineSeparator()
                + "Fecha de generación: " + java.time.LocalDateTime.now() + System.lineSeparator()
                + "Pacientes registrados: " + r.totalPacientes() + System.lineSeparator()
                + "Citas registradas: " + r.totalCitas() + System.lineSeparator()
                + "Citas programadas: " + r.citasProgramadas() + System.lineSeparator()
                + "Citas atendidas: " + r.citasAtendidas() + System.lineSeparator()
                + "Medicamentos registrados: " + r.totalMedicamentos() + System.lineSeparator()
                + "Medicamentos con stock bajo: " + r.medicamentosEnRiesgo() + System.lineSeparator()
                + System.lineSeparator() + "Nota: reporte agregado; no contiene datos identificativos de pacientes.";
    }

    private void persistir() {
        try { repositorio.guardar(datos); }
        catch (IOException ex) { throw new IllegalStateException("No se pudieron guardar los cambios cifrados.", ex); }
    }

    public record ReporteResumen(int totalPacientes, int totalCitas, long citasProgramadas,
                                 long citasAtendidas, int totalMedicamentos, int medicamentosEnRiesgo) { }
}
