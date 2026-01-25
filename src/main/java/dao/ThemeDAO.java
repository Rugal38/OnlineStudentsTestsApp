package dao;

import model.Theme;	
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ThemeDAO {

    public static List<Theme> findByMatiere(int matiereId) {
        List<Theme> list = new ArrayList<>();
        String sql = "SELECT id, nom, matiere_id FROM theme WHERE matiere_id=? ORDER BY nom";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, matiereId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Theme t = new Theme();
                    t.setId(rs.getInt("id"));
                    t.setNom(rs.getString("nom"));
                    t.setMatiereId(rs.getInt("matiere_id"));
                    list.add(t);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    public static String findNameById(int id) {
        String sql = "SELECT nom FROM theme WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("nom");
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

}
