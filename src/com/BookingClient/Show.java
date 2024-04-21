package com.BookingClient;

import java.util.Calendar;
import java.util.List;
import java.util.NoSuchElementException;

public class Show {
    private String name;
    private final int showID;
    private static int showCount;
    private int minAge;
    private Calendar time;
    private final Seat[] seats;
    private static final Promotion defaultPromotion = new Promotion("Default", new float[]{1f}, new int[][]{{0,Integer.MAX_VALUE}});
    private Promotion promotion = defaultPromotion;
    private float defaultSeatPrice = 10.0f;
    private int maxSeatsPerUser = 50;

    public Show(String name, Calendar time, int numRows, int numCols) {
        /* Initializing Variables */
        this.name = name;
        this.time = time;
        showID = showCount;
        showCount += 1;
        // Creating and initializing array with letter names of each row
        String[] rowNames = new String[numCols];
        for (int i = 1; i <= numCols; i++) {
            int input = i;
            StringBuilder output = new StringBuilder();
            while (input > 0) {
                int num = (input - 1) % 26;
                char letter = (char)(num+65);
                output.insert(0, letter);
                input = (input-1) / 26;
            }
            rowNames[i-1] = output.toString();
        }
        // Initializing all the seats
        seats = new Seat[numRows*numCols];
        for (int i = 0; i < numCols; i++) {
            for (int j = 0; j < numRows; j++) {
                int count = i*numRows + j;
                String seatName = String.format("%s%s", rowNames[i], j+1);
                seats[count] = new Seat(count, seatName, defaultSeatPrice);
            }
        }
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public int getID() { return showID; }

    public int getMinAge() { return minAge; }

    public void setMinAge(int age) { this.minAge = age; }

    public Calendar getTime() { return time; }

    public void setTime(Calendar time) { this.time = time; }

    public float getDefaultSeatPrice() { return defaultSeatPrice; }

    public void setDefaultSeatPrice(float price) {
        this.defaultSeatPrice = price;
        calculateSeatPrices();
    }

    public int getMaxSeatsPerUser() { return maxSeatsPerUser; }

    public void setMaxSeatsPerUser(int maxSeatsPerUser) { this.maxSeatsPerUser = maxSeatsPerUser; }

    public Promotion getPromotion() { return promotion; }

    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
        calculateSeatPrices(); // Recalculate seat prices.
    }

    public void removePromotion() {
        this.promotion = defaultPromotion;
    }

    public Seat getSeat(int seatID) throws NoSuchElementException {
        for (Seat seat : seats) {
            if (seat.getID() == seatID) {
                return seat;
            }
        }
        throw new NoSuchElementException("The 'seatID' requested does not exist");
    }

    public Seat getSeat(String seatName) throws NoSuchElementException {
        for (Seat seat : seats) {
            if (seatName.equals(seat.getPos())) {
                return seat;
            }
        }
        throw new NoSuchElementException("The 'seatID' requested does not exist");
    }

    public Seat[] getSeats() {
        return seats;
    }

    /**
     * Calculates and sets the seat prices according to the promotion applied and default seat price.
     */
    private void calculateSeatPrices() {
        /* Set all seats to default price */
        for (Seat seat : seats) {
            seat.setPrice(defaultSeatPrice);
        }
        /* Set prices for seats in promotion */
        List<int[]> seatRanges = promotion.getFullSeatRanges();
        float[] priceModifiers = promotion.getPriceModifiers();
        for (int i = 0; i < seatRanges.size(); i++) {
            int[] range = seatRanges.get(i);
            for (int j = 0; j < range.length && j < seats.length; j++) {
                seats[j].setPrice(defaultSeatPrice*priceModifiers[i]);
            }
        }
    }
}