public class Seat {
    public enum SeatStatus {
        EMPTY,
        HELD,
        BOOKED
    }

    private final String position;
    private final int seatID;
    private SeatStatus status = SeatStatus.EMPTY;
    private float price;

    public Seat(int seatID, String position, float price) {
        this.seatID = seatID;
        this.position = position;
        this.price = price;
    }

    public String getPos() { return position; }

    public int getID() { return seatID; }

    public float getPrice() { return price; }

    public void setPrice(float price) { this.price = price; }

    public SeatStatus getStatus() { return status; }

    public void setEmpty() { status = SeatStatus.EMPTY; }

    public void setHeld() { status = SeatStatus.HELD; }

    public void setBooked() { status = SeatStatus.BOOKED; }
}
