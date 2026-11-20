package pe.santarosa.model;

/** Profesional que puede atender citas en el establecimiento. */
public class ProfesionalSalud extends Persona {
    private String especialidad;

    public ProfesionalSalud(String id, String nombres, int edad, String especialidad) {
        super(id, nombres, edad);
        setEspecialidad(especialidad);
    }
    public String getEspecialidad() { return especialidad; }
    public final void setEspecialidad(String especialidad) {
        if (especialidad == null || especialidad.isBlank()) throw new IllegalArgumentException("La especialidad es obligatoria.");
        this.especialidad = especialidad.trim();
    }
}
