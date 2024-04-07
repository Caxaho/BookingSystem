public class Booking {
    private final int bookingID;
    private static int bookingCount;
    private final int showID;
    private final String[] seats;

    public Booking(int showID, String[] seats) {
        this.showID = showID;
        this.seats = seats;
        bookingID = bookingCount;
        bookingCount += 1;
    }

    public int getID() { return bookingID; }

    public int getShowID() { return showID; }

    public String[] getSeats() { return seats; }
}
