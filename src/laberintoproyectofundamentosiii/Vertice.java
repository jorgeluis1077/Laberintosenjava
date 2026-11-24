
package laberintoproyectofundamentosiii;

public class Vertice {

    private int fila;
    private int columna;
    private int id = -1; // lo asigna Grafo: fila * columnas + columna
    private boolean entrada;
    private boolean salida;

    public Vertice(int fila, int columna, boolean entrada, boolean salida){
        this.fila = fila;
        this.columna = columna;
        this.entrada = entrada;
        this.salida = salida;
    }
    public int getFila(){
        return fila;
    }
    public int getColumna(){
        return columna;
    }
    public int getId(){
        return id;
    }
    void setId(int id){
        this.id = id;
    }
    public boolean esEntrada(){
        return entrada;
    }
    public boolean esSalida(){
        return salida;
    }
    public String toString(){
        return "(" + fila + "," + columna + ")";
    }
}
