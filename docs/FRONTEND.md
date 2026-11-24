# Frontend (interfaz gráfica con Swing)

Interfaz gráfica del proyecto de laberintos. Es un extra independiente: **no depende** de las clases
del resto del repositorio (`Grafo`, `Vertice`, etc.), así que compila y se ejecuta sola.

Por ahora usa `ServicioDemo`, una implementación con datos de demostración (búsquedas propias y
simplificadas). Sirve únicamente para ver y probar la GUI; se sustituirá por un adaptador real.

Requiere JDK 17 o superior, sin librerías externas.

## Estructura

```
Frontend/src/frontend/
  modelo/   tipos simples y la interfaz que usa la GUI
    Celda, DatosLaberinto, ResultadoVista, ErrorCargaException
    ServicioLaberinto   (interfaz: único contacto entre GUI y lógica)
    ServicioDemo        (SOLO demostración)
  gui/      Swing: MainSwing, VentanaLaberinto, PanelLaberinto y auxiliares
```

La GUI solo conoce `ServicioLaberinto`.

## Compilar y ejecutar

Desde la raíz del repositorio (así encuentra la carpeta `MapaLaberintos`):

```
javac -encoding UTF-8 -d out Frontend\src\frontend\modelo\*.java Frontend\src\frontend\gui\*.java
java -cp out frontend.gui.MainSwing
```

Conviene compilar en una carpeta temporal (por ejemplo `-d %TEMP%\lab_out` y luego
`java -cp %TEMP%\lab_out frontend.gui.MainSwing`) para no dejar archivos `.class` en el repo.

## Carpeta de ejemplos

La lista lateral busca, relativas al directorio de ejecución, las carpetas `ejemplos` y luego
`MapaLaberintos` (constante `ServicioDemo.CARPETAS_EJEMPLOS`). Si existe una subcarpeta `invalidos/`,
aparece como sección colapsada "Ejemplos inválidos".

## Formato del mapa

Cada carácter del archivo es una celda: `+ - | #` son paredes, el espacio es transitable y `E` / `S`
marcan entrada y salida. Si el archivo no trae `E` ni `S`, la entrada y la salida son la primera y la
última abertura del borde. La longitud del camino se cuenta en movimientos entre celdas del mapa.

## Cómo conectar con la base real

Crear un adaptador (por ejemplo `frontend.modelo.ServicioBase`) que implemente `ServicioLaberinto`
y cambiar una línea en `MainSwing`: `new VentanaLaberinto(new ServicioBase())`.

Métodos a implementar:

| Método | Qué debe hacer el adaptador |
|---|---|
| `cargar(Path)` | Llamar al cargador de la base, convertir el resultado a `DatosLaberinto` (mapa de caracteres, entrada, salida) y lanzar `ErrorCargaException` con un mensaje legible si falla. Si falla debe conservar el laberinto anterior. |
| `resolverDFS()` / `resolverBFS()` / `resolverDijkstra()` | Ejecutar el algoritmo sobre el laberinto cargado y devolver un `ResultadoVista`: `visitados` en orden de procesamiento, `camino` de entrada a salida (vacío si no hay), `encontrado` y `tiempoNanos`. Convertir cada vértice a `Celda(fila, columna)` de la matriz de caracteres. Se llaman en un hilo de fondo (`SwingWorker`). |
| `reiniciarResultados()` | Limpiar cualquier estado guardado de ejecuciones anteriores. |
| `carpetaEjemplos()`, `listarEjemplos()`, `listarEjemplosInvalidos()` | Devolver la carpeta y los `.txt` en orden natural; se puede reutilizar la lógica de `ServicioDemo`. |

Notas para el adaptador:

- Las coordenadas deben ser las de la matriz de caracteres que se dibuja. Si la base trabaja con
  vértices lógicos (una celda cada dos caracteres), hay que convertirlos, y los pasos intermedios
  del camino también son celdas del mapa.
