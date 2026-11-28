# LaberintoProyectoFundamentosIII

Resolución de laberintos de texto mediante grafos en Java. El programa carga un mapa desde un archivo `.txt`, lo valida, lo modela como un grafo no dirigido: cada conexión se guarda como dos arcos (ida y vuelta) de costo 1. Lo resuelve con DFS, BFS y Dijkstra. Incluye un menú de consola con animación de la exploración, medición de tiempos, dos temas de caracteres (ASCII y Unicode) y una interfaz gráfica con Swing conectada a la misma lógica.

## Tabla de contenido

1. [Autores](#autores)
2. [Fecha de entrega](#fecha-de-entrega)
3. [Requisitos y dependencias](#requisitos-y-dependencias)
4. [Estructura del repositorio](#estructura-del-repositorio)
5. [Compilación y ejecución](#compilación-y-ejecución)
6. [Formato de entrada](#formato-de-entrada)
7. [Menú principal](#menú-principal)
8. [Símbolos de presentación](#símbolos-de-presentación)
9. [Representación del grafo y algoritmos](#representación-del-grafo-y-algoritmos)
10. [Tiempos de ejecución](#tiempos-de-ejecución)
11. [Mapas de prueba](#mapas-de-prueba)
12. [Extras implementados](#extras-implementados)
13. [Responsabilidades](#responsabilidades)
14. [Declaración de uso de IA](#declaración-de-uso-de-ia)

## Autores

| Integrante | Usuario de GitHub |
| --- | --- |
| Mario Francisco Ramirez Morales | MarioJavaScript |
| Jorge Luis Guarista Tanori | jorgeluis1077 |
| Sergio Carrillo Osorio (Sergipato) | sergiocarrilli42-debug |

## Fecha de entrega

**28 de noviembre de 2026.**

## Requisitos y dependencias

| Elemento | Requisito |
| --- | --- |
| Lenguaje | Java, JDK 17 o superior (se usan `record`, `switch` con flechas y `Stream.toList()`). |
| Dependencias externas | Ninguna. Solo la biblioteca estándar: `java.util`, `java.nio.file` y `javax.swing`. |
| Sistema operativo | Windows, Linux o macOS. |
| Consola para el tema Unicode | UTF-8 y una fuente con los símbolos de caja (por ejemplo Cascadia Mono o Consolas). |

## Estructura del repositorio

```text
Laberintosenjava/
├── MapaLaberintos/              Mapas de ejemplo (.txt); invalidos/ contiene mapas que deben rechazarse
├── PruebasLaberintos/           Salidas de consola y soluciones (DFS, BFS, Dijkstra) de los 16 mapas
├── docs/
│   └── FRONTEND.md              Detalles de la GUI Swing y de los temas de consola
├── src/
│   ├── laberintoproyectofundamentosiii/   Base del proyecto
│   │   ├── LaberintoProyectoFundamentosIII.java
│   │   ├── LaberintoInvalidoException.java
│   │   ├── Grafo.java
│   │   ├── Vertice.java
│   │   ├── Vertices.java
│   │   ├── Arcos.java
│   │   └── Despliegue.java
│   └── frontend/
│       ├── modelo/              Tipos compartidos y adaptador hacia la base
│       ├── consola/             Temas y dibujo en consola
│       └── gui/                 Interfaz gráfica Swing
└── README.md

### Base (`laberintoproyectofundamentosiii`)

| Clase | Descripción |
| --- | --- |
| `LaberintoProyectoFundamentosIII` | Clase principal: menú, selección de archivo, validación y lectura del mapa (`validarYLeerLaberinto`), construcción del grafo (`construirGrafoDesdeMapa`), enumeración `Algoritmo` que mide el tiempo de cada búsqueda y orden natural de nombres de archivo. |
| `LaberintoInvalidoException` | Excepción con el motivo legible por el que un mapa se rechaza. |
| `Grafo` | Grafo no dirigido: cada conexión se guarda como dos arcos (ida y vuelta) de costo 1 en listas ligadas de adyacencia; implementa `DFS()`, `BFS()` y `caminoMasCorto()` (Dijkstra) y expone sus resultados complementarios con `getUltimoCaminoDFS()`, `getUltimoCaminoBFS()` y `getUltimosVisitadosDijkstra()`. |
| `Vertice` | Celda transitable: fila, columna, id y marcas de entrada y salida. |
| `Vertices` | Colección de vértices indexada por id (`Hashtable<Integer, Vertice>`). |
| `Arcos` | Arco de una conexión; cada conexión se representa con dos arcos de costo 1. |
| `Despliegue` | Presentación en consola: animación paso a paso, resultado final, leyenda y tiempo; contiene la única conversión de vértices a celdas del frontend. |

### Frontend (`frontend`)

| Paquete | Clase | Descripción |
| --- | --- | --- |
| `modelo` | `Celda` | Posición inmutable (fila, columna). |
| `modelo` | `DatosLaberinto` | Mapa de caracteres con entrada, salida y nombre del archivo. |
| `modelo` | `ResultadoVista` | Visitados, camino, si hubo solución y tiempo de un algoritmo. |
| `modelo` | `ErrorCargaException` | Error de carga con mensaje para el usuario. |
| `modelo` | `ServicioLaberinto` | Interfaz que usa la GUI; es su único contacto con la lógica. |
| `modelo` | `ServicioBase` | Implementación real: usa la carga y el `Grafo` de la base, mide los tiempos y guarda los resultados del laberinto actual. |
| `consola` | `TemaConsola` | Símbolos de los temas ASCII y Unicode. |
| `consola` | `RenderConsola` | Arma el texto del laberinto con la exploración y la leyenda. |
| `consola` | `SalidaConsola` | Configura UTF-8 y vuelve a ASCII si la consola no admite Unicode. |
| `consola` | `PruebaTema` | Prueba que ambos temas conservan filas, columnas, paredes y pasillos en todos los mapas válidos. |
| `gui` | `MainSwing` | Punto de entrada de la GUI. |
| `gui` | `VentanaLaberinto` | Ventana principal: carga, ejecución en `SwingWorker`, animación y resultados. |
| `gui` | `PanelLaberinto`, `RenderLaberinto` | Dibujo del laberinto (región visible) y de sus celdas. |
| `gui` | `PanelEjemplos` | Lista lateral de mapas válidos e inválidos. |
| `gui` | `PanelResultados`, `TarjetaResultado`, `VistaComparar`, `MiniLaberinto` | Tarjetas por algoritmo y vista de comparación con miniaturas. |
| `gui` | `Animador`, `EstadoVista` | Avance paso a paso con `javax.swing.Timer` y estado a pintar. |
| `gui` | `Tema`, `BotonEstilo`, `Insignia`, `Leyenda`, `Formato` | Estilo visual, componentes auxiliares y textos compartidos. |

## Compilación y ejecución

Todos los comandos se ejecutan desde la raíz del repositorio, para que el programa encuentre la carpeta `MapaLaberintos`. Se compila en una carpeta temporal para no dejar archivos `.class` en el repositorio.

### PowerShell (Windows)

```powershell
$out = "$env:TEMP\laberinto"
javac -encoding UTF-8 -d $out (Get-ChildItem -Recurse src -Filter *.java).FullName

# Consola (con --unicode se omite la pregunta del tema)
chcp 65001
java -cp $out laberintoproyectofundamentosiii.LaberintoProyectoFundamentosIII --unicode

# Interfaz gráfica
java -cp $out frontend.gui.MainSwing

# Prueba de los temas de consola
java -cp $out frontend.consola.PruebaTema
```

### Linux y macOS

```bash
out=/tmp/laberinto
javac -encoding UTF-8 -d "$out" $(find src -name '*.java')

java -cp "$out" laberintoproyectofundamentosiii.LaberintoProyectoFundamentosIII --unicode
java -cp "$out" frontend.gui.MainSwing
java -cp "$out" frontend.consola.PruebaTema
```

Sin `--unicode`, el menú pregunta el tema al iniciar: ENTER o `1` para ASCII, `2` para Unicode.

## Formato de entrada

Los laberintos son archivos de texto plano. Cada renglón es una fila de la cuadrícula y cada carácter es una celda.

| Carácter | Significado |
| --- | --- |
| `+`, `-`, `\|` | Pared; no transitable. |
| ` ` (espacio) | Pasillo transitable. |
| `#` | Transitable con costo 1, igual que el espacio. |
| `E` | Entrada explícita. |
| `S` | Salida explícita. |

### Reglas de lectura y validación

- El archivo se lee en UTF-8; se tolera un BOM inicial y se ignoran los renglones vacíos al final.
- Todos los renglones deben tener la misma longitud; un renglón vacío intermedio es inválido.
- Cualquier otro carácter, incluida la tabulación, es inválido.
- Los espacios al final de un renglón forman parte del mapa y deben conservarse.

### Reglas de entrada y salida

- Si se usan marcas, debe haber exactamente una `E` y una `S`.
- Si no hay marcas, debe haber exactamente dos posiciones transitables en el borde. Recorriendo el mapa por filas, la primera es la entrada y la segunda la salida.
- Si el mapa no cumple las reglas, la carga se rechaza con el motivo y se conserva el laberinto anterior.
- Las coordenadas se muestran como `(fila,columna)` y comienzan en `(0,0)`.

### Ejemplo

`MapaLaberintos/laberinto6.txt` (la cuarta fila termina con un espacio):

```text
+-+-+
    |
+-+ +
|    
+-+-+
```

Se construyen 9 vértices, con entrada `(1,0)` y salida `(3,4)`. Al cargarlo, el programa muestra `Entrada borde: (1,0) | Salida borde: (3,4)`.

## Menú principal

| Opción | Función |
| --- | --- |
| 1. Cargar laberinto | Pide la ruta de un `.txt`; con ENTER lista los mapas de `MapaLaberintos` en orden natural (0 cancela). Valida el mapa, construye el grafo y muestra la entrada y la salida. Si falla, muestra el motivo y conserva el laberinto anterior. |
| 2. Resolver con DFS | Anima la exploración y muestra el resultado final. |
| 3. Resolver con BFS | Igual que la opción 2, con BFS. |
| 4. Resolver con Dijkstra | Igual que la opción 2, con Dijkstra (`caminoMasCorto`). |
| 5. Mostrar las tres soluciones | Muestra las tres soluciones seguidas, sin animación, reutilizando los resultados ya calculados para el laberinto actual. |
| 6. Salir | Cierra el programa. |

En las opciones 2 a 4 se elige el modo de avance:

| Entrada | Modo |
| --- | --- |
| ENTER | Paso a paso; ENTER avanza y `f` salta al final. |
| `0` a `5000` | Avance automático con esa pausa en milisegundos. |
| `f` | Muestra directamente el resultado final. |

El resultado final incluye el mapa con el camino, la leyenda, el número de vértices visitados, la longitud en movimientos (o el aviso de que no existe camino) y el tiempo.

## Símbolos de presentación

Los temas solo cambian lo que se imprime; el archivo y el grafo no se modifican y cada posición sigue ocupando un carácter.

| Elemento | ASCII (por defecto) | Unicode |
| --- | --- | --- |
| Paredes | `+ - \|` | `─ │ ┌ ┐ └ ┘ ├ ┤ ┬ ┴ ┼` (cada `+` según sus vecinos) |
| Transitable | espacio | espacio |
| Entrada | `E` | `▶` |
| Salida | `S` | `◆` |
| Visitado | `.` | `·` |
| Vértice actual | `@` | `◉` |
| Camino final | `*` | `●` |

Si la consola no usa UTF-8, `SalidaConsola` avisa y vuelve al tema ASCII.

## Representación del grafo y algoritmos

### Grafo

| Aspecto | Implementación |
| --- | --- |
| Vértices | Una por celda transitable (espacio, `#`, `E`, `S`). |
| Identificador | `id = fila * columnas + columna`. |
| Adyacencia | `Hashtable<Integer, List<Arcos>>` con listas ligadas (`LinkedList`), en lugar de una matriz densa. |
| Arcos | Dirigidos, entre celdas transitables contiguas; todos con peso 1, incluido `#`. |
| Orden de vecinos | Arriba, derecha, abajo, izquierda. |

### Algoritmos

| Algoritmo | Método | Funcionamiento |
| --- | --- | --- |
| DFS | `Grafo.DFS()` | Pila (`ArrayDeque`) de pares (vértice, padre). Apila los vecinos en orden inverso para explorar primero el de arriba. Un vértice se marca y se registra al sacarlo de la pila; su predecesor es el padre que lo apiló. Devuelve los visitados; el camino se lee con `getUltimoCaminoDFS()`. |
| BFS | `Grafo.BFS()` | Cola (`ArrayDeque`) en el orden de vecinos. Marca al encolar y registra al desencolar. Devuelve los visitados; el camino se lee con `getUltimoCaminoBFS()`. |
| Dijkstra | `Grafo.caminoMasCorto()` | `PriorityQueue` ordenada por (distancia, id menor); descarta entradas obsoletas y registra cada vértice al extraerlo. Devuelve el camino; los visitados se leen con `getUltimosVisitadosDijkstra()`. |

Reglas comunes:

- Cada ejecución reinicia el estado del algoritmo.
- Los visitados se registran en orden de procesamiento y sin duplicados.
- La búsqueda termina al procesar la salida o al agotar los vértices alcanzables.
- El camino se reconstruye con los predecesores, de la entrada a la salida.
- Si no hay solución, el camino queda vacío y se informa que no existe; nunca se muestra una longitud de 0.
- La longitud se cuenta en movimientos (vértices del camino menos uno).

Como todos los arcos pesan 1, BFS y Dijkstra encuentran caminos de la misma longitud; DFS puede devolver uno más largo (véase `ciclo_dfs_mas_largo.txt`).

## Tiempos de ejecución

- Se mide con `System.nanoTime()` únicamente la llamada al algoritmo, sin incluir la lectura de getters, la conversión ni el dibujo.
- La consola lo muestra como `Tiempo: 323600 ns (0.323600 ms)`.
- La opción 5 y la GUI reutilizan el resultado ya calculado para el laberinto actual, así que muestran el mismo tiempo de esa ejecución.
- Los valores varían entre ejecuciones por el calentamiento de la JVM; sirven para comparar los algoritmos sobre un mismo mapa.

## Mapas de prueba

### Válidos (`MapaLaberintos/`)

| Archivo | Propósito |
| --- | --- |
| `laberinto1.txt` a `laberinto10.txt` | Laberintos de ejemplo de distintos tamaños (`laberinto9.txt` es el más grande, 159 filas). |
| `con_marcas.txt` | Entrada y salida con `E` y `S`. |
| `con_numerales.txt` | Pasillos con `#`, transitables con costo 1. |
| `espacios_finales.txt` | Espacios al final de los renglones que forman parte del mapa. |
| `no_cuadrado.txt` | Mapa rectangular, no cuadrado. |
| `ciclo_dfs_mas_largo.txt` | Ciclo en el que DFS encuentra un camino más largo que BFS y Dijkstra. |
| `sin_solucion_exploracion.txt` | Sin camino entre entrada y salida; se exploran todos los vértices alcanzables. |

### Inválidos (`MapaLaberintos/invalidos/`)

| Archivo | Motivo mostrado |
| --- | --- |
| `caracter_invalido.txt` | Carácter inválido `'x'` con su fila y columna. |
| `dos_e.txt` | Debe haber exactamente una E y una S. |
| `e_sin_s.txt` | Debe haber exactamente una E y una S. |
| `perimetro_tres.txt` | Sin E/S debe haber exactamente 2 posiciones transitables en el borde. |
| `renglon_vacio_interno.txt` | El renglón tiene longitud 0. |
| `renglones_distintos.txt` | El renglón tiene distinta longitud. |
| `solo_saltos_de_linea.txt` | El archivo está vacío o solo contiene saltos de línea. |
| `tabulacion.txt` | Tabulación inválida con su fila y columna. |
| `utf8_invalido.txt` | El archivo no contiene texto UTF-8 válido. |
| `vacio.txt` | El archivo está vacío o solo contiene saltos de línea. |

## Extras implementados

| Extra | Descripción |
| --- | --- |
| Tiempos de solución | Medición con `System.nanoTime()` de cada algoritmo, en consola y en la GUI. |
| Caracteres alternativos | Tema Unicode con paredes de caja y símbolos para entrada, salida, visitados, actual y camino. |
| Interfaz gráfica con Swing | Carga de mapas, animación de la exploración, tarjetas de resultados y vista comparativa de los tres algoritmos, conectada a la base mediante `ServicioBase`. Detalles en [docs/FRONTEND.md](docs/FRONTEND.md). |
| README | Documentación completa del proyecto. |

## Responsabilidades

| Integrante | Responsabilidades |
| --- | --- |
| Mario Ramírez (MarioJavaScript) | Reestructuración del repositorio, correcciones de la base (lectura y validación, grafo, Dijkstra, DFS), despliegue en consola, tiempos de ejecución y extras (caracteres alternativos, GUI Swing y README). |
| Jorge Luis Guarista Tanori | Implementación de DFS y BFS en `Grafo`, conexión de DFS, BFS y camino más corto en el menú principal, y mapas `laberinto6.txt` a `laberinto10.txt`. |
| Sergipato (sergiocarrilli42-debug) | Menú principal y carga de archivos desde una lista, clases `Grafo`, `Vertice`, `Vertices` y `Arcos`, primera implementación de Dijkstra, mapas `laberinto1.txt` a `laberinto5.txt` y README inicial. |

## Declaración de uso de IA

| Integrante | Empresa | Modelo | Uso |
| --- | --- | --- | --- |
| Mario Ramírez | OpenAI | ChatGPT (Astra 6.2) | Generar código. |
| Mario Ramírez | Anthropic | Claude Opus 5.5 | Auditar el código. |
| jorgeluis1077 | Google | Gemini Hydrogen | Apoyo en el desarrollo. |
| sergiocarrilli42-debug | Google | Gemini Hydrogen | Apoyo en el desarrollo. |

El README anterior detallaba el apoyo de Gemini (Google): revisión y depuración de la lógica de `CaminoMasCorto` (la primera implementación de Dijkstra), manejo de colecciones y casos borde con valores nulos, resolución de dudas generales del proyecto, implementación del menú usando `list.dir` o similar, y redacción y estructura del README.
