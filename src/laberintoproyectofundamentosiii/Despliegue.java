package laberintoproyectofundamentosiii;

import frontend.consola.RenderConsola;
import frontend.consola.TemaConsola;
import frontend.modelo.Celda;
import frontend.modelo.DatosLaberinto;
import frontend.modelo.ResultadoVista;
import java.util.List;
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

    public Despliegue(String mapa, Grafo grafo, Scanner entrada, TemaConsola tema,
                      String algoritmo, List<Vertice> camino) {
        String[] filas = mapa.split("\n");
        char[][] celdas = new char[filas.length][];
        for (int i = 0; i < filas.length; i++) {
            celdas[i] = filas[i].toCharArray();
        }
        Vertice inicio = grafo.getInicio();
        Vertice meta = grafo.getMeta();
        datos = new DatosLaberinto("", celdas, inicio.getFila(), inicio.getColumna(),
                meta.getFila(), meta.getColumna());
        this.entrada = entrada;
        this.tema = tema;
        this.algoritmo = algoritmo;
        this.camino = camino;
    }

    public void despliegaLaberinto(List<Vertice> ordenVisita) {
        ResultadoVista resultado = new ResultadoVista(algoritmo, aCeldas(ordenVisita),
                aCeldas(camino), !camino.isEmpty(), 0);
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
        System.out.print(RenderConsola.vista(datos, resultado, total, resultado.encontrado(), tema));
        System.out.print(RenderConsola.leyenda(tema));
        if (!resultado.encontrado()) {
            System.out.println("No existe camino entre la entrada y la salida");
        }
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
