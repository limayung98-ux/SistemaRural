package pe.santarosa.ui;

import pe.santarosa.model.Medicamento;
import pe.santarosa.model.TipoMovimiento;
import pe.santarosa.service.ServicioSalud;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/** Inventario sencillo de medicamentos e insumos para la presentación del centro de salud. */
public class PanelMedicamentos extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ServicioSalud servicio;
    private final Runnable alCambiar;
    private final JTextField nombre = EstilosUI.campo(16);
    private final JComboBox<String> presentacion = new JComboBox<>(new String[]{
            "Tableta 500 mg", "Tableta 400 mg", "Tableta 10 mg", "Cápsula 500 mg",
            "Jarabe 120 ml", "Sobre", "Frasco alcohol medicinal 70% - 250 ml",
            "Frasco alcohol medicinal 70% - 500 ml"});
    private final JTextField stock = EstilosUI.campo(6);
    private final JTextField minimo = EstilosUI.campo(6);
    private final JTextField vencimiento = EstilosUI.campo(10);
    private final JComboBox<TipoMovimiento> tipo = new JComboBox<>(TipoMovimiento.values());
    private final JTextField cantidad = EstilosUI.campo(6);
    // La primera columna conserva el identificador interno, pero no se muestra en pantalla.
    private final DefaultTableModel modelo = new DefaultTableModel(new Object[]{
            "ID interno", "Medicamento", "Presentación", "Stock actual", "Stock mínimo", "Vencimiento", "Estado"}, 0) {
        @Override public boolean isCellEditable(int fila, int columna) { return false; }
    };
    private final JTable tabla = new JTable(modelo);

    public PanelMedicamentos(ServicioSalud servicio, Runnable alCambiar) {
        this.servicio = servicio;
        this.alCambiar = alCambiar;
        setLayout(new BorderLayout(8, 8));
        setBackground(EstilosUI.FONDO);
        setBorder(new EmptyBorder(8, 8, 8, 8));

        vencimiento.setText(LocalDate.now().plusYears(1).format(FECHA));
        stock.setText("0");
        minimo.setText("5");
        presentacion.setEditable(true);
        presentacion.setPreferredSize(new Dimension(220, 27));

        JPanel formulario = new JPanel(new GridLayout(2, 3, 9, 7));
        formulario.setBackground(EstilosUI.FONDO);
        formulario.setBorder(BorderFactory.createTitledBorder("Registrar medicamento o insumo"));
        formulario.add(EstilosUI.campoFormulario("Nombre", nombre));
        formulario.add(EstilosUI.campoFormulario("Presentación", presentacion));
        formulario.add(EstilosUI.campoFormulario("Stock inicial", stock));
        formulario.add(EstilosUI.campoFormulario("Stock mínimo", minimo));
        formulario.add(EstilosUI.campoFormulario("Vencimiento (DD/MM/AAAA)", vencimiento));

        JPanel botonRegistro = new JPanel(new FlowLayout(FlowLayout.RIGHT, 3, 9));
        botonRegistro.setOpaque(false);
        JButton registrar = EstilosUI.boton("Registrar medicamento");
        registrar.addActionListener(e -> registrar());
        botonRegistro.add(registrar);
        formulario.add(botonRegistro);
        add(formulario, BorderLayout.NORTH);

        EstilosUI.tabla(tabla);
        tabla.getColumnModel().removeColumn(tabla.getColumnModel().getColumn(0));
        int[] anchos = {210, 255, 100, 100, 125, 115};
        for (int i = 0; i < anchos.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }

        JPanel lista = new JPanel(new BorderLayout(0, 5));
        lista.setBackground(EstilosUI.FONDO);
        lista.add(EstilosUI.etiqueta("Inventario de tabletas, cápsulas, alcohol medicinal y otros insumos"), BorderLayout.NORTH);
        lista.add(EstilosUI.scroll(tabla), BorderLayout.CENTER);
        add(lista, BorderLayout.CENTER);

        JPanel ajustes = new JPanel(new FlowLayout(FlowLayout.LEFT, 9, 8));
        ajustes.setBackground(EstilosUI.FONDO);
        ajustes.setBorder(BorderFactory.createTitledBorder("Actualizar existencias del producto seleccionado"));
        ajustes.add(new JLabel("Tipo:"));
        ajustes.add(tipo);
        ajustes.add(new JLabel("Cantidad:"));
        ajustes.add(cantidad);
        JButton aplicar = EstilosUI.boton("Aplicar ajuste");
        aplicar.addActionListener(e -> ajustar());
        ajustes.add(aplicar);
        add(ajustes, BorderLayout.SOUTH);
        refrescar();
    }

    private void registrar() {
        try {
            servicio.registrarMedicamento(nombre.getText(), String.valueOf(presentacion.getSelectedItem()),
                    Integer.parseInt(stock.getText().trim()), Integer.parseInt(minimo.getText().trim()),
                    LocalDate.parse(vencimiento.getText().trim(), FECHA));
            nombre.setText("");
            stock.setText("0");
            refrescar();
            alCambiar.run();
            EstilosUI.aviso(this, "Medicamento o insumo registrado correctamente.", false);
        } catch (NumberFormatException ex) {
            EstilosUI.aviso(this, "El stock inicial y el mínimo deben ser números enteros.", true);
        } catch (DateTimeParseException ex) {
            EstilosUI.aviso(this, "Use el formato de vencimiento DD/MM/AAAA.", true);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            EstilosUI.aviso(this, ex.getMessage(), true);
        }
    }

    private void ajustar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            EstilosUI.aviso(this, "Seleccione un medicamento de la tabla.", true);
            return;
        }
        try {
            int filaModelo = tabla.convertRowIndexToModel(fila);
            String idInterno = String.valueOf(modelo.getValueAt(filaModelo, 0));
            Medicamento medicamento = servicio.obtenerMedicamento(idInterno);
            servicio.ajustarInventario(medicamento.getId(), (TipoMovimiento) tipo.getSelectedItem(),
                    Integer.parseInt(cantidad.getText().trim()));
            cantidad.setText("");
            refrescar();
            alCambiar.run();
            EstilosUI.aviso(this, "Las existencias se actualizaron correctamente.", false);
        } catch (NumberFormatException ex) {
            EstilosUI.aviso(this, "La cantidad debe ser un número entero.", true);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            EstilosUI.aviso(this, ex.getMessage(), true);
        }
    }

    public void refrescar() {
        modelo.setRowCount(0);
        for (Medicamento medicamento : servicio.getMedicamentos()) {
            String estado = medicamento.vencido() ? "VENCIDO" : medicamento.stockBajo() ? "STOCK BAJO" : "DISPONIBLE";
            modelo.addRow(new Object[]{medicamento.getId(), medicamento.getNombre(), medicamento.getPresentacion(),
                    medicamento.getStock(), medicamento.getMinimo(), medicamento.getFechaVencimiento().format(FECHA), estado});
        }
    }
}
