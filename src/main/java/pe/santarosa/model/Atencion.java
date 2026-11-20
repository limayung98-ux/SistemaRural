package pe.santarosa.model;

import java.time.LocalDateTime;

/** Registro de una atención realizada a partir de una cita. */
public class Atencion {
    private final String id;
    private final String citaId;
    private final String profesional;
    private final String observacion;
    private final LocalDateTime fecha;

    public Atencion(String id, String citaId, String profesional, String observacion, LocalDateTime fecha) {
        if (id == null || id.isBlank() || citaId == null || citaId.isBlank()) throw new IllegalArgumentException("Identificadores de atención incompletos.");
        if (profesional == null || profesional.isBlank() || observacion == null || observacion.isBlank()) throw new IllegalArgumentException("Complete los datos de la atención.");
        if (fecha == null) throw new IllegalArgumentException("La fecha de atención es obligatoria.");
        this.id = id;
        this.citaId = citaId;
        this.profesional = profesional.trim();
        this.observacion = observacion.trim();
        this.fecha = fecha;
    }
    public String getId() { return id; }
    public String getCitaId() { return citaId; }
    public String getProfesional() { return profesional; }
    public String getObservacion() { return observacion; }
    public LocalDateTime getFecha() { return fecha; }
}
