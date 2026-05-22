package InterfazGrafica;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;

public class Rutas extends JFrame {

    private static final Color DARK_GREEN   = new Color(27, 94, 60);
    private static final Color LIGHT_GREEN  = new Color(220, 245, 225);
    private static final Color CARD_BG      = new Color(247, 247, 247);
    private static final Color TEXT_DARK    = new Color(20, 20, 20);
    private static final Color TEXT_GRAY    = new Color(110, 110, 110);
    private static final Color BORDER_COLOR = new Color(220, 220, 220);
    private static final Color SIDEBAR_BG   = new Color(22, 78, 50);

    public Rutas() {
        setTitle("ROTU - Rutas");
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

        panel.add(crearItemSidebar("🏠", "Inicio", false));
        panel.add(Box.createVerticalStrut(6));
        panel.add(crearItemSidebar("⇄",  "Rutas",  true));   // activo
        panel.add(Box.createVerticalStrut(6));
        panel.add(crearItemSidebar("📍", "Mapa",   false));
        panel.add(Box.createVerticalStrut(6));
        panel.add(crearItemSidebar("👤", "Perfil", false));

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

        // Navegar de vuelta a Inicio
        if (etiqueta.equals("Inicio")) {
            panel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    new Principal().setVisible(true);
                    dispose();
                }
            });
        }

        return panel;
    }

    // ── CONTENIDO PRINCIPAL ───────────────────────────────────────────────────
    private JPanel crearContenido() {
        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(Color.WHITE);
        contenido.setBorder(new EmptyBorder(0, 0, 0, 0));

        contenido.add(crearEncabezado(),          BorderLayout.NORTH);
        contenido.add(crearPanelTabla(),          BorderLayout.CENTER);

        return contenido;
    }

    // ── ENCABEZADO ────────────────────────────────────────────────────────────
    private JPanel crearEncabezado() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_GREEN);
        panel.setBorder(new EmptyBorder(24, 32, 24, 32));
        panel.setPreferredSize(new Dimension(0, 100));

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setBackground(LIGHT_GREEN);

        JLabel seccion = new JLabel("RUTAS");
        seccion.setFont(new Font("Arial", Font.PLAIN, 11));
        seccion.setForeground(DARK_GREEN);

        JLabel titulo = new JLabel("Gestión de Rutas");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(TEXT_DARK);

        JLabel sub = new JLabel("Consulta y administra todas las rutas disponibles");
        sub.setFont(new Font("Arial", Font.PLAIN, 13));
        sub.setForeground(DARK_GREEN);

        textos.add(seccion);
        textos.add(Box.createVerticalStrut(4));
        textos.add(titulo);
        textos.add(Box.createVerticalStrut(3));
        textos.add(sub);

        // Botón nueva ruta
        JButton btnNueva = new JButton("+ Nueva Ruta");
        btnNueva.setBackground(DARK_GREEN);
        btnNueva.setForeground(Color.WHITE);
        btnNueva.setFont(new Font("Arial", Font.BOLD, 13));
        btnNueva.setBorder(new EmptyBorder(10, 20, 10, 20));
        btnNueva.setFocusPainted(false);
        btnNueva.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        panel.add(textos,   BorderLayout.WEST);
        panel.add(btnNueva, BorderLayout.EAST);
        return panel;
    }

    // ── PANEL CON FILTROS Y TABLA ─────────────────────────────────────────────
    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 32, 32, 32));

        panel.add(crearFiltros(), BorderLayout.NORTH);
        panel.add(crearTabla(),   BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearFiltros() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(0, 0, 16, 0));

        // Buscador
        JPanel caja = new JPanel(new BorderLayout());
        caja.setBackground(Color.WHITE);
        caja.setPreferredSize(new Dimension(280, 38));
        caja.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(6, 10, 6, 10)
        ));

        JLabel lupa = new JLabel("🔍  ");
        lupa.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));

        JTextField campo = new JTextField("Buscar ruta...");
        campo.setBorder(null);
        campo.setFont(new Font("Arial", Font.PLAIN, 13));
        campo.setForeground(Color.GRAY);

        caja.add(lupa,  BorderLayout.WEST);
        caja.add(campo, BorderLayout.CENTER);

        // Filtro línea
        JComboBox<String> filtroLinea = new JComboBox<>(
            new String[]{"Todas las líneas", "Línea verde", "Línea azul", "Línea roja"}
        );
        filtroLinea.setFont(new Font("Arial", Font.PLAIN, 13));
        filtroLinea.setPreferredSize(new Dimension(170, 38));

        // Filtro estado
        JComboBox<String> filtroEstado = new JComboBox<>(
            new String[]{"Todos los estados", "Activa", "Inactiva"}
        );
        filtroEstado.setFont(new Font("Arial", Font.PLAIN, 13));
        filtroEstado.setPreferredSize(new Dimension(170, 38));

        panel.add(caja);
        panel.add(filtroLinea);
        panel.add(filtroEstado);

        return panel;
    }

    private JScrollPane crearTabla() {
        String[] columnas = {"#", "Nombre de Ruta", "Origen", "Destino", "Paradas", "Tiempo est.", "Línea", "Estado"};

        Object[][] datos = {
            {"01", "Portal Norte → Centro",    "Portal Norte",   "Centro",        "3",  "12 min", "Línea verde", "Activa"},
            {"02", "Calle 100 → Soacha",       "Calle 100",      "Soacha",        "7",  "28 min", "Línea azul",  "Activa"},
            {"03", "Usaquén → Av. Jiménez",    "Usaquén",        "Av. Jiménez",   "5",  "19 min", "Línea verde", "Activa"},
            {"04", "El Dorado → La Floresta",  "El Dorado",      "La Floresta",   "4",  "15 min", "Línea roja",  "Activa"},
            {"05", "Bosa → Américas",          "Bosa",           "Américas",      "6",  "22 min", "Línea azul",  "Inactiva"},
            {"06", "Chapinero → Suba",         "Chapinero",      "Suba",          "8",  "35 min", "Línea verde", "Activa"},
            {"07", "Kennedy → Centro",         "Kennedy",        "Centro",        "9",  "40 min", "Línea roja",  "Activa"},
            {"08", "Fontibón → El Lago",       "Fontibón",       "El Lago",       "5",  "20 min", "Línea azul",  "Inactiva"},
        };

        DefaultTableModel modelo = new DefaultTableModel(datos, columnas) {
            public boolean isCellEditable(int row, int col) { return false; }
        };

        JTable tabla = new JTable(modelo);
        tabla.setFont(new Font("Arial", Font.PLAIN, 13));
        tabla.setRowHeight(38);
        tabla.setGridColor(BORDER_COLOR);
        tabla.setShowVerticalLines(false);
        tabla.setSelectionBackground(LIGHT_GREEN);
        tabla.setSelectionForeground(TEXT_DARK);
        tabla.setBackground(Color.WHITE);

        // Encabezado de la tabla
        JTableHeader header = tabla.getTableHeader();
        header.setBackground(CARD_BG);
        header.setForeground(TEXT_GRAY);
        header.setFont(new Font("Arial", Font.BOLD, 12));
        header.setPreferredSize(new Dimension(0, 40));
        header.setBorder(new MatteBorder(0, 0, 1, 0, BORDER_COLOR));

        // Columna # más angosta
        tabla.getColumnModel().getColumn(0).setMaxWidth(40);
        tabla.getColumnModel().getColumn(4).setMaxWidth(70);
        tabla.getColumnModel().getColumn(5).setMaxWidth(90);

        // Colores alternos por fila
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                if (!sel) {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(252, 252, 252));
                    setForeground(TEXT_DARK);
                }
                // Colorear columna Estado
                if (col == 7) {
                    String estado = val.toString();
                    if (!sel) {
                        setForeground(estado.equals("Activa") ? DARK_GREEN : new Color(180, 60, 60));
                    }
                    setFont(new Font("Arial", Font.BOLD, 12));
                } else {
                    setFont(new Font("Arial", Font.PLAIN, 13));
                }
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Rutas().setVisible(true));
    }
}
