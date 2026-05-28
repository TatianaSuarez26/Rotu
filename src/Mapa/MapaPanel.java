package Mapa;
import Mapa.MapaDAO.Arista;
import Mapa.MapaDAO.Estacion;
import Mapa.MapaDAO.ResultadoRuta;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Panel interactivo que dibuja la red de estaciones.
 * - Nodos coloreados por línea (verde / azul / roja)
 * - Aristas con etiqueta de tiempo
 * - Hover resalta la estación
 * - Clic selecciona origen; segundo clic selecciona destino y calcula ruta
 * - Ruta óptima (Dijkstra) resaltada en amarillo
 */
public class MapaPanel extends JPanel{
    // ── Colores ───────────────────────────────────────────────────────────────
    private static final Color COL_VERDE   = new Color(27,  94,  60);
    private static final Color COL_AZUL    = new Color(21,  101, 192);
    private static final Color COL_ROJA    = new Color(198, 40,  40);
    private static final Color COL_BG      = new Color(245, 250, 245);
    private static final Color COL_ARISTA  = new Color(180, 180, 180);
    private static final Color COL_HOVER   = new Color(255, 193, 7);
    private static final Color COL_ORIGEN  = new Color(255, 87,  34);   // naranja = origen
    private static final Color COL_DESTINO = new Color(156, 39, 176);   // morado  = destino
    private static final Color COL_RUTA    = new Color(255, 235, 59);   // amarillo= camino

    private static final int   R           = 14;
    private static final int   R_HUB       = 17;

    //  Coordenadas fijas por id
    private static final Map<Integer, int[]> COORDS = new HashMap<>();
    static {
        COORDS.put(1,  new int[]{310, 60});   // Portal Norte
        COORDS.put(2,  new int[]{510, 90});   // Calle 100
        COORDS.put(3,  new int[]{420, 230});  // Centro
        COORDS.put(4,  new int[]{580, 370});  // Soacha
        COORDS.put(5,  new int[]{200, 120});  // Usaquén
        COORDS.put(6,  new int[]{370, 330});  // Av. Jiménez
        COORDS.put(7,  new int[]{110, 310});  // El Dorado
        COORDS.put(8,  new int[]{40,  400});  // La Floresta
        COORDS.put(9,  new int[]{430, 440});  // Bosa
        COORDS.put(10, new int[]{510, 330});  // Américas
        COORDS.put(11, new int[]{310, 165});  // Chapinero
        COORDS.put(12, new int[]{140, 60});   // Suba
        COORDS.put(13, new int[]{250, 340});  // Kennedy
        COORDS.put(14, new int[]{130, 200});  // Fontibón
        COORDS.put(15, new int[]{230, 230});  // El Lago
    }

    private List<Estacion> estaciones = new ArrayList<>();
    private List<Arista>   aristas    = new ArrayList<>();
    private List<Arista>   aristasBidi = new ArrayList<>();

    private int hoverId    = -1;
    private int origenId   = -1;   // primer clic
    private int destinoId  = -1;   // segundo clic
    private String infoTexto = "";

    // Ruta calculada
    private List<Integer> rutaCamino = new ArrayList<>();
    private int           rutaTiempo = 0;
    private double        rutaDist   = 0;

    // Todas las rutas posibles
    private List<MapaDAO.ResultadoRuta> todasRutas = new ArrayList<>();

    // Listener para notificar al Frame
    public interface RutaListener {
        void onRutaCalculada(String info);
        void onEstacionSeleccionada(String info, boolean esOrigen);
        void onTodasLasRutas(List<MapaDAO.ResultadoRuta> rutas,
                             java.util.function.Function<Integer,String> nombreFn);
    }
    private RutaListener rutaListener;

    public void setRutaListener(RutaListener l) { this.rutaListener = l; }

