package controller;

import dao.QuestionDAO;
import dao.ReponseDAO;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import model.Reponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("adminReponseBean")
@SessionScoped
public class AdminReponseBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer questionId;          // Paramètre de vue : qid
    private String questionType;         // "single" ou "multiple"

    private List<Reponse> reponses = new ArrayList<>();
    private Reponse form = new Reponse();

    @PostConstruct
    public void init() {
        // loadByQuestion() est appelé via <f:viewAction>
    }

    public void loadByQuestion() {
        if (questionId == null) {
            reponses = new ArrayList<>();
            questionType = null;
            return;
        }

        // ✅ Récupérer le type de question
        questionType = QuestionDAO.findTypeById(questionId);

        // ✅ Charger les réponses
        reponses = ReponseDAO.findByQuestionId(questionId);

        // ✅ Initialiser le formulaire
        if (form == null) {
            form = new Reponse();
        }
        form.setQuestionId(questionId);
    }

    public String add() {
        if (questionId == null) {
            return null;
        }

        String type = QuestionDAO.findTypeById(questionId);
        if (type == null) {
            type = "single"; // Valeur par défaut (sécurité)
        }
        questionType = type;

        // ✅ Si "single" : une seule réponse correcte autorisée
        if ("single".equalsIgnoreCase(type) && form.isCorrecte()) {
            ReponseDAO.unsetCorrectByQuestion(questionId);
        }

        form.setQuestionId(questionId);
        ReponseDAO.insert(form);

        // Réinitialiser + recharger
        form = new Reponse();
        form.setQuestionId(questionId);
        loadByQuestion();

        return null;
    }

    public void delete(int id) {
        ReponseDAO.delete(id);
        loadByQuestion();
    }

    public String backToQuestions() {
        return "adminQuestions.xhtml?faces-redirect=true";
    }

    // Getters / Setters
    public Integer getQuestionId() { return questionId; }
    public void setQuestionId(Integer questionId) { this.questionId = questionId; }

    public String getQuestionType() { return questionType; }
    public void setQuestionType(String questionType) { this.questionType = questionType; }

    public List<Reponse> getReponses() { return reponses; }
    public void setReponses(List<Reponse> reponses) { this.reponses = reponses; }

    public Reponse getForm() { return form; }
    public void setForm(Reponse form) { this.form = form; }
}
