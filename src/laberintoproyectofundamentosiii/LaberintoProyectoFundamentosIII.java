package laberintoproyectofundamentosiii;

import frontend.consola.SalidaConsola;
import frontend.consola.TemaConsola;
import frontend.modelo.DatosLaberinto;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.function.Function;
import java.util.regex.Pattern;

public class LaberintoProyectoFundamentosIII {
    private static final Pattern LIMITE_NUMERICO = Pattern.compile("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");

    /** Algoritmos del menu; tambien los usa la GUI a traves de frontend.modelo.ServicioBase. */
    public enum Algoritmo {
        DFS("DFS", Grafo::DFS, Grafo::getUltimoCaminoDFS),
        BFS("BFS", Grafo::BFS, Grafo::getUltimoCaminoBFS),
        DIJKSTRA("Dijkstra", Grafo::caminoMasCorto, Grafo::getUltimosVisitadosDijkstra);

        private final String nombre;
        private final Function<Grafo, List<Vertice>> buscar;
        private final Function<Grafo, List<Vertice>> leerComplemento;

        Algoritmo(String nombre, Function<Grafo, List<Vertice>> buscar,
                  Function<Grafo, List<Vertice>> leerComplemento) {
            this.nombre = nombre;
            this.buscar = buscar;
            this.leerComplemento = leerComplemento;
        }

        @Override
        public String toString() {
            return nombre;
        }

        /** Mide con System.nanoTime() solo la llamada al algoritmo. */
        public ResultadoBusqueda ejecutar(Grafo grafo) {
            long inicio = System.nanoTime();
            List<Vertice> principal = buscar.apply(grafo);
            long tiempoNanos = System.nanoTime() - inicio;
            List<Vertice> complemento = leerComplemento.apply(grafo);
            // Dijkstra devuelve el camino; DFS y BFS devuelven los visitados.
            return this == DIJKSTRA ? new ResultadoBusqueda(complemento, principal, tiempoNanos)
                    : new ResultadoBusqueda(principal, complemento, tiempoNanos);
        }
    }

    public record ResultadoBusqueda(List<Vertice> visitados, List<Vertice> camino, long tiempoNanos) {}

    public static void main(String[] args) {
        ejecutarMenu(Arrays.asList(args).contains("--unicode"));
    }

