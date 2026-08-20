package Utilisateur;

import java.io.IOException;

public class Ats extends Utilisateur {

    private int recruitmentYear;
    private String department;
    private final String role = "ATS";

    public Ats(
            String nom,
            String prenom,
            double matricule,
            float reputation,
            int recruitmentYear,
            String department
    ) throws IOException {

        super(nom, prenom, matricule, reputation);

        this.recruitmentYear = recruitmentYear;
        this.department = department;
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

    protected String getDepartment() {
        return this.department;
    }

    protected void setDepartment(String department) {
        this.department = department;
    }
}