package Rotu_System;

import BaseDatos.RutaDAO;
import BaseDatos.RutaDAO.Ruta;
import Inicio.Inicio;
import Mapa.MapaFrame;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class Rutas extends JFrame {

    private static final Color DARK_GREEN   = new Color(27, 94, 60);
    private static final Color LIGHT_GREEN  = new Color(220, 245, 225);
    private static final Color CARD_BG      = new Color(247, 247, 247);
    private static final Color TEXT_DARK    = new Color(20, 20, 20);
    private static final Color TEXT_GRAY    = new Color(110, 110, 110);
    private static final Color BORDER_COLOR = new Color(220, 220, 220);
    private static final Color SIDEBAR_BG   = new Color(22, 78, 50);

    private DefaultTableModel modeloTabla;
    private JTextField campoBusqueda;
    private JComboBox<String> filtroLinea;
    private JComboBox<String> filtroEstado;
    private JTable tabla;

    public Rutas() {
        setTitle("ROTU - Rutas");
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.add(topBar(),    BorderLayout.NORTH);
        root.add(sidebar(),   BorderLayout.WEST);
        root.add(contenido(), BorderLayout.CENTER);
        setContentPane(root);
    }

    private JPanel topBar() {
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

    private JPanel sidebar() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(SIDEBAR_BG);
        panel.setPreferredSize(new Dimension(200, 700));
        panel.setBorder(new EmptyBorder(30, 0, 30, 0));
        panel.add(itemSidebar("🏠", "Inicio", false));
        panel.add(Box.createVerticalStrut(6));
        panel.add(itemSidebar("⇄",  "Rutas",  true));
        panel.add(Box.createVerticalStrut(6));
        panel.add(itemSidebar("📍", "Mapa",   false));
        return panel;
    }

    private JPanel itemSidebar(String icono, String etiqueta, boolean activo) {
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
                    case "Inicio" -> { new Inicio().setVisible(true);     dispose(); }
                    case "Mapa"   -> { new MapaFrame().setVisible(true);  dispose(); }
                }
            }
        });
        return panel;
    }

    private JPanel contenido() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.add(encabezado(), BorderLayout.NORTH);
        panel.add(panelTabla(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel encabezado() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_GREEN);
        panel.setBorder(new EmptyBorder(24, 32, 24, 32));
        panel.setPreferredSize(new Dimension(0, 100));
        JPanel textos = new JPanel(); textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS)); textos.setBackground(LIGHT_GREEN);
        JLabel sec = new JLabel("RUTAS"); sec.setFont(new Font("Arial", Font.PLAIN, 11)); sec.setForeground(DARK_GREEN);
        JLabel tit = new JLabel("Gestión de Rutas"); tit.setFont(new Font("Arial", Font.BOLD, 24)); tit.setForeground(TEXT_DARK);
        textos.add(sec); textos.add(Box.createVerticalStrut(4)); textos.add(tit); textos.add(Box.createVerticalStrut(3));
        panel.add(textos, BorderLayout.WEST);
        return panel;
    }

    private JPanel panelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 32, 32, 32));
        panel.add(filtros(), BorderLayout.NORTH);
        panel.add(scrollTabla(),   BorderLayout.CENTER);
        return panel;
    }

    private JPanel filtros() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(0, 0, 16, 0));

        JPanel caja = new JPanel(new BorderLayout());
        caja.setBackground(Color.WHITE); caja.setPreferredSize(new Dimension(260, 38));
        caja.setBorder(BorderFactory.createCompoundBorder(new LineBorder(BORDER_COLOR, 1, true), new EmptyBorder(6, 10, 6, 10)));
        JLabel lupa = new JLabel("🔍  "); lupa.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        campoBusqueda = new JTextField("Buscar ruta...");
        campoBusqueda.setBorder(null); campoBusqueda.setFont(new Font("Arial", Font.PLAIN, 13)); campoBusqueda.setForeground(Color.GRAY);
        campoBusqueda.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (campoBusqueda.getText().equals("Buscar ruta...")) { campoBusqueda.setText(""); campoBusqueda.setForeground(TEXT_DARK); }
            }
        });
        campoBusqueda.addActionListener(e -> aplicarFiltros());
        caja.add(lupa, BorderLayout.WEST); caja.add(campoBusqueda, BorderLayout.CENTER);

        filtroLinea = new JComboBox<>(new String[]{"Todas las líneas", "Línea verde", "Línea azul", "Línea roja"});
        filtroLinea.setFont(new Font("Arial", Font.PLAIN, 13)); filtroLinea.setPreferredSize(new Dimension(160, 38));
        filtroLinea.addActionListener(e -> aplicarFiltros());

        filtroEstado = new JComboBox<>(new String[]{"Todos los estados", "Activa", "Inactiva"});
        filtroEstado.setFont(new Font("Arial", Font.PLAIN, 13)); filtroEstado.setPreferredSize(new Dimension(160, 38));
        filtroEstado.addActionListener(e -> aplicarFiltros());

        panel.add(caja); panel.add(filtroLinea); panel.add(filtroEstado);
        return panel;
    }

    private JScrollPane scrollTabla() {
        String[] cols = {"#", "Nombre de Ruta", "Origen", "Destino", "Paradas", "Tiempo", "Distancia", "Línea", "Estado"};
        modeloTabla = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        cargarDatos(RutaDAO.obtenerTodas());
        tabla = new JTable(modeloTabla);
        tabla.setFont(new Font("Arial", Font.PLAIN, 13)); tabla.setRowHeight(38);
        tabla.setGridColor(BORDER_COLOR); tabla.setShowVerticalLines(false);
        tabla.setSelectionBackground(LIGHT_GREEN); tabla.setSelectionForeground(TEXT_DARK); tabla.setBackground(Color.WHITE);
        JTableHeader header = tabla.getTableHeader();
        header.setBackground(CARD_BG); header.setForeground(TEXT_GRAY);
        header.setFont(new Font("Arial", Font.BOLD, 12)); header.setPreferredSize(new Dimension(0, 40));
        header.setBorder(new MatteBorder(0, 0, 1, 0, BORDER_COLOR));
        tabla.getColumnModel().getColumn(0).setMaxWidth(45);
        tabla.getColumnModel().getColumn(4).setMaxWidth(70);
        tabla.getColumnModel().getColumn(5).setMaxWidth(80);
        tabla.getColumnModel().getColumn(6).setMaxWidth(90);
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                if (!sel) { setBackground(row % 2 == 0 ? Color.WHITE : new Color(252, 252, 252)); setForeground(TEXT_DARK); }
                if (col == 8 && val != null) {
                    if (!sel) setForeground(val.toString().equals("Activa") ? DARK_GREEN : new Color(180, 60, 60));
                    setFont(new Font("Arial", Font.BOLD, 12));
                } else { setFont(new Font("Arial", Font.PLAIN, 13)); }
                return this;
            }
        });
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(new LineBorder(BORDER_COLOR, 1, true)); scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    private void cargarDatos(List<Ruta> rutas) {
        modeloTabla.setRowCount(0);
        for (Ruta r : rutas) modeloTabla.addRow(r.toFila());
    }

    private void aplicarFiltros() {
        String texto  = campoBusqueda.getText().trim();
        String linea  = (String) filtroLinea.getSelectedItem();
        String estado = (String) filtroEstado.getSelectedItem();
        boolean hayTexto = !texto.isEmpty() && !texto.equals("Buscar ruta...");
        cargarDatos(hayTexto ? RutaDAO.buscar(texto) : RutaDAO.filtrar(linea, estado));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Rutas().setVisible(true));
    }
}
