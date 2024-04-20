package com.BookingClient;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.stream.Collectors;

/**
 * Utility class to print to the CLI and get user input.
 */
public final class CLI {

    private static final Scanner input = new Scanner(System.in); //For user input

    private CLI() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    /**
     * Prints any number of strings on a new line per string, with the optional starting string "Please select an option:"
     * @param showOptionString Adds "Please select an option:\n" at the beginning of the print statement if True, otherwise it leaves it blank.
     * @param args Any number of strings to print.
     */
    public static void printChoices(boolean showOptionString, String... args) {
        StringBuilder output = new StringBuilder();
        if (showOptionString) { output.append("Please select an option:\n"); }
        for (String arg : args) {
            output.append(String.format("%s\n", arg));
        }
        System.out.println(output);
    }

    //TODO Possibly move the 'login' function to the 'User' class
    /**
     * Logs the user in with given parameters.
     * @param username Username for login.
     * @param password Password for login.
     * @param users Array List of all registered users.
     * @return User on successful login.
     * @throws IllegalArgumentException When invalid username or password.
     */
    public static User login(String username, String password, ArrayList<User> users) throws IllegalArgumentException {
        // Searching for profile with matching username and valid password
        for (User user : users) {
            if (user.getUsername().equals(username) && user.checkPW(password)) {
                System.out.println("Logged in!");
                return user; // Successful login
            }
        }
        throw new IllegalArgumentException("Invalid username or password!");
    }

    /**
     * Validates a date to be in the future.
     * @param date Date to validate.
     * @return True if date is the correct format and in the future.
     */
    public static boolean validFutureDate(String date) {
        // Validating date structure with regex and parsing it with the SimpleDateFormat class
        if (date.matches("^\\d{1,2}/\\d{1,2}/\\d{4}$")) {
            SimpleDateFormat inputDate = new SimpleDateFormat("dd/MM/yyyy"); // Created to validate the date
            inputDate.setLenient(false); // Setting the SimpleDateFormat to be strict
            try {
                inputDate.parse(date); // Parsing date (ParseException raised if invalid/impossible date)
                /* Validating date is in the future */
                Calendar inputCal = inputDate.getCalendar(); // Convert input date to Calendar object
                Calendar currentCal = Calendar.getInstance(); // Get current date
                if (inputCal.after(currentCal)) {
                    return true; // Valid date if input date is in the future, and a valid format
                }
            } catch (ParseException ignored) {
                // Catch ParseException and ignore, as it will return false at the end of the method anyway
            }
        }
        return false; // Returns false if 'date' given was of the wrong format, not a real date, or in the past.
    }

    /**
     * Retrieves and validates user input for selecting an option when in the initial program state.
     * @return Choice made by user (login 'l' = 0, register 'r' = 1, exit 'e' = 2, invalid choice = -1).
     */
    public static int start() {
        printChoices(true,"Login (l)", "Register New Account (r)", "Exit (e)");
        String line = input.nextLine(); // Get user input
        switch (line) {
            case "l":
                return 0;
            case "r":
                return 1;
            case "e":
                return 2;
        }
        return -1; // Invalid choice
    }

