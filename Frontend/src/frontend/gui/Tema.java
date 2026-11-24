package frontend.gui;

import java.awt.Color;
import java.awt.Font;
import java.util.Collections;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

/** Colores, fuentes y ajustes globales del tema oscuro. */
final class Tema {

    // Interfaz
    static final Color FONDO = new Color(0x12141C);
    static final Color PANEL = new Color(0x1B1F2B);
    static final Color PANEL2 = new Color(0x252B3B);
    static final Color BORDE = new Color(0x343C54);
    static final Color TEXTO = new Color(0xE6E9F2);
    static final Color TEXTO_SEC = new Color(0x9AA3B8);
    static final Color ACENTO = new Color(0x4F8CFF);
    static final Color ACENTO_HOVER = new Color(0x3A6FD8);

    // Laberinto
    static final Color PARED = new Color(0x2C3347);
    static final Color TRANSITABLE = new Color(0xE9EDF5);
    static final Color REJILLA = new Color(0, 0, 0, 22);
    static final Color ENTRADA = new Color(0x1FA85A);
    static final Color SALIDA = new Color(0xE0414F);
    static final Color VISITADO = new Color(0xB5CBF0);
    static final Color ACTUAL = new Color(0xFFB020);
    static final Color ACTUAL_BORDE = new Color(0x8A4B00);
    static final Color CAMINO = new Color(0x7C3AED);

    // Insignias
    static final Color INSIGNIA_VISITADOS = new Color(0x0E8F8B);
    static final Color INSIGNIA_CAMINO = new Color(0x7C3AED);

    private Tema() {
    }

    static Font fuente(int estilo, int tam) {
        return new Font("Segoe UI", estilo, tam);
    }

    /** Aplica look and feel multiplataforma con colores y fuentes propios. */
    static void aplicar() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignorada) {
            // Se usa el look and feel por defecto.
        }
        FontUIResource base = new FontUIResource(fuente(Font.PLAIN, 13));
        for (Object clave : Collections.list(UIManager.getDefaults().keys())) {
            if (UIManager.get(clave) instanceof Font) {
                UIManager.put(clave, base);
            }
        }
        Object[][] colores = {
            {"Panel.background", PANEL}, {"OptionPane.background", PANEL},
            {"OptionPane.messageForeground", TEXTO}, {"OptionPane.foreground", TEXTO},
            {"Label.foreground", TEXTO}, {"Label.background", PANEL},
            {"List.background", PANEL2}, {"List.foreground", TEXTO},
            {"List.selectionBackground", ACENTO}, {"List.selectionForeground", Color.WHITE},
            {"ScrollPane.background", FONDO}, {"Viewport.background", FONDO},
            {"Slider.background", PANEL}, {"Slider.foreground", ACENTO},
            {"Slider.focus", PANEL}, {"Slider.shadow", BORDE}, {"Slider.highlight", BORDE},
            {"ProgressBar.background", PANEL2}, {"ProgressBar.foreground", ACENTO},
            {"ProgressBar.selectionBackground", TEXTO}, {"ProgressBar.selectionForeground", TEXTO},
            {"ToolTip.background", PANEL2}, {"ToolTip.foreground", TEXTO},
            {"ToolTip.border", javax.swing.BorderFactory.createLineBorder(BORDE)},
            {"Table.background", PANEL2}, {"Table.foreground", TEXTO}, {"Table.gridColor", BORDE},
            {"Table.selectionBackground", ACENTO}, {"Table.selectionForeground", Color.WHITE},
            {"TableHeader.background", PANEL}, {"TableHeader.foreground", TEXTO_SEC},
            {"Button.background", PANEL2}, {"Button.foreground", TEXTO},
            {"Button.disabledText", new Color(0x6A7288)}, {"Button.select", ACENTO_HOVER},
            {"Button.focus", PANEL2},
            {"ScrollBar.background", PANEL}, {"ScrollBar.track", PANEL},
            {"ScrollBar.thumb", BORDE}, {"ScrollBar.thumbShadow", BORDE},
            {"ScrollBar.thumbHighlight", BORDE}, {"ScrollBar.trackHighlight", PANEL},
            {"ScrollBar.darkShadow", BORDE}, {"ScrollBar.shadow", BORDE},
            {"ScrollBar.highlight", BORDE},
            {"Separator.foreground", BORDE}, {"Separator.background", PANEL},
        };
        for (Object[] par : colores) {
            UIManager.put(par[0], par[1]);
        }
        UIManager.put("ScrollBar.width", 12);
    }
}
