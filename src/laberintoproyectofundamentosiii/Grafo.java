package laberintoproyectofundamentosiii;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Grafo no dirigido: cada conexión se guarda como dos arcos (ida y vuelta) de costo 1.
 * <ul>
 *   <li>Adyacencia: {@code Hashtable<Integer, LinkedList<Arcos>>} cuyas listas son ligadas
 *       ({@link LinkedList}); la clave es el id del vertice (fila * columnas + columna).</li>
 *   <li>Orden de exploracion: los arcos se crean en el orden arriba, derecha, abajo,
 *       izquierda. BFS los recorre en ese orden; DFS los apila en orden inverso para
 *       explorar primero el primero de la lista.</li>
 *   <li>Desempate de Dijkstra: la cola de prioridad ordena por (distancia, id menor) y se
 *       descartan las entradas obsoletas.</li>
 * </ul>
 */
public class Grafo {

    private int columnas;
    private Vertices vertices;
    private Hashtable<Integer, LinkedList<Arcos>> adyacencia;
    private Vertice inicio;
    private Vertice meta;

    // --- Estado auxiliar de DFS ---
    private List<Vertice> ultimoCaminoDFS;

    // --- Estado auxiliar de BFS ---
    private List<Vertice> ultimoCaminoBFS;

    // --- Estado auxiliar de Dijkstra ---
    private List<Vertice> ultimosVisitadosDijkstra;

    public Grafo(int columnas) {
        this.columnas = columnas;
        this.vertices = new Vertices();
        this.adyacencia = new Hashtable<>();
        this.ultimoCaminoDFS = new ArrayList<Vertice>();
        this.ultimoCaminoBFS = new ArrayList<Vertice>();
        this.ultimosVisitadosDijkstra = new ArrayList<Vertice>();
    }

    // Asigna el id unico del vertice: fila * columnas + columna
    private int asignarId(Vertice vertice) {
        vertice.setId(vertice.getFila() * columnas + vertice.getColumna());
        return vertice.getId();
    }

    public void agregarVertice(Vertice vertice) {
        if (vertice == null) {
            return;
        }
        int id = asignarId(vertice);
        vertices.agregar(vertice);
        adyacencia.computeIfAbsent(id, k -> new LinkedList<>());
        if (vertice.esEntrada() && this.inicio == null) {
            this.inicio = vertice;
        }
        if (vertice.esSalida() && this.meta == null) {
            this.meta = vertice;
        }
    }

    public void agregarArco(Vertice origen, Vertice destino, int peso) {
        if (origen == null || destino == null) {
            return;
        }
        if (!vertices.existe(asignarId(origen))) {
            agregarVertice(origen);
        }
        if (!vertices.existe(asignarId(destino))) {
            agregarVertice(destino);
        }
        adyacencia.get(origen.getId()).add(new Arcos(origen, destino, peso));
    }

    public List<Arcos> obtenerArcosDe(Vertice vertice) {
        if (vertice == null) {
            return Collections.emptyList();
        }
        List<Arcos> lista = adyacencia.get(vertice.getId());
        return (lista != null) ? lista : Collections.<Arcos>emptyList();
    }

    public Vertice getInicio() {
        return inicio;
    }

    public Vertice getMeta() {
        return meta;
    }


    // Inicio del DFS

    public List<Vertice> DFS() {
        return DFS(this.inicio, this.meta);
    }

    public List<Vertice> DFS(Vertice origen, Vertice destino) {
        List<Vertice> visitados = new ArrayList<Vertice>();
        this.ultimoCaminoDFS = new ArrayList<Vertice>();
        if (origen == null || destino == null || !vertices.existe(origen.getId()) || !vertices.existe(destino.getId())) {
            return visitados;
        }
        HashSet<Integer> visitadoMapa = new HashSet<>();
        HashMap<Integer, Integer> predecesores = new HashMap<>();

        int idDestino = destino.getId();

        // Pila (LIFO) de pares {vertice, padre que lo apilo}
        Deque<Vertice[]> pilaDFS = new ArrayDeque<Vertice[]>();
        pilaDFS.push(new Vertice[] {origen, null});

        while (!pilaDFS.isEmpty()) {
            Vertice[] entrada = pilaDFS.pop();
            Vertice verticeActual = entrada[0];
            int idActual = verticeActual.getId();
            if (!visitadoMapa.add(idActual)) {
                continue;
            }
            visitados.add(verticeActual);
            if (entrada[1] != null) {
                predecesores.put(idActual, entrada[1].getId());
            }

            if (idActual == idDestino) {
                this.ultimoCaminoDFS = reconstruirCamino(origen, destino, predecesores);
                return visitados;
            }

            Iterator<Arcos> inverso = adyacencia.get(idActual).descendingIterator();
            while (inverso.hasNext()) {
                Arcos arco = inverso.next();
                Vertice vecino = arco.getDestino();
                if (!visitadoMapa.contains(vecino.getId())) {
                    pilaDFS.push(new Vertice[] {vecino, verticeActual});
                }
            }
        }

        return visitados;
    }

