# LaberintoProyectoFundamentosIII

Resolución de laberintos de texto mediante grafos en Java, con interfaz de consola y menú interactivo.

> **Nota de verificación:** este documento conserva la descripción original del proyecto. Algunas funciones descritas todavía no están conectadas al programa principal; consulta [Estado del proyecto](#estado-del-proyecto).

## Contenido

- [Descripción del proyecto](#descripción-del-proyecto)
- [Autores y responsabilidades](#autores-y-responsabilidades)
- [Fecha de entrega](#fecha-de-entrega)
- [Requisitos y dependencias](#requisitos-y-dependencias)
- [Estructura del repositorio](#estructura-del-repositorio)
- [Compilación y ejecución](#compilación-y-ejecución)
- [Formato de entrada y símbolos](#formato-de-entrada-y-símbolos)
- [Menú principal](#menú-principal)
- [Representación del grafo y recorridos](#representación-del-grafo-y-recorridos)
- [Uso de IA](#uso-de-ia)
- [Estado del proyecto](#estado-del-proyecto)

## Descripción del proyecto

El programa carga laberintos desde archivos `.txt`, los modela como grafos y plantea resolverlos mediante algoritmos de búsqueda y camino más corto desde la entrada hasta la meta.

La descripción original indica que el sistema valida la estructura de los mapas, construye vértices y arcos, y permite ejecutar distintos recorridos. También indica que cada algoritmo reinicia su estado en cada ejecución, registra los vértices visitados en orden de procesamiento sin duplicados, guarda predecesores para reconstruir el camino final, evita mostrar una longitud de 0 cuando no hay solución y termina al procesar la meta o agotar los vértices alcanzables. **Estas afirmaciones sobre todos los algoritmos están pendientes de verificar en la aplicación integrada**; el estado actual del código se detalla al final.

## Autores y responsabilidades

| Integrante | Usuario de GitHub | Responsabilidades indicadas en el README original | Responsabilidades definitivas |
| --- | --- | --- | --- |
| Sergio Carrillo Osorio | Pendiente de completar | Implementación de `CaminoMasCorto` (Dijkstra), integración del menú y pruebas de recorrido. | Pendiente de confirmar o completar. |
| Jorge Luis Guarista Tanori | Pendiente de completar | Implementación de los algoritmos DFS y BFS. | Pendiente de confirmar o completar. |
| Mario Francisco Ramirez Morales | Pendiente de completar | No figuraba en el README original. | Auditoría y frontend; pendiente de detallar el alcance. |

## Fecha de entrega

**28 de noviembre de 2026.**

## Requisitos y dependencias

| Elemento | Requisito indicado |
| --- | --- |
| Lenguaje y versión | Java JDK 25. |
| Entorno de desarrollo | Apache NetBeans IDE; el proyecto usa el paquete `laberintoproyectofundamentosiii`. |
| Dependencias externas | Ninguna. Se utiliza únicamente la biblioteca estándar de Java, por ejemplo `java.util.*` y `java.io.*`. |
| Sistema operativo | Compatible con Windows, Linux y macOS; probado en Windows con NetBeans y consola, según el README original. |

## Estructura del repositorio

| Ruta | Contenido |
| --- | --- |
| `LaberintoProyectoFundamentosIII/src/LaberintoProyectoFundamentosIII.java` | Clase principal, menú, selección de archivos `.txt` y validación de mapas. |
| `Clases/src/Vertice.java` | Coordenadas, clave y marcas de entrada y salida de un vértice. |
| `Clases/src/Vertices.java` | Colección de vértices indexada por clave. |
| `Clases/src/Arcos.java` | Arco entre dos vértices y su peso. |
| `Clases/src/Grafo.java` | Lista de adyacencia y construcción de vértices y arcos desde un mapa. |
| `Clases/src/CaminoMasCorto.java` | Implementación de Dijkstra y reconstrucción del camino. |
| `MapaLaberintos/laberinto1.txt` a `laberinto10.txt` | Mapas de ejemplo. |

## Compilación y ejecución

### Con Apache NetBeans IDE

Se conservan las instrucciones originales:

1. Abrir el proyecto `LaberintoProyectoFundamentosIII`.
2. Verificar que los archivos `.txt` estén en el directorio correspondiente.
3. Ejecutar **Clean and Build** para compilar todas las clases.
4. Ejecutar la clase principal `LaberintoProyectoFundamentosIII`.

> **Nota: pendiente de verificar.** En el repositorio actual no se observa la configuración de proyecto de NetBeans necesaria para confirmar estos pasos. La selección de archivos del programa busca `.txt` en el directorio de trabajo (`.`), mientras que los mapas incluidos están en `MapaLaberintos/`.

### Desde consola o terminal

Se conservan los pasos y comandos originales:

1. Posicionarse en la carpeta `src` del proyecto.
2. Compilar las clases del paquete:

   ```powershell
   javac -d build laberintoproyectofundamentosiii/*.java
   ```

3. Ejecutar la clase principal:

   ```powershell
   java -cp build laberintoproyectofundamentosiii.LaberintoProyectoFundamentosIII
   ```

> **Nota: pendiente de verificar.** Los archivos `.java` del repositorio están repartidos entre `LaberintoProyectoFundamentosIII/src` y `Clases/src`, y no se observa la carpeta `src/laberintoproyectofundamentosiii` que requieren los comandos anteriores. Además, al ejecutar el programa, los mapas deben estar en el directorio de trabajo para aparecer en el selector.

Para la **estructura actual del repositorio**, esta secuencia de PowerShell sí compila las seis clases y permite seleccionar los mapas incluidos. Se ejecuta desde la raíz del repositorio:

```powershell
New-Item -ItemType Directory -Path build -Force | Out-Null
javac -d build (Get-ChildItem Clases/src/*.java).FullName LaberintoProyectoFundamentosIII/src/LaberintoProyectoFundamentosIII.java
Set-Location MapaLaberintos
java -cp ../build laberintoproyectofundamentosiii.LaberintoProyectoFundamentosIII
```

En el menú, elige `1` para listar los mapas, selecciona su número y usa `6` para salir. La carga de `laberinto6.txt` muestra las coordenadas `Entrada borde: (1,0) | Salida borde: (3,4)`.

## Formato de entrada y símbolos

Los laberintos se almacenan en archivos de texto plano `.txt`. Cada fila de caracteres representa una fila de la cuadrícula. El lector actual comprueba que el archivo no esté vacío, que todas las filas tengan la misma longitud y que solo contengan los caracteres de la siguiente tabla.

| Símbolo | Significado |
| --- | --- |
| `+`, `-`, `\|` | Paredes o bordes que delimitan el laberinto; no son transitables. |
| ` ` (espacio) | Celda o pasillo transitable. En un borde válido puede funcionar como entrada o salida si no hay marcas explícitas. |
| `E` | Entrada explícita, si se utiliza. |
| `S` | Salida o meta explícita, si se utiliza. |
| `#` | Marca para mostrar el camino solución sobre el mapa; el lector también la acepta como celda transitable. |

Para indicar los extremos hay dos modalidades: usar exactamente una `E` y una `S`, o no usar ninguna de esas marcas y dejar exactamente dos celdas transitables en el perímetro. En la segunda modalidad, el lector toma la primera abertura como entrada y la segunda como salida al recorrer el mapa por filas. Las coordenadas mostradas comienzan en `(0,0)`.

Ejemplo original: `MapaLaberintos/laberinto6.txt`. Los espacios al final de la cuarta fila son parte del mapa y deben conservarse al copiarlo.

```text
-+-+
    |
-+ +
|    
-+-+
```

En este mapa se construyen **9 vértices**, con inicio **(1,0)** y meta **(3,4)**, según el README original. Al cargar archivos, la descripción original indica que se listan los disponibles, se construye el grafo de celdas transitables y se identifican la entrada y la meta. La carga actual solo identifica y muestra los extremos; la construcción del grafo no se llama desde el menú.

## Menú principal

| Opción | Función descrita en el README original |
| --- | --- |
| 1. Cargar laberinto | Lista los archivos disponibles para elegir uno, construye el grafo y muestra el total de vértices, el inicio y la meta. |
| 2. Resolver con DFS | Ejecuta búsqueda en profundidad y explora vecinos en el orden arriba, derecha, abajo, izquierda. |
| 3. Resolver con BFS | Ejecuta búsqueda en anchura y explora vecinos en el orden arriba, derecha, abajo, izquierda. |
| 4. Resolver con Dijkstra | Halla el camino de menor costo y muestra vértices visitados, si hay solución, longitud en movimientos y cantidad de vértices del camino final. |
| 5. Mostrar las tres soluciones | Muestra los resultados y el mapa resuelto con `#` para DFS, BFS y Dijkstra. |
| 6. Salir | Cierra el programa. |

> **Nota: pendiente de implementar en el menú.** Las opciones 2 a 5 actualmente imprimen mensajes o estados, sin ejecutar los algoritmos ni mostrar mapas resueltos. La opción 1 lee y valida el mapa, pero no construye el grafo ni muestra su total de vértices.

## Representación del grafo y recorridos

**Alternativa de adyacencia.** Cada celda transitable se modela como un `Vertice` identificado por su clave o posición. `Grafo` conecta las celdas transitables contiguas mediante objetos `Arcos` en listas de adyacencia, en lugar de emplear una matriz de adyacencia densa. Los arcos que crea `construirDesdeMapa` tienen peso 1.

**Orden de DFS y BFS descrito originalmente.** Los vecinos se exploran arriba, derecha, abajo e izquierda; en DFS se cuida el orden en que se apilan. **Pendiente de verificar:** no hay implementaciones de DFS ni BFS en el repositorio actual. Además, `Grafo.construirDesdeMapa` genera sus arcos en el orden arriba, abajo, izquierda y derecha.

**Desempate de Dijkstra.** Cuando dos o más vértices no visitados tienen la misma distancia mínima actual, se compara el texto de sus claves en orden alfabético; gana la clave que aparece primero. El criterio es repetible y no depende del orden interno de la `Hashtable`:

```java
claveCandidato.compareTo(mejorCandidato.getClave()) < 0
```

Este criterio aparece en `CaminoMasCorto.obtenerSiguienteVerticeMenorDistancia`.

## Uso de IA

El README original declara el uso de **Gemini (Google)** como apoyo en la revisión y depuración de la lógica de `CaminoMasCorto`, el manejo de colecciones y casos borde con valores nulos, la resolución de dudas generales del proyecto, la implementación del menú usando `list.dir` o similar, y la redacción y estructura de este README. Falta atribuir esta declaración a cada integrante y completar el modelo concreto, si se conoce.

| Integrante | Empresa | Modelo | Uso |
| --- | --- | --- | --- |
| Sergio Carrillo Osorio | Pendiente de completar | Pendiente de completar | Pendiente de completar por el integrante. |
| Jorge Luis Guarista Tanori | Pendiente de completar | Pendiente de completar | Pendiente de completar por el integrante. |
| Mario Francisco Ramirez Morales | Pendiente de completar | Pendiente de completar | Pendiente de completar por el integrante. |

Si alguno de los integrantes no utilizó IA, puede registrar **«No se utilizó IA»** en su fila y dejar **Empresa** y **Modelo** como **«No aplica»**. La declaración original de Gemini se conserva arriba hasta que el equipo precise su atribución.

## Estado del proyecto

La inspección del código disponible muestra estos puntos pendientes respecto de las funciones descritas arriba y de los requisitos indicados para la entrega:

| Punto | Estado observado |
| --- | --- |
| Carga e identificación de extremos | El menú lee y valida archivos `.txt` del directorio de trabajo y muestra entrada y salida. |
| Construcción del grafo durante la carga | `Grafo.construirDesdeMapa` existe, pero el menú no lo invoca ni muestra el total de vértices. |
| DFS y BFS | No aparecen implementaciones de estos algoritmos en los archivos Java actuales. |
| Dijkstra desde el menú | `CaminoMasCorto.caminoMasCorto` existe, pero la opción 4 no lo invoca ni presenta sus estadísticas. |
| Visualización de soluciones | La opción 5 solo imprime los indicadores de las opciones elegidas; no presenta caminos ni mapas marcados con `#`. |
| Pasos de compilación documentados | Queda por confirmar o adaptar la estructura necesaria para NetBeans y los comandos originales de consola. |
