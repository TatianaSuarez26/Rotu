package Rotu_System;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class Perfil extends JFrame {

    private static final Color DARK_GREEN   = new Color(27, 94, 60);
    private static final Color LIGHT_GREEN  = new Color(220, 245, 225);
    private static final Color CARD_BG      = new Color(247, 247, 247);
    private static final Color TEXT_DARK    = new Color(20, 20, 20);
    private static final Color TEXT_GRAY    = new Color(110, 110, 110);
    private static final Color BORDER_COLOR = new Color(220, 220, 220);
    private static final Color SIDEBAR_BG   = new Color(22, 78, 50);

    public Perfil() {
        setTitle("ROTU - Perfil");
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
        JLabel campana = new JLabel("🔔"); campana.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18)); campana.setForeground(Color.WHITE);
        JLabel usuario = new JLabel("👤"); usuario.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18)); usuario.setForeground(Color.WHITE);
        derecha.add(campana); derecha.add(usuario);

        panel.add(titulo, BorderLayout.WEST);
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

        panel.add(crearItemSidebar("🏠", "Inicio", false));
        panel.add(Box.createVerticalStrut(6));
        panel.add(crearItemSidebar("⇄",  "Rutas",  false));
        panel.add(Box.createVerticalStrut(6));
        panel.add(crearItemSidebar("📍", "Mapa",   false));
        panel.add(Box.createVerticalStrut(6));
        panel.add(crearItemSidebar("👤", "Perfil", true));
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
                    case "Inicio" -> { new Principal().setVisible(true); dispose(); }
                    case "Rutas"  -> { new Rutas().setVisible(true);    dispose(); }
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
        contenido.add(crearCuerpo());

        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        return scroll;
    }

    // ── ENCABEZADO ────────────────────────────────────────────────────────────
    private JPanel crearEncabezado() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_GREEN);
        panel.setBorder(new EmptyBorder(24, 32, 24, 32));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setBackground(LIGHT_GREEN);

        JLabel sec = new JLabel("MI CUENTA");
        sec.setFont(new Font("Arial", Font.PLAIN, 11));
        sec.setForeground(DARK_GREEN);

        JLabel tit = new JLabel("Perfil de Usuario");
        tit.setFont(new Font("Arial", Font.BOLD, 24));
        tit.setForeground(TEXT_DARK);

        JLabel sub = new JLabel("Información y rutas guardadas");
        sub.setFont(new Font("Arial", Font.PLAIN, 13));
        sub.setForeground(DARK_GREEN);

        textos.add(sec);
        textos.add(Box.createVerticalStrut(4));
        textos.add(tit);
        textos.add(Box.createVerticalStrut(3));
        textos.add(sub);
        panel.add(textos, BorderLayout.WEST);
        return panel;
    }

    // ── CUERPO ────────────────────────────────────────────────────────────────
    private JPanel crearCuerpo() {
        JPanel panel = new JPanel(new BorderLayout(24, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(28, 32, 32, 32));
        panel.add(crearTarjetaUsuario(),  BorderLayout.WEST);
        panel.add(crearTarjetaRutasGuardadas(), BorderLayout.CENTER);
        return panel;
    }

    // ── TARJETA USUARIO ───────────────────────────────────────────────────────
    private JPanel crearTarjetaUsuario() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_BG);
        panel.setPreferredSize(new Dimension(240, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(28, 20, 28, 20)
        ));

        // Avatar
        JLabel avatar = new JLabel("👤", SwingConstants.CENTER);
        avatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        avatar.setAlignmentX(CENTER_ALIGNMENT);

        // Nombre
        JLabel nombre = new JLabel("Carlos Rodríguez", SwingConstants.CENTER);
        nombre.setFont(new Font("Arial", Font.BOLD, 15));
        nombre.setForeground(TEXT_DARK);
        nombre.setAlignmentX(CENTER_ALIGNMENT);

        // Correo
        JLabel correo = new JLabel("carlos@email.com", SwingConstants.CENTER);
        correo.setFont(new Font("Arial", Font.PLAIN, 12));
        correo.setForeground(TEXT_GRAY);
        correo.setAlignmentX(CENTER_ALIGNMENT);

        // Badge tipo
        JLabel badge = new JLabel("  Usuario Premium  ", SwingConstants.CENTER) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(LIGHT_GREEN);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(new Font("Arial", Font.BOLD, 11));
        badge.setForeground(DARK_GREEN);
        badge.setOpaque(false);
        badge.setAlignmentX(CENTER_ALIGNMENT);

        // Separador
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(BORDER_COLOR);

        // Estadísticas
        panel.add(avatar);
        panel.add(Box.createVerticalStrut(10));
        panel.add(nombre);
        panel.add(Box.createVerticalStrut(4));
        panel.add(correo);
        panel.add(Box.createVerticalStrut(12));
        panel.add(badge);
        panel.add(Box.createVerticalStrut(20));
        panel.add(sep);
        panel.add(Box.createVerticalStrut(20));
        panel.add(crearStat("🗺",  "Rutas usadas",    "47"));
        panel.add(Box.createVerticalStrut(12));
        panel.add(crearStat("⭐", "Rutas guardadas", "3"));
        panel.add(Box.createVerticalStrut(12));
        panel.add(crearStat("📅", "Miembro desde",   "Ene 2024"));
        panel.add(Box.createVerticalGlue());

        // Botón cerrar sesión
        panel.add(Box.createVerticalStrut(24));
        JButton btnSalir = new JButton("Cerrar sesión");
        btnSalir.setBackground(new Color(255, 245, 245));
        btnSalir.setForeground(new Color(200, 60, 60));
        btnSalir.setFont(new Font("Arial", Font.BOLD, 12));
        btnSalir.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(240, 200, 200), 1, true),
            new EmptyBorder(8, 16, 8, 16)
        ));
        btnSalir.setFocusPainted(false);
        btnSalir.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSalir.setAlignmentX(CENTER_ALIGNMENT);
        btnSalir.setMaximumSize(new Dimension(180, 36));
        panel.add(btnSalir);

        return panel;
    }

    private JPanel crearStat(String icono, String etiqueta, String valor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));

        JLabel lbl = new JLabel(icono + "  " + etiqueta);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        lbl.setForeground(TEXT_GRAY);

        JLabel val = new JLabel(valor);
        val.setFont(new Font("Arial", Font.BOLD, 13));
        val.setForeground(DARK_GREEN);

        panel.add(lbl, BorderLayout.WEST);
        panel.add(val, BorderLayout.EAST);
        return panel;
    }

    // ── RUTAS GUARDADAS ───────────────────────────────────────────────────────
    private JPanel crearTarjetaRutasGuardadas() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(22, 24, 22, 24)
        ));

        JLabel titulo = new JLabel("Mis Rutas Guardadas");
        titulo.setFont(new Font("Arial", Font.BOLD, 15));
        titulo.setForeground(TEXT_DARK);
        titulo.setAlignmentX(LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createVerticalStrut(6));

        JLabel sub = new JLabel("Accede rápido a tus trayectos frecuentes");
        sub.setFont(new Font("Arial", Font.PLAIN, 12));
        sub.setForeground(TEXT_GRAY);
        sub.setAlignmentX(LEFT_ALIGNMENT);
        card.add(sub);
        card.add(Box.createVerticalStrut(18));

        // Rutas guardadas del usuario
        card.add(crearItemRuta("Portal Norte → Centro",   "3 paradas · Línea verde", "12 min"));
        card.add(new JSeparator());
        card.add(crearItemRuta("Usaquén → Av. Jiménez",   "5 paradas · Línea verde", "19 min"));
        card.add(new JSeparator());
        card.add(crearItemRuta("Kennedy → Centro",         "9 paradas · Línea roja",  "40 min"));

        return card;
    }

    private JPanel crearItemRuta(String ruta, String detalles, String tiempo) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(new EmptyBorder(12, 0, 12, 0));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        panel.setAlignmentX(LEFT_ALIGNMENT);

        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        izq.setBackground(CARD_BG);

        JLabel punto = new JLabel("●");
        punto.setForeground(DARK_GREEN);
        punto.setFont(new Font("Arial", Font.PLAIN, 10));

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setBackground(CARD_BG);

        JLabel lblRuta = new JLabel(ruta);
        lblRuta.setFont(new Font("Arial", Font.BOLD, 13));
        lblRuta.setForeground(TEXT_DARK);

        JLabel lblDet = new JLabel(detalles);
        lblDet.setFont(new Font("Arial", Font.PLAIN, 11));
        lblDet.setForeground(TEXT_GRAY);

        textos.add(lblRuta);
        textos.add(lblDet);
        izq.add(punto);
        izq.add(textos);

        JLabel lblTiempo = new JLabel(tiempo);
        lblTiempo.setFont(new Font("Arial", Font.BOLD, 13));
        lblTiempo.setForeground(DARK_GREEN);

        panel.add(izq,       BorderLayout.WEST);
        panel.add(lblTiempo, BorderLayout.EAST);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Perfil().setVisible(true));
    }
}
