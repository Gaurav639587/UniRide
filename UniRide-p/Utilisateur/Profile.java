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

public class Profile extends Utilisateur {

    // =========================================================
    // FILES
    // =========================================================

    private static final String FICHIER_DEMANDES = "demands.txt";
    private static final String FICHIER_PROFILES = "profiles.txt";

    // Stores profiles in memory for quick access
    private static final Map<Double, Profile> profilesMap =
            new HashMap<>();


    // =========================================================
    // USER STATUS
    // =========================================================

    // Names are kept unchanged because other project classes
    // already use them.
    protected enum status {
        Passager,
        Chauffeur
    }

    /*
     * IMPORTANT:
     * This remains static because the existing project uses
     * status from static methods.
     */
    private static status status;


    // =========================================================
    // USER ROUTES AND PREFERENCES
    // =========================================================

    private List<String> itineraire;
    private List<String> preferences;


    // =========================================================
    // AVAILABILITY
    // =========================================================

    protected enum Horaire {
        Journalier,
        Hebdomadaire,
        Quotidien
    }

    private Horaire horaire;


    // =========================================================
    // TRIP TYPE
    // =========================================================

    protected enum Type {
        allerRetour,
        aller,
        retour
    }

    private Type type;


    // =========================================================
    // RATING INFORMATION
    // =========================================================

    private float moyPass = 0;
    private float moyChauff = 0;

    private int nbPass = 0;
    private int nbChauff = 0;

    private final String role;


    // =========================================================
    // GETTERS AND SETTERS
    // =========================================================

    public status getStatus() {
        return status;
    }

