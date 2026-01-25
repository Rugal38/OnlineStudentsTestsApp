package dao;

import controller.AdminDashboardBean.CreneauRow;	
import controller.AdminDashboardBean.ResultatRow;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DashboardDAO {

    // ===== COUNTS =====
    public static int countQuestions() {
        return countOne("SELECT COUNT(*) FROM question");
    }

    public static int countCandidats() {
        return countOne("SELECT COUNT(*) FROM candidat");
    }

    public static int countResultats() {
        return countOne("SELECT COUNT(*) FROM resultat");
    }

    public static int countCreneauxAVenir() {
        // creneaux >= today
        return countOne("SELECT COUNT(*) FROM creneau WHERE date_exam >= CURDATE()");
    }

    private static int countOne(String sql) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ===== TABLE: CRENEAUX A VENIR =====
    public static List<CreneauRow> findCreneauxAVenir(int limit) {
        List<CreneauRow> list = new ArrayList<>();

        String sql =
            "SELECT c.id, c.date_exam, c.heure_debut, c.heure_fin, c.disponible, c.test_id, t.titre AS test_titre " +
            "FROM creneau c " +
            "LEFT JOIN test t ON t.id = c.test_id " +
            "WHERE c.date_exam >= CURDATE() " +
            "ORDER BY c.date_exam ASC, c.heure_debut ASC " +
            "LIMIT ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CreneauRow c = new CreneauRow();
                    c.setId(rs.getInt("id"));
                    c.setDateExam(rs.getDate("date_exam").toString());
                    c.setHeureDebut(rs.getTime("heure_debut").toString());
                    c.setHeureFin(rs.getTime("heure_fin").toString());
                    c.setDisponible(rs.getBoolean("disponible"));

                    int tid = rs.getInt("test_id");
                    c.setTestId(rs.wasNull() ? null : tid);

                    c.setTestTitre(rs.getString("test_titre") == null ? "(sans titre)" : rs.getString("test_titre"));
                    list.add(c);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // ===== TABLE: DERNIERS RESULTATS =====
    public static List<ResultatRow> findDerniersResultats(int limit) {
        List<ResultatRow> list = new ArrayList<>();

        String sql =
            "SELECT r.id, r.score, r.date_passage, " +
            "       CONCAT(c.nom,' ',c.prenom) AS candidat_nom, " +
            "       t.titre AS test_titre " +
            "FROM resultat r " +
            "LEFT JOIN candidat c ON c.id = r.candidat_id " +
            "LEFT JOIN test t ON t.id = r.test_id " +
            "ORDER BY r.date_passage DESC " +
            "LIMIT ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ResultatRow r = new ResultatRow();
                    r.setId(rs.getInt("id"));
                    r.setScore(rs.getInt("score"));
                    Timestamp ts = rs.getTimestamp("date_passage");
                    r.setDatePassage(ts == null ? "" : ts.toString());

                    String cand = rs.getString("candidat_nom");
                    r.setCandidatNom(cand == null ? "(inconnu)" : cand);

                    String tt = rs.getString("test_titre");
                    r.setTestTitre(tt == null ? "(sans titre)" : tt);

                    list.add(r);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    
    public static Map<String, Integer> countResultsLast7Days() {
        Map<String, Integer> results = new LinkedHashMap<>();
        String sql = "SELECT DATE(date_passage) as passage_day, COUNT(*) as count " +
                     "FROM resultat " +
                     "WHERE date_passage >= CURDATE() - INTERVAL 6 DAY " +
                     "GROUP BY passage_day " +
                     "ORDER BY passage_day ASC";

        // Initialize map with last 7 days
        for (int i = 6; i >= 0; i--) {
            java.time.LocalDate date = java.time.LocalDate.now().minusDays(i);
            results.put(date.toString(), 0);
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String day = rs.getDate("passage_day").toString();
                int count = rs.getInt("count");
                results.put(day, count);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return results;
    }
}
