package com.BookingClient;

import java.text.ParseException;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.stream.Collectors;

public class Main {
    static int MIN_REGISTRATION_AGE = 12;

    public enum ProgramState {
        START {
            @Override
            public ProgramState nextState(int choice) throws IllegalArgumentException {
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
            public ProgramState nextState(int choice) throws IllegalArgumentException {
                switch (choice) {
                    case 0:
                        return SELECT_SHOW;
                    case 1:
                        return CANCEL_SHOW;
                    case 2:
                        return MANAGE_SHOWS;
                    case 3:
                        return MANAGE_PROMOTIONS;
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
            public ProgramState nextState(int choice) throws IllegalArgumentException {
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
                return LOGGED_IN;
            }
        },
        INTERACTIVE_SELECTION {
            @Override
            public ProgramState nextState(int choice) {
                return PAYMENT;
            }

            @Override
            public ProgramState previousState() {
                return LOGGED_IN;
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
        MANAGE_SHOWS {
            @Override
            public ProgramState nextState(int choice) throws IllegalArgumentException {
                switch (choice) {
                    case 0:
                        return REMOVE_SHOW;
                    case 1:
                        return ADD_SHOW;
                    case 2:
                        return RESCHEDULE_SHOW;
                }
                throw new IllegalArgumentException("choice out of bounds");
            }

            @Override
            public ProgramState previousState() {
                return LOGGED_IN;
            }
        },
        REMOVE_SHOW {
            @Override
            public ProgramState nextState(int choice) {
                return LOGGED_IN;
            }

            @Override
            public ProgramState previousState() {
                return LOGGED_IN;
            }
        },
        ADD_SHOW {
            @Override
            public ProgramState nextState(int choice) {
                return LOGGED_IN;
            }

            @Override
            public ProgramState previousState() {
                return LOGGED_IN;
            }
        },
        RESCHEDULE_SHOW {
            @Override
            public ProgramState nextState(int choice) {
                return LOGGED_IN;
            }

            @Override
            public ProgramState previousState() {
                return LOGGED_IN;
            }
        },
        MANAGE_PROMOTIONS {
            @Override
            public ProgramState nextState(int choice) throws IllegalArgumentException {
                switch (choice) {
                    case 0:
                        return APPLY_PROMOTION;
                    case 1:
                        return REMOVE_PROMOTION;
                    case 2:
                        return CREATE_PROMOTION;
                }
                throw new IllegalArgumentException("choice out of bounds");
            }

            @Override
            public ProgramState previousState() {
                return LOGGED_IN;
            }
        },
        APPLY_PROMOTION {
            @Override
            public ProgramState nextState(int choice) {
                return LOGGED_IN;
            }

            @Override
            public ProgramState previousState() {
                return LOGGED_IN;
            }
        },
        REMOVE_PROMOTION {
            @Override
            public ProgramState nextState(int choice) {
                return LOGGED_IN;
            }

            @Override
            public ProgramState previousState() {
                return LOGGED_IN;
            }
        },
        CREATE_PROMOTION {
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
            public ProgramState nextState(int choice) throws UnsupportedOperationException {
                throw new UnsupportedOperationException("No 'nextState' for 'EXIT' state");
            }

            @Override
            public ProgramState previousState() throws UnsupportedOperationException {
                throw new UnsupportedOperationException("No 'nextState' for 'EXIT' state");
            }
        };
        public abstract ProgramState nextState(int choice) throws IllegalArgumentException;
        public abstract ProgramState previousState();
    }

    /* Important Variable Initialization */
    static ProgramState state = ProgramState.START; // State machine instance initialised to 'START' state
    static Venue bcpa = new Venue("Bucks Centre for the Performing Arts (BCPA)", 20, 27); // Single venue since it never changes
    static User currentUser; //Current logged in user
    static ArrayList<User> users = new ArrayList<>(); //Stores all the users, this would usually be in a database.
    static int currentShowSelectedID = -1; // Current showID selected by user
    static LinkedList<Seat> currentUserSeatsSelected = new LinkedList<>(); // Create LinkedList of current seats chosen (FIFO)

    public static void main(String[] args) {
        AddDefaults(bcpa, users); // Adds default users and shows.



        boolean exit = false; // Boolean for main loop control
        int choice; // Holds choice for each state's function return
        // Main Loop
        while (!exit) {
            // Checking state and acting accordingly
            switch (state) {
                case START:
                    choice = CLI.start(); // Retrieve next state choice
                    state = choice >= 0 ? state.nextState(choice) : state.previousState(); // Move states according to choice
                    break;
                case REQUEST_LOGIN:
                    try {
                        currentUser = CLI.loginChoice(users); // Attempt login and retrieve user on success
                        state = state.nextState(0); // Next state
                    } catch (CancellationException | IllegalArgumentException e) {
                        System.out.println("Failed login. Exiting...");
                        state = state.previousState(); // Failed login, return to previous state
                    }
                    break;
                case REGISTRATION:
                    try {
                        User newUser = CLI.registration(MIN_REGISTRATION_AGE); // Attempt to create new user, retrieve new user on success
                        /* Check for duplicate user */
                        List<String> usernames = users.stream().map(User::getUsername).collect(Collectors.toList()); // Get usernames of all users
                        List<String> emails = users.stream().map(User::getEmail).collect(Collectors.toList()); // Get email addresses of all users
                        if (!(usernames.contains(newUser.getUsername()) || emails.contains(newUser.getEmail()))) {
                            users.add(newUser); // Create new user
                            currentUser = newUser; // Set current user to newly registered user
                            state = state.nextState(0); // Next state
                        } else {
                            System.out.println("Account creation unsuccessful. You cannot create accounts with duplicate email addresses or usernames!");
                            state = state.previousState(); // Duplicate account creation attempt, return to previous state
                        }
                    } catch (CancellationException | IllegalArgumentException e) {
                        System.out.println("Account creation unsuccessful. Exiting...");
                        state = state.previousState(); // Unsuccessful account creation, return to previous state
                    }
                    break;
                case LOGGED_IN:
                    try {
                        choice = CLI.loggedInChoice(currentUser); // Retrieve next state choice
                        state = choice >= 0 ? state.nextState(choice) : state.previousState(); // Move states according to choice
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid input, please try again!");
                    }
                    break;
                case SELECT_SHOW:
                    try {
                        currentShowSelectedID = CLI.selectShow(bcpa, true); // Retrieve show ID from user's selection
                    } catch (ParseException | IllegalArgumentException e) {
                        System.out.println("An error occurred when parsing user input, returning to previous state.");
                        currentShowSelectedID = -1; // Setting selected Show ID to an invalid ID to force previous state.
                    }
                    // Verifying that a show ID was returned
                    if (currentShowSelectedID >= 0) {
                        choice = CLI.selectSeatingTypeChoice(); // Retrieve seating choice (Automatic or Interactive)
                    } else {
                        choice = currentShowSelectedID; // When choice<0, it will return to the previous state
                    }
                    state = choice >= 0 ? state.nextState(choice) : state.previousState(); // Move states according to choice
                    break;
                case AUTOMATIC_SELECTION:
                    // Move states depending on if seat selection was successful
                    state = CLI.seatSelection(bcpa, currentShowSelectedID, currentUserSeatsSelected, true) ? state.nextState(0) : state.previousState();
                    break;
                case INTERACTIVE_SELECTION:
                    // Move states depending on if seat selection was successful
                    state = CLI.seatSelection(bcpa, currentShowSelectedID, currentUserSeatsSelected, false) ? state.nextState(0) : state.previousState();
                    break;
                case PAYMENT:
                    // Move state depending on if payment was successful
                    state = CLI.paymentChoice(currentUser, currentShowSelectedID, currentUserSeatsSelected) ? state.nextState(0) : state.previousState();
                    break;
                case CANCEL_SHOW:
                    CLI.cancelShowChoice(currentUser, bcpa); // Attempt to cancel a show
                    state = state.nextState(0); // Move to next state
                    break;
                case MANAGE_SHOWS:
                    try {
                        choice = CLI.manageShows(); // Retrieve next state choice
                        state = choice >= 0 ? state.nextState(choice) : state.previousState(); // Move states according to choice
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid input, please try again!");
                    }
                    break;
                case REMOVE_SHOW:
                    try {
                        CLI.removeShow(bcpa); // Attempt to remove show
                    } catch (RuntimeException ignored) {} // Ignored because will always move to next state
                    System.out.println("Exiting...");
                    state = state.nextState(0); // Move to next state
                    break;
                case ADD_SHOW:
                    try {
                        bcpa.addShow(CLI.createShow(bcpa));
                    } catch (CancellationException ignored) {} // Ignored because will always move to next state
                    System.out.println("Exiting...");
                    state = state.nextState(0); // Move to next state
                    break;
                case RESCHEDULE_SHOW:
                    try {
                        CLI.rescheduleShow(bcpa);
                    } catch (RuntimeException ignored) {} // Ignored because will always move to next state
                    System.out.println("Exiting...");
                    state = state.nextState(0);// Move to next state
                    break;
                case MANAGE_PROMOTIONS:
                    try {
                        choice = CLI.managePromotions(); // Retrieve next state choice
                        state = choice >= 0 ? state.nextState(choice) : state.previousState(); // Move states according to choice
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid input, please try again!");
                    }
                    break;
                case APPLY_PROMOTION:
                    try {
                        CLI.applyPromotion(bcpa); // Attempt to apply promotion
                    } catch (RuntimeException ignored) {} // Ignored because will always move to next state
                    System.out.println("Exiting...");
                    state = state.nextState(0); // Move to next state
                    break;
                case CREATE_PROMOTION:
                    CLI.createPromotion(bcpa); // Attempt to create promotion
                    System.out.println("Exiting...");
                    state = state.nextState(0); // Move to next state
                    break;
                case REMOVE_PROMOTION:
                    try {
                        CLI.removePromotion(bcpa); // Attempt to remove promotion
                    } catch (RuntimeException ignored) {} // Ignored because will always move to next state
                    System.out.println("Exiting...");
                    state = state.nextState(0); // Move to next state
                    break;
                default:
                    exit = true;
            }
        }
    }

    /**
     * Adds example/default shows and users.
     * @param venue Venue to add the shows to.
     * @param userList User list to add the users to.
     */
    private static void AddDefaults(Venue venue, ArrayList<User> userList) {
        /* Creating Default Users and Shows */
        // Default Users for Testing (Customer, VenueManager, Agent, and Admin)
        userList.add(new Customer("wef","wef","wef@wef.com", "07259622506", "wefwef", "04/07/2001", "1 Normal Place, Somewhere, SW26 6EB"));
        userList.add(new VenueManager("Steve Venue", "venue_steve", "vsteve@bcpa.com", "VenueSteve25%"));
        userList.add(new Agent("Michael Agent", "agent_michael", "agent@external.com", "AgentMichael25%"));
        userList.add(new Admin("Xavier Admin", "admin_xavier", "admin@bcpa.com", "AdminXavier25%"));
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
        // Default promotions
        bcpa.addPromotion(new Promotion("Test Promotion (seats A1 to A10 half price)", new float[]{0.5f}, new int[][]{{0,9}}));
    }
}

