package Utilisateur;

import java.io.IOException;

public class Etudiant extends Utilisateur {
    private String specialite;
    private int anneeadmission;
    private String faculte;
    private final String role = "ETUDIANT";

    public Etudiant(String nom, String prenom, double matricule, float reputation, int anneeadmission, String faculte, String specialite) throws IOException {
        super(nom, prenom, matricule, reputation);
        this.anneeadmission = anneeadmission;
        this.faculte = faculte;
        this.specialite = specialite;
    }

    public String getRole() { return this.role; }
    protected String getSpecialite() { return this.specialite; }
    protected void setSpecialite(String specialite) { this.specialite = specialite; }
    protected void setAnneeAdmission(int anneeadmission) { this.anneeadmission = anneeadmission; }
    protected int getAnneeAdmission() { return this.anneeadmission; }
    protected void setFaculte(String faculte) { this.faculte = faculte; }
    protected String getFaculte() { return this.faculte; }
}