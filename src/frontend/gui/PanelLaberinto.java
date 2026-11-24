package frontend.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Scrollable;

/** Dibuja el laberinto en un solo componente, pintando solo la region visible. */
final class PanelLaberinto extends JPanel implements Scrollable {

    static final int MARGEN = 16;

    private EstadoVista estado;
    private int tamanoCelda = 24;

    PanelLaberinto() {
        setBackground(Tema.FONDO);
    }

    void setEstado(EstadoVista nuevo) {
        boolean cambiaTamano = estado == null || nuevo == null || estado.datos != nuevo.datos;
        estado = nuevo;
        if (cambiaTamano) {
            revalidate();
        }
        repaint();
    }

    void setTam(int nuevo) {
        if (nuevo != tamanoCelda) {
            tamanoCelda = nuevo;
            revalidate();
            repaint();
        }
    }

    @Override
    public Dimension getPreferredSize() {
        if (estado == null) {
            return new Dimension(200, 200);
        }
        return new Dimension(estado.datos.columnas() * tamanoCelda + 2 * MARGEN,
                estado.datos.filas() * tamanoCelda + 2 * MARGEN);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        if (estado == null) {
            return;
        }
        Graphics2D g = (Graphics2D) g0.create();
        Rectangle clip = g.getClipBounds();
        if (clip == null) {
            clip = new Rectangle(0, 0, getWidth(), getHeight());
        }
        int ox = Math.max(MARGEN, (getWidth() - estado.datos.columnas() * tamanoCelda) / 2);
        int oy = Math.max(MARGEN, (getHeight() - estado.datos.filas() * tamanoCelda) / 2);
        RenderLaberinto.pintar(g, estado, tamanoCelda, ox, oy, clip);
        g.dispose();
    }

    // ---- Scrollable: si cabe, se estira al viewport para centrar el dibujo ----

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visible, int orientation, int direction) {
        return tamanoCelda;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visible, int orientation, int direction) {
        return (orientation == javax.swing.SwingConstants.VERTICAL ? visible.height : visible.width) - tamanoCelda;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return getParent() instanceof JViewport vp && vp.getWidth() > getPreferredSize().width;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return getParent() instanceof JViewport vp && vp.getHeight() > getPreferredSize().height;
    }
}
