package pe.santarosa.model;

import java.time.LocalDateTime;

/** Registro interno de cada ingreso o salida de existencias. */
public class MovimientoInventario {
    private final String id;
    private final String medicamentoId;
    private final TipoMovimiento tipo;
    private final int cantidad;
    private final LocalDateTime fecha;

    public MovimientoInventario(String id, String medicamentoId, TipoMovimiento tipo,
                                int cantidad, LocalDateTime fecha) {
        if (id == null || id.isBlank() || medicamentoId == null || medicamentoId.isBlank()) {
            throw new IllegalArgumentException("Movimiento sin referencia válida.");
        }
        if (tipo == null || cantidad <= 0 || fecha == null) {
            throw new IllegalArgumentException("Datos del movimiento no válidos.");
        }
        this.id = id;
        this.medicamentoId = medicamentoId;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.fecha = fecha;
    }
    public String getId() { return id; }
    public String getMedicamentoId() { return medicamentoId; }
    public TipoMovimiento getTipo() { return tipo; }
    public int getCantidad() { return cantidad; }
    public LocalDateTime getFecha() { return fecha; }
}
