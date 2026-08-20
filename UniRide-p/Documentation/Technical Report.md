## **Fonctionnalités principales**

* **Gestion d'utilisateurs : création de compte avec différents profils (Étudiant, Enseignant, ATS)**  
* **Système de réputation : évaluation des utilisateurs après chaque course**  
* **Gestion des statuts : alternance entre passager et chauffeur**  
* **Gestion des courses : création, acceptation et finalisation de courses**

## **Structure des classes**

* **Main : interface utilisateur et gestion des flux**  
* **Utilisateur (classe mère) → Etudiant, Enseignant, Ats (classes filles)**  
* **Profile : gestion des profils utilisateurs, préférences et disponibilités**  
* **Course : gestion du cycle de vie des courses**  
* **Admin : fonctionnalités administratives, incluant la gestion de la liste noire**

## **Classe Utilisateur (Khateb Abdelkrim)**

**La classe implémente un système de gestion d'utilisateurs avec persistance des données dans un fichier texte.**

**Structure de Données**

**La classe maintient quatre attributs principaux pour chaque utilisateur:**

* **nom et prénom: Identifiants textuels de l'utilisateur (final, immuables)**  
* **matricule: Numéro d'identification unique (final, immuable)**  
* **reputation: Score numérique entre 0 et 5**  
* **typeUser: Type d'utilisateur choisi lors de la création**

### **Validation des Données**

1. **checkNP(): Assure que les noms et prénoms contiennent uniquement des lettres, supportant les caractères internationaux en utilisant les expressions régulières (regex).**  
2. **checkDate(): Valide le format du matricule**  
3. **checkRep(): Vérifie que la réputation est entre 0-5**

### **Persistance des Utilisateurs**

**Les données utilisateur sont stockées dans un fichier texte "users.txt" avec le format suivant:matricule,nom,prenom,réputation.**

### **Fonctionnalités de Recherche**

* **Afficher tous les utilisateurs (printUsers())**  
* **Une *surchargé* de la methode precedente qui permette à afficher un nombre limité d'utilisateurs (printUsers(int i))**  
* **Trouver un utilisateur par matricule (findUser(double mat))**

### **Sécurité et Encapsulation**

* **Les attributs d'identité sont déclarés final pour prévenir les modifications**  
* **L'accès aux données est protégé par des accesseurs**  
* **La validation stricte lors de la création empêche les données incorrectes**

## **Classe Profile (Nait Mihoub Imad)**

**La classe Profile implémente des fonctionnalités spécifiques à une application de covoiturage. Elle gère deux rôles distincts (passager/chauffeur), des préférences utilisateur, et un système d'évaluation.**

### **Structure de Données**

* **Attributs de base: Hérités de la classe Utilisateur (nom, prénom, matricule, réputation)**  
* **Attributs spécifiques:**  
  * **status: Enum définissant le rôle (Passager ou Chauffeur)**  
  * **itineraire et preferences: Listes de chaînes de caractères(pour insertion et personnalisation pour chaque utilisateur)**  
  * **horaire: Enum pour la disponibilité (Journalier, Hebdomadaire, Quotidien)**  
  * **type: Enum pour le type de trajet (allerRetour, aller, retour)**  
  * **Attributs de notation: moyPass, moyChauff, nbPass, nbChauff**

### **Système de Persistance**

* **profiles.txt: Stockage des profils utilisateurs avec toutes leurs informations**  
* **demands.txt: Stockage des demandes de courses**

### **Gestion des Profils**

* **Mise en cache: Utilisation d'une HashMap statique pour stocker les profils en mémoire**  
* **Opérations CRUD: Méthodes pour créer, lire, mettre à jour les profils**

### **Système d'Évaluation**

* **Calcul de moyennes différenciées selon le statut (passager/chauffeur)**  
* **Méthodes pour mettre à jour les réputations après chaque course**

## **Classe Etudiant (Kaci Ramy)**

### **Description :**

**La classe etudiant représente un utilisateur de type étudiant. Elle hérite des attributs communs à tous les utilisateurs (nom, prénom, matricule, réputation) depuis la classe Utilisateur.**

### **Attributs specifiques :**

* **anneeAdmis : int — Année d’admission de l’étudiant.**  
* **faculte : string — Faculté à laquelle l’étudiant est rattaché.**  
* **specialite : string — Spécialité de l’étudiant.**

### **Méthodes implémentés :**

* **Constructeur super()  Appelle le constructeur de la classe parente pour initialiser les attributs hérités.**

* **Méthodes d’accès get() et de modification set() pour chaque attribut spécifique.**

## **Classe ATS (Kaci Ramy)**

### **Description :**

**La classe ats  représente le personnel administratif, technique et de service de l’université. Elle hérite des caractéristiques de base de utilisateur.**

### **Attributs specifiques:**

* **anneeRecrut : int — Année de recrutement dans l’établissement.**  
* **service : string — Service administratif ou technique dans lequel travaille la personne.**

### **Méthodes implémentés :**

* **Constructeur super() — Pour initialiser les attributs hérités.**  
* **Accesseurs et mutateurs (set()/get()) pour ses attributs spécifiques.**

## **Classe Enseignant (Kaci Ramy)**

## **Description :**

**La classe enseignant représente les membres dans le domaine de l’enseignement . Elle hérite également de Utilisateur.**

