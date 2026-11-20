package pe.santarosa.service;

import pe.santarosa.model.DatosSistema;
import pe.santarosa.repository.RepositorioSalud;
import java.io.IOException;

/** Repositorio de prueba aislado; no escribe datos en el perfil del equipo. */
final class RepositorioMemoria implements RepositorioSalud {
    private DatosSistema datos = new DatosSistema();
    @Override public DatosSistema cargar() { return datos; }
    @Override public void guardar(DatosSistema datos) { this.datos = datos; }
}
