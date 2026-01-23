package dao;

import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResultatDAO {

    // ✅ Insérer une nouvelle tentative (date_passage est gérée par MySQL via CURRENT_TIMESTAMP)
    public static boolean insert(int score, int candidatId, int testId) {
        String sql = "INSERT INTO resultat(score, candidat_id, test_id) VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, score);
            ps.setInt(2, candidatId);
            ps.setInt(3, testId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ✅ Dernier score (pour afficher la dernière tentative)
    public static Integer findLastScore(int candidatId, int testId) {
        String sql = "SELECT score FROM resultat " +
                     "WHERE candidat_id = ? AND test_id = ? " +
                     "ORDER BY date_passage DESC, id DESC LIMIT 1";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, candidatId);
            ps.setInt(2, testId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("score");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ✅ Dernière date de passage (optionnel si vous affichez la date dans l'interface)
    public static Timestamp findLastDate(int candidatId, int testId) {
        String sql = "SELECT date_passage FROM resultat " +
                     "WHERE candidat_id = ? AND test_id = ? " +
                     "ORDER BY date_passage DESC, id DESC LIMIT 1";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, candidatId);
            ps.setInt(2, testId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getTimestamp("date_passage");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ✅ Liste des résultats pour l'affichage (vue avec jointures)
    public static List<model.ResultatView> findAllView() {
        List<model.ResultatView> list = new ArrayList<>();

        String sql =
                "SELECT r.id, r.score, r.date_passage, " +
                "       c.nom, c.prenom, c.email, c.code_session, " +
                "       t.titre AS test_titre " +
                "FROM resultat r " +
                "LEFT JOIN candidat c ON r.candidat_id = c.id " +
                "LEFT JOIN test t ON r.test_id = t.id " +
                "ORDER BY r.date_passage DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                model.ResultatView v = new model.ResultatView();
                v.setId(rs.getInt("id"));
                v.setScore(rs.getInt("score"));
                v.setDatePassage(rs.getTimestamp("date_passage"));
                v.setNom(rs.getString("nom"));
                v.setPrenom(rs.getString("prenom"));
                v.setEmail(rs.getString("email"));
                v.setCodeSession(rs.getString("code_session"));
                v.setTestTitre(rs.getString("test_titre"));
                list.add(v);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
