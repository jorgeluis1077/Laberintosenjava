
package laberintoproyectofundamentosiii;

public class Vertice {
    
    private int fila;
    private int columna;
    private String clave;
    private boolean entrada;
    private boolean salida;
    
    public Vertice(int fila, int columna, boolean entrada, boolean salida){
        this.fila = fila;
        this.columna = columna;
        this.clave = fila + "," + columna;
        this.entrada = entrada;
        this.salida = salida;
    }
    public int getFila(){
        return fila;
    }
    public int getColumna(){
        return columna;
    }
    public String getClave(){
        return clave;
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
