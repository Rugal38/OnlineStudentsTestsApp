package model;

import java.io.Serializable;
import java.sql.Date;

public class Test implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String titre;
    private Integer duree; // en minutes, 0 = pas de limite
    private Date dateTest;

    // Test Settings (formerly global)
    private int nbQuestions;
    private boolean shuffleQuestions;
    private boolean shuffleReponses;
    private int scoreParQuestion;
    private int seuilReussite;
    private boolean afficherResultatFin;
    private boolean afficherCorrection;
    private int maxTentatives;

    public Test() {
        // Default values, similar to global TestSettings
        this.nbQuestions = 10;
        this.shuffleQuestions = true;
        this.shuffleReponses = true;
        this.scoreParQuestion = 1;
        this.seuilReussite = 50;
        this.afficherResultatFin = true;
        this.afficherCorrection = false;
        this.maxTentatives = 1;
        this.duree = 20; // Default duration in minutes
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public Integer getDuree() { return duree; }
    public void setDuree(Integer duree) { this.duree = duree; }

    public Date getDateTest() { return dateTest; }
    public void setDateTest(Date dateTest) { this.dateTest = dateTest; }

    public int getNbQuestions() { return nbQuestions; }
    public void setNbQuestions(int nbQuestions) { this.nbQuestions = nbQuestions; }

    public boolean isShuffleQuestions() { return shuffleQuestions; }
    public void setShuffleQuestions(boolean shuffleQuestions) { this.shuffleQuestions = shuffleQuestions; }

    public boolean isShuffleReponses() { return shuffleReponses; }
    public void setShuffleReponses(boolean shuffleReponses) { this.shuffleReponses = shuffleReponses; }

    public int getScoreParQuestion() { return scoreParQuestion; }
    public void setScoreParQuestion(int scoreParQuestion) { this.scoreParQuestion = scoreParQuestion; }

    public int getSeuilReussite() { return seuilReussite; }
    public void setSeuilReussite(int seuilReussite) { this.seuilReussite = seuilReussite; }

    public boolean isAfficherResultatFin() { return afficherResultatFin; }
    public void setAfficherResultatFin(boolean afficherResultatFin) { this.afficherResultatFin = afficherResultatFin; }

    public boolean isAfficherCorrection() { return afficherCorrection; }
    public void setAfficherCorrection(boolean afficherCorrection) { this.afficherCorrection = afficherCorrection; }

    public int getMaxTentatives() { return maxTentatives; }
    public void setMaxTentatives(int maxTentatives) { this.maxTentatives = maxTentatives; }
}
