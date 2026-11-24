package laberintoproyectofundamentosiii;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

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

    public Grafo(int columnas) {
        this.columnas = columnas;
        this.vertices = new Vertices(columnas);
        this.adyacencia = new Hashtable<Integer, List<Arcos>>();
        this.ultimoCaminoDFS = new ArrayList<Vertice>();
        this.ultimaSolucionDFS = false;
        this.ultimoCaminoBFS = new ArrayList<Vertice>();
        this.ultimaSolucionBFS = false;
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
            adyacencia.put(id, new ArrayList<Arcos>());
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
            return new ArrayList<Arcos>();
        }
        List<Arcos> lista = adyacencia.get(vertice.getId());
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


        // Lista usada como pila (LIFO) exclusiva de DFS
        List<Vertice> pilaDFS = new ArrayList<Vertice>();
        pilaDFS.add(origen);

        while (!pilaDFS.isEmpty()) {
            Vertice verticeActual = pilaDFS.remove(pilaDFS.size() - 1);
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
                    if (!predecesores.containsKey(idVecino)) {
                        predecesores.put(idVecino, verticeActual);
                    }
                    pilaDFS.add(vecino);
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


        // Lista usada como cola (FIFO): se agrega al final y se saca del índice 0
        List<Vertice> colaBFS = new ArrayList<Vertice>();
        visitadoMapa.put(idOrigen, true);
        colaBFS.add(origen);

        while (!colaBFS.isEmpty()) {
            Vertice verticeActual = colaBFS.remove(0);
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
        List<Vertice> rutaMasCorta = new ArrayList<Vertice>();

        if (this.vertices == null || origen == null || destino == null) {
            return rutaMasCorta;
        }

        Collection<Vertice> todosLosVertices = this.vertices.todos();
        if (todosLosVertices == null) {
            return rutaMasCorta;
        }

        int infinito = Integer.MAX_VALUE;
        Hashtable<Integer, Integer> distancias = new Hashtable<Integer, Integer>();
        Hashtable<Integer, Vertice> predecesores = new Hashtable<Integer, Vertice>();
        Hashtable<Integer, Boolean> visitadoMapa = new Hashtable<Integer, Boolean>();

        for (Vertice actual : todosLosVertices) {
            if (actual != null) {
                distancias.put(actual.getId(), infinito);
                visitadoMapa.put(actual.getId(), false);
            }
        }

        int idOrigen = origen.getId();
        int idDestino = destino.getId();

        if (!distancias.containsKey(idOrigen) || !distancias.containsKey(idDestino)) {
            return rutaMasCorta;
        }

        distancias.put(idOrigen, 0);

        while (true) {
            Vertice actual = null;
            int menorDistancia = infinito;

            for (Vertice candidato : todosLosVertices) {
                if (candidato == null) {
                    continue;
                }
                int idCandidato = candidato.getId();
                Boolean yaVisitado = visitadoMapa.get(idCandidato);
                if (yaVisitado != null && yaVisitado) {
                    continue;
                }
                Integer distObj = distancias.get(idCandidato);
                if (distObj != null && distObj < menorDistancia) {
                    menorDistancia = distObj;
                    actual = candidato;
                }
            }

            if (actual == null || menorDistancia == infinito) {
                break;
            }

            int idActual = actual.getId();
            visitadoMapa.put(idActual, true);

            if (idActual == idDestino) {
                return reconstruirCamino(origen, destino, predecesores);
            }

            List<Arcos> arcosSalientes = obtenerArcosDe(actual);
            if (arcosSalientes == null) {
                continue;
            }

            for (int i = 0; i < arcosSalientes.size(); i++) {
                Arcos arco = arcosSalientes.get(i);
                if (arco == null || arco.getDestino() == null) {
                    continue;
                }
                int peso = arco.getPeso();
                if (peso < 0) {
                    continue;
                }

                int idVecino = arco.getDestino().getId();
                if (!distancias.containsKey(idVecino)) {
                    continue;
                }

                Boolean vecinoVisitado = visitadoMapa.get(idVecino);
                if (vecinoVisitado != null && vecinoVisitado) {
                    continue;
                }

                int nuevaDistancia = menorDistancia + peso;
                Integer distVecinoObj = distancias.get(idVecino);
                if (distVecinoObj != null && nuevaDistancia < distVecinoObj) {
                    distancias.put(idVecino, nuevaDistancia);
                    predecesores.put(idVecino, actual);
                }
            }
        }

        return rutaMasCorta;
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

