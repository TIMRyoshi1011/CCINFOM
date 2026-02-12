/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Marcus
 */
public class Theater {
    private String theater_ID;
    private String theater_Name;
    private Integer capacity, max_Rows, max_Cols;
    private String theater_Status;

    public Theater(String theater_Name, Integer capacity, Integer max_Rows, Integer max_Cols, String theater_Status) {
        this.theater_Name = theater_Name;
        this.capacity = capacity;
        this.max_Rows = max_Rows;
        this.max_Cols = max_Cols;
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

    public Integer getMaxRows () {
        return this.max_Rows;
    }

    public void setMaxRows (Integer max_Rows) {
        this.max_Rows = max_Rows;
    }

    public Integer getMaxCols () {
        return this.max_Cols;
    }

    public void setMaxCols (Integer max_Cols) {
        this.max_Cols = max_Cols;
    }

    public String getReservationStatus () {
        return this.theater_Status;
    }

    public void setReservationStatus (String theater_Status) {
        this.theater_Status = theater_Status;
    }
}