    private static void ejecutarMenu(boolean unicode) {
        Scanner sc = new Scanner(System.in);
        if (!unicode) {
            System.out.println("Estilo de símbolos: 1) ASCII  2) Unicode (ENTER = ASCII)");
            unicode = sc.hasNextLine() && sc.nextLine().strip().equals("2");
        }
        TemaConsola tema = SalidaConsola.configurar(unicode ? TemaConsola.UNICODE : TemaConsola.ASCII);
        DatosLaberinto datos = null;
        Grafo grafo = null;
        Algoritmo[] algoritmos = Algoritmo.values();
        EnumMap<Algoritmo, ResultadoBusqueda> resultados = new EnumMap<>(Algoritmo.class);
        int opcion = 0;

        do {
            System.out.println("\n1. Cargar laberinto");
            System.out.println("2. Resolver con DFS");
            System.out.println("3. Resolver con BFS");
            System.out.println("4. Resolver con Dijkstra(caminoMasCorto)");
            System.out.println("5. Mostrar las tres soluciones");
            System.out.println("6. Salir");
            System.out.print("Opcion: ");

            if (!sc.hasNextLine()) {
                break;
            }
            try {
                opcion = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Ingresa un numero valido");
                continue;
            }

            if (opcion == 1) {
                String nuevoMapa;
                try {
                    nuevoMapa = seleccionarYLeerLaberinto(sc);
                } catch (LaberintoInvalidoException e) {
                    System.out.println("Error al cargar: " + e.getMessage() + " Se conserva el laberinto anterior.");
                    continue;
                }

                if (nuevoMapa != null) {
                    Grafo nuevoGrafo = construirGrafoDesdeMapa(nuevoMapa);
                    if (nuevoGrafo != null && nuevoGrafo.getInicio() != null && nuevoGrafo.getMeta() != null) {
                        grafo = nuevoGrafo;
                        datos = Despliegue.datosDelMapa(nuevoMapa, grafo);
                        resultados.clear();
                        System.out.println("Se cargo el archivo y se construyo el grafo.\n" + nuevoMapa);
                    } else {
                        System.out.println("No se pudo construir el grafo (verifica inicio y meta). Se conserva el laberinto anterior.");
                    }
                } else {
                    System.out.println("Carga cancelada. Se conserva el laberinto anterior.");
                }
            } else if (opcion >= 2 && opcion <= 5) {
                if (grafo == null) {
                    System.out.println("Carga primero un laberinto (opcion 1).");
                } else if (opcion == 5) {
                    System.out.println("\n=== RESUMEN DE LAS 3 SOLUCIONES ===");
                    for (Algoritmo algoritmo : algoritmos) {
                        ResultadoBusqueda resultado = resultados.get(algoritmo);
                        if (resultado == null) {
                            resultado = algoritmo.ejecutar(grafo);
                            resultados.put(algoritmo, resultado);
                        }
                        new Despliegue(datos, sc, tema, algoritmo.nombre,
                                resultado.camino(), resultado.tiempoNanos())
                                .mostrarSolucion(resultado.visitados());
                    }
                } else {
                    Algoritmo algoritmo = algoritmos[opcion - 2];
                    System.out.println("Ejecutando " + algoritmo.nombre + "...");
                    ResultadoBusqueda resultado = algoritmo.ejecutar(grafo);
                    resultados.put(algoritmo, resultado);
                    new Despliegue(datos, sc, tema, algoritmo.nombre,
                            resultado.camino(), resultado.tiempoNanos())
                            .despliegaLaberinto(resultado.visitados());
                }
            } else if (opcion != 6) {
                System.out.println("La opcion que ingresaste no es correcta. Intenta de nuevo");
            }
        } while (opcion != 6);

        sc.close();
    }

