package controller;

import dao.CandidatDAO;	
import dao.CreneauDAO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import model.Candidat;
import model.Creneau;
import utils.EmailUtil;

import java.io.Serializable;
import java.util.List;

@Named("choixCreneauBean")
@RequestScoped
public class ChoixCreneauBean implements Serializable {

    private static final long serialVersionUID = 1L;

    public List<Creneau> getCreneaux() {
        // ✅ Uniquement les créneaux disponibles + non passés (le filtrage est géré côté DAO)
        return CreneauDAO.getDisponibles();
    }

    public String choisir(int creneauId) {
        FacesContext fc = FacesContext.getCurrentInstance();

        Candidat candidat = (Candidat) fc.getExternalContext()
                .getSessionMap().get("candidatConnecte");

        if (candidat == null) {
            return "login.xhtml?faces-redirect=true";
        }

        // ✅ Réserver le créneau (disponible = false) de manière sécurisée
        boolean ok = CreneauDAO.reserver(creneauId);
        if (!ok) {
            // Même si le créneau apparaît, il peut être pris entre-temps par quelqu'un d'autre
            fc.getExternalContext().getFlash().put("msg", "❌ Ce créneau est déjà pris. Veuillez en choisir un autre.");
            return "choixCreneau.xhtml?faces-redirect=true";
        }

        // ✅ Associer le candidat au créneau
        CandidatDAO.updateCreneau(candidat.getId(), creneauId);

        // ✅ Récupérer les informations du créneau pour l'e-mail
        Creneau cr = CreneauDAO.findById(creneauId);

        // ✅ E-mail : code session + infos créneau + instructions
        if (cr != null && candidat.getEmail() != null && !candidat.getEmail().trim().isEmpty()) {

            String subject = "Confirmation du créneau et code de session";

            String body =
                    "Bonjour " + candidat.getNom() + " " + candidat.getPrenom() + ",\n\n" +
                    "✅ Votre créneau est confirmé.\n" +
                    "📅 Date : " + cr.getDateExam() + "\n" +
                    "🕒 Heure : " + cr.getHeureDebut() + " - " + cr.getHeureFin() + "\n\n" +
                    "🔑 Code de session : " + candidat.getCodeSession() + "\n\n" +
                    "➡️ Étapes suivantes :\n" +
                    "1) Allez sur la page de connexion (candidat)\n" +
                    "2) Saisissez votre code de session\n" +
                    "3) Vous pourrez démarrer le test uniquement pendant votre créneau\n\n" +
                    "Bon courage.";

            EmailUtil.sendEmail(candidat.getEmail(), subject, body);
        }

        // ✅ Après le choix, rediriger vers la page de connexion
        fc.getExternalContext().invalidateSession(); // Optionnel : repartir sur une session propre
        return "login.xhtml?faces-redirect=true";
    }

    public String refresh() {
        return "choixCreneau.xhtml?faces-redirect=true";
    }
}
