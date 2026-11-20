package pe.santarosa.ui;

import pe.santarosa.model.Cita;
import pe.santarosa.model.EstadoCita;
import pe.santarosa.model.Paciente;
import pe.santarosa.service.ServicioSalud;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/** Agenda de citas con búsqueda, horas AM/PM y motivos seleccionables. */
public class PanelCitas extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("hh:mm a", Locale.US);

    private final ServicioSalud servicio;
    private final Runnable alCambiar;
    private final JComboBox<PacienteOpcion> paciente = new JComboBox<>();
    private final JComboBox<String> profesional = new JComboBox<>(new String[]{
            "Medicina general", "Enfermería", "Obstetricia", "Odontología"});
    private final JTextField fecha = EstilosUI.campo(10);
    private final JComboBox<String> hora = new JComboBox<>();
    private final JComboBox<String> motivo = new JComboBox<>(new String[]{
            "Consulta general", "Control médico", "Dolor o malestar", "Vacunación",
            "Enfermería / curación", "Odontología", "Control prenatal", "Laboratorio",
            "Seguimiento", "Otro"});
    private final JTextField buscar = EstilosUI.campo(24);
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"Código", "Paciente", "Profesional", "Fecha", "Hora", "Motivo", "Estado", "ID interno"}, 0) {
        @Override public boolean isCellEditable(int fila, int columna) { return false; }
    };
    private final JTable tabla = new JTable(modelo);

    public PanelCitas(ServicioSalud servicio, Runnable alCambiar) {
        this.servicio = servicio;
        this.alCambiar = alCambiar;
        setLayout(new BorderLayout(8, 8));
        setBackground(EstilosUI.FONDO);
        setBorder(new EmptyBorder(8, 8, 8, 8));

        fecha.setText(LocalDate.now().plusDays(1).format(FORMATO_FECHA));
        paciente.setPreferredSize(new Dimension(330, 27));
        profesional.setPreferredSize(new Dimension(220, 27));
        hora.setPreferredSize(new Dimension(120, 27));
        llenarHoras();
        hora.setSelectedItem("09:00 AM");

        JPanel superior = new JPanel(new BorderLayout(0, 7));
        superior.setBackground(EstilosUI.FONDO);
        superior.add(EstilosUI.titulo("Registro y búsqueda de citas"), BorderLayout.NORTH);

        JPanel formulario = new JPanel(new GridLayout(3, 2, 10, 5));
        formulario.setBackground(EstilosUI.FONDO);
        formulario.setBorder(BorderFactory.createTitledBorder("Datos de la cita"));
        formulario.add(EstilosUI.campoFormulario("Paciente", paciente));
        formulario.add(EstilosUI.campoFormulario("Profesional de salud", profesional));
        formulario.add(EstilosUI.campoFormulario("Fecha (DD/MM/AAAA)", fecha));
        formulario.add(EstilosUI.campoFormulario("Hora (AM / PM)", hora));
        formulario.add(EstilosUI.campoFormulario("Motivo de la cita", motivo));
        JPanel accionRegistro = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 2));
        accionRegistro.setOpaque(false);
        JButton registrar = EstilosUI.boton("Registrar cita");
        registrar.addActionListener(e -> registrar());
        accionRegistro.add(registrar);
        formulario.add(accionRegistro);
        superior.add(formulario, BorderLayout.CENTER);

        JPanel busqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 5));
        busqueda.setBackground(EstilosUI.FONDO);
        busqueda.add(new JLabel("Buscar cita o paciente:"));
        busqueda.add(buscar);
        JButton filtrar = EstilosUI.botonSecundario("Buscar");
        filtrar.addActionListener(e -> refrescar());
        buscar.addActionListener(e -> refrescar());
        JButton limpiar = EstilosUI.botonSecundario("Mostrar todas");
        limpiar.addActionListener(e -> { buscar.setText(""); refrescar(); });
        busqueda.add(filtrar);
        busqueda.add(limpiar);
        superior.add(busqueda, BorderLayout.SOUTH);
        add(superior, BorderLayout.NORTH);

        EstilosUI.tabla(tabla);
        ocultarColumnaInterna();
        JPanel lista = new JPanel(new BorderLayout(0, 6));
        lista.setBackground(EstilosUI.FONDO);
        JLabel tituloLista = EstilosUI.etiqueta("Citas registradas (las nuevas citas aparecen automáticamente en esta lista)");
        lista.add(tituloLista, BorderLayout.NORTH);
        lista.add(EstilosUI.scroll(tabla), BorderLayout.CENTER);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 3));
        acciones.setBackground(EstilosUI.FONDO);
        JButton atender = EstilosUI.boton("Registrar atención");
        atender.addActionListener(e -> registrarAtencion());
        JButton cancelar = EstilosUI.botonPeligro("Cancelar cita seleccionada");
        cancelar.addActionListener(e -> cancelarSeleccionada());
        acciones.add(atender);
        acciones.add(cancelar);
        lista.add(acciones, BorderLayout.SOUTH);
        add(lista, BorderLayout.CENTER);
        refrescar();
    }

    private void llenarHoras() {
        for (int minutos = 7 * 60; minutos <= 19 * 60; minutos += 30) {
            LocalTime valor = LocalTime.of(minutos / 60, minutos % 60);
            hora.addItem(valor.format(FORMATO_HORA));
        }
    }

    private void registrar() {
        try {
            PacienteOpcion seleccionado = (PacienteOpcion) paciente.getSelectedItem();
            if (seleccionado == null) {
                throw new IllegalArgumentException("Primero debe registrar o seleccionar un paciente.");
            }
            LocalDate dia = LocalDate.parse(fecha.getText().trim(), FORMATO_FECHA);
            LocalTime horaElegida = LocalTime.parse((String) hora.getSelectedItem(), FORMATO_HORA);
            servicio.registrarCita(seleccionado.id(), (String) profesional.getSelectedItem(), dia,
                    horaElegida, (String) motivo.getSelectedItem());
            buscar.setText("");
            refrescar();
            alCambiar.run();
            EstilosUI.aviso(this, "La cita se registró correctamente y ya aparece en la agenda y en Reportes.", false);
        } catch (DateTimeParseException ex) {
            EstilosUI.aviso(this, "Escriba la fecha con el formato DD/MM/AAAA, por ejemplo 25/11/2026.", true);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            EstilosUI.aviso(this, ex.getMessage(), true);
        }
    }

    private void registrarAtencion() {
        Cita seleccionada = obtenerCitaSeleccionada();
        if (seleccionada == null) {
            EstilosUI.aviso(this, "Seleccione una cita de la tabla.", true);
            return;
        }
        JTextArea observacion = new JTextArea(5, 35);
        observacion.setLineWrap(true);
        observacion.setWrapStyleWord(true);
        int resultado = JOptionPane.showConfirmDialog(this, new JScrollPane(observacion),
                "Observaciones de la atención", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (resultado != JOptionPane.OK_OPTION) return;
        try {
            servicio.registrarAtencion(seleccionada.getId(), seleccionada.getProfesional(), observacion.getText());
            refrescar();
            alCambiar.run();
            EstilosUI.aviso(this, "La atención se registró y el estado de la cita cambió a ATENDIDA.", false);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            EstilosUI.aviso(this, ex.getMessage(), true);
        }
    }

    private void cancelarSeleccionada() {
        Cita seleccionada = obtenerCitaSeleccionada();
        if (seleccionada == null) {
            EstilosUI.aviso(this, "Seleccione una cita de la tabla.", true);
            return;
        }
        try {
            if (seleccionada.getEstado() != EstadoCita.PROGRAMADA) {
                throw new IllegalArgumentException("Solo se pueden cancelar citas que estén programadas.");
            }
            servicio.cambiarEstadoCita(seleccionada.getId(), EstadoCita.CANCELADA);
            refrescar();
            alCambiar.run();
            EstilosUI.aviso(this, "La cita fue cancelada.", false);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            EstilosUI.aviso(this, ex.getMessage(), true);
        }
    }

    private Cita obtenerCitaSeleccionada() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return null;
        int filaModelo = tabla.convertRowIndexToModel(fila);
        String id = String.valueOf(modelo.getValueAt(filaModelo, 7));
        return servicio.getCitas().stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null);
    }

    private void ocultarColumnaInterna() {
        tabla.getColumnModel().getColumn(7).setMinWidth(0);
        tabla.getColumnModel().getColumn(7).setMaxWidth(0);
        tabla.getColumnModel().getColumn(7).setPreferredWidth(0);
    }

    public void refrescar() {
        String idSeleccionado = paciente.getSelectedItem() instanceof PacienteOpcion p ? p.id() : null;
        paciente.removeAllItems();
        for (Paciente p : servicio.getPacientes()) {
            paciente.addItem(new PacienteOpcion(p.getId(), p.getNombres() + " - " + p.getDocumentoEnmascarado()));
        }
        if (idSeleccionado != null) {
            for (int i = 0; i < paciente.getItemCount(); i++) {
                if (paciente.getItemAt(i).id().equals(idSeleccionado)) {
                    paciente.setSelectedIndex(i);
                    break;
                }
            }
        }

        modelo.setRowCount(0);
        String criterio = buscar.getText() == null ? "" : buscar.getText().trim().toLowerCase(Locale.ROOT);
        for (Cita cita : servicio.getCitas()) {
            String nombrePaciente = servicio.getPacientes().stream()
                    .filter(p -> p.getId().equals(cita.getPacienteId()))
                    .map(Paciente::getNombres).findFirst().orElse("Paciente no disponible");
            String fechaVisible = cita.getFecha().format(FORMATO_FECHA);
            String horaVisible = cita.getHora().format(FORMATO_HORA);
            String textoBusqueda = (cita.getId() + " " + nombrePaciente + " " + cita.getProfesional() + " "
                    + fechaVisible + " " + horaVisible + " " + cita.getMotivo() + " " + cita.getEstado())
                    .toLowerCase(Locale.ROOT);
            if (textoBusqueda.contains(criterio)) {
                modelo.addRow(new Object[]{cita.getId().substring(0, 8).toUpperCase(), nombrePaciente,
                        cita.getProfesional(), fechaVisible, horaVisible, cita.getMotivo(), cita.getEstado(), cita.getId()});
            }
        }
    }

    private record PacienteOpcion(String id, String etiqueta) {
        @Override public String toString() { return etiqueta; }
    }
}