    public void setStatus(status status) {
        Profile.status = status;
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

    public String getRole() {
        return this.role;
    }


    // =========================================================
    // SWITCH PASSENGER / DRIVER
    // =========================================================

    public void switchStatus() {

        if (Profile.status == status.Passager) {

            Profile.status = status.Chauffeur;

        } else {

            Profile.status = status.Passager;
        }
    }


    // =========================================================
    // CALCULATE AVERAGE RATING
    // =========================================================

    public float calculMoyenne() {

        if (nbPass == 0 && nbChauff == 0) {
            return 0;
        }

        if (Profile.status == status.Passager) {

            if (nbPass == 0) {
                return 0;
            }

            return moyPass / nbPass;
        }

        if (Profile.status == status.Chauffeur) {

            if (nbChauff == 0) {
                return 0;
            }

            return moyChauff / nbChauff;
        }

        return (moyPass + moyChauff)
                / (nbPass + nbChauff);
    }


    // =========================================================
    // UPDATE CURRENT USER RATING
    // =========================================================

    public void refreshMoyenne(float rating) {

        if (Profile.status == status.Passager) {

            moyPass += rating;
            nbPass++;

        } else {

            moyChauff += rating;
            nbChauff++;
        }

        setReputation(calculMoyenne());

        try {

            mettreAJourProfil();

        } catch (IOException e) {

            System.out.println(
                    "Error updating profile: "
                            + e.getMessage()
            );
        }
    }


    // =========================================================
    // UPDATE DRIVER AND PASSENGER RATINGS
    // =========================================================

    public static void refreshMoyenne(
            Profile driver,
            Profile passenger,
            int driverRating,
            int passengerRating) {

        if (driver != null) {

            driver.moyChauff += driverRating;
            driver.nbChauff++;

            driver.setReputation(
                    driver.calculMoyenne()
            );
        }

        if (passenger != null) {

            passenger.moyPass += passengerRating;
            passenger.nbPass++;

            passenger.setReputation(
                    passenger.calculMoyenne()
            );
        }

        try {

            if (driver != null) {
                driver.mettreAJourProfil();
            }

            if (passenger != null) {
                passenger.mettreAJourProfil();
            }

        } catch (IOException e) {

            System.out.println(
                    "Error updating ratings: "
                            + e.getMessage()
            );
        }
    }


    // =========================================================
    // UPDATE RATINGS USING USER IDS
    // =========================================================

    public static boolean refreshMoyenne(
            double driverId,
            double passengerId,
            float driverRating,
            float passengerRating) {

        Profile driver =
                getProfileByMatricule(driverId);

        Profile passenger =
                getProfileByMatricule(passengerId);

        if (driver == null || passenger == null) {

            System.out.println(
                    "Error: Driver or passenger profile was not found."
            );

            return false;
        }

        refreshMoyenne(
                driver,
                passenger,
                Math.round(driverRating),
                Math.round(passengerRating)
        );

        return true;
    }


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public Profile(
            String nom,
            String prenom,
            double matricule,
            float rep,
            status status,
            List<String> itineraire,
            List<String> preferences,
            Horaire horaire,
            Type type) throws IOException {

        super(
                nom,
                prenom,
                matricule,
                rep
        );

        this.role = checkTypeUser();

        Profile.status = status;

        this.itineraire = itineraire;
        this.preferences = preferences;
        this.horaire = horaire;
        this.type = type;

        profilesMap.put(
                matricule,
                this
        );

        sauvegarderProfil();
    }


    // =========================================================
    // SAVE PROFILE
    // =========================================================

    private void sauvegarderProfil()
            throws IOException {

        String profileData =
                getNom() + "," +
                        getPrenom() + "," +
                        getMatricule() + "," +
                        getReputation() + "," +
                        Profile.status + "," +
                        String.join(
                                "|",
                                itineraire
                        ) + "," +
                        String.join(
                                "|",
                                preferences
                        ) + "," +
                        horaire + "," +
                        type + "," +
                        moyPass + "," +
                        moyChauff + "," +
                        nbPass + "," +
                        nbChauff +
                        "\n";

        Files.write(
                Paths.get(FICHIER_PROFILES),
                profileData.getBytes(
                        StandardCharsets.UTF_8
                ),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );
    }


    // =========================================================
    // UPDATE PROFILE
    // =========================================================

    public void mettreAJourProfil()
            throws IOException {

        if (!Files.exists(
                Paths.get(FICHIER_PROFILES)
        )) {

            sauvegarderProfil();
            return;
        }

        List<String> lines =
                Files.readAllLines(
                        Paths.get(FICHIER_PROFILES)
                );

        boolean found = false;

        for (int i = 0; i < lines.size(); i++) {

            String[] parts =
                    lines.get(i).split(",");

            if (parts.length >= 3) {

                try {

                    double id =
                            Double.parseDouble(
                                    parts[2]
                            );

                    if (id == getMatricule()) {

                        String updatedLine =
                                getNom() + "," +
                                        getPrenom() + "," +
                                        getMatricule() + "," +
                                        getReputation() + "," +
                                        Profile.status + "," +
                                        String.join(
                                                "|",
                                                itineraire
                                        ) + "," +
                                        String.join(
                                                "|",
                                                preferences
                                        ) + "," +
                                        horaire + "," +
                                        type + "," +
                                        moyPass + "," +
                                        moyChauff + "," +
                                        nbPass + "," +
                                        nbChauff;

                        lines.set(
                                i,
                                updatedLine
                        );

                        found = true;
                        break;
                    }

                } catch (NumberFormatException e) {

                    // Ignore invalid records
                }
            }
        }

        if (!found) {

            sauvegarderProfil();

        } else {

            Files.write(
                    Paths.get(FICHIER_PROFILES),
                    lines
            );
        }
    }


    // =========================================================
    // FIND PROFILE BY MATRICULE
    // =========================================================

    public static Profile getProfileByMatricule(
            double matricule) {

        // Check memory first
        if (profilesMap.containsKey(matricule)) {

            return profilesMap.get(matricule);
        }

        try {

            if (!Files.exists(
                    Paths.get(FICHIER_PROFILES)
            )) {

                return null;
            }

            List<String> lines =
                    Files.readAllLines(
                            Paths.get(FICHIER_PROFILES)
                    );

            for (String line : lines) {

                String[] parts =
                        line.split(",");

                if (parts.length >= 13) {

                    double id;

                    try {

                        id = Double.parseDouble(
                                parts[2]
                        );

                    } catch (NumberFormatException e) {

                        continue;
                    }

                    if (id == matricule) {

                        String nom =
                                parts[0];

                        String prenom =
                                parts[1];

                        float reputation =
                                Float.parseFloat(
                                        parts[3]
                                );

                        status userStatus =
                                status.valueOf(
                                        parts[4]
                                );

                        List<String> route =
                                parts[5].isEmpty()
                                        ? new ArrayList<>()
                                        : Arrays.asList(
                                        parts[5]
                                                .split("\\|")
                                );

                        List<String> userPreferences =
                                parts[6].isEmpty()
                                        ? new ArrayList<>()
                                        : Arrays.asList(
                                        parts[6]
                                                .split("\\|")
                                );

                        Horaire userHoraire =
                                Horaire.valueOf(
                                        parts[7]
                                );

                        Type userType =
                                Type.valueOf(
                                        parts[8]
                                );

                        /*
                         * Create the profile.
                         */
                        Profile profile =
                                new Profile(
                                        nom,
                                        prenom,
                                        matricule,
                                        reputation,
                                        userStatus,
                                        route,
                                        userPreferences,
                                        userHoraire,
                                        userType
                                );

                        profile.moyPass =
                                Float.parseFloat(
                                        parts[9]
                                );

                        profile.moyChauff =
                                Float.parseFloat(
                                        parts[10]
                                );

                        profile.nbPass =
                                Integer.parseInt(
                                        parts[11]
                                );

                        profile.nbChauff =
                                Integer.parseInt(
                                        parts[12]
                                );

                        profilesMap.put(
                                matricule,
                                profile
                        );

                        return profile;
                    }
                }
            }

        } catch (
                IOException |
                IllegalArgumentException e) {

            /*
             * NumberFormatException is already a subclass
             * of IllegalArgumentException, so it must NOT
             * be placed separately in this multi-catch.
             */
            System.out.println(
                    "Error retrieving profile: "
                            + e.getMessage()
            );
        }

        return null;
    }


    // =========================================================
    // LOAD ALL PROFILES
    // =========================================================

    public static List<Profile>
    chargerTousProfils() {

        List<Profile> profiles =
                new ArrayList<>();

        try {

            if (!Files.exists(
                    Paths.get(FICHIER_PROFILES)
            )) {

                return profiles;
            }

            List<String> lines =
                    Files.readAllLines(
                            Paths.get(FICHIER_PROFILES)
                    );

            for (String line : lines) {

                String[] parts =
                        line.split(",");

                if (parts.length >= 13) {

                    try {

                        double matricule =
                                Double.parseDouble(
                                        parts[2]
                                );

                        Profile profile =
                                getProfileByMatricule(
                                        matricule
                                );

                        if (profile != null &&
                                !profiles.contains(
                                        profile
                                )) {

                            profiles.add(profile);
                        }

                    } catch (
                            NumberFormatException e) {

                        // Ignore invalid records
                    }
                }
            }

        } catch (IOException e) {

            System.out.println(
                    "Error loading profiles: "
                            + e.getMessage()
            );
        }

        return profiles;
    }


    // =========================================================
    // ADD RIDE REQUEST
    // =========================================================

    public void ajouterDemande()
            throws IOException {

        Scanner scanner =
                new Scanner(System.in);

        System.out.println(
                "\n=== New Ride Request ==="
        );

        System.out.print(
                "Starting point: "
        );

        String departure =
                scanner.nextLine();

        System.out.print(
                "Destination: "
        );

        String destination =
                scanner.nextLine();

        System.out.print(
                "Departure time (HH:MM): "
        );

        String time =
                scanner.nextLine();

        String request =
                "Request from: " +
                        getNom() + " " +
                        getPrenom() +
                        " (ID: " +
                        getMatricule() +
                        ")\n" +

                        "Status: " +
                        Profile.status +
                        "\n" +

                        "Route: " +
                        departure +
                        " -> " +
                        destination +
                        "\n" +

                        "Preferences: " +
                        String.join(
                                ", ",
                                preferences
                        ) +
                        "\n" +

                        "Availability: " +
                        horaire +
                        "\n" +

                        "Trip type: " +
                        type +
                        "\n" +

                        "Requested time: " +
                        time +
                        "\n" +

                        "Reputation: " +
                        String.format(
                                "%.1f",
                                getReputation()
                        ) +
                        "\n" +

                        "----------------------------\n";

        Files.write(
                Paths.get(FICHIER_DEMANDES),
                request.getBytes(
                        StandardCharsets.UTF_8
                ),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );

        System.out.println(
                "\nRequest saved successfully!"
        );
    }


    // =========================================================
    // DISPLAY REQUESTS
    // =========================================================

    public static void afficherDemandes() {

        try {

            System.out.println(
                    "\n=== Ride Request List ==="
            );

            if (!Files.exists(
                    Paths.get(FICHIER_DEMANDES)
            )) {

                System.out.println(
                        "No requests at the moment."
                );

                return;
            }

            List<String> lines =
                    Files.readAllLines(
                            Paths.get(FICHIER_DEMANDES)
                    );

            if (lines.isEmpty()) {

                System.out.println(
                        "No requests at the moment."
                );

            } else {

                for (String line : lines) {

                    System.out.println(line);
                }
            }

        } catch (IOException e) {

            System.out.println(
                    "Error reading requests: "
                            + e.getMessage()
            );
        }
    }


    // =========================================================
    // MAIN - PROFILE TEST
    // =========================================================

    public static void main(
            String[] args) {

        Scanner scanner =
                new Scanner(System.in);

        try {

            System.out.println(
                    "=== Create Your Profile ==="
            );

            System.out.print(
                    "Last name: "
            );

            String lastName =
                    scanner.nextLine();

            System.out.print(
                    "First name: "
            );

            String firstName =
                    scanner.nextLine();

            System.out.print(
                    "ID: "
            );

            double matricule =
                    scanner.nextDouble();

            System.out.print(
                    "Initial reputation (1-5): "
            );

            float reputation =
                    scanner.nextFloat();

            scanner.nextLine();

            System.out.print(
                    "Status " +
                            "(1-Passenger, 2-Driver): "
            );

            status userStatus =
                    (scanner.nextInt() == 1)
                            ? status.Passager
                            : status.Chauffeur;

            scanner.nextLine();

            System.out.print(
                    "Usual route " +
                            "(separated by commas): "
            );

            List<String> route =
                    Arrays.asList(
                            scanner
                                    .nextLine()
                                    .split(",")
                    );

            System.out.print(
                    "Preferences " +
                            "(separated by commas): "
            );

            List<String> preferences =
                    Arrays.asList(
                            scanner
                                    .nextLine()
                                    .split(",")
                    );

            System.out.print(
                    "Availability " +
                            "(1-Daily, 2-Weekly, " +
                            "3-Every day): "
            );

            int availabilityChoice =
                    scanner.nextInt();

            Horaire userHoraire;

            switch (availabilityChoice) {

                case 1:

                    userHoraire =
                            Horaire.Journalier;

                    break;

                case 2:

                    userHoraire =
                            Horaire.Hebdomadaire;

                    break;

                case 3:

                    userHoraire =
                            Horaire.Quotidien;

                    break;

                default:

                    System.out.println(
                            "Invalid choice. " +
                                    "Using Daily."
                    );

                    userHoraire =
                            Horaire.Journalier;
            }

            System.out.print(
                    "Trip type " +
                            "(1-Round trip, " +
                            "2-One way, " +
                            "3-Return only): "
            );

            int typeChoice =
                    scanner.nextInt();

            Type userType;

            switch (typeChoice) {

                case 1:

                    userType =
                            Type.allerRetour;

                    break;

                case 2:

                    userType =
                            Type.aller;

                    break;

                case 3:

                    userType =
                            Type.retour;

                    break;

                default:

                    System.out.println(
                            "Invalid choice. " +
                                    "Using One Way."
                    );

                    userType =
                            Type.aller;
            }

            scanner.nextLine();

            Profile profile =
                    new Profile(
                            lastName,
                            firstName,
                            matricule,
                            reputation,
                            userStatus,
                            route,
                            preferences,
                            userHoraire,
                            userType
                    );

            System.out.println(
                    "\nProfile created successfully!"
            );

            // =================================================
            // USER MENU
            // =================================================

            while (true) {

                System.out.println(
                        "\n=== User Menu ==="
                );

                System.out.println(
                        "Current status: " +
                                profile.getStatus()
                );

                System.out.println(
                        "Reputation: " +
                                profile.getReputation()
                );

                System.out.println(
                        "1. Request a ride"
                );

                System.out.println(
                        "2. View all requests"
                );

                System.out.println(
                        "3. Change my status"
                );

                System.out.println(
                        "4. Exit"
                );

                System.out.print(
                        "Your choice: "
                );

                int choice =
                        scanner.nextInt();

                scanner.nextLine();

                switch (choice) {

                    case 1:

                        if (profile.getStatus()
                                == status.Passager) {

                            profile.ajouterDemande();

                        } else {

                            System.out.println(
                                    "Only passengers " +
                                            "can make ride requests."
                            );
                        }

                        break;

                    case 2:

                        afficherDemandes();

                        break;

                    case 3:

                        profile.switchStatus();

                        System.out.println(
                                "Your new status: " +
                                        profile.getStatus()
                        );

                        break;

                    case 4:

                        System.out.println(
                                "Goodbye!"
                        );

                        return;

                    default:

                        System.out.println(
                                "Invalid choice. " +
                                        "Please try again."
                        );
                }
            }

        } catch (Exception e) {

            System.out.println(
                    "Error: " +
                            e.getMessage()
            );

            e.printStackTrace();
        }
    }
}