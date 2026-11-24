# Frontend (interfaz gráfica con Swing y temas de consola)

La interfaz gráfica ya está conectada a la base: `MainSwing` crea la ventana con `ServicioBase`, que usa la carga real (`validarYLeerLaberinto`), el `Grafo` real (`DFS()`, `BFS()`, `caminoMasCorto()`) y la misma medición de tiempos que el menú de consola. Por eso los visitados, la longitud y la forma de medir los tiempos coinciden con la consola.

Requiere JDK 17 o superior, sin librerías externas.

## Estructura

```text
src/frontend/
  modelo/    Celda, DatosLaberinto, ResultadoVista, ErrorCargaException
             ServicioLaberinto   interfaz: único contacto entre la GUI y la lógica
             ServicioBase        implementación con la base real
  consola/   TemaConsola, RenderConsola, SalidaConsola, PruebaTema
  gui/       MainSwing, VentanaLaberinto, PanelLaberinto y auxiliares
```

La GUI solo conoce `ServicioLaberinto`; nunca usa directamente las clases de la base.

## Compilar y ejecutar

Desde la raíz del repositorio (así encuentra `MapaLaberintos`), compilando en una carpeta temporal:

```powershell
$out = "$env:TEMP\laberinto"
javac -encoding UTF-8 -d $out (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp $out frontend.gui.MainSwing
```

```bash
javac -encoding UTF-8 -d /tmp/laberinto $(find src -name '*.java')
java -cp /tmp/laberinto frontend.gui.MainSwing
```

## ServicioBase

| Método | Comportamiento |
| --- | --- |
| `cargar(Path)` | Valida y lee el mapa con la base y construye el grafo. Si falla, lanza `ErrorCargaException` con el motivo de `LaberintoInvalidoException` y conserva el laberinto anterior. |
| `resolverDFS()`, `resolverBFS()`, `resolverDijkstra()` | Ejecutan el algoritmo con la enumeración `Algoritmo` del menú (mide con `System.nanoTime()` solo la llamada al algoritmo), convierten los vértices a `Celda(fila, columna)` con `Despliegue.resultadoVista` y guardan el resultado; si ya existe para el laberinto actual, devuelven el guardado. Sin laberinto cargado lanzan `IllegalStateException`. |
| `reiniciarResultados()` | Limpia los resultados guardados. |
| `carpetaEjemplos()`, `listarEjemplos()`, `listarEjemplosInvalidos()` | Devuelven `MapaLaberintos` y sus `.txt` (y los de `invalidos/`) en orden natural, con el mismo comparador que el menú. |

Los métodos con estado son `synchronized`, porque la GUI los llama desde `SwingWorker`.

## Carpeta de ejemplos

La lista lateral muestra los `.txt` de `MapaLaberintos`, relativa al directorio de ejecución. La subcarpeta `invalidos/` aparece como la sección colapsada "Ejemplos inválidos"; al elegir uno, la GUI muestra el motivo del rechazo y conserva el laberinto anterior.

## Caracteres alternativos (consola)

Extra de presentación: la consola puede dibujar el laberinto con símbolos distintos. Solo cambia lo que se imprime: el archivo sigue usando `+ - | espacio # E S` y no se tocan el mapa ni el grafo. Cada posición sigue siendo un solo carácter.

| Elemento | ASCII (por defecto) | Unicode |
| --- | --- | --- |
| Paredes | `+ - \|` | `─ │ ┌ ┐ └ ┘ ├ ┤ ┬ ┴ ┼` (cada `+` según sus vecinos) |
| Transitable (espacio o `#`) | espacio | espacio |
| Entrada | `E` | `▶` |
| Salida | `S` | `◆` |
| Visitado | `.` | `·` |
| Vértice actual | `@` | `◉` |
| Camino final | `*` | `●` |

La entrada y la salida siempre quedan visibles.

### Tema en el menú principal

Al iniciar `laberintoproyectofundamentosiii.LaberintoProyectoFundamentosIII`, ENTER o `1` eligen ASCII y `2` elige Unicode. Con `--unicode` se omite la pregunta. Las opciones 2 a 4 animan la exploración con el tema elegido y la opción 5 muestra las tres soluciones reutilizando los resultados del laberinto actual.

### Prueba de los temas

```powershell
java -cp $out frontend.consola.PruebaTema
```

`PruebaTema` carga con `ServicioBase` todos los mapas válidos, resuelve con BFS y comprueba en ambos temas filas y columnas, posiciones de pared y pasillo, que no queden `+` sin convertir y que el mapa original no cambie. Imprime `OK` o `FALLO` (código de salida 1).

### UTF-8 en Windows

```powershell
chcp 65001
```

Usa Windows Terminal, o en la consola clásica una fuente como Consolas o Cascadia Mono. Si la consola no usa UTF-8, `SalidaConsola` avisa y vuelve a ASCII. Compila siempre con `-encoding UTF-8`.
