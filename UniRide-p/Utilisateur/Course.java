package Utilisateur;

import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Course {

    private double driverId;
    private double passengerId;
    private int driverRating;
    private int passengerRating;
    private String driverComment;
    private String passengerComment;
    private Date schedule;
    private Status status;

    private static final String COURSES_FILE = "courses.txt";

    public enum Status {
        PENDING,
        IN_PROGRESS,
        COMPLETED
    }

    public Course(double driverId, double passengerId) {
        this.driverId = driverId;
        this.passengerId = passengerId;
        this.status = Status.PENDING;
        this.schedule = new Date();
    }

    // Getters and setters

    public double getDriverId() {
        return driverId;
    }

    public double getPassengerId() {
        return passengerId;
    }

    public int getDriverRating() {
        return driverRating;
    }

    public void setDriverRating(int driverRating) {
        if (driverRating >= 1 && driverRating <= 5) {
            this.driverRating = driverRating;
        } else {
            throw new IllegalArgumentException("The rating must be between 1 and 5.");
        }
    }

    public int getPassengerRating() {
        return passengerRating;
    }

    public void setPassengerRating(int passengerRating) {
        if (passengerRating >= 1 && passengerRating <= 5) {
            this.passengerRating = passengerRating;
        } else {
            throw new IllegalArgumentException("The rating must be between 1 and 5.");
        }
    }

    public String getDriverComment() {
        return driverComment;
    }

    public void setDriverComment(String driverComment) {
        this.driverComment = driverComment;
    }

    public String getPassengerComment() {
        return passengerComment;
    }

    public void setPassengerComment(String passengerComment) {
        this.passengerComment = passengerComment;
    }

    public Date getSchedule() {
        return schedule;
    }

    public Status getStatus() {
        return status;
    }

    // Main ride methods

    public void addCourse() throws IOException {

        StringBuilder courseData = new StringBuilder();

        courseData.append(driverId).append(",")
                .append(passengerId).append(",")
                .append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(schedule)).append(",")
                .append(status.toString()).append(",")
                .append("")
                .append(",")
                .append("")
                .append(",")
                .append("0")
                .append(",")
                .append("0")
                .append(",")
                .append("")
                .append(",")
                .append("")
                .append("\n");

        Files.write(
                Paths.get(COURSES_FILE),
                courseData.toString().getBytes(),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );
    }

    public void startCourse() throws IOException {

        this.status = Status.IN_PROGRESS;
        this.schedule = new Date();

        updateCourseFile();
    }

    public void endCourse(
            int driverRating,
            int passengerRating,
            String driverComment,
            String passengerComment
    ) throws IOException {

        this.status = Status.COMPLETED;

        this.driverRating = driverRating;
        this.passengerRating = passengerRating;
        this.driverComment = driverComment;
        this.passengerComment = passengerComment;

        updateCourseFile();

        // Get the profiles of the driver and passenger
        Profile driver = Profile.getProfileByMatricule(this.driverId);
        Profile passenger = Profile.getProfileByMatricule(this.passengerId);

        // Update both users' ratings
        if (driver != null && passenger != null) {
            Profile.refreshMoyenne(
                    driver,
                    passenger,
                    driverRating,
                    passengerRating
            );
        }
    }

    private void updateCourseFile() throws IOException {

        List<String> lines =
                Files.readAllLines(Paths.get(COURSES_FILE));

        for (int i = 0; i < lines.size(); i++) {

            String[] parts = lines.get(i).split(",");

            if (parts.length >= 2 &&
                    Double.parseDouble(parts[0]) == driverId &&
                    Double.parseDouble(parts[1]) == passengerId) {

                StringBuilder updatedLine = new StringBuilder();

                updatedLine.append(driverId).append(",")
                        .append(passengerId).append(",")
                        .append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(schedule)).append(",")
                        .append(status.toString()).append(",")
                        .append("")
                        .append(",")
                        .append("")
                        .append(",")
                        .append(driverRating)
                        .append(",")
                        .append(passengerRating)
                        .append(",")
                        .append(driverComment != null ? driverComment : "")
                        .append(",")
                        .append(passengerComment != null ? passengerComment : "");

                lines.set(i, updatedLine.toString());

                break;
            }
        }

        Files.write(Paths.get(COURSES_FILE), lines);
    }

    private void updateUserStats() {
        // Implementation not provided in the original code
    }

    // Display all rides

    public static void displayAllCourses() throws IOException {

        if (!Files.exists(Paths.get(COURSES_FILE))) {
            System.out.println("No rides recorded.");
            return;
        }

        List<String> lines =
                Files.readAllLines(Paths.get(COURSES_FILE));

        System.out.println("\n=== Ride List ===");

        System.out.println(
                "Driver ID\tPassenger ID\tSchedule\t\t\tStatus\tDriver Rating\tPassenger Rating"
        );

        for (String line : lines) {

            String[] parts = line.split(",");

            if (parts.length >= 6) {

                System.out.println(
                        parts[0] + "\t" +
                                parts[1] + "\t" +
                                parts[2] + "\t" +
                                parts[3] + "\t" +
                                (parts.length > 6 ? parts[6] : "0") + "\t" +
                                (parts.length > 7 ? parts[7] : "0")
                );
            }
        }
    }

    // Find rides associated with a specific user

    public static List<Course> findCoursesByUser(double userId)
            throws IOException {

        List<Course> results = new ArrayList<>();

        if (!Files.exists(Paths.get(COURSES_FILE))) {
            return results;
        }

        List<String> lines =
                Files.readAllLines(Paths.get(COURSES_FILE));

        for (String line : lines) {

            String[] parts = line.split(",");

            if (parts.length >= 2 &&
                    (Double.parseDouble(parts[0]) == userId ||
                            Double.parseDouble(parts[1]) == userId)) {

                Course course = new Course(
                        Double.parseDouble(parts[0]),
                        Double.parseDouble(parts[1])
                );

                results.add(course);
            }
        }

        return results;
    }

    public static void main(String[] args) throws IOException {

        // Create a ride
        Course course = new Course(12345, 67890);

        course.addCourse();

        course.startCourse();

        // End the ride with ratings
        course.endCourse(
                4,
                5,
                "Very good passenger",
                "Excellent driver"
        );

        // Display all rides
        Course.displayAllCourses();
    }
}