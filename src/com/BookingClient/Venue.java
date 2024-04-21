package com.BookingClient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.NoSuchElementException;

public class Venue {
    private final String name;
    private final int numRows;
    private final int numCols;
    private final ArrayList<Show> shows = new ArrayList<Show>();
    private final ArrayList<Promotion> promotions = new ArrayList<Promotion>();

    public Venue(String name, int numRows, int numCols) {
        this.name = name;
        this.numRows = numRows;
        this.numCols = numCols;
    }

    public String getName() { return name; }

    public int getNumRows() { return numRows; }

    public int getNumCols() { return numCols; }

    public int getNumSeats() { return numCols*numRows; }

    public void addShow(String name, Calendar time) { shows.add(new Show(name, time, numRows, numCols)); }

    public void addShow(Show show) { shows.add(show); }

    /**
     * Cancel show based on given show ID.
     * @param showID Given show ID.
     */
    public void cancelShow(int showID) {
        if (!shows.removeIf(show -> show.getID() == showID)) {
            throw new NoSuchElementException("The 'showID' does not exist");
        }
    }

    /**
     * Get show based on given show ID.
     * @param showID Given show ID.
     * @return Show, if found.
     * @throws NoSuchElementException If show not found.
     */
    public Show getShow(int showID) throws NoSuchElementException {
        for (Show show : shows) {
            if (show.getID() == showID) {
                return show;
            }
        }
        throw new NoSuchElementException("The 'showID' requested does not exist");
    }

    public ArrayList<Show> getShows() { return shows; }

    public void addPromotion(Promotion promotion) { promotions.add(promotion); }

    /**
     * Delete promotion using given promotion ID
     * @param promotionID Given promotion ID.
     * @throws NoSuchElementException If promotion attempting to be deleted does not exist.
     */
    public void removePromotion(int promotionID) throws NoSuchElementException {
        if (!promotions.removeIf(promotion -> promotion.getID() == promotionID)) {
            throw new NoSuchElementException("The 'promotionID' does not exist");
        }
    }

    /**
     * Get promotion based on given promotion ID.
     * @param promotionID Given promotion ID.
     * @return Promotion, if found.
     * @throws NoSuchElementException If promotion not found.
     */
    public Promotion getPromotion(int promotionID) throws NoSuchElementException {
        for (Promotion promotion : promotions) {
            if (promotion.getID() == promotionID) {
                return promotion;
            }
        }
        throw new NoSuchElementException("The 'promotionID' requested does not exist");
    }

    /**
     * Get all promotions.
     * @return All promotions.
     */
    public ArrayList<Promotion> getPromotions() { return promotions; }
}
