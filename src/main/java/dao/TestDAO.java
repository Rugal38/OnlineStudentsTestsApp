package dao;

import model.Test;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TestDAO {

    public static List<Test> findAll() {
        List<Test> list = new ArrayList<>();
        String sql = "SELECT id, titre, duree, date_test FROM test ORDER BY id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Test t = new Test();
                t.setId(rs.getInt("id"));
                t.setTitre(rs.getString("titre"));
                int d = rs.getInt("duree");
                t.setDuree(rs.wasNull() ? null : d);
                t.setDateTest(rs.getDate("date_test"));
                list.add(t);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
