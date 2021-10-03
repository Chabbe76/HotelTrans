package com.company;

import java.sql.ResultSet;
//import java.lang.reflect.Field;
import java.sql.SQLException;
//import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Room implements QueryObject<Room> {

    private int roomID;
    private String roomType;
    private int nrOfBeds;
    private String AC; // boolean?
    private String breakfast; // boolean?
    private int pricePerDay;
    private boolean available;


    @Override // FRÅN INTERFACET QUERYOBJECT<T>
    public Room construct(int id) throws SQLException {

        ResultSet rs = Main.getConnect().createStatement().executeQuery("SELECT * FROM rooms WHERE roomId = "+id);

        if (rs.next()) {
            
            roomID = rs.getInt("roomId");
            available = rs.getBoolean("available");

        }
        
        return this;
    }
    

    // Samlad metod för att uppdatera ett rums status i både databasen och tillhörande objekt i rum-listan
    public void updateStatus(boolean status) throws SQLException {
        // Uppdaterar available i databasen
        Main.getSqlStatement()
                .executeUpdate("UPDATE rooms SET available = " + status + " WHERE roomId = " + roomID + ";");

        // Uppdaterar available i Room-listan
        this.available = status;
    }


    // Olika sätt att visa rum-info:

    public String toString() {
        return "Room " + roomID + " : " + roomType;
    }

    // Printar ett rum som en rad i en tabell (med pris per natt)
    public void tableRow() {
        System.out.println(Main.PadRight(""+roomID, 5)+Main.PadRight(roomType, 25)+pricePerDay);
    }

    public String detailedInfo() {
        return "================= <§> ================="
        + "\n         " + ((this instanceof DeluxeSingle || this instanceof DeluxeDouble) ? " " : "") + roomType.toUpperCase()
        + "\n================= <§> ================="
        + "\nNumber of beds = " + nrOfBeds
        + "\nAC = " + AC
        + "\nBreakfast included = " + breakfast
        + "\nPrice per day = " + pricePerDay;
    }
    
    // Visar info om alla rumtyper
    public static void roomTypeDetails() {
        Scanner sc = new Scanner(System.in);

        Room[] types = { new StandardSingle(), new StandardDouble(), new DeluxeSingle(), new DeluxeDouble() };

        for (int i = 0; i < types.length; i++) {
            System.out.println("\n\n" + types[i].detailedInfo());
        }

        System.out.print("\n\nPress enter to continue ");
        sc.nextLine();
    }


    // getters & setters
    public int getId() {
        return roomID;
    }
    public void setId(int id) {
        this.roomID = id;
    }
    public boolean getAvailable() {
        return available;
    }
    public void setAvailable(boolean available) {
        this.available = available;
    }
    public String getRoomType() {
        return roomType;
    }
    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }
    public int getNrOfBeds() {
        return nrOfBeds;
    }
    public void setNrOfBeds(int nrOfBeds) {
        this.nrOfBeds = nrOfBeds;
    }
    public String getAC() {
        return AC;
    }
    public void setAC(String AC) {
        this.AC = AC;
    }
    public String getBreakfast() {
        return breakfast;
    }
    public void setBreakfast(String breakfast) {
        this.breakfast = breakfast;
    }
    public int getPricePerDay() {
        return pricePerDay;
    }
    public void setPricePerDay(int pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

}
