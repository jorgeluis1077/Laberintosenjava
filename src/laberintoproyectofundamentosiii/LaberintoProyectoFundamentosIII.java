package laberintoproyectofundamentosiii;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class LaberintoProyectoFundamentosIII {

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

            try {
                opcion = Integer.parseInt(sc.nextLine().trim());
            } catch (Exception e) {
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
                    mostrarRuta("Camino DFS", caminoDFS, hayDFS);
                } else if (opcion == 3) {
                    System.out.println("Ejecutando BFS...");
                    resultadoBFS = grafo.BFS();
                    caminoBFS = grafo.getUltimoCaminoBFS();
                    hayBFS = grafo.isUltimaSolucionBFS();
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
        File carpeta = new File(".");

        File[] archivosTxt = carpeta.listFiles((dir, nombre) ->
                nombre.toLowerCase().endsWith(".txt") && new File(dir, nombre).isFile()
        );

        if (archivosTxt == null || archivosTxt.length == 0) {
            System.out.println("No se encontraron archivos .txt en: " + carpeta.getAbsolutePath());
            return null;
        }

        Arrays.sort(archivosTxt, (a, b) -> {
            int numeroA = extraerNumero(a.getName());
            int numeroB = extraerNumero(b.getName());
            if (numeroA != numeroB) {
                return Integer.compare(numeroA, numeroB);
            }
            return a.getName().compareToIgnoreCase(b.getName());
        });

        System.out.println("\nArchivos .txt disponibles:");
        for (int i = 0; i < archivosTxt.length; i++) {
            System.out.println((i + 1) + ". " + archivosTxt[i].getName());
        }

        System.out.print("Elige el numero del archivo (0 para cancelar): ");
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

    private static int extraerNumero(String nombreArchivo) {
        String soloDigitos = nombreArchivo.replaceAll("\\D+", "");
        if (soloDigitos.isEmpty()) {
            return Integer.MAX_VALUE;
        }
        try {
            return Integer.parseInt(soloDigitos);
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE;
        }
    }

    public static String validarYLeerLaberinto(String ruta) {
        try {
            File archivo = new File(ruta);
            if (!archivo.exists() || !archivo.canRead()) {
                System.out.println("No se puede leer el archivo");
                return null;
            }

            Scanner lector = new Scanner(archivo);
            String texto = "";
            int totalFilas = 0;
            while (lector.hasNextLine()) {
                texto += lector.nextLine() + "\n";
                totalFilas++;
            }
            lector.close();

            if (totalFilas == 0) {
                System.out.println("El archivo esta vacio");
                return null;
            }

            String[] renglones = texto.split("\n");
            int totalColumnas = renglones[0].length();
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
                String lineaActual = renglones[fila].replace("\r", "");
                renglones[fila] = lineaActual;

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
                    for (int col = 0; col < totalColumnas; col++) {
                        boolean estaEnBorde = (fila == 0 || fila == totalFilas - 1
                                || col == 0 || col == totalColumnas - 1);
                        char caracter = renglones[fila].charAt(col);
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

            return texto;
        } catch (Exception e) {
            System.out.println("Error al cargar: " + e.getMessage());
            return null;
        }
    }
}
