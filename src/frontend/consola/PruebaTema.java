package frontend.consola;

import frontend.modelo.DatosLaberinto;
import frontend.modelo.ErrorCargaException;
import frontend.modelo.ResultadoVista;
import frontend.modelo.ServicioBase;
import frontend.modelo.ServicioLaberinto;
import java.nio.file.Path;
import java.util.List;

/**
 * Comprueba que las vistas ASCII y Unicode conservan la forma del laberinto.
 * Uso (desde la raiz del repo): java -cp out frontend.consola.PruebaTema
 */
public final class PruebaTema {

    private static final String PAREDES_ASCII = "+-|";
    private static final String PAREDES_UNICODE = "─│┌┐└┘├┤┬┴┼";

    private PruebaTema() {
    }

    public static void main(String[] args) {
        SalidaConsola.configurar(TemaConsola.ASCII);
        ServicioLaberinto servicio = new ServicioBase();
        List<Path> archivos = servicio.listarEjemplos();
        if (archivos.isEmpty()) {
            System.out.println("FALLO: no se encontraron ejemplos en MapaLaberintos/.");
            System.exit(1);
        }
        int comprobados = 0;
        try {
            for (Path archivo : archivos) {
                DatosLaberinto datos;
                try {
                    datos = servicio.cargar(archivo);
                } catch (ErrorCargaException e) {
                    throw new AssertionError(archivo.getFileName() + ": " + e.getMessage());
                }
                char[][] copia = new char[datos.filas()][];
                for (int f = 0; f < copia.length; f++) {
                    copia[f] = datos.mapa()[f].clone();
                }
                String nombre = archivo.getFileName().toString();
                comprobar(datos, null, nombre);
                comprobar(datos, servicio.resolverBFS(), nombre);
                for (int f = 0; f < copia.length; f++) {
                    if (!java.util.Arrays.equals(copia[f], datos.mapa()[f])) {
                        throw new AssertionError(nombre + ": el mapa original cambio");
                    }
                }
                System.out.println("OK " + nombre);
                comprobados++;
            }
        } catch (AssertionError fallo) {
            System.out.println("FALLO: " + fallo.getMessage());
            System.exit(1);
        }
        if (comprobados == 0) {
            System.out.println("FALLO: no se comprobó ningún laberinto.");
            System.exit(1);
        }
        System.out.println("OK: " + comprobados + " laberintos comprobados en ASCII y Unicode.");
    }

    private static void comprobar(DatosLaberinto d, ResultadoVista r, String nombre) {
        int pasos = r == null ? 0 : r.visitados().size();
        String[] ascii = RenderConsola.vista(d, r, pasos, r != null && r.encontrado(), TemaConsola.ASCII).split("\n", -1);
        String[] uni = RenderConsola.vista(d, r, pasos, r != null && r.encontrado(), TemaConsola.UNICODE).split("\n", -1);
        // (a) mismas filas y columnas (el ultimo elemento es vacio por el salto final)
        if (ascii.length - 1 != d.filas() || uni.length - 1 != d.filas()) {
            throw new AssertionError(nombre + ": numero de filas distinto");
        }
        for (int f = 0; f < d.filas(); f++) {
            if (ascii[f].length() != d.columnas() || uni[f].length() != d.columnas()) {
                throw new AssertionError(nombre + ": numero de columnas distinto en fila " + f);
            }
            for (int c = 0; c < d.columnas(); c++) {
                char a = ascii[f].charAt(c);
                char u = uni[f].charAt(c);
                if (pared(d, f, c)) {
                    // (b) pared en ambas vistas
                    if (PAREDES_ASCII.indexOf(a) < 0 || PAREDES_UNICODE.indexOf(u) < 0) {
                        throw new AssertionError(nombre + ": pared perdida en " + f + "," + c);
                    }
                    // (c) ningun '+' sin convertir y esquinas/cruces correctos
                    if (u == '+') {
                        throw new AssertionError(nombre + ": '+' sin convertir en " + f + "," + c);
                    }
                    if (d.en(f, c) == '+' && u != esperado(d, f, c)) {
                        throw new AssertionError(nombre + ": simbolo incorrecto en " + f + "," + c
                                + " (se esperaba " + esperado(d, f, c) + " y hay " + u + ")");
                    }
                } else if (PAREDES_ASCII.indexOf(a) >= 0 || PAREDES_UNICODE.indexOf(u) >= 0) {
                    throw new AssertionError(nombre + ": transitable convertido en pared en " + f + "," + c);
                }
            }
        }
    }

    /** Simbolo esperado para un '+', calculado de forma independiente. */
    private static char esperado(DatosLaberinto d, int f, int c) {
        boolean n = pared(d, f - 1, c), e = pared(d, f, c + 1), s = pared(d, f + 1, c), o = pared(d, f, c - 1);
        int cuenta = (n ? 1 : 0) + (e ? 1 : 0) + (s ? 1 : 0) + (o ? 1 : 0);
        if (cuenta == 4 || cuenta == 0) return '┼';
        if (cuenta == 3) return !n ? '┬' : !s ? '┴' : !o ? '├' : '┤';
        if (cuenta == 2) {
            if (n && s) return '│';
            if (e && o) return '─';
            return n ? (e ? '└' : '┘') : (e ? '┌' : '┐');
        }
        return (n || s) ? '│' : '─';
    }

    private static boolean pared(DatosLaberinto d, int f, int c) {
        return f >= 0 && f < d.filas() && c >= 0 && c < d.columnas() && PAREDES_ASCII.indexOf(d.en(f, c)) >= 0;
    }
}
