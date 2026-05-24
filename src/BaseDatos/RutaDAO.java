package BaseDatos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RutaDAO {

    // ── Modelo Ruta ───────────────────────────────────────────────────────────
    public static class Ruta {
        public int    id;
        public String nombre, origen, destino, linea, estado;
        public int    paradas, tiempo;
        public double distancia;

        public Ruta(int id, String nombre, String origen, String destino,
                    int paradas, int tiempo, double distancia, String linea, String estado) {
            this.id = id; this.nombre = nombre; this.origen = origen;
            this.destino = destino; this.paradas = paradas; this.tiempo = tiempo;
            this.distancia = distancia; this.linea = linea; this.estado = estado;
        }

        public Object[] toFila() {
            return new Object[]{
                String.format("%02d", id), nombre, origen, destino,
                String.valueOf(paradas), tiempo + " min",
                String.format("%.1f km", distancia), linea, estado
            };
        }
    }

    // ── Obtener todas ─────────────────────────────────────────────────────────
    public static List<Ruta> obtenerTodas() {
        List<Ruta> lista = new ArrayList<>();
        try (Connection con = Conexion.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM rutas ORDER BY id")) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return lista;
    }

    // ── Buscar por texto ──────────────────────────────────────────────────────
    public static List<Ruta> buscar(String texto) {
        List<Ruta> lista = new ArrayList<>();
        String sql = "SELECT * FROM rutas WHERE LOWER(nombre) LIKE ? OR LOWER(origen) LIKE ? OR LOWER(destino) LIKE ? ORDER BY id";
        String p = "%" + texto.toLowerCase() + "%";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p); ps.setString(2, p); ps.setString(3, p);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return lista;
    }

    // ── Filtrar por línea y estado ────────────────────────────────────────────
    public static List<Ruta> filtrar(String linea, String estado) {
        List<Ruta> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM rutas WHERE 1=1");
        if (linea  != null && !linea.equals("Todas las líneas"))   sql.append(" AND linea=?");
        if (estado != null && !estado.equals("Todos los estados")) sql.append(" AND estado=?");
        sql.append(" ORDER BY id");
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int i = 1;
            if (linea  != null && !linea.equals("Todas las líneas"))   ps.setString(i++, linea);
            if (estado != null && !estado.equals("Todos los estados")) ps.setString(i,   estado);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return lista;
    }

    // ── Insertar ──────────────────────────────────────────────────────────────
    public static boolean insertar(Ruta r) {
        String sql = "INSERT INTO rutas (nombre,origen,destino,paradas,tiempo,distancia,linea,estado) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, r.nombre); ps.setString(2, r.origen);
            ps.setString(3, r.destino); ps.setInt(4, r.paradas);
            ps.setInt(5, r.tiempo); ps.setDouble(6, r.distancia);
            ps.setString(7, r.linea); ps.setString(8, r.estado);
            ps.executeUpdate(); return true;
        } catch (SQLException e) { System.err.println(e.getMessage()); return false; }
    }

    // ── Eliminar ──────────────────────────────────────────────────────────────
    public static boolean eliminar(int id) {
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement("DELETE FROM rutas WHERE id=?")) {
            ps.setInt(1, id); ps.executeUpdate(); return true;
        } catch (SQLException e) { System.err.println(e.getMessage()); return false; }
    }

    // ── Mapper ────────────────────────────────────────────────────────────────
    private static Ruta mapear(ResultSet rs) throws SQLException {
        return new Ruta(rs.getInt("id"), rs.getString("nombre"),
            rs.getString("origen"), rs.getString("destino"),
            rs.getInt("paradas"), rs.getInt("tiempo"), rs.getDouble("distancia"),
            rs.getString("linea"), rs.getString("estado"));
    }
}
