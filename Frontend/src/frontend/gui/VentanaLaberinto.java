package frontend.gui;

import frontend.modelo.DatosLaberinto;
import frontend.modelo.ErrorCargaException;
import frontend.modelo.ResultadoVista;
import frontend.modelo.ServicioLaberinto;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

/** Ventana principal. Solo conoce ServicioLaberinto, nunca las clases de la base. */
public class VentanaLaberinto extends JFrame {

    private static final String[] ALGORITMOS = {"DFS", "BFS", "Dijkstra"};
    private static final String TIP_SIN_LABERINTO = "Primero carga un laberinto";
    private static final String TIP_OCUPADO = "Espera a que termine el cálculo";
    private static final int ZOOM_MIN = 8;
    private static final int ZOOM_MAX = 64;
    private static final int CELDA_MIN_AJUSTE = 24;

    private final ServicioLaberinto servicio;
    private final Map<String, ResultadoVista> resultados = new LinkedHashMap<>();
    private final Animador animador;

    private DatosLaberinto datos;
    private Path archivoActual;
    private ResultadoVista actual;
    private int paso;
    private boolean ocupado;

    private final BotonEstilo btnCargar = new BotonEstilo("Cargar laberinto");
    private final BotonEstilo btnComparar = new BotonEstilo("Comparar los tres");
    private final BotonEstilo btnTiempos = new BotonEstilo("Tiempos");
    private final BotonEstilo btnSalir = new BotonEstilo("Salir");
    private final Map<String, BotonEstilo> btnAlgoritmo = new LinkedHashMap<>();

    private final PanelEjemplos panelEjemplos;
    private final PanelResultados panelResultados = new PanelResultados(ALGORITMOS);
    private final PanelLaberinto panelLab = new PanelLaberinto();
    private final JScrollPane scroll = new JScrollPane(panelLab);
    private final VistaComparar vistaComparar;
    private final CardLayout cartas = new CardLayout();
    private final JPanel centro = new JPanel(cartas);

    private final JLabel lblTitulo = new JLabel(" ");
    private final JLabel lblAviso = new JLabel(" ");
    private final JLabel lblEstado = new JLabel("Listo");
    private final JProgressBar progreso = new JProgressBar(0, 1);
    private final BotonEstilo btnPausa = new BotonEstilo("Pausar", true);
    private final BotonEstilo btnSiguiente = new BotonEstilo("Siguiente paso", true);
    private final BotonEstilo btnFinal = new BotonEstilo("Saltar al final", true);
    private final BotonEstilo btnAjustar = new BotonEstilo("Ajustar", true);
    private final JSlider sliderVel = new JSlider(1, 10, 6);
    private final JSlider sliderZoom = new JSlider(ZOOM_MIN, ZOOM_MAX, 24);

    public VentanaLaberinto(ServicioLaberinto servicio) {
        super("Laberintos - Interfaz gráfica");
        this.servicio = servicio;
        this.animador = new Animador(this::alPaso, this::alTerminarAnimacion);
        animador.setVelocidad(sliderVel.getValue());
        this.panelEjemplos = new PanelEjemplos(servicio, this::cargar);
        this.vistaComparar = new VistaComparar(ALGORITMOS, () -> cartas.show(centro, "lab"));

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(Tema.PANEL);
        setLayout(new BorderLayout());
        add(crearBarra(), BorderLayout.NORTH);
        add(panelEjemplos, BorderLayout.WEST);
        add(crearCentro(), BorderLayout.CENTER);
        add(panelResultados, BorderLayout.EAST);
        add(crearBarraEstado(), BorderLayout.SOUTH);

        conectarAcciones();
        actualizarControles();
        setSize(1280, 780);
        setMinimumSize(new Dimension(1100, 680));
        setLocationRelativeTo(null);
    }

    // ---------- Construccion de la interfaz ----------

