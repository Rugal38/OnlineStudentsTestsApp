package model;

import java.sql.Timestamp;

public class ResultatView {
    private int id;
    private int score;
    private Timestamp datePassage;

    private String nom;
    private String prenom;
    private String email;
    private String codeSession;

    private String testTitre;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public Timestamp getDatePassage() { return datePassage; }
    public void setDatePassage(Timestamp datePassage) { this.datePassage = datePassage; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCodeSession() { return codeSession; }
    public void setCodeSession(String codeSession) { this.codeSession = codeSession; }

    public String getTestTitre() { return testTitre; }
    public void setTestTitre(String testTitre) { this.testTitre = testTitre; }
}
