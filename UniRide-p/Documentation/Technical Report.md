## **Main Features**

* **User Management:** Account creation with different profiles (Student, Teacher, Administrative/Technical/Service Staff)
* **Reputation System:** User ratings after each ride
* **Status Management:** Switching between passenger and driver
* **Ride Management:** Creation, acceptance, and completion of rides

## **Class Structure**

* **Main:** User interface and application flow management
* **Utilisateur (Parent Class) → Etudiant, Enseignant, Ats (Child Classes)**
* **Profile:** Management of user profiles, preferences, and availability
* **Course:** Management of the ride lifecycle
* **Admin:** Administrative features, including blacklist management

## **Utilisateur Class**

**The class implements a user management system with data persistence in a text file.**

### **Data Structure**

**The class maintains four main attributes for each user:**

* **Last name and first name:** Textual identifiers of the user (final, immutable)
* **Matricule:** Unique identification number (final, immutable)
* **Reputation:** Numerical score between 0 and 5
* **User type:** Type of user selected during account creation

### **Data Validation**

1. **checkNP():** Ensures that first and last names contain only letters, supporting international characters using regular expressions (regex).
2. **checkDate():** Validates the matricule format
3. **checkRep():** Verifies that the reputation is between 0 and 5

### **User Data Persistence**

**User data is stored in a text file named `users.txt` using the following format:**

`matricule,lastName,firstName,reputation`

### **Search Features**

* **Display all users (`printUsers()`)**
* **Overloaded version of the previous method to display a limited number of users (`printUsers(int i)`)**
* **Find a user by matricule (`findUser(double mat)`)**

### **Security and Encapsulation**

* **Identity attributes are declared final to prevent modifications**
* **Data access is protected through accessor methods**
* **Strict validation during account creation prevents invalid data**

## **Profile Class**

**The Profile class implements features specific to a carpooling application. It manages two distinct roles (passenger/driver), user preferences, and a rating system.**

### **Data Structure**

* **Basic attributes:** Inherited from the `Utilisateur` class (last name, first name, matricule, reputation)
* **Specific attributes:**
  * **status:** Enum defining the role (Passenger or Driver)
  * **itineraire and preferences:** Lists of strings used for customization and personalization for each user
  * **horaire:** Enum for availability (Daily, Weekly, Every day)
  * **type:** Enum for trip type (Round trip, One way, Return only)
  * **Rating attributes:** `moyPass`, `moyChauff`, `nbPass`, `nbChauff`

### **Persistence System**

* **`profiles.txt`:** Stores user profiles with all their information
* **`demands.txt`:** Stores ride requests

### **Profile Management**

* **Caching:** Uses a static `HashMap` to store profiles in memory
* **CRUD Operations:** Methods for creating, reading, and updating profiles

### **Rating System**

* **Calculation of separate averages according to status (passenger/driver)**
* **Methods for updating reputations after each ride**

## **Etudiant Class**

### **Description**

**The `Etudiant` class represents a student user. It inherits the attributes common to all users (last name, first name, matricule, reputation) from the `Utilisateur` class.**

### **Specific Attributes**

* **`anneeAdmis` : int** — Student admission year
* **`faculte` : String** — Faculty to which the student belongs
* **`specialite` : String** — Student specialization

### **Implemented Methods**

* **`super()` constructor:** Calls the parent class constructor to initialize inherited attributes.
* **Getter and setter methods:** Provide access to and modify each specific attribute.

## **ATS Class**

### **Description**

**The `Ats` class represents the administrative, technical, and service staff of the university. It inherits the basic characteristics of the `Utilisateur` class.**

### **Specific Attributes**

* **`anneeRecrut` : int** — Recruitment year at the institution
* **`service` : String** — Administrative or technical department in which the person works

### **Implemented Methods**

* **`super()` constructor:** Initializes the inherited attributes.
* **Accessors and mutators (`set()`/`get()`):** Manage the class-specific attributes.

## **Enseignant Class**

### **Description**

**The `Enseignant` class represents members of the teaching staff. It also inherits from `Utilisateur`.**

### **Specific Attributes**

* **`anneeRecrut` : int** — Recruitment year as a teacher
* **`faculte` : String** — Faculty to which the teacher is assigned

