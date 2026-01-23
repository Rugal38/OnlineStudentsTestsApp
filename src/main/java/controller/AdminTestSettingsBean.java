package controller;

import dao.SettingsDAO;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import model.TestSettings;

import java.io.Serializable;

@Named("adminTestSettingsBean")
@SessionScoped
public class AdminTestSettingsBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private TestSettings settings;

    private String message;
    private boolean success;

    @PostConstruct
    public void init() {
        load();
    }

    public void load() {
        message = null;
        success = false;

        settings = SettingsDAO.get();
        if (settings == null) {
            // Valeurs par défaut si la ligne n'existe pas dans la base
            settings = new TestSettings();
            settings.setNbQuestions(10);
            settings.setShuffleQuestions(true);
            settings.setShuffleReponses(true);
            settings.setScoreParQuestion(1);
            settings.setSeuilReussite(50);
            settings.setAfficherResultatFin(true);
            settings.setAfficherCorrection(false);
            settings.setMaxTentatives(1);

            message = "⚠️ Les paramètres (id = 1) n'existent pas dans la base de données. Veuillez faire un INSERT SQL.";
            success = false;
        }
    }

    public String save() {
        message = null;
        success = false;

        // Validations
        if (settings.getNbQuestions() <= 0) {
            message = "❌ Le nombre de questions doit être supérieur à 0.";
            return null;
        }
        if (settings.getScoreParQuestion() <= 0) {
            message = "❌ Le score par question doit être supérieur à 0.";
            return null;
        }
        if (settings.getSeuilReussite() < 0 || settings.getSeuilReussite() > 100) {
            message = "❌ Le seuil de réussite doit être compris entre 0 et 100.";
            return null;
        }
        if (settings.getMaxTentatives() <= 0) {
            message = "❌ Le nombre maximum de tentatives doit être supérieur ou égal à 1.";
            return null;
        }

        boolean ok = SettingsDAO.update(settings);
        if (ok) {
            success = true;
            message = "✅ Paramètres enregistrés avec succès.";
        } else {
            message = "❌ Échec de l'enregistrement. Vérifiez la table test_settings et la ligne id = 1.";
        }

        return null; // Rester sur la même page
    }

    // Getters
    public TestSettings getSettings() { return settings; }
    public String getMessage() { return message; }
    public boolean isSuccess() { return success; }
}
