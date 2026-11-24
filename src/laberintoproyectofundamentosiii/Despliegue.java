package laberintoproyectofundamentosiii;

import frontend.consola.RenderConsola;
import frontend.consola.TemaConsola;
import frontend.modelo.Celda;
import frontend.modelo.DatosLaberinto;
import frontend.modelo.ResultadoVista;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public final class Despliegue {
    private static final String LIMPIAR_PANTALLA = "\033[2J\033[H";
    private static final int PAUSA_MAXIMA_MS = 5000;
    private static final int SALTAR_AL_FINAL = -1;
    private static final int PASO_MANUAL = -2;

    private final DatosLaberinto datos;
    private final Scanner entrada;
    private final TemaConsola tema;
    private final String algoritmo;
    private final List<Vertice> camino;
    private final long tiempoNanos;

    public Despliegue(DatosLaberinto datos, Scanner entrada, TemaConsola tema,
                      String algoritmo, List<Vertice> camino, long tiempoNanos) {
        this.datos = datos;
        this.entrada = entrada;
        this.tema = tema;
        this.algoritmo = algoritmo;
        this.camino = camino;
        this.tiempoNanos = tiempoNanos;
    }

    public static DatosLaberinto datosDelMapa(String mapa, Grafo grafo) {
        String[] filas = mapa.split("\n");
        char[][] celdas = new char[filas.length][];
        for (int i = 0; i < filas.length; i++) {
            celdas[i] = filas[i].toCharArray();
        }
        Vertice inicio = grafo.getInicio();
        Vertice meta = grafo.getMeta();
        return new DatosLaberinto("", celdas, inicio.getFila(), inicio.getColumna(),
                meta.getFila(), meta.getColumna());
    }

    public void despliegaLaberinto(List<Vertice> ordenVisita) {
        ResultadoVista resultado = convertirResultado(ordenVisita);
        int total = resultado.visitados().size();
        if (total > 0) {
            int pausa = elegirPausa();
            for (int paso = 1; pausa != SALTAR_AL_FINAL && paso <= total; paso++) {
                System.out.print(LIMPIAR_PANTALLA);
                System.out.printf("%s - paso %d de %d%n", algoritmo, paso, total);
                System.out.print(RenderConsola.vista(datos, resultado, paso, false, true, tema));
                if (!avanzar(pausa)) {
                    break;
                }
            }
        }

        System.out.print(LIMPIAR_PANTALLA);
        System.out.println("Resultado final: " + algoritmo);
        mostrarResultado(resultado, true);
    }

    void mostrarSolucion(List<Vertice> ordenVisita) {
        System.out.println("\n--- " + algoritmo + " ---");
        mostrarResultado(convertirResultado(ordenVisita), false);
    }

    private void mostrarResultado(ResultadoVista resultado, boolean incluirLeyenda) {
        int visitados = resultado.visitados().size();
        System.out.print(RenderConsola.vista(datos, resultado, visitados, resultado.encontrado(), tema));
        if (incluirLeyenda) {
            System.out.print(RenderConsola.leyenda(tema));
        }
        System.out.println("Visitados: " + visitados);
        if (resultado.encontrado()) {
            int longitud = resultado.longitud();
            System.out.println("Longitud: " + longitud + (longitud == 1 ? " movimiento" : " movimientos"));
        } else {
            System.out.println("No existe camino entre la entrada y la salida");
        }
        System.out.printf(Locale.ROOT, "Tiempo: %d ns (%.6f ms)%n",
                resultado.tiempoNanos(), resultado.tiempoNanos() / 1_000_000.0);
    }

    private ResultadoVista convertirResultado(List<Vertice> visitados) {
        return resultadoVista(algoritmo, visitados, camino, tiempoNanos);
    }

    /** Unica conversion de vertices de la base a celdas del frontend (consola y GUI). */
    public static ResultadoVista resultadoVista(String algoritmo, List<Vertice> visitados,
                                                List<Vertice> camino, long tiempoNanos) {
        return new ResultadoVista(algoritmo, aCeldas(visitados), aCeldas(camino), !camino.isEmpty(), tiempoNanos);
    }

    private static List<Celda> aCeldas(List<Vertice> vertices) {
        return vertices.stream().map(v -> new Celda(v.getFila(), v.getColumna())).toList();
    }

    private int elegirPausa() {
        while (true) {
            System.out.println("ENTER = modo paso a paso, 0 a 5000 = pausa automática en ms, f = saltar al final");
            String opcion = leerComando();
            if (opcion.equalsIgnoreCase("f")) {
                return SALTAR_AL_FINAL;
            }
            if (opcion.isEmpty()) {
                return PASO_MANUAL;
            }
            try {
                int pausa = Integer.parseInt(opcion);
                if (pausa >= 0 && pausa <= PAUSA_MAXIMA_MS) {
                    return pausa;
                }
            } catch (NumberFormatException e) {
                // Se vuelve a pedir una opcion valida.
            }
            System.out.println("Elige un valor de 0 a 5000, ENTER o f.");
        }
    }

    private boolean avanzar(int pausa) {
        if (pausa == PASO_MANUAL) {
            System.out.println("ENTER = siguiente paso, f = saltar al final");
            return !leerComando().equalsIgnoreCase("f");
        }
        if (pausa == 0) {
            return true;
        }
        System.out.flush();
        try {
            Thread.sleep(pausa);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    private String leerComando() {
        return entrada.hasNextLine() ? entrada.nextLine().strip() : "f";
    }
}
