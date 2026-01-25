

package controller;

import dao.DashboardDAO;	
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Named("adminDashboardBean")
@SessionScoped
public class AdminDashboardBean implements Serializable {

    private static final long serialVersionUID = 1L;

    // Stats globales
    private int totalQuestions;
    private int totalCandidats;
    private int totalCreneauxAVenir;
    private int totalDerniersResultats;

    // Données groupées
    private List<DayGroup<CreneauRow>> creneauxParJour = new ArrayList<>();
    private List<DayGroup<ResultatRow>> resultatsParJour = new ArrayList<>();

    // Données pour le graphique
    private Map<String, Integer> resultsChartData = new LinkedHashMap<>();
    private String resultsChartDataJson;


    @PostConstruct
    public void init() {
        load();
    }

    public void load() {
        // Stats
        totalQuestions = DashboardDAO.countQuestions();
        totalCandidats = DashboardDAO.countCandidats();
        totalCreneauxAVenir = DashboardDAO.countCreneauxAVenir();
        totalDerniersResultats = DashboardDAO.countResultats();

        // Créneaux à venir
        List<CreneauRow> creneaux = DashboardDAO.findCreneauxAVenir(100);
        Map<String, List<CreneauRow>> creneauxGrouped = creneaux.stream()
                .collect(Collectors.groupingBy(CreneauRow::getDateExam, LinkedHashMap::new, Collectors.toList()));
        creneauxParJour = creneauxGrouped.entrySet().stream()
                .map(entry -> new DayGroup<>(formatDate(entry.getKey()), entry.getValue()))
                .collect(Collectors.toList());


        // Derniers résultats
        List<ResultatRow> resultats = DashboardDAO.findDerniersResultats(100);
        Map<String, List<ResultatRow>> resultatsGrouped = resultats.stream()
                .collect(Collectors.groupingBy(ResultatRow::getDatePassageDay, LinkedHashMap::new, Collectors.toList()));
        resultatsParJour = resultatsGrouped.entrySet().stream()
                .map(entry -> new DayGroup<>(formatDate(entry.getKey()), entry.getValue()))
                .collect(Collectors.toList());

        // Données pour le graphique des résultats des 7 derniers jours
        resultsChartData = DashboardDAO.countResultsLast7Days();
        resultsChartDataJson = mapToJson(resultsChartData);
    }

    private String formatDate(String dateStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return date.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy"));
        } catch (Exception e) {
            return dateStr;
        }
    }

    private String mapToJson(Map<String, Integer> map) {
        return map.entrySet().stream()
                .map(e -> String.format("\"%s\": %d", e.getKey(), e.getValue()))
                .collect(Collectors.joining(", ", "{", "}"));
    }


    // ==================================================================
    // INNER CLASSES (DTOs)
    // ==================================================================

    public static class DayGroup<T> {
        private String day;
        private List<T> items;
        public DayGroup(String day, List<T> items) { this.day = day; this.items = items; }
        public String getDay() { return day; }
        public List<T> getItems() { return items; }
    }

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
        public String getDatePassageDay() {
            return (datePassage != null && datePassage.length() >= 10) ? datePassage.substring(0, 10) : "";
        }
    }


    // ==================================================================
    // GETTERS
    // ==================================================================

    public int getTotalQuestions() { return totalQuestions; }
    public int getTotalCandidats() { return totalCandidats; }
    public int getTotalCreneauxAVenir() { return totalCreneauxAVenir; }
    public int getTotalDerniersResultats() { return totalDerniersResultats; }

    public List<DayGroup<CreneauRow>> getCreneauxParJour() { return creneauxParJour; }
    public List<DayGroup<ResultatRow>> getResultatsParJour() { return resultatsParJour; }

    public Map<String, Integer> getResultsChartData() { return resultsChartData; }
    public String getResultsChartDataJson() { return resultsChartDataJson; }
}
