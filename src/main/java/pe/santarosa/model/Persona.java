package pe.santarosa.model;

/** Clase base para las personas vinculadas al establecimiento. */
public abstract class Persona {
    private final String id;
    private String nombres;
    private int edad;

    protected Persona(String id, String nombres, int edad) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("El identificador es obligatorio.");
        this.id = id;
        setNombres(nombres);
        setEdad(edad);
    }

    public String getId() { return id; }
    public String getNombres() { return nombres; }
    public final void setNombres(String nombres) {
        if (nombres == null || !nombres.trim().matches("[\\p{L} .'-]{3,80}")) {
            throw new IllegalArgumentException("Ingrese nombres válidos (mínimo 3 caracteres).");
        }
        this.nombres = nombres.trim().replaceAll("\\s+", " ");
    }
    public int getEdad() { return edad; }
    public final void setEdad(int edad) {
        if (edad < 0 || edad > 120) throw new IllegalArgumentException("La edad debe estar entre 0 y 120 años.");
        this.edad = edad;
    }
}
