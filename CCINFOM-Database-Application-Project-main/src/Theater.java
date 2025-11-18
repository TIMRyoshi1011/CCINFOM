import java.time.LocalDate;
import java.util.ArrayList;

public class Theater {
    
    private ArrayList<String> theater_ID = new ArrayList<>();
    private ArrayList<String> name = new ArrayList<>();
    private ArrayList<Integer> capacity = new ArrayList<>();
    private ArrayList<LocalDate> reserved_Date = new ArrayList<>();
    private ArrayList<String> reservation_Status = new ArrayList<>();

    public Theater(ArrayList<String> theater_ID, ArrayList<String> name, ArrayList<Integer> capacity, ArrayList<LocalDate> reserved_Date, ArrayList<String> reservation_Status) {
        this.theater_ID = theater_ID;
        this.name = name;
        this.capacity = capacity;
        this.reserved_Date = reserved_Date;
        this.reservation_Status = reservation_Status;
    }

    public ArrayList<String> getTheaterID () {
        return this.theater_ID;
    }

    public void setTheaterID (ArrayList<String> theater_ID) {
        this.theater_ID = theater_ID;
    }

    public ArrayList<String> getName () {
        return this.name;
    }

    public void setName (ArrayList<String> name) {
        this.name = name;
    }

    public ArrayList<Integer> getCapacity () {
        return this.capacity;
    }

    public void setCapacity (ArrayList<Integer> capacity) {
        this.capacity = capacity;
    }

    public ArrayList<LocalDate> getReservedDate () {
        return this.reserved_Date;
    }

    public void setReservedDate (ArrayList<LocalDate> reserved_Date) {
        this.reserved_Date = reserved_Date;
    }

    public ArrayList<String> reservation_Status () {
        return this.reservation_Status;
    }

    public void setReservationStatus (ArrayList<String> reservation_Status) {
        this.reservation_Status = reservation_Status;
    }
}



