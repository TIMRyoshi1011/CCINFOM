import java.time.LocalTime;

public class Show {

    private String showId;
    private String title;
    private LocalTime runtime;
    private String status;
    private int price;

    public Show(String title, LocalTime runtime, int price, String status){
        this.title = title;
        this.runtime = runtime;
        this.status = status;
        this.price = price;
    }
    public String getShowId(){return this.showId;}

    public String getTitle() {return this.title;}
    public void setTitle(String title) {this.title = title;}

    public LocalTime getShowRuntime() {return this.runtime;}
    public void setShowRuntime(LocalTime runtime) {this.runtime = runtime;}

    public String getShowStatus() {return this.status;}
    public void setShowStatus(String status) {this.status = status;}

    public int getPrice(){return this.price;}
    public void setPrice(int price){this.price = price;}

    public void showDetails(){
        System.out.println("________________________________");
        System.out.println("Title: " + this.title);
        System.out.println("Runtime: " + this.runtime);
        System.out.println("Status: " + this.status);
        System.out.println("Price: P" + this.price);
        System.out.println("________________________________");
    }
}
