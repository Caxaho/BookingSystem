import java.util.ArrayList;
import java.util.Date;
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

    public void addShow(String name, Date time) { shows.add(new Show(name, time, numRows, numCols)); }

    public void cancelShow(int showID) { shows.removeIf(show -> show.getID() == showID); }

    public Show getShow(int showID) {
        for (Show show : shows) {
            if (show.getID() == showID) {
                return show;
            }
        }
        throw new NoSuchElementException("The 'showID' requested does not exist");
    }

    public ArrayList<Show> getShows() { return shows; }

    public void addPromotion(Promotion promotion) { promotions.add(promotion); }

    public void removePromotion(int promotionID) { promotions.removeIf(promotion -> promotion.getID() == promotionID); }

    public Promotion getPromotion(int promotionID) {
        for (Promotion promotion : promotions) {
            if (promotion.getID() == promotionID) {
                return promotion;
            }
        }
        throw new NoSuchElementException("The 'promotionID' requested does not exist");
    }
}
