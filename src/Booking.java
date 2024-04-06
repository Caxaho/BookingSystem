public class Booking {
    private final int bookingID;
    private static int bookingCount;
    private final int showID;
    private final String[] seats;

    public Booking(int showID, String[] seats) {
        this.showID = showID;
        this.seats = seats;
        bookingCount += 1;
        bookingID = bookingCount;
    }

    public int getID() { return bookingID; }

    public int getShowID() { return showID; }

    public String[] getSeats() { return seats; }
}
