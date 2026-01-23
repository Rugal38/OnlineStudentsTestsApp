package controller;

import dao.CandidatDAO;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import model.Candidat;

import java.io.Serializable;
import java.util.UUID;

@Named("inscriptionBean")
@SessionScoped
public class InscriptionBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nom;
    private String prenom;
    private String email;
    private String gsm;
    private String ecole;
    private String filiere;

    private String message;

    private Candidat candidatInscrit;
    private String codeGenere;

    public String inscrire() {
        message = null;

        // ✅ clean inputs
        String n = trimToNull(nom);
        String p = trimToNull(prenom);
        String em = trimToNull(email);

        if (n == null || p == null || em == null) {
            message = "Veuillez remplir au minimum: Nom, Prénom, Email.";
            return null;
        }

        // ✅ generate unique code_session (8 chars)
        String code;
        do {
            code = UUID.randomUUID().toString().replace("-", "")
                    .substring(0, 8).toUpperCase();
        } while (CandidatDAO.codeExists(code));

        Candidat c = new Candidat();
        c.setNom(n);
        c.setPrenom(p);
        c.setEmail(em);
        c.setGsm(trimToNull(gsm));
        c.setEcole(trimToNull(ecole));
        c.setFiliere(trimToNull(filiere));
        c.setCodeSession(code);

        // ✅ candidate not chosen slot yet
        c.setCreneauId(null);

        boolean ok = CandidatDAO.insert(c);
        if (!ok) {
            message = "Erreur lors de l'inscription.";
            return null;
        }

        // ✅ fetch saved candidate to ensure ID
        Candidat saved = CandidatDAO.findByCodeSession(code);
        if (saved == null) {
            message = "Inscription OK mais impossible de recharger le candidat.";
            return null;
        }

        candidatInscrit = saved;
        codeGenere = code;

        // ✅ store in session for choixCreneau
        FacesContext.getCurrentInstance().getExternalContext()
                .getSessionMap().put("candidatConnecte", saved);

        // ✅ clear form
        resetForm();

        // ✅ IMPORTANT: redirect to avoid ViewExpired (POST->GET)
        return "choixCreneau.xhtml?faces-redirect=true";
    }

    private void resetForm() {
        nom = "";
        prenom = "";
        email = "";
        gsm = "";
        ecole = "";
        filiere = "";
    }

    private String trimToNull(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    // getters/setters
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getGsm() { return gsm; }
    public void setGsm(String gsm) { this.gsm = gsm; }

    public String getEcole() { return ecole; }
    public void setEcole(String ecole) { this.ecole = ecole; }

    public String getFiliere() { return filiere; }
    public void setFiliere(String filiere) { this.filiere = filiere; }

    public String getMessage() { return message; }

    public Candidat getCandidatInscrit() { return candidatInscrit; }
    public String getCodeGenere() { return codeGenere; }
}
