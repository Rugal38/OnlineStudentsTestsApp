package model;

import java.io.Serializable;
import java.sql.Date;

public class Test implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String titre;
    private Integer duree;
    private Date dateTest;

    public Test() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public Integer getDuree() { return duree; }
    public void setDuree(Integer duree) { this.duree = duree; }

    public Date getDateTest() { return dateTest; }
    public void setDateTest(Date dateTest) { this.dateTest = dateTest; }
}
