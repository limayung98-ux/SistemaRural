package pe.santarosa.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pe.santarosa.model.Paciente;
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
}