    /**
     * Retrieves and validates user input to create a user.
     * @return User (Customer) if account creation was successful.
     * @throws CancellationException If user inputs 'e'.
     */
    public static User registration(int minRegistrationAge) throws CancellationException {
        // Array of the strings used to request the account requirements from the user
        String[] accountRequirements = new String[]{
                "Full Name",
                "Username (all lowercase, no special characters)",
                "Email Address",
                "Mobile Number (No area codes e.g. '07334560229')",
                "Date of Birth (In the form dd/mm/yyyy with a minimum age of 12 e.g. 15/08/1997)",
                "Home Address (of the format 'House Number and Street, City, Postcode' with lines separated by commas e.g '1 Planning Lane, Oxford, OX3 5IQ')",
                "Password (It must contain a minimum of eight characters, at least one uppercase letter, one lowercase letter, one number, and one special character)"
        };
        // Array to store the validated user inputted account details
        String[] accountDetails = new String[accountRequirements.length];

        /* Getting and validating user input */
        int stage = 0;
        String requirement;
        boolean validEntry;
        while (stage < accountRequirements.length) {
            validEntry = false;
            requirement = accountRequirements[stage]; // Current stage's requirement string
            printChoices(false, "Exit (e)", String.format("Enter your %s:",requirement));
            String line = input.nextLine(); // Get user input
            if (line.equals("e")) {
                throw new CancellationException("Exit ('e') was inputted by the user."); // Return false if exit 'e' is inputted
            } else {
                switch (stage) {
                    case 0: // Full Name
                        validEntry = Customer.validFullName(line);
                        break;
                    case 1: // Username
                        validEntry = Customer.validUsername(line);
                        break;
                    case 2: // Email Address
                        validEntry = Customer.validEmailAddress(line);
                        break;
                    case 3: // Mobile Number
                        validEntry = Customer.validPhoneNumber(line);
                        break;
                    case 4: // Date of Birth
                        validEntry = Customer.validDateOfBirth(line, minRegistrationAge);
                        break;
                    case 5: // Home Address
                        validEntry = Customer.validHomeAddress(line);
                        break;
                    case 6: // Password
                        validEntry = Customer.validPass(line);
                        break;
                }
                // If valid entry, increase stage and add entry to account details
                if (validEntry) {
                    accountDetails[stage] = line;
                    stage += 1;
                } else {
                    System.out.printf("Please enter a valid %s%n", accountRequirements[stage]);
                }
            }
        }

        // Return 'Customer' that was created
        return new Customer(
                accountDetails[0], // Name
                accountDetails[1], // Username
                accountDetails[2], // Email Address
                accountDetails[3], // Mobile Number
                accountDetails[6], // Password
                accountDetails[4], // DOB
                accountDetails[5]); // Home Address
    }

    /**
     * Retrieve and validate input from user to attempt to login.
     * @return True if successful login.
     * @throws CancellationException or IllegalArgumentException if user inputs 'e' for exit, or, invalid username or password was inputted.
     */
    public static User loginChoice(ArrayList<User> users) throws CancellationException, IllegalArgumentException {
        int stage = 0;
        String username = "", pass = "";
        while (stage < 2) {
            printChoices(true,"Exit (e)", String.format("Enter %s:", stage == 0 ? "username" : "password"));
            String line = input.nextLine();
            if (line.equals("e")) {
                throw new CancellationException("Exit ('e') was inputted by the user."); // Throw  if exit 'e' is inputted
            } else {
                if (stage == 0) {
                    username = line;
                } else {
                    pass = line;
                }
                stage += 1;
            }
        }
        // Attempt logon and return User if successful (Error is thrown otherwise)
        return login(username, pass, users);
    }

    /**
     * Executed when in the 'LOGGED_IN' state in the ProgramState state machine.
     * @param currentUser Currently logged-in user.
     * @return Choice made by user (Book Show 'b' = 0, Cancel Show 'c' = 1, exit 'e' = -1, invalid choice = -1).
     * @throws IllegalArgumentException When a user selects an action that they are not permitted to perform.
     */
    public static int loggedInChoice(User currentUser) throws IllegalArgumentException{
        User.AccountType userType = currentUser.getAccountType();
        int choice = -1;
        // Output choices based on user type
        switch (userType) {
            case CUSTOMER:
                printChoices(true,"Book Show (b)", "Cancel Show (c)", "Logout (e)");
                break;
            case ADMIN:
                break;
            case AGENT:
                break;
            case VENUE_MANAGER:
                printChoices(true, "Manage Shows(s)", "Manage Promotions(p)", "Logout (e)");
                break;
        }
        // Get input
        String line = input.nextLine();
        switch (line) {
            case "e":
                return -1;
//                break;
            case "b":
                choice = 0;
                break;
            case "c":
                choice = 1;
                break;
            case "s":
                choice = 2;
                break;
            case "p":
                choice = 3;
                break;
        }
        // Validates user choice. Ensures only the choices available to the current user are selectable
        boolean validChoice = true;
        switch (userType) {
            case CUSTOMER:
                if (!(choice <= 1 && choice >= 0)) {
                    validChoice = false;
                }
                break;
            case ADMIN:
                break;
            case AGENT:
                break;
            case VENUE_MANAGER:
                if (!(choice <= 3 && choice >= 2)) {
                    validChoice = false;
                }
                break;
        }
        if (!validChoice) {
            throw new IllegalArgumentException("User not permitted to perform this action.");
        }
        return choice;
    }

