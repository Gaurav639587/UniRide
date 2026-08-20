package Utilisateur;

import java.io.IOException;

public class Enseignant extends Utilisateur {

    private int recruitmentYear;
    private String faculty;
    private final String role = "ENSEIGNANT";

    public Enseignant(
            String nom,
            String prenom,
            double matricule,
            float reputation,
            int recruitmentYear,
            String faculty
    ) throws IOException {

        super(nom, prenom, matricule, reputation);

        this.recruitmentYear = recruitmentYear;
        this.faculty = faculty;
    }

    public String getRole() {
        return this.role;
    }

    protected int getRecruitmentYear() {
        return this.recruitmentYear;
    }

    protected void setRecruitmentYear(int recruitmentYear) {
        this.recruitmentYear = recruitmentYear;
    }

    protected String getFaculty() {
        return this.faculty;
    }

    protected void setFaculty(String faculty) {
        this.faculty = faculty;
    }
}