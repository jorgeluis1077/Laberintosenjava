
package laberintoproyectofundamentosiii;

public class Arcos {
    private Vertice origen;
    private Vertice destino;
    private int peso;
    
    public Arcos(Vertice origen, Vertice destino, int peso){
        this.origen = origen;
        this.destino = destino;
        this.peso = peso;
    }
    public Vertice getDestino(){
        return destino;
    }
    public int getPeso(){
        return peso;
    }
    public String toString(){
        return origen.toString() + "-> " + destino.toString() + "(peso: " + peso + ")";
    }
}
