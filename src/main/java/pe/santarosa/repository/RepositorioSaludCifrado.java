package pe.santarosa.repository;

import pe.santarosa.config.ConfiguracionAplicacion;
import pe.santarosa.model.*;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermission;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.time.*;
import java.util.EnumSet;
import java.util.Set;

/** Persistencia local cifrada con AES/GCM. Los registros no se guardan en texto plano. */
public class RepositorioSaludCifrado implements RepositorioSalud {
    private static final int VERSION = 2;
    private static final int NONCE_BYTES = 12;
    private static final int TAG_BITS = 128;
    private final Path directorio;
    private final Path archivoDatos;
    private final Path archivoClave;
    private final SecureRandom aleatorio = new SecureRandom();

    public RepositorioSaludCifrado() {
        this(ConfiguracionAplicacion.getInstancia().getDirectorioDatos());
    }

    /** Constructor con ruta configurable, útil para pruebas automatizadas. */
    public RepositorioSaludCifrado(Path directorio) {
        this.directorio = directorio;
        this.archivoDatos = directorio.resolve("registros.salud.enc");
        this.archivoClave = directorio.resolve("clave.local");
    }

    @Override public synchronized DatosSistema cargar() throws IOException {
        if (!Files.exists(archivoDatos)) return new DatosSistema();
        try {
            byte[] sobre = Files.readAllBytes(archivoDatos);
            if (sobre.length <= NONCE_BYTES) throw new IOException("El archivo de datos está incompleto.");
            byte[] nonce = java.util.Arrays.copyOfRange(sobre, 0, NONCE_BYTES);
            byte[] cifrado = java.util.Arrays.copyOfRange(sobre, NONCE_BYTES, sobre.length);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, cargarClave(), new GCMParameterSpec(TAG_BITS, nonce));
            return deserializar(cipher.doFinal(cifrado));
        } catch (GeneralSecurityException ex) {
            throw new IOException("No se pudieron descifrar los registros. Verifique la clave local.", ex);
        }
    }

    @Override public synchronized void guardar(DatosSistema datos) throws IOException {
        if (datos == null) throw new IllegalArgumentException("No se puede guardar un estado vacío.");
        Files.createDirectories(directorio);
        try {
            byte[] nonce = new byte[NONCE_BYTES];
            aleatorio.nextBytes(nonce);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, cargarClave(), new GCMParameterSpec(TAG_BITS, nonce));
            byte[] cifrado = cipher.doFinal(serializar(datos));
            byte[] sobre = new byte[nonce.length + cifrado.length];
            System.arraycopy(nonce, 0, sobre, 0, nonce.length);
            System.arraycopy(cifrado, 0, sobre, nonce.length, cifrado.length);
            Path temporal = directorio.resolve("registros.salud.tmp");
            Files.write(temporal, sobre, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            protegerPermisos(temporal);
            try {
                Files.move(temporal, archivoDatos, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ex) {
                Files.move(temporal, archivoDatos, StandardCopyOption.REPLACE_EXISTING);
            }
            protegerPermisos(archivoDatos);
        } catch (GeneralSecurityException ex) {
            throw new IOException("No fue posible cifrar los registros locales.", ex);
        }
    }

    private SecretKey cargarClave() throws IOException, GeneralSecurityException {
        if (Files.exists(archivoClave)) {
            byte[] bytes = Files.readAllBytes(archivoClave);
            return new javax.crypto.spec.SecretKeySpec(bytes, "AES");
        }
        Files.createDirectories(directorio);
        KeyGenerator generador = KeyGenerator.getInstance("AES");
        generador.init(256);
        SecretKey clave = generador.generateKey();
        Files.write(archivoClave, clave.getEncoded(), StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
        protegerPermisos(archivoClave);
        return clave;
    }

    private void protegerPermisos(Path archivo) {
        try {
            Set<PosixFilePermission> permisos = EnumSet.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE);
            Files.setPosixFilePermissions(archivo, permisos);
        } catch (UnsupportedOperationException | IOException ignored) {
            // En Windows, los permisos POSIX no están disponibles; se conservan los permisos del usuario actual.
        }
    }

    private byte[] serializar(DatosSistema datos) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(bytes)) {
            out.writeInt(VERSION);
            out.writeInt(datos.getPacientes().size());
            for (Paciente p : datos.getPacientes()) {
                out.writeUTF(p.getId()); out.writeUTF(p.getNombres()); out.writeInt(p.getEdad());
                out.writeUTF(p.getDocumentoHash()); out.writeUTF(p.getDocumentoSalt());
                out.writeUTF(p.getDocumentoEnmascarado().substring(4)); out.writeUTF(p.getTelefono());
            }
            out.writeInt(datos.getMedicamentos().size());
            for (Medicamento m : datos.getMedicamentos()) {
                out.writeUTF(m.getId()); out.writeUTF(m.getNombre()); out.writeUTF(m.getPresentacion());
                out.writeInt(m.getStock()); out.writeInt(m.getMinimo());
                out.writeUTF(m.getFechaVencimiento().toString());
            }
            out.writeInt(datos.getCitas().size());
            for (Cita c : datos.getCitas()) {
                out.writeUTF(c.getId()); out.writeUTF(c.getPacienteId()); out.writeUTF(c.getProfesional());
                out.writeUTF(c.getFecha().toString()); out.writeUTF(c.getHora().toString());
                out.writeUTF(c.getMotivo()); out.writeUTF(c.getEstado().name());
            }
            out.writeInt(datos.getAtenciones().size());
            for (Atencion a : datos.getAtenciones()) {
                out.writeUTF(a.getId()); out.writeUTF(a.getCitaId()); out.writeUTF(a.getProfesional());
                out.writeUTF(a.getObservacion()); out.writeUTF(a.getFecha().toString());
            }
            out.writeInt(datos.getMovimientos().size());
            for (MovimientoInventario m : datos.getMovimientos()) {
                out.writeUTF(m.getId()); out.writeUTF(m.getMedicamentoId()); out.writeUTF(m.getTipo().name());
                out.writeInt(m.getCantidad()); out.writeUTF(m.getFecha().toString());
            }
        }
        return bytes.toByteArray();
    }

    private DatosSistema deserializar(byte[] bytes) throws IOException {
        DatosSistema datos = new DatosSistema();
        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes))) {
            int version = in.readInt();
            if (version != 1 && version != VERSION) throw new IOException("La versión de los datos no es compatible.");
            int totalPacientes = leerCantidad(in);
            for (int i = 0; i < totalPacientes; i++) {
                datos.add(new Paciente(in.readUTF(), in.readUTF(), in.readInt(), in.readUTF(), in.readUTF(), in.readUTF(), in.readUTF()));
            }
            int totalMedicamentos = leerCantidad(in);
            for (int i = 0; i < totalMedicamentos; i++) {
                String id = in.readUTF();
                String nombre = in.readUTF();
                String presentacion = in.readUTF();
                if (version == 1) in.readUTF(); // Descarta lote del formato anterior.
                int stock = in.readInt();
                int minimo = in.readInt();
                if (version == 1) in.readUTF(); // Descarta precio del formato anterior.
                LocalDate vencimiento = LocalDate.parse(in.readUTF());
                datos.add(new Medicamento(id, nombre, presentacion, stock, minimo, vencimiento));
            }
            int totalCitas = leerCantidad(in);
            for (int i = 0; i < totalCitas; i++) {
                datos.add(new Cita(in.readUTF(), in.readUTF(), in.readUTF(), LocalDate.parse(in.readUTF()),
                        LocalTime.parse(in.readUTF()), in.readUTF(), EstadoCita.valueOf(in.readUTF())));
            }
            int totalAtenciones = leerCantidad(in);
            for (int i = 0; i < totalAtenciones; i++) {
                datos.add(new Atencion(in.readUTF(), in.readUTF(), in.readUTF(), in.readUTF(), LocalDateTime.parse(in.readUTF())));
            }
            int totalMovimientos = leerCantidad(in);
            for (int i = 0; i < totalMovimientos; i++) {
                String id = in.readUTF();
                String medicamentoId = in.readUTF();
                TipoMovimiento tipo = TipoMovimiento.valueOf(in.readUTF());
                int cantidad = in.readInt();
                if (version == 1) in.readUTF(); // Descarta el detalle del formato anterior.
                LocalDateTime fecha = LocalDateTime.parse(in.readUTF());
                datos.add(new MovimientoInventario(id, medicamentoId, tipo, cantidad, fecha));
            }
            if (in.available() != 0) throw new IOException("El archivo contiene datos adicionales no reconocidos.");
        } catch (RuntimeException ex) {
            throw new IOException("El contenido cifrado no tiene una estructura válida.", ex);
        }
        return datos;
    }

    private int leerCantidad(DataInputStream in) throws IOException {
        int cantidad = in.readInt();
        if (cantidad < 0 || cantidad > 1_000_000) throw new IOException("Cantidad de registros fuera de rango.");
        return cantidad;
    }
}
