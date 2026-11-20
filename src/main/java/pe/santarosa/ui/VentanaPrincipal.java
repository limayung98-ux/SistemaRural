package pe.santarosa.ui;

import pe.santarosa.service.ServicioSalud;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Ventana principal con pestañas, siguiendo la distribución solicitada en la referencia. */
public class VentanaPrincipal extends JFrame {
    private static final long serialVersionUID = 1L;

    private final PanelPacientes pacientes;
    private final PanelCitas citas;
    private final PanelMedicamentos medicamentos;
    private final PanelReportes reportes;

    public VentanaPrincipal(ServicioSalud servicio) {
        super("SistemaRural-PE | Centro de Salud Santa Rosa");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(940, 650));
        setSize(1080, 760);
        setLocationRelativeTo(null);

        JPanel raiz = new JPanel(new BorderLayout(0, 8));
        raiz.setBackground(EstilosUI.FONDO);
        raiz.setBorder(new EmptyBorder(8, 10, 10, 10));

        JLabel encabezado = new JLabel("SISTEMA DE GESTIÓN - CENTRO DE SALUD SANTA ROSA", SwingConstants.CENTER);
        encabezado.setFont(new Font("SansSerif", Font.BOLD, 18));
        encabezado.setForeground(EstilosUI.TEXTO);
        encabezado.setBorder(new EmptyBorder(8, 0, 6, 0));
        raiz.add(encabezado, BorderLayout.NORTH);

        pacientes = new PanelPacientes(servicio, this::refrescarTodo);
        citas = new PanelCitas(servicio, this::refrescarTodo);
        medicamentos = new PanelMedicamentos(servicio, this::refrescarTodo);
        reportes = new PanelReportes(servicio);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Pacientes", pacientes);
        pestanas.addTab("Citas", citas);
        pestanas.addTab("Medicamentos", medicamentos);
        pestanas.addTab("Reportes", reportes);
        pestanas.setFont(new Font("SansSerif", Font.BOLD, 12));
        raiz.add(pestanas, BorderLayout.CENTER);

        setContentPane(raiz);
    }

    private void refrescarTodo() {
        pacientes.refrescar();
        citas.refrescar();
        medicamentos.refrescar();
        reportes.refrescar();
    }
}
