package com.BookingClient;

public class Contract {
    private final float commission;
    private final int showID;
    private final int[] seats;

    public Contract(float commission, int showID, int[] seats) {
        this.commission = commission;
        this.showID = showID;
        this.seats = seats;
    }

    public float getCommission() { return commission; }

    public int getShowID() { return showID; }

    public int[] getSeats() { return seats; }
}
