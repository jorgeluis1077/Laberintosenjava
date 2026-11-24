package laberintoproyectofundamentosiii;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

public class Grafo {

    private Vertices vertices;
    private Hashtable<String, List<Arcos>> adyacencia;
    private Vertice inicio;
    private Vertice meta;

    // --- Estado auxiliar de DFS ---
    private List<Vertice> ultimoCaminoDFS;
    private boolean ultimaSolucionDFS;

    // --- Estado auxiliar de BFS ---
    private List<Vertice> ultimoCaminoBFS;
    private boolean ultimaSolucionBFS;

    public Grafo() {
        this.vertices = new Vertices();
        this.adyacencia = new Hashtable<String, List<Arcos>>();
        this.ultimoCaminoDFS = new ArrayList<Vertice>();
        this.ultimaSolucionDFS = false;
        this.ultimoCaminoBFS = new ArrayList<Vertice>();
        this.ultimaSolucionBFS = false;
    }

    public void agregarVertice(Vertice vertice) {
        if (vertice == null || vertice.getClave() == null) {
            return;
        }
        vertices.agregar(vertice);
        if (!adyacencia.containsKey(vertice.getClave())) {
            adyacencia.put(vertice.getClave(), new ArrayList<Arcos>());
        }
        if (vertice.esEntrada() && this.inicio == null) {
            this.inicio = vertice;
        }
        if (vertice.esSalida() && this.meta == null) {
            this.meta = vertice;
        }
    }

    public void agregarArco(Vertice origen, Vertice destino, int peso) {
        if (origen == null || destino == null || origen.getClave() == null) {
            return;
        }
        if (!vertices.existe(origen.getClave())) {
            agregarVertice(origen);
        }
        if (destino.getClave() != null && !vertices.existe(destino.getClave())) {
            agregarVertice(destino);
        }

        List<Arcos> lista = adyacencia.get(origen.getClave());
        if (lista == null) {
            lista = new ArrayList<Arcos>();
            adyacencia.put(origen.getClave(), lista);
        }
        lista.add(new Arcos(origen, destino, peso));
    }

