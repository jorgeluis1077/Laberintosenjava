
package laberintoproyectofundamentosiii;

import java.util.Collection;
import java.util.Hashtable;

public class Vertices {
    private Hashtable<String, Vertice> tablaVertices;
    
    public Vertices(){
        this.tablaVertices = new Hashtable<String, Vertice>();
    }
    public void agregar(Vertice vertice){
        if(vertice != null){
            tablaVertices.put(vertice.getClave(), vertice);
        }
    }
    public Vertice obtener(String clave){
        return tablaVertices.get(clave);
    }
    public Vertice obtenerPorCoordenada(int fila, int columna){
        return tablaVertices.get(fila + "," + columna);
    }
    public boolean existe(String clave){
        return tablaVertices.containsKey(clave);
    }
    public Collection<Vertice> todos(){
        return tablaVertices.values();
    }
    public int total(){
        return tablaVertices.size();
    }
}
