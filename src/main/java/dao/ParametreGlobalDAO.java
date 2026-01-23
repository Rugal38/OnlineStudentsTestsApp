package dao;

import model.ParametreGlobal;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ParametreGlobalDAO {

    public static ParametreGlobal get() {
        String sql = "SELECT id, nb_questions_default, temps_question_minutes FROM parametre_global WHERE id=1";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                ParametreGlobal p = new ParametreGlobal();
                p.setId(rs.getInt("id"));
                p.setNbQuestionsDefault(rs.getInt("nb_questions_default"));
                p.setTempsQuestionMinutes(rs.getInt("temps_question_minutes"));
                return p;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean update(ParametreGlobal p) {
        String sql = "UPDATE parametre_global SET nb_questions_default=?, temps_question_minutes=? WHERE id=1";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, p.getNbQuestionsDefault());
            ps.setInt(2, p.getTempsQuestionMinutes());
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
