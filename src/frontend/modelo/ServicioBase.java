package frontend.modelo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import laberintoproyectofundamentosiii.Despliegue;
import laberintoproyectofundamentosiii.Grafo;
import laberintoproyectofundamentosiii.LaberintoInvalidoException;
import laberintoproyectofundamentosiii.LaberintoProyectoFundamentosIII;
import laberintoproyectofundamentosiii.LaberintoProyectoFundamentosIII.Algoritmo;
import laberintoproyectofundamentosiii.LaberintoProyectoFundamentosIII.ResultadoBusqueda;

/**
 * Adaptador entre la GUI y la base: usa la carga y el Grafo reales y guarda en cache
 * el resultado de cada algoritmo para el laberinto actual. Los metodos con estado son
 * synchronized porque la GUI los llama desde SwingWorker.
 */
public final class ServicioBase implements ServicioLaberinto {

    private static final Path CARPETA_EJEMPLOS = Path.of("MapaLaberintos");

    private final Map<Algoritmo, ResultadoVista> resultados = new EnumMap<>(Algoritmo.class);
    private Grafo grafo;

    /** Si la carga falla lanza ErrorCargaException con el motivo y conserva el laberinto anterior. */
    @Override
    public synchronized DatosLaberinto cargar(Path archivo) throws ErrorCargaException {
        String mapa;
        try {
            mapa = LaberintoProyectoFundamentosIII.validarYLeerLaberinto(archivo.toString());
        } catch (LaberintoInvalidoException e) {
            throw new ErrorCargaException(e.getMessage());
        }
        grafo = LaberintoProyectoFundamentosIII.construirGrafoDesdeMapa(mapa);
        resultados.clear();
        return Despliegue.datosDelMapa(mapa, grafo).conNombre(archivo.getFileName().toString());
    }

    @Override
    public ResultadoVista resolverDFS() {
        return resolver(Algoritmo.DFS);
    }

    @Override
    public ResultadoVista resolverBFS() {
        return resolver(Algoritmo.BFS);
    }

    @Override
    public ResultadoVista resolverDijkstra() {
        return resolver(Algoritmo.DIJKSTRA);
    }

    @Override
    public synchronized void reiniciarResultados() {
        resultados.clear();
    }

    private synchronized ResultadoVista resolver(Algoritmo algoritmo) {
        if (grafo == null) {
            throw new IllegalStateException("No hay laberinto cargado");
        }
        return resultados.computeIfAbsent(algoritmo, a -> {
            ResultadoBusqueda r = a.ejecutar(grafo);
            return Despliegue.resultadoVista(a.toString(), r.visitados(), r.camino(), r.tiempoNanos());
        });
    }

    @Override
    public Optional<Path> carpetaEjemplos() {
        return Optional.of(CARPETA_EJEMPLOS).filter(Files::isDirectory);
    }

    @Override
    public List<Path> listarEjemplos() {
        return listarTxt(CARPETA_EJEMPLOS);
    }

    @Override
    public List<Path> listarEjemplosInvalidos() {
        return listarTxt(CARPETA_EJEMPLOS.resolve("invalidos"));
    }

    /** Archivos .txt de la carpeta en orden natural; lista vacia si no existe. */
    private static List<Path> listarTxt(Path carpeta) {
        try (Stream<Path> archivos = Files.list(carpeta)) {
            return archivos.filter(p -> Files.isRegularFile(p)
                            && p.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".txt"))
                    .sorted(Comparator.comparing(p -> p.getFileName().toString(),
                            LaberintoProyectoFundamentosIII::compararNombres))
                    .toList();
        } catch (IOException e) {
            return List.of();
        }
    }
}
