package com.company;

public class DeluxeDouble extends Room {
    
    // String roomType = "Deluxe double room";
    // private int nrOfBeds = 2;
    // private String AC = "Yes";
    // private String breakfast = "Yes";
    // private int pricePerDay = 1500;

    // Default constructor sätter de fasta värdena! De är deklarerade i superklassen som private. Dunno if its correct practice men det funkar bra.
    DeluxeDouble() {
        setRoomType("Deluxe double room");
        setNrOfBeds(2);
        setAC("Yes");
        setBreakfast("Yes");
        setPricePerDay(1500);
    }
    
    // Constructor för när man laddar från databasen! Hämtat roomId och roomStatus.
    DeluxeDouble(int id, boolean available) {
        setId(id);
        setRoomType("Deluxe double room");
        setNrOfBeds(2);
        setAC("Yes");
        setBreakfast("Yes");
        setPricePerDay(1500);
        setAvailable(available);
    }    
    
}
