package pe.santarosa;

import pe.santarosa.model.Paciente;
import pe.santarosa.repository.RepositorioSaludCifrado;
import pe.santarosa.service.ServicioSalud;
import pe.santarosa.ui.EstilosUI;
import pe.santarosa.ui.VentanaPrincipal;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalTime;

/** Punto de entrada de la aplicación académica del Centro de Salud Santa Rosa. */
public final class Main {
    private Main() { }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Se utiliza el aspecto predeterminado de Swing.
        }
        EstilosUI.configurarTipografia();
        SwingUtilities.invokeLater(() -> {
            try {
                ServicioSalud servicio = new ServicioSalud(new RepositorioSaludCifrado());
                cargarDatosFicticios(servicio);
                new VentanaPrincipal(servicio).setVisible(true);
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Centro de Salud Santa Rosa", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    /** Agrega registros sintéticos cuando el repositorio está vacío, para facilitar la exposición. */
    private static void cargarDatosFicticios(ServicioSalud servicio) {
        LocalDate vencimiento = LocalDate.now().plusYears(2);
        if (servicio.getMedicamentos().isEmpty()) {
            servicio.registrarMedicamento("Paracetamol", "Tableta 500 mg", 120, 20, vencimiento);
            servicio.registrarMedicamento("Amoxicilina", "Cápsula 500 mg", 60, 15, vencimiento);
            servicio.registrarMedicamento("Ibuprofeno", "Tableta 400 mg", 85, 20, vencimiento);
            servicio.registrarMedicamento("Loratadina", "Tableta 10 mg", 40, 10, vencimiento);
            servicio.registrarMedicamento("Sales de rehidratación oral", "Sobre", 32, 12, vencimiento);
            servicio.registrarMedicamento("Alcohol medicinal 70%", "Frasco 250 ml", 24, 8, vencimiento);
        }
        if (servicio.getPacientes().isEmpty()) {
            Paciente paciente = servicio.registrarPaciente("Paciente Ficticio Uno", 25, "00000000", "900000001");
            servicio.registrarCita(paciente.getId(), "Medicina general", LocalDate.now().plusDays(1),
                    LocalTime.of(9, 0), "Control general");
        }
    }
}
