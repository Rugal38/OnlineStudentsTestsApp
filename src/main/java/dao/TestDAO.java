package dao;

import model.Test;	
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TestDAO {

    public static List<Test> findAll() {
        List<Test> list = new ArrayList<>();
        String sql = "SELECT id, titre, duree, date_test, " +
                     "nb_questions, shuffle_questions, shuffle_reponses, score_par_question, " +
                     "seuil_reussite, afficher_resultat_fin, afficher_correction, max_tentatives " +
                     "FROM test ORDER BY id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToTest(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static Test findById(int id) {
        String sql = "SELECT id, titre, duree, date_test, " +
                     "nb_questions, shuffle_questions, shuffle_reponses, score_par_question, " +
                     "seuil_reussite, afficher_resultat_fin, afficher_correction, max_tentatives " +
                     "FROM test WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTest(rs);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean insert(Test test) {
        String sql = "INSERT INTO test (titre, duree, date_test, " +
                     "nb_questions, shuffle_questions, shuffle_reponses, score_par_question, " +
                     "seuil_reussite, afficher_resultat_fin, afficher_correction, max_tentatives) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, test.getTitre());
            ps.setObject(2, test.getDuree(), java.sql.Types.INTEGER);
            ps.setDate(3, test.getDateTest());
            ps.setInt(4, test.getNbQuestions());
            ps.setBoolean(5, test.isShuffleQuestions());
            ps.setBoolean(6, test.isShuffleReponses());
            ps.setInt(7, test.getScoreParQuestion());
            ps.setInt(8, test.getSeuilReussite());
            ps.setBoolean(9, test.isAfficherResultatFin());
            ps.setBoolean(10, test.isAfficherCorrection());
            ps.setInt(11, test.getMaxTentatives());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        test.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean update(Test test) {
        String sql = "UPDATE test SET titre=?, duree=?, date_test=?, " +
                     "nb_questions=?, shuffle_questions=?, shuffle_reponses=?, score_par_question=?, " +
                     "seuil_reussite=?, afficher_resultat_fin=?, afficher_correction=?, max_tentatives=? " +
                     "WHERE id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, test.getTitre());
            ps.setObject(2, test.getDuree(), java.sql.Types.INTEGER);
            ps.setDate(3, test.getDateTest());
            ps.setInt(4, test.getNbQuestions());
            ps.setBoolean(5, test.isShuffleQuestions());
            ps.setBoolean(6, test.isShuffleReponses());
            ps.setInt(7, test.getScoreParQuestion());
            ps.setInt(8, test.getSeuilReussite());
            ps.setBoolean(9, test.isAfficherResultatFin());
            ps.setBoolean(10, test.isAfficherCorrection());
            ps.setInt(11, test.getMaxTentatives());
            ps.setInt(12, test.getId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean delete(int id) {
        String sql = "DELETE FROM test WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static Test mapResultSetToTest(ResultSet rs) throws Exception {
        Test t = new Test();
        t.setId(rs.getInt("id"));
        t.setTitre(rs.getString("titre"));
        int duree = rs.getInt("duree");
        t.setDuree(rs.wasNull() ? null : duree);
        t.setDateTest(rs.getDate("date_test"));
        
        t.setNbQuestions(rs.getInt("nb_questions"));
        t.setShuffleQuestions(rs.getBoolean("shuffle_questions"));
        t.setShuffleReponses(rs.getBoolean("shuffle_reponses"));
        t.setScoreParQuestion(rs.getInt("score_par_question"));
        t.setSeuilReussite(rs.getInt("seuil_reussite"));
        t.setAfficherResultatFin(rs.getBoolean("afficher_resultat_fin"));
        t.setAfficherCorrection(rs.getBoolean("afficher_correction"));
        t.setMaxTentatives(rs.getInt("max_tentatives"));
        return t;
    }
}