package dao;

import model.Question;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionDAO {

    public static List<Question> findByTestId(int testId) {
        return findByTest(testId);
    }

    public static List<Question> findAll() {
        List<Question> list = new ArrayList<>();
        String sql = "SELECT id, libelle, theme, type, test_id, matiere_id, theme_id FROM question ORDER BY id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Question q = map(rs);
                q.setReponses(ReponseDAO.findByQuestion(q.getId()));
                list.add(q);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static List<Question> findByTest(int testId) {
        List<Question> list = new ArrayList<>();
        String sql = "SELECT id, libelle, theme, type, test_id, matiere_id, theme_id " +
                     "FROM question WHERE test_id = ? ORDER BY id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, testId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Question q = map(rs);
                    q.setReponses(ReponseDAO.findByQuestion(q.getId()));
                    list.add(q);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static Question findById(int id) {
        String sql = "SELECT id, libelle, theme, type, test_id, matiere_id, theme_id FROM question WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Question q = map(rs);
                    q.setReponses(ReponseDAO.findByQuestion(q.getId()));
                    return q;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Integer insertAndReturnId(Question q) {
        // ✅ Ajout de theme_id
        String sql = "INSERT INTO question(libelle, theme, type, test_id, matiere_id, theme_id) VALUES (?,?,?,?,?,?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, q.getLibelle());

            // (Optionnel) Champ texte "theme" gardé pour compatibilité / affichage
            ps.setString(2, q.getTheme());

            ps.setString(3, q.getType());

            if (q.getTestId() == null) ps.setNull(4, Types.INTEGER);
            else ps.setInt(4, q.getTestId());

            if (q.getMatiereId() == null) ps.setNull(5, Types.INTEGER);
            else ps.setInt(5, q.getMatiereId());

            if (q.getThemeId() == null) ps.setNull(6, Types.INTEGER);
            else ps.setInt(6, q.getThemeId());

            int ok = ps.executeUpdate();
            if (ok == 0) return null;

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int newId = rs.getInt(1);
                    q.setId(newId);
                    return newId;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean update(Question q) {
        // ✅ Ajout de theme_id
        String sql = "UPDATE question SET libelle = ?, theme = ?, type = ?, test_id = ?, matiere_id = ?, theme_id = ? WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, q.getLibelle());
            ps.setString(2, q.getTheme());
            ps.setString(3, q.getType());

            if (q.getTestId() == null) ps.setNull(4, Types.INTEGER);
            else ps.setInt(4, q.getTestId());

            if (q.getMatiereId() == null) ps.setNull(5, Types.INTEGER);
            else ps.setInt(5, q.getMatiereId());

            if (q.getThemeId() == null) ps.setNull(6, Types.INTEGER);
            else ps.setInt(6, q.getThemeId());

            ps.setInt(7, q.getId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean delete(int id) {
        ReponseDAO.deleteByQuestion(id);

        String sql = "DELETE FROM question WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private static Question map(ResultSet rs) throws Exception {
        Question q = new Question();

        q.setId(rs.getInt("id"));
        q.setLibelle(rs.getString("libelle"));
        q.setTheme(rs.getString("theme")); // Champ texte "theme" (peut être supprimé plus tard si inutile)
        q.setType(rs.getString("type"));

        int t = rs.getInt("test_id");
        q.setTestId(rs.wasNull() ? null : t);

        int m = rs.getInt("matiere_id");
        q.setMatiereId(rs.wasNull() ? null : m);

        int th = rs.getInt("theme_id");
        q.setThemeId(rs.wasNull() ? null : th);

        return q;
    }

    public static String findTypeById(int id) {
        String sql = "SELECT type FROM question WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("type");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
