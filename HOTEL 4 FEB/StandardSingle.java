package com.company;

public class StandardSingle extends Room {
    
    // String roomType = "Standard single room";
    // private int nrOfBeds = 1;
    // private String AC = "No";
    // private String breakfast = "No";
    // private int pricePerDay = 500;
    
    // Default constructor sätter de fasta värdena! De är deklarerade i superklassen som private. Dunno if its correct practice men det funkar bra.
    StandardSingle() {
        setRoomType("Standard single room");
        setNrOfBeds(1);
        setAC("No");
        setBreakfast("No");
        setPricePerDay(500);
    }
    
    // Constructor för när man laddar från databasen! Hämtat roomId och roomStatus.
    StandardSingle(int id, boolean available) {
        setId(id);
        setRoomType("Standard single room");
        setNrOfBeds(1);
        setAC("No");
        setBreakfast("No");
        setPricePerDay(500);
        setAvailable(available);
    }
    
}
