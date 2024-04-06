public class Promotion {
    private String name;
    private final int promotionID;
    private static int promotionCount;
    private final int[] prices;

    public Promotion(String name, int[] prices) {
        this.name = name;
        this.prices = prices;
        promotionCount += 1;
        promotionID = promotionCount;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public int getID() { return promotionID; }

    public int[] getPrices() { return prices; }
}
