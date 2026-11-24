package frontend.modelo;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.stream.Stream;

/**
 * SOLO PARA DEMOSTRACION. Datos simulados para probar la GUI sin los algoritmos reales.
 * Las busquedas de aqui son propias y simplificadas (no son las del proyecto).
 * Se sustituira por un adaptador real que implemente ServicioLaberinto.
 */
public class ServicioDemo implements ServicioLaberinto {

    /** Carpetas de ejemplos, en orden de busqueda (relativas al directorio de ejecucion). */
    public static final String[] CARPETAS_EJEMPLOS = {"ejemplos", "MapaLaberintos"};
    public static final String SUBCARPETA_INVALIDOS = "invalidos";

    private static final String CARACTERES_VALIDOS = "+-| #ES";

    private static final String[] EMBEBIDO = {
        "+-+-+-+-+-+-+-+",
        "|E|     |     |",
        "+ + +-+ + +-+ +",
        "|   | |   | | |",
        "+-+ + +-+-+ + +",
        "|   |       | |",
        "+ +-+-+-+-+-+ +",
        "|             S",
        "+-+-+-+-+-+-+-+"
    };

    private static final int[][] DIRECCIONES = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};

    private DatosLaberinto datos;

    // ---------- Carga ----------

    /** Si archivo es null carga un laberinto pequeno embebido. */
    @Override
    public DatosLaberinto cargar(Path archivo) throws ErrorCargaException {
        List<String> lineas;
        String nombre;
        if (archivo == null) {
            lineas = new ArrayList<>(List.of(EMBEBIDO));
            nombre = "ejemplo embebido";
        } else {
            if (!Files.exists(archivo)) {
                throw new ErrorCargaException("El archivo no existe: " + archivo);
            }
            if (!Files.isRegularFile(archivo)) {
                throw new ErrorCargaException("La ruta no es un archivo: " + archivo);
            }
            try {
                // Sin trim: los espacios al final de cada renglon son parte del mapa.
                lineas = new ArrayList<>(Files.readAllLines(archivo, StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new ErrorCargaException("No se pudo leer el archivo: " + e.getMessage());
            }
            nombre = archivo.getFileName().toString();
        }
        if (!lineas.isEmpty() && lineas.get(0).startsWith("﻿")) {
            lineas.set(0, lineas.get(0).substring(1));
        }
        while (!lineas.isEmpty() && lineas.get(lineas.size() - 1).isEmpty()) {
            lineas.remove(lineas.size() - 1);
        }
        if (lineas.isEmpty()) {
            throw new ErrorCargaException("El archivo está vacío.");
        }

        int ancho = lineas.get(0).length();
        char[][] mapa = new char[lineas.size()][];
        int ef = -1, ec = -1, sf = -1, sc = -1;
        for (int r = 0; r < lineas.size(); r++) {
            String l = lineas.get(r);
            if (l.length() != ancho) {
                throw new ErrorCargaException("Los renglones no miden lo mismo: el renglón " + (r + 1)
                        + " tiene " + l.length() + " caracteres y se esperaban " + ancho + ".");
            }
            mapa[r] = l.toCharArray();
            for (int c = 0; c < ancho; c++) {
                char ch = mapa[r][c];
                if (CARACTERES_VALIDOS.indexOf(ch) < 0) {
                    throw new ErrorCargaException("Carácter no válido '" + ch + "' en el renglón "
                            + (r + 1) + ", columna " + (c + 1) + ".");
                }
                if (ch == 'E') {
                    if (ef >= 0) throw new ErrorCargaException("Hay más de una entrada (E).");
                    ef = r;
                    ec = c;
                } else if (ch == 'S') {
                    if (sf >= 0) throw new ErrorCargaException("Hay más de una salida (S).");
                    sf = r;
                    sc = c;
                }
            }
        }

        if (ef < 0 && sf < 0) {
            // Sin letras: entrada y salida son la primera y la ultima abertura del borde.
            int[] primera = null, ultima = null;
            int total = 0;
            for (int r = 0; r < mapa.length; r++) {
                for (int c = 0; c < ancho; c++) {
                    boolean borde = r == 0 || r == mapa.length - 1 || c == 0 || c == ancho - 1;
                    if (borde && mapa[r][c] == ' ') {
                        if (primera == null) primera = new int[]{r, c};
                        ultima = new int[]{r, c};
                        total++;
                    }
                }
            }
            if (total < 2) {
                throw new ErrorCargaException("No se encontraron la entrada y la salida "
                        + "(letras E y S, o dos aberturas en el borde).");
            }
            ef = primera[0];
            ec = primera[1];
            sf = ultima[0];
            sc = ultima[1];
        } else if (ef < 0) {
            throw new ErrorCargaException("Falta la entrada (E).");
        } else if (sf < 0) {
            throw new ErrorCargaException("Falta la salida (S).");
        }

        datos = new DatosLaberinto(nombre, mapa, ef, ec, sf, sc);
        return datos;
    }

    // ---------- Resolucion simulada ----------

    @Override
    public ResultadoVista resolverDFS() {
        return buscar("DFS");
    }

    @Override
    public ResultadoVista resolverBFS() {
        return buscar("BFS");
    }

    @Override
    public ResultadoVista resolverDijkstra() {
        return buscar("Dijkstra");
    }

    @Override
    public void reiniciarResultados() {
        // La demo no guarda resultados.
    }

    /** Busqueda simple propia; solo sirve para generar datos verosimiles. */
    private ResultadoVista buscar(String algoritmo) {
        DatosLaberinto d = datos;
        if (d == null) {
            throw new IllegalStateException("No hay laberinto cargado");
        }
        int cols = d.columnas();
        int n = d.filas() * cols;
        int ini = d.entradaFila() * cols + d.entradaCol();
        int fin = d.salidaFila() * cols + d.salidaCol();

        long t0 = System.nanoTime();
        int[] padre = new int[n];
        Arrays.fill(padre, -1);
        boolean[] hecho = new boolean[n];
        List<Celda> visitados = new ArrayList<>();
        boolean encontrado = false;

        if (algoritmo.equals("BFS")) {
            ArrayDeque<Integer> cola = new ArrayDeque<>();
            cola.add(ini);
            hecho[ini] = true;
            while (!cola.isEmpty()) {
                int u = cola.poll();
                visitados.add(aCelda(u, cols));
                if (u == fin) {
                    encontrado = true;
                    break;
                }
                for (int v : vecinos(d, u)) {
                    if (!hecho[v]) {
                        hecho[v] = true;
                        padre[v] = u;
                        cola.add(v);
                    }
                }
            }
        } else if (algoritmo.equals("DFS")) {
            ArrayDeque<int[]> pila = new ArrayDeque<>();
            pila.push(new int[]{ini, -1});
            while (!pila.isEmpty()) {
                int[] e = pila.pop();
                int u = e[0];
                if (hecho[u]) continue;
                hecho[u] = true;
                padre[u] = e[1];
                visitados.add(aCelda(u, cols));
                if (u == fin) {
                    encontrado = true;
                    break;
                }
                for (int v : vecinos(d, u)) {
                    if (!hecho[v]) pila.push(new int[]{v, u});
                }
            }
        } else {
            // Dijkstra con peso 1 por movimiento: {distancia, orden, nodo, padre}
            PriorityQueue<long[]> cola = new PriorityQueue<>(
                    Comparator.<long[]>comparingLong(a -> a[0]).thenComparingLong(a -> a[1]));
            long orden = 0;
            cola.add(new long[]{0, orden++, ini, -1});
            while (!cola.isEmpty()) {
                long[] e = cola.poll();
                int u = (int) e[2];
                if (hecho[u]) continue;
                hecho[u] = true;
                padre[u] = (int) e[3];
                visitados.add(aCelda(u, cols));
                if (u == fin) {
                    encontrado = true;
                    break;
                }
                for (int v : vecinos(d, u)) {
                    if (!hecho[v]) cola.add(new long[]{e[0] + 1, orden++, v, u});
                }
            }
        }

        List<Celda> camino = new ArrayList<>();
        if (encontrado) {
            for (int u = fin; u != -1; u = padre[u]) {
                camino.add(aCelda(u, cols));
            }
            Collections.reverse(camino);
        }
        long tiempo = System.nanoTime() - t0;
        return new ResultadoVista(algoritmo, visitados, camino, encontrado, tiempo);
    }

    private static int[] vecinos(DatosLaberinto d, int u) {
        int cols = d.columnas();
        int f = u / cols, c = u % cols;
        int[] tmp = new int[4];
        int k = 0;
        for (int[] dir : DIRECCIONES) {
            int nf = f + dir[0], nc = c + dir[1];
            if (nf >= 0 && nf < d.filas() && nc >= 0 && nc < cols && !d.esPared(nf, nc)) {
                tmp[k++] = nf * cols + nc;
            }
        }
        return Arrays.copyOf(tmp, k);
    }

    private static Celda aCelda(int id, int cols) {
        return new Celda(id / cols, id % cols);
    }

    // ---------- Ejemplos ----------

    @Override
    public Optional<Path> carpetaEjemplos() {
        for (String nombre : CARPETAS_EJEMPLOS) {
            Path p = Paths.get(nombre);
            if (Files.isDirectory(p)) {
                return Optional.of(p);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Path> listarEjemplos() {
        return carpetaEjemplos().map(ServicioDemo::listarTxt).orElse(List.of());
    }

    @Override
    public List<Path> listarEjemplosInvalidos() {
        return carpetaEjemplos().map(c -> listarTxt(c.resolve(SUBCARPETA_INVALIDOS))).orElse(List.of());
    }

    private static List<Path> listarTxt(Path carpeta) {
        if (!Files.isDirectory(carpeta)) {
            return List.of();
        }
        try (Stream<Path> s = Files.list(carpeta)) {
            List<Path> r = new ArrayList<>(s.filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().toLowerCase().endsWith(".txt")).toList());
            r.sort(Comparator.comparing((Path p) -> p.getFileName().toString(), ServicioDemo::compararNatural));
            return r;
        } catch (IOException e) {
            return List.of();
        }
    }

    /** Orden natural: laberinto2 antes que laberinto10. */
    static int compararNatural(String a, String b) {
        String[] ta = a.split("(?<=\\d)(?=\\D)|(?<=\\D)(?=\\d)");
        String[] tb = b.split("(?<=\\d)(?=\\D)|(?<=\\D)(?=\\d)");
        for (int i = 0; i < Math.min(ta.length, tb.length); i++) {
            boolean na = Character.isDigit(ta[i].charAt(0)), nb = Character.isDigit(tb[i].charAt(0));
            int cmp = (na && nb) ? new BigInteger(ta[i]).compareTo(new BigInteger(tb[i]))
                                 : ta[i].compareToIgnoreCase(tb[i]);
            if (cmp != 0) return cmp;
        }
        return Integer.compare(ta.length, tb.length);
    }
}
