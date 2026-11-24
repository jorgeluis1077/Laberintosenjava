package frontend.gui;

import frontend.modelo.DatosLaberinto;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

/** Dibujo de las celdas; lo comparten el panel principal y las miniaturas. */
final class RenderLaberinto {

    private RenderLaberinto() {
    }

    /**
     * Pinta solo las celdas que cruzan {@code clip}.
     * Capas: paredes, entrada, salida, visitados, actual, camino final.
     */
    static void pintar(Graphics2D g, EstadoVista e, int tamanoCelda, int ox, int oy, Rectangle clip) {
        DatosLaberinto d = e.datos;
        int c0 = Math.max(0, (clip.x - ox) / tamanoCelda);
        int c1 = Math.min(d.columnas() - 1, (clip.x + clip.width - ox) / tamanoCelda);
        int r0 = Math.max(0, (clip.y - oy) / tamanoCelda);
        int r1 = Math.min(d.filas() - 1, (clip.y + clip.height - oy) / tamanoCelda);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        boolean suave = tamanoCelda >= 10;
        int arco = Math.max(4, tamanoCelda / 3);
        Font letra = suave ? Tema.fuente(Font.BOLD, Math.round(tamanoCelda * 0.55f)) : null;
        BasicStroke trazo = new BasicStroke(2f);

        for (int r = r0; r <= r1; r++) {
            for (int c = c0; c <= c1; c++) {
                int x = ox + c * tamanoCelda;
                int y = oy + r * tamanoCelda;
                if (d.esPared(r, c)) {
                    g.setColor(Tema.PARED);
                    g.fillRect(x, y, tamanoCelda, tamanoCelda);
                    continue;
                }
                g.setColor(Tema.TRANSITABLE);
                g.fillRect(x, y, tamanoCelda, tamanoCelda);
                if (suave) {
                    g.setColor(Tema.REJILLA);
                    g.drawRect(x, y, tamanoCelda - 1, tamanoCelda - 1);
                }

                boolean esEntrada = r == d.entradaFila() && c == d.entradaCol();
                boolean esSalida = r == d.salidaFila() && c == d.salidaCol();
                boolean esActual = e.actual != null && e.actual.fila() == r && e.actual.columna() == c;

                Color relleno = null;
                if (e.camino[r][c]) {
                    relleno = Tema.CAMINO;
                } else if (esActual) {
                    relleno = Tema.ACTUAL;
                } else if (esEntrada) {
                    relleno = Tema.ENTRADA;
                } else if (esSalida) {
                    relleno = Tema.SALIDA;
                } else if (e.visitado[r][c]) {
                    relleno = Tema.VISITADO;
                }
                if (relleno != null) {
                    g.setColor(relleno);
                    if (suave) {
                        g.fillRoundRect(x + 1, y + 1, tamanoCelda - 2, tamanoCelda - 2, arco, arco);
                    } else {
                        g.fillRect(x, y, tamanoCelda, tamanoCelda);
                    }
                }
                if (esActual && suave) {
                    g.setColor(Tema.ACTUAL_BORDE);
                    g.setStroke(trazo);
                    g.drawRoundRect(x + 1, y + 1, tamanoCelda - 3, tamanoCelda - 3, arco, arco);
                    g.setStroke(new BasicStroke(1f));
                }
                if (suave && (esEntrada || esSalida) && !esActual && !e.camino[r][c]) {
                    g.setFont(letra);
                    g.setColor(Color.WHITE);
                    FontMetrics fm = g.getFontMetrics();
                    String s = esEntrada ? "E" : "S";
                    g.drawString(s, x + (tamanoCelda - fm.stringWidth(s)) / 2,
                            y + (tamanoCelda - fm.getHeight()) / 2 + fm.getAscent());
                }
            }
        }
    }
}
