package pe.santarosa.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Medicamento con control de existencias y vencimiento. */
public class Medicamento {
    private final String id;
    private String nombre;
    private String presentacion;
    private String lote;
    private int stock;
    private int minimo;
    private BigDecimal precioUnitario;
    private LocalDate fechaVencimiento;

    public Medicamento(String id, String nombre, String presentacion, String lote,
                       int stock, int minimo, BigDecimal precioUnitario, LocalDate fechaVencimiento) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("El código del medicamento es obligatorio.");
        this.id = id;
        setNombre(nombre);
        setPresentacion(presentacion);
        setLote(lote);
        setStock(stock);
        setMinimo(minimo);
        setPrecioUnitario(precioUnitario);
        setFechaVencimiento(fechaVencimiento);
    }
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public final void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("El nombre del medicamento es obligatorio.");
        this.nombre = nombre.trim();
    }
    public String getPresentacion() { return presentacion; }
    public final void setPresentacion(String presentacion) {
        if (presentacion == null || presentacion.isBlank()) throw new IllegalArgumentException("La presentación es obligatoria.");
        this.presentacion = presentacion.trim();
    }
    public String getLote() { return lote; }
    public final void setLote(String lote) { this.lote = lote == null ? "Sin lote" : lote.trim(); }
    public int getStock() { return stock; }
    public final void setStock(int stock) {
        if (stock < 0) throw new IllegalArgumentException("El stock no puede ser negativo.");
        this.stock = stock;
    }
    public int getMinimo() { return minimo; }
    public final void setMinimo(int minimo) {
        if (minimo < 0) throw new IllegalArgumentException("El stock mínimo no puede ser negativo.");
        this.minimo = minimo;
    }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public final void setPrecioUnitario(BigDecimal precioUnitario) {
        if (precioUnitario == null || precioUnitario.signum() < 0) throw new IllegalArgumentException("El precio no puede ser negativo.");
        this.precioUnitario = precioUnitario;
    }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public final void setFechaVencimiento(LocalDate fechaVencimiento) {
        if (fechaVencimiento == null) throw new IllegalArgumentException("La fecha de vencimiento es obligatoria.");
        this.fechaVencimiento = fechaVencimiento;
    }
    public boolean stockBajo() { return stock <= minimo; }
    public boolean vencido() { return fechaVencimiento.isBefore(LocalDate.now()); }
    public void ingresar(int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        try { stock = Math.addExact(stock, cantidad); }
        catch (ArithmeticException ex) { throw new IllegalArgumentException("La cantidad supera el máximo permitido.", ex); }
    }
    public void entregar(int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        if (cantidad > stock) throw new IllegalArgumentException("No hay stock suficiente para realizar la salida.");
        stock -= cantidad;
    }
}
