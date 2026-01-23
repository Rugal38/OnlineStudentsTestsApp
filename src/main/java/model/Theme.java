package model;

import java.io.Serializable;

public class Theme implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String nom;
    private int matiereId;

    public Theme() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public int getMatiereId() { return matiereId; }
    public void setMatiereId(int matiereId) { this.matiereId = matiereId; }
}
