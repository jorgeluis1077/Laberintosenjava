package frontend.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

/** Etiqueta pequena con fondo de color (ej. "Menos visitados"). */
final class Insignia extends JLabel {

    private final Color fondo;

    Insignia(String texto, Color fondo) {
        super(texto);
        this.fondo = fondo;
        setOpaque(false);
        setAlignmentX(LEFT_ALIGNMENT);
        setForeground(Color.WHITE);
        setFont(Tema.fuente(Font.BOLD, 11));
        setBorder(new EmptyBorder(2, 8, 2, 8));
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(fondo);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }
}
