package Utilisateur;

import java.io.IOException;

public class Ats extends Utilisateur {
    private int anneeRecrutement;
    private String service;
    private final String role = "ATS";

    public Ats(String nom, String prenom, double matricule, float reputation, int anneeRecrutement, String service) throws IOException {
        super(nom, prenom, matricule, reputation);
        this.anneeRecrutement = anneeRecrutement;
        this.service = service;
    }

    public String getRole() {
        return this.role;
    }

    protected int getAnneeRecrutement() {
        return this.anneeRecrutement;
    }

    protected void setAnneeRecrutement(int anneeRecrutement) {
        this.anneeRecrutement = anneeRecrutement;
    }

    protected String getService() {
        return this.service;
    }

    protected void setService(String service) {
        this.service = service;
    }
}

