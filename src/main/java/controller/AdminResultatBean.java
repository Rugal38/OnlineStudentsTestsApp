package controller;

import dao.ResultatDAO;	
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import model.ResultatView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("adminResultatBean")
@SessionScoped
public class AdminResultatBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<ResultatView> rows = new ArrayList<>();

    @PostConstruct
    public void init() {
        load();
    }

    public void load() {
        rows = ResultatDAO.findAllView();
    }

    public List<ResultatView> getRows() { return rows; }
}
