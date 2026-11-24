package frontend.modelo;

/**
 * Laberinto tal cual esta en el archivo: una matriz de caracteres
 * ('+', '-', '|', ' ', '#', 'E', 'S'). Cada posicion de la matriz es una celda a dibujar.
 */
public record DatosLaberinto(String nombreArchivo, char[][] mapa,
                             int entradaFila, int entradaCol,
                             int salidaFila, int salidaCol) {

    public int filas() {
        return mapa.length;
    }

    public int columnas() {
        return mapa[0].length;
    }

    public char en(int fila, int col) {
        return mapa[fila][col];
    }

    public boolean esPared(int fila, int col) {
        char c = mapa[fila][col];
        return c == '+' || c == '-' || c == '|';
    }

    public DatosLaberinto conNombre(String nombre) {
        return new DatosLaberinto(nombre, mapa, entradaFila, entradaCol, salidaFila, salidaCol);
    }
}
