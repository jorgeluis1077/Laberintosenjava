package laberintoproyectofundamentosiii;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

public class CaminoMasCorto {

    private Grafo grafo;
    private List<Vertice> visitados;
    private List<Vertice> caminoFinal;
    private Hashtable<String, Integer> distancias;
    private Hashtable<String, Vertice> predecesores;
    private Hashtable<String, Boolean> visitadoMapa;
    private boolean solucionEncontrada;

    public CaminoMasCorto(Grafo grafo) {
        this.grafo = grafo;
        this.visitados = new ArrayList<Vertice>();
        this.caminoFinal = new ArrayList<Vertice>();
        this.distancias = new Hashtable<String, Integer>();
        this.predecesores = new Hashtable<String, Vertice>();
        this.visitadoMapa = new Hashtable<String, Boolean>();
        this.solucionEncontrada = false;
    }

    private void reiniciarEstado() {
        this.visitados = new ArrayList<Vertice>();
        this.caminoFinal = new ArrayList<Vertice>();
        this.distancias = new Hashtable<String, Integer>();
        this.predecesores = new Hashtable<String, Vertice>();
        this.visitadoMapa = new Hashtable<String, Boolean>();
        this.solucionEncontrada = false;
    }

    public List<Vertice> caminoMasCorto() {
        reiniciarEstado();

        if (grafo == null || grafo.getVertices() == null) {
            return visitados;
        }

        Vertice origen = grafo.getInicio();
        Vertice destino = grafo.getMeta();

        if (origen == null || destino == null
                || origen.getClave() == null || destino.getClave() == null) {
            return visitados;
        }

        Collection<Vertice> todosLosVertices = grafo.getVertices().todos();
        if (todosLosVertices == null) {
            return visitados;
        }

        int infinito = Integer.MAX_VALUE;

        for (Vertice actual : todosLosVertices) {
            if (actual == null) {
                continue;
            }
            String claveActual = actual.getClave();
            if (claveActual == null) {
                continue;
            }
            distancias.put(claveActual, infinito);
            visitadoMapa.put(claveActual, false);
        }

        String claveOrigen = origen.getClave();
        String claveDestino = destino.getClave();

        if (!distancias.containsKey(claveOrigen) || !distancias.containsKey(claveDestino)) {
            return visitados;
        }

        distancias.put(claveOrigen, 0);

        while (true) {
            Vertice verticeActual = obtenerSiguienteVerticeMenorDistancia(todosLosVertices);

            if (verticeActual == null) {
                break;
            }

            String claveActual = verticeActual.getClave();
            if (claveActual == null) {
                break;
            }

            Integer distanciaActualObj = distancias.get(claveActual);
            if (distanciaActualObj == null) {
                break;
            }

            int distanciaActual = distanciaActualObj;
            if (distanciaActual == infinito) {
                break;
            }

            Boolean yaVisitadoObj = visitadoMapa.get(claveActual);
            if (yaVisitadoObj != null && yaVisitadoObj) {
                continue;
            }

            visitadoMapa.put(claveActual, true);
            visitados.add(verticeActual);

            if (claveActual.equals(claveDestino)) {
                solucionEncontrada = true;
                break;
            }

            List<Arcos> arcosSalientes = grafo.obtenerArcosDe(verticeActual);
            if (arcosSalientes == null) {
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

                String claveVecino = vecino.getClave();
                if (claveVecino == null || !distancias.containsKey(claveVecino)
                        || !visitadoMapa.containsKey(claveVecino)) {
                    continue;
                }

                Boolean vecinoVisitadoObj = visitadoMapa.get(claveVecino);
                if (vecinoVisitadoObj != null && vecinoVisitadoObj) {
                    continue;
                }

                int pesoArco = arco.getPeso();
                if (pesoArco < 0) {
                    continue;
                }

                Integer distanciaVecinoObj = distancias.get(claveVecino);
                if (distanciaVecinoObj == null) {
                    continue;
                }

                int nuevaDistancia = distanciaActual + pesoArco;
                int distanciaVecino = distanciaVecinoObj;

                if (nuevaDistancia < distanciaVecino) {
                    distancias.put(claveVecino, nuevaDistancia);
                    predecesores.put(claveVecino, verticeActual);
                }
            }
        }

        if (solucionEncontrada) {
            reconstruirCamino(origen, destino);
        }

        return visitados;
    }

    private Vertice obtenerSiguienteVerticeMenorDistancia(Collection<Vertice> todosLosVertices) {
        Vertice mejorCandidato = null;
        int menorDistancia = Integer.MAX_VALUE;

        for (Vertice candidato : todosLosVertices) {
            if (candidato == null) {
                continue;
            }

            String claveCandidato = candidato.getClave();
            if (claveCandidato == null) {
                continue;
            }

            Boolean yaVisitadoObj = visitadoMapa.get(claveCandidato);
            if (yaVisitadoObj != null && yaVisitadoObj) {
                continue;
            }

            Integer distanciaCandidatoObj = distancias.get(claveCandidato);
            if (distanciaCandidatoObj == null) {
                continue;
            }

            int distanciaCandidato = distanciaCandidatoObj;
            if (distanciaCandidato < menorDistancia) {
                menorDistancia = distanciaCandidato;
                mejorCandidato = candidato;
            } else if (distanciaCandidato == menorDistancia && mejorCandidato != null) {
                if (claveCandidato.compareTo(mejorCandidato.getClave()) < 0) {
                    mejorCandidato = candidato;
                }
            }
        }

        return mejorCandidato;
    }

    private void reconstruirCamino(Vertice origen, Vertice destino) {
        List<Vertice> caminoInverso = new ArrayList<Vertice>();
        Vertice paso = destino;

        while (paso != null) {
            caminoInverso.add(paso);
            String clavePaso = paso.getClave();
            if (clavePaso == null) {
                break;
            }
            if (clavePaso.equals(origen.getClave())) {
                break;
            }
            paso = predecesores.get(clavePaso);
        }

        if (caminoInverso.isEmpty()) {
            solucionEncontrada = false;
            caminoFinal = new ArrayList<Vertice>();
            return;
        }

        Vertice ultimo = caminoInverso.get(caminoInverso.size() - 1);
        if (ultimo == null || ultimo.getClave() == null
                || !ultimo.getClave().equals(origen.getClave())) {
            solucionEncontrada = false;
            caminoFinal = new ArrayList<Vertice>();
            return;
        }

        Collections.reverse(caminoInverso);
        caminoFinal = caminoInverso;
    }

    public boolean isSolucionEncontrada() {
        return solucionEncontrada;
    }

    public List<Vertice> getVisitados() {
        return visitados;
    }

    public List<Vertice> getCaminoFinal() {
        return caminoFinal;
    }

    public int obtenerLongitudEnMovimientos() {
        if (!solucionEncontrada || caminoFinal.size() < 2) {
            return -1;
        }
        return caminoFinal.size() - 1;
    }

    public int obtenerCostoTotal() {
        if (!solucionEncontrada || grafo == null || grafo.getMeta() == null
                || grafo.getMeta().getClave() == null) {
            return -1;
        }
        Integer costo = distancias.get(grafo.getMeta().getClave());
        if (costo == null || costo == Integer.MAX_VALUE) {
            return -1;
        }
        return costo;
    }
}
