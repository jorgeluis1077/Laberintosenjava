package frontend.gui;

import frontend.modelo.ServicioLaberinto;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

/** Panel lateral con la lista de laberintos de ejemplo (un clic carga). */
final class PanelEjemplos extends JPanel {

    private final ServicioLaberinto servicio;
    private final Consumer<Path> alElegir;
    private final Consumer<Boolean> alOcupar;
    private final DefaultListModel<Path> modeloValidos = new DefaultListModel<>();
    private final DefaultListModel<Path> modeloInvalidos = new DefaultListModel<>();
    private final JList<Path> listaValidos = crearLista(modeloValidos);
    private final JList<Path> listaInvalidos = crearLista(modeloInvalidos);
    private final JPanel centro = new JPanel(new BorderLayout());
    private final JScrollPane scrollValidos = new JScrollPane(listaValidos);
    private final JLabel sinCarpeta = new JLabel("No se encontró la carpeta de ejemplos", SwingConstants.CENTER);
    private final JPanel seccionInvalidos = new JPanel(new BorderLayout(0, 4));
    private final JButton cabeceraInvalidos = new JButton();
    private final JScrollPane scrollInvalidos = new JScrollPane(listaInvalidos);
    private final JButton btnActualizar = new BotonEstilo("Actualizar lista", true);

    private Path seleccion;
    private boolean suprimir;
    private boolean invalidosAbierta;

