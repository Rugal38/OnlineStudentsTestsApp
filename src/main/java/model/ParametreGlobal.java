package model;

public class ParametreGlobal {
    private int id = 1;
    private int nbQuestionsDefault;
    private int tempsQuestionMinutes;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getNbQuestionsDefault() { return nbQuestionsDefault; }
    public void setNbQuestionsDefault(int nbQuestionsDefault) { this.nbQuestionsDefault = nbQuestionsDefault; }

    public int getTempsQuestionMinutes() { return tempsQuestionMinutes; }
    public void setTempsQuestionMinutes(int tempsQuestionMinutes) { this.tempsQuestionMinutes = tempsQuestionMinutes; }
}
