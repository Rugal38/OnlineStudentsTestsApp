package controller;

import dao.CandidatDAO;
import dao.CreneauDAO;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import model.Candidat;
import model.Creneau;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("adminInscriptionsBean")
@SessionScoped
public class AdminInscriptionsBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Candidat> candidats = new ArrayList<>();
    private List<Creneau> creneauxDisponibles = new ArrayList<>();

    // Créneau sélectionné dans la liste déroulante (utilisé pour chaque ligne)
    private Integer selectedCreneauId;

    public void load() {
        candidats = CandidatDAO.findAll();
        creneauxDisponibles = CreneauDAO.getDisponibles();
    }

    public String labelCreneau(Integer creneauId) {
        if (creneauId == null) return "Aucun";
        Creneau c = CreneauDAO.findById(creneauId);
        if (c == null) return "Aucun";
        return c.getDateExam() + " " + c.getHeureDebut() + " → " + c.getHeureFin() + " | Test " + c.getTestId();
    }

    public void affecter(int candidatId) {
        if (selectedCreneauId == null) {
            msg("❌ Veuillez choisir un créneau dans la liste.", FacesMessage.SEVERITY_ERROR);
            return;
        }

        boolean ok = CandidatDAO.updateCreneau(candidatId, selectedCreneauId);
        if (!ok) {
            msg("❌ Impossible de mettre à jour le candidat.", FacesMessage.SEVERITY_ERROR);
            return;
        }

        // Optionnel : après affectation, marquer le créneau comme réservé (disponible = false)
        CreneauDAO.reserver(selectedCreneauId);

        // Rafraîchir les données
        load();
        selectedCreneauId = null;

        msg("✅ Créneau affecté avec succès.", FacesMessage.SEVERITY_INFO);
    }

    private void msg(String texte, FacesMessage.Severity severite) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severite, texte, null));
    }

    // Getters / Setters
    public List<Candidat> getCandidats() { return candidats; }
    public List<Creneau> getCreneauxDisponibles() { return creneauxDisponibles; }

    public Integer getSelectedCreneauId() { return selectedCreneauId; }
    public void setSelectedCreneauId(Integer selectedCreneauId) { this.selectedCreneauId = selectedCreneauId; }
}