    //  Constructor
    public MapaPanel() {
        setPreferredSize(new Dimension(660, 500));
        setBackground(COL_BG);

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseMoved(MouseEvent e) {
                int prev = hoverId;
                hoverId = estacionEnPunto(e.getX(), e.getY());
                if (hoverId != prev) repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int id = estacionEnPunto(e.getX(), e.getY());
                if (id < 0) return;

                if (origenId < 0) {
                    // Primer clic: elegir origen
                    origenId  = id;
                    destinoId = -1;
                    rutaCamino.clear();
                    String nombre = nombreEstacion(id);
                    infoTexto = "Origen: " + nombre + "  → Selecciona el destino";
                    if (rutaListener != null) rutaListener.onEstacionSeleccionada(nombre, true);
                } else if (destinoId < 0 && id != origenId) {
                    // Segundo clic: elegir destino y calcular
                    destinoId = id;
                    calcularRuta();
                } else {
                    // Reiniciar selección
                    origenId  = id;
                    destinoId = -1;
                    rutaCamino.clear();
                    String nombre = nombreEstacion(id);
                    infoTexto = "Origen: " + nombre + "  → Selecciona el destino";
                    if (rutaListener != null) rutaListener.onEstacionSeleccionada(nombre, true);
                }
                repaint();
            }
        });

        cargarDatos();
    }

    //  Selección programática desde ComboBox
    public void seleccionarOrigen(int id) {
        origenId  = id;
        destinoId = -1;
        rutaCamino.clear();
        infoTexto = "Origen: " + nombreEstacion(id) + "  → Selecciona el destino";
        repaint();
    }

    public void seleccionarDestino(int id) {
        if (origenId < 0 || id == origenId) return;
        destinoId = id;
        calcularRuta();
        repaint();
    }

    public void limpiarSeleccion() {
        origenId  = -1;
        destinoId = -1;
        rutaCamino.clear();
        todasRutas.clear();
        infoTexto = "";
        repaint();
    }

    //  Cargar datos de la BD
    public void cargarDatos() {
        estaciones  = MapaDAO.obtenerEstaciones();
        aristas     = new ArrayList<>(MapaDAO.obtenerAristas());
        aristasBidi = MapaDAO.obtenerAristasBidireccionales();
        repaint();
    }

    public List<Estacion> getEstaciones() { return estaciones; }

    //  Dijkstra
    private void calcularRuta() {
        ResultadoRuta resultado = MapaDAO.dijkstra(origenId, destinoId, estaciones, aristasBidi);

        // Calcular todas las rutas posibles (DFS)
        todasRutas = MapaDAO.todasLasRutas(origenId, destinoId, estaciones, aristasBidi);

        if (resultado == null) {
            infoTexto = "⚠ No hay ruta entre las estaciones seleccionadas";
            rutaCamino.clear();
            todasRutas.clear();
            if (rutaListener != null) rutaListener.onRutaCalculada(infoTexto);
        } else {
            rutaCamino  = resultado.camino;
            rutaTiempo  = resultado.tiempoTotal;
            rutaDist    = resultado.distanciaTotal;
            String paradas = (rutaCamino.size() - 1) + " tramo(s)";
            infoTexto = nombreEstacion(origenId) + " → " + nombreEstacion(destinoId)
                    + "  |  " + rutaTiempo + " min  |  " + String.format("%.1f", rutaDist) + " km  |  " + paradas;
            if (rutaListener != null) {
                rutaListener.onRutaCalculada(infoTexto);
                rutaListener.onTodasLasRutas(todasRutas, this::nombreEstacion);
            }
        }
    }

    //  Pintar
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Map<Integer, Estacion> map = new HashMap<>();
        for (Estacion e : estaciones) map.put(e.id, e);

        Set<String> aristaRuta = new HashSet<>();
        for (int i = 0; i < rutaCamino.size() - 1; i++) {
            aristaRuta.add(rutaCamino.get(i) + "-" + rutaCamino.get(i + 1));
            aristaRuta.add(rutaCamino.get(i + 1) + "-" + rutaCamino.get(i));
        }

        dibujarAristas(g2, map, aristaRuta);
        dibujarNodos(g2, map);
        dibujarLeyenda(g2);
        if (!infoTexto.isEmpty()) dibujarInfo(g2);
    }


    private void dibujarAristas(Graphics2D g2, Map<Integer, Estacion> map, Set<String> aristaRuta) {
        Font fArista = new Font("Arial", Font.BOLD, 10);

        for (Arista a : aristas) {
            int[] co = COORDS.get(a.idOrigen);
            int[] cd = COORDS.get(a.idDestino);
            if (co == null || cd == null) continue;

            boolean esRuta  = aristaRuta.contains(a.idOrigen + "-" + a.idDestino);

            if (esRuta) {
                g2.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(new Color(COL_RUTA.getRed(), COL_RUTA.getGreen(), COL_RUTA.getBlue(), 220));
            } else {
                g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                Estacion eo = map.get(a.idOrigen);
                Color c = eo != null ? colorNodo(eo) : COL_ARISTA;
                g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 130));
            }
            g2.drawLine(co[0], co[1], cd[0], cd[1]);

            // Etiqueta con offset perpendicular y fondo
            int mx = (co[0] + cd[0]) / 2;
            int my = (co[1] + cd[1]) / 2;
            int dx = cd[0] - co[0], dy = cd[1] - co[1];
            double len = Math.sqrt(dx * dx + dy * dy);
            int ox = 0, oy = -4;
            if (len > 0) { ox = (int)(-dy / len * 10); oy = (int)(dx / len * 10) - 2; }

            g2.setStroke(new BasicStroke(1f));
            g2.setFont(fArista);
            FontMetrics fm = g2.getFontMetrics();
            String label = a.tiempo + " min";
            int lw = fm.stringWidth(label);
            int lx = mx + ox - lw / 2;
            int ly = my + oy;

            g2.setColor(new Color(255, 255, 255, 185));
            g2.fillRoundRect(lx - 2, ly - fm.getAscent(), lw + 4, fm.getHeight() + 1, 4, 4);
            g2.setColor(new Color(60, 60, 60));
            g2.drawString(label, lx, ly);
        }
        g2.setStroke(new BasicStroke(1f));
    }

    private void dibujarNodos(Graphics2D g2, Map<Integer, Estacion> map) {
        Font fNombre = new Font("Arial", Font.BOLD, 11);
        Font fLabel  = new Font("Arial", Font.PLAIN, 10);

        for (Estacion e : estaciones) {
            int[] c = COORDS.get(e.id);
            if (c == null) continue;

            int x = c[0], y = c[1];
            Color base = colorNodo(e);
            boolean esHub    = e.id == 3;
            boolean esHover  = e.id == hoverId;
            boolean esOrigen = e.id == origenId;
            boolean esDestino= e.id == destinoId;
            boolean enRuta   = rutaCamino.contains(e.id);
            int r = esHub ? R_HUB : R;

            g2.setColor(new Color(0, 0, 0, 40));
            g2.fillOval(x - r + 2, y - r + 3, r * 2, r * 2);

            Color fill;
            if (esOrigen)       fill = COL_ORIGEN;
            else if (esDestino) fill = COL_DESTINO;
            else if (enRuta && !rutaCamino.isEmpty()) fill = COL_RUTA.darker();
            else if (esHover)   fill = COL_HOVER;
            else                fill = base;

            g2.setColor(fill);
            g2.fillOval(x - r, y - r, r * 2, r * 2);

            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(esHub ? 3f : 2f));
            g2.drawOval(x - r, y - r, r * 2, r * 2);
            g2.setStroke(new BasicStroke(1f));

            // Icono origen/destino
            if (esOrigen || esDestino) {
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 10));
                String ico = esOrigen ? "A" : "B";
                FontMetrics fm2 = g2.getFontMetrics();
                g2.drawString(ico, x - fm2.stringWidth(ico)/2, y + fm2.getAscent()/2 - 1);
            }

            g2.setFont(esHub ? fNombre : fLabel);
            FontMetrics fm = g2.getFontMetrics();
            int tw = fm.stringWidth(e.nombre);
            int lx = x - tw / 2;
            int ly = y - r - 4;
            if (ly < 14) ly = y + r + 12;

            g2.setColor(new Color(255, 255, 255, 190));
            g2.fillRoundRect(lx - 2, ly - fm.getAscent(), tw + 4, fm.getHeight(), 4, 4);
            g2.setColor(esHub ? COL_VERDE : new Color(30, 30, 30));
            g2.drawString(e.nombre, lx, ly);
        }
    }

    private void dibujarLeyenda(Graphics2D g2) {
        int lx = 10, ly = getHeight() - 120;
        g2.setColor(new Color(255, 255, 255, 220));
        g2.fillRoundRect(lx - 4, ly - 4, 150, 118, 10, 10);
        g2.setColor(new Color(220, 220, 220));
        g2.setStroke(new BasicStroke(0.8f));
        g2.drawRoundRect(lx - 4, ly - 4, 150, 118, 10, 10);

        Font fl = new Font("Arial", Font.PLAIN, 11);
        g2.setFont(fl);
        Object[][] items = {
                {"Línea verde",  COL_VERDE},
                {"Línea azul",   COL_AZUL},
                {"Línea roja",   COL_ROJA},
                {"Origen (A)",   COL_ORIGEN},
                {"Destino (B)",  COL_DESTINO},
        };
        int step = 20;
        for (int i = 0; i < items.length; i++) {
            Color c = (Color) items[i][1];
            g2.setColor(c);
            g2.fillOval(lx, ly + i * step + 2, 12, 12);
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawOval(lx, ly + i * step + 2, 12, 12);
            g2.setStroke(new BasicStroke(0.8f));
            g2.setColor(new Color(40, 40, 40));
            g2.drawString((String) items[i][0], lx + 18, ly + i * step + 13);
        }
        g2.setFont(new Font("Arial", Font.ITALIC, 9));
        g2.setColor(new Color(120, 120, 120));
        g2.drawString("Clic = seleccionar origen/destino", lx, ly + 5 * step + 10);
    }

    private void dibujarInfo(Graphics2D g2) {
        int pw = getWidth();
        Font fi = new Font("Arial", Font.BOLD, 12);
        g2.setFont(fi);
        FontMetrics fm = g2.getFontMetrics();
        int tw = fm.stringWidth(infoTexto);
        int bx = Math.max(4, pw - tw - 28), by = 10;
        int bw = tw + 20, bh = 30;

        g2.setColor(new Color(255, 255, 255, 220));
        g2.fillRoundRect(bx, by, bw, bh, 10, 10);
        g2.setColor(new Color(200, 200, 200));
        g2.setStroke(new BasicStroke(0.8f));
        g2.drawRoundRect(bx, by, bw, bh, 10, 10);
        g2.setColor(new Color(27, 94, 60));
        g2.drawString(infoTexto, bx + 10, by + 20);
    }

    // ── Utilidades
    private int estacionEnPunto(int px, int py) {
        for (Estacion e : estaciones) {
            int[] c = COORDS.get(e.id);
            if (c == null) continue;
            int r = (e.id == 3) ? R_HUB : R;
            int dx = px - c[0], dy = py - c[1];
            if (dx * dx + dy * dy <= (r + 5) * (r + 5)) return e.id;
        }
        return -1;
    }

    private String nombreEstacion(int id) {
        for (Estacion e : estaciones) if (e.id == id) return e.nombre;
        return "id=" + id;
    }

    private Color colorLinea(String linea) {
        if (linea == null) return COL_ARISTA;
        return switch (linea) {
            case "Línea verde" -> COL_VERDE;
            case "Línea azul"  -> COL_AZUL;
            case "Línea roja"  -> COL_ROJA;
            default            -> COL_ARISTA;
        };
    }

    // Color visual por ID de estación (sobreescribe la línea de BD para nodos específicos)
    private Color colorNodo(Estacion e) {
        return switch (e.id) {
            case 14, 15 -> COL_ROJA;   // Fontibón, El Lago → línea roja
            case 6      -> COL_AZUL;   // Av. Jiménez       → línea azul
            default     -> colorLinea(e.linea);
        };
    }

    public String getInfoTexto()   { return infoTexto;  }
    public int    getOrigenId()    { return origenId;   }
    public int    getDestinoId()   { return destinoId;  }
    public int    getTiempoRuta()  { return rutaTiempo; }
    public double getDistRuta()    { return rutaDist;   }
    public int    getParadas()     { return Math.max(0, rutaCamino.size() - 1); }
    public boolean tieneRuta()        { return !rutaCamino.isEmpty() && origenId >= 0 && destinoId >= 0; }
    public String  getNombreOrigen()  { return nombreEstacion(origenId);  }
    public String  getNombreDestino() { return nombreEstacion(destinoId); }

    public String getLineaRuta() {
        Map<String, Integer> conteo = new HashMap<>();
        for (int id : rutaCamino) {
            for (Estacion e : estaciones) {
                if (e.id == id) { conteo.merge(e.linea, 1, Integer::sum); break; }
            }
        }
        return conteo.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Línea verde");
    }
}
