package com.BookingClient;

import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.stream.Collectors;

public class Main {
    static int MIN_REGISTRATION_AGE = 12;
    public enum ProgramState {
        START {
            @Override
            public ProgramState nextState(int choice) {
                switch (choice) {
                    case 0:
                        return REQUEST_LOGIN;
                    case 1:
                        return REGISTRATION;
                    case 2:
                        return EXIT;
                }
                throw new IllegalArgumentException("choice out of bounds");
            }

            @Override
            public ProgramState previousState() {
                return START;
            }
        },
        REGISTRATION {
            @Override
            public ProgramState nextState(int choice) {
                return LOGGED_IN;
            }

            @Override
            public ProgramState previousState() {
                return START;
            }
        },
        REQUEST_LOGIN {
            @Override
            public ProgramState nextState(int choice) {
                return LOGGED_IN;
            }

            @Override
            public ProgramState previousState() {
                return START;
            }
        },
        LOGGED_IN {
            @Override
            public ProgramState nextState(int choice) {
                switch (choice) {
                    case 0:
                        return SELECT_SHOW;
                    case 1:
                        return CANCEL_SHOW;
                }
                throw new IllegalArgumentException("choice out of bounds");
            }

            @Override
            public ProgramState previousState() {
                return START;
            }
        },
        SELECT_SHOW {
            @Override
            public ProgramState nextState(int choice) {
                switch (choice) {
                    case 0:
                        return AUTOMATIC_SELECTION;
                    case 1:
                        return INTERACTIVE_SELECTION;
                }
                throw new IllegalArgumentException("choice out of bounds");
            }

            @Override
            public ProgramState previousState() {
                return LOGGED_IN;
            }
        },
        AUTOMATIC_SELECTION {
            @Override
            public ProgramState nextState(int choice) {
                return PAYMENT;
            }

            @Override
            public ProgramState previousState() {
                return SELECT_SHOW;
            }
        },
        INTERACTIVE_SELECTION {
            @Override
            public ProgramState nextState(int choice) {
                return PAYMENT;
            }

            @Override
            public ProgramState previousState() {
                return SELECT_SHOW;
            }
        },
        PAYMENT {
            @Override
            public ProgramState nextState(int choice) {
                return LOGGED_IN;
            }

            @Override
            public ProgramState previousState() {
                return INTERACTIVE_SELECTION;
            }
        },
        CANCEL_SHOW {
            @Override
            public ProgramState nextState(int choice) {
                return LOGGED_IN;
            }

            @Override
            public ProgramState previousState() {
                return LOGGED_IN;
            }
        },
        EXIT {
            @Override
            public ProgramState nextState(int choice) {
                throw new UnsupportedOperationException("No 'nextState' for 'EXIT' state");
            }

            @Override
            public ProgramState previousState() {
                throw new UnsupportedOperationException("No 'nextState' for 'EXIT' state");
            }
        };
        public abstract ProgramState nextState(int choice);
        public abstract ProgramState previousState();
    }

    static ProgramState state = ProgramState.START; // State machine instance
    static Venue bcpa = new Venue("Bucks Centre for the Performing Arts (BCPA)", 20, 27); // Single venue since it never changes
    static User currentUser; //Current logged in user
//    static Scanner input = new Scanner(System.in); // For getting user input
    static ArrayList<User> users = new ArrayList<>(); //Stores all the users, this would usually be in a database.

