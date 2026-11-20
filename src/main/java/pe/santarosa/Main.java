package pe.santarosa;

import pe.santarosa.repository.RepositorioSaludCifrado;
import pe.santarosa.service.ServicioSalud;
import pe.santarosa.model.Medicamento;
import java.math.BigDecimal;
import java.time.LocalDate;
import pe.santarosa.ui.VentanaPrincipal;
import pe.santarosa.ui.EstilosUI;
import javax.swing.*;

/** Punto de entrada de SistemaRural-PE. */
public final class Main {
    private Main() { }
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { /* Se utiliza el aspecto predeterminado de Swing. */ }
        EstilosUI.configurarTipografia();
        SwingUtilities.invokeLater(() -> {
            try {
                ServicioSalud servicio = new ServicioSalud(new RepositorioSaludCifrado());
                cargarInventarioDeExposicion(servicio);
                new VentanaPrincipal(servicio).setVisible(true);
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "SistemaRural-PE", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    /** Carga un inventario académico de muestra solo cuando el repositorio está vacío. */
    private static void cargarInventarioDeExposicion(ServicioSalud servicio) {
        if (!servicio.getMedicamentos().isEmpty()) return;
        LocalDate vencimiento = LocalDate.now().plusYears(2);
        servicio.registrarMedicamento("Paracetamol", "Tableta 500 mg", "DEMO-PAR-001", 120, 20, new BigDecimal("0.20"), vencimiento);
        servicio.registrarMedicamento("Amoxicilina", "Cápsula 500 mg", "DEMO-AMO-002", 60, 15, new BigDecimal("0.45"), vencimiento);
        servicio.registrarMedicamento("Ibuprofeno", "Tableta 400 mg", "DEMO-IBU-003", 85, 20, new BigDecimal("0.30"), vencimiento);
        servicio.registrarMedicamento("Loratadina", "Tableta 10 mg", "DEMO-LOR-004", 40, 10, new BigDecimal("0.25"), vencimiento);
        servicio.registrarMedicamento("Sales de rehidratación oral", "Sobre", "DEMO-SRO-005", 32, 12, new BigDecimal("1.20"), vencimiento);
        servicio.registrarMedicamento("Alcohol medicinal 70%", "Frasco 250 ml", "DEMO-ALC-006", 24, 8, new BigDecimal("2.50"), vencimiento);
    }
}
