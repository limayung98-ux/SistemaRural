package pe.santarosa.model;

import java.time.LocalDate;
import java.time.LocalTime;

/** Cita médica asociada a un paciente y a un profesional. */
public class Cita {
    private final String id;
    private final String pacienteId;
    private String profesional;
    private LocalDate fecha;
    private LocalTime hora;
    private String motivo;
    private EstadoCita estado;

    public Cita(String id, String pacienteId, String profesional, LocalDate fecha,
                LocalTime hora, String motivo, EstadoCita estado) {
        if (id == null || id.isBlank() || pacienteId == null || pacienteId.isBlank())
            throw new IllegalArgumentException("La cita debe estar vinculada a un paciente.");
        this.id = id;
        this.pacienteId = pacienteId;
        setProfesional(profesional);
        setFecha(fecha);
        setHora(hora);
        setMotivo(motivo);
        setEstado(estado);
    }
    public String getId() { return id; }
    public String getPacienteId() { return pacienteId; }
    public String getProfesional() { return profesional; }
    public final void setProfesional(String profesional) {
        if (profesional == null || profesional.isBlank()) throw new IllegalArgumentException("Seleccione al profesional de salud.");
        this.profesional = profesional.trim();
    }
    public LocalDate getFecha() { return fecha; }
    public final void setFecha(LocalDate fecha) {
        if (fecha == null) throw new IllegalArgumentException("La fecha de la cita es obligatoria.");
        this.fecha = fecha;
    }
    public LocalTime getHora() { return hora; }
    public final void setHora(LocalTime hora) {
        if (hora == null) throw new IllegalArgumentException("La hora es obligatoria.");
        this.hora = hora;
    }
    public String getMotivo() { return motivo; }
    public final void setMotivo(String motivo) {
        if (motivo == null || motivo.isBlank()) throw new IllegalArgumentException("Indique el motivo de la cita.");
        this.motivo = motivo.trim();
    }
    public EstadoCita getEstado() { return estado; }
    public final void setEstado(EstadoCita estado) {
        if (estado == null) throw new IllegalArgumentException("El estado de la cita es obligatorio.");
        this.estado = estado;
    }
}
