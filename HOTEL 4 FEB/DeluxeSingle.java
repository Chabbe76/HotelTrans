package com.company;

public class DeluxeSingle extends Room {
    
    // String roomType = "Deluxe single room";
    // private int nrOfBeds = 1;
    // private String AC = "yes";
    // private String breakfast = "yes";
    // private int pricePerDay = 1000;
    
    // Default constructor sätter de fasta värdena! De är deklarerade i superklassen som private. Dunno if its correct practice men det funkar bra.
    DeluxeSingle() {
        setRoomType("Deluxe single room");
        setNrOfBeds(1);
        setAC("Yes");
        setBreakfast("Yes");
        setPricePerDay(1000);
    }
    
    // Constructor för när man laddar från databasen! Hämtat roomId och roomStatus.
    DeluxeSingle(int id, boolean available) {
        setId(id);
        setRoomType("Deluxe single room");
        setNrOfBeds(1);
        setAC("Yes");
        setBreakfast("Yes");
        setPricePerDay(1000);
        setAvailable(available);
    }
    
}
