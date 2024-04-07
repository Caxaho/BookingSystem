import java.util.ArrayList;
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
