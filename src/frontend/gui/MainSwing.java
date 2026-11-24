package frontend.gui;

import frontend.modelo.ServicioDemo;
import javax.swing.SwingUtilities;

/** Punto de entrada. Por ahora usa ServicioDemo; se cambiara por el adaptador real. */
public final class MainSwing {

    private MainSwing() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Tema.aplicar();
            new VentanaLaberinto(new ServicioDemo()).setVisible(true);
        });
    }
}
