package laberintoproyectofundamentosiii;

public class LaberintoInvalidoException extends Exception {
    public LaberintoInvalidoException(String motivo) {
        super(motivo);
    }

    public LaberintoInvalidoException(String motivo, Throwable causa) {
        super(motivo, causa);
    }
}
