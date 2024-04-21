package com.BookingClient;

import java.util.ArrayList;

public class Agent extends User {
    private final ArrayList<Contract> contracts = new ArrayList<Contract>(); // Contracts issued by venue managers

    /**
     * Agent constructor. Child of User.
     * @param name Name of agent .
     * @param username Username of agent.
     * @param emailAddress Email address of agent.
     * @param password Password of agent.
     */
    public Agent(String name, String username, String emailAddress, String password) {
        super(name, username, emailAddress, password); // Constructs using parent class constructor
        this.setAccountType(AccountType.AGENT);
    }

    /**
     * Add contract to agent.
     * @param commission Commission percentage.
     * @param showID Show which the contract pertains to.
     * @param seats Seat IDs included in the contract.
     */
    public void addContract(float commission, int showID, int[] seats) {
        contracts.add(new Contract(commission, showID, seats));
    }

    /**
     * Get all contracts of agent.
     * @return contracts of agent.
     */
    public ArrayList<Contract> getContracts() { return contracts; }
}
