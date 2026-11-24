package frontend.modelo;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * Unico punto de contacto entre la GUI y la logica del proyecto.
 * La GUI solo conoce esta interfaz; ServicioBase la implementa con la base real.
 */
public interface ServicioLaberinto {

    /** Carga y valida un laberinto; si falla, el estado anterior se conserva. */
    DatosLaberinto cargar(Path archivo) throws ErrorCargaException;

    /** Los tres lanzan IllegalStateException si no hay laberinto cargado. */
    ResultadoVista resolverDFS();

    ResultadoVista resolverBFS();

    ResultadoVista resolverDijkstra();

    /** Descarta resultados guardados del laberinto anterior. */
    void reiniciarResultados();

    /** Carpeta de ejemplos, si existe. */
    Optional<Path> carpetaEjemplos();

    /** Archivos .txt de la carpeta de ejemplos, en orden natural. */
    List<Path> listarEjemplos();

    /** Archivos .txt de la subcarpeta invalidos/ (vacio si no existe). */
    List<Path> listarEjemplosInvalidos();
}
