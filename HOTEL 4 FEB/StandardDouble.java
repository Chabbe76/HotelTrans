package com.company;

public class StandardDouble extends Room {
    
    // String roomType = "Standard double room";
    // private int nrOfBeds = 2;
    // private String AC = "No";
    // private String breakfast = "no";
    // private int pricePerDay = 800;
    
    // Default constructor sätter de fasta värdena! De är deklarerade i superklassen som private. Dunno if its correct practice men det funkar bra.
    StandardDouble() {
        setRoomType("Standard double room");
        setNrOfBeds(2);
        setAC("No");
        setBreakfast("No");
        setPricePerDay(800);
    }
    
    // Constructor för när man laddar från databasen! Hämtat roomId och roomStatus.
    StandardDouble(int id, boolean available) {
        setId(id);
        setRoomType("Standard double room");
        setNrOfBeds(2);
        setAC("No");
        setBreakfast("No");
        setPricePerDay(800);
        setAvailable(available);
    }
    
}