### **Implemented Methods**

* **`super()` constructor:** Initializes the object through the parent class.
* **Accessor and modifier methods:** Provide access to and update the class-specific attributes.

## **Course Class**

**The `Course` class was designed to model a ride between two users: a driver and a passenger, identified by their matricule numbers. This class manages the ride lifecycle, mutual user ratings, and data stored in a text file (`courses.txt`).**

## **Class Structure**

### **Main Attributes**

* **`matChauffeur` *(double)*:** Matricule of the user acting as the driver
* **`matPassager` *(double)*:** Passenger's matricule
* **`noteChauffeur`, `notePassager` *(int)*:** Ratings out of 5 assigned respectively by the passenger and the driver
* **`commentChauffeur`, `commentPassager` *(String)*:** Comments associated with the ratings
* **`horaire` *(Date)*:** Date and time of the ride
* **`status` *(enum Status)*:** Ride state (`PENDING`, `IN_PROGRESS`, `COMPLETED`)
* **`COURSES_FILE` *(String)*:** Path to the ride storage file

### **Constructor**

* **Initializes a new ride with the provided matricules and a `PENDING` status. The current time is used as the initial schedule.**

### **Accessors / Mutators**

* **Standard getters and setters for all attributes**
* **Ability to modify comments**

### **Main Methods**

#### **`addCourse()`**

**Saves a new ride to the `courses.txt` file.**

#### **`startCourse()`**

* **Changes the ride status to `IN_PROGRESS`.**
* **Updates the `courses.txt` file.**

#### **`endCourse()`**

* **Completes the ride with ratings and comments.**
* **Updates the status to `COMPLETED`.**
* **Updates the storage file.**

#### **`updateCourseFile()`**

* **Private method that reads the entire `courses.txt` file, locates the line corresponding to the current ride using the two matricules, modifies it, and rewrites the file.**

#### **`displayAllCourses()`**

* **Displays the list of all recorded rides, including driver, passenger, date, status, and ratings.**

#### **`findCoursesByUser(double matricule)`**

* **Returns a list of `Course` objects for a specific user, whether they are a driver or passenger.**

## **Admin Class**

### **Structure and Features**

### **Authentication and Security**

* **Credential Management:** A static password system (`password`) with the ability to change the password.
* **Password Validation:** An algorithm that verifies the presence of at least one digit, one symbol, and a minimum length of 8 characters.

### **User Management**

* **User Banning (`banUser`, `unbanUser`):** A system that maintains a blacklist in an external file (`blacklist.txt`).
* **User Deletion (`deleteUser`):** Permanently removes a user from the system and cleans up their ban status if applicable.
* **Banned User Display (`showBannedUsers`):** Allows administrators to view the list of suspended users.

### **Ride Monitoring**

* **Real-Time View (`viewOngoingCourses`):** Displays all rides currently having the `IN_PROGRESS` status.
* **Complete History (`viewCourseHistory`):** Displays all completed rides with their ratings.
* **Date Filtering (`viewCoursesByDate`):** Allows rides from a specific day to be isolated.

### **Analytics and Statistics**

* **Statistics Generation (`generateStats`):** Produces a complete dashboard including:
  * **Distribution of users by category (students, teachers, ATS)**
  * **Number of active users**
  * **Number of rides by user category**
  * **Top 10 highest-rated drivers (with a minimum of 3 rides)**
  * **List of lowest-rated users (potential candidates for banning)**

### **User Interface**

* **Interactive console interface (`showAdminMenu`):** Provides access to all features through a numbered menu.

## **Data Management**

### **File Structures**

* **`blacklist.txt`:** Stores the IDs of banned users
* **`users.txt`:** Database of registered users
* **`courses.txt`:** Ride registry (ongoing, completed, scheduled)
* **`profiles.txt`:** Detailed user profiles with ratings and statistics

### **Notable Algorithms**

* **Driver/passenger average calculation:** Aggregates ratings to identify problematic users
* **Smart filtering:** Uses the Java Stream API to efficiently manipulate data collections
* **Transactional file management:** Uses temporary files to maintain data integrity during deletion operations