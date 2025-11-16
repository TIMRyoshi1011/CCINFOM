import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TheaterRecords {

    //Add Theater Records
    public static void addRecord(Scanner sc, ArrayList<String> theater_ID, ArrayList<String> name, ArrayList<Integer> capacity, ArrayList<LocalDate> reserved_Date, ArrayList<String> reservation_Status) {

        String newTheaterID, newName, newStatus;
        int newCapacity;
        LocalDate newDate = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");

        //New Theater ID
        do { 
            System.out.print("Theater ID (TH + 6 digits): ");
            newTheaterID = sc.nextLine().toUpperCase();

            if(newTheaterID.isEmpty()) { //To prevent empty inputs
                System.out.println("No input, please try again.");
            } else if (!newTheaterID.matches("TH\\d{6}")) {
                System.out.println("Please follow the correct format.");
            } 
            else if (theater_ID.contains(newTheaterID)) { //To prevent duplicates
                System.out.println("Theater ID exists, please try again.");
            } 

        } while (newTheaterID.isEmpty() || !newTheaterID.matches("TH\\d{6}") || theater_ID.contains(newTheaterID));
        theater_ID.add(newTheaterID); //Adds new theater ID to array list of theater IDs

        //New Theater Name
        do { 
            System.out.print("Theater Name: ");
            newName = sc.nextLine().toUpperCase();

            if(newName.isEmpty()) { //To prevent empty inputs
                System.out.println("No input, please try again.");
            } else if (name.contains(newName)) {  //To prevent duplicates
                System.out.println("Theater Name exists, please try again.");
            } 
        } while (newName.isEmpty() || name.contains(newName));
        name.add(newName); //Adds new theater name to array list of theater names

        //New Theater Capacity
        do { 
            System.out.print("Capacity: ");
            while (!sc.hasNextInt()) { 
                System.out.println("Numbers only, please try again.");
                sc.nextLine();
                System.out.print("Capacity: ");
            }
            newCapacity = sc.nextInt();
            sc.nextLine();

            if (newCapacity <= 0) {
                System.out.println("Capacity should be greater than 0, please try again.");
            }
        } while (newCapacity <= 0);
        capacity.add(newCapacity); //Adds new capacity to array list of capacities

        //New Theater Reserved Date
        do {
            System.out.print("Reservation Date (yyyy-MM-dd): ");
            String oldDate = sc.nextLine();

            if(oldDate.isEmpty()) { //To prevent empty inputs
                System.out.println("No input, please try again.");
            }

            try {
                newDate = LocalDate.parse(oldDate, formatter); //To transform old date format to new date format
                reserved_Date.add(newDate); //Adds new reservation date to array list of reserved dates
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format, please use MM-dd-yyyy.");
            }
        } while (newDate == null);

        //New Theater Status
        do { 
            System.out.print("Reservation Status: ");
            newStatus = sc.nextLine().toUpperCase();

            if(newStatus.isEmpty()) { //To prevent empty inputs
                System.out.println("No input, please try again.");
            }
        } while (newStatus.isEmpty());
        reservation_Status.add(newStatus); //Adds new reservation status to array list of reservation statuses

        System.out.println("Theater record added.");
    }

    /* these 3 methods
     * Update theater capacity (if renovations were made)
     * Update reservation date
     * Update reservation status
    */

    //Delete Theater Records
    public static void deleteRecord(Scanner sc, ArrayList<String> theater_ID, ArrayList<String> name, ArrayList<Integer> capacity, ArrayList<LocalDate> reserved_Date, ArrayList<String> reservation_Status) {
        int row;
    
        viewRecord(theater_ID, name, capacity, reserved_Date, reservation_Status);
    
        System.out.println("What row would you like to delete?");
        System.out.print("Row: ");
        row = sc.nextInt();
    
        if (row >= 0 && row < theater_ID.size()) {
            theater_ID.remove(row); name.remove(row); capacity.remove(row); reserved_Date.remove(row); reservation_Status.remove(row);
            System.out.println("Record deleted at row " + row);
        }
        else {
            System.out.println("Invalid row number. Make sure it exists!");
        }
    }

    //View Theater Records
    public static void viewRecord(ArrayList<String> theater_ID, ArrayList<String> name, ArrayList<Integer> capacity, ArrayList<LocalDate> reserved_Date, ArrayList<String> reservation_Status) {
        System.out.printf("%-10s | %-12s | %-8s | %-10s | %-10s|\n", "Theater ID", "Theater Name", "Capacity", "Reserved Date", "Reservation Status");
    
        for (int i = 0; i < theater_ID.size(); i++) {
            System.out.printf("%-10s | %-12s | %-8d | %-10s | %-10s|\n", theater_ID.get(i), name.get(i), capacity.get(i), reserved_Date.get(i), reservation_Status.get(i));
        }
    }

    /* these 5 methods
     * List capacity of theater
     * List reservation date of theater
     * List reservation status of theater
     * View theater capacity and audience turnout for a show
     * View the shows scheduled on a specific date and the respective time
    */
}