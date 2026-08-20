package Utilisateur;

import java.io.*;
import java.nio.file.*;
import java.text.*;
import java.util.*;
import java.util.stream.*;

public class Admin {
    private static String password = "pass123";
    private static final String blackPath = "blacklist.txt";
    private static final String usersPath = "users.txt";
    private static final String COURSES_FILE = "courses.txt";
    private static final String FICHIER_PROFILES = "profiles.txt";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Admin() {
        // Default constructor
    }

    public void changePass() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter current password:");
        String currentPass = sc.nextLine();

        if (currentPass.equals(password)) {
            while (true) {
                System.out.println("Enter new password:");
                String newpass = sc.nextLine();
                if (!isValidPass(newpass)) {System.out.println("Password must be at least 8 characters and contain at least one number and one symbol");}
                else {
                    password = newpass;
                    System.out.println("Password changed");
                    break;
                }
            }
        } else {System.out.println("Incorrect password");}
    }

    private boolean isValidPass(String password) {
        boolean digit = false;
        boolean symbol = false;

        if (password.length() < 8) {return false;}
        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {digit = true;} else if (!Character.isLetterOrDigit(c)) {symbol = true;}
            if (digit && symbol) {return true;}
        }
        return false;
    }

    public void banUser(double matUser) throws IOException {
        if (Utilisateur.findUser(matUser)) {
            BufferedWriter writer = new BufferedWriter(new FileWriter(blackPath, true));
            writer.write(String.format("%.0f", matUser) + "\n");
            writer.close();
            System.out.println("Banned user with ID " + matUser);
        } else {
            System.out.println("User not found.");
        }
    }

    public boolean isUserBanned(double matricule) throws IOException {
        File file = new File(blackPath);
        if (!file.exists()) {return false;}
        BufferedReader reader = new BufferedReader(new FileReader(blackPath));
        String line;

        while ((line = reader.readLine()) != null) {
            if (Double.parseDouble(line) == matricule) {
                reader.close();
                return true;
            }
        }
        reader.close();
        return false;
    }

    public void unbanUser(double matricule) throws IOException {
        File blackFile = new File(blackPath);
        if (!blackFile.exists()) {
            System.out.println("Blacklist file does not exist.");
            return;
        }
        File tblackFile = new File("temp_blacklist.txt");
        BufferedReader reader = new BufferedReader(new FileReader(blackFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tblackFile));
        String line;
        boolean found = false;

        while ((line = reader.readLine()) != null) {
            if (Double.parseDouble(line) != matricule) {
                writer.write(line + "\n");
            } else {
                found = true;
            }
        }
        reader.close();
        writer.close();

        if (found) {
            blackFile.delete();
            tblackFile.renameTo(blackFile);
            System.out.println("Unbanned user with ID " + matricule);
        } else {
            tblackFile.delete();
            System.out.println("User not found in the blacklist");
        }
    }

    public void deleteUser(double mat) throws IOException {
        File usersFile = new File(usersPath);
        File temp = new File("users_temp.txt");

        if (!usersFile.exists()) {System.out.println("Users file doesn't exist"); return;}
        boolean userExists = Utilisateur.findUser(mat);
        if (!userExists) {System.out.println("User doesn't exist"); return;}

        BufferedReader reader = new BufferedReader(new FileReader(usersFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(temp));
        String line;
        boolean userDeleted = false;

        while ((line = reader.readLine()) != null) {
            String[] data = line.split(",");

            if (data.length > 0) {
                try {
                    double matricule = Double.parseDouble(data[0]);

                    if (matricule != mat) {
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
            temp.renameTo(usersFile);
            System.out.println("User deleted.");
            if (isUserBanned(mat)) {unbanUser(mat);}
        } else {
            temp.delete();
        }
    }

    public void showBannedUsers() throws IOException {
        File file = new File(blackPath);
        if (!file.exists() || file.length() == 0) {
            System.out.println("No banned users");
            return;
        }
        System.out.println("List of banned users:");
        BufferedReader reader = new BufferedReader(new FileReader(blackPath));
        String line;

        while ((line = reader.readLine()) != null) {
            double matricule = Double.parseDouble(line);
            System.out.println(String.format("%.0f", matricule));
        }
        System.out.println("--------------------------------");
        reader.close();
    }

    // Visualiser les courses en cours à un instant donné

    public void viewOngoingCourses() throws IOException {
        if (!Files.exists(Paths.get(COURSES_FILE))) {
            System.out.println("No rides recorded.");
            return;
        }

        List<String> lines = Files.readAllLines(Paths.get(COURSES_FILE));
        System.out.println("\n=== Courses en cours ===");
        System.out.println("Chauffeur\tPassager\tHoraire\t\t\tStatut");

        boolean coursesFound = false;
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length >= 4 && parts[3].equals("IN_PROGRESS")) {
                System.out.println(parts[0] + "\t" + parts[1] + "\t" + parts[2] + "\t" + parts[3]);
                coursesFound = true;
            }
        }

        if (!coursesFound) {
            System.out.println("No rides are currently in progress.");
        }
    }

    // Visualiser l'historique des courses passées

    public void viewCourseHistory() throws IOException {
        if (!Files.exists(Paths.get(COURSES_FILE))) {
            System.out.println("No rides recorded.");
            return;
        }

        List<String> lines = Files.readAllLines(Paths.get(COURSES_FILE));
        System.out.println("\n=== Ride History ===");
        System.out.println("Chauffeur\tPassager\tHoraire\t\t\tStatut\tNote Chauffeur\tNote Passager\tCommentaire Chauffeur\tCommentaire Passager");

        boolean coursesFound = false;
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length >= 8 && parts[3].equals("COMPLETED")) {
                String commentChauffeur = parts.length > 8 ? parts[8] : "";
                String commentPassager = parts.length > 9 ? parts[9] : "";

                System.out.println(parts[0] + "\t" + parts[1] + "\t" + parts[2] + "\t" +
                        parts[3] + "\t" + parts[6] + "\t" + parts[7] + "\t" +
                        commentChauffeur + "\t" + commentPassager);
                coursesFound = true;
            }
        }

        if (!coursesFound) {
            System.out.println("No completed rides found.");
        }
    }

    // Filtrer les courses par date

    public void viewCoursesByDate(String date) throws IOException {
        if (!Files.exists(Paths.get(COURSES_FILE))) {
            System.out.println("No rides recorded.");
            return;
        }

        List<String> lines = Files.readAllLines(Paths.get(COURSES_FILE));
        System.out.println("\n=== Courses du " + date + " ===");
        System.out.println("Chauffeur\tPassager\tHoraire\t\t\tStatut");

        boolean coursesFound = false;
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length >= 3 && parts[2].startsWith(date)) {
                System.out.println(parts[0] + "\t" + parts[1] + "\t" + parts[2] + "\t" + parts[3]);
                coursesFound = true;
            }
        }

        if (!coursesFound) {
            System.out.println("No rides found for this date.");
        }
    }

    // Générer des statistiques d'utilisation de l'application

    public void generateStats() throws IOException {
        Map<String, Integer> userTypeCount = countUsersByType();
        int activeUsers = countActiveUsers();
        Map<String, Integer> coursesByCategory = countCoursesByCategory();
        List<Map.Entry<Double, Float>> topDrivers = getTopDrivers(10);
        List<Map.Entry<Double, Float>> worstUsers = getWorstUsers(10);

        System.out.println("\n=== STATISTIQUES D'UTILISATION ===");

        // Nombre d'utilisateurs par catégorie
        System.out.println("\nNumber of users by category:");
        System.out.println("- Students: " + userTypeCount.getOrDefault("ETUDIANT", 0));
        System.out.println("- Teachers: " + userTypeCount.getOrDefault("ENSEIGNANT", 0));
        System.out.println("- ATS: " + userTypeCount.getOrDefault("ATS", 0));
        System.out.println("- Total: " + userTypeCount.values().stream().mapToInt(Integer::intValue).sum());

        // Nombre d'utilisateurs actifs
        System.out.println("\nNumber of active users: " + activeUsers);

        // Catégories proposant le plus de courses
        System.out.println("\nNumber of rides by user category:");
        coursesByCategory.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(entry -> System.out.println("- " + entry.getKey() + ": " + entry.getValue()));

        // Top 10 des chauffeurs
        System.out.println("\nTop 10 drivers:");
        for (int i = 0; i < topDrivers.size(); i++) {
            Map.Entry<Double, Float> driver = topDrivers.get(i);
            System.out.println((i+1) + ". ID: " + String.format("%.0f", driver.getKey()) +
                    ", Note moyenne: " + String.format("%.2f", driver.getValue()));
        }

        // Pires utilisateurs (à considérer pour bannissement)
        System.out.println("\nUsers with the lowest ratings (consider for banning):");
        for (int i = 0; i < worstUsers.size(); i++) {
            Map.Entry<Double, Float> user = worstUsers.get(i);
            System.out.println((i+1) + ". ID: " + String.format("%.0f", user.getKey()) +
                    ", Note moyenne: " + String.format("%.2f", user.getValue()));
        }
    }

    // Compter le nombre d'utilisateurs par type

    private Map<String, Integer> countUsersByType() throws IOException {
        Map<String, Integer> userTypeCount = new HashMap<>();

        if (!Files.exists(Paths.get(usersPath))) {
            return userTypeCount;
        }

        List<String> lines = Files.readAllLines(Paths.get(usersPath));
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length >= 3) {
                double matricule = Double.parseDouble(parts[0]);
                Profile profile = Profile.getProfileByMatricule(matricule);
                if (profile != null) {
                    String type = profile.getRole();
                    userTypeCount.put(type, userTypeCount.getOrDefault(type, 0) + 1);
                }
            }
        }

        return userTypeCount;
    }

    // Compter le nombre d'utilisateurs actifs (qui ont participé à au moins une course)

    private int countActiveUsers() throws IOException {
        Set<Double> activeUsers = new HashSet<>();

        if (!Files.exists(Paths.get(COURSES_FILE))) {
            return 0;
        }

        List<String> lines = Files.readAllLines(Paths.get(COURSES_FILE));
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length >= 2) {
                activeUsers.add(Double.parseDouble(parts[0]));
                activeUsers.add(Double.parseDouble(parts[1])); 
            }
        }

        return activeUsers.size();
    }

    // Compter le nombre de courses par catégorie d'utilisateur

    private Map<String, Integer> countCoursesByCategory() throws IOException {
        Map<String, Integer> coursesByCategory = new HashMap<>();

        if (!Files.exists(Paths.get(COURSES_FILE))) {
            return coursesByCategory;
        }

        List<String> lines = Files.readAllLines(Paths.get(COURSES_FILE));
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length >= 2) {
                double chauffeurMat = Double.parseDouble(parts[0]);
                Profile chauffeur = Profile.getProfileByMatricule(chauffeurMat);

                if (chauffeur != null) {
                    String type = chauffeur.getRole();
                    coursesByCategory.put(type, coursesByCategory.getOrDefault(type, 0) + 1);
                }
            }
        }

        return coursesByCategory;
    }

    // Obtenir les meilleurs chauffeurs selon leur note moyenne

    private List<Map.Entry<Double, Float>> getTopDrivers(int limit) throws IOException {
        Map<Double, Float> driverRatings = new HashMap<>();
        Map<Double, Integer> driverCounts = new HashMap<>();

        if (!Files.exists(Paths.get(FICHIER_PROFILES))) {
            return new ArrayList<>();
        }

        List<String> profileLines = Files.readAllLines(Paths.get(FICHIER_PROFILES));
        for (String line : profileLines) {
            String[] parts = line.split(",");
            if (parts.length >= 13) {
                double matricule = Double.parseDouble(parts[2]);
                float moyChauff = Float.parseFloat(parts[10]);
                int nbChauff = Integer.parseInt(parts[12]);

                if (nbChauff > 0) {
                    driverRatings.put(matricule, moyChauff / nbChauff);
                    driverCounts.put(matricule, nbChauff);
                }
            }
        }

        // Filtrer les chauffeurs avec au moins 3 courses
        return driverRatings.entrySet().stream()
                .filter(entry -> driverCounts.getOrDefault(entry.getKey(), 0) >= 3)
                .sorted(Map.Entry.<Double, Float>comparingByValue().reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    // Obtenir les utilisateurs avec les notes les plus basses

    private List<Map.Entry<Double, Float>> getWorstUsers(int limit) throws IOException {
        Map<Double, Float> userRatings = new HashMap<>();
        Map<Double, Integer> userCounts = new HashMap<>();

        if (!Files.exists(Paths.get(FICHIER_PROFILES))) {
            return new ArrayList<>();
        }

        List<String> profileLines = Files.readAllLines(Paths.get(FICHIER_PROFILES));
        for (String line : profileLines) {
            String[] parts = line.split(",");
            if (parts.length >= 13) {
                double matricule = Double.parseDouble(parts[2]);
                float moyPass = Float.parseFloat(parts[9]);
                float moyChauff = Float.parseFloat(parts[10]);
                int nbPass = Integer.parseInt(parts[11]);
                int nbChauff = Integer.parseInt(parts[12]);

                float totalRating = moyPass + moyChauff;
                int totalCount = nbPass + nbChauff;

                if (totalCount > 0) {
                    userRatings.put(matricule, totalRating / totalCount);
                    userCounts.put(matricule, totalCount);
                }
            }
        }

        // Filtrer les utilisateurs avec au moins 3 évaluations
        return userRatings.entrySet().stream()
                .filter(entry -> userCounts.getOrDefault(entry.getKey(), 0) >= 3)
                .sorted(Map.Entry.comparingByValue())
                .limit(limit)
                .collect(Collectors.toList());
    }

    // Afficher le menu d'administration

    public void showAdminMenu() {
        Scanner sc = new Scanner(System.in);
        int choice = 0;

        while (true) {
            System.out.println("\n=== Administration Menu ===");
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
                        System.out.print("Enter the date (format YYYY-MM-DD): ");
                        String date = sc.nextLine();
                        viewCoursesByDate(date);
                        break;
                    case 4:
                        generateStats();
                        break;
                    case 5:
                        System.out.print("Enter the ID of the user to ban: ");
                        double matToBan = sc.nextDouble();
                        sc.nextLine(); // Clear buffer
                        banUser(matToBan);
                        break;
                    case 6:
                        System.out.print("Enter the ID of the user to unban: ");
                        double matToUnban = sc.nextDouble();
                        sc.nextLine(); // Clear buffer
                        unbanUser(matToUnban);
                        break;
                    case 7:
                        showBannedUsers();
                        break;
                    case 8:
                        System.out.print("Enter the ID of the user to delete: ");
                        double matToDelete = sc.nextDouble();
                        sc.nextLine(); // Clear buffer
                        deleteUser(matToDelete);
                        break;
                    case 9:
                        changePass();
                        break;
                    case 10:
                        System.out.println("Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid choice, please try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
                sc.nextLine();
            }
        }
    }

    public static void main(String[] args) {
        Admin admin = new Admin();
        System.out.println("=== Administration System ===");

        Scanner sc = new Scanner(System.in);
        System.out.print("Admin password: ");
        String inputPass = sc.nextLine();

        if (inputPass.equals(password)) {
            System.out.println("Login successful!");
            admin.showAdminMenu();
        } else {
            System.out.println("Incorrect password. Access denied.");
        }
    }
}
