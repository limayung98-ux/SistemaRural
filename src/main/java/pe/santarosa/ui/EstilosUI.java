package pe.santarosa.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/** Estilos compartidos: interfaz clásica, clara y cercana a la referencia visual. */
public final class EstilosUI {
    public static final Color FONDO = new Color(238, 238, 238);
    public static final Color TEXTO = new Color(45, 55, 65);
    public static final Color AZUL = new Color(37, 99, 145);
    public static final Color BORDE = new Color(170, 185, 198);
    public static final Color CELESTE = new Color(220, 235, 247);

    private EstilosUI() { }

    public static void configurarTipografia() {
        Font normal = new Font("SansSerif", Font.PLAIN, 12);
        String[] claves = {"Label.font", "Button.font", "TextField.font", "TextArea.font",
                "ComboBox.font", "Table.font", "TableHeader.font", "TabbedPane.font"};
        for (String clave : claves) UIManager.put(clave, normal);
        UIManager.put("control", FONDO);
        UIManager.put("nimbusLightBackground", Color.WHITE);
    }

    public static JPanel tarjeta() {
        JPanel panel = new JPanel();
        panel.setBackground(FONDO);
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        return panel;
    }

    public static JLabel titulo(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(new Font("SansSerif", Font.BOLD, 18));
        etiqueta.setForeground(TEXTO);
        return etiqueta;
    }

    public static JLabel etiqueta(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(new Font("SansSerif", Font.PLAIN, 12));
        etiqueta.setForeground(TEXTO);
        return etiqueta;
    }

    public static JTextField campo(int columnas) {
        JTextField campo = new JTextField(columnas);
        campo.setPreferredSize(new Dimension(Math.max(70, campo.getPreferredSize().width), 27));
        return campo;
    }

    public static <T> JComboBox<T> combo() {
        JComboBox<T> combo = new JComboBox<>();
        combo.setPreferredSize(new Dimension(170, 27));
        return combo;
    }

    public static JButton boton(String texto) {
        JButton boton = new JButton(texto);
        boton.setFocusPainted(false);
        boton.setFont(new Font("SansSerif", Font.BOLD, 12));
        boton.setPreferredSize(new Dimension(Math.max(112, boton.getPreferredSize().width + 16), 29));
        return boton;
    }

    public static JButton botonSecundario(String texto) {
        JButton boton = boton(texto);
        return boton;
    }

    public static JButton botonPeligro(String texto) {
        JButton boton = boton(texto);
        boton.setForeground(new Color(145, 35, 35));
        return boton;
    }

    public static void tabla(JTable tabla) {
        tabla.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tabla.setForeground(TEXTO);
        tabla.setBackground(Color.WHITE);
        tabla.setRowHeight(25);
        tabla.setGridColor(new Color(205, 212, 218));
        tabla.setShowHorizontalLines(true);
        tabla.setShowVerticalLines(true);
        tabla.setFillsViewportHeight(true);
        tabla.setSelectionBackground(new Color(205, 226, 243));
        tabla.setSelectionForeground(Color.BLACK);
        tabla.setAutoCreateRowSorter(true);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getTableHeader().setFont(new Font("SansSerif", Font.PLAIN, 12));
        tabla.getTableHeader().setPreferredSize(new Dimension(10, 25));
        DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();
        centrado.setHorizontalAlignment(SwingConstants.CENTER);
        tabla.getTableHeader().setDefaultRenderer(centrado);
    }

    public static JScrollPane scroll(JTable tabla) {
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(BORDE));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    public static JPanel campoFormulario(String nombre, JComponent componente) {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setOpaque(false);
        panel.add(etiqueta(nombre), BorderLayout.NORTH);
        panel.add(componente, BorderLayout.CENTER);
        return panel;
    }

    public static void aviso(Component parent, String mensaje, boolean error) {
        JOptionPane.showMessageDialog(parent, mensaje,
                error ? "Revisar información" : "Operación realizada",
                error ? JOptionPane.WARNING_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
    }
}
