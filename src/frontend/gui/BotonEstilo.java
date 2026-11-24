package frontend.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;

/** Boton con estilo unico: esquinas redondeadas, hover y estado atenuado. */
final class BotonEstilo extends JButton {

    private boolean encima;

    BotonEstilo(String texto) {
        this(texto, false);
    }

    BotonEstilo(String texto, boolean compacto) {
        super(texto);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setForeground(Tema.TEXTO);
        setFont(Tema.fuente(Font.BOLD, 13));
        setBorder(compacto ? new EmptyBorder(5, 12, 5, 12) : new EmptyBorder(8, 16, 8, 16));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                encima = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                encima = false;
                repaint();
            }
        });
    }

    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        setCursor(Cursor.getPredefinedCursor(b ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color fondo;
        if (!isEnabled()) {
            fondo = new Color(0x1E2330);
        } else if (getModel().isPressed()) {
            fondo = Tema.ACENTO_HOVER;
        } else if (encima) {
            fondo = new Color(0x303851);
        } else {
            fondo = Tema.PANEL2;
        }
        g2.setColor(fondo);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
        g2.setColor(isEnabled() && encima ? Tema.ACENTO : Tema.BORDE);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
        g2.dispose();
        super.paintComponent(g);
    }
}
