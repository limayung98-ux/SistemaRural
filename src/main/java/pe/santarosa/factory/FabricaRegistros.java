package pe.santarosa.factory;

import pe.santarosa.model.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/** Patrón Factory: concentra la creación de entidades del dominio. */
public final class FabricaRegistros {
    private FabricaRegistros() { }
    public static Paciente crearPaciente(String nombres, int edad, String hash, String salt, String ultimos4, String telefono) {
        return new Paciente(UUID.randomUUID().toString(), nombres, edad, hash, salt, ultimos4, telefono);
    }
    public static Medicamento crearMedicamento(String nombre, String presentacion, String lote, int stock,
                                               int minimo, BigDecimal precio, LocalDate vencimiento) {
        return new Medicamento("MED-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(), nombre,
                presentacion, lote, stock, minimo, precio, vencimiento);
    }
    public static Cita crearCita(String pacienteId, String profesional, LocalDate fecha, LocalTime hora, String motivo) {
        return new Cita(UUID.randomUUID().toString(), pacienteId, profesional, fecha, hora, motivo, EstadoCita.PROGRAMADA);
    }
    public static Atencion crearAtencion(String citaId, String profesional, String observacion) {
        return new Atencion(UUID.randomUUID().toString(), citaId, profesional, observacion, java.time.LocalDateTime.now());
    }
    public static MovimientoInventario crearMovimiento(String medicamentoId, TipoMovimiento tipo, int cantidad, String detalle) {
        return new MovimientoInventario(UUID.randomUUID().toString(), medicamentoId, tipo, cantidad, detalle, java.time.LocalDateTime.now());
    }
}
