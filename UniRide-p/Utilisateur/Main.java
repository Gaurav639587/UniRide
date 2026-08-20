package Utilisateur;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    private static final String USERS_FILE = "users.txt";
    private static final String COURSES_FILE = "courses.txt";
    private static final String DEMANDS_FILE = "demands.txt";
    private static final String FICHIER_PROFILES = "profiles.txt";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== Welcome to the University Carpooling Application ===");

        initializeFiles();

        while (true) {
            System.out.println("\n=== Main Menu ===");
            System.out.println("1. Create an account");
            System.out.println("2. Log in");
            System.out.println("3. Administrator access");
            System.out.println("4. Exit");
            System.out.print("Your choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        creerCompte();
                        break;
                    case 2:
                        seConnecter();
                        break;
                    case 3:
                        accesAdmin();
                        break;
                    case 4:
                        System.out.println("Thank you for using our application. Goodbye!");
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // Initialise les fichiers nécessaires s'ils n'existent pas
 
    private static void initializeFiles() {
        try {
            // Créer les fichiers s'ils n'existent pas
            createFileIfNotExists(USERS_FILE);
            createFileIfNotExists(COURSES_FILE);
            createFileIfNotExists(DEMANDS_FILE);
            createFileIfNotExists(FICHIER_PROFILES);
        } catch (IOException e) {
            System.out.println("Error initializing files: " + e.getMessage());
        }
    }

    // Crée un fichier s'il n'existe pas

    private static void createFileIfNotExists(String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            file.createNewFile();
            System.out.println("File created: " + fileName);
        }
    }

    // Interface pour créer un compte utilisateur

    private static void creerCompte() throws IOException {
        System.out.println("\n=== Account Creation ===");

        System.out.print("Last name: ");
        String nom = scanner.nextLine();

        System.out.print("First name: ");
        String prenom = scanner.nextLine();

        double matricule;
        while (true) {
            System.out.print("Student/Employee ID (format YYYYXXXX): ");
            try {
                matricule = Double.parseDouble(scanner.nextLine());

                // Vérifier si l'utilisateur existe déjà
                if (userExists(matricule)) {
                    System.out.println("A user with this ID already exists.");
                    continue;
                }

                // Vérifier le format du matricule
                String matStr = String.format("%.0f", matricule);
                if (matStr.length() != 8) {
                    System.out.println("The ID must be in the format YYYYXXXX (8 digits).");
                    continue;
                }

                break;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }

        float reputation = 3.0f; // Réputation par défaut pour les nouveaux utilisateurs

        // Créer un utilisateur de base
        Utilisateur utilisateur = null;

        // Demander le type d'utilisateur
        System.out.println("\nUser type:");
        System.out.println("1. Student");
        System.out.println("2. Teacher");
        System.out.println("3. Administrative/Technical/Service Staff");
        System.out.print("Your choice: ");
        int typeChoix = Integer.parseInt(scanner.nextLine());

        switch (typeChoix) {
            case 1: // Étudiant
                System.out.print("Admission year: ");
                int anneeAdmission = Integer.parseInt(scanner.nextLine());

                System.out.print("Faculty: ");
                String faculteEtudiant = scanner.nextLine();

                System.out.print("Specialization: ");
                String specialite = scanner.nextLine();

                utilisateur = new Etudiant(nom, prenom, matricule, reputation, anneeAdmission, faculteEtudiant, specialite);
                break;

            case 2: // Enseignant
                System.out.print("Recruitment year: ");
                int anneeRecrutementEnseignant = Integer.parseInt(scanner.nextLine());

                System.out.print("Faculty: ");
                String faculteEnseignant = scanner.nextLine();

                utilisateur = new Enseignant(nom, prenom, matricule, reputation, anneeRecrutementEnseignant, faculteEnseignant);
                break;

            case 3: // ATS
                System.out.print("Recruitment year: ");
                int anneeRecrutementATS = Integer.parseInt(scanner.nextLine());

                System.out.print("Department: ");
                String service = scanner.nextLine();

                utilisateur = new Ats(nom, prenom, matricule, reputation, anneeRecrutementATS, service);
                break;

            default:
                System.out.println("Invalid user type.");
                return;
        }

        // Créer le profil utilisateur
        createUserProfile(utilisateur, matricule);

        System.out.println("\nAccount created successfully! You can now log in.");
    }

    // Crée un profil pour l'utilisateur

    private static void createUserProfile(Utilisateur utilisateur, double matricule) throws IOException {
        System.out.println("\n=== Profile Setup ===");

        System.out.print("Status (1-Passenger, 2-Driver): ");
        Profile.status statut = (Integer.parseInt(scanner.nextLine()) == 1) ?
                Profile.status.valueOf("Passager") :
                Profile.status.valueOf("Chauffeur");

        System.out.println("Usual route (points separated by commas): ");
        List<String> itineraire = Arrays.asList(scanner.nextLine().split(","));

        System.out.println("Preferences (separated by commas, e.g. music, non-smoking): ");
        List<String> preferences = Arrays.asList(scanner.nextLine().split(","));

        System.out.print("Availability (1-Daily, 2-Weekly, 3-Every day): ");
        int horChoix = Integer.parseInt(scanner.nextLine());
        Profile.Horaire horaire = null;
        switch (horChoix) {
            case 1: horaire = Profile.Horaire.valueOf("Journalier"); break;
            case 2: horaire = Profile.Horaire.valueOf("Hebdomadaire"); break;
            case 3: horaire = Profile.Horaire.valueOf("Quotidien"); break;
            default: horaire = Profile.Horaire.valueOf("Quotidien");
        }

        System.out.print("Trip type (1-Round trip, 2-Outbound, 3-Return): ");
        int typeChoix = Integer.parseInt(scanner.nextLine());
        Profile.Type type = null;
        switch (typeChoix) {
            case 1: type = Profile.Type.valueOf("allerRetour"); break;
            case 2: type = Profile.Type.valueOf("aller"); break;
            case 3: type = Profile.Type.valueOf("retour"); break;
            default: type = Profile.Type.valueOf("allerRetour");
        }

        // Créer le profil
        Profile profil = new Profile(
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                matricule,
                utilisateur.getReputation(),
                statut,
                itineraire,
                preferences,
                horaire,
                type
        );
    }

    // Interface de connexion utilisateur

    private static void seConnecter() throws IOException {
        System.out.println("\n=== Login ===");

        System.out.print("ID: ");
        double matricule;
        try {
            matricule = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
            return;
        }

        // Vérifier si l'utilisateur est banni
        if (isUserBanned(matricule)) {
            System.out.println("Your account has been banned by the administrator. Please contact support for more information.");
            return;
        }

        // Vérifier si l'utilisateur existe
        if (!userExists(matricule)) {
            System.out.println("No user found with this ID.");
            return;
        }

        // Charger le profil de l'utilisateur
        Profile profil = Profile.getProfileByMatricule(matricule);
        if (profil == null) {
            System.out.println("Error loading profile.");
            return;
        }

        System.out.println("\nWelcome, " + profil.getPrenom() + " " + profil.getNom() + " !");

        // Afficher le menu utilisateur
        afficherMenuUtilisateur(profil);
    }

    // Vérifie si un utilisateur est banni

    private static boolean isUserBanned(double matricule) {
        try {
            File file = new File("blacklist.txt");
            if (!file.exists()) {
                return false;
            }

            BufferedReader reader = new BufferedReader(new FileReader("blacklist.txt"));
            String line;

            while ((line = reader.readLine()) != null) {
                if (Double.parseDouble(line) == matricule) {
                    reader.close();
                    return true;
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error checking the blacklist: " + e.getMessage());
        }

        return false;
    }

    // Vérifie si un utilisateur existe dans le système

    private static boolean userExists(double matricule) {
        try {
            File file = new File(USERS_FILE);
            if (!file.exists()) {
                return false;
            }

            BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 0 && Double.parseDouble(parts[0]) == matricule) {
                    reader.close();
                    return true;
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error checking whether the user exists: " + e.getMessage());
        }

        return false;
    }

    // Affiche le menu utilisateur après connexion

    private static void afficherMenuUtilisateur(Profile profil) throws IOException {
        while (true) {
            boolean isPassager = profil.getStatus().toString().equals("Passager");

            System.out.println("\n=== User Menu ===");
            System.out.println("Current status: " + profil.getStatus());
            System.out.println("Reputation: " + String.format("%.1f", profil.getReputation()));

            if (isPassager) {
                System.out.println("1. Request a ride");
                System.out.println("2. View my ride history");
                System.out.println("3. Change my status (become a driver)");
            } else {
                System.out.println("1. View available ride requests");
                System.out.println("2. View my ride history");
                System.out.println("3. Change my status (become a passenger)");
            }

            System.out.println("4. Edit my profile");
            System.out.println("5. Log out");
            System.out.print("Your choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        if (isPassager) {
                            faireDemandeCoursePourPassager(profil);
                        } else {
                            voirDemandesDisponibles(profil);
                        }
                        break;
                    case 2:
                        voirHistoriqueCourses(profil);
                        break;
                    case 3:
                        profil.switchStatus();
                        System.out.println("Status changed successfully!");
                        break;
                    case 4:
                        modifierProfil(profil);
                        break;
                    case 5:
                        System.out.println("Logging out...");
                        return;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    // Permet à un passager de faire une demande de course

    private static void faireDemandeCoursePourPassager(Profile profil) throws IOException {
        System.out.println("\n=== Request a Ride ===");

        System.out.print("Starting point: ");
        String depart = scanner.nextLine();

        System.out.print("Destination: ");
        String arrivee = scanner.nextLine();

        System.out.print("Departure time (HH:MM): ");
        String heure = scanner.nextLine();

        String demande = "Request from: " + profil.getNom() + " " + profil.getPrenom() +
                " (Mat: " + profil.getMatricule() + ")\n" +
                "Status: " + profil.getStatus().toString() + "\n" +
                "Route: " + depart + " -> " + arrivee + "\n" +
                "Preferences: " + String.join(", ", profil.getPreferences()) + "\n" +
                "Availability: " + profil.getHoraire().toString() + "\n" +
                "Type: " + profil.getType().toString() + "\n" +
                "Requested time: " + heure + "\n" +
                "Reputation: " + String.format("%.1f", profil.getReputation()) + "\n" +
                "----------------------------\n";

        Files.write(Paths.get(DEMANDS_FILE),
                demande.getBytes(),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND);

        System.out.println("Ride request saved successfully!");
    }

    // Affiche les demandes de course disponibles pour les chauffeurs

    private static void voirDemandesDisponibles(Profile profil) throws IOException {
        System.out.println("\n=== Available Ride Requests ===");

        File file = new File(DEMANDS_FILE);
        if (!file.exists() || file.length() == 0) {
            System.out.println("No ride requests are currently available.");
            return;
        }

        List<String> demandes = new ArrayList<>();
        List<String> demandesCompletes = Files.readAllLines(Paths.get(DEMANDS_FILE));

        // Extraire les demandes individuelles
        StringBuilder currentDemande = new StringBuilder();
        int index = 1;

        for (String line : demandesCompletes) {
            if (line.equals("----------------------------")) {
                currentDemande.append(line).append("\n");
                demandes.add(currentDemande.toString());
                currentDemande = new StringBuilder();
            } else {
                currentDemande.append(line).append("\n");
            }
        }

        // Afficher les demandes
        if (demandes.isEmpty()) {
            System.out.println("No ride requests are currently available.");
            return;
        }

        for (int i = 0; i < demandes.size(); i++) {
            System.out.println("\nDemande #" + (i + 1));
            System.out.print(demandes.get(i));
        }

        // Demander au chauffeur s'il veut accepter une demande
        System.out.print("\nWould you like to accept a request? (Y/N): ");
        String reponse = scanner.nextLine();

        if (reponse.equalsIgnoreCase("Y")) {
            System.out.print("Enter the number of the request you want to accept: ");
            int choix = Integer.parseInt(scanner.nextLine()) - 1;

            if (choix >= 0 && choix < demandes.size()) {
                accepterDemande(demandes.get(choix), profil);
            } else {
                System.out.println("Invalid request number.");
            }
        }
    }

    // Permet à un chauffeur d'accepter une demande de course

    private static void accepterDemande(String demande, Profile chauffeur) throws IOException {
        // Extraire les informations de la demande
        String[] lignes = demande.split("\n");

        // Extraire le matricule du passager
        String ligneMat = lignes[0];
        double matPassager = Double.parseDouble(ligneMat.substring(ligneMat.indexOf("Mat: ") + 5, ligneMat.indexOf(")")));

        // Créer une nouvelle course
        Course course = new Course(chauffeur.getMatricule(), matPassager);
        course.addCourse();
        course.startCourse();

        // Supprimer la demande du fichier
        supprimerDemande(demande);

        System.out.println("\nYou accepted the ride request!");
        System.out.println("The ride has been created and is now in progress.");

        // Demander au chauffeur s'il veut terminer la course
        System.out.print("\nWould you like to end the ride now? (Y/N): ");
        String reponse = scanner.nextLine();
        if (reponse.equalsIgnoreCase("Y")) {
            terminerCourse(course);
        }
    }

    // Supprime une demande de course du fichier

    private static void supprimerDemande(String demande) throws IOException {
        List<String> toutesLignes = Files.readAllLines(Paths.get(DEMANDS_FILE));
        String contenuFichier = String.join("\n", toutesLignes);

        // Remplacer la demande par une chaîne vide
        contenuFichier = contenuFichier.replace(demande, "");

        // Nettoyer les lignes vides
        contenuFichier = contenuFichier.replaceAll("(?m)^\\s*$\\n", "");

        // Réécrire le fichier
        Files.write(Paths.get(DEMANDS_FILE), contenuFichier.getBytes());
    }

    // Permet de terminer une course

    private static void terminerCourse(Course course) throws IOException {
        System.out.println("\n=== End Ride ===");

        // Note pour le chauffeur
        int noteChauffeur;
        while (true) {
            System.out.print("Driver rating (1-5): ");
            try {
                noteChauffeur = Integer.parseInt(scanner.nextLine());
                if (noteChauffeur >= 1 && noteChauffeur <= 5) {
                    break;
                } else {
                    System.out.println("The rating must be between 1 and 5.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }

        // Note pour le passager
        int notePassager;
        while (true) {
            System.out.print("Passenger rating (1-5): ");
            try {
                notePassager = Integer.parseInt(scanner.nextLine());
                if (notePassager >= 1 && notePassager <= 5) {
                    break;
                } else {
                    System.out.println("The rating must be between 1 and 5.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }

        // Commentaires
        System.out.print("Comment for the driver: ");
        String commentChauffeur = scanner.nextLine();

        System.out.print("Comment for the passenger: ");
        String commentPassager = scanner.nextLine();

        // Terminer la course
        course.endCourse(noteChauffeur, notePassager, commentChauffeur, commentPassager);

        System.out.println("\nRide completed successfully!");
    }

    // Affiche l'historique des courses d'un utilisateur

    private static void voirHistoriqueCourses(Profile profil) throws IOException {
        System.out.println("\n=== My Ride History ===");

        File file = new File(COURSES_FILE);
        if (!file.exists() || file.length() == 0) {
            System.out.println("No rides recorded.");
            return;
        }

        List<String> lines = Files.readAllLines(Paths.get(COURSES_FILE));
        boolean coursesFound = false;

        System.out.println("Chauffeur\tPassager\tHoraire\t\t\tStatut\tNoteCh\tNotePass\tCommentaires");

        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length >= 4) {
                double matChauffeur = Double.parseDouble(parts[0]);
                double matPassager = Double.parseDouble(parts[1]);

                if (matChauffeur == profil.getMatricule() || matPassager == profil.getMatricule()) {
                    // Afficher les détails de la course
                    String roleUtilisateur = (matChauffeur == profil.getMatricule()) ? "Chauffeur" : "Passager";
                    String statut = parts[3];
                    String noteChauffeur = (parts.length > 6) ? parts[6] : "N/A";
                    String notePassager = (parts.length > 7) ? parts[7] : "N/A";
                    String commentaires = (parts.length > 8) ? parts[8] + " / " + ((parts.length > 9) ? parts[9] : "") : "Aucun";

                    System.out.println(parts[0] + "\t" + parts[1] + "\t" + parts[2] + "\t" +
                            statut + "\t" + noteChauffeur + "\t" + notePassager + "\t" + commentaires);

                    coursesFound = true;
                }
            }
        }

        if (!coursesFound) {
            System.out.println("No rides found for your account.");
        }
    }

    // Permet de modifier le profil utilisateur
    private static void modifierProfil(Profile profil) throws IOException {
        System.out.println("\n=== Edit My Profile ===");

        System.out.println("1. Edit my preferences");
        System.out.println("2. Edit my usual route");
        System.out.println("3. Edit my availability");
        System.out.println("4. Edit my trip type");
        System.out.println("5. Back");
        System.out.print("Your choice: ");

        int choice = Integer.parseInt(scanner.nextLine());

        switch (choice) {
            case 1:
                System.out.println("New preferences (separated by commas): ");
                List<String> preferences = Arrays.asList(scanner.nextLine().split(","));
                profil.setPreferences(preferences);
                break;
            case 2:
                System.out.println("New usual route (points separated by commas): ");
                List<String> itineraire = Arrays.asList(scanner.nextLine().split(","));
                profil.setItineraire(itineraire);
                break;
            case 3:
                System.out.print("Availability (1-Daily, 2-Weekly, 3-Every day): ");
                int horChoix = Integer.parseInt(scanner.nextLine());
                Profile.Horaire horaire = null;
                switch (horChoix) {
                    case 1: horaire = Profile.Horaire.valueOf("Journalier"); break;
                    case 2: horaire = Profile.Horaire.valueOf("Hebdomadaire"); break;
                    case 3: horaire = Profile.Horaire.valueOf("Quotidien"); break;
                    default: horaire = Profile.Horaire.valueOf("Quotidien");
                }
                profil.changeHoraire(horaire);
                break;
            case 4:
                System.out.print("Trip type (1-Round trip, 2-Outbound, 3-Return): ");
                int typeChoix = Integer.parseInt(scanner.nextLine());
                Profile.Type type = null;
                switch (typeChoix) {
                    case 1: type = Profile.Type.valueOf("allerRetour"); break;
                    case 2: type = Profile.Type.valueOf("aller"); break;
                    case 3: type = Profile.Type.valueOf("retour"); break;
                    default: type = Profile.Type.valueOf("allerRetour");
                }
                profil.changeType(type);
                break;
            case 5:
                return;
            default:
                System.out.println("Invalid choice.");
        }

        profil.mettreAJourProfil();
        System.out.println("Profile updated successfully!");
    }

    // Interface d'accès administrateur

    private static void accesAdmin() {
        System.out.println("\n=== Administrator Access ===");

        System.out.print("Administrator password: ");
        String password = scanner.nextLine();

        try {
            Admin admin = new Admin();
            // Vérification du mot de passe
            if (password.equals("pass123")) { // Mot de passe par défaut défini dans Admin.java
                System.out.println("Login successful!");
                admin.showAdminMenu();
            } else {
                System.out.println("Incorrect password. Access denied.");
            }
        } catch (Exception e) {
            System.out.println("Error accessing administrator panel: " + e.getMessage());
        }
    }
}
