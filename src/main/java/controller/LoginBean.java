package controller;

import dao.CandidatDAO;
import dao.CreneauDAO;
import dao.ResultatDAO;
import dao.SettingsDAO;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import model.Candidat;
import model.Creneau;
import model.TestSettings;

import java.io.Serializable;

@Named("loginBean")
@SessionScoped
public class LoginBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String codeSession;

    private Candidat candidat;
    private Creneau creneau;

    private String message;
    private boolean startEnabled;

    private String testPassedMessage; // New field for passed test message
    private boolean showTestPassedMessage; // New field to control message visibility

    @Inject
    private TestBean testBean;

    public String login() {
        message = null;
        startEnabled = false;
        candidat = null;
        creneau = null;
        testPassedMessage = null;
        showTestPassedMessage = false;

        if (codeSession == null || codeSession.trim().isEmpty()) {
            message = "Veuillez saisir votre code session.";
            return null;
        }

        candidat = CandidatDAO.findByCodeSession(codeSession.trim());
        if (candidat == null) {
            message = "Code session invalide.";
            return null;
        }

        FacesContext.getCurrentInstance().getExternalContext()
                .getSessionMap().put("candidatConnecte", candidat);

        // Le candidat n'a pas encore choisi de créneau
        if (candidat.getCreneauId() == null) {
            message = "Vous n'avez pas encore choisi un créneau.";
            startEnabled = false;
            creneau = null;
            return "loginSuccess.xhtml?faces-redirect=true";
        }

        creneau = CreneauDAO.findById(candidat.getCreneauId());
        if (creneau == null) {
            message = "Créneau introuvable.";
            startEnabled = false;
            return "loginSuccess.xhtml?faces-redirect=true";
        }

        // ✅ CAS A : le créneau est passé
        if (CreneauDAO.isAfterCreneau(creneau)) {
            message = "⛔ Créneau passé… Veuillez choisir un autre créneau.";
            startEnabled = false;
            return "choixCreneau.xhtml?faces-redirect=true";
        }

        // ✅ CAS B : le créneau n'a pas encore commencé
        if (CreneauDAO.isBeforeCreneau(creneau)) {
            message = "⏳ Veuillez attendre l'heure de votre créneau.";
            startEnabled = false;
            return "loginSuccess.xhtml?faces-redirect=true";
        }

        // ✅ CAS C : actuellement dans le créneau
        message = "✅ Vous pouvez démarrer maintenant.";
        startEnabled = true;

        // --- Check for test attempts and success ---
        TestSettings testSettings = SettingsDAO.get();
        if (testSettings != null && creneau.getTestId() != null) {
            int attempts = ResultatDAO.countAttempts(candidat.getId(), creneau.getTestId());

            if (testSettings.getMaxTentatives() > 0 && attempts >= testSettings.getMaxTentatives()) {
                showTestPassedMessage = true;
                startEnabled = false; // Disable start button

                Integer lastScore = ResultatDAO.findLastScore(candidat.getId(), creneau.getTestId());
                if (lastScore != null) {
                    int maxScorePossible = testSettings.getNbQuestions() * testSettings.getScoreParQuestion();
                    int scorePercentage = (maxScorePossible > 0) ? (int) Math.round(((double) lastScore / maxScorePossible) * 100) : 0;

                    if (scorePercentage >= testSettings.getSeuilReussite()) {
                        testPassedMessage = "✅ Vous avez déjà réussi ce test avec un score de " + scorePercentage + "% (" + lastScore + "/" + maxScorePossible + ").";
                    } else {
                        testPassedMessage = "⛔ Vous avez atteint le nombre maximal de tentatives (" + testSettings.getMaxTentatives() + ") pour ce test avec un score de " + scorePercentage + "% (" + lastScore + "/" + maxScorePossible + ").";
                    }
                } else {
                    testPassedMessage = "⛔ Vous avez atteint le nombre maximal de tentatives (" + testSettings.getMaxTentatives() + ") pour ce test.";
                }
            } else if (attempts > 0) { // If attempts made but not maxed, check last result
                Integer lastScore = ResultatDAO.findLastScore(candidat.getId(), creneau.getTestId());
                if (lastScore != null) {
                    int maxScorePossible = testSettings.getNbQuestions() * testSettings.getScoreParQuestion();
                    int scorePercentage = (maxScorePossible > 0) ? (int) Math.round(((double) lastScore / maxScorePossible) * 100) : 0;

                    if (scorePercentage >= testSettings.getSeuilReussite()) {
                        testPassedMessage = "✅ Vous avez déjà réussi ce test avec un score de " + scorePercentage + "% (" + lastScore + "/" + maxScorePossible + ").";
                        showTestPassedMessage = true;
                        startEnabled = false; // Disable start button if already passed
                    }
                }
            }
        }
        // --- End check ---

        return "loginSuccess.xhtml?faces-redirect=true";
    }

    public String demarrerTest() {
        if (!startEnabled) return null;
        return (testBean != null) ? testBean.startTest() : "passTest.xhtml?faces-redirect=true";
    }

    public void reload() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc == null) return;

        Candidat sessionC = (Candidat) fc.getExternalContext()
                .getSessionMap().get("candidatConnecte");

        if (sessionC == null) {
            candidat = null;
            creneau = null;
            message = null;
            startEnabled = false;
            testPassedMessage = null;
            showTestPassedMessage = false;
            return;
        }

        candidat = CandidatDAO.findByCodeSession(sessionC.getCodeSession());
        if (candidat == null) {
            creneau = null;
            message = null;
            startEnabled = false;
            testPassedMessage = null;
            showTestPassedMessage = false;
            return;
        }

        fc.getExternalContext().getSessionMap().put("candidatConnecte", candidat);

        if (candidat.getCreneauId() != null) {
            creneau = CreneauDAO.findById(candidat.getCreneauId());
        } else {
            creneau = null;
        }

        if (creneau == null) {
            message = "Vous n'avez pas encore choisi un créneau.";
            startEnabled = false;
            testPassedMessage = null;
            showTestPassedMessage = false;
            return;
        }

        // ✅ Les trois cas
        if (CreneauDAO.isAfterCreneau(creneau)) {
            message = "⛔ Créneau passé… Veuillez choisir un autre créneau.";
            startEnabled = false;
            testPassedMessage = null;
            showTestPassedMessage = false;
            return;
        }

        if (CreneauDAO.isBeforeCreneau(creneau)) {
            message = "⏳ Veuillez attendre l'heure de votre créneau.";
            startEnabled = false;
            testPassedMessage = null;
            showTestPassedMessage = false;
            return;
        }

        message = "✅ Vous pouvez démarrer maintenant.";
        startEnabled = true;

        // --- Check for test attempts and success (same logic as login) ---
        TestSettings testSettings = SettingsDAO.get();
        if (testSettings != null && creneau.getTestId() != null) {
            int attempts = ResultatDAO.countAttempts(candidat.getId(), creneau.getTestId());

            if (testSettings.getMaxTentatives() > 0 && attempts >= testSettings.getMaxTentatives()) {
                showTestPassedMessage = true;
                startEnabled = false; // Disable start button

                Integer lastScore = ResultatDAO.findLastScore(candidat.getId(), creneau.getTestId());
                if (lastScore != null) {
                    int maxScorePossible = testSettings.getNbQuestions() * testSettings.getScoreParQuestion();
                    int scorePercentage = (maxScorePossible > 0) ? (int) Math.round(((double) lastScore / maxScorePossible) * 100) : 0;

                    if (scorePercentage >= testSettings.getSeuilReussite()) {
                        testPassedMessage = "✅ Vous avez déjà réussi ce test avec un score de " + scorePercentage + "% (" + lastScore + "/" + maxScorePossible + ").";
                    } else {
                        testPassedMessage = "⛔ Vous avez atteint le nombre maximal de tentatives (" + testSettings.getMaxTentatives() + ") pour ce test avec un score de " + scorePercentage + "% (" + lastScore + "/" + maxScorePossible + ").";
                    }
                } else {
                    testPassedMessage = "⛔ Vous avez atteint le nombre maximal de tentatives (" + testSettings.getMaxTentatives() + ") pour ce test.";
                }
            } else if (attempts > 0) { // If attempts made but not maxed, check last result
                Integer lastScore = ResultatDAO.findLastScore(candidat.getId(), creneau.getTestId());
                if (lastScore != null) {
                    int maxScorePossible = testSettings.getNbQuestions() * testSettings.getScoreParQuestion();
                    int scorePercentage = (maxScorePossible > 0) ? (int) Math.round(((double) lastScore / maxScorePossible) * 100) : 0;

                    if (scorePercentage >= testSettings.getSeuilReussite()) {
                        testPassedMessage = "✅ Vous avez déjà réussi ce test avec un score de " + scorePercentage + "% (" + lastScore + "/" + maxScorePossible + ").";
                        showTestPassedMessage = true;
                        startEnabled = false; // Disable start button if already passed
                    }
                }
            }
        }
        // --- End check ---
    }

    public String goInscription() {
        reset();
        return "inscription.xhtml?faces-redirect=true";
    }

    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "login.xhtml?faces-redirect=true";
    }

    public void reset() {
        codeSession = null;
        candidat = null;
        creneau = null;
        message = null;
        startEnabled = false;
        testPassedMessage = null;
        showTestPassedMessage = false;
    }

    // Getters / Setters
    public String getCodeSession() { return codeSession; }
    public void setCodeSession(String codeSession) { this.codeSession = codeSession; }

    public Candidat getCandidat() { return candidat; }
    public Creneau getCreneau() { return creneau; }

    public String getMessage() { return message; }

    public boolean isStartEnabled() { return startEnabled; }

    public String getTestPassedMessage() { return testPassedMessage; }
    public boolean isShowTestPassedMessage() { return showTestPassedMessage; }
}