    public List<Arcos> obtenerArcosDe(Vertice vertice) {
        if (vertice == null || vertice.getClave() == null) {
            return new ArrayList<Arcos>();
        }
        List<Arcos> lista = adyacencia.get(vertice.getClave());
        return (lista != null) ? lista : new ArrayList<Arcos>();
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

        if (this.vertices == null || origen == null || destino == null
                || origen.getClave() == null || destino.getClave() == null) {
            System.out.println("DFS: Uno o ambos vértices no existen en el grafo.");
            return visitados;
        }

        Collection<Vertice> todosLosVertices = this.vertices.todos();
        if (todosLosVertices == null) {
            return visitados;
        }

        Hashtable<String, Boolean> visitadoMapa = new Hashtable<String, Boolean>();
        Hashtable<String, Vertice> predecesores = new Hashtable<String, Vertice>();

        for (Vertice actual : todosLosVertices) {
            if (actual != null && actual.getClave() != null) {
                visitadoMapa.put(actual.getClave(), false);
            }
        }

        String claveOrigen = origen.getClave();
        String claveDestino = destino.getClave();

        if (!visitadoMapa.containsKey(claveOrigen) || !visitadoMapa.containsKey(claveDestino)) {
            System.out.println("DFS: Uno o ambos vértices no existen en el grafo.");
            return visitados;
        }

        System.out.println("Recorrido DFS desde " + origen + " hasta " + destino + ":");

        // Lista usada como pila (LIFO) exclusiva de DFS
        List<Vertice> pilaDFS = new ArrayList<Vertice>();
        pilaDFS.add(origen);

        while (!pilaDFS.isEmpty()) {
            Vertice verticeActual = pilaDFS.remove(pilaDFS.size() - 1);
            if (verticeActual == null || verticeActual.getClave() == null) {
                continue;
            }

            String claveActual = verticeActual.getClave();
            Boolean yaVisitado = visitadoMapa.get(claveActual);
            if (yaVisitado != null && yaVisitado) {
                continue;
            }

            visitadoMapa.put(claveActual, true);
            visitados.add(verticeActual);
            System.out.print(verticeActual + " ");

            if (claveActual.equals(claveDestino)) {
                System.out.println("\nDFS: Se ha alcanzado el vértice final.");
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
                if (vecino == null || vecino.getClave() == null) {
                    continue;
                }

                String claveVecino = vecino.getClave();
                if (!visitadoMapa.containsKey(claveVecino)) {
                    continue;
                }

                Boolean vecinoVisitado = visitadoMapa.get(claveVecino);
                if (vecinoVisitado == null || !vecinoVisitado) {
                    if (!predecesores.containsKey(claveVecino)) {
                        predecesores.put(claveVecino, verticeActual);
                    }
                    pilaDFS.add(vecino);
                }
            }
        }

        System.out.println("\nDFS: No se encontró un camino hacia el vértice final.");
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

        if (this.vertices == null || origen == null || destino == null
                || origen.getClave() == null || destino.getClave() == null) {
            System.out.println("BFS: Uno o ambos vértices no existen en el grafo.");
            return visitados;
        }

        Collection<Vertice> todosLosVertices = this.vertices.todos();
        if (todosLosVertices == null) {
            return visitados;
        }

        Hashtable<String, Boolean> visitadoMapa = new Hashtable<String, Boolean>();
        Hashtable<String, Vertice> predecesores = new Hashtable<String, Vertice>();

        for (Vertice actual : todosLosVertices) {
            if (actual != null && actual.getClave() != null) {
                visitadoMapa.put(actual.getClave(), false);
            }
        }

        String claveOrigen = origen.getClave();
        String claveDestino = destino.getClave();

        if (!visitadoMapa.containsKey(claveOrigen) || !visitadoMapa.containsKey(claveDestino)) {
            System.out.println("BFS: Uno o ambos vértices no existen en el grafo.");
            return visitados;
        }

        System.out.println("Recorrido BFS desde " + origen + " hasta " + destino + ":");

        // Lista usada como cola (FIFO): se agrega al final y se saca del índice 0
        List<Vertice> colaBFS = new ArrayList<Vertice>();
        visitadoMapa.put(claveOrigen, true);
        colaBFS.add(origen);

        while (!colaBFS.isEmpty()) {
            Vertice verticeActual = colaBFS.remove(0);
            if (verticeActual == null || verticeActual.getClave() == null) {
                continue;
            }

            String claveActual = verticeActual.getClave();
            visitados.add(verticeActual);
            System.out.print(verticeActual + " ");

            if (claveActual.equals(claveDestino)) {
                System.out.println("\nBFS: Se ha alcanzado el vértice final.");
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
                if (vecino == null || vecino.getClave() == null) {
                    continue;
                }

                String claveVecino = vecino.getClave();
                if (!visitadoMapa.containsKey(claveVecino)) {
                    continue;
                }

                Boolean yaVisitado = visitadoMapa.get(claveVecino);
                if (yaVisitado == null || !yaVisitado) {
                    visitadoMapa.put(claveVecino, true);
                    if (!predecesores.containsKey(claveVecino)) {
                        predecesores.put(claveVecino, verticeActual);
                    }
                    colaBFS.add(vecino);
                }
            }
        }

        System.out.println("\nBFS: No se encontró un camino hacia el vértice final.");
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
        List<Vertice> rutaMasCorta = new ArrayList<Vertice>();

        if (this.vertices == null || origen == null || destino == null
                || origen.getClave() == null || destino.getClave() == null) {
            return rutaMasCorta;
        }

        Collection<Vertice> todosLosVertices = this.vertices.todos();
        if (todosLosVertices == null) {
            return rutaMasCorta;
        }

        int infinito = Integer.MAX_VALUE;
        Hashtable<String, Integer> distancias = new Hashtable<String, Integer>();
        Hashtable<String, Vertice> predecesores = new Hashtable<String, Vertice>();
        Hashtable<String, Boolean> visitadoMapa = new Hashtable<String, Boolean>();

        for (Vertice actual : todosLosVertices) {
            if (actual != null && actual.getClave() != null) {
                distancias.put(actual.getClave(), infinito);
                visitadoMapa.put(actual.getClave(), false);
            }
        }

        String claveOrigen = origen.getClave();
        String claveDestino = destino.getClave();

        if (!distancias.containsKey(claveOrigen) || !distancias.containsKey(claveDestino)) {
            return rutaMasCorta;
        }

        distancias.put(claveOrigen, 0);

        while (true) {
            Vertice actual = null;
            int menorDistancia = infinito;

            for (Vertice candidato : todosLosVertices) {
                if (candidato == null || candidato.getClave() == null) {
                    continue;
                }
                String claveCandidato = candidato.getClave();
                Boolean yaVisitado = visitadoMapa.get(claveCandidato);
                if (yaVisitado != null && yaVisitado) {
                    continue;
                }
                Integer distObj = distancias.get(claveCandidato);
                if (distObj != null && distObj < menorDistancia) {
                    menorDistancia = distObj;
                    actual = candidato;
                }
            }

            if (actual == null || menorDistancia == infinito) {
                break;
            }

            String claveActual = actual.getClave();
            visitadoMapa.put(claveActual, true);

            if (claveActual.equals(claveDestino)) {
                return reconstruirCamino(origen, destino, predecesores);
            }

            List<Arcos> arcosSalientes = obtenerArcosDe(actual);
            if (arcosSalientes == null) {
                continue;
            }

            for (int i = 0; i < arcosSalientes.size(); i++) {
                Arcos arco = arcosSalientes.get(i);
                if (arco == null || arco.getDestino() == null || arco.getDestino().getClave() == null) {
                    continue;
                }
                int peso = arco.getPeso();
                if (peso < 0) {
                    continue;
                }

                String claveVecino = arco.getDestino().getClave();
                if (!distancias.containsKey(claveVecino)) {
                    continue;
                }

                Boolean vecinoVisitado = visitadoMapa.get(claveVecino);
                if (vecinoVisitado != null && vecinoVisitado) {
                    continue;
                }

                int nuevaDistancia = menorDistancia + peso;
                Integer distVecinoObj = distancias.get(claveVecino);
                if (distVecinoObj != null && nuevaDistancia < distVecinoObj) {
                    distancias.put(claveVecino, nuevaDistancia);
                    predecesores.put(claveVecino, actual);
                }
            }
        }

        return rutaMasCorta;
    }

    private List<Vertice> reconstruirCamino(Vertice origen, Vertice destino,
            Hashtable<String, Vertice> predecesores) {
        List<Vertice> camino = new ArrayList<Vertice>();
        Vertice paso = destino;

        while (paso != null) {
            camino.add(paso);
            String clavePaso = paso.getClave();
            if (clavePaso == null || clavePaso.equals(origen.getClave())) {
                break;
            }
            paso = predecesores.get(clavePaso);
        }

        if (camino.isEmpty() || !camino.get(camino.size() - 1).getClave().equals(origen.getClave())) {
            return new ArrayList<Vertice>();
        }

        Collections.reverse(camino);
        return camino;
    }

    // Fin del camino mas corto

}

