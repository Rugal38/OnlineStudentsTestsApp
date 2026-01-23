package model;

import java.io.Serializable;
import java.util.List;

public class Question implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String libelle;
    private String theme;
    private String type;

    // ✅ test_id peut être NULL
    private Integer testId;

    // ✅ matiere_id (important car présent dans la base de données)
    private Integer matiereId;

    // ✅ theme_id (lié au thème sélectionné)
    private Integer themeId;

    private List<Reponse> reponses;

    public Question() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }

    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getTestId() { return testId; }
    public void setTestId(Integer testId) { this.testId = testId; }

    public Integer getMatiereId() { return matiereId; }
    public void setMatiereId(Integer matiereId) { this.matiereId = matiereId; }

    public Integer getThemeId() { return themeId; }
    public void setThemeId(Integer themeId) { this.themeId = themeId; }

    public List<Reponse> getReponses() { return reponses; }
    public void setReponses(List<Reponse> reponses) { this.reponses = reponses; }
}
