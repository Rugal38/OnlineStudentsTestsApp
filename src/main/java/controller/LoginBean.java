package controller;

import dao.CandidatDAO;		
import dao.CreneauDAO;
import dao.ResultatDAO;
import dao.TestDAO; // Added TestDAO import
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import model.Candidat;
import model.Creneau;
import model.Test; // Added Test import

import java.io.Serializable;

@Named("loginBean")
@SessionScoped
public class LoginBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String codeSession;

    private Candidat candidat;
    private Creneau creneau;
    private model.Test currentTest; // Added currentTest field

    private String message;
    private boolean startEnabled;

    private String testPassedMessage;
    private boolean showTestPassedMessage;

    @Inject
    private TestBean testBean;

    public String login() {
        message = null;
        startEnabled = false;
        candidat = null;
        creneau = null;
        currentTest = null; // Initialize currentTest
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
        
        // Fetch test-specific settings
        if (creneau.getTestId() != null) {
            currentTest = TestDAO.findById(creneau.getTestId());
            if (currentTest == null) {
                // Should not happen if data integrity is maintained
                message = "Test introuvable pour ce créneau.";
                startEnabled = false;
                return "loginSuccess.xhtml?faces-redirect=true";
            }
        } else {
            message = "Ce créneau n'est associé à aucun test.";
            startEnabled = false;
            return "loginSuccess.xhtml?faces-redirect=true";
        }

        // --- Check creneau timing (CAS A, B, C) ---
        if (CreneauDAO.isAfterCreneau(creneau)) {
            message = "⛔ Créneau passé… Veuillez choisir un autre créneau.";
            startEnabled = false;
            return "choixCreneau.xhtml?faces-redirect=true";
        }

        if (CreneauDAO.isBeforeCreneau(creneau)) {
            message = "⏳ Veuillez attendre l'heure de votre créneau.";
            startEnabled = false;
            return "loginSuccess.xhtml?faces-redirect=true";
        }

        message = "✅ Vous pouvez démarrer maintenant.";
        startEnabled = true;

        // --- Check for test attempts and success using test-specific settings ---
        if (currentTest != null && currentTest.getId() > 0) { // Ensure currentTest and its ID are valid
            int attempts = ResultatDAO.countAttempts(candidat.getId(), currentTest.getId());

            if (currentTest.getMaxTentatives() > 0 && attempts >= currentTest.getMaxTentatives()) {
                showTestPassedMessage = true;
                startEnabled = false; // Disable start button

                Integer lastScore = ResultatDAO.findLastScore(candidat.getId(), currentTest.getId());
                if (lastScore != null) {
                    int maxScorePossible = currentTest.getNbQuestions() * currentTest.getScoreParQuestion();
                    int scorePercentage = (maxScorePossible > 0) ? (int) Math.round(((double) lastScore / maxScorePossible) * 100) : 0;

                    if (scorePercentage >= currentTest.getSeuilReussite()) {
                        testPassedMessage = "✅ Vous avez déjà réussi ce test (" + currentTest.getTitre() + ") avec un score de " + scorePercentage + "% (" + lastScore + "/" + maxScorePossible + ").";
                    } else {
                        testPassedMessage = "⛔ Vous avez atteint le nombre maximal de tentatives (" + currentTest.getMaxTentatives() + ") pour le test (" + currentTest.getTitre() + ") avec un score de " + scorePercentage + "% (" + lastScore + "/" + maxScorePossible + ").";
                    }
                } else {
                    testPassedMessage = "⛔ Vous avez atteint le nombre maximal de tentatives (" + currentTest.getMaxTentatives() + ") pour le test (" + currentTest.getTitre() + ").";
                }
            } else if (attempts > 0) { // If attempts made but not maxed, check last result for pass
                Integer lastScore = ResultatDAO.findLastScore(candidat.getId(), currentTest.getId());
                if (lastScore != null) {
                    int maxScorePossible = currentTest.getNbQuestions() * currentTest.getScoreParQuestion();
                    int scorePercentage = (maxScorePossible > 0) ? (int) Math.round(((double) lastScore / maxScorePossible) * 100) : 0;

                    if (scorePercentage >= currentTest.getSeuilReussite()) {
                        testPassedMessage = "✅ Vous avez déjà réussi ce test (" + currentTest.getTitre() + ") avec un score de " + scorePercentage + "% (" + lastScore + "/" + maxScorePossible + ").";
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
            currentTest = null;
            message = null;
            startEnabled = false;
            testPassedMessage = null;
            showTestPassedMessage = false;
            return;
        }

        candidat = CandidatDAO.findByCodeSession(sessionC.getCodeSession());
        if (candidat == null) {
            creneau = null;
            currentTest = null;
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
            currentTest = null;
            testPassedMessage = null;
            showTestPassedMessage = false;
            return;
        }

        // Fetch test-specific settings
        if (creneau.getTestId() != null) {
            currentTest = TestDAO.findById(creneau.getTestId());
            if (currentTest == null) {
                message = "Test introuvable pour ce créneau.";
                startEnabled = false;
                testPassedMessage = null;
                showTestPassedMessage = false;
                return;
            }
        } else {
            message = "Ce créneau n'est associé à aucun test.";
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
        if (currentTest != null && currentTest.getId() > 0) { // Ensure currentTest and its ID are valid
            int attempts = ResultatDAO.countAttempts(candidat.getId(), currentTest.getId());

            if (currentTest.getMaxTentatives() > 0 && attempts >= currentTest.getMaxTentatives()) {
                showTestPassedMessage = true;
                startEnabled = false; // Disable start button

                Integer lastScore = ResultatDAO.findLastScore(candidat.getId(), currentTest.getId());
                if (lastScore != null) {
                    int maxScorePossible = currentTest.getNbQuestions() * currentTest.getScoreParQuestion();
                    int scorePercentage = (maxScorePossible > 0) ? (int) Math.round(((double) lastScore / maxScorePossible) * 100) : 0;

                    if (scorePercentage >= currentTest.getSeuilReussite()) {
                        testPassedMessage = "✅ Vous avez déjà réussi ce test (" + currentTest.getTitre() + ") avec un score de " + scorePercentage + "% (" + lastScore + "/" + maxScorePossible + ").";
                    } else {
                        testPassedMessage = "⛔ Vous avez atteint le nombre maximal de tentatives (" + currentTest.getMaxTentatives() + ") pour le test (" + currentTest.getTitre() + ") avec un score de " + scorePercentage + "% (" + lastScore + "/" + maxScorePossible + ").";
                    }
                } else {
                    testPassedMessage = "⛔ Vous avez atteint le nombre maximal de tentatives (" + currentTest.getMaxTentatives() + ") pour le test (" + currentTest.getTitre() + ").";
                }
            } else if (attempts > 0) { // If attempts made but not maxed, check last result for pass
                Integer lastScore = ResultatDAO.findLastScore(candidat.getId(), currentTest.getId());
                if (lastScore != null) {
                    int maxScorePossible = currentTest.getNbQuestions() * currentTest.getScoreParQuestion();
                    int scorePercentage = (maxScorePossible > 0) ? (int) Math.round(((double) lastScore / maxScorePossible) * 100) : 0;

                    if (scorePercentage >= currentTest.getSeuilReussite()) {
                        testPassedMessage = "✅ Vous avez déjà réussi ce test (" + currentTest.getTitre() + ") avec un score de " + scorePercentage + "% (" + lastScore + "/" + maxScorePossible + ").";
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
        currentTest = null; // Reset currentTest
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
    public model.Test getCurrentTest() { return currentTest; } // Add getter for currentTest

    public String getMessage() { return message; }

    public boolean isStartEnabled() { return startEnabled; }

    public String getTestPassedMessage() { return testPassedMessage; }
    public boolean isShowTestPassedMessage() { return showTestPassedMessage; }
}
