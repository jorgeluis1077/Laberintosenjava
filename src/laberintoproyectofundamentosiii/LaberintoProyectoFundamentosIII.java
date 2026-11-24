package laberintoproyectofundamentosiii;

import java.io.File;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Pattern;

public class LaberintoProyectoFundamentosIII {
    private static final Pattern LIMITE_NUMERICO = Pattern.compile("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");

    public static void main(String[] args) {
        ejecutarMenu();
    }

    public static void ejecutarMenu() {
        Scanner sc = new Scanner(System.in);
        String mapa = null;
        Grafo grafo = null;

        List<Vertice> resultadoDFS = null;
        List<Vertice> caminoDFS = null;

        List<Vertice> resultadoBFS = null;
        List<Vertice> caminoBFS = null;

        List<Vertice> caminoCorto = null;

        boolean hayDFS = false;
        boolean hayBFS = false;
        boolean hayDijkstra = false;
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
                String nuevoMapa = seleccionarYLeerLaberinto(sc);

                if (nuevoMapa != null) {
                    Grafo nuevoGrafo = construirGrafoDesdeMapa(nuevoMapa);
                    if (nuevoGrafo != null && nuevoGrafo.getInicio() != null && nuevoGrafo.getMeta() != null) {
                        mapa = nuevoMapa;
                        grafo = nuevoGrafo;

                        resultadoDFS = null;
                        caminoDFS = null;
                        resultadoBFS = null;
                        caminoBFS = null;
                        caminoCorto = null;

                        hayDFS = false;
                        hayBFS = false;
                        hayDijkstra = false;
                        System.out.println("Se cargo el archivo y se construyo el grafo.\n" + mapa);
                    } else {
                        System.out.println("No se pudo construir el grafo (verifica inicio y meta). Se mantiene el anterior.");
                    }
                } else {
                    System.out.println("Fallo al cargar. Se mantiene el laberinto anterior, si es que ya existia");
                }
            } else if (opcion >= 2 && opcion <= 5) {
                if (mapa == null || grafo == null) {
                    System.out.println("Carga primero un laberinto (opcion 1).");
                } else if (opcion == 2) {
                    System.out.println("Ejecutando DFS...");
                    resultadoDFS = grafo.DFS();
                    caminoDFS = grafo.getUltimoCaminoDFS();
                    hayDFS = grafo.isUltimaSolucionDFS();
                    System.out.println("Visitados DFS: " + resultadoDFS);
                    mostrarRuta("Camino DFS", caminoDFS, hayDFS);
                } else if (opcion == 3) {
                    System.out.println("Ejecutando BFS...");
                    resultadoBFS = grafo.BFS();
                    caminoBFS = grafo.getUltimoCaminoBFS();
                    hayBFS = grafo.isUltimaSolucionBFS();
                    System.out.println("Visitados BFS: " + resultadoBFS);
                    mostrarRuta("Camino BFS", caminoBFS, hayBFS);
                } else if (opcion == 4) {
                    System.out.println("Ejecutando Dijkstra (caminoMasCorto)...");
                    caminoCorto = grafo.caminoMasCorto();
                    hayDijkstra = (caminoCorto != null && !caminoCorto.isEmpty());
                    mostrarRuta("Camino mas corto (Dijkstra)", caminoCorto, hayDijkstra);
                } else {
                    System.out.println("\n=== RESUMEN DE LAS 3 SOLUCIONES ===");
                    if (!hayDFS) {
                        resultadoDFS = grafo.DFS();
                        caminoDFS = grafo.getUltimoCaminoDFS();
                        hayDFS = grafo.isUltimaSolucionDFS();
                    }
                    mostrarRuta("1) DFS", caminoDFS, hayDFS);

                    if (!hayBFS) {
                        resultadoBFS = grafo.BFS();
                        caminoBFS = grafo.getUltimoCaminoBFS();
                        hayBFS = grafo.isUltimaSolucionBFS();
                    }
                    mostrarRuta("2) BFS", caminoBFS, hayBFS);

                    if (!hayDijkstra) {
                        caminoCorto = grafo.caminoMasCorto();
                        hayDijkstra = (caminoCorto != null && !caminoCorto.isEmpty());
                    }
                    mostrarRuta("3) Dijkstra (caminoMasCorto)", caminoCorto, hayDijkstra);
                }
            } else if (opcion != 6) {
                System.out.println("La opcion que ingresaste no es correcta. Intenta de nuevo");
            }
        } while (opcion != 6);

        sc.close();
    }

    private static void mostrarRuta(String titulo, List<Vertice> camino, boolean encontrada) {
        System.out.println("--- " + titulo + " ---");
        if (!encontrada || camino == null || camino.isEmpty()) {
            System.out.println("No se encontro un camino hacia la meta.");
            return;
        }
        System.out.println("Pasos en el camino: " + camino.size());
        System.out.println("Ruta: " + camino);
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

                // Usamos directamente tu constructor: new Vertice(fila, columna, entrada, salida)
                Vertice v = new Vertice(f, c, esEntrada, esSalida);
                matrizVertices[f][c] = v;
                grafo.agregarVertice(v);

                if (esEntrada) {
                    grafo.setInicio(v);
                }
                if (esSalida) {
                    grafo.setMeta(v);
                }
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

    public static String seleccionarYLeerLaberinto(Scanner sc) {
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
            System.out.println("No se encontraron archivos .txt en: " + carpeta.getAbsolutePath());
            return null;
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
            System.out.println("Debes ingresar un numero valido.");
            return null;
        }

        if (eleccion == 0) {
            System.out.println("Carga cancelada.");
            return null;
        }

        if (eleccion < 1 || eleccion > archivosTxt.length) {
            System.out.println("Numero fuera de rango.");
            return null;
        }

        File archivoElegido = archivosTxt[eleccion - 1];
        System.out.println("Cargando: " + archivoElegido.getName());
        return validarYLeerLaberinto(archivoElegido.getPath());
    }

    private static int compararNombres(String nombreA, String nombreB) {
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

    public static String validarYLeerLaberinto(String ruta) {
        try {
            File archivo = new File(ruta);
            if (!archivo.exists() || !archivo.canRead()) {
                System.out.println("No se puede leer el archivo");
                return null;
            }

            List<String> renglones = new ArrayList<>(Files.readAllLines(archivo.toPath(), StandardCharsets.UTF_8));
            if (!renglones.isEmpty() && renglones.get(0).startsWith("\uFEFF")) {
                renglones.set(0, renglones.get(0).substring(1));
            }
            int totalFilas = renglones.size();
            while (totalFilas > 0 && renglones.get(totalFilas - 1).isEmpty()) {
                totalFilas--;
            }

            if (totalFilas == 0) {
                System.out.println("El archivo esta vacio o solo contiene saltos de linea.");
                return null;
            }

            int totalColumnas = renglones.get(0).length();
            if (totalColumnas == 0) {
                System.out.println("Los renglones no pueden tener longitud 0");
                return null;
            }

            int cantidadE = 0;
            int cantidadS = 0;
            int filaEntrada = -1;
            int columnaEntrada = -1;
            int filaSalida = -1;
            int columnaSalida = -1;

            for (int fila = 0; fila < totalFilas; fila++) {
                String lineaActual = renglones.get(fila);

                if (lineaActual.length() != totalColumnas) {
                    System.out.println("El renglon " + (fila + 1) + " tiene distinta longitud.");
                    return null;
                }

                for (int col = 0; col < totalColumnas; col++) {
                    char caracter = lineaActual.charAt(col);
                    if (caracter != '+' && caracter != '-' && caracter != '|' && caracter != ' '
                            && caracter != '#' && caracter != 'E' && caracter != 'S') {
                        System.out.println("Caracter invalido '" + caracter + "' en fila " + (fila + 1)
                                + ", columna " + (col + 1) + ".");
                        return null;
                    }
                    if (caracter == 'E') {
                        cantidadE++;
                        filaEntrada = fila;
                        columnaEntrada = col;
                    }
                    if (caracter == 'S') {
                        cantidadS++;
                        filaSalida = fila;
                        columnaSalida = col;
                    }
                }
            }

            if (cantidadE > 0 || cantidadS > 0) {
                if (cantidadE != 1 || cantidadS != 1) {
                    System.out.println("Debe haber exactamente una E y una S");
                    return null;
                }
                System.out.println("Entrada: (" + filaEntrada + "," + columnaEntrada + ") | Salida: (" + filaSalida + "," + columnaSalida + ")");
            } else {
                int transitablesEnBorde = 0;
                int[] filaBorde = new int[2];
                int[] columnaBorde = new int[2];

                for (int fila = 0; fila < totalFilas; fila++) {
                    String renglon = renglones.get(fila);
                    for (int col = 0; col < totalColumnas; col++) {
                        boolean estaEnBorde = (fila == 0 || fila == totalFilas - 1
                                || col == 0 || col == totalColumnas - 1);
                        char caracter = renglon.charAt(col);
                        boolean esTransitable = (caracter == ' ' || caracter == '#'
                                || caracter == 'E' || caracter == 'S');

                        if (estaEnBorde && esTransitable) {
                            if (transitablesEnBorde < 2) {
                                filaBorde[transitablesEnBorde] = fila;
                                columnaBorde[transitablesEnBorde] = col;
                            }
                            transitablesEnBorde++;
                        }
                    }
                }

                if (transitablesEnBorde != 2) {
                    System.out.println("Sin E/S debe haber exactamente 2 espacios transitables en el borde.");
                    return null;
                }
                System.out.println("Entrada borde: (" + filaBorde[0] + "," + columnaBorde[0]
                        + ") | Salida borde: (" + filaBorde[1] + "," + columnaBorde[1] + ")");
            }

            return String.join("\n", renglones.subList(0, totalFilas)) + "\n";
        } catch (Exception e) {
            System.out.println("Error al cargar: " + e.getMessage());
            return null;
        }
    }
}
