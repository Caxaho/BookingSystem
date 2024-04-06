public class VenueManager extends User{
    public VenueManager(String name, String username, String emailAddress, String password) {
        super(name, username, emailAddress, password);
        this.setAccountType(AccountType.VENUE_MANAGER);
    }
}