    public static Grafo construirGrafoDesdeMapa(String mapa) {
        if (mapa == null || mapa.isEmpty()) {
            return null;
        }

        String[] renglones = mapa.split("\n");
        int filas = renglones.length;
        int columnas = renglones[0].length();

        Grafo grafo = new Grafo(columnas);
        Vertice[][] matrizVertices = new Vertice[filas][columnas];

        boolean tieneEYS = false;
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                char ch = renglones[f].charAt(c);
                if (ch == 'E' || ch == 'S') {
                    tieneEYS = true;
                }
            }
        }

        int bordesEncontrados = 0;

        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                char ch = renglones[f].charAt(c);
                boolean esTransitable = (ch == ' ' || ch == '#' || ch == 'E' || ch == 'S');
                if (!esTransitable) {
                    continue;
                }

                boolean esEntrada = (ch == 'E');
                boolean esSalida = (ch == 'S');

                if (!tieneEYS) {
                    boolean enBorde = (f == 0 || f == filas - 1 || c == 0 || c == columnas - 1);
                    if (enBorde) {
                        if (bordesEncontrados == 0) {
                            esEntrada = true;
                        } else if (bordesEncontrados == 1) {
                            esSalida = true;
                        }
                        bordesEncontrados++;
                    }
                }

                Vertice v = new Vertice(f, c, esEntrada, esSalida);
                matrizVertices[f][c] = v;
                grafo.agregarVertice(v);

            }
        }

        // Orden de vecinos: arriba, derecha, abajo, izquierda
        int[] df = {-1, 0, 1, 0};
        int[] dc = {0, 1, 0, -1};

        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                Vertice origen = matrizVertices[f][c];
                if (origen == null) {
                    continue;
                }

                for (int d = 0; d < 4; d++) {
                    int nf = f + df[d];
                    int nc = c + dc[d];
                    if (nf >= 0 && nf < filas && nc >= 0 && nc < columnas) {
                        Vertice destino = matrizVertices[nf][nc];
                        if (destino != null) {
                            grafo.agregarArco(origen, destino, 1);
                        }
                    }
                }
            }
        }

        return grafo;
    }

    private static String seleccionarYLeerLaberinto(Scanner sc) throws LaberintoInvalidoException {
        System.out.print("Ruta del archivo (ENTER para listar los mapas): ");
        if (!sc.hasNextLine()) {
            return null;
        }
        String ruta = sc.nextLine().strip();
        if (!ruta.isEmpty()) {
            char extremo = ruta.charAt(0);
            if (ruta.length() >= 2 && (extremo == '"' || extremo == '\'')
                    && ruta.charAt(ruta.length() - 1) == extremo) {
                ruta = ruta.substring(1, ruta.length() - 1);
            }
            return validarYLeerLaberinto(ruta);
        }

        File carpeta = new File("MapaLaberintos");
        File[] archivosTxt = carpeta.listFiles(archivo ->
                archivo.isFile() && archivo.getName().toLowerCase(Locale.ROOT).endsWith(".txt")
        );

        if (archivosTxt == null || archivosTxt.length == 0) {
            throw new LaberintoInvalidoException("No se encontraron archivos .txt en: " + carpeta.getAbsolutePath());
        }

        Arrays.sort(archivosTxt, (a, b) -> compararNombres(a.getName(), b.getName()));

        System.out.println("\nArchivos .txt disponibles:");
        for (int i = 0; i < archivosTxt.length; i++) {
            System.out.println((i + 1) + ". " + archivosTxt[i].getName());
        }

        System.out.print("Elige el numero del archivo (0 para cancelar): ");
        if (!sc.hasNextLine()) {
            return null;
        }
        int eleccion;
        try {
            eleccion = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            throw new LaberintoInvalidoException("Debes ingresar un numero valido.", e);
        }

        if (eleccion == 0) {
            return null;
        }

        if (eleccion < 1 || eleccion > archivosTxt.length) {
            throw new LaberintoInvalidoException("Numero fuera de rango.");
        }

        File archivoElegido = archivosTxt[eleccion - 1];
        System.out.println("Cargando: " + archivoElegido.getName());
        return validarYLeerLaberinto(archivoElegido.getPath());
    }

    /** Orden natural: laberinto2 antes que laberinto10. */
    public static int compararNombres(String nombreA, String nombreB) {
        String[] partesA = LIMITE_NUMERICO.split(nombreA);
        String[] partesB = LIMITE_NUMERICO.split(nombreB);
        for (int i = 0; i < Math.min(partesA.length, partesB.length); i++) {
            String parteA = partesA[i];
            String parteB = partesB[i];
            int orden = esDigitoAscii(parteA.charAt(0)) && esDigitoAscii(parteB.charAt(0))
                    ? new BigInteger(parteA).compareTo(new BigInteger(parteB))
                    : parteA.compareToIgnoreCase(parteB);
            if (orden != 0) {
                return orden;
            }
        }
        int orden = Integer.compare(partesA.length, partesB.length);
        return orden != 0 ? orden : nombreA.compareTo(nombreB);
    }

    private static boolean esDigitoAscii(char caracter) {
        return caracter >= '0' && caracter <= '9';
    }

    public static String validarYLeerLaberinto(String ruta) throws LaberintoInvalidoException {
        List<String> renglones = leerRenglones(ruta);
        if (!renglones.isEmpty() && renglones.get(0).startsWith("\uFEFF")) {
            renglones.set(0, renglones.get(0).substring(1));
        }
        int totalFilas = renglones.size();
        while (totalFilas > 0 && renglones.get(totalFilas - 1).isEmpty()) {
            totalFilas--;
        }
        if (totalFilas == 0) {
            throw new LaberintoInvalidoException("El archivo está vacío o solo contiene saltos de línea.");
        }

        int totalColumnas = renglones.get(0).length();
        int cantidadE = 0;
        int cantidadS = 0;
        String entrada = null;
        String salida = null;
        int transitablesEnBorde = 0;
        int[] filasBorde = new int[2];
        int[] columnasBorde = new int[2];

        for (int fila = 0; fila < totalFilas; fila++) {
            String renglon = renglones.get(fila);
            if (renglon.isEmpty()) {
                throw new LaberintoInvalidoException("El renglón " + (fila + 1) + " tiene longitud 0.");
            }
            if (renglon.length() != totalColumnas) {
                throw new LaberintoInvalidoException("El renglón " + (fila + 1) + " tiene distinta longitud.");
            }
            for (int col = 0; col < totalColumnas; col++) {
                char caracter = renglon.charAt(col);
                switch (caracter) {
                    case '+', '-', '|' -> { }
                    case 'E' -> {
                        cantidadE++;
                        entrada = coordenadas(fila, col);
                    }
                    case 'S' -> {
                        cantidadS++;
                        salida = coordenadas(fila, col);
                    }
                    case ' ', '#' -> {
                        if (fila == 0 || fila == totalFilas - 1 || col == 0 || col == totalColumnas - 1) {
                            if (transitablesEnBorde < filasBorde.length) {
                                filasBorde[transitablesEnBorde] = fila;
                                columnasBorde[transitablesEnBorde] = col;
                            }
                            transitablesEnBorde++;
                        }
                    }
                    default -> {
                        String motivo = caracter == '\t' ? "Tabulación inválida" : "Carácter inválido '" + caracter + "'";
                        throw new LaberintoInvalidoException(motivo + " en fila " + (fila + 1)
                                + ", columna " + (col + 1) + ".");
                    }
                }
            }
        }

        if (cantidadE > 0 || cantidadS > 0) {
            if (cantidadE != 1 || cantidadS != 1) {
                throw new LaberintoInvalidoException("Debe haber exactamente una E y una S.");
            }
            System.out.println("Entrada: " + entrada + " | Salida: " + salida);
        } else {
            if (transitablesEnBorde != 2) {
                throw new LaberintoInvalidoException("Sin E/S debe haber exactamente 2 posiciones transitables en el borde. "
                        + "Se encontraron " + transitablesEnBorde + ".");
            }
            System.out.println("Entrada borde: " + coordenadas(filasBorde[0], columnasBorde[0])
                    + " | Salida borde: " + coordenadas(filasBorde[1], columnasBorde[1]));
        }

        return String.join("\n", renglones.subList(0, totalFilas)) + "\n";
    }

    private static List<String> leerRenglones(String ruta) throws LaberintoInvalidoException {
        try {
            Path archivo = Path.of(ruta);
            if (Files.notExists(archivo)) {
                throw new LaberintoInvalidoException("El archivo no existe.");
            }
            if (!Files.isRegularFile(archivo) || !Files.isReadable(archivo)) {
                throw new LaberintoInvalidoException("La ruta debe corresponder a un archivo legible.");
            }
            return new ArrayList<>(Files.readAllLines(archivo, StandardCharsets.UTF_8));
        } catch (InvalidPathException e) {
            throw new LaberintoInvalidoException("La ruta del archivo no es válida.", e);
        } catch (CharacterCodingException e) {
            throw new LaberintoInvalidoException("El archivo no contiene texto UTF-8 válido.", e);
        } catch (IOException | SecurityException e) {
            throw new LaberintoInvalidoException("No se puede leer el archivo.", e);
        }
    }

    private static String coordenadas(int fila, int columna) {
        return "(" + fila + "," + columna + ")";
    }
}