    /**
     * Validates and retrieves user input for a date range in the future
     * @return Date range as Calendar[2] (if valid), otherwise date range from present time to 1 year in the future.
     * @throws ParseException If dates fail to parse after they have been validated. Should be impossible.
     * @throws IllegalArgumentException If invalid date format is inputted by the user.
     */
    public static Calendar[] getFutureDateRange() throws ParseException, IllegalArgumentException {
        printChoices(false,"Exit (any character)", "Please enter a date range in the format dd/MM/yyyy-dd/MM/yyyy (If invalid or past dates are entered, all shows within the next year will be displayed):");
        String line = input.nextLine(); // Get user input
        String[] splitLine = line.split("-"); // Split user input into separate Strings using regex '-'
        /* Validate correct number of inputs */
        if (splitLine.length != 2) {
            throw new IllegalArgumentException("Invalid date range!");
        }
        /* Validate dates are in correct format and in the future. If not, returns large date range (present to +1 year in the future) */
        if (!(validFutureDate(splitLine[0]) && validFutureDate(splitLine[1]))) {
            /* Create present and future Calendars, then Dates from those Calendars */
            // Calendars
            Calendar present = Calendar.getInstance(); // Initialise present Calendar
            Calendar future = Calendar.getInstance(); // Initialise future Calendar
            future.add(Calendar.YEAR, 1); // Add 1 year to 'future' Calendar
            return new Calendar[]{present, future}; // Return date range from present to 1 year in the future
        }
        /* Orders dates from lowest to highest and returns them */
        // Converting validated string inputs into Calendar objects
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy"); // Date format to convert dates back to strings
        Calendar first = Calendar.getInstance();
        first.setTime(df.parse(splitLine[0]));
        Calendar second = Calendar.getInstance();
        second.setTime(df.parse(splitLine[1]));
        if (first.before(second)) {
            return new Calendar[]{first, second};
        } else {
            return new Calendar[]{second, first};
        }
    }

    /**
     * Retrieves and validates show selection from user.
     * @param venue Venue to retrieve the shows of.
     * @return Show ID picked by the user, or -1 if 'e' is inputted.
     * @throws IllegalArgumentException On user input validation error.
     * @throws ParseException On user input parse error. The parsing occurs after validation so this exception should never occur.
     */
    public static int selectShow(Venue venue) throws ParseException, IllegalArgumentException {
        Calendar[] dateRange = getFutureDateRange(); // Get validated date range from user.
        boolean validOption = false;
        while (!validOption) {
            printChoices(false, "exit (e)", "Please select a show: ");
            /* Get ShowIDs of Shows within date range */
            ArrayList<Show> allShows = venue.getShows(); // Get all shows in venue
            ArrayList<Show> validShows = new ArrayList<>();
            for (Show show : allShows) {
                if (show.getTime().after(dateRange[0]) && show.getTime().before(dateRange[1])) {
                    validShows.add(show);
                }
            }
            // If there are no shows matching the user's criteria, notify the user of this, and return -1.
            if (validShows.isEmpty()) {
                System.out.println("No shows match the criteria entered.");
                return -1;
            }
            /* Displaying all shows to the user */
            int[] showIDs = new int[validShows.size()]; // Array to hold all show IDs
            // Display shows
            for (int i = 1; i <= validShows.size(); i++) {
                Show show = validShows.get(i-1);
                System.out.printf("(%d) Name: %s\n\tTime: %s \n", i, show.getName(), show.getTime().getTime());
                showIDs[i-1] = show.getID();
            }
            /* Getting and validating user input for show selection */
            String line = input.nextLine();
            try {
                int choice = Integer.parseInt(line);
                if (choice > 0 && choice <= venue.getShows().size()) {
                    return venue.getShow(showIDs[choice-1]).getID();
                }
            } catch (NumberFormatException e) {
                if (line.equals("e")) {
                    validOption = true; // Escape loop if 'e' is
                }
            }
        }
        return -1;
    }

