import java.util.Date;
import java.util.NoSuchElementException;

public class Show {
    private String name;
    private final int showID;
    private static int showCount;
    private int minAge;
    private Date time;
    private final Seat[] seats;
    private Promotion promotion;
    private float minSeatPrice = 10.0f;
    private int maxSeatsPerUser = 50;

    public Show(String name, Date time, int numRows, int numCols) {
        /* Initializing Variables */
        this.name = name;
        this.time = time;
        showCount += 1;
        showID = showCount;
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
                int count = i*j + j;
                String seatName = String.format("%s%s", rowNames[i], j);
                seats[count] = new Seat(count, seatName, minSeatPrice);
            }
        }
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public int getID() { return showID; }

    public int getMinAge() { return minAge; }

    public void setMinAge(int age) { this.minAge = age; }

    public Date getTime() { return time; }

    public void setTime(Date time) { this.time = time; }

    public float getMinSeatPrice() { return minSeatPrice; }

    public void setMinSeatPrice(float price) { this.minSeatPrice = price; }

    public int getMaxSeatsPerUser() { return maxSeatsPerUser; }

    public void setMaxSeatsPerUser(int maxSeatsPerUser) { this.maxSeatsPerUser = maxSeatsPerUser; }

    public Promotion getPromotion() { return promotion; }

    public void setPromotion(Promotion promotion) { this.promotion = promotion; }

    public Seat getSeat(int seatID) {
        for (Seat seat : seats) {
            if (seat.getID() == seatID) {
                return seat;
            }
        }
        throw new NoSuchElementException("The 'seatID' requested does not exist");
    }

    public Seat getSeat(String seatName) {
        for (Seat seat : seats) {
            if (seatName.equals(seat.getPos())) {
                return seat;
            }
        }
        throw new NoSuchElementException("The 'seatID' requested does not exist");
    }
}