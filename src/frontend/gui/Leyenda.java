package frontend.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** Leyenda compacta: cada muestra de color va pegada a su texto. */
final class Leyenda extends JPanel {

    Leyenda() {
        super(new FlowLayout(FlowLayout.LEFT, 14, 2));
        setOpaque(false);
        item("Pared", Tema.PARED);
        item("Transitable", Tema.TRANSITABLE);
        item("Entrada", Tema.ENTRADA);
        item("Salida", Tema.SALIDA);
        item("Visitado", Tema.VISITADO);
        item("Actual", Tema.ACTUAL);
        item("Camino final", Tema.CAMINO);
    }

    private void item(String texto, Color color) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        p.setOpaque(false);
        p.add(new Muestra(color));
        JLabel l = new JLabel(texto);
        l.setFont(Tema.fuente(Font.PLAIN, 12));
        l.setForeground(Tema.TEXTO_SEC);
        p.add(l);
        add(p);
    }

    private static final class Muestra extends JComponent {
        private final Color color;

        Muestra(Color color) {
            this.color = color;
            setPreferredSize(new Dimension(14, 14));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillRoundRect(0, 0, 13, 13, 5, 5);
            g2.setColor(Tema.BORDE);
            g2.drawRoundRect(0, 0, 13, 13, 5, 5);
            g2.dispose();
        }
    }
}
