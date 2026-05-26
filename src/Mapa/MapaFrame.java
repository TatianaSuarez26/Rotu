package Mapa;
import BaseDatos.Conexion;
import Inicio.Inicio;
import Rotu_System.Rutas;
import Mapa.MapaDAO.Estacion;
import java.sql.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;
public class MapaFrame extends JFrame {
    private static final Color DARK_GREEN   = new Color(27,  94,  60);
    private static final Color LIGHT_GREEN  = new Color(220, 245, 225);
    private static final Color TEXT_DARK    = new Color(20,  20,  20);
    private static final Color TEXT_GRAY    = new Color(110, 110, 110);
    private static final Color SIDEBAR_BG   = new Color(22,  78,  50);
    private static final Color BORDER_COLOR = new Color(220, 220, 220);
    private static final Color CARD_BG      = new Color(247, 250, 247);

    private MapaPanel  mapaPanel;
    private JLabel     lblInfo;
    private JComboBox<String> cbOrigen;
    private JComboBox<String> cbDestino;
    private JPanel     panelTodasRutas;
    private JPanel     contenedorRutas;

    // ids paralelos a los ComboBox
    private int[] ids;

    public MapaFrame() {
        setTitle("ROTU - Mapa");
        setSize(1200, 720);
        setMinimumSize(new Dimension(950, 620));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.add(crearTopBar(),    BorderLayout.NORTH);
        root.add(crearSidebar(),   BorderLayout.WEST);
        root.add(crearContenido(), BorderLayout.CENTER);
        setContentPane(root);
    }

    // ── TOP BAR ───────────────────────────────────────────────────────────────
    private JPanel crearTopBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(DARK_GREEN);
        panel.setPreferredSize(new Dimension(1200, 55));
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel titulo = new JLabel("ROTU");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Arial", Font.BOLD, 22));

