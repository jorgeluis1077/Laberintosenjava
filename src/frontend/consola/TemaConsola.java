package frontend.consola;

import frontend.modelo.DatosLaberinto;

/**
 * Simbolos de presentacion en consola. Solo afecta a lo que se imprime:
 * el mapa de entrada sigue usando + - | espacio # E S.
 * Cada posicion sigue siendo UN caracter (mismas filas y columnas).
 */
public enum TemaConsola {

    ASCII("+ - |", 'E', 'S', '.', '@', '*'),
    UNICODE("─ │ ┌ ┐ └ ┘ ├ ┤ ┬ ┴ ┼", '▶', '◆', '·', '◉', '●');

    private final String paredes;
    private final char entrada;
    private final char salida;
    private final char visitado;
    private final char actual;
    private final char camino;

    TemaConsola(String paredes, char entrada, char salida, char visitado, char actual, char camino) {
        this.paredes = paredes;
        this.entrada = entrada;
        this.salida = salida;
        this.visitado = visitado;
        this.actual = actual;
        this.camino = camino;
    }

    public char transitable() {
        return ' ';
    }

    public char entrada() {
        return entrada;
    }

    public char salida() {
        return salida;
    }

    public char visitado() {
        return visitado;
    }

    public char actual() {
        return actual;
    }

    public char camino() {
        return camino;
    }

    /** Descripcion de los simbolos de pared para la leyenda. */
    public String paredes() {
        return paredes;
    }

    /** Simbolo de pared en (fila, columna), o espacio si es transitable. */
    public char pared(DatosLaberinto datos, int fila, int columna) {
        if (!datos.esPared(fila, columna)) {
            return transitable();
        }
        char original = datos.en(fila, columna);
        if (this == ASCII) {
            return original;
        }
        if (original == '-') {
            return '─';
        }
        if (original == '|') {
            return '│';
        }
        // Un bit por vecino con pared: arriba 1, derecha 2, abajo 4, izquierda 8.
        int vecinos = 0;
        if (hayPared(datos, fila - 1, columna)) vecinos |= 1;
        if (hayPared(datos, fila, columna + 1)) vecinos |= 2;
        if (hayPared(datos, fila + 1, columna)) vecinos |= 4;
        if (hayPared(datos, fila, columna - 1)) vecinos |= 8;
        return switch (vecinos) {
            case 6 -> '┌';
            case 12 -> '┐';
            case 3 -> '└';
            case 9 -> '┘';
            case 7 -> '├';
            case 13 -> '┤';
            case 14 -> '┬';
            case 11 -> '┴';
            case 2, 8, 10 -> '─';
            case 1, 4, 5 -> '│';
            default -> '┼'; // sin vecinos o cruce completo
        };
    }

    private static boolean hayPared(DatosLaberinto datos, int fila, int columna) {
        if (fila < 0 || fila >= datos.filas() || columna < 0 || columna >= datos.columnas()) {
            return false;
        }
        return datos.esPared(fila, columna);
    }
}
