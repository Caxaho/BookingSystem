package com.BookingClient;

public class User {
    private String name;
    private String username;
    private String emailAddress;
    private String password;
    private String mobileNo = "";
    private int userID;
    private static int userCount;
    public enum AccountType {
        AGENT,
        CUSTOMER,
        ADMIN,
        VENUE_MANAGER
    }
    private AccountType accountType = AccountType.CUSTOMER;

    public User(String name, String username, String emailAddress, String password) {
        this.name = name;
        this.username = username;
        this.emailAddress = emailAddress;
        this.password = password;
    }

    public String getName() { return name; }

    public String getUsername() { return username; }

    public int getID() { return userID; }

    public String getEmail() { return emailAddress; }

    public String getMobile() { return mobileNo; }

    public void setMobile(String mobileNo) { this.mobileNo = mobileNo; }

    public AccountType getAccountType() { return accountType; }

    public void setAccountType(AccountType type) { accountType = type; }

    public Boolean checkPW(String pass) { return password.equals(pass); }
}
