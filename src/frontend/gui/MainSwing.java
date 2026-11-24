package frontend.gui;

import frontend.modelo.ServicioBase;
import javax.swing.SwingUtilities;

/** Punto de entrada de la GUI, conectada a la base mediante ServicioBase. */
public final class MainSwing {

    private MainSwing() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Tema.aplicar();
            new VentanaLaberinto(new ServicioBase()).setVisible(true);
        });
    }
}
