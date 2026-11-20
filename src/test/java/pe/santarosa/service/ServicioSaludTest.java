package pe.santarosa.service;

import org.junit.jupiter.api.Test;
import pe.santarosa.model.*;
import java.time.LocalDate;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.*;

class ServicioSaludTest {
    @Test void rechazaDniDuplicado() {
        ServicioSalud servicio = new ServicioSalud(new RepositorioMemoria());
        servicio.registrarPaciente("Ana Torres", 20, "12345678", "900111222");
        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarPaciente("Ana Torres", 20, "12345678", "900111222"));
    }

    @Test void evitaCruceDeHorarioDelMismoProfesional() {
        ServicioSalud servicio = new ServicioSalud(new RepositorioMemoria());
        Paciente p = servicio.registrarPaciente("Luis Ramos", 34, "87654321", "900111333");
        LocalDate fecha = LocalDate.now().plusDays(2);
        servicio.registrarCita(p.getId(), "Medicina general", fecha, LocalTime.of(9, 0), "Control general");
        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarCita(p.getId(), "Medicina general", fecha, LocalTime.of(9, 0), "Consulta"));
    }

    @Test void impideSalidaMayorAlStockYActualizaExistencias() {
        ServicioSalud servicio = new ServicioSalud(new RepositorioMemoria());
        Medicamento m = servicio.registrarMedicamento("Paracetamol", "Tableta 500 mg", 10, 2,
                LocalDate.now().plusMonths(10));
        servicio.ajustarInventario(m.getId(), TipoMovimiento.SALIDA, 3);
        assertEquals(7, servicio.obtenerMedicamento(m.getId()).getStock());
        assertThrows(IllegalArgumentException.class,
                () -> servicio.ajustarInventario(m.getId(), TipoMovimiento.SALIDA, 20));
    }

    @Test void reporteNoExponeElDocumentoCompleto() {
        ServicioSalud servicio = new ServicioSalud(new RepositorioMemoria());
        servicio.registrarPaciente("Paciente Ficticio", 25, "11223344", "900111222");
        String reporte = servicio.generarTextoReporte("Centro de prueba");
        assertTrue(reporte.contains("Pacientes registrados: 1"));
        assertFalse(reporte.contains("11223344"));
    }

    @Test void registraAtencionYActualizaEstadoDeLaCita() {
        ServicioSalud servicio = new ServicioSalud(new RepositorioMemoria());
        Paciente paciente = servicio.registrarPaciente("Rosa Medina", 42, "33445566", "900333444");
        Cita cita = servicio.registrarCita(paciente.getId(), "Medicina general",
                LocalDate.now().plusDays(1), LocalTime.of(10, 30), "Control médico");

        Atencion atencion = servicio.registrarAtencion(cita.getId(), "Medicina general", "Control de seguimiento");

        assertNotNull(atencion.getId());
        assertEquals(EstadoCita.ATENDIDA, servicio.obtenerCita(cita.getId()).getEstado());
        assertEquals(1, servicio.getAtenciones().size());
    }

    @Test void buscaPacientesPorNombreYDocumentoEnmascarado() {
        ServicioSalud servicio = new ServicioSalud(new RepositorioMemoria());
        servicio.registrarPaciente("Carlos Quispe", 28, "55667788", "900444555");

        assertEquals(1, servicio.buscarPacientes("Carlos").size());
        assertEquals(1, servicio.buscarPacientes("7788").size());
        assertTrue(servicio.buscarPacientes("no existe").isEmpty());
    }


    @Test void cuentaCitasProgramadasConOperacionesFuncionales() {
        ServicioSalud servicio = new ServicioSalud(new RepositorioMemoria());
        Paciente paciente = servicio.registrarPaciente("Paciente de Prueba", 27, "66778899", "900555666");
        servicio.registrarCita(paciente.getId(), "Medicina general", LocalDate.now().plusDays(3),
                LocalTime.of(11, 0), "Control general");
        assertEquals(1L, servicio.contarCitas(EstadoCita.PROGRAMADA));
        assertEquals(0L, servicio.contarCitas(EstadoCita.ATENDIDA));
    }

}
