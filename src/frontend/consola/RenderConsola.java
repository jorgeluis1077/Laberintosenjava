package frontend.consola;

import frontend.modelo.Celda;
import frontend.modelo.DatosLaberinto;
import frontend.modelo.ResultadoVista;
import java.util.List;

/** Convierte un laberinto (con exploracion opcional) en texto para consola. */
public final class RenderConsola {

    private RenderConsola() {
    }

    /**
     * Devuelve el laberinto, una linea por fila.
     * Capas: paredes, entrada, salida, visitados, actual y camino final.
     * La entrada y la salida siempre quedan visibles.
     *
     * @param resultado     puede ser null (solo se dibuja el laberinto)
     * @param pasos         cuantos visitados mostrar (se ajusta al rango valido)
     * @param mostrarCamino dibuja el camino final del resultado
     */
    public static String vista(DatosLaberinto datos, ResultadoVista resultado, int pasos,
                               boolean mostrarCamino, TemaConsola tema) {
        return vista(datos, resultado, pasos, mostrarCamino,
                resultado != null && pasos < resultado.visitados().size(), tema);
    }

    /** Permite marcar el vertice actual tambien en el ultimo paso de la exploracion. */
    public static String vista(DatosLaberinto datos, ResultadoVista resultado, int pasos,
                               boolean mostrarCamino, boolean mostrarActual, TemaConsola tema) {
        int filas = datos.filas();
        int cols = datos.columnas();
        boolean[][] visitado = new boolean[filas][cols];
        boolean[][] camino = new boolean[filas][cols];
        Celda actual = null;
        if (resultado != null) {
            List<Celda> v = resultado.visitados();
            int n = Math.max(0, Math.min(pasos, v.size()));
            for (int i = 0; i < n; i++) {
                visitado[v.get(i).fila()][v.get(i).columna()] = true;
            }
            if (mostrarActual && n > 0) {
                actual = v.get(n - 1);
            }
            if (mostrarCamino) {
                for (Celda c : resultado.camino()) {
                    camino[c.fila()][c.columna()] = true;
                }
            }
        }

        StringBuilder sb = new StringBuilder(filas * (cols + 1));
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < cols; c++) {
                char ch = datos.esPared(f, c) ? tema.pared(datos, f, c) : tema.transitable();
                if (f == datos.entradaFila() && c == datos.entradaCol()) {
                    ch = tema.entrada();
                } else if (f == datos.salidaFila() && c == datos.salidaCol()) {
                    ch = tema.salida();
                } else {
                    if (visitado[f][c]) ch = tema.visitado();
                    if (actual != null && actual.fila() == f && actual.columna() == c) ch = tema.actual();
                    if (camino[f][c]) ch = tema.camino();
                }
                sb.append(ch);
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Leyenda de simbolos del tema (dos lineas). */
    public static String leyenda(TemaConsola tema) {
        return new StringBuilder()
                .append("Pared: ").append(tema.paredes())
                .append("   Transitable: espacio")
                .append("   Entrada: ").append(tema.entrada())
                .append("   Salida: ").append(tema.salida()).append('\n')
                .append("Visitado: ").append(tema.visitado())
                .append("   Actual: ").append(tema.actual())
                .append("   Camino final: ").append(tema.camino()).append('\n')
                .toString();
    }
}
