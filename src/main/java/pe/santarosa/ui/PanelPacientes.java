package pe.santarosa.ui;

import pe.santarosa.model.Paciente;
import pe.santarosa.service.ServicioSalud;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/** Registro y búsqueda de pacientes; el documento se presenta siempre enmascarado. */
public class PanelPacientes extends JPanel {
    private static final long serialVersionUID = 1L;

    private final ServicioSalud servicio;
    private final Runnable alCambiar;
    private final JTextField nombres = EstilosUI.campo(23);
    private final JTextField dni = EstilosUI.campo(9);
    private final JTextField edad = EstilosUI.campo(4);
    private final JTextField telefono = EstilosUI.campo(11);
    private final JTextField buscar = EstilosUI.campo(22);
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"Nombre y apellidos", "Documento", "Edad", "Teléfono"}, 0) {
        @Override public boolean isCellEditable(int fila, int columna) { return false; }
    };
    private final JTable tabla = new JTable(modelo);

    public PanelPacientes(ServicioSalud servicio, Runnable alCambiar) {
        this.servicio = servicio;
        this.alCambiar = alCambiar;
        setLayout(new BorderLayout(8, 8));
        setBackground(EstilosUI.FONDO);
        setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel zonaSuperior = new JPanel();
        zonaSuperior.setBackground(EstilosUI.FONDO);
        zonaSuperior.setLayout(new BoxLayout(zonaSuperior, BoxLayout.Y_AXIS));

        JPanel formulario = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 6));
        formulario.setBackground(EstilosUI.FONDO);
        formulario.setBorder(BorderFactory.createTitledBorder("Datos del paciente"));
        formulario.add(new JLabel("Nombre y apellidos:"));
        formulario.add(nombres);
        formulario.add(new JLabel("DNI:"));
        formulario.add(dni);
        formulario.add(new JLabel("Edad:"));
        formulario.add(edad);
        formulario.add(new JLabel("Teléfono:"));
        formulario.add(telefono);
        JButton registrar = EstilosUI.boton("Registrar paciente");
        registrar.addActionListener(e -> registrar());
        formulario.add(registrar);
        zonaSuperior.add(formulario);

        JPanel busqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 7));
        busqueda.setBackground(EstilosUI.FONDO);
        busqueda.add(new JLabel("Buscar paciente:"));
        busqueda.add(buscar);
        JButton filtrar = EstilosUI.botonSecundario("Buscar");
        filtrar.addActionListener(e -> refrescar());
        buscar.addActionListener(e -> refrescar());
        JButton limpiar = EstilosUI.botonSecundario("Limpiar búsqueda");
        limpiar.addActionListener(e -> { buscar.setText(""); refrescar(); });
        busqueda.add(filtrar);
        busqueda.add(limpiar);
        zonaSuperior.add(busqueda);

        add(zonaSuperior, BorderLayout.NORTH);
        EstilosUI.tabla(tabla);
        add(EstilosUI.scroll(tabla), BorderLayout.CENTER);
        refrescar();
    }

    private void registrar() {
        try {
            int anios = Integer.parseInt(edad.getText().trim());
            servicio.registrarPaciente(nombres.getText(), anios, dni.getText().trim(), telefono.getText().trim());
            limpiarFormulario();
            refrescar();
            alCambiar.run();
            EstilosUI.aviso(this, "Paciente registrado correctamente. El DNI se conserva protegido y se muestra parcialmente oculto.", false);
        } catch (NumberFormatException ex) {
            EstilosUI.aviso(this, "La edad debe ser un número entero.", true);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            EstilosUI.aviso(this, ex.getMessage(), true);
        }
    }

    private void limpiarFormulario() {
        nombres.setText("");
        dni.setText("");
        edad.setText("");
        telefono.setText("");
        nombres.requestFocusInWindow();
    }

    public void refrescar() {
        modelo.setRowCount(0);
        for (Paciente paciente : servicio.buscarPacientes(buscar.getText())) {
            modelo.addRow(new Object[]{paciente.getNombres(), paciente.getDocumentoEnmascarado(),
                    paciente.getEdad(), paciente.getTelefonoEnmascarado()});
        }
    }
}
