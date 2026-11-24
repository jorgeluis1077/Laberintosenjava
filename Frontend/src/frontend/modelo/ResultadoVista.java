package frontend.modelo;

import java.util.List;

/** Resultado de un algoritmo, listo para mostrarse (sin depender de la base). */
public record ResultadoVista(String algoritmo, List<Celda> visitados, List<Celda> camino,
                             boolean encontrado, long tiempoNanos) {

    public ResultadoVista {
        visitados = List.copyOf(visitados);
        camino = List.copyOf(camino);
    }

    /** Longitud en movimientos; -1 si no hay camino. */
    public int longitud() {
        return encontrado && !camino.isEmpty() ? camino.size() - 1 : -1;
    }
}