    public static void main(String[] args) {
        AddDefaults(bcpa, users); // Adds default users and shows.


        /* Important Variable Initialization */
        int currentShowSelectedID = -1; // Current showID selected by user (BAD IMPLEMENTATION)
        LinkedList<Seat> currentUserSeatsSelected = new LinkedList<>(); // Create LinkedList of current seats chosen (FIFO) (BAD IMPLEMENTATION)

        boolean exit = false; // Boolean for main loop control
        int choice; // Holds choice for each state's function return
        // Main Loop
        while (!exit) {
            switch (state) {
                case START:
                    choice = CLI.start(); // Perform operations for START state and return the state to move to
                    state = choice >= 0 ? state.nextState(choice) : state.previousState();
                    break;
                case REQUEST_LOGIN:
                    try {
                        currentUser = CLI.loginChoice(users);
                        state = state.nextState(0);
                    } catch (CancellationException | IllegalArgumentException e) {
                        state = state.previousState();
                    }
                    break;
                case REGISTRATION:
                    try {
                        User newUser = CLI.registration(MIN_REGISTRATION_AGE);
                        /* Check for duplicate user */
                        List<String> usernames = users.stream().map(User::getUsername).collect(Collectors.toList()); // Get usernames of all users
                        List<String> emails = users.stream().map(User::getEmail).collect(Collectors.toList()); // Get email addresses of all users
                        if (!(usernames.contains(newUser.getUsername()) || emails.contains(newUser.getEmail()))) {
                            users.add(newUser); // Create new user
                            currentUser = newUser;
                            state = state.nextState(0);
                        } else {
                            System.out.println("Account creation unsuccessful. You cannot create accounts with duplicate email addresses or usernames!");
                            state = state.previousState();
                        }
                    } catch (CancellationException | IllegalArgumentException e) {
                        state = state.previousState();
                    }
                    break;
                case LOGGED_IN:
                    try {
                        choice = CLI.loggedInChoice(currentUser);
                        state = choice >= 0 ? state.nextState(choice) : state.previousState();
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid input, please try again!");
                    }
                    break;
                case SELECT_SHOW:
                    try {
                        currentShowSelectedID = CLI.selectShow(bcpa);
                        if (currentShowSelectedID >= 0) {
                            choice = CLI.selectSeatingTypeChoice();
                        } else {
                            choice = currentShowSelectedID;
                        }
                        state = choice >= 0 ? state.nextState(choice) : state.previousState();

                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid input, please try again!");
                    }
                    break;
                case AUTOMATIC_SELECTION:
                    state = CLI.seatSelection(bcpa, currentShowSelectedID, currentUserSeatsSelected, true) ? state.nextState(0) : state.previousState();
                    break;
                case INTERACTIVE_SELECTION:
                    state = CLI.seatSelection(bcpa, currentShowSelectedID, currentUserSeatsSelected, false) ? state.nextState(0) : state.previousState();
                    break;
                case PAYMENT:
                    state = CLI.paymentChoice(currentUser, currentShowSelectedID, currentUserSeatsSelected) ? state.nextState(0) : state.previousState();
                    break;
                case CANCEL_SHOW:
                    CLI.cancelShowChoice(currentUser, bcpa);
                    state = state.nextState(0);
                    break;
                default:
                    exit = true;
            }
        }
//        System.out.println(exit);
//        System.out.println(state);
//        System.out.println(currentUser);
    }

    /**
     * Adds example/default shows and users.
     * @param venue Venue to add the shows to.
     * @param userList User list to add the users to.
     */
    private static void AddDefaults(Venue venue, ArrayList<User> userList) {
        /* Creating Default Users and Shows */
        // Default customer
        userList.add(new Customer("wef","wef","wef@wef.com", "07259622506", "wefwef", "04/07/2001", "1 Normal Place, Somewhere, SW26 6EB"));
        // Default shows
        Calendar calTest1 = Calendar.getInstance();
        calTest1.set(Calendar.YEAR, 2024);
        calTest1.set(Calendar.MONTH, Calendar.JULY);
        calTest1.set(Calendar.HOUR, 16);
        calTest1.set(Calendar.MINUTE, 40);
        calTest1.set(Calendar.SECOND, 0);
        venue.addShow("Test Show", calTest1);
        Calendar calTest2;
        calTest2 = (Calendar) calTest1.clone();
        calTest2.set(Calendar.HOUR, 19);
        venue.addShow("Test Show 2", calTest2);
    }

//    /**
//     * Prints any number of strings on a new line per string, with the optional starting string "Please select an option:"
//     * @param showOptionString Adds "Please select an option:\n" at the beginning of the print statement if True, otherwise it leaves it blank.
//     * @param args Any number of strings to print.
//     */
//    public static void PrintChoices(boolean showOptionString, String... args) {
//        StringBuilder output = new StringBuilder();
//        if (showOptionString) { output.append("Please select an option:\n"); }
//        for (String arg : args) {
//            output.append(String.format("%s\n", arg));
//        }
//        System.out.println(output);
//    }

