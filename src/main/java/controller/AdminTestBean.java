package controller;

import dao.TestDAO;	
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import model.Test;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Named("adminTestBean")
@SessionScoped
public class AdminTestBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Test> tests = new ArrayList<>();
    private Test form = new Test();
    private boolean editMode = false;

    private LocalDate selectedDate;

    private String message;
    private boolean success;

    @PostConstruct
    public void init() {
        load();
    }

    public void load() {
        tests = TestDAO.findAll();
    }

    public String goAdd() {
        form = new Test();
        selectedDate = null; // Initialize selectedDate for new test
        editMode = false;
        message = null;
        success = false;
        return "addEditTest.xhtml?faces-redirect=true";
    }

    public String goEdit(int id) {
        Test t = TestDAO.findById(id);
        if (t == null) {
            load();
            message = "❌ Test introuvable.";
            success = false;
            return "adminManageTests.xhtml?faces-redirect=true"; // Redirect back to list
        }

        form = t;
        selectedDate = (t.getDateTest() != null) ? t.getDateTest().toLocalDate() : null; // Initialize selectedDate for existing test
        editMode = true;
        message = null;
        success = false;
        return "addEditTest.xhtml?faces-redirect=true";
    }

    public String save() {
        message = null;
        success = false;

        try {
            // Validate form
            if (form.getTitre() == null || form.getTitre().trim().isEmpty()) {
                message = "❌ Le titre du test est requis.";
                return null;
            }
            if (selectedDate != null) {
                form.setDateTest(Date.valueOf(selectedDate));
            } else {
                form.setDateTest(null);
            }

            // Validate settings
            if (form.getNbQuestions() <= 0) { message = "❌ Le nombre de questions doit être > 0."; return null; }
            if (form.getScoreParQuestion() <= 0) { message = "❌ Le score par question doit être > 0."; return null; }
            if (form.getSeuilReussite() < 0 || form.getSeuilReussite() > 100) { message = "❌ Le seuil de réussite doit être entre 0 et 100."; return null; }
            if (form.getMaxTentatives() <= 0) { message = "❌ Le nombre max de tentatives doit être > 0."; return null; }
            if (form.getDuree() < 0) { message = "❌ La durée du test ne peut pas être négative."; return null; }


            boolean ok;
            if (editMode) {
                ok = TestDAO.update(form);
            } else {
                ok = TestDAO.insert(form);
            }

            if (ok) {
                success = true;
                message = "✅ Test enregistré avec succès.";
                load(); // Refresh list
                return "adminManageTests.xhtml?faces-redirect=true"; // Go back to list
            } else {
                message = "❌ Échec de l'enregistrement du test.";
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            message = "❌ Une erreur inattendue est survenue: " + e.getMessage();
            return null;
        }
    }

    public void delete(int id) {
        TestDAO.delete(id);
        load(); // Refresh list
        FacesContext.getCurrentInstance().addMessage(null, new jakarta.faces.application.FacesMessage(
                jakarta.faces.application.FacesMessage.SEVERITY_INFO, "Test supprimé!", null));
    }

    public String backToList() {
        load();
        return "adminManageTests.xhtml?faces-redirect=true";
    }


    // Getters / Setters
    public List<Test> getTests() { return tests; }
    public Test getForm() { return form; }
    public void setForm(Test form) { this.form = form; }
    public boolean isEditMode() { return editMode; }

    public LocalDate getSelectedDate() { return selectedDate; }
    public void setSelectedDate(LocalDate selectedDate) { this.selectedDate = selectedDate; }
    public String getMessage() { return message; }
    public boolean isSuccess() { return success; }
}
