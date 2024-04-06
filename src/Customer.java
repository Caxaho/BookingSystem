import java.util.ArrayList;
import java.util.Date;
import java.util.NoSuchElementException;

public class Customer extends User{
    private Date dob;
    private String homeAddress;
    private ArrayList<Booking> bookings = new ArrayList<Booking>();

    public Customer(String name, String username, String emailAddress, String password, Date dob, String homeAddress) {
        super(name, username, emailAddress, password);
        this.dob = dob;
        this.homeAddress = homeAddress;
        this.setAccountType(AccountType.CUSTOMER);
    }

    public Date getDOB() { return dob; }

    public String getHome() { return homeAddress; }

    public void setHome(String homeAddress) { this.homeAddress = homeAddress; }

    public void addBooking(int showID, String[] seats) { bookings.add(new Booking(showID, seats)); }

    public ArrayList<Booking> getBookings() { return bookings; }

    public void cancelBooking(int bookingID) {
        if (!bookings.removeIf(booking -> booking.getID() == bookingID)) {
            throw new NoSuchElementException("The 'bookingID' requested does not exist");
        }
    }
}
