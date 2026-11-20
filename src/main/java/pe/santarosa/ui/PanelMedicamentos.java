package pe.santarosa.ui;

import pe.santarosa.model.Medicamento;
import pe.santarosa.model.TipoMovimiento;
import pe.santarosa.service.ServicioSalud;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/** Administración de medicamentos, tabletas y alcohol medicinal con control de existencias. */
public class PanelMedicamentos extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final ServicioSalud servicio;
    private final Runnable alCambiar;
    private final JTextField nombre = EstilosUI.campo(15);
    private final JComboBox<String> presentacion = new JComboBox<>(new String[]{
            "Tableta 500 mg", "Tableta 400 mg", "Cápsula 500 mg", "Jarabe 120 ml",
            "Sobre", "Frasco alcohol medicinal 70% - 250 ml", "Frasco alcohol medicinal 70% - 500 ml"});
    private final JTextField lote = EstilosUI.campo(10);
    private final JTextField stock = EstilosUI.campo(5);
    private final JTextField minimo = EstilosUI.campo(5);
    private final JTextField precio = EstilosUI.campo(7);
    private final JTextField vencimiento = EstilosUI.campo(10);
    private final JComboBox<TipoMovimiento> tipo = new JComboBox<>(TipoMovimiento.values());
    private final JTextField cantidad = EstilosUI.campo(6);
    private final JTextField detalle = EstilosUI.campo(18);
    private final DefaultTableModel modelo = new DefaultTableModel(new Object[]{
            "Código", "Medicamento", "Presentación", "Lote", "Stock", "Mínimo", "Precio (S/)", "Vencimiento", "Estado"}, 0) {
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
        precio.setText("0.00");
        presentacion.setEditable(true);
        presentacion.setPreferredSize(new Dimension(210, 27));

        JPanel formulario = new JPanel(new GridLayout(2, 4, 9, 6));
        formulario.setBackground(EstilosUI.FONDO);
        formulario.setBorder(BorderFactory.createTitledBorder("Registrar medicamento o insumo"));
        formulario.add(EstilosUI.campoFormulario("Nombre", nombre));
        formulario.add(EstilosUI.campoFormulario("Presentación", presentacion));
        formulario.add(EstilosUI.campoFormulario("Lote", lote));
        formulario.add(EstilosUI.campoFormulario("Stock inicial", stock));
        formulario.add(EstilosUI.campoFormulario("Stock mínimo", minimo));
        formulario.add(EstilosUI.campoFormulario("Precio unitario (S/)", precio));
        formulario.add(EstilosUI.campoFormulario("Vencimiento (DD/MM/AAAA)", vencimiento));
        JPanel botonRegistro = new JPanel(new FlowLayout(FlowLayout.RIGHT, 3, 10));
        botonRegistro.setOpaque(false);
        JButton registrar = EstilosUI.boton("Registrar medicamento");
        registrar.addActionListener(e -> registrar());
        botonRegistro.add(registrar);
        formulario.add(botonRegistro);
        add(formulario, BorderLayout.NORTH);

        EstilosUI.tabla(tabla);
        int[] anchos = {95, 170, 195, 115, 55, 55, 80, 105, 95};
        for (int i = 0; i < anchos.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
        JPanel lista = new JPanel(new BorderLayout(0, 5));
        lista.setBackground(EstilosUI.FONDO);
        lista.add(EstilosUI.etiqueta("Inventario de tabletas, cápsulas, alcohol medicinal y otros insumos"), BorderLayout.NORTH);
        lista.add(EstilosUI.scroll(tabla), BorderLayout.CENTER);
        add(lista, BorderLayout.CENTER);

        JPanel movimientos = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 7));
        movimientos.setBackground(EstilosUI.FONDO);
        movimientos.setBorder(BorderFactory.createTitledBorder("Movimiento de inventario (seleccione primero un producto)"));
        movimientos.add(new JLabel("Tipo:"));
        movimientos.add(tipo);
        movimientos.add(new JLabel("Cantidad:"));
        movimientos.add(cantidad);
        movimientos.add(new JLabel("Detalle:"));
        movimientos.add(detalle);
        JButton aplicar = EstilosUI.boton("Aplicar movimiento");
        aplicar.addActionListener(e -> ajustar());
        movimientos.add(aplicar);
        add(movimientos, BorderLayout.SOUTH);
        refrescar();
    }

    private void registrar() {
        try {
            servicio.registrarMedicamento(nombre.getText(), String.valueOf(presentacion.getSelectedItem()), lote.getText(),
                    Integer.parseInt(stock.getText().trim()), Integer.parseInt(minimo.getText().trim()),
                    new BigDecimal(precio.getText().trim()), LocalDate.parse(vencimiento.getText().trim(), FECHA));
            nombre.setText("");
            lote.setText("");
            stock.setText("0");
            precio.setText("0.00");
            refrescar();
            alCambiar.run();
            EstilosUI.aviso(this, "Medicamento o insumo registrado correctamente.", false);
        } catch (NumberFormatException ex) {
            EstilosUI.aviso(this, "Stock, mínimo y precio deben contener valores numéricos válidos.", true);
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
            String codigo = String.valueOf(modelo.getValueAt(filaModelo, 0));
            Medicamento medicamento = servicio.obtenerMedicamento(codigo);
            servicio.ajustarInventario(medicamento.getId(), (TipoMovimiento) tipo.getSelectedItem(),
                    Integer.parseInt(cantidad.getText().trim()), detalle.getText());
            cantidad.setText("");
            detalle.setText("");
            refrescar();
            alCambiar.run();
            EstilosUI.aviso(this, "Movimiento de inventario registrado correctamente.", false);
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
                    medicamento.getLote(), medicamento.getStock(), medicamento.getMinimo(),
                    medicamento.getPrecioUnitario(), medicamento.getFechaVencimiento().format(FECHA), estado});
        }
    }
}
