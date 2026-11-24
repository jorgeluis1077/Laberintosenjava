package frontend.gui;

import frontend.modelo.DatosLaberinto;
import frontend.modelo.ResultadoVista;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.JPanel;

/** Miniatura del laberinto ajustada al tamano disponible, con el resultado final. */
final class MiniLaberinto extends JPanel {

    private EstadoVista estado;

    MiniLaberinto() {
        setBackground(Tema.FONDO);
        setPreferredSize(new Dimension(220, 200));
    }

    void mostrar(DatosLaberinto datos, ResultadoVista r) {
        estado = EstadoVista.desde(datos, r, r.visitados().size(), r.encontrado());
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        if (estado == null) {
            return;
        }
        int filas = estado.datos.filas();
        int cols = estado.datos.columnas();
        int margen = 6;
        int tam = Math.max(1, Math.min((getWidth() - 2 * margen) / cols, (getHeight() - 2 * margen) / filas));
        int ox = (getWidth() - cols * tam) / 2;
        int oy = (getHeight() - filas * tam) / 2;
        Graphics2D g = (Graphics2D) g0.create();
        RenderLaberinto.pintar(g, estado, tam, ox, oy, new Rectangle(0, 0, getWidth(), getHeight()));
        g.dispose();
    }
}
