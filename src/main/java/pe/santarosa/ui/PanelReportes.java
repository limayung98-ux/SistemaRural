package pe.santarosa.ui;

import pe.santarosa.config.ConfiguracionAplicacion;
import pe.santarosa.model.Atencion;
import pe.santarosa.model.Cita;
import pe.santarosa.model.Paciente;
import pe.santarosa.service.ServicioSalud;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/** Reportes agregados y comprobantes individuales con formato de recibo imprimible. */
public class PanelReportes extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("hh:mm a", Locale.US);
    private final ServicioSalud servicio;
    private final JComboBox<CitaOpcion> selectorCita = new JComboBox<>();
    private final JTextArea comprobante = crearArea();
    private final JTextArea resumen = crearArea();
    private String comprobanteActual = "";
    private String resumenActual = "";

    public PanelReportes(ServicioSalud servicio) {
        this.servicio = servicio;
        setLayout(new BorderLayout(8, 8));
        setBackground(EstilosUI.FONDO);
        setBorder(new EmptyBorder(8, 8, 8, 8));

        JTabbedPane subPestanas = new JTabbedPane();
        subPestanas.addTab("Comprobante de cita", crearPanelComprobante());
        subPestanas.addTab("Resumen general", crearPanelResumen());
        add(subPestanas, BorderLayout.CENTER);
        refrescar();
    }

    private JPanel crearPanelComprobante() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(EstilosUI.FONDO);
        JPanel superior = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 6));
        superior.setBackground(EstilosUI.FONDO);
        superior.add(new JLabel("Seleccionar cita:"));
        selectorCita.setPreferredSize(new Dimension(460, 27));
        selectorCita.addActionListener(e -> generarComprobante());
        superior.add(selectorCita);
        JButton generar = EstilosUI.boton("Generar comprobante");
        generar.addActionListener(e -> generarComprobante());
        superior.add(generar);
        panel.add(superior, BorderLayout.NORTH);

        comprobante.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        comprobante.setBorder(new EmptyBorder(15, 18, 15, 18));
        panel.add(new JScrollPane(comprobante), BorderLayout.CENTER);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 5));
        acciones.setBackground(EstilosUI.FONDO);
        JButton imprimir = EstilosUI.boton("Imprimir");
        imprimir.addActionListener(e -> imprimirComprobante());
        JButton exportar = EstilosUI.botonSecundario("Exportar TXT");
        exportar.addActionListener(e -> exportarComprobante());
        acciones.add(imprimir);
        acciones.add(exportar);
        panel.add(acciones, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPanelResumen() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(EstilosUI.FONDO);
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 5));
        acciones.setBackground(EstilosUI.FONDO);
        JButton generar = EstilosUI.boton("Actualizar reporte");
        generar.addActionListener(e -> generarResumen());
        JButton exportar = EstilosUI.botonSecundario("Exportar resumen TXT");
        exportar.addActionListener(e -> exportarResumen());
        acciones.add(generar);
        acciones.add(exportar);
        panel.add(acciones, BorderLayout.NORTH);
        resumen.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        resumen.setBorder(new EmptyBorder(15, 18, 15, 18));
        panel.add(new JScrollPane(resumen), BorderLayout.CENTER);
        return panel;
    }

    private static JTextArea crearArea() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(false);
        area.setBackground(Color.WHITE);
        area.setForeground(new Color(35, 42, 48));
        return area;
    }

    private void generarComprobante() {
        CitaOpcion seleccion = (CitaOpcion) selectorCita.getSelectedItem();
        if (seleccion == null) {
            comprobanteActual = "Aún no hay citas registradas.\nRegistre una cita en la pestaña Citas para generar su comprobante.";
            comprobante.setText(comprobanteActual);
            return;
        }
        try {
            Cita cita = servicio.obtenerCita(seleccion.id());
            Paciente paciente = servicio.obtenerPaciente(cita.getPacienteId());
            comprobanteActual = construirComprobante(cita, paciente);
            comprobante.setText(comprobanteActual);
            comprobante.setCaretPosition(0);
        } catch (IllegalArgumentException ex) {
            comprobanteActual = "No fue posible recuperar la cita seleccionada.";
            comprobante.setText(comprobanteActual);
        }
    }

    private String construirComprobante(Cita cita, Paciente paciente) {
        String nombreCentro = ConfiguracionAplicacion.getInstancia().getNombreCentro();
        String codigo = "SR-" + cita.getId().substring(0, 8).toUpperCase(Locale.ROOT);
        String separador = "======================================================================";
        StringBuilder texto = new StringBuilder();
        texto.append(separador).append('\n');
        texto.append("                 ").append(nombreCentro.toUpperCase(Locale.ROOT)).append('\n');
        texto.append("                    COMPROBANTE DE CITA\n");
        texto.append(separador).append("\n\n");
        texto.append(String.format("Comprobante N.°: %-25s Emisión: %s%n", codigo,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        texto.append("--------------------------------------------------------------------\n");
        texto.append("DATOS DEL PACIENTE\n");
        texto.append("Nombre y apellidos : ").append(paciente.getNombres()).append('\n');
        texto.append("Documento          : ").append(paciente.getDocumentoEnmascarado()).append('\n');
        texto.append("Edad               : ").append(paciente.getEdad()).append(" años\n");
        texto.append("--------------------------------------------------------------------\n");
        texto.append("DETALLE DE LA CITA\n");
        texto.append("Profesional        : ").append(cita.getProfesional()).append('\n');
        texto.append("Fecha              : ").append(cita.getFecha().format(FECHA)).append('\n');
        texto.append("Hora               : ").append(cita.getHora().format(HORA)).append('\n');
        texto.append("Motivo             : ").append(cita.getMotivo()).append('\n');
        texto.append("Estado             : ").append(cita.getEstado()).append('\n');
        List<Atencion> atenciones = servicio.getAtenciones().stream()
                .filter(atencion -> atencion.getCitaId().equals(cita.getId())).toList();
        if (!atenciones.isEmpty()) {
            Atencion atencion = atenciones.get(atenciones.size() - 1);
            texto.append("--------------------------------------------------------------------\n");
            texto.append("REGISTRO DE ATENCIÓN\n");
            texto.append("Observación        : ").append(atencion.getObservacion()).append('\n');
            texto.append("Fecha de atención  : ").append(atencion.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append('\n');
        }
        texto.append("--------------------------------------------------------------------\n");
        texto.append("\n¡Gracias por confiar en nuestro equipo de salud!\n");
        texto.append("Le deseamos bienestar y una pronta mejoría.\n");
        texto.append("Su salud es nuestra mayor prioridad.\n");
        texto.append(separador).append('\n');
        texto.append(separador).append('\n');
        return texto.toString();
    }

    private void imprimirComprobante() {
        if (comprobanteActual.isBlank()) generarComprobante();
        try {
            comprobante.print();
        } catch (java.awt.print.PrinterException ex) {
            EstilosUI.aviso(this, "No se pudo imprimir el comprobante: " + ex.getMessage(), true);
        }
    }

    private void exportarComprobante() {
        if (comprobanteActual.isBlank()) generarComprobante();
        if (selectorCita.getSelectedItem() == null) {
            EstilosUI.aviso(this, "Primero registre o seleccione una cita.", true);
            return;
        }
        JFileChooser selector = new JFileChooser();
        selector.setSelectedFile(new java.io.File("comprobante_cita_santa_rosa.txt"));
        if (selector.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        guardarTexto(selector.getSelectedFile().toPath(), comprobanteActual, "Comprobante exportado correctamente.");
    }

    private void generarResumen() {
        resumenActual = servicio.generarTextoReporte(ConfiguracionAplicacion.getInstancia().getNombreCentro());
        resumen.setText(resumenActual);
        resumen.setCaretPosition(0);
    }

    private void exportarResumen() {
        if (resumenActual.isBlank()) generarResumen();
        JFileChooser selector = new JFileChooser();
        selector.setSelectedFile(new java.io.File("reporte_general_santa_rosa.txt"));
        if (selector.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        guardarTexto(selector.getSelectedFile().toPath(), resumenActual,
                "Resumen exportado. El archivo contiene indicadores generales y no incluye nombres ni DNI.");
    }

    private void guardarTexto(Path destino, String contenido, String mensaje) {
        try {
            Files.writeString(destino, contenido, StandardCharsets.UTF_8);
            EstilosUI.aviso(this, mensaje + "\n" + destino, false);
        } catch (IOException ex) {
            EstilosUI.aviso(this, "No se pudo exportar el archivo: " + ex.getMessage(), true);
        }
    }

    public void refrescar() {
        String anterior = selectorCita.getSelectedItem() instanceof CitaOpcion opcion ? opcion.id() : null;
        selectorCita.removeAllItems();
        List<Cita> citas = servicio.getCitas();
        for (Cita cita : citas) {
            Paciente paciente = servicio.getPacientes().stream()
                    .filter(p -> p.getId().equals(cita.getPacienteId())).findFirst().orElse(null);
            String nombre = paciente == null ? "Paciente no disponible" : paciente.getNombres();
            selectorCita.addItem(new CitaOpcion(cita.getId(), nombre + " | " + cita.getFecha().format(FECHA)
                    + " | " + cita.getHora().format(HORA)));
        }
        if (anterior != null) {
            for (int i = 0; i < selectorCita.getItemCount(); i++) {
                if (selectorCita.getItemAt(i).id().equals(anterior)) {
                    selectorCita.setSelectedIndex(i);
                    break;
                }
            }
        }
        if (selectorCita.getSelectedIndex() < 0 && selectorCita.getItemCount() > 0) {
            selectorCita.setSelectedIndex(selectorCita.getItemCount() - 1);
        }
        generarComprobante();
        generarResumen();
    }

    private record CitaOpcion(String id, String etiqueta) {
        @Override public String toString() { return etiqueta; }
    }
}
