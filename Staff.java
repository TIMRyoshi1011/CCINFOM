public class Staff {

    private int staffId;
    private String firstName;
    private String lastName;
    private String position;
    private String status;
    private String shift;
    private double salary;

    public Staff(int staffId, String firstName, String lastName, String position, String status, String shift,
            double salary) {
        this.staffId = staffId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.status = status;
        this.shift = shift;
        this.salary = salary;
    }

    public int getStaffId() {
        return this.staffId;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPosition() {
        return this.position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getShift() {
        return this.shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public double getSalary() {
        return this.salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public void staffDetails() {
        System.out.println("________________________________");
        System.out.println("Staff ID: " + this.staffId + " | " + this.firstName + " " + this.lastName);
        System.out.println("Position: " + this.position);
        System.out.println("Status: " + this.status);
        System.out.println("Shift: " + this.shift);
        System.out.println("Salary: $" + this.salary);
        System.out.println("________________________________");
    }
}
