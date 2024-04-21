package com.BookingClient;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Promotion {
    private String name;
    private final int promotionID;
    private static int promotionCount;
    private final float[] priceModifiers;
    private final int[][] seatRanges;
    private final List<int[]> seatRangesFull;

    /**
     * Instantiate Promotion class with immutable price modifiers and seat ranges which must match in length.
     * @param name Name of promotion.
     * @param priceModifiers float array of percentage modifiers of the original seat price.
     * @param seatRanges 2D int array with ranges of seat IDs to apply the price modifiers to.
     * @throws IllegalArgumentException If priceModifiers and seatRanges do not match in length,
     * the seat range arrays do not have a valid length,
     * the IDs of the seat ranges is high to low instead of low to high,
     * or seatRanges contains duplicates/crossover seat IDs.
     */
    public Promotion(String name, float[] priceModifiers, int[][] seatRanges) throws IllegalArgumentException {
        /* Validate that the number of price modifiers (tiers) is the same as the number of seat ranges provided */
        if (priceModifiers.length != seatRanges.length) {
            throw new IllegalArgumentException("PriceModifiers array length does not match seatRanges array length");
        }
        /* Validate the seat ranges have valid length */
        List<Integer> lengthOfRanges = Arrays.stream(seatRanges).map(i -> i.length).collect(Collectors.toList()); // Get lengths of ranges
        for (Integer i : lengthOfRanges) {
            if (i!=2) { throw new IllegalArgumentException("A seat range array provided is not a valid length"); }
        }
        /* Validate the seat ranges to be low to high */
        if (Arrays.stream(seatRanges).anyMatch(ints -> ints[0] > ints[1])) {
            throw new IllegalArgumentException("A seat range array provided is ordered incorrectly (high to low)");
        }
        /* Validate the seat ranges to have no duplicated ranges/crossovers */
        // Create list of int arrays which contain all the seat IDs in each range
        List<int[]> validRangesJoined = Arrays.stream(seatRanges).map(i -> IntStream.range(i[0], i[1]+1).toArray()).collect(Collectors.toList());
        // Flatten above List to get all seats in all ranges
        int[] flattenedRangesArray = validRangesJoined.stream().flatMapToInt(Arrays::stream).toArray();
        // Check for duplicates
        if (Arrays.stream(flattenedRangesArray).distinct().count() != flattenedRangesArray.length) {
            throw new IllegalArgumentException("seatRanges contains duplicates/crossovers");
        }
        /* Initialize class variables */
        this.name = name;
        this.priceModifiers = priceModifiers;
        this.seatRanges = seatRanges;
        this.seatRangesFull = validRangesJoined;
        this.promotionID = promotionCount;
        promotionCount += 1;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public int getID() { return promotionID; }

    public float[] getPriceModifiers() { return priceModifiers; }

    public List<int[]> getFullSeatRanges() { return seatRangesFull; }
}