    /**
     * Validates a date of birth String.
     * @param dob Date of birth as String.
     * @return True if the 'dob' parameter is a valid date of birth and is at least 12 years ago. False otherwise.
     */
//    public static boolean ValidDateOfBirth(String dob) {
//        // Validating DOB structure with regex and parsing it with the SimpleDateFormat class
//        if (dob.matches("^\\d{1,2}/\\d{1,2}/\\d{4}$")) {
//            SimpleDateFormat inputDOB = new SimpleDateFormat("dd/MM/yyyy"); // Created to validate the date
//            inputDOB.setLenient(false); // Setting the SimpleDateFormat to be strict
//            try {
//                inputDOB.parse(dob); // Parsing date (ParseException raised if invalid/impossible date)
//                /* Validating age (at least 12 years old) */
//                Calendar inputCal = inputDOB.getCalendar(); // Convert input date to Calendar object
//                Calendar cal = Calendar.getInstance(); // Get current date
//                cal.set(cal.get(Calendar.YEAR)-MIN_REGISTRATION_AGE, cal.get(Calendar.MONTH), cal.get(Calendar.DATE)); // Take 12 years off the current date
//                if (inputCal.before(cal)) {
//                    return true; // Valid date if input date is at least 12 years ago, and a valid format
//                }
//            } catch (ParseException ignored) {
//                // Catch ParseException and ignore, as it will return false at the end of the method anyway
//            }
//        }
//        return false; // Returns false if 'dob' given was of the wrong format, not a real date, or below the minimum age requirement
//    }

    /**
     * Validates username with regex (lowercase only, no special characters, 2 or more characters).
     * @param username Username to validate.
     * @return True if valid username.
     */
//    public static boolean ValidUsername(String username) {
//        return username.matches("^[a-z0-9_-]{2,}$");
//    }

    /**
     * Validates password with regex (It must contain a minimum of eight characters, at least one uppercase letter, one lowercase letter, one number, and one special character).
     * @param pass Password to validate.
     * @return True if valid password.
     */
//    public static boolean ValidPass(String pass) {
//        return pass.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
//    }

    /**
     * Validates full name with regex.
     * @param fullName Full name to validate.
     * @return True if valid full name.
     */
//    public static boolean ValidFullName(String fullName) {
//        String lowerCaseName = fullName.toLowerCase();
//        return lowerCaseName.matches("^[a-z ,.'-]+$");
//    }

    /**
     * Validates simple mobile phone number with regex.
     * @param phoneNumber Phone number to validate.
     * @return True if valid phone number.
     */
//    public static boolean ValidPhoneNumber(String phoneNumber) {
//        return phoneNumber.matches("^\\d{11}$");
//    }

    /**
     * Validates email address with regex (accepted format = demo@contoso.com)
     * @param email Email address to validate.
     * @return True if valid email address.
     */
//    public static boolean ValidEmailAddress(String email) {
//        return email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
//    }