### **Attributs specifiques  :**

* **anneeRecrut : int — Année de recrutement comme enseignant.**  
* **faculte : string — Faculté d'affectation de l'enseignant.**

### **Méthodes implémentés :**

* **Constructeur super() — Pour l’initialisation via la classe parente.**  
* **Méthodes d'accès et de modification pour ses attributs.**

## **Classe Course (Nait Mihoub Imad)**

**La classe course  a été conçue pour modéliser une course entre deux utilisateurs : un chauffeur et un passager, identifiés par leurs numéro de matricule. Cette classe gère le cycle de  la course, l’évaluation mutuelle des utilisateurs, et les données dans un fichier texte (courses.txt).**

## **Structure de la classe**

### **Attributs principaux :**

* **matChauffeur *(double)* : matricule de l’utilisateur jouant le rôle de chauffeur.**  
* **matPassager *(double)* : matricule du passager.**  
* **noteChauffeur, notePassager *(int)* : notes sur 5 attribuées respectivement par le passager et le chauffeur.**  
* **commentChauffeur, commentPassager *(String)* : commentaires associés aux évaluations.**  
* **horaire *(Date)* : date et heure de la course.**  
* **status *(enum Status)* : état de la course (PENDING, IN\_PROGRESS, COMPLETED).**  
* **COURSES\_FILE *(String)* : chemin du fichier de stockage des courses.**

### **Constructeur :**

* **Initialise une nouvelle course avec les matricules fournis et un statut PENDING. L’heure actuelle est prise comme horaire initial.**

### **Accesseurs / Mutateurs :**

* **Getters et Setters classiques pour tous les attributs.**  
* **Possibilité de modifier les commentaires.**

### **Méthodes principales :**

#### **addCourse() :**

**Enregistre une nouvelle course dans le fichier courses.txt.**

**startCourse() :**

* **Change le statut de la course à IN\_PROGRESS.**  
* **Met à jour le fichier courses.txt.**

#### **endCourse() :**

* **Clôture la course avec les évaluations et les commentaires.**  
* **Met à jour le statut à COMPLETED.**  
* **Met à jour le fichier de stockage.**

#### **updateCourseFile() :**

* **Méthode privée qui lit tout le fichier courses.txt, localise la ligne correspondant à la course actuelle (via les deux matricules), la modifie et réécrit le fichier.**

#### **displayAllCourses()  :**

* **Affiche la liste de toutes les courses enregistrées, avec chauffeur, passager, date, statut et notes.**

#### **findCoursesByUser(double matricule) :**

* **Retourne une liste d’objets Course pour un utilisateur donné (qu’il soit chauffeur ou passager).**

## **Classe Admin (Khateb Abdelkrim \- Nait Mihoub Imad)**

**Structure et Fonctionnalités**

### **Authentification et Sécurité**

* **Gestion des identifiants : Un système de mot de passe statique (password) avec possibilité de modification.**  
* **Validation de mot de passe : Algorithme vérifiant la présence d'au moins un chiffre, un symbole et une longueur minimale de 8 caractères.**

### **Gestion des Utilisateurs**

* **Bannissement d'utilisateurs (banUser, unbanUser) : Système maintenant une liste noire dans un fichier externe (blacklist.txt).**  
* **Suppression d'utilisateurs (deleteUser) : Retire définitivement un utilisateur du système avec nettoyage de son statut de bannissement si applicable.**  
* **Visualisation des bannis (showBannedUsers) : Consultation de la liste des utilisateurs suspendus.**

### **Monitoring des Courses**

* **Visualisation en temps réel (viewOngoingCourses) : Affiche toutes les courses actuellement en statut "IN\_PROGRESS".**  
* **Historique complet (viewCourseHistory) : Consulte l'ensemble des courses terminées avec leurs évaluations.**  
* **Filtrage par date (viewCoursesByDate) : Permet d'isoler les courses d'une journée spécifique.**

### **Analytique et Statistiques**

* **Génération de statistiques (generateStats) : Produit un tableau de bord complet incluant :**  
  * **Répartition des utilisateurs par catégorie (étudiants, enseignants, ATS)**  
  * **Nombre d'utilisateurs actifs**  
  * **Nombre de courses par catégorie d'utilisateur**  
  * **Top 10 des chauffeurs les mieux notés (avec minimum 3 courses)**  
  * **Liste des utilisateurs les moins bien notés (potentiels candidats au bannissement)**

### **Interface Utilisateur**

* **Interface console interactive (showAdminMenu) permettant d'accéder à toutes les fonctionnalités via un menu numéroté.**

## **Gestion des Données**

### **Structures de Fichiers**

* **blacklist.txt : Stockage des identifiants des utilisateurs bannis**  
* **users.txt : Base de données des utilisateurs inscrits**  
* **courses.txt : Registre des courses (en cours, complétées, programmées)**  
* **profiles.txt : Profils détaillés des utilisateurs avec notes et statistiques**

### **Algorithmes Notables**

* **Calcul des moyennes des chauffeurs/passagers : Agrégation des évaluations pour identifier les utilisateurs problématiques**  
* **Filtrage intelligent : Utilisation des Stream API Java pour manipuler efficacement les collections de données**  
* **Gestion transactionnelle des fichiers : Utilisation de fichiers temporaires pour assurer l'intégrité des données lors des suppressions.**
