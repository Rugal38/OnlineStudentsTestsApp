package model;

import java.io.Serializable;

public class TestSettings implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id = 1;

    private int nbQuestions;
    private boolean shuffleQuestions;
    private boolean shuffleReponses;
    private int scoreParQuestion;
    private int seuilReussite;
    private boolean afficherResultatFin;
    private boolean afficherCorrection;
    private int maxTentatives;
    private int duree; // en minutes, 0 = pas de limite

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

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
    
    public int getDuree() { return duree; }
    public void setDuree(int duree) { this.duree = duree; }
}
