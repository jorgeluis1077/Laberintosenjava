
package laberintoproyectofundamentosiii;
import java.io.File;
import java.util.Scanner;
public class LaberintoProyectoFundamentosIII {

    public static void main(String[] args) {
        ejecutarMenu();
    }
    public static void ejecutarMenu() {
        Scanner sc = new Scanner(System.in);
        String mapa = null;
        boolean hayDFS = false;
        boolean hayBFS = false;
        boolean hayDijkstra = false;
        int opcion = 0;
        do {
            System.out.println("\n1. Cargar laberinto");
            System.out.println("2. Resolver con DFS");
            System.out.println("3. Resolver con BFS");
            System.out.println("4. Resolver con Dijkstra(caminoMasCorto)");
            System.out.println("5. Mostrar las tres soluciones");
            System.out.println("6. Salir");
            System.out.println("Opcion: ");
            try {
                opcion = Integer.parseInt(sc.nextLine().trim());
            } catch (Exception e){
                System.out.println("Ingresa un numero valido");
                continue;
            }
            if (opcion == 1){
                System.out.println("Ruta del archivo (escribe su nombre exacto incluyendo el .txt): ");
                String ruta = sc.nextLine().trim();
                String nuevoMapa = cargaArchivo(ruta);
                if(nuevoMapa != null){
                    mapa = nuevoMapa;
                    hayDFS = false;
                    hayBFS = false;
                    hayDijkstra = false;
                    System.out.println("Se cargo el archivo.\n" + mapa);
                } else{
                    System.out.println("Fallo al cargar. Se mantiene el laberinto anterior, si es que ya existia");
                }
            }else if (opcion >= 2 && opcion <= 5){
                        if(mapa == null){
                            System.out.println("Carga primero un laberinto (opcion 1).");
                        }else if(opcion == 2){
                            System.out.println("Ejecutando DFS...");
                            hayDFS = true;
                        }else if(opcion == 3){
                            System.out.println("Ejecutando BFS...");
                            hayBFS = true;
                        }else if(opcion == 4){
                            System.out.println("Ejecutando Dijkstra (caminoMasCorto)...");
                            hayDijkstra = true;
                        }else{
                            System.out.println("Mostrando las 3 soluciones (DFS:" + hayDFS + ", BFS:" + hayBFS + ", Dijkstra:" + hayDijkstra + ")"); 
                        }
            }else if (opcion != 6){
                                System.out.println("La opcion que ingresaste no es correcta. Intenta de nuevo");
            }
        } while (opcion != 6);
        sc.close();
}
    public static String cargaArchivo (String ruta){
        try{
            File archivo = new File(ruta);
            Scanner sc = new Scanner(archivo);
            String contenido = "";
            while (sc.hasNextLine()){
                contenido += sc.nextLine() + "\n";
            }
            sc.close();
            if (contenido.isEmpty()) return null;
            return contenido;
        }catch (Exception e){
            System.out.println("No se pudo abrir el archivo" + e.getMessage());
            return null;
        }
      }
    }
