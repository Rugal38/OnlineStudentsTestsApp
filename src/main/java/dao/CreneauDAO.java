package dao;

import model.Creneau;
import utils.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CreneauDAO {

    // ========= BASIC QUERIES =========

    public static List<Creneau> findAll() {
        List<Creneau> list = new ArrayList<>();
        String sql = "SELECT id, date_exam, heure_debut, heure_fin, disponible, test_id " +
                "FROM creneau ORDER BY date_exam, heure_debut";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(map(rs));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Creneau findById(int id) {
        String sql = "SELECT id, date_exam, heure_debut, heure_fin, disponible, test_id " +
                "FROM creneau WHERE id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Creneau findById(Integer id) {
        if (id == null) return null;
        return findById(id.intValue());
    }

    public static boolean insert(Creneau c) {
        String sql = "INSERT INTO creneau(date_exam, heure_debut, heure_fin, disponible, test_id) " +
                "VALUES (?,?,?,?,?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(c.getDateExam())); // YYYY-MM-DD
            ps.setTime(2, Time.valueOf(normalizeTime(c.getHeureDebut())));
            ps.setTime(3, Time.valueOf(normalizeTime(c.getHeureFin())));
            ps.setBoolean(4, c.isDisponible());

            if (c.getTestId() == null) ps.setNull(5, Types.INTEGER);
            else ps.setInt(5, c.getTestId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean update(Creneau c) {
        String sql = "UPDATE creneau SET date_exam=?, heure_debut=?, heure_fin=?, disponible=?, test_id=? WHERE id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(c.getDateExam()));
            ps.setTime(2, Time.valueOf(normalizeTime(c.getHeureDebut())));
            ps.setTime(3, Time.valueOf(normalizeTime(c.getHeureFin())));
            ps.setBoolean(4, c.isDisponible());

            if (c.getTestId() == null) ps.setNull(5, Types.INTEGER);
            else ps.setInt(5, c.getTestId());

            ps.setInt(6, c.getId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean delete(int id) {
        String sql = "DELETE FROM creneau WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ========= USER SIDE METHODS =========

    public static List<Creneau> getDisponibles() {
        List<Creneau> list = new ArrayList<>();

        String sql =
                "SELECT id, date_exam, heure_debut, heure_fin, disponible, test_id " +
                "FROM creneau " +
                "WHERE disponible=1 " +
                "AND (date_exam > CURRENT_DATE " +
                "     OR (date_exam = CURRENT_DATE AND heure_fin > CURRENT_TIME)) " +
                "ORDER BY date_exam, heure_debut";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(map(rs));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean reserver(int creneauId) {
        String sql = "UPDATE creneau SET disponible=0 WHERE id=? AND disponible=1";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, creneauId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ========= A / B / C TIME STATUS =========

    public static boolean isBeforeCreneau(Creneau c) {
        if (c == null) return false;
        LocalDate date = LocalDate.parse(c.getDateExam().trim());
        LocalTime start = parseTime(c.getHeureDebut());
        LocalDateTime startDT = LocalDateTime.of(date, start);
        return LocalDateTime.now().isBefore(startDT);
    }

    public static boolean isAfterCreneau(Creneau c) {
        if (c == null) return false;
        LocalDate date = LocalDate.parse(c.getDateExam().trim());
        LocalTime end = parseTime(c.getHeureFin());
        LocalDateTime endDT = LocalDateTime.of(date, end);
        return LocalDateTime.now().isAfter(endDT);
    }

    public static boolean isNowInCreneau(Creneau c) {
        if (c == null) return false;

        LocalDate date = LocalDate.parse(c.getDateExam().trim());
        LocalTime start = parseTime(c.getHeureDebut());
        LocalTime end = parseTime(c.getHeureFin());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDT = LocalDateTime.of(date, start);
        LocalDateTime endDT = LocalDateTime.of(date, end);

        return (!now.isBefore(startDT)) && (!now.isAfter(endDT));
    }

    // ========= AUTO HEURE_FIN FROM test.duree =========

    public static String computeHeureFin(Integer testId, String heureDebutStr) {
        if (testId == null || heureDebutStr == null || heureDebutStr.trim().isEmpty()) return null;

        Integer dureeMin = getTestDureeMinutes(testId);
        if (dureeMin == null) return null;

        LocalTime start = parseTime(heureDebutStr);
        LocalTime fin = start.plusMinutes(dureeMin);

        return fin.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public static Integer getTestDureeMinutes(int testId) {
        String sql = "SELECT duree FROM test WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, testId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int d = rs.getInt("duree");
                    return rs.wasNull() ? null : d;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ========= HELPERS =========

    private static Creneau map(ResultSet rs) throws Exception {
        Creneau c = new Creneau();
        c.setId(rs.getInt("id"));
        c.setDateExam(rs.getDate("date_exam").toString());     // YYYY-MM-DD
        c.setHeureDebut(rs.getTime("heure_debut").toString()); // HH:mm:ss
        c.setHeureFin(rs.getTime("heure_fin").toString());     // HH:mm:ss
        c.setDisponible(rs.getBoolean("disponible"));

        int t = rs.getInt("test_id");
        c.setTestId(rs.wasNull() ? null : t);

        return c;
    }

    private static String normalizeTime(String t) {
        if (t == null) return null;
        t = t.trim();

        // 1:12 -> 01:12:00
        if (t.matches("^\\d{1}:\\d{2}$")) t = "0" + t;
        // 01:12 -> 01:12:00
        if (t.matches("^\\d{2}:\\d{2}$")) return t + ":00";
        // 01:12:00
        if (t.matches("^\\d{2}:\\d{2}:\\d{2}$")) return t;

        return t;
    }

    private static LocalTime parseTime(String t) {
        String norm = normalizeTime(t);
        // norm = HH:mm:ss
        return LocalTime.parse(norm, DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}
