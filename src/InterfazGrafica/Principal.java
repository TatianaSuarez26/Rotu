package InterfazGrafica;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class Principal extends JFrame {

    private static final Color DARK_GREEN   = new Color(27, 94, 60);
    private static final Color LIGHT_GREEN  = new Color(220, 245, 225);
    private static final Color CARD_BG      = new Color(247, 247, 247);
    private static final Color TEXT_DARK    = new Color(20, 20, 20);
    private static final Color TEXT_GRAY    = new Color(110, 110, 110);
    private static final Color BORDER_COLOR = new Color(220, 220, 220);
    private static final Color SIDEBAR_BG   = new Color(22, 78, 50);

    public Principal() {
        setTitle("SIRUM");
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.add(crearTopBar(),    BorderLayout.NORTH);
        root.add(crearSidebar(),   BorderLayout.WEST);
        root.add(crearContenido(), BorderLayout.CENTER);

        setContentPane(root);
    }

    // ── BARRA SUPERIOR ────────────────────────────────────────────────────────
    private JPanel crearTopBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(DARK_GREEN);
        panel.setPreferredSize(new Dimension(1100, 55));
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel titulo = new JLabel("ROTU");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Arial", Font.BOLD, 22));

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        derecha.setBackground(DARK_GREEN);

        JLabel campana = new JLabel("🔔");
        JLabel usuario = new JLabel("👤");
        campana.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        usuario.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        campana.setForeground(Color.WHITE);
        usuario.setForeground(Color.WHITE);
        derecha.add(campana);
        derecha.add(usuario);

        panel.add(titulo,  BorderLayout.WEST);
        panel.add(derecha, BorderLayout.EAST);
        return panel;
    }

    // ── BARRA LATERAL ─────────────────────────────────────────────────────────
    private JPanel crearSidebar() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(SIDEBAR_BG);
        panel.setPreferredSize(new Dimension(200, 700));
        panel.setBorder(new EmptyBorder(30, 0, 30, 0));

        panel.add(crearItemSidebar("🏠",  "Inicio",    true));
        panel.add(Box.createVerticalStrut(6));
        panel.add(crearItemSidebar("⇄",   "Rutas",     false));
        panel.add(Box.createVerticalStrut(6));
        panel.add(crearItemSidebar("📍",  "Mapa",      false));
        panel.add(Box.createVerticalStrut(6));
        panel.add(crearItemSidebar("👤",  "Perfil",    false));

        return panel;
    }

    private JPanel crearItemSidebar(String icono, String etiqueta, boolean activo) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 10));
        panel.setMaximumSize(new Dimension(200, 48));
        panel.setBackground(activo ? DARK_GREEN : SIDEBAR_BG);
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel ico = new JLabel(icono);
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        ico.setForeground(Color.WHITE);

        JLabel txt = new JLabel(etiqueta);
        txt.setFont(new Font("Arial", activo ? Font.BOLD : Font.PLAIN, 13));
        txt.setForeground(Color.WHITE);

        panel.add(ico);
        panel.add(txt);

        if (etiqueta.equals("Rutas")) {
            panel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    new Rutas().setVisible(true);
                    dispose();
                }
            });
        }

        return panel;
    }

    // ── CONTENIDO PRINCIPAL ───────────────────────────────────────────────────
    private JScrollPane crearContenido() {
        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(Color.WHITE);

        contenido.add(crearEncabezado());
        contenido.add(crearBuscador());
        contenido.add(crearAccesosRapidos());
        contenido.add(crearRutasRecientes());

        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        return scroll;
    }

    // ── ENCABEZADO VERDE ──────────────────────────────────────────────────────
    private JPanel crearEncabezado() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(LIGHT_GREEN);
        panel.setBorder(new EmptyBorder(28, 32, 28, 32));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JLabel bienvenido = new JLabel("BIENVENIDO");
        bienvenido.setFont(new Font("Arial", Font.PLAIN, 11));
        bienvenido.setForeground(DARK_GREEN);
        bienvenido.setAlignmentX(LEFT_ALIGNMENT);

        JLabel pregunta = new JLabel("¿A dónde vas hoy?");
        pregunta.setFont(new Font("Arial", Font.BOLD, 26));
        pregunta.setForeground(TEXT_DARK);
        pregunta.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subtitulo = new JLabel("Encuentra la ruta más eficiente");
        subtitulo.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitulo.setForeground(DARK_GREEN);
        subtitulo.setAlignmentX(LEFT_ALIGNMENT);

        panel.add(bienvenido);
        panel.add(Box.createVerticalStrut(5));
        panel.add(pregunta);
        panel.add(Box.createVerticalStrut(4));
        panel.add(subtitulo);
        return panel;
    }

    // ── BUSCADOR ──────────────────────────────────────────────────────────────
    private JPanel crearBuscador() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(new EmptyBorder(20, 32, 10, 32));
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

        JPanel caja = new JPanel(new BorderLayout());
        caja.setBackground(Color.WHITE);
        caja.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(10, 14, 10, 14)
        ));

        JLabel lupa = new JLabel("🔍  ");
        lupa.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));

        JTextField campo = new JTextField("Buscar estación o destino...");
        campo.setBorder(null);
        campo.setFont(new Font("Arial", Font.PLAIN, 14));
        campo.setForeground(Color.GRAY);

        caja.add(lupa,  BorderLayout.WEST);
        caja.add(campo, BorderLayout.CENTER);
        wrapper.add(caja);
        return wrapper;
    }

    // ── ACCESOS RÁPIDOS ───────────────────────────────────────────────────────
    private JPanel crearAccesosRapidos() {
        JPanel outer = new JPanel();
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        outer.setBackground(Color.WHITE);
        outer.setBorder(new EmptyBorder(10, 32, 10, 32));

        JLabel titulo = new JLabel("ACCESOS RÁPIDOS");
        titulo.setFont(new Font("Arial", Font.BOLD, 12));
        titulo.setForeground(TEXT_DARK);
        titulo.setAlignmentX(LEFT_ALIGNMENT);
        outer.add(titulo);
        outer.add(Box.createVerticalStrut(12));

        JPanel grid = new JPanel(new GridLayout(1, 4, 14, 0));
        grid.setBackground(Color.WHITE);
        grid.setAlignmentX(LEFT_ALIGNMENT);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        grid.add(crearTarjeta("⇄",  "Buscar ruta",    "Origen a destino"));
        grid.add(crearTarjeta("📍", "Estaciones",     "Ver todas las paradas"));
        grid.add(crearTarjeta("🕓", "Tiempos en vivo","Estado del sistema"));
        grid.add(crearTarjeta("☆",  "Mis rutas",      "Rutas guardadas"));

        outer.add(grid);
        return outer;
    }

    private JPanel crearTarjeta(String icono, String titulo, String subtitulo) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(18, 18, 18, 18)
        ));

        JLabel ico = new JLabel(icono);
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        ico.setForeground(DARK_GREEN);
        ico.setAlignmentX(LEFT_ALIGNMENT);

        JLabel tit = new JLabel(titulo);
        tit.setFont(new Font("Arial", Font.BOLD, 14));
        tit.setForeground(TEXT_DARK);
        tit.setAlignmentX(LEFT_ALIGNMENT);

        JLabel sub = new JLabel(subtitulo);
        sub.setFont(new Font("Arial", Font.PLAIN, 12));
        sub.setForeground(TEXT_GRAY);
        sub.setAlignmentX(LEFT_ALIGNMENT);

        card.add(ico);
        card.add(Box.createVerticalStrut(10));
        card.add(tit);
        card.add(Box.createVerticalStrut(3));
        card.add(sub);
        return card;
    }

    // ── RUTAS RECIENTES ───────────────────────────────────────────────────────
    private JPanel crearRutasRecientes() {
        JPanel outer = new JPanel();
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        outer.setBackground(Color.WHITE);
        outer.setBorder(new EmptyBorder(20, 32, 32, 32));

        JLabel titulo = new JLabel("RUTAS RECIENTES");
        titulo.setFont(new Font("Arial", Font.BOLD, 12));
        titulo.setForeground(TEXT_DARK);
        titulo.setAlignmentX(LEFT_ALIGNMENT);
        outer.add(titulo);
        outer.add(Box.createVerticalStrut(10));

        outer.add(crearItemRuta("Portal Norte → Centro",  "3 paradas · Línea verde", "12 min"));
        outer.add(new JSeparator());
        outer.add(crearItemRuta("Calle 100 → Soacha",     "7 paradas · Línea azul",  "28 min"));
        outer.add(new JSeparator());
        outer.add(crearItemRuta("Usaquén → Av. Jiménez",  "5 paradas · Línea verde", "19 min"));

        return outer;
    }

    private JPanel crearItemRuta(String ruta, String detalles, String tiempo) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(12, 0, 12, 0));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        panel.setAlignmentX(LEFT_ALIGNMENT);

        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        izq.setBackground(Color.WHITE);

        JLabel punto = new JLabel("●");
        punto.setForeground(DARK_GREEN);
        punto.setFont(new Font("Arial", Font.PLAIN, 11));

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setBackground(Color.WHITE);

        JLabel lblRuta = new JLabel(ruta);
        lblRuta.setFont(new Font("Arial", Font.BOLD, 14));
        lblRuta.setForeground(TEXT_DARK);

        JLabel lblDet = new JLabel(detalles);
        lblDet.setFont(new Font("Arial", Font.PLAIN, 12));
        lblDet.setForeground(TEXT_GRAY);

        textos.add(lblRuta);
        textos.add(lblDet);
        izq.add(punto);
        izq.add(textos);

        JLabel lblTiempo = new JLabel(tiempo);
        lblTiempo.setFont(new Font("Arial", Font.BOLD, 14));
        lblTiempo.setForeground(DARK_GREEN);
        lblTiempo.setBorder(new EmptyBorder(0, 0, 0, 4));

        panel.add(izq,       BorderLayout.WEST);
        panel.add(lblTiempo, BorderLayout.EAST);
        return panel;
    }

    // ── MAIN ──────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Principal().setVisible(true);
        });
    }
}
