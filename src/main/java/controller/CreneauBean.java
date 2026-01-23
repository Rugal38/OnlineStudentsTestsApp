package controller;

import dao.CandidatDAO;
import dao.CreneauDAO;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import model.Candidat;
import model.Creneau;

import java.io.Serializable;
import java.util.List;

@Named("creneauBean")
@SessionScoped
public class CreneauBean implements Serializable {

    private static final long serialVersionUID = 1L;

    public List<Creneau> getCreneauxDisponibles() {
        return CreneauDAO.getDisponibles();
    }

    public String choisirPourCandidat(int creneauId) {

        FacesContext fc = FacesContext.getCurrentInstance();
        Candidat candidat = (Candidat) fc.getExternalContext()
                .getSessionMap().get("candidatConnecte");

        if (candidat == null) {
            return "login.xhtml?faces-redirect=true";
        }

        // ✅ Réserver le créneau (disponible = false)
        boolean ok = CreneauDAO.reserver(creneauId);

        if (!ok) {
            return null; // Créneau déjà pris
        }

        // ✅ Mettre à jour candidat.creneau_id
        CandidatDAO.updateCreneau(candidat.getId(), creneauId);

        // ✅ Recharger le candidat depuis la base et mettre à jour la session
        Candidat updated = CandidatDAO.findByCodeSession(candidat.getCodeSession());
        fc.getExternalContext().getSessionMap().put("candidatConnecte", updated);

        return "loginSuccess.xhtml?faces-redirect=true";
    }
}
