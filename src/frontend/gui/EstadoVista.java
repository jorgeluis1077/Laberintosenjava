package frontend.gui;

import frontend.modelo.Celda;
import frontend.modelo.DatosLaberinto;
import frontend.modelo.ResultadoVista;
import java.util.List;

/** Instantanea de lo que hay que pintar: laberinto, visitados, actual y camino. */
final class EstadoVista {

    final DatosLaberinto datos;
    final boolean[][] visitado;
    final boolean[][] camino;
    final Celda actual; // null si no hay vertice actual

    private EstadoVista(DatosLaberinto datos, boolean[][] visitado, boolean[][] camino, Celda actual) {
        this.datos = datos;
        this.visitado = visitado;
        this.camino = camino;
        this.actual = actual;
    }

    /** Estado tras mostrar los primeros {@code paso} visitados de {@code r} (r puede ser null). */
    static EstadoVista desde(DatosLaberinto datos, ResultadoVista r, int paso, boolean mostrarCamino) {
        boolean[][] vis = new boolean[datos.filas()][datos.columnas()];
        boolean[][] cam = new boolean[datos.filas()][datos.columnas()];
        Celda actual = null;
        if (r != null) {
            List<Celda> v = r.visitados();
            int n = Math.max(0, Math.min(paso, v.size()));
            for (int i = 0; i < n; i++) {
                Celda c = v.get(i);
                vis[c.fila()][c.columna()] = true;
            }
            if (n > 0 && n < v.size()) {
                actual = v.get(n - 1);
            }
            if (mostrarCamino) {
                for (Celda c : r.camino()) {
                    cam[c.fila()][c.columna()] = true;
                }
            }
        }
        return new EstadoVista(datos, vis, cam, actual);
    }
}
