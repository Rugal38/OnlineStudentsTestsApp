package controller;

import dao.QuestionDAO;
import dao.TestDAO;
import dao.ThemeDAO;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import model.Question;
import model.Test;
import model.Theme;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("adminQuestionBean")
@SessionScoped
public class AdminQuestionBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Question> questions = new ArrayList<>();
    private Question form = new Question();
    private boolean editMode = false;

    private Integer filterTestId;

    // ✅ Liste déroulante des thèmes (selon la matière)
    private List<Theme> themes = new ArrayList<>();

    // ✅ Liste déroulante des tests
    private List<Test> tests = new ArrayList<>();

    @PostConstruct
    public void init() {
        tests = TestDAO.findAll(); // ✅ charger les tests une seule fois
        load();
    }

    public void load() {
        if (filterTestId == null) {
            questions = QuestionDAO.findAll();
        } else {
            questions = QuestionDAO.findByTest(filterTestId);
        }
    }

    public void clearFilter() {
        filterTestId = null;
        load();
    }

    public String goAdd() {
        form = new Question();
        form.setType("single");
        editMode = false;

        // Rafraîchir les listes déroulantes
        tests = TestDAO.findAll();
        themes = new ArrayList<>();
        form.setThemeId(null);

        return "addQuestion.xhtml?faces-redirect=true";
    }

    public String goEdit(int id) {
        Question q = QuestionDAO.findById(id);
        if (q == null) {
            load();
            return "adminQuestions.xhtml?faces-redirect=true";
        }

        form = q;

        if (form.getType() == null || form.getType().trim().isEmpty()) {
            form.setType("single");
        }

        // Rafraîchir la liste des tests
        tests = TestDAO.findAll();

        // Charger les thèmes de la matière sélectionnée
        if (form.getMatiereId() != null) {
            themes = ThemeDAO.findByMatiere(form.getMatiereId());
        } else {
            themes = new ArrayList<>();
            form.setThemeId(null);
        }

        editMode = true;
        return "editQuestion.xhtml?faces-redirect=true";
    }

    // ✅ appelée par f:ajax quand la matière change
    public void onMatiereChange() {
        Integer matiereId = form.getMatiereId();

        if (matiereId == null) {
            themes = new ArrayList<>();
            form.setThemeId(null);
            return;
        }

        themes = ThemeDAO.findByMatiere(matiereId);
        form.setThemeId(null);
    }

    public String save() {
        // Valeur par défaut
        if (form.getType() == null || form.getType().trim().isEmpty()) {
            form.setType("single");
        }

        // Le test peut être null
        if (form.getTestId() != null && form.getTestId() <= 0) {
            form.setTestId(null);
        }

        // ✅ Synchroniser le nom du thème (optionnel mais pratique pour l'affichage)
        if (form.getThemeId() != null) {
            String themeName = ThemeDAO.findNameById(form.getThemeId());
            form.setTheme(themeName);
        } else {
            form.setTheme(null);
        }

        if (editMode) {
            QuestionDAO.update(form);
            load();
            return "adminQuestions.xhtml?faces-redirect=true";
        } else {
            Integer newId = QuestionDAO.insertAndReturnId(form);
            load();

            if (newId != null) {
                return "addReponses.xhtml?faces-redirect=true&qid=" + newId;
            }

            return "adminQuestions.xhtml?faces-redirect=true";
        }
    }

    public void delete(int id) {
        QuestionDAO.delete(id);
        load();
    }

    public String backToList() {
        load();
        return "adminQuestions.xhtml?faces-redirect=true";
    }

    public String manageReponses(int questionId) {
        return "addReponses.xhtml?faces-redirect=true&qid=" + questionId;
    }

    // Getters / Setters
    public List<Question> getQuestions() { return questions; }

    public Question getForm() { return form; }
    public void setForm(Question form) { this.form = form; }

    public boolean isEditMode() { return editMode; }

    public Integer getFilterTestId() { return filterTestId; }
    public void setFilterTestId(Integer filterTestId) { this.filterTestId = filterTestId; }

    public List<Theme> getThemes() { return themes; }
    public void setThemes(List<Theme> themes) { this.themes = themes; }

    public List<Test> getTests() { return tests; }
    public void setTests(List<Test> tests) { this.tests = tests; }
}
