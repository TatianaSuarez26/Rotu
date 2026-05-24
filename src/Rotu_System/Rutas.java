package Rotu_System;

import BaseDatos.RutaDAO;
import BaseDatos.RutaDAO.Ruta;

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
        root.add(crearTopBar(),    BorderLayout.NORTH);
        root.add(crearSidebar(),   BorderLayout.WEST);
        root.add(crearContenido(), BorderLayout.CENTER);
        setContentPane(root);
    }

    private JPanel crearTopBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(DARK_GREEN);
        panel.setPreferredSize(new Dimension(1100, 55));
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));
        JLabel titulo = new JLabel("ROTU");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Arial", Font.BOLD, 22));
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        der.setBackground(DARK_GREEN);
        JLabel campana = new JLabel("🔔"); campana.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18)); campana.setForeground(Color.WHITE);
        JLabel usuario = new JLabel("👤"); usuario.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18)); usuario.setForeground(Color.WHITE);
        der.add(campana); der.add(usuario);
        panel.add(titulo, BorderLayout.WEST); panel.add(der, BorderLayout.EAST);
        return panel;
    }

    private JPanel crearSidebar() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(SIDEBAR_BG);
        panel.setPreferredSize(new Dimension(200, 700));
        panel.setBorder(new EmptyBorder(30, 0, 30, 0));
        panel.add(crearItemSidebar("🏠", "Inicio", false));
        panel.add(Box.createVerticalStrut(6));
        panel.add(crearItemSidebar("⇄",  "Rutas",  true));
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
        JLabel ico = new JLabel(icono); ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16)); ico.setForeground(Color.WHITE);
        JLabel txt = new JLabel(etiqueta); txt.setFont(new Font("Arial", activo ? Font.BOLD : Font.PLAIN, 13)); txt.setForeground(Color.WHITE);
        panel.add(ico); panel.add(txt);
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                switch (etiqueta) {
                    case "Inicio" -> { new Principal().setVisible(true); dispose(); }
                    case "Perfil" -> { new Perfil().setVisible(true);   dispose(); }
                }
            }
        });
        return panel;
    }

    private JPanel crearContenido() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.add(crearEncabezado(), BorderLayout.NORTH);
        panel.add(crearPanelTabla(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearEncabezado() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_GREEN);
        panel.setBorder(new EmptyBorder(24, 32, 24, 32));
        panel.setPreferredSize(new Dimension(0, 100));
        JPanel textos = new JPanel(); textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS)); textos.setBackground(LIGHT_GREEN);
        JLabel sec = new JLabel("RUTAS"); sec.setFont(new Font("Arial", Font.PLAIN, 11)); sec.setForeground(DARK_GREEN);
        JLabel tit = new JLabel("Gestión de Rutas"); tit.setFont(new Font("Arial", Font.BOLD, 24)); tit.setForeground(TEXT_DARK);
        JLabel sub = new JLabel("Consulta y administra todas las rutas disponibles"); sub.setFont(new Font("Arial", Font.PLAIN, 13)); sub.setForeground(DARK_GREEN);
        textos.add(sec); textos.add(Box.createVerticalStrut(4)); textos.add(tit); textos.add(Box.createVerticalStrut(3)); textos.add(sub);
        JButton btnNueva = new JButton("+ Nueva Ruta");
        btnNueva.setBackground(DARK_GREEN); btnNueva.setForeground(Color.WHITE);
        btnNueva.setFont(new Font("Arial", Font.BOLD, 13)); btnNueva.setBorder(new EmptyBorder(10, 20, 10, 20));
        btnNueva.setFocusPainted(false); btnNueva.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnNueva.addActionListener(e -> abrirDialogoNuevaRuta());
        panel.add(textos, BorderLayout.WEST); panel.add(btnNueva, BorderLayout.EAST);
        return panel;
    }

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

        JButton btnEliminar = new JButton("🗑  Eliminar");
        btnEliminar.setFont(new Font("Arial", Font.PLAIN, 13)); btnEliminar.setForeground(new Color(180, 50, 50));
        btnEliminar.setBackground(Color.WHITE);
        btnEliminar.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(220, 180, 180), 1, true), new EmptyBorder(6, 14, 6, 14)));
        btnEliminar.setFocusPainted(false); btnEliminar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEliminar.addActionListener(e -> eliminarSeleccionada());

        panel.add(caja); panel.add(filtroLinea); panel.add(filtroEstado); panel.add(btnEliminar);
        return panel;
    }

    private JScrollPane crearTabla() {
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

    private void eliminarSeleccionada() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { JOptionPane.showMessageDialog(this, "Selecciona una ruta para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE); return; }
        String nombre = (String) modeloTabla.getValueAt(fila, 1);
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar \"" + nombre + "\"?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt((String) modeloTabla.getValueAt(fila, 0));
            if (RutaDAO.eliminar(id)) aplicarFiltros();
        }
    }

    private void abrirDialogoNuevaRuta() {
        JDialog d = new JDialog(this, "Nueva Ruta", true);
        d.setSize(460, 440); d.setLocationRelativeTo(this); d.setResizable(false);
        JPanel panel = new JPanel(); panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(24, 28, 24, 28)); panel.setBackground(Color.WHITE);
        JLabel tit = new JLabel("Agregar Nueva Ruta"); tit.setFont(new Font("Arial", Font.BOLD, 16)); tit.setForeground(TEXT_DARK); tit.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(tit); panel.add(Box.createVerticalStrut(20));
        JTextField fNombre = campo("Nombre"); JTextField fOrigen = campo("Origen"); JTextField fDestino = campo("Destino");
        JTextField fParadas = campo("Paradas"); JTextField fTiempo = campo("Tiempo (min)"); JTextField fDist = campo("Distancia (km)");
        JComboBox<String> cbLinea = new JComboBox<>(new String[]{"Línea verde", "Línea azul", "Línea roja"});
        cbLinea.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(fila("Nombre", fNombre)); panel.add(Box.createVerticalStrut(10));
        panel.add(fila("Origen", fOrigen)); panel.add(Box.createVerticalStrut(10));
        panel.add(fila("Destino", fDestino)); panel.add(Box.createVerticalStrut(10));
        JPanel f2 = new JPanel(new GridLayout(1, 3, 10, 0)); f2.setBackground(Color.WHITE);
        f2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58)); f2.setAlignmentX(LEFT_ALIGNMENT);
        f2.add(fila("Paradas", fParadas)); f2.add(fila("Tiempo(min)", fTiempo)); f2.add(fila("Dist.(km)", fDist));
        panel.add(f2); panel.add(Box.createVerticalStrut(10)); panel.add(fila("Línea", cbLinea)); panel.add(Box.createVerticalStrut(20));
        JPanel bots = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0)); bots.setBackground(Color.WHITE); bots.setAlignmentX(LEFT_ALIGNMENT);
        JButton btnC = new JButton("Cancelar"); btnC.setFont(new Font("Arial", Font.PLAIN, 13)); btnC.addActionListener(e -> d.dispose());
        JButton btnG = new JButton("Guardar"); btnG.setBackground(DARK_GREEN); btnG.setForeground(Color.WHITE);
        btnG.setFont(new Font("Arial", Font.BOLD, 13)); btnG.setBorder(new EmptyBorder(8, 20, 8, 20)); btnG.setFocusPainted(false);
        btnG.addActionListener(e -> {
            try {
                Ruta r = new Ruta(0, fNombre.getText().trim(), fOrigen.getText().trim(), fDestino.getText().trim(),
                    Integer.parseInt(fParadas.getText().trim()), Integer.parseInt(fTiempo.getText().trim()),
                    Double.parseDouble(fDist.getText().trim()), (String) cbLinea.getSelectedItem(), "Activa");
                if (RutaDAO.insertar(r)) { aplicarFiltros(); d.dispose(); }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(d, "Paradas, tiempo y distancia deben ser números.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        bots.add(btnC); bots.add(btnG); panel.add(bots);
        d.setContentPane(panel); d.setVisible(true);
    }

    private JTextField campo(String ph) {
        JTextField f = new JTextField(ph); f.setFont(new Font("Arial", Font.PLAIN, 13)); f.setForeground(Color.GRAY);
        f.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) { if (f.getText().equals(ph)) { f.setText(""); f.setForeground(TEXT_DARK); } }
        });
        f.setBorder(BorderFactory.createCompoundBorder(new LineBorder(BORDER_COLOR, 1, true), new EmptyBorder(7, 10, 7, 10)));
        return f;
    }

    private JPanel fila(String label, JComponent comp) {
        JPanel p = new JPanel(); p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE); p.setAlignmentX(LEFT_ALIGNMENT); p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        JLabel lbl = new JLabel(label); lbl.setFont(new Font("Arial", Font.BOLD, 11)); lbl.setForeground(TEXT_GRAY); lbl.setAlignmentX(LEFT_ALIGNMENT);
        comp.setAlignmentX(LEFT_ALIGNMENT); comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        p.add(lbl); p.add(Box.createVerticalStrut(4)); p.add(comp); return p;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Rutas().setVisible(true));
    }
}
