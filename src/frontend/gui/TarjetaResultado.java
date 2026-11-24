package frontend.gui;

import frontend.modelo.DatosLaberinto;
import frontend.modelo.ResultadoVista;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/** Tarjeta con los datos de un algoritmo; con miniatura opcional (vista de comparacion). */
final class TarjetaResultado extends JPanel {

    private final boolean conMini;
    private final JLabel titulo = new JLabel();
    private final JPanel insignias = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    private final JLabel aviso = new JLabel(" ");
    private final JLabel vVisitados = valor();
    private final JLabel vLongitud = valor();
    private final JLabel vTiempo = valor();
    private final MiniLaberinto mini = new MiniLaberinto();

    TarjetaResultado(String nombre, boolean conMini) {
        this.conMini = conMini;
        setOpaque(false);
        setLayout(new BorderLayout(0, 8));
        setBorder(new EmptyBorder(12, conMini ? 12 : 14, 12, conMini ? 12 : 14));

        titulo.setFont(Tema.fuente(Font.BOLD, 15));
        titulo.setText(nombre);
        titulo.setAlignmentX(LEFT_ALIGNMENT);
        insignias.setOpaque(false);
        insignias.setAlignmentX(LEFT_ALIGNMENT);
        // Con miniatura la tarjeta es angosta: insignias en columna y altura fija.
        insignias.setLayout(new BoxLayout(insignias, conMini ? BoxLayout.Y_AXIS : BoxLayout.X_AXIS));
        if (conMini) {
            insignias.setPreferredSize(new Dimension(10, 46));
        }

        JPanel cabecera = new JPanel();
        cabecera.setOpaque(false);
        cabecera.setLayout(new BoxLayout(cabecera, BoxLayout.Y_AXIS));
        cabecera.add(titulo);
        cabecera.add(javax.swing.Box.createVerticalStrut(4));
        cabecera.add(insignias);
        if (conMini) {
            insignias.setAlignmentX(LEFT_ALIGNMENT);
        }
        add(cabecera, BorderLayout.NORTH);

        JPanel pie = new JPanel(new BorderLayout(0, 6));
        pie.setOpaque(false);
        if (conMini) {
            aviso.setForeground(Tema.SALIDA);
            aviso.setFont(Tema.fuente(Font.BOLD, 13));
            pie.add(aviso, BorderLayout.NORTH);
            add(mini, BorderLayout.CENTER);
        }
        pie.add(filas(), BorderLayout.CENTER);
        add(pie, BorderLayout.SOUTH);
        vaciar();
    }

    private static JLabel valor() {
        JLabel l = new JLabel("-", SwingConstants.RIGHT);
        l.setFont(Tema.fuente(Font.BOLD, 13));
        return l;
    }

    private JPanel filas() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        String[] nombres = {"Visitados", "Longitud", "Tiempo"};
        JLabel[] valores = {vVisitados, vLongitud, vTiempo};
        for (int i = 0; i < 3; i++) {
            GridBagConstraints c = new GridBagConstraints();
            c.gridy = i;
            c.insets = new Insets(2, 0, 2, 0);
            c.anchor = GridBagConstraints.WEST;
            JLabel etiqueta = new JLabel(nombres[i]);
            etiqueta.setForeground(Tema.TEXTO_SEC);
            if (conMini) {
                // Etiqueta arriba y valor debajo, para que nada se corte.
                etiqueta.setFont(Tema.fuente(Font.PLAIN, 11));
                valores[i].setHorizontalAlignment(SwingConstants.LEFT);
                c.gridx = 0;
                c.weightx = 1;
                c.fill = GridBagConstraints.HORIZONTAL;
                c.gridy = i * 2;
                c.insets = new Insets(4, 0, 0, 0);
                p.add(etiqueta, c);
                c.gridy = i * 2 + 1;
                c.insets = new Insets(0, 0, 0, 0);
                p.add(valores[i], c);
                continue;
            }
            c.gridx = 0;
            p.add(etiqueta, c);
            c.gridx = 1;
            c.weightx = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.insets = new Insets(2, 12, 2, 0);
            p.add(valores[i], c);
        }
        return p;
    }

    void vaciar() {
        insignias.removeAll();
        vVisitados.setText("-");
        vLongitud.setText("-");
        vTiempo.setText("-");
        aviso.setText(" ");
        revalidate();
        repaint();
    }

    void mostrar(DatosLaberinto datos, ResultadoVista r, boolean menosVisitados, boolean caminoMinimo) {
        insignias.removeAll();
        if (menosVisitados) {
            insignias.add(new Insignia("Menos visitados", Tema.INSIGNIA_VISITADOS));
            insignias.add(conMini ? javax.swing.Box.createVerticalStrut(4)
                                  : javax.swing.Box.createHorizontalStrut(6));
        }
        if (caminoMinimo) {
            insignias.add(new Insignia("Camino mínimo", Tema.INSIGNIA_CAMINO));
        }
        vVisitados.setText(String.valueOf(r.visitados().size()));
        vLongitud.setText(Formato.longitud(r));
        vTiempo.setText(Formato.tiempo(r));
        aviso.setText(r.encontrado() ? " " : "No existe camino");
        if (conMini) {
            mini.mostrar(datos, r);
        }
        revalidate();
        repaint();
    }

    static void actualizarTodas(Map<String, TarjetaResultado> tarjetas, DatosLaberinto datos,
                                Map<String, ResultadoVista> resultados) {
        Formato.Marcas marcas = Formato.marcas(resultados.values());
        for (Map.Entry<String, TarjetaResultado> entrada : tarjetas.entrySet()) {
            ResultadoVista resultado = resultados.get(entrada.getKey());
            if (resultado == null) {
                entrada.getValue().vaciar();
            } else {
                entrada.getValue().mostrar(datos, resultado,
                        marcas.menosVisitados().contains(resultado.algoritmo()),
                        marcas.caminoMinimo().contains(resultado.algoritmo()));
            }
        }
    }

    @Override
    public Dimension getMaximumSize() {
        // En la columna lateral la tarjeta no debe estirarse en vertical.
        return conMini ? super.getMaximumSize() : new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Tema.PANEL2);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
        g2.setColor(Tema.BORDE);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
        g2.dispose();
        super.paintComponent(g);
    }
}
