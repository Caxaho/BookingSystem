import java.util.ArrayList;

public class Agent extends User {
    private final ArrayList<Contract> contracts = new ArrayList<Contract>();

    public Agent(String name, String username, String emailAddress, String password) {
        super(name, username, emailAddress, password);
        this.setAccountType(AccountType.AGENT);
    }

    public void addContract(float commission, int showID, int[] seats) {
        contracts.add(new Contract(commission, showID, seats));
    }

    public ArrayList<Contract> getContracts() { return contracts; }
}
