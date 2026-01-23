package controller;

import dao.DashboardDAO;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("adminDashboardBean")
@SessionScoped
public class AdminDashboardBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private int totalQuestions;
    private int totalCandidats;
    private int totalCreneauxAVenir;
    private int totalDerniersResultats;

    private List<CreneauRow> creneauxAVenir = new ArrayList<>();
    private List<ResultatRow> derniersResultats = new ArrayList<>();

    public void load() {
        totalQuestions = DashboardDAO.countQuestions();
        totalCandidats = DashboardDAO.countCandidats();

        creneauxAVenir = DashboardDAO.findCreneauxAVenir(8);
        totalCreneauxAVenir = DashboardDAO.countCreneauxAVenir();

        derniersResultats = DashboardDAO.findDerniersResultats(8);
        totalDerniersResultats = DashboardDAO.countResultats();
    }

    // ===== Objets de transfert pour les tableaux du tableau de bord =====
    public static class CreneauRow {
        private int id;
        private String dateExam;
        private String heureDebut;
        private String heureFin;
        private boolean disponible;
        private Integer testId;
        private String testTitre;

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

        public String getTestTitre() { return testTitre; }
        public void setTestTitre(String testTitre) { this.testTitre = testTitre; }
    }

    public static class ResultatRow {
        private int id;
        private int score;
        private String datePassage;
        private String candidatNom;
        private String testTitre;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public int getScore() { return score; }
        public void setScore(int score) { this.score = score; }

        public String getDatePassage() { return datePassage; }
        public void setDatePassage(String datePassage) { this.datePassage = datePassage; }

        public String getCandidatNom() { return candidatNom; }
        public void setCandidatNom(String candidatNom) { this.candidatNom = candidatNom; }

        public String getTestTitre() { return testTitre; }
        public void setTestTitre(String testTitre) { this.testTitre = testTitre; }
    }

    // ===== Accesseurs (getters) =====
    public int getTotalQuestions() { return totalQuestions; }
    public int getTotalCandidats() { return totalCandidats; }
    public int getTotalCreneauxAVenir() { return totalCreneauxAVenir; }
    public int getTotalDerniersResultats() { return totalDerniersResultats; }

    public List<CreneauRow> getCreneauxAVenir() { return creneauxAVenir; }
    public List<ResultatRow> getDerniersResultats() { return derniersResultats; }
}
