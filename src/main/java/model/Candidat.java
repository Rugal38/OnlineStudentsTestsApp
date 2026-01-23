package model;

import java.io.Serializable;

public class Candidat implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String gsm;
    private String ecole;
    private String filiere;
    private String codeSession;
    private Integer creneauId;

    public Candidat() {}

    public Candidat(int id, String nom, String prenom, String email, String gsm,
                    String ecole, String filiere, String codeSession) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.gsm = gsm;
        this.ecole = ecole;
        this.filiere = filiere;
        this.codeSession = codeSession;
    }

    // ======== GETTERS / SETTERS ========

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

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

    public String getCodeSession() { return codeSession; }
    public void setCodeSession(String codeSession) { this.codeSession = codeSession; }

    @Override
    public String toString() {
        return "Candidat{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", gsm='" + gsm + '\'' +
                ", ecole='" + ecole + '\'' +
                ", filiere='" + filiere + '\'' +
                ", codeSession='" + codeSession + '\'' +
                '}';
    }
    public Integer getCreneauId() { return creneauId; }
    public void setCreneauId(Integer creneauId) { this.creneauId = creneauId; }

}
