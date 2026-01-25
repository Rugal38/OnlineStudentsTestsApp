package controller;


import dao.CreneauDAO;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import model.Creneau;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Named("adminCreneauBean")
@SessionScoped
public class AdminCreneauBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Creneau> creneaux = new ArrayList<>();

    private Integer id;
    private String dateExam;     // yyyy-MM-dd ou yyyy-M-d
    private String heureDebut;   // H:mm / HH:mm / HH:mm:ss
    private String heureFin;     // calculée automatiquement
    private Integer testId;
    private boolean disponible = true;

    private String message;
    private boolean success;

    @PostConstruct
    public void init() {
        load();
    }

    // ====== Charger la liste ======
    public void load() {
        creneaux = CreneauDAO.findAll();
    }

    // ====== Aller vers la page d'ajout ======
    public String goAdd() {
        reset();
        return "addCreneau.xhtml?faces-redirect=true";
    }

    // ✅ Recalculer l'heure de fin quand le test ou l'heure de début change (via f:ajax)
    public void recomputeHeureFin() {
        try {
            message = null;
            success = false;

            if (testId == null || heureDebut == null || heureDebut.trim().isEmpty()) {
                this.heureFin = "";
                return;
            }

            String hd = normalizeTime(heureDebut.trim()); // => HH:mm:ss
            String fin = CreneauDAO.computeHeureFin(this.testId, hd);

            this.heureFin = (fin == null) ? "" : fin;

        } catch (Exception e) {
            this.heureFin = "";
        }
    }

    // ====== Ajouter ======
    public String ajouter() {
        message = null;
        success = false;

        try {
            if (isBlank(dateExam) || isBlank(heureDebut) || testId == null) {
                message = "Veuillez remplir : Date, Heure de début et Test.";
                return null;
            }

            // ✅ analyser la date (accepte aussi 2026-1-17)
            LocalDate d = parseFlexibleDate(dateExam.trim());
            if (d == null) {
                message = "❌ Format de date invalide. Exemple : 2026-01-17";
                return null;
            }

            // ✅ date interdite dans le passé
            if (d.isBefore(LocalDate.now())) {
                message = "❌ La date ne peut pas être dans le passé.";
                return null;
            }

            // ✅ analyser l'heure
            LocalTime start = parseFlexibleTime(heureDebut.trim());
            if (start == null) {
                message = "❌ Format d'heure invalide. Exemple : 09:00 ou 09:00:00 ou 1:12";
                return null;
            }

            // ✅ si aujourd'hui : heure de début >= maintenant
            if (d.equals(LocalDate.now())) {
                LocalTime now = LocalTime.now().withSecond(0).withNano(0);
                if (start.isBefore(now)) {
                    message = "❌ Pour aujourd'hui, l'heure de début doit être supérieure ou égale à l'heure actuelle.";
                    return null;
                }
            }

            // ✅ normaliser + calculer l'heure de fin depuis la durée du test
            String hd = normalizeTime(heureDebut.trim());
            String fin = CreneauDAO.computeHeureFin(testId, hd);

            if (fin == null) {
                message = "❌ Test introuvable ou durée non définie. Vérifiez le testId.";
                return null;
            }

            this.heureFin = fin;

            Creneau c = new Creneau();
            c.setDateExam(d.toString());   // yyyy-MM-dd
            c.setHeureDebut(hd);           // HH:mm:ss
            c.setHeureFin(fin);
            c.setDisponible(disponible);
            c.setTestId(testId);

            boolean ok = CreneauDAO.insert(c);

            if (ok) {
                success = true;
                message = "✅ Créneau ajouté avec succès.";
                return "adminCreneaux.xhtml?faces-redirect=true";
            } else {
                message = "❌ Échec de l'ajout du créneau.";
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            message = "❌ Erreur : formats attendus (date : 2026-01-17 / heure : 09:00 ou 09:00:00).";
            return null;
        }
    }

    // ====== Supprimer ======
    public String delete(int id) {
        CreneauDAO.delete(id);
        return "adminCreneaux.xhtml?faces-redirect=true";
    }

    // ====== Charger un créneau pour modification ======
    public String loadForEdit(int id) {
        Creneau c = CreneauDAO.findById(id);
        if (c == null) {
            return "adminCreneaux.xhtml?faces-redirect=true";
        }

        this.id = c.getId();
        this.dateExam = c.getDateExam();
        this.heureDebut = c.getHeureDebut();
        this.testId = c.getTestId();
        this.disponible = c.isDisponible();

        // Recalculer l'heure de fin (ou garder celle enregistrée)
        try {
            String hd = (this.heureDebut == null) ? null : normalizeTime(this.heureDebut);
            this.heureFin = (hd == null || this.testId == null) ? null : CreneauDAO.computeHeureFin(this.testId, hd);
        } catch (Exception ignored) {
        }

        if (this.heureFin == null) {
            this.heureFin = c.getHeureFin();
        }

        return "editCreneau.xhtml?faces-redirect=true";
    }

    // ====== Modifier ======
    public String update() {
        message = null;
        success = false;

        try {
            if (id == null) {
                message = "❌ Identifiant manquant.";
                return null;
            }

            if (isBlank(dateExam) || isBlank(heureDebut) || testId == null) {
                message = "Veuillez remplir : Date, Heure de début et Test.";
                return null;
            }

            // ✅ analyser la date
            LocalDate d = parseFlexibleDate(dateExam.trim());
            if (d == null) {
                message = "❌ Format de date invalide. Exemple : 2026-01-17";
                return null;
            }

            // ✅ pas de date passée
            if (d.isBefore(LocalDate.now())) {
                message = "❌ La date ne peut pas être dans le passé.";
                return null;
            }

            // ✅ analyser l'heure
            LocalTime start = parseFlexibleTime(heureDebut.trim());
            if (start == null) {
                message = "❌ Format d'heure invalide. Exemple : 09:00 ou 09:00:00 ou 1:12";
                return null;
            }

            // ✅ si aujourd'hui : start >= maintenant
            if (d.equals(LocalDate.now())) {
                LocalTime now = LocalTime.now().withSecond(0).withNano(0);
                if (start.isBefore(now)) {
                    message = "❌ Pour aujourd'hui, l'heure de début doit être supérieure ou égale à l'heure actuelle.";
                    return null;
                }
            }

            // ✅ normaliser + calculer fin
            String hd = normalizeTime(heureDebut.trim());
            String fin = CreneauDAO.computeHeureFin(testId, hd);

            if (fin == null) {
                message = "❌ Test introuvable ou durée non définie. Vérifiez le testId.";
                return null;
            }

            this.heureFin = fin;

            Creneau c = new Creneau();
            c.setId(id);
            c.setDateExam(d.toString());
            c.setHeureDebut(hd);
            c.setHeureFin(fin);
            c.setTestId(testId);
            c.setDisponible(disponible);

            boolean ok = CreneauDAO.update(c);

            if (ok) {
                success = true;
                message = "✅ Créneau modifié avec succès.";
                return "adminCreneaux.xhtml?faces-redirect=true";
            } else {
                message = "❌ Échec de la modification.";
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            message = "❌ Erreur lors de la modification.";
            return null;
        }
    }

    // ====== Navigation ======
    public String backToList() {
        load();
        return "adminCreneaux.xhtml?faces-redirect=true";
    }

    public String reset() {
        id = null;
        dateExam = "";
        heureDebut = "";
        heureFin = "";
        testId = null;
        disponible = true;
        message = null;
        success = false;
        return null;
    }

    // =========================
    // Helpers (DATE + TIME)
    // =========================

    // ✅ accepte : yyyy-MM-dd ou yyyy-M-d
    private LocalDate parseFlexibleDate(String s) {
        if (s == null) return null;
        s = s.trim();

        try {
            return LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ignore) {
            try {
                return LocalDate.parse(s, DateTimeFormatter.ofPattern("yyyy-M-d"));
            } catch (DateTimeParseException ignore2) {
                return null;
            }
        }
    }

    // ✅ accepte : H:mm ou HH:mm ou HH:mm:ss
    private LocalTime parseFlexibleTime(String t) {
        if (t == null) return null;
        t = t.trim();

        try {
            if (t.matches("^\\d{1,2}:\\d{2}$")) {
                return LocalTime.parse(t, DateTimeFormatter.ofPattern("H:mm"));
            }

            if (t.matches("^\\d{1,2}:\\d{2}:\\d{2}$")) {
                return LocalTime.parse(t, DateTimeFormatter.ofPattern("H:mm:ss"));
            }

            return LocalTime.parse(t);

        } catch (Exception e) {
            return null;
        }
    }

    // ✅ normalise vers HH:mm:ss
    private String normalizeTime(String t) {
        LocalTime lt = parseFlexibleTime(t);
        if (lt == null) throw new IllegalArgumentException("heure invalide");
        return lt.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    // ====== Getters / Setters ======

    public List<Creneau> getCreneaux() {
        load();
        return creneaux;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getDateExam() { return dateExam; }
    public void setDateExam(String dateExam) { this.dateExam = dateExam; }

    public String getHeureDebut() { return heureDebut; }
    public void setHeureDebut(String heureDebut) { this.heureDebut = heureDebut; }

    public String getHeureFin() { return heureFin; }
    public void setHeureFin(String heureFin) { this.heureFin = heureFin; }

    public Integer getTestId() { return testId; }
    public void setTestId(Integer testId) { this.testId = testId; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    public String getMessage() { return message; }
    public boolean isSuccess() { return success; }
}
