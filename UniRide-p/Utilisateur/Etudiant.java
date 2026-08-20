package Utilisateur;

import java.io.IOException;

public class Etudiant extends Utilisateur {

    private String specialization;
    private int admissionYear;
    private String faculty;
    private final String role = "ETUDIANT";

    public Etudiant(
            String nom,
            String prenom,
            double matricule,
            float reputation,
            int admissionYear,
            String faculty,
            String specialization
    ) throws IOException {

        super(nom, prenom, matricule, reputation);

        this.admissionYear = admissionYear;
        this.faculty = faculty;
        this.specialization = specialization;
    }

    public String getRole() {
        return this.role;
    }

    protected String getSpecialization() {
        return this.specialization;
    }

    protected void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    protected void setAdmissionYear(int admissionYear) {
        this.admissionYear = admissionYear;
    }

    protected int getAdmissionYear() {
        return this.admissionYear;
    }

    protected void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    protected String getFaculty() {
        return this.faculty;
    }
}