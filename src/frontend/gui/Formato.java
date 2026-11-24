package frontend.gui;

import frontend.modelo.ResultadoVista;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/** Textos y etiquetas comparativas compartidas por varias vistas. */
final class Formato {

    private Formato() {
    }

    static String longitud(ResultadoVista r) {
        return r.encontrado() ? r.longitud() + " movimientos" : "Sin camino";
    }

    /** Ej.: 58300 ns (0.058 ms) */
    static String tiempo(ResultadoVista r) {
        return r.tiempoNanos() + " ns (" + String.format(Locale.ROOT, "%.3f", r.tiempoNanos() / 1e6) + " ms)";
    }

    /** Algoritmos con menos visitados y con camino mas corto (vacios si hay menos de dos resultados). */
    record Marcas(Set<String> menosVisitados, Set<String> caminoMinimo) {
    }

    static Marcas marcas(Collection<ResultadoVista> resultados) {
        Set<String> vis = new HashSet<>();
        Set<String> cam = new HashSet<>();
        if (resultados.size() < 2) {
            return new Marcas(vis, cam);
        }
        int minVis = Integer.MAX_VALUE;
        int minCam = Integer.MAX_VALUE;
        for (ResultadoVista r : resultados) {
            minVis = Math.min(minVis, r.visitados().size());
            if (r.encontrado()) {
                minCam = Math.min(minCam, r.longitud());
            }
        }
        for (ResultadoVista r : resultados) {
            if (r.visitados().size() == minVis) {
                vis.add(r.algoritmo());
            }
            if (r.encontrado() && r.longitud() == minCam) {
                cam.add(r.algoritmo());
            }
        }
        return new Marcas(vis, cam);
    }
}
