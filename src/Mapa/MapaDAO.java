package Mapa;
import BaseDatos.Conexion;
import java.sql.*;
import java.util.*;
public class MapaDAO {

    //  Estacion
    public static class Estacion {
        public int    id;
        public String nombre, linea, descripcion;

        public Estacion(int id, String nombre, String linea, String descripcion) {
            this.id          = id;
            this.nombre      = nombre;
            this.linea       = linea;
            this.descripcion = descripcion;
        }
    }

    // Arista
    public static class Arista {
        public int    idOrigen, idDestino, tiempo;
        public double distancia;

        public Arista(int idOrigen, int idDestino, int tiempo, double distancia) {
            this.idOrigen   = idOrigen;
            this.idDestino  = idDestino;
            this.tiempo     = tiempo;
            this.distancia  = distancia;
        }
    }

    // Resultado de ruta Dijkstra
    public static class ResultadoRuta {
        public List<Integer> camino;
        public int           tiempoTotal;
        public double        distanciaTotal;

        public ResultadoRuta(List<Integer> camino, int tiempoTotal, double distanciaTotal) {
            this.camino         = camino;
            this.tiempoTotal    = tiempoTotal;
            this.distanciaTotal = distanciaTotal;
        }
    }

    //  Obtener todas las estaciones
    public static List<Estacion> obtenerEstaciones() {
        List<Estacion> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, linea, descripcion FROM estaciones ORDER BY id";
        try (Connection con = Conexion.getConexion();
             Statement  st  = con.createStatement();
             ResultSet  rs  = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Estacion(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("linea"),
                        rs.getString("descripcion")
                ));
            }
        } catch (SQLException e) {
            System.err.println("MapaDAO.obtenerEstaciones: " + e.getMessage());
        }
        return lista;
    }

    // Obtener aristas para dibujo (sin duplicar)
    public static List<Arista> obtenerAristas() {
        List<Arista> lista = new ArrayList<>();
        String sql = "SELECT id_origen, id_destino, tiempo, distancia "
                + "FROM conexiones WHERE id_origen < id_destino ORDER BY id_origen";
        try (Connection con = Conexion.getConexion();
             Statement  st  = con.createStatement();
             ResultSet  rs  = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Arista(
                        rs.getInt("id_origen"),
                        rs.getInt("id_destino"),
                        rs.getInt("tiempo"),
                        rs.getDouble("distancia")
                ));
            }
        } catch (SQLException e) {
            System.err.println("MapaDAO.obtenerAristas: " + e.getMessage());
        }
        return lista;
    }

    //  Obtener aristas bidireccionales (para Dijkstra)
    public static List<Arista> obtenerAristasBidireccionales() {
        List<Arista> lista = new ArrayList<>();
        String sql = "SELECT id_origen, id_destino, tiempo, distancia FROM conexiones";
        try (Connection con = Conexion.getConexion();
             Statement  st  = con.createStatement();
             ResultSet  rs  = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Arista(
                        rs.getInt("id_origen"),
                        rs.getInt("id_destino"),
                        rs.getInt("tiempo"),
                        rs.getDouble("distancia")
                ));
            }
        } catch (SQLException e) {
            System.err.println("MapaDAO.obtenerAristasBidireccionales: " + e.getMessage());
        }
        return lista;
    }

    // ── Dijkstra (por tiempo minimo) ──────────────────────────────────────────
    public static ResultadoRuta dijkstra(int origenId, int destinoId,
                                         List<Estacion> estaciones,
                                         List<Arista>   aristasParam) {

        if (aristasParam == null || aristasParam.isEmpty()) return null;
        List<Arista> fuente = aristasParam;

        // Construir grafo (bidireccional aunque ya vengan en ambos sentidos)
        Map<Integer, List<int[]>> grafo = new HashMap<>();
        Set<String> agregadas = new HashSet<>();
        for (Arista a : fuente) {
            String k1 = a.idOrigen + "-" + a.idDestino;
            String k2 = a.idDestino + "-" + a.idOrigen;
            if (!agregadas.contains(k1)) {
                grafo.computeIfAbsent(a.idOrigen,  x -> new ArrayList<>())
                        .add(new int[]{a.idDestino, a.tiempo, (int)(a.distancia * 10)});
                agregadas.add(k1);
            }
            if (!agregadas.contains(k2)) {
                grafo.computeIfAbsent(a.idDestino, x -> new ArrayList<>())
                        .add(new int[]{a.idOrigen,  a.tiempo, (int)(a.distancia * 10)});
                agregadas.add(k2);
            }
        }

        // Recolectar todos los ids conocidos
        Set<Integer> todosIds = new HashSet<>(grafo.keySet());
        for (Estacion e : estaciones) todosIds.add(e.id);

        // Inicializar distancias
        Map<Integer, Integer> dist   = new HashMap<>();
        Map<Integer, Integer> prev   = new HashMap<>();
        Map<Integer, Integer> distKm = new HashMap<>();
        for (int id : todosIds) { dist.put(id, Integer.MAX_VALUE); prev.put(id, -1); }
        dist.put(origenId, 0);
        distKm.put(origenId, 0);

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(x -> x[0]));
        pq.offer(new int[]{0, origenId});

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int t = curr[0], u = curr[1];
            Integer du = dist.get(u);
            if (du == null || t > du) continue;
            if (u == destinoId) break;

            List<int[]> vecinos = grafo.getOrDefault(u, Collections.emptyList());
            for (int[] v : vecinos) {
                int vecino = v[0], peso = v[1], km10 = v[2];
                int nuevo = t + peso;
                Integer dv = dist.getOrDefault(vecino, Integer.MAX_VALUE);
                if (nuevo < dv) {
                    dist.put(vecino, nuevo);
                    distKm.put(vecino, distKm.getOrDefault(u, 0) + km10);
                    prev.put(vecino, u);
                    pq.offer(new int[]{nuevo, vecino});
                }
            }
        }

        Integer dDest = dist.get(destinoId);
        if (dDest == null || dDest == Integer.MAX_VALUE) return null;

        // Reconstruir camino
        List<Integer> camino = new ArrayList<>();
        int cur = destinoId;
        int maxIter = 100;
        while (cur != -1 && maxIter-- > 0) {
            camino.add(0, cur);
            if (cur == origenId) break;
            Integer p = prev.get(cur);
            cur = (p == null) ? -1 : p;
        }
        if (camino.isEmpty() || camino.get(0) != origenId) return null;

        double km = distKm.getOrDefault(destinoId, 0) / 10.0;
        return new ResultadoRuta(camino, dDest, km);
    }

    // ── DFS: encontrar TODAS las rutas simples (sin ciclos) entre origen y destino ──
    public static List<ResultadoRuta> todasLasRutas(int origenId, int destinoId,
                                                    List<Estacion> estaciones,
                                                    List<Arista>   aristasParam) {
        if (aristasParam == null || aristasParam.isEmpty()) return new ArrayList<>();
        List<Arista> fuente = aristasParam;

        Map<Integer, List<int[]>> grafo = new HashMap<>();
        Set<String> agregadas = new HashSet<>();
        for (Arista a : fuente) {
            String k1 = a.idOrigen + "-" + a.idDestino;
            String k2 = a.idDestino + "-" + a.idOrigen;
            if (!agregadas.contains(k1)) {
                grafo.computeIfAbsent(a.idOrigen,  x -> new ArrayList<>())
                        .add(new int[]{a.idDestino, a.tiempo, (int)(a.distancia * 10)});
                agregadas.add(k1);
            }
            if (!agregadas.contains(k2)) {
                grafo.computeIfAbsent(a.idDestino, x -> new ArrayList<>())
                        .add(new int[]{a.idOrigen,  a.tiempo, (int)(a.distancia * 10)});
                agregadas.add(k2);
            }
        }

        List<ResultadoRuta> resultado = new ArrayList<>();
        List<Integer> caminoActual   = new ArrayList<>();
        Set<Integer>  visitados      = new HashSet<>();
        caminoActual.add(origenId);
        visitados.add(origenId);

        dfs(origenId, destinoId, grafo, caminoActual, visitados, 0, 0, resultado);

        // Ordenar por tiempo ascendente
        resultado.sort(Comparator.comparingInt(r -> r.tiempoTotal));
        return resultado;
    }

    private static void dfs(int actual, int destino,
                            Map<Integer, List<int[]>> grafo,
                            List<Integer> caminoActual,
                            Set<Integer>  visitados,
                            int tiempoAcum, int distAcum,
                            List<ResultadoRuta> resultado) {
        if (actual == destino) {
            resultado.add(new ResultadoRuta(
                    new ArrayList<>(caminoActual),
                    tiempoAcum,
                    distAcum / 10.0
            ));
            return;
        }
        // Limitar profundidad para evitar rutas demasiado largas (más de 8 saltos)
        if (caminoActual.size() > 9) return;

        List<int[]> vecinos = grafo.getOrDefault(actual, Collections.emptyList());
        for (int[] v : vecinos) {
            int vecino = v[0], tiempo = v[1], km10 = v[2];
            if (!visitados.contains(vecino)) {
                visitados.add(vecino);
                caminoActual.add(vecino);
                dfs(vecino, destino, grafo, caminoActual, visitados,
                        tiempoAcum + tiempo, distAcum + km10, resultado);
                caminoActual.remove(caminoActual.size() - 1);
                visitados.remove(vecino);
            }
        }
    }
}
