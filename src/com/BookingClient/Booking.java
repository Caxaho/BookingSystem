package com.BookingClient;

public class Booking {
    private final int bookingID;
    private static int bookingCount; // Used to automatically increment booking IDs across all bookings
    private final int showID; // Show which the booking pertains to
    private final String[] seats; // Seat names included in booking

    /**
     * Booking constructor
     * @param showID Show ID which the booking pertains to.
     * @param seats Seats included in the booking (names i.e. 'A4', or 'B5' etc.)
     */
    public Booking(int showID, String[] seats) {
        this.showID = showID;
        this.seats = seats;
        bookingID = bookingCount;
        bookingCount += 1;
    }

    /**
     * Get booking ID.
     * @return Booking ID.
     */
    public int getID() { return bookingID; }

    /**
     * Get show ID.
     * @return Show ID of booking.
     */
    public int getShowID() { return showID; }

    /**
     * Get seats.
     * @return Seats of booking (names i.e. 'A4', or' B5' etc.).
     */
    public String[] getSeats() { return seats; }
}
