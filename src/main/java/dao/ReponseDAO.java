package dao;

import model.Reponse;	
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReponseDAO {

    public static List<Reponse> findByQuestionId(int questionId) {
        return findByQuestion(questionId);
    }

    public static List<Reponse> findByQuestion(int questionId) {
        List<Reponse> list = new ArrayList<>();

        // ✅ Dans la base, la colonne s'appelle "libelle".
        // On lui donne un alias "texte" pour rester compatible avec le modèle.
        String sql = "SELECT id, libelle AS texte, correcte, question_id " +
                     "FROM reponse WHERE question_id = ? ORDER BY id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, questionId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Reponse r = new Reponse();
                    r.setId(rs.getInt("id"));
                    r.setTexte(rs.getString("texte")); // alias
                    r.setCorrecte(rs.getBoolean("correcte"));
                    r.setQuestionId(rs.getInt("question_id"));
                    list.add(r);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static boolean insert(Reponse r) {
        // ✅ INSERT utilise la colonne "libelle"
        String sql = "INSERT INTO reponse(libelle, correcte, question_id) VALUES (?,?,?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, r.getTexte());
            ps.setBoolean(2, r.isCorrecte());
            ps.setInt(3, r.getQuestionId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean update(Reponse r) {
        // ✅ UPDATE utilise la colonne "libelle"
        String sql = "UPDATE reponse SET libelle = ?, correcte = ? WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, r.getTexte());
            ps.setBoolean(2, r.isCorrecte());
            ps.setInt(3, r.getId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean delete(int id) {
        String sql = "DELETE FROM reponse WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean deleteByQuestion(int questionId) {
        String sql = "DELETE FROM reponse WHERE question_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, questionId);
            ps.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void unsetCorrectByQuestion(int questionId) {
        String sql = "UPDATE reponse SET correcte = 0 WHERE question_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, questionId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
