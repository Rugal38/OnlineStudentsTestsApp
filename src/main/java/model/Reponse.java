package model;

import java.io.Serializable;

public class Reponse implements Serializable {
	private static final long serialVersionUID = 1L;

    private int id;
    private String texte;
    private boolean correcte;
    private int questionId;

    public Reponse() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTexte() { return texte; }
    public void setTexte(String texte) { this.texte = texte; }

    public boolean isCorrecte() { return correcte; }
    public void setCorrecte(boolean correcte) { this.correcte = correcte; }

    public int getQuestionId() { return questionId; }
    public void setQuestionId(int questionId) { this.questionId = questionId; }
}
