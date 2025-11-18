public class Customer {
    
    private String customerId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String emailAddress;

    public Customer(String customerId, String firstName, String lastName, String phoneNumber, String emailAddress) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
    }

    public String getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String fName) {
        this.firstName = fName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lName) {
        this.lastName = lName;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setStringNumber(String phoneNo) {
        this.phoneNumber = phoneNo;
    }

    public String getEmailAdd() {
        return this.emailAddress;
    }

    public void setString(String email) {
        this.emailAddress = email;
    }

    public void customerDetails() {
        System.out.println("________________________________");
        System.out.println("Customer ID: " + this.customerId);
        System.out.println("Custumer Name: " + this.firstName + " " + this.lastName);
        System.out.println("Phone Number: " + this.phoneNumber );
        System.out.println("Email: " + this.emailAddress);
        System.out.println("________________________________");
    }
}
