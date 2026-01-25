package dao;

import model.TestSettings;
import utils.DBConnection;

import java.sql.*;

public class SettingsDAO {

    public static TestSettings get() {
        String sql = "SELECT * FROM test_settings WHERE id=1";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                TestSettings s = new TestSettings();
                s.setId(rs.getInt("id"));
                s.setNbQuestions(rs.getInt("nb_questions"));
                s.setShuffleQuestions(rs.getBoolean("shuffle_questions"));
                s.setShuffleReponses(rs.getBoolean("shuffle_reponses"));
                s.setScoreParQuestion(rs.getInt("score_par_question"));
                s.setSeuilReussite(rs.getInt("seuil_reussite"));
                s.setAfficherResultatFin(rs.getBoolean("afficher_resultat_fin"));
                s.setAfficherCorrection(rs.getBoolean("afficher_correction"));
                s.setMaxTentatives(rs.getInt("max_tentatives"));
                s.setDuree(rs.getInt("duree"));
                return s;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean update(TestSettings s) {
        String sql = "UPDATE test_settings SET nb_questions=?, shuffle_questions=?, shuffle_reponses=?, " +
                "score_par_question=?, seuil_reussite=?, afficher_resultat_fin=?, afficher_correction=?, max_tentatives=?, duree=? " +
                "WHERE id=1";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, s.getNbQuestions());
            ps.setBoolean(2, s.isShuffleQuestions());
            ps.setBoolean(3, s.isShuffleReponses());
            ps.setInt(4, s.getScoreParQuestion());
            ps.setInt(5, s.getSeuilReussite());
            ps.setBoolean(6, s.isAfficherResultatFin());
            ps.setBoolean(7, s.isAfficherCorrection());
            ps.setInt(8, s.getMaxTentatives());
            ps.setInt(9, s.getDuree());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
