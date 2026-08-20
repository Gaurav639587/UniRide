package Utilisateur;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.*;

public class Admin {

    private static String password = "pass123";

    private static final String BLACKLIST_FILE = "blacklist.txt";
    private static final String USERS_FILE = "users.txt";
    private static final String COURSES_FILE = "courses.txt";
    private static final String PROFILES_FILE = "profiles.txt";

    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Admin() {
        // Default constructor
    }

    public void changePass() {

        Scanner sc = new Scanner(System.in);

        System.out.println("Enter current password:");
        String currentPassword = sc.nextLine();

        if (currentPassword.equals(password)) {

            while (true) {

                System.out.println("Enter new password:");
                String newPassword = sc.nextLine();

                if (!isValidPass(newPassword)) {

                    System.out.println(
                            "Password must be at least 8 characters " +
                                    "and contain at least one number and one symbol."
                    );

                } else {

                    password = newPassword;
                    System.out.println("Password changed successfully.");
                    break;
                }
            }

        } else {

            System.out.println("Incorrect password.");
        }
    }

    private boolean isValidPass(String password) {

        boolean hasDigit = false;
        boolean hasSymbol = false;

        if (password.length() < 8) {
            return false;
        }

        for (char c : password.toCharArray()) {

            if (Character.isDigit(c)) {
                hasDigit = true;

            } else if (!Character.isLetterOrDigit(c)) {
                hasSymbol = true;
            }

            if (hasDigit && hasSymbol) {
                return true;
            }
        }

        return false;
    }

    public void banUser(double userId) throws IOException {

        if (Utilisateur.findUser(userId)) {

            BufferedWriter writer =
                    new BufferedWriter(
                            new FileWriter(BLACKLIST_FILE, true)
                    );

            writer.write(String.format("%.0f", userId) + "\n");
            writer.close();

            System.out.println(
                    "User with ID " + userId + " has been banned."
            );

        } else {

            System.out.println("User not found.");
        }
    }

    public boolean isUserBanned(double userId) throws IOException {

        File file = new File(BLACKLIST_FILE);

        if (!file.exists()) {
            return false;
        }

        BufferedReader reader =
                new BufferedReader(new FileReader(BLACKLIST_FILE));

        String line;

        while ((line = reader.readLine()) != null) {

            if (Double.parseDouble(line) == userId) {

                reader.close();
                return true;
            }
        }

        reader.close();
        return false;
    }

    public void unbanUser(double userId) throws IOException {

        File blacklistFile = new File(BLACKLIST_FILE);

        if (!blacklistFile.exists()) {

            System.out.println("Blacklist file does not exist.");
            return;
        }

        File temporaryBlacklist =
                new File("temp_blacklist.txt");

        BufferedReader reader =
                new BufferedReader(new FileReader(blacklistFile));

        BufferedWriter writer =
                new BufferedWriter(new FileWriter(temporaryBlacklist));

        String line;
        boolean found = false;

        while ((line = reader.readLine()) != null) {

            if (Double.parseDouble(line) != userId) {

                writer.write(line + "\n");

            } else {

                found = true;
            }
        }

        reader.close();
        writer.close();

        if (found) {

            blacklistFile.delete();
            temporaryBlacklist.renameTo(blacklistFile);

            System.out.println(
                    "User with ID " + userId + " has been unbanned."
            );

        } else {

            temporaryBlacklist.delete();

            System.out.println(
                    "User not found in the blacklist."
            );
        }
    }

    public void deleteUser(double userId) throws IOException {

        File usersFile = new File(USERS_FILE);
        File temporaryFile = new File("users_temp.txt");

        if (!usersFile.exists()) {

            System.out.println("Users file does not exist.");
            return;
        }

        boolean userExists = Utilisateur.findUser(userId);

        if (!userExists) {

            System.out.println("User does not exist.");
            return;
        }

        BufferedReader reader =
                new BufferedReader(new FileReader(usersFile));

        BufferedWriter writer =
                new BufferedWriter(new FileWriter(temporaryFile));

        String line;
        boolean userDeleted = false;

        while ((line = reader.readLine()) != null) {

            String[] data = line.split(",");

            if (data.length > 0) {

                try {

                    double id = Double.parseDouble(data[0]);

                    if (id != userId) {

                        writer.write(line + "\n");

                    } else {

                        userDeleted = true;
                    }

                } catch (NumberFormatException e) {

                    writer.write(line + "\n");
                }
            }
        }

        reader.close();
        writer.close();

        if (userDeleted) {

            usersFile.delete();
            temporaryFile.renameTo(usersFile);

            System.out.println("User deleted successfully.");

            if (isUserBanned(userId)) {
                unbanUser(userId);
            }

        } else {

            temporaryFile.delete();
        }
    }

