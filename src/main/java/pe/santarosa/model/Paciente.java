package pe.santarosa.model;

/** Paciente. El documento nunca se conserva en texto plano. */
public class Paciente extends Persona {
    private final String documentoHash;
    private final String documentoSalt;
    private final String ultimosCuatro;
    private String telefono;

    public Paciente(String id, String nombres, int edad, String documentoHash,
                    String documentoSalt, String ultimosCuatro, String telefono) {
        super(id, nombres, edad);
        if (documentoHash == null || documentoHash.isBlank() || documentoSalt == null || documentoSalt.isBlank())
            throw new IllegalArgumentException("No fue posible proteger el documento.");
        if (ultimosCuatro == null || !ultimosCuatro.matches("\\d{4}"))
            throw new IllegalArgumentException("Los últimos dígitos del documento no son válidos.");
        this.documentoHash = documentoHash;
        this.documentoSalt = documentoSalt;
        this.ultimosCuatro = ultimosCuatro;
        setTelefono(telefono);
    }

    public String getDocumentoHash() { return documentoHash; }
    public String getDocumentoSalt() { return documentoSalt; }
    public String getDocumentoEnmascarado() { return "****" + ultimosCuatro; }
    public String getTelefono() { return telefono; }
    public String getTelefonoEnmascarado() {
        if (telefono == null || telefono.isBlank()) return "No registrado";
        return "******" + telefono.substring(Math.max(0, telefono.length() - 3));
    }
    public final void setTelefono(String telefono) {
        String limpio = telefono == null ? "" : telefono.trim();
        if (!limpio.isEmpty() && !limpio.matches("[0-9+ -]{6,15}"))
            throw new IllegalArgumentException("El teléfono debe contener entre 6 y 15 caracteres válidos.");
        this.telefono = limpio;
    }

    @Override public String toString() {
        return getNombres() + " (" + getDocumentoEnmascarado() + ")";
    }
}