    /**
     * Retrieves and validates user input for which type of seat selection they would like to choose.
     * @return Choice made by user (Automatic Seat Selection 'a' = 0, Interactive Seat Selection 'i' = 1, exit 'e' = 2, invalid choice = -1).
     */
    public static int selectSeatingTypeChoice() {
        printChoices(true,"Automatic Seat Selection (a)", "Interactive Seat Selection (i)", "Exit (e)");
        String line = input.nextLine();
        switch (line) {
            case "a":
                return 0;
            case "i":
                return 1;
            case "e":
                break;
        }
        return -1;
    }

    /**
     * Displays the seats to the user and indicates if the seats are empty, held, or booked.
     * @param showID Show ID of the show to display seats for.
     * @throws RuntimeException When there is an error retrieving the seating data.
     */
    public static void displaySeats(Venue venue, int showID) throws RuntimeException{
        Show show = venue.getShow(showID);
        int numRows = venue.getNumRows();
        int numCols = venue.getNumCols();
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
                Seat.SeatStatus status; // Getting seat status
                try {
                    Seat seat = show.getSeat(String.format("%s%d",rowLetter,j+1));
                    status = seat.getStatus();
                } catch (NoSuchElementException e) {
                    throw new RuntimeException("Failed to retrieve seat data while displaying.");
                }
                output.append(String.format("%c  ", status.toString().charAt(0))); // Appending seat status to diagram
            }
            output.append(String.format("\t%s\n", rowLetter)); // Appending row letter to the string and starting new line
        }
        output.append("\n Key:\nE = Empty\nH = Held\nB = Booked"); // Appending key to the string
        System.out.println(output); // Display seats
    }

    /**
     * Retrieve and validate user input for number of seats to purchase.
     * @param showID Show ID for the show the user is selecting tickets for.
     * @return Number of seats/tickets chosen by the user.
     */
    public static int chooseNumberOfSeats(Venue venue, int showID) {
        int numTickets = 1;
        boolean validNumTickets = false;
        while (!validNumTickets) {
            System.out.println("How many tickets would you like to purchase?");
            String line = input.nextLine();
            try {
                numTickets = Integer.parseInt(line);
                //TODO Implement users maximum seats per show properly (it currently only checks for the current purchase)
                if (numTickets > 0 && numTickets <= venue.getShow(showID).getMaxSeatsPerUser()) {
                    validNumTickets = true;
                } else {
                    System.out.printf("The number of tickets must be greater than 0, and one user may only purchase %d tickets per show.%n", venue.getShow(showID).getMaxSeatsPerUser());
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number of tickets.");
            }
        }
        return numTickets;
    }

    /**
     * Retrieve and validate user input for price range of seats
     * @return float[2] with the first element being the
     */
    public static float[] selectPriceRange() {
        boolean validChoice = false;
        while (!validChoice) {
            printChoices(true, "exit (e)", "Please state a preferred price range in the format '9.00-15.00'");
            String line = input.nextLine();
            if (line.equals("e")) {
                validChoice = true;
            } else {
                try {
                    if (line.matches("^\\d+\\.?\\d{0,2}-\\d+\\.?\\d{0,2}$")) {
                        String[] pricesStrings = line.split("-");
                        return new float[]{Float.parseFloat(pricesStrings[0]), Float.parseFloat(pricesStrings[1])};
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return new float[]{0,0};
    }

    /**
     * Retrieves and validates user input for seat selection.
     * @param showID Show ID for show that the user is selecting seats for.
     * @param seatSelection LinkedList to store the current seats selected.
     * @param autoPickSeats If True, automatic seat selection will take place before the user is taken to interactive seat selection.
     * @return True if valid seat selection.
     */
    public static boolean seatSelection(Venue venue, int showID, LinkedList<Seat> seatSelection, boolean autoPickSeats) {
        // Ensuring showID exists
        try {
            venue.getShow(showID);
        } catch (NoSuchElementException e) {
            return false;
        }
        // Stores user input
        String line;

        // Ask for number of tickets
        int numTickets = chooseNumberOfSeats(venue, showID);
        /* Get and display price range of seats (min and max price) */
        DecimalFormat df = new DecimalFormat("0.00"); // Decimal format used to truncate min and max prices
        List<Seat> seats = Arrays.asList(venue.getShow(showID).getSeats()); // All seats in show
        List<Float> seatPrices = seats.stream().map(Seat::getPrice).collect(Collectors.toList()); // Prices of all seats in show
        Float maxPrice = Collections.max(seatPrices);
        Float minPrice = Collections.min(seatPrices);
        System.out.printf("%nMinimum Seat Price: £%s%nMaximum Seat Price: £%s%n%n", df.format(minPrice), df.format(maxPrice));

        // Automatic Seat Selection (assuming front seats are best and back seats are the worst)
        if (autoPickSeats) {
            //Get user price range
            float[] priceRange = selectPriceRange();
            //Getting the best available seats in price range (lower ID is better and seat list is created from the lowest ID to highest)
            ArrayList<Integer> seatIDs = new ArrayList<>();
            for (Seat seat : venue.getShow(showID).getSeats()) {
                if (seat.getStatus() == Seat.SeatStatus.EMPTY && seat.getPrice() <= priceRange[1] && seat.getPrice() >= priceRange[0]) {
                    seatIDs.add(seat.getID());
                }
            }
            //Sorting available seatIDs in ascending order just in case
            Collections.sort(seatIDs);
            //Selecting best seats out of available seats (It is known );
            if (seatIDs.size() > numTickets) {
                for (int i = 0; i < numTickets; i++){
                    Seat bestSeat = venue.getShow(showID).getSeat(seatIDs.get(i));
                    seatSelection.add(bestSeat);
                    bestSeat.setHeld();
                }
            } else {
                System.out.println("No tickets filled your criteria.");
                return false;
            }
        }

        /* Interactive Seat selection */
        boolean acceptedSeatSelection = false;
        while (!acceptedSeatSelection) {
            // Show seats and wait for selection
            displaySeats(venue, showID);
            printChoices(true,"exit (e)", "accept selection (a)",String.format("Please select a seat you would like to book (In the format 'B3', 'A4', etc.)\n You have picked %d out of %d seats", seatSelection.size(), numTickets));
            String delimitedSeatsList = seatSelection.stream().map(Seat::getPos).collect(Collectors.joining(",")); // Seats chosen
            System.out.printf("Seats Selected: %s%n", delimitedSeatsList);
            line = input.nextLine();
            switch (line) {
                case "e":
                    //Set all held seats to empty and clear the selection
                    for (Seat seat : seatSelection) {
                        seat.setEmpty();
                    }
                    seatSelection.clear();
                    return false;
                case "a":
                    if (seatSelection.size() == numTickets) {
                        acceptedSeatSelection = true;
                    }
                    break;
                default:
                    try {
                        Seat chosen = venue.getShow(showID).getSeat(line);
                        // Check if taken
                        String chosenStatus = chosen.getStatus().toString();
                        boolean taken = chosenStatus.equals(Seat.SeatStatus.HELD.toString()) || chosenStatus.equals(Seat.SeatStatus.BOOKED.toString());
                        if (!taken) {
                            seatSelection.add(chosen);
                            chosen.setHeld();
                            venue.getShow(showID).getSeat(line);
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
        return true;
    }

    /**
     * Retrieves and validates user input for payment information.
     * @param showID Show ID for the show that the user is buying tickets for.
     * @param seatSelection Array List of seats that the user has currently selected.
     * @param currentUser Currently logged-in user.
     * @return True if valid payment information.
     */
    public static boolean paymentChoice(User currentUser, int showID, LinkedList<Seat> seatSelection) {
        // Display costs (with volume discounts 6+ tickets = 5% off all tickets)
        float discount = seatSelection.size() >= 6 ? 5.0f : 0.0f;
        float totalCost = 0;
        DecimalFormat df = new DecimalFormat("0.00");
        for (Seat seat : seatSelection) {
            float initialPrice = seat.getPrice();
            float discountPrice = (1-(discount/100))*initialPrice;
            System.out.printf("Seat: %s\tInitial Price: £%s\tDiscount: %s%%\tPrice: £%s\n", seat.getPos(), df.format(initialPrice), df.format(discount), df.format(discountPrice));
            totalCost += discountPrice;
        }
        System.out.printf("Total cost: £%s\n", df.format(totalCost));

        // Get card details
        int stage = 0;
        String cardNumber = ""; // Stores card number (never used as all card numbers with valid format are accepted)
        String securityNumber = ""; // Stores security number (never used as all security numbers with valid format are accepted)
        while (stage < 2) {
            printChoices(true,"Exit (e)", String.format("Enter %s:", stage == 0 ? "Card Number (with format XXXX-XXXX-XXXX-XXXX)" : "Security Number"));
            String line = input.nextLine();
            if (line.equals("e")) {
                return false;
            } else {
                if (stage == 0) {
                    if (line.matches("^(\\d{4}[-\\s]){3}\\d{4}$")) {
                        cardNumber = line;
                        stage += 1;
                    }
                } else {
                    if (line.matches("^\\d{3}$")) {
                        securityNumber = line;
                        stage += 1;
                    }
                }
            }
        }
        if (currentUser.getAccountType() == User.AccountType.CUSTOMER) {
            String[] bookedSeats = new String[seatSelection.size()];
            for (int i = 0; i < seatSelection.size(); i++) {
                Seat seat = seatSelection.get(i);
                seat.setBooked();
                bookedSeats[i] = seat.getPos();
            }
            Customer customer = (Customer)currentUser;
            customer.addBooking(showID, bookedSeats);
            seatSelection.clear();
            System.out.println("Booking successful!");
        }
        return true;
    }

    /**
     * Retrieves and validates user input for show that a user wishes to cancel, and cancels the show if a valid choice is chosen.
     * @param user User that is cancelling a show.
     */
    public static void cancelShowChoice(User user, Venue venue) {
        /* Validate that user is a customer */
        if (!(user.getAccountType() == User.AccountType.CUSTOMER)) {
            return;
        }
        /* Display customer's bookings */
        printChoices(false, "Exit (any character)", "Please select a booking to cancel. Bookings: ");
        Customer customer = (Customer)user;
        ArrayList<Booking> bookings = customer.getBookings();
        for (int i = 0; i < bookings.size(); i++) {
            Booking booking = bookings.get(i);
            Show bookedShow = venue.getShow(booking.getShowID());
            System.out.printf("(%d) %s%n", (i+1), bookedShow.getName());
            System.out.printf("\tTime: %s%n", bookedShow.getTime().getTime());
            System.out.printf("\tNo. of Seats: %d%n", booking.getSeats().length);
            System.out.printf("\tSeats: %s%n", String.join(",", booking.getSeats()));
        }

        /* Cancel booking if valid integer and then return */
        if (input.hasNextInt()) {
            int line = Integer.parseInt(input.nextLine());
            if (line <= bookings.size() && line > 0) {
                customer.cancelBooking(bookings.get(line - 1).getID());
            }
            return;
        }
        input.nextLine(); //If it is not an integer, the scanner will read and dispose of the next line
    }

    /**
     * Retrieves and validates user input for selecting an option to manage the shows.
     * @return Choice made by user (delete show 'd' = 0, add show 'a' = 1, reschedule show 'r' = 2, exit 'e' = -1, invalid choice = -1).
     */
    public static int manageShows() {
        printChoices(true,"Delete Show(d)", "Add Show(a)", "Reschedule Show(r)", "Exit (e)");
        String line = input.nextLine(); // Get user input
        switch (line) {
            case "d":
                return 0;
            case "a":
                return 1;
            case "r":
                return 2;
            case "e":
                return -1;
        }
        return -1; // Invalid choice
    }

    public static void removeShow() {
    }

    /**
     * Creates a show for a specified venue with validated user input.
     * @param venue Venue to create show for.
     * @return Show object with user inputted and validated values.
     * @throws CancellationException If exit ('e') is inputted.
     */
    public static Show createShow(Venue venue) throws CancellationException {
        // Array of the strings used to request the account requirements from the user
        String[] showRequirements = new String[]{
                "Show Name",
                "Date and Time of the show (in the format 'dd/MM/yyyy 00:00' where '00:00' is 24 hour time format)"
        };
        // Variables to store the validated user inputted show details
        String showName = "Unnamed Show";
        Calendar showTime = Calendar.getInstance();
        /* Getting and validating user input */
        int stage = 0;
        String requirement;
        boolean validEntry;
        while (stage < showRequirements.length) {
            validEntry = false;
            requirement = showRequirements[stage]; // Current stage's requirement string
            printChoices(false, "Exit (e)", String.format("Enter the %s:", requirement));
            String line = input.nextLine(); // Get user input
            if (line.equals("e")) {
                throw new CancellationException("Exit ('e') was inputted by the user."); // Return false if exit 'e' is inputted
            } else {
                switch (stage) {
                    case 0: // Show Name
                        validEntry = true; // Show name is always valid (could be literally any string)
                        showName = line; // Set show name
                        break;
                    case 1: // Date and Time of Show
                        String[] splitLine = line.split("\\s");
                        if (splitLine.length != 2) { break; } // Invalid number of entries
                        if (!validFutureDate(splitLine[0])) { break; } // Invalid future date
                        if (!splitLine[1].matches("^([01][0-9]|2[0-3]):([0-5][0-9])$")) { break; } // Invalid time format
                        validEntry = true;
                        /* Setting show time */
                        int[] splitDate = Arrays.stream(splitLine[0].split("/")).mapToInt(Integer::parseInt).toArray(); // Get array of date, month, and year
                        int[] splitTime = Arrays.stream(splitLine[1].split(":")).mapToInt(Integer::parseInt).toArray(); // Get array of hour and minute
                        showTime.set(splitDate[2], splitDate[1]-1, splitDate[0], splitTime[0], splitTime[1]); // Set show time
                        showTime.set(Calendar.SECOND, 0);
                        break;
                }
                // If valid entry, increase stage, otherwise display validation error
                if (validEntry) {
                    stage += 1;
                } else {
                    System.out.printf("Please enter a valid %s%n", showRequirements[stage]);
                }
            }
        }
        /* Create and return show */
        return new Show(showName, showTime, venue.getNumRows(), venue.getNumCols());
    }

    public static void rescheduleShow() {
    }
}
