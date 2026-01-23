package controller;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import java.io.Serializable;

@Named("adminLoginBean")
@SessionScoped
public class AdminLoginBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;
    private String password;

    private boolean adminLogged;

    // ✅ Identifiants administrateur (modifiez-les si besoin)
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "admin123";

    public String login() {
        if (ADMIN_USER.equals(username) && ADMIN_PASS.equals(password)) {
            adminLogged = true;
            return "adminDashboard.xhtml?faces-redirect=true";
        }

        adminLogged = false;
        FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(
                        FacesMessage.SEVERITY_ERROR,
                        "Connexion invalide",
                        "Nom d'utilisateur ou mot de passe incorrect."
                )
        );
        return null;
    }

    public String logout() {
        adminLogged = false;
        username = null;
        password = null;
        return "adminLogin.xhtml?faces-redirect=true";
    }

    public String guard() {
        return adminLogged ? null : "adminLogin.xhtml?faces-redirect=true";
    }

    public boolean isAdminLogged() { return adminLogged; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
