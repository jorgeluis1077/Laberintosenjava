package frontend.consola;

import java.io.Console;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/** Configura System.out en UTF-8 y decide si el tema Unicode se puede usar. */
public final class SalidaConsola {

    private SalidaConsola() {
    }

    /**
     * Pone System.out en UTF-8. Devuelve el tema efectivo: si se pidio UNICODE
     * y la salida no lo admite, avisa y devuelve ASCII.
     */
    public static TemaConsola configurar(TemaConsola pedido) {
        PrintStream utf8;
        try {
            utf8 = new PrintStream(System.out, true, StandardCharsets.UTF_8);
            System.setOut(utf8);
        } catch (RuntimeException e) {
            return respaldo(pedido, "no se pudo configurar UTF-8");
        }
        if (pedido == TemaConsola.ASCII) {
            return pedido;
        }
        // Si hay consola real, su pagina de codigos debe ser UTF-8 (en Windows: chcp 65001).
        Console consola = System.console();
        if (consola != null) {
            Charset cs = consola.charset();
            if (!cs.equals(StandardCharsets.UTF_8)) {
                return respaldo(pedido, "la consola usa " + cs.displayName());
            }
        }
        if (utf8.checkError()) {
            return respaldo(pedido, "la salida no acepta caracteres Unicode");
        }
        return pedido;
    }

    private static TemaConsola respaldo(TemaConsola pedido, String motivo) {
        if (pedido == TemaConsola.ASCII) {
            return pedido;
        }
        System.out.println("Aviso: " + motivo + "; se usa el estilo ASCII.");
        return TemaConsola.ASCII;
    }
}
