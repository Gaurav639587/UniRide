package Utilisateur;

import java.io.*;
import java.util.Scanner;

public class Utilisateur {

    private final String nom ;
    private final String prenom ;
    private final double matricule ;
    private float reputation ;
    private static final String fpath = "users.txt";
    protected final String typeUser;
    // change encapsulation accordingly and without altering the means of security
    Utilisateur(String nom, String prenom, double matricule, float rep) throws IOException { // throws IOException is used to be able to use the fileWriter
        BufferedWriter writer = new BufferedWriter(new FileWriter(fpath,true));

        if (!checkNP(nom) || !checkNP(prenom)) {throw new IllegalArgumentException("The name should contain letters only");} // to get rid of the might not be init for final variables problem fixed
        if (checkDate(matricule)) {this.matricule = matricule;}
        else{throw new IllegalArgumentException("Invalid matricule year, Try again");}  // to get rid of the might not be init for final variables problem fixed
        this.nom = nom;
        this.prenom = prenom;
        this.typeUser = checkTypeUser();

        setReputation(rep);
        writer.write(String.format("%.0f",matricule) + "," + nom + "," + prenom + "," + rep + "\n");
        writer.close();
    }
    protected String getNom() {return this.nom;}
    protected String getPrenom() {return this.prenom;}
    protected double getMatricule() {return this.matricule;}
    protected float getReputation() {return this.reputation;}
    protected void setReputation(float rep) {
        if(checkRep(rep)){this.reputation = rep;}
        else{ System.out.println("Value entered out of range. Choose a value between 1 and 5.");}
    }
    public boolean checkRep(float rep){return rep >= 0 && rep <= 5;}

    public boolean checkNP(Object temp) {
// i did object temp to implement oop by checking its type with instanceof it will be dealt with if it causes any problems
        if (temp instanceof String) {
            String name = (String) temp;
            return name.matches("^[\\p{L}]+$");}//check if name is in any language ^[\p{L}]+$ <- this means all letters in all languages upper & lower case
        return false;
    }
    void printUsers() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fpath));
        String line = reader.readLine();
        String[] user = line.split(",");

        while(line != null) {
            line = reader.readLine();
            user = line.split(",");
            showUser(user);
        }
        reader.close();
    }
    /// overloaded the printUsers method to display a certain amount of users
    void printUsers(int i) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fpath));
        String[] user = reader.readLine().split(",");

        while(i>0) {
            user = reader.readLine().split(",");
            showUser(user);
            i--;
        }
        reader.close();
    }
    static boolean findUser(double mat) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fpath));
        String user = reader.readLine();
        boolean found = false;

        while (user != null) {
            String[] fmat = user.split(",");
            if (fmat.length > 0 && Double.parseDouble(fmat[0]) == mat) {
                System.out.println("User with ID " + mat + " found.");
                showUser(fmat);
                return true ;
            }
            fmat = user.split(",");
        }
        if (!found) {
            System.out.println("User with ID " + mat + " not found.");
        }
        reader.close();
        return found;
    }
    /// overloading findUser to find up to four users
    void findUser(double mat1, double mat2) throws IOException {findUser(mat1);findUser(mat2);}
    void findUser(double mat1, double mat2 , double mat3) throws IOException {findUser(mat1);findUser(mat2);findUser(mat3);}
    void findUser(double mat1, double mat2 , double mat3, double mat4) throws IOException {findUser(mat1);findUser(mat2);findUser(mat3);findUser(mat4);}

    static void showUser(String[] fmat) throws IOException {System.out.println("ID: " + fmat[0] + "\nLast name: " + fmat[1] + "\nFirst name: " + fmat[2] + "\nReputation: " + fmat[3] + "\n-------------------------------------");}
    void showUser(String[] fmat,int i) throws IOException { /// overloaded showUser to show a certain number of users which is contained in the variable i
        if (i==0){return;} else if (i>3 || i<0) {
            System.out.println("The number has to be between 1 and 4"); return;
        }else{
            while(i>0){System.out.println(fmat[i]+"\n"); i--;}}
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

    String checkTypeUser(){
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("Choose user type :\n");
            System.out.println("1 - STUDENT\n2 - TEACHER\n3 - ADMIN/TECH/SERVICE");
            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    return "ETUDIANT";
                case 2:
                    return "ENSEIGNANT";
                case 3:
                    return "ATS";
                default:
                    System.out.println("Invalid input");
            }
        }
    }
}