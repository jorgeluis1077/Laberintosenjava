package frontend.consola;

import frontend.modelo.DatosLaberinto;
import frontend.modelo.ErrorCargaException;
import frontend.modelo.ResultadoVista;
import frontend.modelo.ServicioDemo;
import frontend.modelo.ServicioLaberinto;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

/**
 * SOLO PARA DEMOSTRACION: vista previa del tema de consola con ServicioDemo.
 * No imita el menu de la base.
 * Uso: java -cp out frontend.consola.DemoConsola [--unicode] [archivo.txt]
 */
public final class DemoConsola {

    private DemoConsola() {
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        boolean unicode = false;
        Path archivo = null;
        for (String a : args) {
            if (a.equals("--unicode")) {
                unicode = true;
            } else {
                archivo = Path.of(a);
            }
        }
        if (!unicode) {
            System.out.println("Estilo de símbolos: 1) ASCII  2) Unicode (ENTER = ASCII)");
            unicode = leer(in).trim().equals("2");
        }
        TemaConsola tema = SalidaConsola.configurar(unicode ? TemaConsola.UNICODE : TemaConsola.ASCII);

        ServicioLaberinto servicio = new ServicioDemo();
        if (archivo == null) {
            archivo = elegirEjemplo(servicio, in);
        }
        DatosLaberinto datos;
        try {
            datos = servicio.cargar(archivo); // null = laberinto embebido
        } catch (ErrorCargaException e) {
            System.out.println("No se pudo cargar: " + e.getMessage());
            return;
        }
        System.out.println("Cargado: " + datos.nombreArchivo() + " (" + datos.filas() + "x" + datos.columnas() + ")");

        System.out.println("Algoritmo: 1) DFS  2) BFS  3) Dijkstra (ENTER = BFS)");
        String op = leer(in).trim();
        ResultadoVista r = op.equals("1") ? servicio.resolverDFS()
                : op.equals("3") ? servicio.resolverDijkstra() : servicio.resolverBFS();

        explorar(datos, r, tema, in);
        mostrarFinal(datos, r, tema);
    }

    private static Path elegirEjemplo(ServicioLaberinto servicio, Scanner in) {
        List<Path> lista = servicio.listarEjemplos();
        if (lista.isEmpty()) {
            System.out.println("No se encontró la carpeta de ejemplos; se usa un laberinto embebido.");
            return null;
        }
        for (int i = 0; i < lista.size(); i++) {
            System.out.println("  " + (i + 1) + ") " + lista.get(i).getFileName());
        }
        System.out.println("Laberinto (ENTER = 1):");
        int n = 1;
        try {
            String s = leer(in).trim();
            if (!s.isEmpty()) n = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            n = 1;
        }
        return lista.get(Math.max(1, Math.min(n, lista.size())) - 1);
    }

    /** Muestra la exploracion paso a paso: ENTER avanza, "f" salta al final. */
    private static void explorar(DatosLaberinto datos, ResultadoVista r, TemaConsola tema, Scanner in) {
        int total = r.visitados().size();
        System.out.println("ENTER = siguiente paso, f = saltar al final");
        for (int paso = 1; paso < total; paso++) {
            System.out.println();
            System.out.println(r.algoritmo() + " - paso " + paso + " de " + total);
            System.out.print(RenderConsola.vista(datos, r, paso, false, tema));
            System.out.println("[ENTER / f]");
            if (leer(in).trim().equalsIgnoreCase("f")) {
                break;
            }
        }
    }

    private static void mostrarFinal(DatosLaberinto datos, ResultadoVista r, TemaConsola tema) {
        System.out.println();
        System.out.println("Resultado final: " + r.algoritmo());
        System.out.print(RenderConsola.vista(datos, r, r.visitados().size(), r.encontrado(), tema));
        System.out.print(RenderConsola.leyenda(tema));
        System.out.println("Visitados: " + r.visitados().size());
        if (r.encontrado()) {
            System.out.println("Longitud: " + r.longitud() + " movimientos");
        } else {
            System.out.println("No existe camino entre la entrada y la salida");
        }
        System.out.println("Tiempo: " + r.tiempoNanos() + " ns");
    }

    /** Lee una linea; al acabar la entrada devuelve "f" para no quedarse esperando. */
    private static String leer(Scanner in) {
        return in.hasNextLine() ? in.nextLine() : "f";
    }
}
