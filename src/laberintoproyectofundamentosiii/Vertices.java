
package laberintoproyectofundamentosiii;

import java.util.Collection;
import java.util.Hashtable;

public class Vertices {
    private Hashtable<Integer, Vertice> tablaVertices;
    private int columnas;

    public Vertices(int columnas){
        this.tablaVertices = new Hashtable<Integer, Vertice>();
        this.columnas = columnas;
    }
    public void agregar(Vertice vertice){
        if(vertice != null){
            tablaVertices.put(vertice.getId(), vertice);
        }
    }
    public Vertice obtener(int id){
        return tablaVertices.get(id);
    }
    public Vertice obtenerPorCoordenada(int fila, int columna){
        return tablaVertices.get(fila * columnas + columna);
    }
    public boolean existe(int id){
        return tablaVertices.containsKey(id);
    }
    public Collection<Vertice> todos(){
        return tablaVertices.values();
    }
    public int total(){
        return tablaVertices.size();
    }
}
