package pe.santarosa.repository;

import pe.santarosa.model.DatosSistema;
import java.io.IOException;

/** Contrato del almacenamiento. El servicio depende de esta abstracción (DIP). */
public interface RepositorioSalud {
    DatosSistema cargar() throws IOException;
    void guardar(DatosSistema datos) throws IOException;
}
