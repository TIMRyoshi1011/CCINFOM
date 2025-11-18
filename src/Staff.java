public class Staff {

    private String staffId;
    private String firstName;
    private String lastName;
    private String position;
    private String status;
    private String shift;
    private int salary;

    public Staff(String staffId, String firstName, String lastName, String position, String status, String shift,
            int salary) {
        this.staffId = staffId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.status = status;
        this.shift = shift;
        this.salary = salary;
    }

    public String getStaffId() {
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

    public int getSalary() {
        return this.salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public void staffDetails() {
        Main.header("Staff Details");
        System.out.println("Staff ID: " + this.staffId + " | " + this.firstName + " " + this.lastName);
        System.out.println("Position: " + this.position);
        System.out.println("Status: " + this.status);
        System.out.println("Shift: " + this.shift);
        System.out.println("Salary: ₱" + this.salary);
        Main.subheader();
    }
}
