package pe.santarosa.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pe.santarosa.model.Paciente;
import pe.santarosa.model.Medicamento;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.security.SecureRandom;
import java.time.LocalDate;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import pe.santarosa.service.ServicioSalud;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class RepositorioSaludCifradoTest {
    @TempDir Path temporal;

    @Test void guardaCifradoYRecuperaRegistros() throws Exception {
        RepositorioSaludCifrado repositorio = new RepositorioSaludCifrado(temporal);
        ServicioSalud servicio = new ServicioSalud(repositorio);
        servicio.registrarPaciente("Paciente de Prueba", 30, "22334455", "900222333");

        byte[] contenido = Files.readAllBytes(temporal.resolve("registros.salud.enc"));
        String legible = new String(contenido, StandardCharsets.UTF_8);
        assertFalse(legible.contains("Paciente de Prueba"));
        assertFalse(legible.contains("22334455"));

        ServicioSalud recargado = new ServicioSalud(new RepositorioSaludCifrado(temporal));
        assertEquals(1, recargado.getPacientes().size());
        Paciente paciente = recargado.getPacientes().get(0);
        assertEquals("****4455", paciente.getDocumentoEnmascarado());
    }

    @Test void leeRegistrosDelFormatoAnteriorYLosMigraAlGuardar() throws Exception {
        KeyGenerator generador = KeyGenerator.getInstance("AES");
        generador.init(256);
        SecretKey clave = generador.generateKey();
        Files.write(temporal.resolve("clave.local"), clave.getEncoded());

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(bytes)) {
            out.writeInt(1); // Formato anterior.
            out.writeInt(0); // Pacientes.
            out.writeInt(1); // Medicamentos.
            out.writeUTF("MED-ANTERIOR");
            out.writeUTF("Paracetamol");
            out.writeUTF("Tableta 500 mg");
            out.writeUTF("LOTE-ANTERIOR");
            out.writeInt(18);
            out.writeInt(4);
            out.writeUTF("0.50");
            out.writeUTF(LocalDate.now().plusMonths(8).toString());
            out.writeInt(0); // Citas.
            out.writeInt(0); // Atenciones.
            out.writeInt(0); // Movimientos.
        }
        byte[] nonce = new byte[12];
        new SecureRandom().nextBytes(nonce);
        Cipher cifrador = Cipher.getInstance("AES/GCM/NoPadding");
        cifrador.init(Cipher.ENCRYPT_MODE, clave, new GCMParameterSpec(128, nonce));
        byte[] cifrado = cifrador.doFinal(bytes.toByteArray());
        byte[] sobre = new byte[nonce.length + cifrado.length];
        System.arraycopy(nonce, 0, sobre, 0, nonce.length);
        System.arraycopy(cifrado, 0, sobre, nonce.length, cifrado.length);
        Files.write(temporal.resolve("registros.salud.enc"), sobre);

        ServicioSalud servicio = new ServicioSalud(new RepositorioSaludCifrado(temporal));
        Medicamento anterior = servicio.getMedicamentos().get(0);
        assertEquals("Paracetamol", anterior.getNombre());
        assertEquals(18, anterior.getStock());
        servicio.registrarMedicamento("Alcohol medicinal 70%", "Frasco 250 ml", 10, 2,
                LocalDate.now().plusMonths(12));

        ServicioSalud migrado = new ServicioSalud(new RepositorioSaludCifrado(temporal));
        assertEquals(2, migrado.getMedicamentos().size());
        assertEquals(18, migrado.getMedicamentos().stream()
                .filter(m -> m.getNombre().equals("Paracetamol")).findFirst().orElseThrow().getStock());
    }

}
