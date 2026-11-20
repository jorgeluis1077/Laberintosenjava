package laberintoproyectofundamentosiii;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class Grafo {

    private Vertices vertices;
    private Hashtable<String, List<Arcos>> adyacencia;
    private Vertice inicio;
    private Vertice meta;

    public Grafo() {
        this.vertices = new Vertices();
        this.adyacencia = new Hashtable<String, List<Arcos>>();
    }

    public void agregarVertice(Vertice vertice) {
        if (vertice != null && !vertices.existe(vertice.getClave())) {
            vertices.agregar(vertice);
            adyacencia.put(vertice.getClave(), new ArrayList<Arcos>());
            if (vertice.esEntrada()) {
                this.inicio = vertice;
            }
            if (vertice.esSalida()) {
                this.meta = vertice;
            }
        }
    }

    public void agregarArco(Vertice origen, Vertice destino, int peso) {
        if (origen == null || destino == null) {
            return;
        }
        List<Arcos> lista = adyacencia.get(origen.getClave());
        if (lista != null) {
            lista.add(new Arcos(origen, destino, peso));
        }
    }

    public Vertices getVertices() {
        return vertices;
    }

    public List<Arcos> obtenerArcosDe(Vertice vertice) {
        if (vertice == null) {
            return new ArrayList<Arcos>();
        }
        List<Arcos> lista = adyacencia.get(vertice.getClave());
        if (lista == null) {
            return new ArrayList<Arcos>();
        }
        return lista;
    }

    public Vertice getInicio() {
        return inicio;
    }

    public void setInicio(Vertice inicio) {
        this.inicio = inicio;
    }

    public Vertice getMeta() {
        return meta;
    }

    public void setMeta(Vertice meta) {
        this.meta = meta;
    }

    public static Grafo construirDesdeMapa(String mapaTexto, int filaEntrada, int columnaEntrada,
                                           int filaSalida, int columnaSalida) {
        Grafo grafo = new Grafo();
        String[] filas = mapaTexto.split("\n");
        int totalFilas = filas.length;
        int totalColumnas = filas[0].length();

        for (int r = 0; r < totalFilas; r++) {
            for (int c = 0; c < totalColumnas; c++) {
                char caracter = filas[r].charAt(c);
                if (esTransitable(caracter)) {
                    boolean esEntrada = (r == filaEntrada && c == columnaEntrada);
                    boolean esSalida = (r == filaSalida && c == columnaSalida);
                    Vertice v = new Vertice(r, c, esEntrada, esSalida);
                    grafo.agregarVertice(v);
                }
            }
        }

        int[] deltaFila = {-1, 1, 0, 0};
        int[] deltaCol = {0, 0, -1, 1};

        for (Vertice actual : grafo.getVertices().todos()) {
            for (int i = 0; i < 4; i++) {
                int vecinaFila = actual.getFila() + deltaFila[i];
                int vecinaCol = actual.getColumna() + deltaCol[i];
                Vertice vecina = grafo.getVertices().obtenerPorCoordenada(vecinaFila, vecinaCol);
                if (vecina != null) {
                    grafo.agregarArco(actual, vecina, 1);
                }
            }
        }

        if (grafo.getInicio() == null && filaEntrada >= 0 && columnaEntrada >= 0) {
            grafo.setInicio(grafo.getVertices().obtenerPorCoordenada(filaEntrada, columnaEntrada));
        }
        if (grafo.getMeta() == null && filaSalida >= 0 && columnaSalida >= 0) {
            grafo.setMeta(grafo.getVertices().obtenerPorCoordenada(filaSalida, columnaSalida));
        }

        return grafo;
    }

    private static boolean esTransitable(char caracter) {
        return caracter == ' ' || caracter == '#' || caracter == 'E' || caracter == 'S';
    }
}
