package dao;

import model.Candidat;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CandidatDAO {

    // =========================
    // INSERTION
    // =========================
    public static boolean insert(Candidat c) {
        String sql = "INSERT INTO candidat(nom, prenom, email, gsm, ecole, filiere, code_session, creneau_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, c.getNom());
            ps.setString(2, c.getPrenom());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getGsm());
            ps.setString(5, c.getEcole());
            ps.setString(6, c.getFiliere());
            ps.setString(7, c.getCodeSession());

            if (c.getCreneauId() == null) {
                ps.setNull(8, Types.INTEGER);
            } else {
                ps.setInt(8, c.getCreneauId());
            }

            int ok = ps.executeUpdate();
            if (ok > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        c.setId(rs.getInt(1));
                    }
                }
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // =========================
    // VÉRIFIER SI UN CODE EXISTE
    // =========================
    public static boolean codeExists(String codeSession) {
        String sql = "SELECT 1 FROM candidat WHERE code_session = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codeSession);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // =========================
    // RECHERCHER PAR CODE SESSION
    // =========================
    public static Candidat findByCodeSession(String codeSession) {
        Candidat c = null;

        String sql = "SELECT id, nom, prenom, email, gsm, ecole, filiere, code_session, creneau_id " +
                     "FROM candidat WHERE code_session = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codeSession);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    c = map(rs);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return c;
    }

    // Alias de compatibilité (si un ancien code appelle une signature différente)
    public static Candidat findByCodeSession(String codeSession, boolean unused) {
        return findByCodeSession(codeSession);
    }

    // =========================
    // METTRE À JOUR LE CRÉNEAU DU CANDIDAT
    // =========================
    public static boolean updateCreneau(int candidatId, int creneauId) {
        String sql = "UPDATE candidat SET creneau_id = ? WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, creneauId);
            ps.setInt(2, candidatId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // =========================
    // LISTE DE TOUS LES CANDIDATS (ADMIN)
    // =========================
    public static List<Candidat> findAll() {
        List<Candidat> list = new ArrayList<>();

        String sql = "SELECT id, nom, prenom, email, gsm, ecole, filiere, code_session, creneau_id " +
                     "FROM candidat ORDER BY id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // =========================
    // MÉTHODE DE MAPPING
    // =========================
    private static Candidat map(ResultSet rs) throws Exception {
        Candidat c = new Candidat();

        c.setId(rs.getInt("id"));
        c.setNom(rs.getString("nom"));
        c.setPrenom(rs.getString("prenom"));
        c.setEmail(rs.getString("email"));
        c.setGsm(rs.getString("gsm"));
        c.setEcole(rs.getString("ecole"));
        c.setFiliere(rs.getString("filiere"));
        c.setCodeSession(rs.getString("code_session"));

        int cid = rs.getInt("creneau_id");
        c.setCreneauId(rs.wasNull() ? null : cid);

        return c;
    }
}
