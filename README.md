# LaberintoProyectoFundamentosIII
Programa hecho en java para cargar laberintos hecho con archivos de texto, modelarlos con un grafo y resolverlos mediante algoritmos de búsqueda y camino más corto con interfaz por consola y un menú interactivo
## Descripción del proyecto
El sistema lee mapas de laberintos en archivos ".txt", validando su estructura, construyendo su representación en forma de grafo con vértices y arcos, y permitiendo ejecutar distintos algoritmos de recorrido o búsqueda desde la entrada hasta la meta. Cada algoritmo reinicia su estado en cada ejecución, registra los vértices visitados en orden de procesamiento sin duplicados, guarda predecesores para construir el camino final, evita poner una longitud 0 cuando no existe solución y se detiene al procesar la meta o agotar los vértices alcanzables.
## Autores
-Sergio Carrillo Osorio --> Implementación del CaminoMasCorto(Dijkstra), integración del menú y pruebas de recorrido
-Jorge Luis Guarista Tanori --> Implementación de los algoritmos DFS y BFS
## Fecha de entrega
28 de Noviembre de 2026
## Requisitos, dependencias y versión de Java
- Lenguaje: Java JDK 25
- Entorno de desarrollo: Apache NetBeans IDE (proyecto estructurado en el paquete `laberintoproyectofundamentosiii`).
- Dependencias externas: Ninguna. Utiliza únicamente la librería estándar de Java (`java.util.*`, `java.io.*`, etc.).
- Sistema operativo: Compatible con Windows, Linux y macOS (probado en entorno Windows con NetBeans y consola).
## Uso de la IA
Asistente utilizado: Gemini (Google)
Para apoyo en revisión y depuración de la lógica de CaminoMasCorto, manejo de colecciones y casos borde con valores nulos, resolución de dudas sobre el proyecto en general, implementación del menú usando list.dir o similar y redacción y estructura del README.md
## Instrucciones para compilar y ejecutar
1. Abrir el proyecto LaberintoProyectoFundamentosIII
2. Verificar que los archivos .txt estén en el directorio correspondiente
3. Ejecutar Clean and Build para compilar todas las clases
4. Ejecutar la clase principal LaberintoProyectoFundamentosIII
## Desde consola o terminal
1. Posicionarse en la carpeta src del proyecto
2. Compilar las clases del paquete:
   javac -d build laberintoproyectofundamentosiii/*.java
3. Ejecutar la clase principal:
   java -cp build laberintoproyectofundamentosiii.LaberintoProyectoFundamentosIII
## Símbolos del laberinto
+, -, |: Paredes o bordes que delimitan el laberinto (no caminables).

' ' (espacio): Celda o pasillo transitable (y en bordes válidos puede actuar como entrada o salida cuando no hay marcas explícitas).

E: Entrada explícita del laberinto (si se utiliza).

S: Salida o meta explícita del laberinto (si se utiliza).

#: Marca usada para mostrar el camino solución sobre el mapa.
## Formato de entrada y ejemplo
Los laberintos se almacenan en archivos de texto plano (.txt) formados por filas de caracteres que representan la cuadrícula del mapa. Al cargarlos, se listan los archivos disponibles, se construye el grafo de vértices transitables y se identifican la entrada y la meta.
EJEMPLO:
laberinto6.txt
+-+-+
    |
+-+ +
|    
+-+-+
En este mapa se construyen 9 vértices, con inicio (1,0) y meta (3,4)
## Menú principal
1. Cargar laberinto: Lista los archivos disponibles para elegir cuál cargar, construye el grafo y muestra total de vértices, inicio y meta
2. Resolver con DFS: Ejecuta búsqueda en profundidad explorando vecinos en el orden arriba, derecha, abajo, izquierda
3. Resolver con BFS: Ejecuta búsqueda en anchura explorando vecinos en el orden arriba, derecha, abajo, izquierda
4. Resolver con Dijkstra: Ejecuta Dijkstra para hallar el camino de menor costo y muestra estadísticas como vértices visitados, si hay solución, longitud en movimientos y cantidad de vértices en el camino final
5. Mostrar las tres soluciones: Muestra los resultados y el mapa resuelto con # para DFS, BFS y Dijkstra
6. Salir: Cierra el programa
## Alternativa de adyacencia y orden de recorrido
- Representación de adyacencia: Cada celda transitable se modela con un Vertice con su clave o posición y se conecta con celdas transitables contiguas mediante arcos o vecinos (Arcos/ lista de adyacencia en Grafo), en lugar de usar una matriz de adyacencia densa.
- Orden en DFS y BFS: Los vecinos se exploran en orden arriba, derecha, abajo e izquierda (en DFS cuidando el orden en que se apilan los vecinos)
## Criterio de desempate en Dijkstra
Cuando dos o más vértices no visitados tienen la misma distancia mínima actual, el desempate se hace de forma repetible y consistente comparando el texto de la clave del vértice en orden alfabético (claveCandidato.compareTo(mejorCandidato.getClave()) < 0), ganando la clave que va primero en ese orden. Así el resultado no depende de cómo se acomoden los datos por dentro en la Hashtable.
