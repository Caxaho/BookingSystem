package com.BookingClient;

public class User {
    private final String name;
    private final String username;
    private final String emailAddress;
    private final String password;
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

    /**
     * Validates full name with regex.
     * @param fullName Full name to validate.
     * @return True if valid full name.
     */
    public static boolean validFullName(String fullName) {
        String lowerCaseName = fullName.toLowerCase();
        return lowerCaseName.matches("^[a-z ,.'-]+$");
    }

    public String getUsername() { return username; }

    /**
     * Validates username with regex (lowercase only, no special characters, 2 or more characters).
     * @param username Username to validate.
     * @return True if valid username.
     */
    public static boolean validUsername(String username) {
        return username.matches("^[a-z0-9_-]{2,}$");
    }

    public int getID() { return userID; }

    public String getEmail() { return emailAddress; }

    /**
     * Validates email address with regex (accepted format = demo@contoso.com)
     * @param email Email address to validate.
     * @return True if valid email address.
     */
    public static boolean validEmailAddress(String email) {
        return email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    public String getMobile() { return mobileNo; }

    public void setMobile(String mobileNo) { this.mobileNo = mobileNo; }

    /**
     * Validates simple mobile phone number with regex.
     * @param phoneNumber Phone number to validate.
     * @return True if valid phone number.
     */
    public static boolean validPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("^\\d{11}$");
    }

    public AccountType getAccountType() { return accountType; }

    public void setAccountType(AccountType type) { accountType = type; }

    public Boolean checkPW(String pass) { return password.equals(pass); }

    /**
     * Validates password with regex (It must contain a minimum of eight characters, at least one uppercase letter, one lowercase letter, one number, and one special character).
     * @param pass Password to validate.
     * @return True if valid password.
     */
    public static boolean validPass(String pass) {
        return pass.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
    }
}