- La GUI no inventa movimientos entre posiciones no conectadas: solo marca lo visitado y el actual.
- Si no hay laberinto cargado, los métodos `resolver*` deben lanzar `IllegalStateException`.

## Caracteres alternativos (consola)

Extra de presentación: la consola puede dibujar el laberinto con símbolos distintos. **Solo cambia lo
que se imprime**: el archivo sigue usando `+ - | espacio # E S`, y no se toca el mapa ni el grafo.
Cada posición sigue siendo un solo carácter (mismas filas y columnas); la conversión ocurre al imprimir.

| Elemento | ASCII (por defecto) | Unicode |
|---|---|---|
| Paredes | `+ - \|` | `─ │ ┌ ┐ └ ┘ ├ ┤ ┬ ┴ ┼` (cada `+` según sus vecinos) |
| Transitable | espacio | espacio |
| Entrada | `E` | `▶` |
| Salida | `S` | `◆` |
| Visitado | `.` | `·` |
| Vértice actual | `@` | `◉` |
| Camino final | `*` | `●` |

Las `#` se conservan en ambos temas. La entrada y la salida siempre quedan visibles.

Clases (paquete `frontend.consola`): `TemaConsola` (símbolos), `RenderConsola` (arma el texto y la
leyenda), `SalidaConsola` (UTF-8 y respaldo a ASCII), `DemoConsola` y `PruebaTema` (`main`).

### Compilar y ejecutar

Desde la raíz del repositorio, en una carpeta temporal para no dejar `.class` en el repo:

```
javac -encoding UTF-8 -d %TEMP%\lab_con Frontend\src\frontend\modelo\*.java Frontend\src\frontend\consola\*.java
java -cp %TEMP%\lab_con frontend.consola.DemoConsola
java -cp %TEMP%\lab_con frontend.consola.DemoConsola --unicode
java -cp %TEMP%\lab_con frontend.consola.PruebaTema
```

- `DemoConsola` (solo demostración, usa `ServicioDemo`) pregunta el estilo, carga un ejemplo y muestra la
  exploración con ENTER; `f` salta al final. Acepta `--unicode` y, opcionalmente, la ruta de un `.txt`.
- `PruebaTema` recorre los ejemplos válidos y comprueba filas y columnas, posiciones de pared y
  transitable, y que no queden `+` sin convertir. Imprime `OK` o `FALLO` (código de salida 1).

### UTF-8 en Windows

Para ver el tema Unicode hay que usar UTF-8 en la consola y una fuente que tenga los símbolos:

```
chcp 65001
```

Usa Windows Terminal, o en la consola clásica una fuente como "Consolas" o "Cascadia Mono". Si la
consola no usa UTF-8, `SalidaConsola` avisa y vuelve a ASCII. Compila siempre con `-encoding UTF-8`.

### Cómo conectar con la base real

1. Al arrancar el menú, llamar a `SalidaConsola.configurar(TemaConsola.ASCII)` (o `UNICODE`); devuelve
   el tema efectivo, que puede ser ASCII si la consola no admite Unicode. **Aquí se cambia el tema**
   (por ejemplo con una opción de menú o el argumento `--unicode`).
2. Donde el menú imprime el laberinto, reemplazar el dibujo por
   `RenderConsola.vista(datos, resultado, pasos, mostrarCamino, tema)` y luego
   `RenderConsola.leyenda(tema)`:
   - `datos`: un `DatosLaberinto` (convertir el mapa de la base a `char[][]`, entrada y salida).
   - `resultado`: un `ResultadoVista` con `visitados` y `camino` (o `null` si solo se muestra el mapa).
   - `pasos`: cuántos visitados mostrar; el último es el vértice actual. Para mostrar todo, `visitados.size()`.
   - `mostrarCamino`: `true` solo al final y si hay solución.
3. Si no hay solución, mostrar la exploración completa y el mensaje "No existe camino entre la
   entrada y la salida", sin longitud.
4. Las coordenadas de `Celda` son las de la matriz de caracteres, igual que en la GUI.
