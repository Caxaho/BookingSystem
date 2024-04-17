package com.BookingClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.NoSuchElementException;

public class Customer extends User{
    private String mobileNo;
    private String dob;
    private String homeAddress;
    private ArrayList<Booking> bookings = new ArrayList<Booking>();

    public Customer(String name, String username, String emailAddress, String mobileNo, String password, String dob, String homeAddress) {
        super(name, username, emailAddress, password);
        this.mobileNo = mobileNo;
        this.dob = dob;
        this.homeAddress = homeAddress;
        this.setAccountType(AccountType.CUSTOMER);
    }

    public String getDOB() { return dob; }

    /**
     * Validates a date of birth String.
     * @param dob Date of birth as String.
     * @return True if the 'dob' parameter is a valid date of birth and is at least 12 years ago. False otherwise.
     */
    public static boolean validDateOfBirth(String dob, int minRegistrationAge) {
        // Validating DOB structure with regex and parsing it with the SimpleDateFormat class
        if (dob.matches("^\\d{1,2}/\\d{1,2}/\\d{4}$")) {
            SimpleDateFormat inputDOB = new SimpleDateFormat("dd/MM/yyyy"); // Created to validate the date
            inputDOB.setLenient(false); // Setting the SimpleDateFormat to be strict
            try {
                inputDOB.parse(dob); // Parsing date (ParseException raised if invalid/impossible date)
                /* Validating age (at least 12 years old) */
                Calendar inputCal = inputDOB.getCalendar(); // Convert input date to Calendar object
                Calendar cal = Calendar.getInstance(); // Get current date
                cal.set(cal.get(Calendar.YEAR)-minRegistrationAge, cal.get(Calendar.MONTH), cal.get(Calendar.DATE)); // Take 12 years off the current date
                if (inputCal.before(cal)) {
                    return true; // Valid date if input date is at least 12 years ago, and a valid format
                }
            } catch (ParseException ignored) {
                // Catch ParseException and ignore, as it will return false at the end of the method anyway
            }
        }
        return false; // Returns false if 'dob' given was of the wrong format, not a real date, or below the minimum age requirement
    }

    public String getHome() { return homeAddress; }

    public void setHome(String homeAddress) { this.homeAddress = homeAddress; }

    /**
     * Validates home address with regex.
     * @param homeAddress Home address to validate.
     * @return True if valid home address.
     */
    public static boolean validHomeAddress(String homeAddress) {
        return homeAddress.matches("^\\d+[a-zA-Z\\s]+,[a-zA-Z\\s]+,\\s*[a-zA-Z]{1,2}\\d{1,2}\\s\\d[a-zA-Z]{1,2}$");
    }

    public void addBooking(int showID, String[] seats) { bookings.add(new Booking(showID, seats)); }

    public ArrayList<Booking> getBookings() { return bookings; }

    public void cancelBooking(int bookingID) throws NoSuchElementException {
        if (!bookings.removeIf(booking -> booking.getID() == bookingID)) {
            throw new NoSuchElementException("The 'bookingID' requested does not exist");
        }
    }
}