    public List<Vertice> getUltimoCaminoDFS() {
        return ultimoCaminoDFS;
    }

    // Fin del DFS

    // Inicio del BFS

    public List<Vertice> BFS() {
        return BFS(this.inicio, this.meta);
    }

    public List<Vertice> BFS(Vertice origen, Vertice destino) {
        List<Vertice> visitados = new ArrayList<Vertice>();
        this.ultimoCaminoBFS = new ArrayList<Vertice>();
        if (origen == null || destino == null || !vertices.existe(origen.getId()) || !vertices.existe(destino.getId())) {
            return visitados;
        }
        HashSet<Integer> visitadoMapa = new HashSet<>();
        HashMap<Integer, Integer> predecesores = new HashMap<>();

        int idOrigen = origen.getId();
        int idDestino = destino.getId();


        // Cola (FIFO)
        Deque<Vertice> colaBFS = new ArrayDeque<Vertice>();
        visitadoMapa.add(idOrigen);
        colaBFS.add(origen);

        while (!colaBFS.isEmpty()) {
            Vertice verticeActual = colaBFS.poll();
            int idActual = verticeActual.getId();
            visitados.add(verticeActual);

            if (idActual == idDestino) {
                this.ultimoCaminoBFS = reconstruirCamino(origen, destino, predecesores);
                return visitados;
            }

            List<Arcos> arcosSalientes = obtenerArcosDe(verticeActual);
            for (Arcos arco : arcosSalientes) {
                Vertice vecino = arco.getDestino();
                int idVecino = vecino.getId();
                if (visitadoMapa.add(idVecino)) {
                    predecesores.put(idVecino, idActual);
                    colaBFS.add(vecino);
                }
            }
        }

        return visitados;
    }

    public List<Vertice> getUltimoCaminoBFS() {
        return ultimoCaminoBFS;
    }

    // Fin del BFS

    // Inicio del camino mas corto

    public List<Vertice> caminoMasCorto() {
        return caminoMasCorto(this.inicio, this.meta);
    }

    public List<Vertice> caminoMasCorto(Vertice origen, Vertice destino) {
        this.ultimosVisitadosDijkstra = new ArrayList<Vertice>();

        if (origen == null || destino == null
                || !vertices.existe(origen.getId()) || !vertices.existe(destino.getId())) {
            return new ArrayList<Vertice>();
        }

        Hashtable<Integer, Integer> distancias = new Hashtable<Integer, Integer>();
        HashMap<Integer, Integer> predecesores = new HashMap<>();

        // Entradas {distancia, id}: sale primero la menor distancia y, en empate, el menor id
        PriorityQueue<int[]> cola = new PriorityQueue<int[]>(
                (a, b) -> a[0] != b[0] ? Integer.compare(a[0], b[0]) : Integer.compare(a[1], b[1]));
        distancias.put(origen.getId(), 0);
        cola.add(new int[] {0, origen.getId()});

        while (!cola.isEmpty()) {
            int[] entrada = cola.poll();
            if (entrada[0] > distancias.get(entrada[1])) {
                continue; // entrada obsoleta
            }

            Vertice actual = vertices.obtener(entrada[1]);
            ultimosVisitadosDijkstra.add(actual);
            if (actual.getId() == destino.getId()) {
                return reconstruirCamino(origen, destino, predecesores);
            }

            for (Arcos arco : obtenerArcosDe(actual)) {
                int idVecino = arco.getDestino().getId();
                int nuevaDistancia = entrada[0] + arco.getPeso();
                if (arco.getPeso() >= 0 && nuevaDistancia < distancias.getOrDefault(idVecino, Integer.MAX_VALUE)) {
                    distancias.put(idVecino, nuevaDistancia);
                    predecesores.put(idVecino, actual.getId());
                    cola.add(new int[] {nuevaDistancia, idVecino});
                }
            }
        }

        return new ArrayList<Vertice>();
    }

    public List<Vertice> getUltimosVisitadosDijkstra() {
        return ultimosVisitadosDijkstra;
    }

    private List<Vertice> reconstruirCamino(Vertice origen, Vertice destino,
            HashMap<Integer, Integer> predecesores) {
        List<Vertice> camino = new ArrayList<Vertice>();
        Vertice paso = destino;

        while (paso != null) {
            camino.add(paso);
            int idPaso = paso.getId();
            if (idPaso == origen.getId()) {
                break;
            }
            Integer padre = predecesores.get(idPaso);
            paso = padre == null ? null : vertices.obtener(padre);
        }

        if (camino.isEmpty() || camino.get(camino.size() - 1).getId() != origen.getId()) {
            return new ArrayList<Vertice>();
        }

        Collections.reverse(camino);
        return camino;
    }

    // Fin del camino mas corto

}

