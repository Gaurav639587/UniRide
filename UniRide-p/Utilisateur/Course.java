package Utilisateur;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class Course {
    private double matChauffeur;
    private double matPassager;
    private int noteChauffeur;
    private int notePassager;
    private String commentChauffeur;
    private String commentPassager;
    private Date horaire;
    private Status status;
    private static final String COURSES_FILE = "courses.txt";

    public enum Status {
        PENDING, IN_PROGRESS, COMPLETED
    }

    public Course(double matChauffeur, double matPassager) {
        this.matChauffeur = matChauffeur;
        this.matPassager = matPassager;
        this.status = Status.PENDING;
        this.horaire = new Date();
    }

    // Getters et setters
    public double getMatChauffeur() {
        return matChauffeur;
    }

    public double getMatPassager() {
        return matPassager;
    }

    public int getNoteChauffeur() {
        return noteChauffeur;
    }

    public void setNoteChauffeur(int noteChauffeur) {
        if (noteChauffeur >= 1 && noteChauffeur <= 5) {
            this.noteChauffeur = noteChauffeur;
        } else {
            throw new IllegalArgumentException("La note doit être entre 1 et 5");
        }
    }

    public int getNotePassager() {
        return notePassager;
    }

    public void setNotePassager(int notePassager) {
        if (notePassager >= 1 && notePassager <= 5) {
            this.notePassager = notePassager;
        } else {
            throw new IllegalArgumentException("La note doit être entre 1 et 5");
        }
    }

    public String getCommentChauffeur() {
        return commentChauffeur;
    }

    public void setCommentChauffeur(String commentChauffeur) {
        this.commentChauffeur = commentChauffeur;
    }

    public String getCommentPassager() {
        return commentPassager;
    }

    public void setCommentPassager(String commentPassager) {
        this.commentPassager = commentPassager;
    }

    public Date getHoraire() {
        return horaire;
    }

    public Status getStatus() {
        return status;
    }

    // Méthodes principales
    public void addCourse() throws IOException {
        // Using string concatenation instead of String.format to avoid format issues
        StringBuilder courseData = new StringBuilder();
        courseData.append(matChauffeur).append(",")
                .append(matPassager).append(",")
                .append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(horaire)).append(",")
                .append(status.toString()).append(",")
                .append("").append(",")
                .append("").append(",")
                .append("0").append(",")
                .append("0").append(",")
                .append("").append(",")
                .append("").append("\n");

        Files.write(Paths.get(COURSES_FILE),
                courseData.toString().getBytes(),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND);
    }

    public void startCourse() throws IOException {
        this.status = Status.IN_PROGRESS;
        this.horaire = new Date();
        updateCourseFile();
    }

    public void endCourse(int noteChauff, int notePass, String commChauff, String commPass) throws IOException {
        this.status = Status.COMPLETED;
        this.noteChauffeur = noteChauff;
        this.notePassager = notePass;
        this.commentChauffeur = commChauff;
        this.commentPassager = commPass;
        updateCourseFile();

        // Get the Profile objects for the driver and passenger
        Profile chauffeur = Profile.getProfileByMatricule(this.matChauffeur);
        Profile passager = Profile.getProfileByMatricule(this.matPassager);

        // Update both user ratings at once
        if (chauffeur != null && passager != null) {
            Profile.refreshMoyenne(chauffeur, passager, noteChauff, notePass);
        }
    }

    private void updateCourseFile() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(COURSES_FILE));
        for (int i = 0; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(",");
            if (parts.length >= 2 &&
                    Double.parseDouble(parts[0]) == matChauffeur &&
                    Double.parseDouble(parts[1]) == matPassager) {

                // Using string concatenation instead of String.format
                StringBuilder updatedLine = new StringBuilder();
                updatedLine.append(matChauffeur).append(",")
                        .append(matPassager).append(",")
                        .append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(horaire)).append(",")
                        .append(status.toString()).append(",")
                        .append("").append(",")
                        .append("").append(",")
                        .append(noteChauffeur).append(",")
                        .append(notePassager).append(",")
                        .append(commentChauffeur != null ? commentChauffeur : "").append(",")
                        .append(commentPassager != null ? commentPassager : "");

                lines.set(i, updatedLine.toString());
                break;
            }
        }
        Files.write(Paths.get(COURSES_FILE), lines);
    }

    private void updateUserStats() {
        // Implementation not provided in original code
    }

    // Méthode pour afficher toutes les courses
    public static void displayAllCourses() throws IOException {
        if (!Files.exists(Paths.get(COURSES_FILE))) {
            System.out.println("No rides recorded.");
            return;
        }

        List<String> lines = Files.readAllLines(Paths.get(COURSES_FILE));
        System.out.println("\n=== Ride List ===");
        System.out.println("Chauffeur\tPassager\tHoraire\t\t\tStatut\tNoteCh\tNotePass");

        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length >= 6) {
                // Using simple println concatenation instead of printf
                System.out.println(parts[0] + "\t" + parts[1] + "\t" + parts[2] + "\t" +
                        parts[3] + "\t" +
                        (parts.length > 6 ? parts[6] : "0") + "\t" +
                        (parts.length > 7 ? parts[7] : "0"));
            }
        }
    }

    // Methode pour trouver des courses
    public static List<Course> findCoursesByUser(double matricule) throws IOException {
        List<Course> results = new ArrayList<>();
        if (!Files.exists(Paths.get(COURSES_FILE))) {
            return results;
        }

        List<String> lines = Files.readAllLines(Paths.get(COURSES_FILE));
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length >= 2 &&
                    (Double.parseDouble(parts[0]) == matricule ||
                            Double.parseDouble(parts[1]) == matricule)) {

                Course course = new Course(
                        Double.parseDouble(parts[0]),
                        Double.parseDouble(parts[1]));
                results.add(course);
            }
        }
        return results;
    }

    public static void main(String[] args) throws IOException {
        // Creation d'une course
        Course course = new Course(12345, 67890);
        course.addCourse();

        course.startCourse();

        // Terminer la course avec notes
        course.endCourse(4, 5, "Très bon passager", "Excellent chauffeur");

        // Afficher toutes les courses
        Course.displayAllCourses();
    }
}