    public void showBannedUsers() throws IOException {

        File file = new File(BLACKLIST_FILE);

        if (!file.exists() || file.length() == 0) {

            System.out.println("No banned users.");
            return;
        }

        System.out.println("List of banned users:");

        BufferedReader reader =
                new BufferedReader(new FileReader(BLACKLIST_FILE));

        String line;

        while ((line = reader.readLine()) != null) {

            double userId = Double.parseDouble(line);

            System.out.println(
                    String.format("%.0f", userId)
            );
        }

        System.out.println("--------------------------------");

        reader.close();
    }

    // View rides currently in progress

    public void viewOngoingCourses() throws IOException {

        if (!Files.exists(Paths.get(COURSES_FILE))) {

            System.out.println("No rides recorded.");
            return;
        }

        List<String> lines =
                Files.readAllLines(Paths.get(COURSES_FILE));

        System.out.println("\n=== Ongoing Rides ===");

        System.out.println(
                "Driver ID\tPassenger ID\tSchedule\t\t\tStatus"
        );

        boolean ridesFound = false;

        for (String line : lines) {

            String[] parts = line.split(",");

            if (parts.length >= 4 &&
                    parts[3].equals("IN_PROGRESS")) {

                System.out.println(
                        parts[0] + "\t" +
                                parts[1] + "\t" +
                                parts[2] + "\t" +
                                parts[3]
                );

                ridesFound = true;
            }
        }

        if (!ridesFound) {

            System.out.println(
                    "No rides are currently in progress."
            );
        }
    }

    // View completed ride history

    public void viewCourseHistory() throws IOException {

        if (!Files.exists(Paths.get(COURSES_FILE))) {

            System.out.println("No rides recorded.");
            return;
        }

        List<String> lines =
                Files.readAllLines(Paths.get(COURSES_FILE));

        System.out.println("\n=== Ride History ===");

        System.out.println(
                "Driver ID\tPassenger ID\tSchedule\t\t\tStatus\t" +
                        "Driver Rating\tPassenger Rating\tDriver Comment\tPassenger Comment"
        );

        boolean ridesFound = false;

        for (String line : lines) {

            String[] parts = line.split(",");

            if (parts.length >= 8 &&
                    parts[3].equals("COMPLETED")) {

                String driverComment =
                        parts.length > 8 ? parts[8] : "";

                String passengerComment =
                        parts.length > 9 ? parts[9] : "";

                System.out.println(
                        parts[0] + "\t" +
                                parts[1] + "\t" +
                                parts[2] + "\t" +
                                parts[3] + "\t" +
                                parts[6] + "\t" +
                                parts[7] + "\t" +
                                driverComment + "\t" +
                                passengerComment
                );

                ridesFound = true;
            }
        }

        if (!ridesFound) {

            System.out.println(
                    "No completed rides found."
            );
        }
    }

    // Filter rides by date

    public void viewCoursesByDate(String date)
            throws IOException {

        if (!Files.exists(Paths.get(COURSES_FILE))) {

            System.out.println("No rides recorded.");
            return;
        }

        List<String> lines =
                Files.readAllLines(Paths.get(COURSES_FILE));

        System.out.println(
                "\n=== Rides on " + date + " ==="
        );

        System.out.println(
                "Driver ID\tPassenger ID\tSchedule\t\t\tStatus"
        );

        boolean ridesFound = false;

        for (String line : lines) {

            String[] parts = line.split(",");

            if (parts.length >= 3 &&
                    parts[2].startsWith(date)) {

                System.out.println(
                        parts[0] + "\t" +
                                parts[1] + "\t" +
                                parts[2] + "\t" +
                                parts[3]
                );

                ridesFound = true;
            }
        }

        if (!ridesFound) {

            System.out.println(
                    "No rides found for this date."
            );
        }
    }

    // Generate application usage statistics

