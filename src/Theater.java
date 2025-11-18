public class Theater {
    
    private String theater_ID;
    private String theater_Name;
    private Integer capacity;
    private String theater_Status;

    public Theater(String theater_ID, String theater_Name, Integer capacity, String theater_Status) {
        this.theater_ID = theater_ID;
        this.theater_Name = theater_Name;
        this.capacity = capacity;
        this.theater_Status = theater_Status;
    }

    public String getTheaterID () {
        return this.theater_ID;
    }

    public void setTheaterID (String theater_ID) {
        this.theater_ID = theater_ID;
    }

    public String getName () {
        return this.theater_Name;
    }

    public void setName (String theater_Name) {
        this.theater_Name = theater_Name;
    }

    public Integer getCapacity () {
        return this.capacity;
    }

    public void setCapacity (Integer capacity) {
        this.capacity = capacity;
    }

    public String getReservationStatus () {
        return this.theater_Status;
    }

    public void setReservationStatus (String theater_Status) {
        this.theater_Status = theater_Status;
    }
}
