import java.sql.SQLOutput;
import java.util.ArrayList;

public class ShowRecords {

    private ArrayList<Show> shows;

    public ShowRecords(){
        shows = new ArrayList<>();
    }

    public void addShow(Show show){
        shows.add(show);
        System.out.println("Added Successfully");
    }

    public boolean updateShow(String showId, Show updatedShow){
        for(int i = 0; i < shows.size(); i++){
            if (shows.get(i).getShowId().equals(showId)){
                shows.set(i, updatedShow);
                System.out.println("Updated Successfully");
                return true;
            }
        }
        System.out.println("Show not Found.");
        return false;
    }

    public boolean deleteShow(String showId) {
        return shows.removeIf(show -> show.getShowId().equals(showId));
    }

    public boolean viewShow(String showId) {
        for (Show show : shows){
            if (show.getShowId().equals(showId)){
                show.showDetails();
                return true;
            }
        }
        System.out.println("Show Not Found.");
        return false;
    }

    public void listAllShows() {
        for (Show show : shows){
            show.showDetails();
        }
    }

    public void listAllUpcomingShows(){
        for (Show show : shows){
            if (show.getShowStatus().equals("Upcoming")){
                show.showDetails();
            }
        }
    }

    public void listShowsInTheater(int theaterId){
        for (Show show : shows){
            if (show.getTheaterId() == theaterId){
                show.showDetails();
            }
        }
    }

}
