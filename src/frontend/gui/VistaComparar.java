package frontend.gui;

import frontend.modelo.DatosLaberinto;
import frontend.modelo.ResultadoVista;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/** Vista con una tarjeta por algoritmo y un boton para volver. */
final class VistaComparar extends JPanel {

    private final Map<String, TarjetaResultado> tarjetas = new LinkedHashMap<>();

    VistaComparar(String[] algoritmos, Runnable alVolver) {
        setLayout(new BorderLayout(0, 12));
        setBackground(Tema.PANEL);
        setBorder(new EmptyBorder(14, 14, 14, 14));

        JLabel titulo = new JLabel("Comparación de algoritmos");
        titulo.setFont(Tema.fuente(Font.BOLD, 18));
        BotonEstilo volver = new BotonEstilo("Volver");
        volver.addActionListener(e -> alVolver.run());
        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setOpaque(false);
        cabecera.add(titulo, BorderLayout.WEST);
        cabecera.add(volver, BorderLayout.EAST);
        add(cabecera, BorderLayout.NORTH);

        JPanel rejilla = new JPanel(new GridLayout(1, algoritmos.length, 12, 0));
        rejilla.setOpaque(false);
        for (String nombre : algoritmos) {
            TarjetaResultado t = new TarjetaResultado(nombre, true);
            tarjetas.put(nombre, t);
            rejilla.add(t);
        }
        add(rejilla, BorderLayout.CENTER);
    }

    void mostrar(DatosLaberinto datos, Map<String, ResultadoVista> resultados) {
        TarjetaResultado.actualizarTodas(tarjetas, datos, resultados);
    }
}