    PanelEjemplos(ServicioLaberinto servicio, Consumer<Path> alElegir, Consumer<Boolean> alOcupar) {
        this.servicio = servicio;
        this.alElegir = alElegir;
        this.alOcupar = alOcupar;
        setBackground(Tema.PANEL);
        setLayout(new BorderLayout(0, 10));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, Tema.BORDE), new EmptyBorder(12, 12, 12, 12)));
        setPreferredSize(new Dimension(230, 10));

        JLabel titulo = new JLabel("Laberintos");
        titulo.setFont(Tema.fuente(Font.BOLD, 15));
        add(titulo, BorderLayout.NORTH);

        centro.setOpaque(false);
        scrollValidos.setBorder(BorderFactory.createLineBorder(Tema.BORDE));
        sinCarpeta.setForeground(Tema.TEXTO_SEC);
        sinCarpeta.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(centro, BorderLayout.CENTER);

        cabeceraInvalidos.setContentAreaFilled(false);
        cabeceraInvalidos.setBorderPainted(false);
        cabeceraInvalidos.setFocusPainted(false);
        cabeceraInvalidos.setHorizontalAlignment(SwingConstants.LEFT);
        cabeceraInvalidos.setForeground(Tema.TEXTO_SEC);
        cabeceraInvalidos.setFont(Tema.fuente(Font.BOLD, 12));
        cabeceraInvalidos.addActionListener(e -> {
            invalidosAbierta = !invalidosAbierta;
            actualizarSeccionInvalidos();
        });
        scrollInvalidos.setBorder(BorderFactory.createLineBorder(Tema.BORDE));
        scrollInvalidos.setPreferredSize(new Dimension(10, 130));
        seccionInvalidos.setOpaque(false);
        seccionInvalidos.add(cabeceraInvalidos, BorderLayout.NORTH);
        seccionInvalidos.add(scrollInvalidos, BorderLayout.CENTER);

        JPanel sur = new JPanel(new BorderLayout(0, 8));
        sur.setOpaque(false);
        sur.add(seccionInvalidos, BorderLayout.CENTER);
        sur.add(btnActualizar, BorderLayout.SOUTH);
        add(sur, BorderLayout.SOUTH);

        btnActualizar.addActionListener(e -> actualizar());
        conectar(listaValidos, listaInvalidos);
        conectar(listaInvalidos, listaValidos);
    }

    private JList<Path> crearLista(DefaultListModel<Path> modelo) {
        JList<Path> l = new JList<>(modelo);
        l.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        l.setBackground(Tema.PANEL2);
        l.setForeground(Tema.TEXTO);
        l.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> lista, Object valor, int i,
                                                          boolean sel, boolean foco) {
                super.getListCellRendererComponent(lista, valor, i, sel, false);
                String n = ((Path) valor).getFileName().toString();
                int punto = n.lastIndexOf('.');
                setText(punto > 0 ? n.substring(0, punto) : n);
                setBorder(new EmptyBorder(6, 10, 6, 10));
                return this;
            }
        });
        return l;
    }

    private void conectar(JList<Path> lista, JList<Path> otra) {
        lista.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || suprimir || lista.getSelectedValue() == null) {
                return;
            }
            suprimir = true;
            otra.clearSelection();
            suprimir = false;
            alElegir.accept(lista.getSelectedValue());
        });
    }

    /** Vuelve a leer la carpeta y conserva la seleccion si el archivo sigue en la lista. */
    void actualizar() {
        alOcupar.accept(true);
        btnActualizar.setEnabled(false);
        new SwingWorker<Catalogo, Void>() {
            @Override
            protected Catalogo doInBackground() {
                return new Catalogo(servicio.listarEjemplos(), servicio.listarEjemplosInvalidos(),
                        servicio.carpetaEjemplos().isPresent());
            }

            @Override
            protected void done() {
                try {
                    aplicar(get());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    sinCarpeta.setText("No se pudo listar los ejemplos: " + e.getCause().getMessage());
                    centro.removeAll();
                    centro.add(sinCarpeta, BorderLayout.NORTH);
                    centro.revalidate();
                    centro.repaint();
                } finally {
                    alOcupar.accept(false);
                }
            }
        }.execute();
    }

    private record Catalogo(List<Path> validos, List<Path> invalidos, boolean existeCarpeta) {}

    private void aplicar(Catalogo catalogo) {
        suprimir = true;
        modeloValidos.clear();
        modeloInvalidos.clear();
        catalogo.validos().forEach(modeloValidos::addElement);
        catalogo.invalidos().forEach(modeloInvalidos::addElement);
        centro.removeAll();
        if (catalogo.existeCarpeta()) {
            centro.add(scrollValidos, BorderLayout.CENTER);
        } else {
            centro.add(sinCarpeta, BorderLayout.NORTH);
        }
        centro.revalidate();
        centro.repaint();
        seccionInvalidos.setVisible(!modeloInvalidos.isEmpty());
        actualizarSeccionInvalidos();
        suprimir = false;
        seleccionar(seleccion);
    }

    private void actualizarSeccionInvalidos() {
        cabeceraInvalidos.setText((invalidosAbierta ? "[-] " : "[+] ") + "Ejemplos inválidos ("
                + modeloInvalidos.size() + ")");
        scrollInvalidos.setVisible(invalidosAbierta);
        seccionInvalidos.revalidate();
        revalidate();
    }

    /** Marca el archivo indicado (sin disparar la carga); null limpia la seleccion. */
    void seleccionar(Path p) {
        seleccion = p;
        suprimir = true;
        listaValidos.clearSelection();
        listaInvalidos.clearSelection();
        if (p != null) {
            elegirEn(listaValidos, modeloValidos, p);
            elegirEn(listaInvalidos, modeloInvalidos, p);
        }
        suprimir = false;
    }

    private static void elegirEn(JList<Path> lista, DefaultListModel<Path> modelo, Path p) {
        for (int i = 0; i < modelo.size(); i++) {
            if (mismo(modelo.get(i), p)) {
                lista.setSelectedIndex(i);
                lista.ensureIndexIsVisible(i);
                return;
            }
        }
    }

    private static boolean mismo(Path a, Path b) {
        try {
            return Files.isSameFile(a, b);
        } catch (IOException e) {
            return a.toAbsolutePath().normalize().equals(b.toAbsolutePath().normalize());
        }
    }

    void setHabilitado(boolean habilitado) {
        listaValidos.setEnabled(habilitado);
        listaInvalidos.setEnabled(habilitado);
        btnActualizar.setEnabled(habilitado);
        cabeceraInvalidos.setEnabled(habilitado);
    }
}