        panel.add(titulo, BorderLayout.WEST);
        return panel;
    }

    // ── SIDEBAR ───────────────────────────────────────────────────────────────
    private JPanel crearSidebar() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(SIDEBAR_BG);
        panel.setPreferredSize(new Dimension(200, 720));
        panel.setBorder(new EmptyBorder(30, 0, 30, 0));

        panel.add(crearItem("🏠", "Inicio", false));
        panel.add(Box.createVerticalStrut(6));
        panel.add(crearItem("⇄",  "Rutas",  false));
        panel.add(Box.createVerticalStrut(6));
        panel.add(crearItem("📍", "Mapa",   true));
        return panel;
    }

    private JPanel crearItem(String icono, String etiqueta, boolean activo) {
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
                    case "Inicio" -> { new Inicio().setVisible(true);    dispose(); }
                    case "Rutas"  -> { new Rutas().setVisible(true);     dispose(); }
                }
            }
        });
        return panel;
    }

    // ── CONTENIDO ─────────────────────────────────────────────────────────────
    private JPanel crearContenido() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.add(crearEncabezado(), BorderLayout.NORTH);
        panel.add(crearCuerpoMapa(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearEncabezado() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_GREEN);
        panel.setBorder(new EmptyBorder(22, 32, 22, 32));
        panel.setPreferredSize(new Dimension(0, 115));

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setBackground(LIGHT_GREEN);

        JLabel sec = new JLabel("MAPA"); sec.setFont(new Font("Arial", Font.PLAIN, 11)); sec.setForeground(DARK_GREEN);
        JLabel tit = new JLabel("Red de Estaciones"); tit.setFont(new Font("Arial", Font.BOLD, 24)); tit.setForeground(TEXT_DARK);
        JLabel sub = new JLabel("Selecciona origen y destino para calcular la mejor ruta"); sub.setFont(new Font("Arial", Font.PLAIN, 13)); sub.setForeground(DARK_GREEN);

        textos.add(sec);
        textos.add(Box.createVerticalStrut(4));
        textos.add(tit);
        textos.add(Box.createVerticalStrut(3));
        textos.add(sub);

        JButton btnGuardar = new JButton("★  Guardar ruta");
        btnGuardar.setBackground(DARK_GREEN);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Arial", Font.BOLD, 13));
        btnGuardar.setBorder(new EmptyBorder(10, 20, 10, 20));
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnGuardar.addActionListener(e -> guardarRuta());

        panel.add(textos,     BorderLayout.WEST);
        panel.add(btnGuardar, BorderLayout.EAST);
        return panel;
    }

    private JPanel crearCuerpoMapa() {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(16, 24, 16, 24));

        // Panel izquierdo: mapa + info
        JPanel panelMapa = new JPanel(new BorderLayout());
        panelMapa.setBackground(Color.WHITE);

        mapaPanel = new MapaPanel();
        mapaPanel.setBorder(new LineBorder(BORDER_COLOR, 1, true));

        lblInfo = new JLabel("Haz clic en una estación del mapa o usa los selectores →");
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 12));
        lblInfo.setForeground(TEXT_GRAY);
        lblInfo.setBorder(new EmptyBorder(8, 4, 4, 4));

        mapaPanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent e) {
                String info = mapaPanel.getInfoTexto();
                if (!info.isEmpty()) lblInfo.setText(info);
            }
        });
        mapaPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // sincronizar comboboxes después del clic en el mapa
                SwingUtilities.invokeLater(() -> sincronizarCombosDesdePanel());
            }
        });

        mapaPanel.setRutaListener(new MapaPanel.RutaListener() {
            public void onRutaCalculada(String info) {
                lblInfo.setText(info);
            }
            public void onEstacionSeleccionada(String nombre, boolean esOrigen) {
                lblInfo.setText((esOrigen ? "Origen: " : "Destino: ") + nombre
                        + (esOrigen ? "  \u2192 Selecciona el destino" : ""));
            }
            public void onTodasLasRutas(java.util.List<Mapa.MapaDAO.ResultadoRuta> rutas,
                                        java.util.function.Function<Integer,String> nombreFn) {
                mostrarTodasLasRutas(rutas, nombreFn);
            }
        });

        panelMapa.add(mapaPanel, BorderLayout.CENTER);
        panelMapa.add(lblInfo,   BorderLayout.SOUTH);

        // Panel derecho: buscador
        JPanel panelBuscador = crearPanelBuscador();

        panel.add(panelMapa,    BorderLayout.CENTER);
        panel.add(panelBuscador, BorderLayout.EAST);
        return panel;
    }

    // ── PANEL LATERAL (rutas posibles) ───────────────────────────────────────
    private JPanel crearPanelBuscador() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));
        panel.setPreferredSize(new Dimension(280, 0));
        panel.setBorder(new CompoundBorder(
                new MatteBorder(0, 1, 0, 0, BORDER_COLOR),
                new EmptyBorder(16, 14, 16, 14)
        ));

        // Encabezado fijo arriba
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(new Color(240, 248, 255));

        JLabel tit = new JLabel("\uD83D\uDDFA Rutas Posibles");
        tit.setFont(new Font("Arial", Font.BOLD, 15));
        tit.setForeground(new Color(21, 101, 192));
        tit.setAlignmentX(LEFT_ALIGNMENT);
        header.add(tit);
        header.add(Box.createVerticalStrut(3));

        JLabel sub = new JLabel("Haz clic en origen y destino en el mapa");
        sub.setFont(new Font("Arial", Font.PLAIN, 11));
        sub.setForeground(TEXT_GRAY);
        sub.setAlignmentX(LEFT_ALIGNMENT);
        header.add(sub);
        header.add(Box.createVerticalStrut(10));

        JButton btnLimpiar = new JButton("\u2715  Limpiar selecci\u00F3n");
        btnLimpiar.setBackground(Color.WHITE);
        btnLimpiar.setForeground(TEXT_GRAY);
        btnLimpiar.setFont(new Font("Arial", Font.PLAIN, 11));
        btnLimpiar.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLimpiar.setAlignmentX(LEFT_ALIGNMENT);
        btnLimpiar.addActionListener(e -> {
            mapaPanel.limpiarSeleccion();
            lblInfo.setText("Haz clic en una estaci\u00F3n del mapa");
            if (panelTodasRutas != null) panelTodasRutas.setVisible(false);
        });
        header.add(btnLimpiar);
        header.add(Box.createVerticalStrut(12));

        panel.add(header, BorderLayout.NORTH);

        // Panel todas las rutas ocupa todo el espacio restante
        panelTodasRutas = crearPanelTodasRutas();
        panel.add(panelTodasRutas, BorderLayout.CENTER);

        // Combos ocultos para que sincronizarCombosDesdePanel no falle
        cbOrigen  = new JComboBox<>();
        cbDestino = new JComboBox<>();
        SwingUtilities.invokeLater(this::poblarComboBoxes);
        return panel;
    }


    private void poblarComboBoxes() {
        List<Estacion> estaciones = mapaPanel.getEstaciones();
        ids = new int[estaciones.size() + 1];
        ids[0] = -1;

        cbOrigen.removeAllItems();
        cbDestino.removeAllItems();
        cbOrigen.addItem("-- Seleccionar origen --");
        cbDestino.addItem("-- Seleccionar destino --");

        for (int i = 0; i < estaciones.size(); i++) {
            Estacion e = estaciones.get(i);
            ids[i + 1] = e.id;
            cbOrigen.addItem(e.nombre + "  (" + e.linea + ")");
            cbDestino.addItem(e.nombre + "  (" + e.linea + ")");
        }
    }


    private void sincronizarCombosDesdePanel() {
        if (ids == null) return;
        int idO = mapaPanel.getOrigenId();
        int idD = mapaPanel.getDestinoId();
        for (int i = 1; i < ids.length; i++) {
            if (ids[i] == idO) cbOrigen.setSelectedIndex(i);
            if (ids[i] == idD) cbDestino.setSelectedIndex(i);
        }
    }

    private JPanel crearPanelTodasRutas() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(new LineBorder(new Color(160, 200, 220), 1, true));
        panel.setVisible(false);

        contenedorRutas = new JPanel();
        contenedorRutas.setLayout(new BoxLayout(contenedorRutas, BoxLayout.Y_AXIS));
        contenedorRutas.setBackground(new Color(240, 248, 255));
        contenedorRutas.setBorder(new EmptyBorder(8, 8, 8, 8));

        JScrollPane scroll = new JScrollPane(contenedorRutas);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(240, 248, 255));
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(12);

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void mostrarTodasLasRutas(java.util.List<Mapa.MapaDAO.ResultadoRuta> rutas,
                                      java.util.function.Function<Integer,String> nombreFn) {
        contenedorRutas.removeAll();

        for (int i = 0; i < rutas.size(); i++) {
            Mapa.MapaDAO.ResultadoRuta r = rutas.get(i);
            boolean esOptima = (i == 0);

            JPanel tarjeta = new JPanel();
            tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
            tarjeta.setBackground(esOptima ? new Color(220, 245, 225) : Color.WHITE);
            tarjeta.setBorder(new CompoundBorder(
                    new LineBorder(esOptima ? new Color(80, 160, 100) : new Color(210, 210, 210), 1, true),
                    new EmptyBorder(10, 10, 10, 10)
            ));
            tarjeta.setAlignmentX(LEFT_ALIGNMENT);
            tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

            // Encabezado: número + badge óptima
            JPanel encabezado = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            encabezado.setBackground(tarjeta.getBackground());
            encabezado.setAlignmentX(LEFT_ALIGNMENT);
            JLabel numLabel = new JLabel("Ruta " + (i + 1));
            numLabel.setFont(new Font("Arial", Font.BOLD, 12));
            numLabel.setForeground(esOptima ? DARK_GREEN : TEXT_GRAY);
            encabezado.add(numLabel);
            if (esOptima) {
                JLabel badge = new JLabel("  \u2B50 \u00D3ptima");
                badge.setFont(new Font("Arial", Font.BOLD, 11));
                badge.setForeground(DARK_GREEN);
                encabezado.add(badge);
            }
            tarjeta.add(encabezado);
            tarjeta.add(Box.createVerticalStrut(5));

            // Camino: cada estación en su propia línea con flechas
            java.util.List<Integer> camino = r.camino;
            for (int j = 0; j < camino.size(); j++) {
                String prefix = (j == 0) ? "  \uD83D\uDFE2 " : (j == camino.size() - 1) ? "  \uD83D\uDD34 " : "  \u25CB ";
                JLabel lblEst = new JLabel(prefix + nombreFn.apply(camino.get(j)));
                lblEst.setFont(new Font("Arial", Font.PLAIN, 11));
                lblEst.setForeground(new Color(30, 30, 30));
                lblEst.setAlignmentX(LEFT_ALIGNMENT);
                tarjeta.add(lblEst);
                if (j < camino.size() - 1) {
                    JLabel arrow = new JLabel("      \u2193");
                    arrow.setFont(new Font("Arial", Font.PLAIN, 10));
                    arrow.setForeground(new Color(150, 150, 150));
                    arrow.setAlignmentX(LEFT_ALIGNMENT);
                    tarjeta.add(arrow);
                }
            }
            tarjeta.add(Box.createVerticalStrut(6));

            // Estadísticas
            JLabel lblStats = new JLabel(
                    r.tiempoTotal + " min   \u2022   "
                            + String.format("%.1f", r.distanciaTotal) + " km   \u2022   "
                            + (r.camino.size() - 1) + " tramo(s)");
            lblStats.setFont(new Font("Arial", Font.BOLD, 11));
            lblStats.setForeground(esOptima ? DARK_GREEN : new Color(80, 80, 80));
            lblStats.setAlignmentX(LEFT_ALIGNMENT);
            tarjeta.add(lblStats);

            contenedorRutas.add(tarjeta);
            contenedorRutas.add(Box.createVerticalStrut(7));
        }

        if (rutas.isEmpty()) {
            JLabel lbl = new JLabel("No se encontraron rutas.");
            lbl.setFont(new Font("Arial", Font.ITALIC, 12));
            lbl.setForeground(TEXT_GRAY);
            lbl.setAlignmentX(LEFT_ALIGNMENT);
            contenedorRutas.add(lbl);
        }

        contenedorRutas.revalidate();
        contenedorRutas.repaint();
        panelTodasRutas.setVisible(true);
        panelTodasRutas.revalidate();
    }

    // ── GUARDAR RUTA EN BD ────────────────────────────────────────────────────
    private void guardarRuta() {
        if (!mapaPanel.tieneRuta()) {
            JOptionPane.showMessageDialog(this,
                "Primero calcula una ruta seleccionando origen y destino en el mapa.",
                "Sin ruta", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombre    = mapaPanel.getNombreOrigen() + " → " + mapaPanel.getNombreDestino();
        String origen    = mapaPanel.getNombreOrigen();
        String destino   = mapaPanel.getNombreDestino();
        int    paradas   = mapaPanel.getParadas();
        int    tiempo    = mapaPanel.getTiempoRuta();
        double distancia = mapaPanel.getDistRuta();
        String linea     = mapaPanel.getLineaRuta();

        try (Connection con = Conexion.getConexion()) {

            // 1. Verificar si la ruta ya existe en la tabla rutas
            int idRuta = -1;
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT id FROM rutas WHERE origen = ? AND destino = ?")) {
                ps.setString(1, origen);
                ps.setString(2, destino);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) idRuta = rs.getInt("id");
            }

            // 2. Si no existe, insertarla
            if (idRuta == -1) {
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO rutas (nombre,origen,destino,paradas,tiempo,distancia,linea,estado) " +
                        "VALUES (?,?,?,?,?,?,?,'Activa')",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, nombre);
                    ps.setString(2, origen);
                    ps.setString(3, destino);
                    ps.setInt(4, paradas);
                    ps.setInt(5, tiempo);
                    ps.setDouble(6, distancia);
                    ps.setString(7, linea);
                    ps.executeUpdate();
                    ResultSet gen = ps.getGeneratedKeys();
                    if (gen.next()) idRuta = gen.getInt(1);
                }
            }

            // 3. Verificar si ya está guardada para el usuario
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT id FROM rutas_guardadas WHERE id_usuario = ? AND id_ruta = ?")) {
                ps.setInt(1, 1);
                ps.setInt(2, idRuta);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this,
                        "Esta ruta ya está en tus rutas guardadas.",
                        "Aviso", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }

            // 4. Guardar en rutas_guardadas
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO rutas_guardadas (id_usuario, id_ruta) VALUES (?, ?)")) {
                ps.setInt(1, 1);
                ps.setInt(2, idRuta);
                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(this,
                "✅  Ruta \"" + nombre + "\" guardada correctamente.",
                "Guardado", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            System.err.println("Error guardando ruta: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Error al guardar la ruta: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MapaFrame().setVisible(true));
    }
}
