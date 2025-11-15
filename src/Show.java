import java.time.LocalTime;

public class Show {

    private String showId;
    private String title;
    private LocalTime runtime;
    private LocalTime startTime;
    private LocalTime endTime;
    private int theaterId;
    private String status;

    public Show(String showId, String title, LocalTime runtime, LocalTime startTime, LocalTime endTime
                , int theaterId, String status){
        this.showId = showId;
        this.title = title;
        this.runtime = runtime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.theaterId = theaterId;
        this.status = status;
    }
    public String getShowId(){return this.showId;}

    public String getTitle() {return this.title;}
    public void setTitle(String title) {this.title = title;}

    public LocalTime getShowRuntime() {return this.runtime;}
    public void setShowRuntime(LocalTime runtime) {this.runtime = runtime;}

    public LocalTime getStartTime() {return startTime;}
    public void setStartTime(LocalTime startTime) {this.startTime = startTime;}

    public LocalTime getEndTime() {return endTime;}
    public void setEndTime(LocalTime endTime) {this.endTime = endTime;}

    public int getTheaterId() {return this.theaterId;}
    public void setTheaterId(int theaterId) {this.theaterId = theaterId;}

    public String getShowStatus() {return this.status;}
    public void setShowStatus(String status) {this.status = status;}

    public void showDetails(){
        System.out.println("________________________________");
        System.out.println("Show ID: " + this.showId + " | " + this.title);
        System.out.println("Runtime: " + this.runtime);
        System.out.println("Start: " + this.startTime + " End: " + this.endTime);
        System.out.println("Theater: " + theaterId + " | " + this.status);
        System.out.println("________________________________");
    }
}
