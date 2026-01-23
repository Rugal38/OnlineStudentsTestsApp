package controller;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import java.io.IOException;

@Named("adminGuard")
@RequestScoped
public class AdminGuard {

    public void checkAdmin() {
        FacesContext fc = FacesContext.getCurrentInstance();
        Object logged = fc.getExternalContext().getSessionMap().get("adminLogged");
        boolean ok = logged instanceof Boolean && (Boolean) logged;

        if (!ok) {
            try {
                fc.getExternalContext().redirect("adminLogin.xhtml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