    public void generateStats() throws IOException {

        Map<String, Integer> userTypeCount =
                countUsersByType();

        int activeUsers = countActiveUsers();

        Map<String, Integer> ridesByCategory =
                countCoursesByCategory();

        List<Map.Entry<Double, Float>> topDrivers =
                getTopDrivers(10);

        List<Map.Entry<Double, Float>> lowestRatedUsers =
                getWorstUsers(10);

        System.out.println(
                "\n=== APPLICATION USAGE STATISTICS ==="
        );

        // Number of users by category

        System.out.println(
                "\nNumber of users by category:"
        );

        System.out.println(
                "- Students: " +
                        userTypeCount.getOrDefault("ETUDIANT", 0)
        );

        System.out.println(
                "- Teachers: " +
                        userTypeCount.getOrDefault("ENSEIGNANT", 0)
        );

        System.out.println(
                "- Administrative/Technical/Service Staff: " +
                        userTypeCount.getOrDefault("ATS", 0)
        );

        System.out.println(
                "- Total: " +
                        userTypeCount.values()
                                .stream()
                                .mapToInt(Integer::intValue)
                                .sum()
        );

        // Number of active users

        System.out.println(
                "\nNumber of active users: " + activeUsers
        );

        // Number of rides by user category

        System.out.println(
                "\nNumber of rides by user category:"
        );

        ridesByCategory.entrySet()
                .stream()
                .sorted(
                        Map.Entry
                                .<String, Integer>comparingByValue()
                                .reversed()
                )
                .forEach(
                        entry ->
                                System.out.println(
                                        "- " +
                                                entry.getKey() +
                                                ": " +
                                                entry.getValue()
                                )
                );

        // Top 10 drivers

        System.out.println("\nTop 10 drivers:");

        for (int i = 0; i < topDrivers.size(); i++) {

            Map.Entry<Double, Float> driver =
                    topDrivers.get(i);

            System.out.println(
                    (i + 1) +
                            ". ID: " +
                            String.format("%.0f", driver.getKey()) +
                            ", Average rating: " +
                            String.format("%.2f", driver.getValue())
            );
        }

        // Lowest-rated users

        System.out.println(
                "\nUsers with the lowest ratings " +
                        "(consider for banning):"
        );

        for (int i = 0; i < lowestRatedUsers.size(); i++) {

            Map.Entry<Double, Float> user =
                    lowestRatedUsers.get(i);

            System.out.println(
                    (i + 1) +
                            ". ID: " +
                            String.format("%.0f", user.getKey()) +
                            ", Average rating: " +
                            String.format("%.2f", user.getValue())
            );
        }
    }

    // Count users by type

    private Map<String, Integer> countUsersByType()
            throws IOException {

        Map<String, Integer> userTypeCount =
                new HashMap<>();

        if (!Files.exists(Paths.get(USERS_FILE))) {
            return userTypeCount;
        }

        List<String> lines =
                Files.readAllLines(Paths.get(USERS_FILE));

        for (String line : lines) {

            String[] parts = line.split(",");

            if (parts.length >= 3) {

                double userId =
                        Double.parseDouble(parts[0]);

                Profile profile =
                        Profile.getProfileByMatricule(userId);

                if (profile != null) {

                    String type = profile.getRole();

                    userTypeCount.put(
                            type,
                            userTypeCount.getOrDefault(type, 0) + 1
                    );
                }
            }
        }

        return userTypeCount;
    }

    // Count active users who participated in at least one ride

    private int countActiveUsers() throws IOException {

        Set<Double> activeUsers =
                new HashSet<>();

        if (!Files.exists(Paths.get(COURSES_FILE))) {
            return 0;
        }

        List<String> lines =
                Files.readAllLines(Paths.get(COURSES_FILE));

        for (String line : lines) {

            String[] parts = line.split(",");

            if (parts.length >= 2) {

                activeUsers.add(
                        Double.parseDouble(parts[0])
                );

                activeUsers.add(
                        Double.parseDouble(parts[1])
                );
            }
        }

        return activeUsers.size();
    }

    // Count rides by user category

    private Map<String, Integer> countCoursesByCategory()
            throws IOException {

        Map<String, Integer> ridesByCategory =
                new HashMap<>();

        if (!Files.exists(Paths.get(COURSES_FILE))) {
            return ridesByCategory;
        }

        List<String> lines =
                Files.readAllLines(Paths.get(COURSES_FILE));

        for (String line : lines) {

            String[] parts = line.split(",");

            if (parts.length >= 2) {

                double driverId =
                        Double.parseDouble(parts[0]);

                Profile driver =
                        Profile.getProfileByMatricule(driverId);

                if (driver != null) {

                    String type = driver.getRole();

                    ridesByCategory.put(
                            type,
                            ridesByCategory.getOrDefault(type, 0) + 1
                    );
                }
            }
        }

        return ridesByCategory;
    }

    // Get the highest-rated drivers

    private List<Map.Entry<Double, Float>> getTopDrivers(
            int limit) throws IOException {

        Map<Double, Float> driverRatings =
                new HashMap<>();

        Map<Double, Integer> driverCounts =
                new HashMap<>();

        if (!Files.exists(Paths.get(PROFILES_FILE))) {
            return new ArrayList<>();
        }

        List<String> profileLines =
                Files.readAllLines(Paths.get(PROFILES_FILE));

        for (String line : profileLines) {

            String[] parts = line.split(",");

            if (parts.length >= 13) {

                double userId =
                        Double.parseDouble(parts[2]);

                float driverAverage =
                        Float.parseFloat(parts[10]);

                int driverCount =
                        Integer.parseInt(parts[12]);

                if (driverCount > 0) {

                    driverRatings.put(
                            userId,
                            driverAverage / driverCount
                    );

                    driverCounts.put(
                            userId,
                            driverCount
                    );
                }
            }
        }

        // Only consider drivers with at least three rides

        return driverRatings.entrySet()
                .stream()
                .filter(
                        entry ->
                                driverCounts.getOrDefault(
                                        entry.getKey(),
                                        0
                                ) >= 3
                )
                .sorted(
                        Map.Entry
                                .<Double, Float>comparingByValue()
                                .reversed()
                )
                .limit(limit)
                .collect(Collectors.toList());
    }