    private JPanel crearBarra() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(Tema.PANEL);
        barra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Tema.BORDE), new EmptyBorder(8, 12, 8, 12)));

        JPanel grupos = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        grupos.setOpaque(false);
        grupos.add(btnCargar);
        grupos.add(separador());
        JLabel resolver = new JLabel("Resolver:");
        resolver.setForeground(Tema.TEXTO_SEC);
        grupos.add(resolver);
        for (String nombre : ALGORITMOS) {
            BotonEstilo b = new BotonEstilo(nombre);
            btnAlgoritmo.put(nombre, b);
            grupos.add(b);
        }
        grupos.add(separador());
        grupos.add(btnComparar);
        grupos.add(btnTiempos);
        barra.add(grupos, BorderLayout.WEST);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        derecha.setOpaque(false);
        derecha.add(btnSalir);
        barra.add(derecha, BorderLayout.EAST);
        return barra;
    }

    private static JSeparator separador() {
        JSeparator s = new JSeparator(SwingConstants.VERTICAL);
        s.setForeground(Tema.BORDE);
        s.setPreferredSize(new Dimension(2, 28));
        return s;
    }

    private JPanel crearCentro() {
        centro.setBackground(Tema.PANEL);
        centro.add(crearVacio(), "vacio");
        centro.add(crearVistaLaberinto(), "lab");
        centro.add(vistaComparar, "comparar");
        cartas.show(centro, "vacio");
        return centro;
    }

    private JPanel crearVacio() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Tema.PANEL);
        JLabel msg = new JLabel("Carga un laberinto para comenzar");
        msg.setFont(Tema.fuente(Font.BOLD, 28));
        JLabel sub = new JLabel("Elige uno de la lista o abre un archivo .txt");
        sub.setForeground(Tema.TEXTO_SEC);
        sub.setFont(Tema.fuente(Font.PLAIN, 14));
        BotonEstilo boton = new BotonEstilo("Cargar laberinto");
        boton.addActionListener(e -> elegirArchivo());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.insets = new java.awt.Insets(6, 0, 6, 0);
        p.add(msg, c);
        p.add(sub, c);
        c.insets = new java.awt.Insets(18, 0, 0, 0);
        p.add(boton, c);
        return p;
    }

    private JPanel crearVistaLaberinto() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(Tema.PANEL);
        p.setBorder(new EmptyBorder(12, 12, 8, 12));

        lblTitulo.setFont(Tema.fuente(Font.BOLD, 17));
        lblAviso.setFont(Tema.fuente(Font.BOLD, 13));
        lblAviso.setForeground(Tema.ACTUAL);
        progreso.setBorderPainted(false);
        progreso.setPreferredSize(new Dimension(10, 8));
        progreso.setMaximumSize(new Dimension(Integer.MAX_VALUE, 8));
        JPanel norte = new JPanel();
        norte.setOpaque(false);
        norte.setLayout(new BoxLayout(norte, BoxLayout.Y_AXIS));
        for (java.awt.Component c : new java.awt.Component[]{lblTitulo, Box.createVerticalStrut(6), progreso,
                Box.createVerticalStrut(4), lblAviso}) {
            ((javax.swing.JComponent) c).setAlignmentX(LEFT_ALIGNMENT);
            norte.add(c);
        }
        p.add(norte, BorderLayout.NORTH);

        scroll.setBorder(BorderFactory.createLineBorder(Tema.BORDE));
        scroll.getVerticalScrollBar().setUnitIncrement(24);
        scroll.getHorizontalScrollBar().setUnitIncrement(24);
        p.add(scroll, BorderLayout.CENTER);

        JPanel filaAnim = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        filaAnim.setOpaque(false);
        filaAnim.add(btnPausa);
        filaAnim.add(btnSiguiente);
        filaAnim.add(btnFinal);
        filaAnim.add(etiqueta("Velocidad"));
        sliderVel.setPreferredSize(new Dimension(120, 24));
        filaAnim.add(sliderVel);

        JPanel filaZoom = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        filaZoom.setOpaque(false);
        filaZoom.add(etiqueta("Zoom"));
        sliderZoom.setPreferredSize(new Dimension(180, 24));
        filaZoom.add(sliderZoom);
        filaZoom.add(btnAjustar);

        JPanel sur = new JPanel();
        sur.setOpaque(false);
        sur.setLayout(new BoxLayout(sur, BoxLayout.Y_AXIS));
        for (JPanel f : new JPanel[]{filaAnim, filaZoom, new Leyenda()}) {
            f.setAlignmentX(LEFT_ALIGNMENT);
            sur.add(f);
        }
        p.add(sur, BorderLayout.SOUTH);
        return p;
    }

    private static JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setForeground(Tema.TEXTO_SEC);
        return l;
    }

    private JPanel crearBarraEstado() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Tema.PANEL);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Tema.BORDE), new EmptyBorder(6, 12, 6, 12)));
        lblEstado.setForeground(Tema.TEXTO_SEC);
        p.add(lblEstado, BorderLayout.WEST);
        return p;
    }

    private void conectarAcciones() {
        btnCargar.addActionListener(e -> elegirArchivo());
        btnSalir.addActionListener(e -> {
            animador.detener();
            dispose();
            System.exit(0);
        });
        for (Map.Entry<String, BotonEstilo> e : btnAlgoritmo.entrySet()) {
            e.getValue().addActionListener(ev -> resolver(e.getKey()));
        }
        btnComparar.addActionListener(e -> compararTodos());
        btnTiempos.addActionListener(e -> mostrarTiempos());

        btnPausa.addActionListener(e -> {
            if (animador.corriendo()) {
                animador.pausar();
                btnPausa.setText("Continuar");
            } else {
                animador.continuar();
                btnPausa.setText("Pausar");
            }
        });
        btnSiguiente.addActionListener(e -> {
            animador.siguiente();
            btnPausa.setText("Continuar");
            actualizarControles();
        });
        btnFinal.addActionListener(e -> animador.saltarAlFinal());
        sliderVel.addChangeListener(e -> animador.setVelocidad(sliderVel.getValue()));
        sliderZoom.addChangeListener(e -> panelLab.setTam(sliderZoom.getValue()));
        btnAjustar.addActionListener(e -> ajustarZoom());
    }

    // ---------- Carga ----------

    private void elegirArchivo() {
        if (ocupado) {
            return;
        }
        JFileChooser fc = new JFileChooser(servicio.carpetaEjemplos().map(Path::toFile).orElse(new File(".")));
        fc.setFileFilter(new FileNameExtensionFilter("Laberintos (*.txt)", "txt"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            cargar(fc.getSelectedFile().toPath());
        }
    }

    private void cargar(Path archivo) {
        if (ocupado) {
            panelEjemplos.seleccionar(archivoActual);
            return;
        }
        try {
            DatosLaberinto nuevo = servicio.cargar(archivo);
            animador.detener();
            servicio.reiniciarResultados();
            resultados.clear();
            panelResultados.limpiar();
            datos = nuevo;
            archivoActual = archivo;
            actual = null;
            paso = 0;
            btnPausa.setText("Pausar");
            refrescarVista();
            cartas.show(centro, "lab");
            panelEjemplos.seleccionar(archivo);
            lblEstado.setText("Cargado: " + nombre(archivo) + " (" + datos.filas() + "x" + datos.columnas() + ")");
            actualizarControles();
            SwingUtilities.invokeLater(this::ajustarZoom);
        } catch (ErrorCargaException ex) {
            panelEjemplos.seleccionar(archivoActual);
            lblEstado.setText("No se pudo cargar: " + nombre(archivo));
            JOptionPane.showMessageDialog(this, ex.getMessage(), "No se pudo cargar el laberinto",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String nombre(Path p) {
        return p == null ? "" : p.getFileName().toString();
    }

    private void ajustarZoom() {
        if (datos == null) {
            return;
        }
        Dimension ext = scroll.getViewport().getExtentSize();
        int tam = CELDA_MIN_AJUSTE;
        if (ext.width > 0 && ext.height > 0) {
            int ajuste = Math.min((ext.width - 2 * PanelLaberinto.MARGEN) / datos.columnas(),
                    (ext.height - 2 * PanelLaberinto.MARGEN) / datos.filas());
            tam = Math.max(CELDA_MIN_AJUSTE, Math.min(ZOOM_MAX, ajuste));
        }
        sliderZoom.setValue(tam);
        panelLab.setTam(tam);
        scroll.getViewport().setViewPosition(new Point(0, 0));
    }

    // ---------- Resolver ----------

    private ResultadoVista ejecutar(String algoritmo) {
        return switch (algoritmo) {
            case "DFS" -> servicio.resolverDFS();
            case "BFS" -> servicio.resolverBFS();
            default -> servicio.resolverDijkstra();
        };
    }

    private void resolver(String algoritmo) {
        if (datos == null || ocupado) {
            return;
        }
        animador.detener();
        cartas.show(centro, "lab");
        ocupado = true;
        lblEstado.setText("Resolviendo con " + algoritmo + "...");
        actualizarControles();
        new SwingWorker<ResultadoVista, Void>() {
            @Override
            protected ResultadoVista doInBackground() {
                return ejecutar(algoritmo);
            }

            @Override
            protected void done() {
                ocupado = false;
                try {
                    ResultadoVista r = get();
                    resultados.put(algoritmo, r);
                    panelResultados.actualizar(datos, resultados);
                    actual = r;
                    btnPausa.setText("Pausar");
                    animador.iniciar(r.visitados().size());
                    lblEstado.setText("Animando " + algoritmo + ": " + r.visitados().size() + " visitados");
                } catch (InterruptedException | ExecutionException ex) {
                    errorAlResolver(ex);
                }
                actualizarControles();
            }
        }.execute();
    }

    private void compararTodos() {
        if (datos == null || ocupado) {
            return;
        }
        animador.detener();
        ocupado = true;
        lblEstado.setText("Resolviendo con los tres algoritmos...");
        actualizarControles();
        new SwingWorker<List<ResultadoVista>, Void>() {
            @Override
            protected List<ResultadoVista> doInBackground() {
                List<ResultadoVista> lista = new ArrayList<>();
                for (String a : ALGORITMOS) {
                    lista.add(ejecutar(a));
                }
                return lista;
            }

            @Override
            protected void done() {
                ocupado = false;
                try {
                    for (ResultadoVista r : get()) {
                        resultados.put(r.algoritmo(), r);
                    }
                    panelResultados.actualizar(datos, resultados);
                    vistaComparar.mostrar(datos, resultados);
                    cartas.show(centro, "comparar");
                    lblEstado.setText("Comparación lista");
                } catch (InterruptedException | ExecutionException ex) {
                    errorAlResolver(ex);
                }
                actualizarControles();
            }
        }.execute();
    }

    private void errorAlResolver(Exception ex) {
        Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
        lblEstado.setText("Error al resolver");
        JOptionPane.showMessageDialog(this, "No se pudo resolver el laberinto:\n" + causa.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarTiempos() {
        if (resultados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Aún no hay resultados. Resuelve el laberinto con algún algoritmo.",
                    "Tiempos", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String[] columnas = {"Algoritmo", "Visitados", "Longitud", "Tiempo (ns)", "Tiempo (ms)"};
        List<Object[]> filas = new ArrayList<>();
        for (String a : ALGORITMOS) {
            ResultadoVista r = resultados.get(a);
            if (r != null) {
                filas.add(new Object[]{a, r.visitados().size(), r.encontrado() ? r.longitud() : "Sin camino",
                        r.tiempoNanos(), String.format(java.util.Locale.ROOT, "%.3f", r.tiempoNanos() / 1e6)});
            }
        }
        JTable tabla = new JTable(filas.toArray(new Object[0][]), columnas) {
            @Override
            public boolean isCellEditable(int f, int c) {
                return false;
            }
        };
        tabla.setRowHeight(26);
        tabla.setFillsViewportHeight(true);
        JScrollPane sp = new JScrollPane(tabla);
        sp.setPreferredSize(new Dimension(520, 40 + 26 * filas.size() + 4));
        JOptionPane.showMessageDialog(this, sp, "Tiempos", JOptionPane.PLAIN_MESSAGE);
    }

    // ---------- Animacion y estado visual ----------

    private void alPaso(int p) {
        paso = p;
        refrescarVista();
    }

    private void alTerminarAnimacion() {
        if (actual != null) {
            lblEstado.setText(actual.encontrado()
                    ? actual.algoritmo() + ": camino de " + actual.longitud() + " movimientos"
                    : actual.algoritmo() + ": sin camino");
        }
        actualizarControles();
    }

    private void refrescarVista() {
        if (datos == null) {
            return;
        }
        int total = actual == null ? 0 : actual.visitados().size();
        boolean fin = actual != null && paso >= total;
        panelLab.setEstado(EstadoVista.desde(datos, actual, paso, fin && actual.encontrado()));
        if (actual == null) {
            lblTitulo.setText("Laberinto: " + datos.nombreArchivo() + " (" + datos.filas() + "x" + datos.columnas() + ")");
            progreso.setMaximum(1);
            progreso.setValue(0);
        } else {
            lblTitulo.setText("Algoritmo: " + actual.algoritmo() + " · Paso " + paso + " de " + total);
            progreso.setMaximum(Math.max(1, total));
            progreso.setValue(paso);
        }
        lblAviso.setText(fin && !actual.encontrado() ? "No existe camino entre la entrada y la salida" : " ");
    }

    private void actualizarControles() {
        boolean hay = datos != null;
        boolean libre = hay && !ocupado;
        String tip = !hay ? TIP_SIN_LABERINTO : (ocupado ? TIP_OCUPADO : null);
        for (BotonEstilo b : btnAlgoritmo.values()) {
            habilitar(b, libre, tip);
        }
        habilitar(btnComparar, libre, tip);
        habilitar(btnCargar, !ocupado, TIP_OCUPADO);
        panelEjemplos.setHabilitado(!ocupado);

        boolean enCurso = actual != null && !animador.terminada();
        btnPausa.setEnabled(enCurso);
        btnSiguiente.setEnabled(enCurso);
        btnFinal.setEnabled(enCurso);
        btnAjustar.setEnabled(hay);
        sliderZoom.setEnabled(hay);
        if (!enCurso) {
            btnPausa.setText("Pausar");
        }
    }

    private static void habilitar(BotonEstilo b, boolean on, String tipSiNo) {
        b.setEnabled(on);
        b.setToolTipText(on ? null : tipSiNo);
    }
}
