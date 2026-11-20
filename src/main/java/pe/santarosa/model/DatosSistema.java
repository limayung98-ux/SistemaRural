package pe.santarosa.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Agregado que representa el estado que guarda el repositorio local. */
public class DatosSistema {
    private final List<Paciente> pacientes = new ArrayList<>();
    private final List<Medicamento> medicamentos = new ArrayList<>();
    private final List<Cita> citas = new ArrayList<>();
    private final List<Atencion> atenciones = new ArrayList<>();
    private final List<MovimientoInventario> movimientos = new ArrayList<>();

    public List<Paciente> getPacientes() { return Collections.unmodifiableList(pacientes); }
    public List<Medicamento> getMedicamentos() { return Collections.unmodifiableList(medicamentos); }
    public List<Cita> getCitas() { return Collections.unmodifiableList(citas); }
    public List<Atencion> getAtenciones() { return Collections.unmodifiableList(atenciones); }
    public List<MovimientoInventario> getMovimientos() { return Collections.unmodifiableList(movimientos); }
    public void add(Paciente x) { pacientes.add(x); }
    public void add(Medicamento x) { medicamentos.add(x); }
    public void add(Cita x) { citas.add(x); }
    public void add(Atencion x) { atenciones.add(x); }
    public void add(MovimientoInventario x) { movimientos.add(x); }
}
