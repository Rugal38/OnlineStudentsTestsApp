package model;

import java.io.Serializable;

public class Creneau implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String dateExam;     // yyyy-mm-dd
    private String heureDebut;   // HH:mm:ss
    private String heureFin;     // HH:mm:ss
    private boolean disponible;
    private Integer testId;      // nullable

    public Creneau() {}

    public Creneau(int id, String dateExam, String heureDebut, String heureFin, boolean disponible, Integer testId) {
        this.id = id;
        this.dateExam = dateExam;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.disponible = disponible;
        this.testId = testId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDateExam() { return dateExam; }
    public void setDateExam(String dateExam) { this.dateExam = dateExam; }

    public String getHeureDebut() { return heureDebut; }
    public void setHeureDebut(String heureDebut) { this.heureDebut = heureDebut; }

    public String getHeureFin() { return heureFin; }
    public void setHeureFin(String heureFin) { this.heureFin = heureFin; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    public Integer getTestId() { return testId; }
    public void setTestId(Integer testId) { this.testId = testId; }
}