    // Get users with the lowest ratings

    private List<Map.Entry<Double, Float>> getWorstUsers(
            int limit) throws IOException {

        Map<Double, Float> userRatings =
                new HashMap<>();

        Map<Double, Integer> userCounts =
                new HashMap<>();

        if (!Files.exists(Paths.get(PROFILES_FILE))) {
            return new ArrayList<>();
        }

        List<String> profileLines =
                Files.readAllLines(Paths.get(PROFILES_FILE));

        for (String line : profileLines) {

            String[] parts = line.split(",");

            if (parts.length >= 13) {

                double userId =
                        Double.parseDouble(parts[2]);

                float passengerAverage =
                        Float.parseFloat(parts[9]);

                float driverAverage =
                        Float.parseFloat(parts[10]);

                int passengerCount =
                        Integer.parseInt(parts[11]);

                int driverCount =
                        Integer.parseInt(parts[12]);

                float totalRating =
                        passengerAverage + driverAverage;

                int totalCount =
                        passengerCount + driverCount;

                if (totalCount > 0) {

                    userRatings.put(
                            userId,
                            totalRating / totalCount
                    );

                    userCounts.put(
                            userId,
                            totalCount
                    );
                }
            }
        }

        // Only consider users with at least three ratings

        return userRatings.entrySet()
                .stream()
                .filter(
                        entry ->
                                userCounts.getOrDefault(
                                        entry.getKey(),
                                        0
                                ) >= 3
                )
                .sorted(Map.Entry.comparingByValue())
                .limit(limit)
                .collect(Collectors.toList());
    }

    // Display the administration menu

    public void showAdminMenu() {

        Scanner sc = new Scanner(System.in);

        int choice = 0;

        while (true) {

            System.out.println(
                    "\n=== Administration Menu ==="
            );

            System.out.println("1. View ongoing rides");
            System.out.println("2. View ride history");
            System.out.println("3. View rides by date");
            System.out.println("4. View usage statistics");
            System.out.println("5. Ban a user");
            System.out.println("6. Unban a user");
            System.out.println("7. View banned users");
            System.out.println("8. Delete a user");
            System.out.println("9. Change admin password");
            System.out.println("10. Exit");

            System.out.print("Your choice: ");

            try {

                choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {

                    case 1:
                        viewOngoingCourses();
                        break;

                    case 2:
                        viewCourseHistory();
                        break;

                    case 3:
                        System.out.print(
                                "Enter the date (format YYYY-MM-DD): "
                        );

                        String date = sc.nextLine();

                        viewCoursesByDate(date);
                        break;

                    case 4:
                        generateStats();
                        break;

                    case 5:

                        System.out.print(
                                "Enter the ID of the user to ban: "
                        );

                        double userToBan = sc.nextDouble();
                        sc.nextLine();

                        banUser(userToBan);
                        break;

                    case 6:

                        System.out.print(
                                "Enter the ID of the user to unban: "
                        );

                        double userToUnban = sc.nextDouble();
                        sc.nextLine();

                        unbanUser(userToUnban);
                        break;

                    case 7:
                        showBannedUsers();
                        break;

                    case 8:

                        System.out.print(
                                "Enter the ID of the user to delete: "
                        );

                        double userToDelete = sc.nextDouble();
                        sc.nextLine();

                        deleteUser(userToDelete);
                        break;

                    case 9:
                        changePass();
                        break;

                    case 10:

                        System.out.println("Goodbye!");
                        return;

                    default:

                        System.out.println(
                                "Invalid choice. Please try again."
                        );
                }

            } catch (Exception e) {

                System.out.println(
                        "Error: " + e.getMessage()
                );

                e.printStackTrace();

                sc.nextLine();
            }
        }
    }

    public static void main(String[] args) {

        Admin admin = new Admin();

        System.out.println(
                "=== Administration System ==="
        );

        Scanner sc = new Scanner(System.in);

        System.out.print("Admin password: ");

        String inputPassword = sc.nextLine();

        if (inputPassword.equals(password)) {

            System.out.println(
                    "Login successful!"
            );

            admin.showAdminMenu();

        } else {

            System.out.println(
                    "Incorrect password. Access denied."
            );
        }
    }
}