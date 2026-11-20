package pe.santarosa.config;

import java.nio.file.Path;

/** Patrón Singleton: centraliza los ajustes locales de una única instancia. */
public final class ConfiguracionAplicacion {
    private static final ConfiguracionAplicacion INSTANCIA = new ConfiguracionAplicacion();
    private final String nombreCentro = System.getProperty("sistemarural.centro", "Centro de Salud Santa Rosa");
    private final String ubicacion = System.getProperty("sistemarural.ubicacion", "Chugur, Cajamarca, Perú");
    private final Path directorioDatos = Path.of(System.getProperty("user.home"), ".sistemarural-santa-rosa");

    private ConfiguracionAplicacion() { }
    public static ConfiguracionAplicacion getInstancia() { return INSTANCIA; }
    public String getNombreCentro() { return nombreCentro; }
    public String getUbicacion() { return ubicacion; }
    public Path getDirectorioDatos() { return directorioDatos; }
}
