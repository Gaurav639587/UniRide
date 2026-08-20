package Utilisateur;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Profile extends Utilisateur{
    public static void refreshMoyenne(Profile chauffeur, Profile passager, int noteChauff, int notePass) {
    }

    protected enum status { Passager, Chauffeur }
    private static status status;
    private List<String> itineraire;
    private List<String> preferences;
    protected enum Horaire { Journalier, Hebdomadaire, Quotidien }
    private static Horaire horaire;
    protected enum Type { allerRetour, aller, retour }
    private static Type type;
    private float moyPass = 0;
    private float moyChauff = 0;
    private int nbPass = 0;
    private int nbChauff = 0;
    private final String role;
    private static final String FICHIER_DEMANDES = "demands.txt";
    private static final String FICHIER_PROFILES = "profiles.txt";

    // Map pour stocker tous les profils en mémoire (pour un accès rapide)
    private static Map<Double, Profile> profilesMap = new HashMap<>();

    public status getStatus() {
        return status;
    }

    public void setStatus(status status) {
        this.status = status;
    }

    public List<String> getItineraire() {
        return itineraire;
    }

    public void setItineraire(List<String> itineraire) {
        this.itineraire = itineraire;
    }

    public List<String> getPreferences() {
        return preferences;
    }

    public void setPreferences(List<String> preferences) {
        this.preferences = preferences;
    }

    public Horaire getHoraire() {
        return horaire;
    }

    public void changeHoraire(Horaire horaire) {
        this.horaire = horaire;
    }

    public Type getType() {
        return type;
    }

    public void changeType(Type type) {
        this.type = type;
    }

    public String getRole() { return this.role; }

    public void switchStatus() {
        this.status = (this.status == status.Passager) ? status.Chauffeur : status.Passager;
    }

    public float calculMoyenne() {
        if (nbPass == 0 && nbChauff == 0) return 0;
        if (status == status.Passager) return moyPass / nbPass;  // Fixed: was moyPass / nbChauff
        if (status == status.Chauffeur) return moyChauff / nbChauff;  // Fixed: was moyChauff / nbPass
        return (moyPass + moyChauff) / (nbPass + nbChauff);
    }

    /**
     * Met à jour la moyenne des notes pour l'utilisateur en fonction des évaluations reçues
     * @param rating La note reçue
     */
    public void refreshMoyenne(float rating) {
        if (status == status.Passager) {
            moyPass += rating;
            nbPass++;
        } else {
            moyChauff += rating;
            nbChauff++;
        }
        setReputation(calculMoyenne());

        // Mettre à jour le profil dans le fichier
        try {
            mettreAJourProfil();
        } catch (IOException e) {
            System.out.println("Error updating profile: " + e.getMessage());
        }
    }

    /**
     * Met à jour les moyennes des notes pour le chauffeur et le passager impliqués dans une course
     * @param matChauffeur Le matricule du chauffeur
     * @param matPassager Le matricule du passager
     * @param noteChauffeur La note attribuée au chauffeur
     * @param notePassager La note attribuée au passager
     * @return true si la mise à jour a réussi, false sinon
     */
    public static boolean refreshMoyenne(double matChauffeur, double matPassager, float noteChauffeur, float notePassager) {
        // Récupérer les profils par matricule
        Profile chauffeur = getProfileByMatricule(matChauffeur);
        Profile passager = getProfileByMatricule(matPassager);

        if (chauffeur == null || passager == null) {
            System.out.println("Error: One or more profiles were not found.");
            return false;
        }

        // Mise à jour pour le chauffeur
        chauffeur.moyChauff += noteChauffeur;
        chauffeur.nbChauff++;
        chauffeur.setReputation(chauffeur.calculMoyenne());

        // Mise à jour pour le passager
        passager.moyPass += notePassager;
        passager.nbPass++;
        passager.setReputation(passager.calculMoyenne());

        // Sauvegarder les modifications
        try {
            chauffeur.mettreAJourProfil();
            passager.mettreAJourProfil();
            return true;
        } catch (IOException e) {
            System.out.println("Error updating profiles: " + e.getMessage());
            return false;
        }
    }

    public Profile(String nom, String prenom, double matricule, float rep, status status, List<String> itineraire, List<String> preferences, Horaire horaire, Type type) throws IOException {
        super(nom, prenom, matricule, rep);
        this.role = checkTypeUser();  // Input from user
        this.status = status;
        this.itineraire = itineraire;
        this.preferences = preferences;
        this.horaire = horaire;
        this.type = type;

        profilesMap.put(matricule, this);
        sauvegarderProfil(); // save into users.txt
    }

    /**
     * Sauvegarde le profil dans le fichier des profils
     */
    private void sauvegarderProfil() throws IOException {
        String profileData = getNom() + "," +
                getPrenom() + "," +
                getMatricule() + "," +
                getReputation() + "," +
                status + "," +
                String.join("|", itineraire) + "," +
                String.join("|", preferences) + "," +
                horaire + "," +
                type + "," +
                moyPass + "," +
                moyChauff + "," +
                nbPass + "," +
                nbChauff + "\n";

        Files.write(Paths.get(FICHIER_PROFILES),
                profileData.getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND);
    }

    /**
     * Met à jour le profil dans le fichier des profils
     */
    public void mettreAJourProfil() throws IOException {
        if (!Files.exists(Paths.get(FICHIER_PROFILES))) {
            sauvegarderProfil();
            return;
        }

        List<String> lines = Files.readAllLines(Paths.get(FICHIER_PROFILES));
        boolean found = false;

        for (int i = 0; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(",");
            if (parts.length >= 3 && Double.parseDouble(parts[2]) == getMatricule()) {
                String updatedLine = getNom() + "," +
                        getPrenom() + "," +
                        getMatricule() + "," +
                        getReputation() + "," +
                        status + "," +
                        String.join("|", itineraire) + "," +
                        String.join("|", preferences) + "," +
                        horaire + "," +
                        type + "," +
                        moyPass + "," +
                        moyChauff + "," +
                        nbPass + "," +
                        nbChauff;

                lines.set(i, updatedLine);
                found = true;
                break;
            }
        }

        if (!found) {
            sauvegarderProfil();
        } else {
            Files.write(Paths.get(FICHIER_PROFILES), lines);
        }
    }

    /**
     * Récupère un profil par son matricule
     * @param matricule Le matricule du profil à récupérer
     * @return Le profil correspondant ou null si non trouvé
     */
    public static Profile getProfileByMatricule(double matricule) {
        // Si le profil est déjà en mémoire, le retourner
        if (profilesMap.containsKey(matricule)) {
            return profilesMap.get(matricule);
        }

        // Sinon, essayer de le charger depuis le fichier
        try {
            if (!Files.exists(Paths.get(FICHIER_PROFILES))) {
                return null;
            }

            List<String> lines = Files.readAllLines(Paths.get(FICHIER_PROFILES));

            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length >= 13 && Double.parseDouble(parts[2]) == matricule) {
                    // Reconstruire le profil
                    String nom = parts[0];
                    String prenom = parts[1];
                    float reputation = Float.parseFloat(parts[3]);
                    status stat = status.valueOf(parts[4]);

                    List<String> itineraire = Arrays.asList(parts[5].split("\\|"));
                    List<String> preferences = Arrays.asList(parts[6].split("\\|"));

                    Horaire hor = Horaire.valueOf(parts[7]);
                    Type typ = Type.valueOf(parts[8]);

                    // Créer le profil sans le sauvegarder à nouveau
                    Profile profile = new Profile(nom, prenom, matricule, reputation, stat, itineraire, preferences, hor, typ);

                    // Mettre à jour les moyennes et compteurs
                    profile.moyPass = Float.parseFloat(parts[9]);
                    profile.moyChauff = Float.parseFloat(parts[10]);
                    profile.nbPass = Integer.parseInt(parts[11]);
                    profile.nbChauff = Integer.parseInt(parts[12]);

                    // Ajouter à la map et retourner
                    profilesMap.put(matricule, profile);
                    return profile;
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error retrieving profile: " + e.getMessage());
        }

        return null;
    }

    /**
     * Charge tous les profils depuis le fichier
     */
    public static List<Profile> chargerTousProfils() {
        List<Profile> profiles = new ArrayList<>();

        try {
            if (!Files.exists(Paths.get(FICHIER_PROFILES))) {
                return profiles;
            }

            List<String> lines = Files.readAllLines(Paths.get(FICHIER_PROFILES));

            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length >= 13) {
                    double matricule = Double.parseDouble(parts[2]);

                    // Si le profil n'est pas déjà chargé, le charger
                    if (!profilesMap.containsKey(matricule)) {
                        getProfileByMatricule(matricule);
                    }

                    profiles.add(profilesMap.get(matricule));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading profiles: " + e.getMessage());
        }

        return profiles;
    }

    public void ajouterDemande() throws IOException {
        Scanner sc = new Scanner(System.in);

        System.out.println("\n=== New Ride Request ===");
        System.out.print("Starting point: ");
        String depart = sc.nextLine();
        System.out.print("Destination: ");
        String arrivee = sc.nextLine();
        System.out.print("Departure time (HH:MM): ");
        String heure = sc.nextLine();

        String demande = "Request from: " + getNom() + " " + getPrenom() +
                " (Mat: " + getMatricule() + ")\n" +
                "Status: " + status.toString() + "\n" +
                "Route: " + depart + " -> " + arrivee + "\n" +
                "Preferences: " + String.join(", ", preferences) + "\n" +
                "Availability: " + horaire.toString() + "\n" +
                "Type: " + type.toString() + "\n" +
                "Requested time: " + heure + "\n" +
                "Reputation: " + String.format("%.1f", getReputation()) + "\n" +
                "----------------------------\n";

        Files.write(Paths.get(FICHIER_DEMANDES),
                demande.getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND);

        System.out.println("\nRequest saved successfully!");
    }

    public static void afficherDemandes() {
        try {
            System.out.println("\n=== Request List ===");

            if (!Files.exists(Paths.get(FICHIER_DEMANDES))) {
                System.out.println("No requests at the moment.");
                return;
            }

            List<String> lignes = Files.readAllLines(Paths.get(FICHIER_DEMANDES));

            if (lignes.isEmpty()) {
                System.out.println("No requests at the moment.");
            } else {
                for (String ligne : lignes) {
                    System.out.println(ligne);
                }
            }
        } catch (IOException e) {
            System.out.println("Read error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        try {
            System.out.println("=== Create Your Profile ===");
            System.out.print("Last name: ");
            String nom = sc.nextLine();
            System.out.print("First name: ");
            String prenom = sc.nextLine();
            System.out.print("ID: ");
            double matricule = sc.nextDouble();
            System.out.print("Initial reputation (1-5): ");
            float reputation = sc.nextFloat();
            sc.nextLine();


            System.out.print("Status (1-Passenger, 2-Driver): ");
            status statut = (sc.nextInt() == 1) ? status.Passager : status.Chauffeur;
            sc.nextLine();

            System.out.println("Usual route (separated by commas): ");
            List<String> itineraire = Arrays.asList(sc.nextLine().split(","));

            System.out.println("Preferences (separated by commas): ");
            List<String> preferences = Arrays.asList(sc.nextLine().split(","));

            System.out.print("Availability (1-Daily, 2-Weekly, 3-Every day): ");
            Horaire horaire = Horaire.values()[sc.nextInt()-1];

            System.out.print("Trip type (1-Round trip, 2-One way, 3-Return only): ");
            Type type = Type.values()[sc.nextInt()-1];
            sc.nextLine();

            Profile profil = new Profile(nom, prenom, matricule, reputation,
                    statut, itineraire, preferences, horaire, type);

            System.out.println("\nProfile created successfully!");


            while (true) {
                System.out.println("\n=== Main Menu ===");
                System.out.println("1. Request a ride");
                System.out.println("2. View all requests");
                System.out.println("3. Change my status");
                System.out.println("4. Exit");
                System.out.print("Choice: ");

                int choix = sc.nextInt();
                sc.nextLine();

                switch (choix) {
                    case 1:
                        if (status == status.Passager) {
                            profil.ajouterDemande();
                        } else {
                            System.out.println("Only passengers can make requests.");
                        }
                        break;
                    case 2:
                        afficherDemandes();
                        break;
                    case 3:
                        profil.switchStatus();
                        System.out.println("New status: " + status);
                        break;
                    case 4:
                        System.out.println("Goodbye!");
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice!");
                }
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}