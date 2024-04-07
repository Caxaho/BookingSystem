import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

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
                }
                throw new IllegalArgumentException("choice out of bounds");
            }

            @Override
            public ProgramState previousState() {
                return EXIT;
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
                switch (choice) {
                    case 0:
                        return PAYMENT;
                    case 1:
                        return INTERACTIVE_SELECTION;
                }
                throw new IllegalArgumentException("choice out of bounds");
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
    static ArrayList<Customer> customers = new ArrayList<Customer>();
    static ArrayList<Admin> admins = new ArrayList<Admin>();
    static ArrayList<Agent> agents = new ArrayList<Agent>();
    static ArrayList<VenueManager> managers = new ArrayList<VenueManager>();

    static Scanner input = new Scanner(System.in);
    public static void main(String[] args) {
        boolean exit = false; //Boolean for main loop control
        int choice; //Holds choice for each state's function return
        while (!exit) {
            switch (state) {
                case START:
                    try {
                        choice = StartChoice();
                        state = choice < 2 ? state.nextState(choice) : state.previousState();
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid input, please try again!");

                    }
                    break;
                case REQUEST_LOGIN:
                    break;
                case REGISTRATION:
                    break;
                default:
                    exit = true;
            }
        }
        System.out.println(exit);
        System.out.println(state);
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
            try {
                SimpleDateFormat inputDOB = new SimpleDateFormat(dob); // Created to validate the date
                // Validating age
                Calendar inputCal = inputDOB.getCalendar(); // Convert input date to Calendar object
                Calendar cal = Calendar.getInstance(); // Get current date
                cal.set(cal.get(Calendar.YEAR)-MIN_REGISTRATION_AGE, cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
                if (inputCal.before(cal)) {
                    return true; // Valid date
                }
            } catch (IllegalArgumentException ignored) {
            }
        }
        return false; // Returns false if 'dob' given was of the wrong format, not a real date, or below the minimum age requirement
    }

    public static boolean ValidUsername(String username) {
        return username.matches("^[a-z0-9_-]+$");
    }

    public static boolean ValidPass(String pass) {
        return pass.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
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
                        if (line.matches("^[a-z ,.'-]+$")) {
                            accountDetails[stage] = line;
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
                            System.out.println("Please enter a valid email address.");
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
                        } else {
                            System.out.println("Please enter a valid password. It must contain a minimum of eight characters, at least one uppercase letter, one lowercase letter, one number, and one special character.");
                        }
                        break;
                }
            }
        }

        // Return true if successfully added to ArrayList, false otherwise
        return customers.add(new Customer(
                accountDetails[0], // Name
                accountDetails[1], // Username
                accountDetails[2], // Email Address
                accountDetails[3], // Mobile Number
                accountDetails[6], // Password
                new SimpleDateFormat(accountDetails[4]), // DOB
                accountDetails[5])); // Home Address
    }
}