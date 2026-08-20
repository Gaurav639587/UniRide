package Utilisateur;

import java.io.*;
import java.util.Scanner;

public class Utilisateur {

    private final String nom;
    private final String prenom;
    private final double matricule;
    private float reputation;
    private static final String fpath = "users.txt";
    protected final String typeUser;

    // Constructor
    Utilisateur(String nom, String prenom, double matricule, float rep) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fpath, true));

        if (!checkNP(nom) || !checkNP(prenom)) {
            throw new IllegalArgumentException("The name should contain letters only.");
        }

        if (checkDate(matricule)) {
            this.matricule = matricule;
        } else {
            throw new IllegalArgumentException("Invalid matricule year. Please try again.");
        }

        this.nom = nom;
        this.prenom = prenom;
        this.typeUser = checkTypeUser();

        setReputation(rep);

        writer.write(
                String.format("%.0f", matricule)
                        + "," + nom
                        + "," + prenom
                        + "," + rep
                        + "\n"
        );

        writer.close();
    }

    protected String getNom() {
        return this.nom;
    }

    protected String getPrenom() {
        return this.prenom;
    }

    protected double getMatricule() {
        return this.matricule;
    }

    protected float getReputation() {
        return this.reputation;
    }

    protected void setReputation(float rep) {
        if (checkRep(rep)) {
            this.reputation = rep;
        } else {
            System.out.println("Invalid value. Please choose a value between 1 and 5.");
        }
    }

    public boolean checkRep(float rep) {
        return rep >= 0 && rep <= 5;
    }

    public boolean checkNP(Object temp) {

        // Check whether the provided object is a String
        if (temp instanceof String) {
            String name = (String) temp;

            // Accept letters from different languages
            return name.matches("^[\\p{L}]+$");
        }

        return false;
    }

    void printUsers() throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(fpath));

        String line = reader.readLine();

        if (line == null) {
            reader.close();
            return;
        }

        String[] user = line.split(",");

        while (line != null) {
            showUser(user);

            line = reader.readLine();

            if (line != null) {
                user = line.split(",");
            }
        }

        reader.close();
    }

    // Overloaded printUsers method to display a certain number of users
    void printUsers(int i) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(fpath));

        String line = reader.readLine();

        while (i > 0 && line != null) {

            String[] user = line.split(",");
            showUser(user);

            line = reader.readLine();
            i--;
        }

        reader.close();
    }

    static boolean findUser(double mat) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(fpath));

        String user;
        boolean found = false;

        while ((user = reader.readLine()) != null) {

            String[] fmat = user.split(",");

            if (fmat.length > 0 && Double.parseDouble(fmat[0]) == mat) {

                System.out.println("User with ID " + mat + " found.");
                showUser(fmat);

                reader.close();
                return true;
            }
        }

        System.out.println("User with ID " + mat + " not found.");

        reader.close();

        return found;
    }

    // Overloaded findUser methods
    void findUser(double mat1, double mat2) throws IOException {
        findUser(mat1);
        findUser(mat2);
    }

    void findUser(double mat1, double mat2, double mat3) throws IOException {
        findUser(mat1);
        findUser(mat2);
        findUser(mat3);
    }

    void findUser(double mat1, double mat2, double mat3, double mat4) throws IOException {
        findUser(mat1);
        findUser(mat2);
        findUser(mat3);
        findUser(mat4);
    }

    static void showUser(String[] fmat) throws IOException {

        System.out.println(
                "ID: " + fmat[0]
                        + "\nLast name: " + fmat[1]
                        + "\nFirst name: " + fmat[2]
                        + "\nReputation: " + fmat[3]
                        + "\n-------------------------------------"
        );
    }

    // Overloaded showUser method to show a certain number of users
    void showUser(String[] fmat, int i) throws IOException {

        if (i == 0) {
            return;
        } else if (i > 3 || i < 0) {

            System.out.println("The number must be between 1 and 4.");
            return;

        } else {

            while (i > 0) {
                System.out.println(fmat[i] + "\n");
                i--;
            }
        }
    }

    boolean checkDate(double mat) {

        String matString = String.format("%.0f", mat);

        if (matString.length() != 8) {
            return false;
        }

        int matriculeYear = Integer.parseInt(matString.substring(0, 4));
        int currentYear = java.time.Year.now().getValue();

        return matriculeYear >= 2000 && matriculeYear <= currentYear;
    }

    String checkTypeUser() {

        Scanner sc = new Scanner(System.in);

        while (true) {

            System.out.println("Choose user type:\n");
            System.out.println(
                    "1 - STUDENT\n" +
                            "2 - TEACHER\n" +
                            "3 - ADMINISTRATIVE/TECHNICAL/SERVICE STAFF"
            );

            int choice = sc.nextInt();

            switch (choice) {

                case 1:
                    return "ETUDIANT";

                case 2:
                    return "ENSEIGNANT";

                case 3:
                    return "ATS";

                default:
                    System.out.println("Invalid input. Please choose 1, 2, or 3.");
            }
        }
    }
}