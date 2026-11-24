package laberintoproyectofundamentosiii;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Grafo dirigido de un laberinto.
 * <ul>
 *   <li>Adyacencia: {@code Hashtable<Integer, List<Arcos>>} cuyas listas son ligadas
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
    private Hashtable<Integer, List<Arcos>> adyacencia;
    private Vertice inicio;
    private Vertice meta;

    // --- Estado auxiliar de DFS ---
    private List<Vertice> ultimoCaminoDFS;
    private boolean ultimaSolucionDFS;

    // --- Estado auxiliar de BFS ---
    private List<Vertice> ultimoCaminoBFS;
    private boolean ultimaSolucionBFS;

    // --- Estado auxiliar de Dijkstra ---
    private List<Vertice> ultimosVisitadosDijkstra;

    public Grafo(int columnas) {
        this.columnas = columnas;
        this.vertices = new Vertices(columnas);
        this.adyacencia = new Hashtable<Integer, List<Arcos>>();
        this.ultimoCaminoDFS = new ArrayList<Vertice>();
        this.ultimaSolucionDFS = false;
        this.ultimoCaminoBFS = new ArrayList<Vertice>();
        this.ultimaSolucionBFS = false;
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
        if (!adyacencia.containsKey(id)) {
            adyacencia.put(id, new LinkedList<Arcos>());
        }
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

    public Vertices getVertices() {
        return vertices;
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


    // Inicio del DFS

    public List<Vertice> DFS() {
        return DFS(this.inicio, this.meta);
    }

    public List<Vertice> DFS(Vertice origen, Vertice destino) {
        List<Vertice> visitados = new ArrayList<Vertice>();
        this.ultimoCaminoDFS = new ArrayList<Vertice>();
        this.ultimaSolucionDFS = false;

        if (this.vertices == null || origen == null || destino == null) {
            return visitados;
        }

        Collection<Vertice> todosLosVertices = this.vertices.todos();
        if (todosLosVertices == null) {
            return visitados;
        }

        Hashtable<Integer, Boolean> visitadoMapa = new Hashtable<Integer, Boolean>();
        Hashtable<Integer, Vertice> predecesores = new Hashtable<Integer, Vertice>();

        for (Vertice actual : todosLosVertices) {
            if (actual != null) {
                visitadoMapa.put(actual.getId(), false);
            }
        }

        int idOrigen = origen.getId();
        int idDestino = destino.getId();

        if (!visitadoMapa.containsKey(idOrigen) || !visitadoMapa.containsKey(idDestino)) {
            return visitados;
        }


        // Pila (LIFO) de pares {vertice, padre que lo apilo}
        Deque<Vertice[]> pilaDFS = new ArrayDeque<Vertice[]>();
        pilaDFS.push(new Vertice[] {origen, null});

        while (!pilaDFS.isEmpty()) {
            Vertice[] entrada = pilaDFS.pop();
            Vertice verticeActual = entrada[0];
            if (verticeActual == null) {
                continue;
            }

            int idActual = verticeActual.getId();
            Boolean yaVisitado = visitadoMapa.get(idActual);
            if (yaVisitado != null && yaVisitado) {
                continue;
            }

            visitadoMapa.put(idActual, true);
            visitados.add(verticeActual);
            if (entrada[1] != null) {
                predecesores.put(idActual, entrada[1]);
            }

            if (idActual == idDestino) {
                this.ultimaSolucionDFS = true;
                this.ultimoCaminoDFS = reconstruirCamino(origen, destino, predecesores);
                return visitados;
            }

            List<Arcos> arcosSalientes = obtenerArcosDe(verticeActual);
            if (arcosSalientes == null || arcosSalientes.isEmpty()) {
                continue;
            }

            for (int i = arcosSalientes.size() - 1; i >= 0; i--) {
                Arcos arco = arcosSalientes.get(i);
                if (arco == null) {
                    continue;
                }

                Vertice vecino = arco.getDestino();
                if (vecino == null) {
                    continue;
                }

                int idVecino = vecino.getId();
                if (!visitadoMapa.containsKey(idVecino)) {
                    continue;
                }

                Boolean vecinoVisitado = visitadoMapa.get(idVecino);
                if (vecinoVisitado == null || !vecinoVisitado) {
                    pilaDFS.push(new Vertice[] {vecino, verticeActual});
                }
            }
        }

        return visitados;
    }

    public List<Vertice> getUltimoCaminoDFS() {
        return ultimoCaminoDFS;
    }

    public boolean isUltimaSolucionDFS() {
        return ultimaSolucionDFS;
    }

    // Fin del DFS

    // Inicio del BFS

    public List<Vertice> BFS() {
        return BFS(this.inicio, this.meta);
    }

    public List<Vertice> BFS(Vertice origen, Vertice destino) {
        List<Vertice> visitados = new ArrayList<Vertice>();
        this.ultimoCaminoBFS = new ArrayList<Vertice>();
        this.ultimaSolucionBFS = false;

        if (this.vertices == null || origen == null || destino == null) {
            return visitados;
        }

        Collection<Vertice> todosLosVertices = this.vertices.todos();
        if (todosLosVertices == null) {
            return visitados;
        }

        Hashtable<Integer, Boolean> visitadoMapa = new Hashtable<Integer, Boolean>();
        Hashtable<Integer, Vertice> predecesores = new Hashtable<Integer, Vertice>();

        for (Vertice actual : todosLosVertices) {
            if (actual != null) {
                visitadoMapa.put(actual.getId(), false);
            }
        }

        int idOrigen = origen.getId();
        int idDestino = destino.getId();

        if (!visitadoMapa.containsKey(idOrigen) || !visitadoMapa.containsKey(idDestino)) {
            return visitados;
        }


        // Cola (FIFO)
        Deque<Vertice> colaBFS = new ArrayDeque<Vertice>();
        visitadoMapa.put(idOrigen, true);
        colaBFS.add(origen);

        while (!colaBFS.isEmpty()) {
            Vertice verticeActual = colaBFS.poll();
            if (verticeActual == null) {
                continue;
            }

            int idActual = verticeActual.getId();
            visitados.add(verticeActual);

            if (idActual == idDestino) {
                this.ultimaSolucionBFS = true;
                this.ultimoCaminoBFS = reconstruirCamino(origen, destino, predecesores);
                return visitados;
            }

            List<Arcos> arcosSalientes = obtenerArcosDe(verticeActual);
            if (arcosSalientes == null || arcosSalientes.isEmpty()) {
                continue;
            }

            for (int i = 0; i < arcosSalientes.size(); i++) {
                Arcos arco = arcosSalientes.get(i);
                if (arco == null) {
                    continue;
                }

                Vertice vecino = arco.getDestino();
                if (vecino == null) {
                    continue;
                }

                int idVecino = vecino.getId();
                if (!visitadoMapa.containsKey(idVecino)) {
                    continue;
                }

                Boolean yaVisitado = visitadoMapa.get(idVecino);
                if (yaVisitado == null || !yaVisitado) {
                    visitadoMapa.put(idVecino, true);
                    if (!predecesores.containsKey(idVecino)) {
                        predecesores.put(idVecino, verticeActual);
                    }
                    colaBFS.add(vecino);
                }
            }
        }

        return visitados;
    }

    public List<Vertice> getUltimoCaminoBFS() {
        return ultimoCaminoBFS;
    }

    public boolean isUltimaSolucionBFS() {
        return ultimaSolucionBFS;
    }

    // Fin del BFS

    // Inicio del camino mas corto

    public List<Vertice> caminoMasCorto() {
        return caminoMasCorto(this.inicio, this.meta);
    }

    public List<Vertice> caminoMasCorto(Vertice origen, Vertice destino) {
        this.ultimosVisitadosDijkstra = new ArrayList<Vertice>();

        if (this.vertices == null || origen == null || destino == null
                || !vertices.existe(origen.getId()) || !vertices.existe(destino.getId())) {
            return new ArrayList<Vertice>();
        }

        Hashtable<Integer, Integer> distancias = new Hashtable<Integer, Integer>();
        Hashtable<Integer, Vertice> predecesores = new Hashtable<Integer, Vertice>();

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
                    predecesores.put(idVecino, actual);
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
            Hashtable<Integer, Vertice> predecesores) {
        List<Vertice> camino = new ArrayList<Vertice>();
        Vertice paso = destino;

        while (paso != null) {
            camino.add(paso);
            int idPaso = paso.getId();
            if (idPaso == origen.getId()) {
                break;
            }
            paso = predecesores.get(idPaso);
        }

        if (camino.isEmpty() || camino.get(camino.size() - 1).getId() != origen.getId()) {
            return new ArrayList<Vertice>();
        }

        Collections.reverse(camino);
        return camino;
    }

    // Fin del camino mas corto

}

