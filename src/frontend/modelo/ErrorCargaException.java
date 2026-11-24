package frontend.modelo;

/** Error al cargar un laberinto; el mensaje esta pensado para mostrarse al usuario. */
public class ErrorCargaException extends Exception {
    private static final long serialVersionUID = 1L;

    public ErrorCargaException(String mensaje) {
        super(mensaje);
    }
}
