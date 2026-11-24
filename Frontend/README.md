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
