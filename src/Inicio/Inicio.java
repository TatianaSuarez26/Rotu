package Inicio;

import BaseDatos.Conexion;
import Mapa.MapaFrame;
import Rotu_System.Rutas;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Inicio extends JFrame {

    private static final Color DARK_GREEN   = new Color(27, 94, 60);
    private static final Color LIGHT_GREEN  = new Color(220, 245, 225);
    private static final Color CARD_BG      = new Color(247, 247, 247);
    private static final Color TEXT_DARK    = new Color(20, 20, 20);
    private static final Color TEXT_GRAY    = new Color(110, 110, 110);
    private static final Color BORDER_COLOR = new Color(220, 220, 220);
    private static final Color SIDEBAR_BG   = new Color(22, 78, 50);

    // ID del usuario activo (Carlos Rodríguez)
    private static final int ID_USUARIO = 1;

    public Inicio() {
        setTitle("ROTU");
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

        panel.add(titulo, BorderLayout.WEST);
        return panel;
    }

    // ── BARRA LATERAL ─────────────────────────────────────────────────────────
    private JPanel crearSidebar() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(SIDEBAR_BG);
        panel.setPreferredSize(new Dimension(200, 700));
        panel.setBorder(new EmptyBorder(30, 0, 30, 0));

        panel.add(crearItemSidebar("🏠", "Inicio", true));
        panel.add(Box.createVerticalStrut(6));
        panel.add(crearItemSidebar("⇄",  "Rutas",  false));
        panel.add(Box.createVerticalStrut(6));
        panel.add(crearItemSidebar("📍", "Mapa",   false));
        return panel;
    }

    private JPanel crearItemSidebar(String icono, String etiqueta, boolean activo) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 10));
        panel.setMaximumSize(new Dimension(200, 48));
        panel.setBackground(activo ? DARK_GREEN : SIDEBAR_BG);
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel ico = new JLabel(icono); ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16)); ico.setForeground(Color.WHITE);
        JLabel txt = new JLabel(etiqueta); txt.setFont(new Font("Arial", activo ? Font.BOLD : Font.PLAIN, 13)); txt.setForeground(Color.WHITE);
        panel.add(ico); panel.add(txt);

        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                switch (etiqueta) {
                    case "Rutas" -> { new Rutas().setVisible(true);     dispose(); }
                    case "Mapa"  -> { new MapaFrame().setVisible(true); dispose(); }
                }
            }
        });
        return panel;
    }

    // ── CONTENIDO ─────────────────────────────────────────────────────────────
    private JScrollPane crearContenido() {
        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(Color.WHITE);

        contenido.add(crearEncabezado());
        contenido.add(crearAccesosRapidos());
        contenido.add(crearRutasGuardadas());

        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        return scroll;
    }

    // ── ENCABEZADO ────────────────────────────────────────────────────────────
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

        JPanel grid = new JPanel(new GridLayout(1, 2, 14, 0));
        grid.setBackground(Color.WHITE);
        grid.setAlignmentX(LEFT_ALIGNMENT);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        // Buscar ruta → abre Rutas
        JPanel cardRutas = crearTarjeta("⇄", "Buscar ruta", "Origen a destino");
        cardRutas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new Rutas().setVisible(true); dispose();
            }
        });

        // Estaciones → abre Mapa
        JPanel cardMapa = crearTarjeta("📍", "Estaciones", "Ver todas las paradas");
        cardMapa.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new MapaFrame().setVisible(true); dispose();
            }
        });

        grid.add(cardRutas);
        grid.add(cardMapa);

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

    // ── MIS RUTAS GUARDADAS (desde BD) ────────────────────────────────────────
    private JPanel crearRutasGuardadas() {
        JPanel outer = new JPanel();
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        outer.setBackground(Color.WHITE);
        outer.setBorder(new EmptyBorder(20, 32, 32, 32));

        JLabel titulo = new JLabel("MIS RUTAS GUARDADAS");
        titulo.setFont(new Font("Arial", Font.BOLD, 12));
        titulo.setForeground(TEXT_DARK);
        titulo.setAlignmentX(LEFT_ALIGNMENT);
        outer.add(titulo);
        outer.add(Box.createVerticalStrut(10));

        List<String[]> rutas = cargarRutasGuardadas();

        if (rutas.isEmpty()) {
            JLabel vacio = new JLabel("No tienes rutas guardadas aún.");
            vacio.setFont(new Font("Arial", Font.ITALIC, 13));
            vacio.setForeground(TEXT_GRAY);
            vacio.setAlignmentX(LEFT_ALIGNMENT);
            outer.add(vacio);
        } else {
            for (int i = 0; i < rutas.size(); i++) {
                String[] r = rutas.get(i);
                // r = [nombre, origen, destino, paradas, tiempo, linea]
                String nombre   = r[0];
                String detalles = r[3] + " paradas · " + r[5];
                String tiempo   = r[4] + " min";
                outer.add(crearItemRuta(nombre, detalles, tiempo));
                if (i < rutas.size() - 1) outer.add(new JSeparator());
            }
        }

        return outer;
    }

    /** Consulta las rutas guardadas del usuario activo junto con sus datos. */
    private List<String[]> cargarRutasGuardadas() {
        List<String[]> lista = new ArrayList<>();
        String sql = """
            SELECT r.nombre, r.origen, r.destino,
                   r.paradas, r.tiempo, r.linea
            FROM rutas_guardadas rg
            JOIN rutas r ON rg.id_ruta = r.id
            WHERE rg.id_usuario = ?
            ORDER BY rg.fecha DESC
        """;
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, ID_USUARIO);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new String[]{
                    rs.getString("nombre"),
                    rs.getString("origen"),
                    rs.getString("destino"),
                    rs.getInt("paradas") + "",
                    rs.getInt("tiempo")  + "",
                    rs.getString("linea")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error cargando rutas guardadas: " + e.getMessage());
        }
        return lista;
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
        SwingUtilities.invokeLater(() -> new Inicio().setVisible(true));
    }
}
