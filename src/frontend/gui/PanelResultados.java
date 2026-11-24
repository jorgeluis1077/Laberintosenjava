package frontend.gui;

import frontend.modelo.DatosLaberinto;
import frontend.modelo.ResultadoVista;
import java.awt.Dimension;
import java.awt.Font;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/** Columna derecha: una tarjeta por algoritmo. */
final class PanelResultados extends JPanel {

    private final Map<String, TarjetaResultado> tarjetas = new LinkedHashMap<>();

    PanelResultados(String[] algoritmos) {
        setBackground(Tema.PANEL);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, Tema.BORDE), new EmptyBorder(12, 12, 12, 12)));
        setPreferredSize(new Dimension(300, 10));

        JLabel titulo = new JLabel("Resultados");
        titulo.setFont(Tema.fuente(Font.BOLD, 15));
        titulo.setAlignmentX(LEFT_ALIGNMENT);
        add(titulo);
        add(Box.createVerticalStrut(10));
        for (String nombre : algoritmos) {
            TarjetaResultado t = new TarjetaResultado(nombre, false);
            t.setAlignmentX(LEFT_ALIGNMENT);
            tarjetas.put(nombre, t);
            add(t);
            add(Box.createVerticalStrut(10));
        }
        add(Box.createVerticalGlue());
    }

    void limpiar() {
        tarjetas.values().forEach(TarjetaResultado::vaciar);
    }

    void actualizar(DatosLaberinto datos, Map<String, ResultadoVista> resultados) {
        Formato.Marcas m = Formato.marcas(resultados.values());
        for (Map.Entry<String, TarjetaResultado> e : tarjetas.entrySet()) {
            ResultadoVista r = resultados.get(e.getKey());
            if (r == null) {
                e.getValue().vaciar();
            } else {
                e.getValue().mostrar(datos, r, m.menosVisitados().contains(r.algoritmo()),
                        m.caminoMinimo().contains(r.algoritmo()));
            }
        }
    }
}
