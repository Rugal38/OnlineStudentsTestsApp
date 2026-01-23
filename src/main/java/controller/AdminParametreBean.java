package controller;

import dao.ParametreGlobalDAO;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import model.ParametreGlobal;

import java.io.Serializable;

@Named("adminParametreBean")
@SessionScoped
public class AdminParametreBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private ParametreGlobal param = new ParametreGlobal();
    private String message;
    private boolean success;

    @PostConstruct
    public void init() {
        load();
    }

    public void load() {
        ParametreGlobal p = ParametreGlobalDAO.get();
        if (p != null) {
            param = p;
        } else {
            // Valeurs par défaut
            param.setNbQuestionsDefault(10);
            param.setTempsQuestionMinutes(2);
        }
        message = null;
        success = false;
    }

    public String save() {
        message = null;
        success = false;

        // Validations
        if (param.getNbQuestionsDefault() <= 0 || param.getNbQuestionsDefault() > 200) {
            message = "❌ Le nombre de questions doit être compris entre 1 et 200.";
            return null;
        }

        if (param.getTempsQuestionMinutes() <= 0 || param.getTempsQuestionMinutes() > 60) {
            message = "❌ Le temps par question doit être compris entre 1 et 60 minutes.";
            return null;
        }

        boolean ok = ParametreGlobalDAO.update(param);
        success = ok;
        message = ok ? "✅ Paramètres enregistrés avec succès." : "❌ Erreur lors de l'enregistrement des paramètres.";
        return null; // Rester sur la même page
    }

    // Navigation
    public String backToDashboard() {
        return "adminDashboard.xhtml?faces-redirect=true";
    }

    // Getters
    public ParametreGlobal getParam() { return param; }
    public String getMessage() { return message; }
    public boolean isSuccess() { return success; }
}
