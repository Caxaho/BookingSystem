import java.security.InvalidParameterException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
                return SELECT_SHOW;
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

    static ProgramState state = ProgramState.START;
    static Venue bcpa = new Venue("Bucks Centre for the Performing Arts (BCPA)", 27, 20);
    static ArrayList<User> users = new ArrayList<>();
    static User currentUser;
    static Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        AddDefaults(); // Adds default users and shows.

        /* Important Variable Initialization */
        int currentShowSelectedID = -1; // Current showID selected by user (BAD IMPLEMENTATION)
        LinkedList<Seat> currentUserSeatsSelected = new LinkedList<>(); // Create LinkedList of current seats chosen (FIFO) (BAD IMPLEMENTATION)

        boolean exit = false; // Boolean for main loop control
        int choice; // Holds choice for each state's function return
        // Main Loop
        while (!exit) {
            switch (state) {
                case START:
                    try {
                        choice = StartChoice();
                        state = choice >= 0 ? state.nextState(choice) : state.previousState();
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid input, please try again!");
                    }
                    break;
                case REQUEST_LOGIN:
                    state = LoginChoice() ? state.nextState(0) : state.previousState();
                    break;
                case REGISTRATION:
                    state = RegistrationChoice() ? state.nextState(0) : state.previousState();
                    break;
                case LOGGED_IN:
                    try {
                        choice = LoggedInChoice();
                        state = choice >= 0 ? state.nextState(choice) : state.previousState();
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid input, please try again!");
                    }
                    break;
                case SELECT_SHOW:
                    try {
                        currentShowSelectedID = SelectShow();
                        if (currentShowSelectedID >= 0) {
                            choice = SelectSeatingTypeChoice();
                        } else {
                            choice = currentShowSelectedID;
                        }
                        state = choice >= 0 ? state.nextState(choice) : state.previousState();

                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid input, please try again!");
                    }
                    break;
                case AUTOMATIC_SELECTION:
                    choice = SeatSelection(currentShowSelectedID, currentUserSeatsSelected, true);
                    state = choice >= 0 ? state.nextState(choice) : state.previousState();
                    break;
                case INTERACTIVE_SELECTION:
                    choice = SeatSelection(currentShowSelectedID, currentUserSeatsSelected, false);
                    state = choice >= 0 ? state.nextState(choice) : state.previousState();
                    break;
                default:
                    exit = true;
            }
        }
        System.out.println(exit);
        System.out.println(state);
        System.out.println(currentUser);
    }

    private static void AddDefaults() {
        /* Creating Default Users and Shows */
        // Default customer
        users.add(new Customer("wef","wef","wef@wef.com", "07259622506", "wefwef", "04/07/2001", "1 Normal Place, Somewhere, SW26 6EB"));
        // Default shows
        Calendar calTest1 = Calendar.getInstance();
        calTest1.set(Calendar.YEAR, 2024);
        calTest1.set(Calendar.MONTH, Calendar.JULY);
        calTest1.set(Calendar.HOUR, 16);
        calTest1.set(Calendar.MINUTE, 40);
        calTest1.set(Calendar.SECOND, 0);
        bcpa.addShow("Test Show", calTest1);
        Calendar calTest2;
        calTest2 = (Calendar) calTest1.clone();
        calTest2.set(Calendar.HOUR, 19);
        bcpa.addShow("Test Show 2", calTest2);
    }

    public static void PrintChoices(boolean showOptionString, String... args) {
        StringBuilder output = new StringBuilder();
        if (showOptionString) { output.append("Please select an option:\n"); }
        for (String arg : args) {
            output.append(String.format("%s\n", arg));
        }
        System.out.println(output);
    }

    public static boolean ValidDateOfBirth(String dob) {
        if (dob.matches("^\\d{1,2}\\/\\d{1,2}\\/\\d{4}$")) {
            SimpleDateFormat inputDOB = new SimpleDateFormat("dd/MM/yyyy"); // Created to validate the date
            inputDOB.setLenient(false);
            try {
                inputDOB.parse(dob); // Parsing date (ParseException raised if invalid)
                // Validating age
                Calendar inputCal = inputDOB.getCalendar(); // Convert input date to Calendar object
                Calendar cal = Calendar.getInstance(); // Get current date
                cal.set(cal.get(Calendar.YEAR)-MIN_REGISTRATION_AGE, cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
                if (inputCal.before(cal)) {
                    return true; // Valid date
                }
            } catch (ParseException ignored) {
            }
        }
        return false; // Returns false if 'dob' given was of the wrong format, not a real date, or below the minimum age requirement
    }

    public static boolean ValidUsername(String username) {
        return username.matches("^[a-z0-9_-]{2,}$");
    }

    public static boolean ValidPass(String pass) {
        return pass.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
    }

    public static User Login(String username, String password) {
        // Checking for customer profile
        for (User user : users) {
            if (user.getUsername().equals(username) && user.checkPW(password)) {
                System.out.println("Logged in!");
                return user;
            }
        }
        throw new InvalidParameterException();
    }

    public static int StartChoice() {
        PrintChoices(true,"Login (l)", "Register New Account (r)", "Exit (e)");
        String line = input.nextLine();
        switch (line) {
            case "l":
                return 0;
            case "r":
                return 1;
            case "e":
                return 2;
        }
        return -1;
    }

    public static boolean RegistrationChoice() {
        String[] accountRequirements = new String[]{
                "Full Name",
                "Username",
                "Email Address",
                "Mobile Number (No area codes e.g. '07334560229')",
                "Date of Birth (In the form dd/mm/yyyy with a minimum age of 12 e.g. 15/08/1997)",
                "Home Address (of the format 'House Number and Street, City, Postcode' with lines separated by commas e.g '1 Planning Lane, Oxford, OX3 5IQ')",
                "Password"
        };
        String[] accountDetails = new String[accountRequirements.length];
        int stage = 0;
        String requirement;
        while (stage < accountRequirements.length) {
            requirement = accountRequirements[stage];
            PrintChoices(false, "Exit (e)", String.format("Enter your %s:",requirement));
            String line = input.nextLine();
            if (line.equals("e")) {
                return false;
            } else {
                switch (stage) {
                    case 0: // Full Name
                        String lowerCaseName = line.toLowerCase();
                        if (lowerCaseName.matches("^[a-z ,.'-]+$")) {
                            accountDetails[stage] = lowerCaseName;
                            stage += 1;
                        } else {
                            System.out.println("Please enter a valid name.");
                        }
                        break;
                    case 1: // Username
                        if (ValidUsername(line)) {
                            accountDetails[stage] = line;
                            stage += 1;
                        } else {
                            System.out.println("Please enter a valid username.");
                        }
                        break;
                    case 2: // Email Address
                        if (line.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                            accountDetails[stage] = line;
                            stage += 1;
                        } else {
                            System.out.println("Please enter a valid email address.");
                        }
                        break;
                    case 3: // Mobile Number
                        if (line.matches("^\\d{11}$")) {
                            accountDetails[stage] = line;
                            stage += 1;
                        } else {
                            System.out.println("Please enter a valid mobile number.");
                        }
                        break;
                    case 4: // Date of Birth
                        if (ValidDateOfBirth(line)) {
                            accountDetails[stage] = line;
                            stage += 1;
                        } else {
                            System.out.println("Please enter a valid date.");
                        }
                        break;
                    case 5: // Home Address
                        if (line.matches("^\\d+[a-zA-Z\\s]+,[a-zA-Z\\s]+,\\s*[a-zA-Z]{1,2}\\d{1,2}\\s\\d[a-zA-Z]{1,2}$")) {
                            accountDetails[stage] = line;
                            stage += 1;
                        } else {
                            System.out.println("Please enter a valid home address.");
                        }
                        break;
                    case 6: // Password
                        if (ValidPass(line)) {
                            accountDetails[stage] = line;
                            stage += 1;
                        } else {
                            System.out.println("Please enter a valid password. It must contain a minimum of eight characters, at least one uppercase letter, one lowercase letter, one number, and one special character.");
                        }
                        break;
                }
            }
        }

        if (users.add(new Customer( // Returns true if successfully added to ArrayList, false otherwise
                accountDetails[0], // Name
                accountDetails[1], // Username
                accountDetails[2], // Email Address
                accountDetails[3], // Mobile Number
                accountDetails[6], // Password
                accountDetails[4], // DOB
                accountDetails[5]))) // Home Address
        {
            try { // Attempt logon
                currentUser = Login(accountDetails[1], accountDetails[6]);
                return true;
            } catch (InvalidParameterException ignored) {
            }
        }
        return false; //Unsuccessful account creation or logon
    }

    public static boolean LoginChoice() {
        int stage = 0;
        String username = "";
        String pass = "";
        while (stage < 2) {
            PrintChoices(true,"Exit (e)", String.format("Enter %s:", stage == 0 ? "username" : "password"));
            String line = input.nextLine();
            if (line.equals("e")) {
                return false;
            } else {
                if (stage == 0) {
                    username = line;
                    stage += 1;
                } else {
                    pass = line;
                    stage += 1;
                }
            }
        }
        // Attempt logon
        try {
            currentUser = Login(username, pass);
            return true;
        } catch (InvalidParameterException e) {
            System.out.println("Username/Password was incorrect.");
        }
        return false;
    }

    public static int LoggedInChoice() {
        User.AccountType userType = currentUser.getAccountType();
        int choice = -2;
        // Output choices based on user type
        switch (userType) {
            case CUSTOMER:
                PrintChoices(true,"Book Show (b)", "Cancel Show (c)", "Logout (e)");
                break;
            case ADMIN:
                break;
            case AGENT:
                break;
            case VENUE_MANAGER:
                break;
        }
        // Get input
        String line = input.nextLine();
        switch (line) {
            case "e":
                choice = -1;
                break;
            case "b":
                choice = 0;
                break;
            case "c":
                choice = 1;
                break;
        }
        // Ensures only the choices available to the current user are selectable
        if (!(userType == User.AccountType.CUSTOMER && choice <= 1 && choice >= -1)) {
            throw new IllegalArgumentException("User not permitted to perform this action.");
        }
        return choice;
    }

    public static int SelectShow() {
        boolean validOption = false;
        while (!validOption) {
            PrintChoices(false, "exit (e)", "Please select a show: ");
            int count = 1;
            int[] showIDs = new int[bcpa.getShows().size()];
            for (Show show: bcpa.getShows()) {
                System.out.printf("Name: %s\tTime: %s (%d)\n", show.getName(), show.getTime().getTime(), count);
                showIDs[count-1] = show.getID();
                count += 1;
            }
            String line = input.nextLine();
            try {
                int choice = Integer.parseInt(line);
                if (choice > 0 && choice <= bcpa.getShows().size()) {
                    return bcpa.getShow(showIDs[choice-1]).getID();
                }
            } catch (NumberFormatException e) {
                if (line.equals("e")) {
                    validOption = true;
                }
            }
        }
        return -1;
    }

    public static int SelectSeatingTypeChoice() {
        PrintChoices(true,"Automatic Seat Selection (a)", "Interactive Seat Selection (i)", "Exit (e)");
        String line = input.nextLine();
        switch (line) {
            case "a":
                return 0;
            case "i":
                return 1;
            case "e":
                return 2;
        }
        return -1;
    }

    public static void DisplaySeats(int showID) {
        Show show = bcpa.getShow(showID);
        int numRows = bcpa.getNumRows();
        int numCols = bcpa.getNumCols();
        StringBuilder output = new StringBuilder();
        //Print column numbers
        for (int i = 1; i <= numRows; i++) {
            output.append(String.format("%d  ", i).substring(0,3));
        }
        output.append("\n");
        for (int i = 0; i < numCols; i++) {
            // Getting row letter
            int input = i+1;
            StringBuilder rowLetter = new StringBuilder();
            while (input > 0) {
                int num = (input - 1) % 26;
                char letter = (char) (num + 65);
                rowLetter.insert(0, letter);
                input = (input - 1) / 26;
            }
            for (int j = 0; j < numRows; j++) {
                //Getting seat status
                Seat.SeatStatus status = Seat.SeatStatus.EMPTY;
                try {
                    Seat seat = show.getSeat(String.format("%s%d",rowLetter,j+1));
                    status = seat.getStatus();
                } catch (NoSuchElementException e) {
                    throw new RuntimeException("Failed to retrieve seat data while displaying.");
                }
                //Appending seat status to diagram
                output.append(String.format("%c  ", status.toString().charAt(0)));
            }
            output.append(String.format("\t%s\n", rowLetter));
        }
        output.append("\n Key:\nE = Empty\nH = Held\nB = Booked");
        System.out.println(output);
    }

    public static int ChooseNumberOfSeats(int showID) {
        int numTickets = 1;
        boolean validNumTickets = false;
        while (!validNumTickets) {
            System.out.println("How many tickets would you like to purchase?");
            String line = input.nextLine();
            try {
                numTickets = Integer.parseInt(line);
                if (numTickets > 0 && numTickets <= bcpa.getShow(showID).getMaxSeatsPerUser()) {
                    validNumTickets = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number of tickets.");
            }
        }
        return numTickets;
    }

    public static float[] SelectPriceRange() {
        boolean validChoice = false;
        while (!validChoice) {
            PrintChoices(true, "exit (e)", "Please state a preferred price range in the format '9.00-15.00'");
            String line = input.nextLine();
            if (line.equals("e")) {
                validChoice = true;
            } else {
                try {
                    if (line.matches("^\\d+.\\d{1,2}-\\d+.\\d{1,2}$")) {
                        String[] pricesStrings = line.split("-");
                        return new float[]{Float.parseFloat(pricesStrings[0]), Float.parseFloat(pricesStrings[1])};
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return new float[]{0,0};
    }

    public static int SeatSelection(int showID, LinkedList<Seat> seatSelection, boolean autoPickSeats) {
        // Ensuring showID exists
        try {
            bcpa.getShow(showID);
        } catch (NoSuchElementException e) {
            return -1;
        }
        // Stores user input
        String line;

        // Ask for number of tickets
        int numTickets = ChooseNumberOfSeats(showID);

        // Automatic Seat Selection (assuming front seats are best and back seats are the worst)
        if (autoPickSeats) {
            //Get user price range
            float[] priceRange = SelectPriceRange();
            //Getting best available seats in price range (lower ID is better and seat list is created from the lowest ID to highest)
            ArrayList<Integer> seatIDs = new ArrayList<>();
            for (Seat seat : bcpa.getShow(showID).getSeats()) {
                if (seat.getStatus() == Seat.SeatStatus.EMPTY && seat.getPrice() < priceRange[1] && seat.getPrice() > priceRange[0]) {
                    seatIDs.add(seat.getID());
                }
            }
            //Sorting available seatIDs in ascending order just in case
            Collections.sort(seatIDs);
            //Selecting best seats out of available seats (It is known );
            if (seatIDs.size() > numTickets) {
                for (int i = 0; i < numTickets; i++){
                    Seat bestSeat = bcpa.getShow(showID).getSeat(seatIDs.get(i));
                    seatSelection.add(bestSeat);
                    bestSeat.setHeld();
                }
            } else {
                System.out.println("No tickets filled your criteria.");
                return -1;
            }
        }

        //Interactive Seat selection
        boolean acceptedSeatSelection = false;
        while (!acceptedSeatSelection) {
            // Show seats and wait for selection
            DisplaySeats(showID);
            PrintChoices(true,"exit (e)", "accept selection (a)",String.format("Please select a seat you would like to book (In the format 'B3', 'A4', etc.)\n You have picked %d out of %d seats\n", seatSelection.size(), numTickets));
            line = input.nextLine();
            switch (line) {
                case "e":
                    //Set all held seats to empty and clear the selection
                    for (Seat seat : seatSelection) {
                        seat.setEmpty();
                    }
                    seatSelection.clear();
                    return -1;
                case "a":
                    if (seatSelection.size() == numTickets) {
                        acceptedSeatSelection = true;
                    }
                    break;
                default:
                    try {
                        Seat chosen = bcpa.getShow(showID).getSeat(line);
                        // Check if taken
                        String chosenStatus = chosen.getStatus().toString();
                        boolean taken = chosenStatus.equals(Seat.SeatStatus.HELD.toString()) || chosenStatus.equals(Seat.SeatStatus.BOOKED.toString());
                        if (!taken) {
                            seatSelection.add(chosen);
                            chosen.setHeld();
                            bcpa.getShow(showID).getSeat(line);
                            if (seatSelection.size() > numTickets) {
                                seatSelection.removeFirst().setEmpty();
                            }
                        } else {
                            System.out.println("Seat already taken, please pick a valid seat.");
                        }
                    } catch (NoSuchElementException e) {
                        System.out.println("Please enter a valid seat.");
                    }
            }
        }
        return 0;
    }
}

