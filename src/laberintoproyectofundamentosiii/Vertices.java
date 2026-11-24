
package laberintoproyectofundamentosiii;

import java.util.Hashtable;

public class Vertices {
    private Hashtable<Integer, Vertice> tablaVertices;

    public Vertices(){
        this.tablaVertices = new Hashtable<Integer, Vertice>();
    }
    public void agregar(Vertice vertice){
        if(vertice != null){
            tablaVertices.put(vertice.getId(), vertice);
        }
    }
    public Vertice obtener(int id){
        return tablaVertices.get(id);
    }
    public boolean existe(int id){
        return tablaVertices.containsKey(id);
    }
}