    /**
     * Validates home address with regex.
     * @param homeAddress Home address to validate.
     * @return True if valid home address.
     */
//    public static boolean ValidHomeAddress(String homeAddress) {
//        return homeAddress.matches("^\\d+[a-zA-Z\\s]+,[a-zA-Z\\s]+,\\s*[a-zA-Z]{1,2}\\d{1,2}\\s\\d[a-zA-Z]{1,2}$");
//    }

//    /**
//     * Logs the user in with given parameters.
//     * @param username Username for login.
//     * @param password Password for login.
//     * @return True on successful login.
//     */
//    public static boolean Login(String username, String password) {
//        // Searching for profile with matching username and valid password
//        for (User user : users) {
//            if (user.getUsername().equals(username) && user.checkPW(password)) {
//                System.out.println("Logged in!");
//                currentUser = user;
//                return true; // Successful login
//            }
//        }
//        return false;
//    }
//
//    /**
//     * Executed when in the 'START' state in the ProgramState state machine.
//     * @return Choice made by user (login 'l' = 0, register 'r' = 1, exit 'e' = 2, invalid choice = -1).
//     */
//    public static int StartChoice() {
//        PrintChoices(true,"Login (l)", "Register New Account (r)", "Exit (e)");
//        String line = input.nextLine(); // Get user input
//        switch (line) {
//            case "l":
//                return 0;
//            case "r":
//                return 1;
//            case "e":
//                return 2;
//        }
//        return -1; // Invalid choice
//    }
//
//    /**
//     * Executed when in the 'REGISTRATION' state in the ProgramState state machine.
//     * @return True if account creation was successful
//     */
//    public static boolean RegistrationChoice(ArrayList<User> userArrayList) {
//        // Array of the strings used to request the account requirements from the user
//        String[] accountRequirements = new String[]{
//                "Full Name",
//                "Username (all lowercase, no special characters)",
//                "Email Address",
//                "Mobile Number (No area codes e.g. '07334560229')",
//                "Date of Birth (In the form dd/mm/yyyy with a minimum age of 12 e.g. 15/08/1997)",
//                "Home Address (of the format 'House Number and Street, City, Postcode' with lines separated by commas e.g '1 Planning Lane, Oxford, OX3 5IQ')",
//                "Password (It must contain a minimum of eight characters, at least one uppercase letter, one lowercase letter, one number, and one special character)"
//        };
//        // Array to store the validated user inputted account details
//        String[] accountDetails = new String[accountRequirements.length];
//
//        /* Getting and validating user input */
//        int stage = 0;
//        String requirement;
//        boolean validEntry;
//        while (stage < accountRequirements.length) {
//            validEntry = false;
//            requirement = accountRequirements[stage]; // Current stage's requirement string
//            PrintChoices(false, "Exit (e)", String.format("Enter your %s:",requirement));
//            String line = input.nextLine(); // Get user input
//            if (line.equals("e")) {
//                return false; // Return false if exit 'e' is inputted
//            } else {
//                switch (stage) {
//                    case 0: // Full Name
//                        validEntry = ValidFullName(line);
//                        break;
//                    case 1: // Username
//                        validEntry = ValidUsername(line);
//                        break;
//                    case 2: // Email Address
//                        validEntry = ValidEmailAddress(line);
//                        break;
//                    case 3: // Mobile Number
//                        validEntry = ValidPhoneNumber(line);
//                        break;
//                    case 4: // Date of Birth
//                        validEntry = ValidDateOfBirth(line);
//                        break;
//                    case 5: // Home Address
//                        validEntry = ValidHomeAddress(line);
//                        break;
//                    case 6: // Password
//                        validEntry = ValidPass(line);
//                        break;
//                }
//                // If valid entry, increase stage and add entry to account details
//                if (validEntry) {
//                    accountDetails[stage] = line;
//                    stage += 1;
//                } else {
//                    System.out.printf("Please enter a valid %s%n", accountRequirements[stage]);
//                }
//            }
//        }
//
//        /* Check for duplicate user */
//        List<String> usernames = userArrayList.stream().map(User::getUsername).collect(Collectors.toList()); // Get usernames of all users
//        List<String> emails = userArrayList.stream().map(User::getEmail).collect(Collectors.toList()); // Get email addresses of all users
//        if (!(usernames.contains(accountDetails[1]) || emails.contains(accountDetails[2]))) {
//            /* Create new user */
//            users.add(new Customer(
//                    accountDetails[0], // Name
//                    accountDetails[1], // Username
//                    accountDetails[2], // Email Address
//                    accountDetails[3], // Mobile Number
//                    accountDetails[6], // Password
//                    accountDetails[4], // DOB
//                    accountDetails[5])); // Home Address
//        }
//
//        // Attempt login and return success
//        return Login(accountDetails[1], accountDetails[6]);
//    }
//
//    /**
//     * Executed when in the 'REQUEST_LOGIN' state in the ProgramState state machine.
//     * @return True if successful login.
//     */
//    public static boolean LoginChoice() {
//        int stage = 0;
//        String username = "", pass = "";
//        while (stage < 2) {
//            PrintChoices(true,"Exit (e)", String.format("Enter %s:", stage == 0 ? "username" : "password"));
//            String line = input.nextLine();
//            if (line.equals("e")) {
//                return false; // Return false if exit 'e' is inputted
//            } else {
//                if (stage == 0) {
//                    username = line;
//                } else {
//                    pass = line;
//                }
//                stage += 1;
//            }
//        }
//        // Attempt logon
//        if(Login(username, pass)) {
//            return true;
//        } else {
//            System.out.println("Username/Password was incorrect.");
//        }
//        return false;
//    }
//
//    /**
//     * Executed when in the 'LOGGED_IN' state in the ProgramState state machine.
//     * @return Choice made by user (Book Show 'b' = 0, Cancel Show 'c' = 1, exit 'e' = -1, invalid choice = -1).
//     */
//    public static int LoggedInChoice() throws IllegalArgumentException{
//        User.AccountType userType = currentUser.getAccountType();
//        int choice = -1;
//        // Output choices based on user type
//        switch (userType) {
//            case CUSTOMER:
//                PrintChoices(true,"Book Show (b)", "Cancel Show (c)", "Logout (e)");
//                break;
//            case ADMIN:
//                break;
//            case AGENT:
//                break;
//            case VENUE_MANAGER:
//                break;
//        }
//        // Get input
//        String line = input.nextLine();
//        switch (line) {
//            case "e":
//                return -1;
//            case "b":
//                choice = 0;
//                break;
//            case "c":
//                choice = 1;
//                break;
//        }
//        // Ensures only the choices available to the current user are selectable
//        if (!(userType == User.AccountType.CUSTOMER && choice <= 1 && choice >= 0)) {
//            throw new IllegalArgumentException("User not permitted to perform this action.");
//        }
//        return choice;
//    }
//
//    /**
//     * Retrieves and validates show selection from user.
//     * @return Show ID picked by the user, or -1 if 'e' is inputted.
//     */
//    public static int SelectShow() {
//        boolean validOption = false;
//        while (!validOption) {
//            PrintChoices(false, "exit (e)", "Please select a show: ");
//            int count = 1; // Counter for shows in Venue
//            int[] showIDs = new int[bcpa.getShows().size()]; // Array to hold all show IDs
//            // Displaying all shows to the user
//            for (Show show: bcpa.getShows()) {
//                System.out.printf("(%d) Name: %s\n\tTime: %s \n", count, show.getName(), show.getTime().getTime());
//                showIDs[count-1] = show.getID();
//                count += 1;
//            }
//            /* Getting and validating user input */
//            String line = input.nextLine();
//            try {
//                int choice = Integer.parseInt(line);
//                if (choice > 0 && choice <= bcpa.getShows().size()) {
//                    return bcpa.getShow(showIDs[choice-1]).getID();
//                }
//            } catch (NumberFormatException e) {
//                if (line.equals("e")) {
//                    validOption = true; // Escape loop if 'e' is
//                }
//            }
//        }
//        return -1;
//    }
//
//    /**
//     * Retrieves and validates user input for which type of seat selection they would like to choose.
//     * @return Choice made by user (Automatic Seat Selection 'a' = 0, Interactive Seat Selection 'i' = 1, exit 'e' = 2, invalid choice = -1).
//     */
//    public static int SelectSeatingTypeChoice() {
//        PrintChoices(true,"Automatic Seat Selection (a)", "Interactive Seat Selection (i)", "Exit (e)");
//        String line = input.nextLine();
//        switch (line) {
//            case "a":
//                return 0;
//            case "i":
//                return 1;
//            case "e":
//                break;
//        }
//        return -1;
//    }
//
//    /**
//     * Displays the seats to the user and indicates if the seats are empty, held, or booked.
//     * @param showID Show ID of the show to display seats for.
//     */
//    public static void DisplaySeats(int showID) throws RuntimeException{
//        Show show = bcpa.getShow(showID);
//        int numRows = bcpa.getNumRows();
//        int numCols = bcpa.getNumCols();
//        StringBuilder output = new StringBuilder();
//        //Print column numbers
//        for (int i = 1; i <= numRows; i++) {
//            output.append(String.format("%d  ", i).substring(0,3));
//        }
//        output.append("\n");
//        for (int i = 0; i < numCols; i++) {
//            // Getting row letter
//            int input = i+1;
//            StringBuilder rowLetter = new StringBuilder();
//            while (input > 0) {
//                int num = (input - 1) % 26;
//                char letter = (char) (num + 65);
//                rowLetter.insert(0, letter);
//                input = (input - 1) / 26;
//            }
//            for (int j = 0; j < numRows; j++) {
//                Seat.SeatStatus status = Seat.SeatStatus.EMPTY; // Getting seat status
//                try {
//                    Seat seat = show.getSeat(String.format("%s%d",rowLetter,j+1));
//                    status = seat.getStatus();
//                } catch (NoSuchElementException e) {
//                    throw new RuntimeException("Failed to retrieve seat data while displaying.");
//                }
//                output.append(String.format("%c  ", status.toString().charAt(0))); // Appending seat status to diagram
//            }
//            output.append(String.format("\t%s\n", rowLetter)); // Appending row letter to the string and starting new line
//        }
//        output.append("\n Key:\nE = Empty\nH = Held\nB = Booked"); // Appending key to the string
//        System.out.println(output); // Display seats
//    }
//
//    /**
//     * Retrieve and validate user input for number of seats to purchase.
//     * @param showID Show ID for the show the user is selecting tickets for.
//     * @return Number of seats/tickets chosen by the user.
//     */
//    public static int ChooseNumberOfSeats(int showID) {
//        int numTickets = 1;
//        boolean validNumTickets = false;
//        while (!validNumTickets) {
//            System.out.println("How many tickets would you like to purchase?");
//            String line = input.nextLine();
//            try {
//                numTickets = Integer.parseInt(line);
//                //TODO Implement users maximum seats per show properly (it currently only checks for the current purchase)
//                if (numTickets > 0 && numTickets <= bcpa.getShow(showID).getMaxSeatsPerUser()) {
//                    validNumTickets = true;
//                } else {
//                    System.out.printf("The number of tickets must be greater than 0, and one user may only purchase %d tickets per show.%n", bcpa.getShow(showID).getMaxSeatsPerUser());
//                }
//            } catch (NumberFormatException e) {
//                System.out.println("Please enter a valid number of tickets.");
//            }
//        }
//        return numTickets;
//    }
//
//    /**
//     * Retrieve and validate user input for price range of seats
//     * @return float[2] with the first element being the
//     */
//    public static float[] SelectPriceRange() {
//        boolean validChoice = false;
//        while (!validChoice) {
//            PrintChoices(true, "exit (e)", "Please state a preferred price range in the format '9.00-15.00'");
//            String line = input.nextLine();
//            if (line.equals("e")) {
//                validChoice = true;
//            } else {
//                try {
//                    if (line.matches("^\\d+.\\d{1,2}-\\d+.\\d{1,2}$")) {
//                        String[] pricesStrings = line.split("-");
//                        return new float[]{Float.parseFloat(pricesStrings[0]), Float.parseFloat(pricesStrings[1])};
//                    }
//                } catch (NumberFormatException ignored) {
//                }
//            }
//        }
//        return new float[]{0,0};
//    }
//
//    /**
//     * Retrieves and validates user input for seat selection.
//     * @param showID Show ID for show that the user is selecting seats for.
//     * @param seatSelection LinkedList to store the current seats selected.
//     * @param autoPickSeats If True, automatic seat selection will take place before the user is taken to interactive seat selection.
//     * @return True if valid seat selection.
//     */
//    public static boolean SeatSelection(int showID, LinkedList<Seat> seatSelection, boolean autoPickSeats) {
//        // Ensuring showID exists
//        try {
//            bcpa.getShow(showID);
//        } catch (NoSuchElementException e) {
//            return false;
//        }
//        // Stores user input
//        String line;
//
//        // Ask for number of tickets
//        int numTickets = ChooseNumberOfSeats(showID);
//
//        // Automatic Seat Selection (assuming front seats are best and back seats are the worst)
//        if (autoPickSeats) {
//            //Get user price range
//            float[] priceRange = SelectPriceRange();
//            //Getting the best available seats in price range (lower ID is better and seat list is created from the lowest ID to highest)
//            ArrayList<Integer> seatIDs = new ArrayList<>();
//            for (Seat seat : bcpa.getShow(showID).getSeats()) {
//                if (seat.getStatus() == Seat.SeatStatus.EMPTY && seat.getPrice() < priceRange[1] && seat.getPrice() > priceRange[0]) {
//                    seatIDs.add(seat.getID());
//                }
//            }
//            //Sorting available seatIDs in ascending order just in case
//            Collections.sort(seatIDs);
//            //Selecting best seats out of available seats (It is known );
//            if (seatIDs.size() > numTickets) {
//                for (int i = 0; i < numTickets; i++){
//                    Seat bestSeat = bcpa.getShow(showID).getSeat(seatIDs.get(i));
//                    seatSelection.add(bestSeat);
//                    bestSeat.setHeld();
//                }
//            } else {
//                System.out.println("No tickets filled your criteria.");
//                return false;
//            }
//        }
//
//        //Interactive Seat selection
//        boolean acceptedSeatSelection = false;
//        while (!acceptedSeatSelection) {
//            // Show seats and wait for selection
//            DisplaySeats(showID);
//            PrintChoices(true,"exit (e)", "accept selection (a)",String.format("Please select a seat you would like to book (In the format 'B3', 'A4', etc.)\n You have picked %d out of %d seats\n", seatSelection.size(), numTickets));
//            line = input.nextLine();
//            switch (line) {
//                case "e":
//                    //Set all held seats to empty and clear the selection
//                    for (Seat seat : seatSelection) {
//                        seat.setEmpty();
//                    }
//                    seatSelection.clear();
//                    return false;
//                case "a":
//                    if (seatSelection.size() == numTickets) {
//                        acceptedSeatSelection = true;
//                    }
//                    break;
//                default:
//                    try {
//                        Seat chosen = bcpa.getShow(showID).getSeat(line);
//                        // Check if taken
//                        String chosenStatus = chosen.getStatus().toString();
//                        boolean taken = chosenStatus.equals(Seat.SeatStatus.HELD.toString()) || chosenStatus.equals(Seat.SeatStatus.BOOKED.toString());
//                        if (!taken) {
//                            seatSelection.add(chosen);
//                            chosen.setHeld();
//                            bcpa.getShow(showID).getSeat(line);
//                            if (seatSelection.size() > numTickets) {
//                                seatSelection.removeFirst().setEmpty();
//                            }
//                        } else {
//                            System.out.println("Seat already taken, please pick a valid seat.");
//                        }
//                    } catch (NoSuchElementException e) {
//                        System.out.println("Please enter a valid seat.");
//                    }
//            }
//        }
//        return true;
//    }
//
//    /**
//     * Retrieves and validates user input for payment information.
//     * @param showID Show ID for the show that the user is buying tickets for
//     * @param seatSelection
//     * @return True if valid payment information.
//     */
//    private static boolean PaymentChoice(int showID, LinkedList<Seat> seatSelection) {
//        // Display costs (with volume discounts 6+ tickets = 5% off all tickets)
//        float discount = seatSelection.size() >= 6 ? 5.0f : 0.0f;
//        float totalCost = 0;
//        DecimalFormat df = new DecimalFormat("0.00");
//        for (Seat seat : seatSelection) {
//            float initialPrice = seat.getPrice();
//            float discountPrice = (1-(discount/100))*initialPrice;
//            System.out.printf("Seat: %s\tInitial Price: £%s\tDiscount: %s%%\tPrice: £%s\n", seat.getPos(), df.format(initialPrice), df.format(discount), df.format(discountPrice));
//            totalCost += discountPrice;
//        }
//        System.out.printf("Total cost: £%s\n", df.format(totalCost));
//
//        // Get card details
//        int stage = 0;
//        String cardNumber = "";
//        String securityNumber = "";
//        while (stage < 2) {
//            PrintChoices(true,"Exit (e)", String.format("Enter %s:", stage == 0 ? "Card Number (with format XXXX-XXXX-XXXX-XXXX)" : "Security Number"));
//            String line = input.nextLine();
//            if (line.equals("e")) {
//                return false;
//            } else {
//                if (stage == 0) {
//                    if (line.matches("^(\\d{4}[-\\s]){3}\\d{4}$")) {
//                        cardNumber = line;
//                        stage += 1;
//                    }
//                } else {
//                    if (line.matches("^\\d{3}$")) {
//                        securityNumber = line;
//                        stage += 1;
//                    }
//                }
//            }
//        }
//        if (currentUser.getAccountType() == User.AccountType.CUSTOMER) {
//            String[] bookedSeats = new String[seatSelection.size()];
//            for (int i = 0; i < seatSelection.size(); i++) {
//                Seat seat = seatSelection.get(i);
//                seat.setBooked();
//                bookedSeats[i] = seat.getPos();
//            }
//            Customer customer = (Customer)currentUser;
//            customer.addBooking(showID, bookedSeats);
//            seatSelection.clear();
//            System.out.println("Booking successful!");
//        }
//        return true;
//    }
//
//    /**
//     * Retrieves and validates user input for show that a user wishes to cancel, and cancels the show if a valid choice is chosen.
//     * @param user User that is cancelling a show.
//     */
//    private static void CancelShowChoice(User user, Venue venue) {
//        /* Validate that user is a customer */
//        if (!(user.getAccountType() == User.AccountType.CUSTOMER)) {
//            return;
//        }
//        /* Display customer's bookings */
//        PrintChoices(false, "Exit (any character)", "Please select a booking to cancel. Bookings: ");
//        Customer customer = (Customer)user;
//        ArrayList<Booking> bookings = customer.getBookings();
//        for (int i = 0; i < bookings.size(); i++) {
//            Booking booking = bookings.get(i);
//            Show bookedShow = venue.getShow(booking.getShowID());
//            System.out.printf("(%d) %s%n", (i+1), bookedShow.getName());
//            System.out.printf("\tTime: %s%n", bookedShow.getTime().getTime());
//            System.out.printf("\tNo. of Seats: %d%n", booking.getSeats().length);
//            System.out.printf("\tSeats: %s%n", String.join(",", booking.getSeats()));
//        }
//
//        /* Cancel booking if valid integer and then return */
//        if (input.hasNextInt()) {
//            int line = Integer.parseInt(input.nextLine());
//            if (line <= bookings.size() && line > 0) {
//                customer.cancelBooking(bookings.get(line - 1).getID());
//            }
//            return;
//        }
//        input.nextLine(); //If it is not an integer, the scanner will read and dispose of the next line
//    }
}