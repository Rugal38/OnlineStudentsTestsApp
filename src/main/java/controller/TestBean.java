package controller;

import dao.CreneauDAO;
import dao.QuestionDAO;
import dao.ReponseDAO;
import dao.ResultatDAO;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import model.Candidat;
import model.Creneau;
import model.Question;
import model.Reponse;
import utils.EmailUtil;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

@Named("testBean")
@SessionScoped
public class TestBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final int QUESTIONS_PER_TEST = 10;

    private int testId;
    private int candidatId;

    private List<Question> questions = new ArrayList<>();
    private int index = 0;

    private Question currentQuestion;
    private Integer selectedReponseId;      // single
    private List<Integer> selectedReponses; // multiple

    private int scoreFinal = 0;
    private int numeroQuestion = 0;
    private int totalQuestions = 0;
    private Timestamp lastDatePassage;

    @PostConstruct
    public void init() {
        selectedReponses = new ArrayList<>();
    }

    public String startTest() {
        resetState();

        FacesContext fc = FacesContext.getCurrentInstance();
        Candidat candidat = (Candidat) fc.getExternalContext().getSessionMap().get("candidatConnecte");
        if (candidat == null) {
            return "login.xhtml?faces-redirect=true";
        }

        candidatId = candidat.getId();

        Integer creneauId = candidat.getCreneauId();
        if (creneauId == null) return "loginSuccess.xhtml?faces-redirect=true";

        Creneau cr = CreneauDAO.findById(creneauId);
        if (cr == null || cr.getTestId() == null) return "loginSuccess.xhtml?faces-redirect=true";

        testId = cr.getTestId();

        loadRandomQuestionsBalancedByTheme(testId, QUESTIONS_PER_TEST);

        totalQuestions = (questions != null) ? questions.size() : 0;

        if (totalQuestions == 0) {
            currentQuestion = null;
            numeroQuestion = 0;
            return "passTest.xhtml?faces-redirect=true";
        }

        index = 0;
        setCurrentQuestion(questions.get(0));
        numeroQuestion = 1;

        return "passTest.xhtml?faces-redirect=true";
    }

    public String next() {
        checkAndAddScore();

        index++;
        clearSelections();

        if (index >= totalQuestions) {
            return finish();
        }

        setCurrentQuestion(questions.get(index));
        numeroQuestion = index + 1;

        return null;
    }

    public String autoNext() {
        return next();
    }

    public String finish() {
        if (candidatId > 0 && testId > 0) {
            ResultatDAO.insert(scoreFinal, candidatId, testId);
            lastDatePassage = ResultatDAO.findLastDate(candidatId, testId);

            try {
                FacesContext fc = FacesContext.getCurrentInstance();
                Candidat candidat = (Candidat) fc.getExternalContext().getSessionMap().get("candidatConnecte");

                if (candidat != null && candidat.getEmail() != null && !candidat.getEmail().trim().isEmpty()) {
                    String subject = "Résultat du test";
                    String body =
                            "Bonjour " + candidat.getNom() + " " + candidat.getPrenom() + "\n\n" +
                            "Votre test est terminé ✅\n" +
                            "Score : " + scoreFinal + "\n" +
                            "Date passage : " + (lastDatePassage != null ? lastDatePassage.toString().replace(".0","") : "") + "\n\n" +
                            "Bon courage.";

                    EmailUtil.sendEmail(candidat.getEmail(), subject, body);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "resultat.xhtml?faces-redirect=true";
    }

    private void loadRandomQuestionsBalancedByTheme(int testId, int wanted) {
        List<Question> all;
        try {
            all = QuestionDAO.findByTestId(testId);
        } catch (Exception e) {
            e.printStackTrace();
            all = new ArrayList<>();
        }

        for (Question q : all) {
            try {
                List<Reponse> reps = ReponseDAO.findByQuestionId(q.getId());
                q.setReponses(reps);
            } catch (Exception e) {
                e.printStackTrace();
                q.setReponses(new ArrayList<>());
            }
        }

        if (all.isEmpty()) {
            questions = new ArrayList<>();
            return;
        }

        Map<String, List<Question>> byTheme = new LinkedHashMap<>();
        for (Question q : all) {
            String theme = (q.getTheme() == null || q.getTheme().trim().isEmpty()) ? "DEFAULT" : q.getTheme().trim();
            byTheme.computeIfAbsent(theme, k -> new ArrayList<>()).add(q);
        }

        Random rnd = new Random();
        for (List<Question> list : byTheme.values()) {
            Collections.shuffle(list, rnd);
        }

        List<Question> picked = new ArrayList<>();
        int target = (wanted <= 0) ? all.size() : Math.min(wanted, all.size());

        boolean added;
        do {
            added = false;
            for (List<Question> list : byTheme.values()) {
                if (picked.size() >= target) break;
                if (!list.isEmpty()) {
                    picked.add(list.remove(0));
                    added = true;
                }
            }
        } while (added && picked.size() < target);

        Collections.shuffle(picked, rnd);
        questions = picked;
    }

 // ==========================
 // SCORING
 // ==========================
 private void checkAndAddScore() {
     if (currentQuestion == null) return;

     List<Reponse> reps = currentQuestion.getReponses();
     if (reps == null || reps.isEmpty()) return;

     String type = (currentQuestion.getType() == null) ? "" : currentQuestion.getType().trim();

     if ("single".equalsIgnoreCase(type)) {
         if (selectedReponseId == null) return;

         for (Reponse r : reps) {
             if (Objects.equals(r.getId(), selectedReponseId) && r.isCorrecte()) {
                 scoreFinal++;
                 break;
             }
         }

     } else if ("multiple".equalsIgnoreCase(type)) {
         if (selectedReponses == null) selectedReponses = new ArrayList<>();

         Set<Integer> correct = new HashSet<>();
         for (Reponse r : reps) {
             if (r.isCorrecte()) correct.add(r.getId());
         }

         Set<Integer> sel = new HashSet<>(selectedReponses);

         if (!correct.isEmpty() && sel.equals(correct)) {
             scoreFinal++;
         }
     }
 }


    private void resetState() {
        scoreFinal = 0;
        index = 0;
        numeroQuestion = 0;
        totalQuestions = 0;
        currentQuestion = null;
        lastDatePassage = null;
        questions = new ArrayList<>();
        clearSelections();
    }

    private void clearSelections() {
        selectedReponseId = null;
        selectedReponses = new ArrayList<>();
    }

    private void setCurrentQuestion(Question q) {
        currentQuestion = q;
    }

    public Question getCurrentQuestion() { return currentQuestion; }

    public Integer getSelectedReponseId() { return selectedReponseId; }
    public void setSelectedReponseId(Integer selectedReponseId) { this.selectedReponseId = selectedReponseId; }

    public List<Integer> getSelectedReponses() { return selectedReponses; }
    public void setSelectedReponses(List<Integer> selectedReponses) { this.selectedReponses = selectedReponses; }

    public int getScoreFinal() { return scoreFinal; }
    public int getNumeroQuestion() { return numeroQuestion; }
    public int getTotalQuestions() { return totalQuestions; }

    public Timestamp getLastDatePassage() { return lastDatePassage; }
    public void setLastDatePassage(Timestamp lastDatePassage) { this.lastDatePassage = lastDatePassage; }
}
