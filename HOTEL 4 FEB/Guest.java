package com.company;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;


public class Guest implements QueryObject<Guest> {

    private static Statement sqlStatement = Main.getSqlStatement();
    private static Scanner sc = new Scanner(System.in);
    
    private int guestID;
    private String fname;
    private String lname;
    private int bookingID;
    private Room room;
    private Bill bill;

    // Konstruktorer
    Guest() {
    }
    Guest(String fname, String lname) {
        this.fname = fname;
        this.lname = lname;
    }
    @Override // FRÅN INTERFACET QUERYOBJECT<T>
    public Guest construct(int id) throws SQLException {
        
        ResultSet rs = sqlStatement.executeQuery("SELECT * FROM customers "+
            "LEFT JOIN booking ON customers.customerId = booking.customerId WHERE customers.customerId = "+id
        );
        if (rs.next()) {
            
            guestID = rs.getInt("customerId");
            fname = rs.getString("firstName");
            lname = rs.getString("lastName");
            bookingID = rs.getInt("bookingId");
        }
        return this;
    }

    public void insert() throws SQLException {
        // Verkställer INSERT i databasen och sätter objektets id till id:t från databasen
        // -> sen kan gästens attribut användas där det passar (i klassen - fname/this.guestID, utanför klassen - obj.getGuestID())

        sqlStatement.executeUpdate("INSERT INTO customers (firstName, lastName) VALUES ('"+fname+"', '"+lname+"');");
        // Hämtar senast tillagda gästen
        ResultSet rs = sqlStatement.executeQuery("SELECT customerId FROM customers ORDER BY customerId DESC LIMIT 1;");
        rs.next();
        this.guestID = rs.getInt("customerId");
    }

    public void menu() throws SQLException {
        boolean exit = false;

        while (!exit) {
            System.out.println(
                "\nGuest Menu"+
                "\n1. My booking details"+
                "\n2. Order food"+
                "\n3. Checkout"+
                "\n0. Sign out" // Log out?
            );

            int key = Main.parseInputInt();

            switch (key) {
                case 1:
                    myBookingDetails();
                    break;
                case 2:
                    orderFood();
                    break;
                case 3:
                    boolean checkout = checkout();

                    if (checkout) {
                        System.out.println("\n\nThank you for staying at Elite Hotel!\n");
                        
                        // loggar ut automatiskt om gästen checkat ut från Guest view
                        exit = true;
                    }
                    break;    
                default:
                    exit = true;
                    break;
            }
        }   
    }

    private void orderFood() throws SQLException {

        Food.printMenu();

        System.out.println("\nType the items you want to order, separate with comma:");
        String order = sc.nextLine();

        // .split(",") returnerar en array av ord separerade med komma ("," tas bort) -> gör om det till en List
        List<String> items = Arrays.asList(order.split(","));

        // Gör om orden i listan så att första " " efter "," tas bort (.substring(1)) om det finns
        items = items.stream().map(s -> (s.charAt(0) == ' ') ? s.substring(1) : s ).collect(Collectors.toList());
        
        List<Integer> orderIDs = new ArrayList<>();
        
        int sum = 0;

        System.out.println("\nYour order:");
        for (String item : items) {

            ResultSet rs2 = sqlStatement.executeQuery("SELECT * FROM food WHERE foodName LIKE '"+item+"';");
            if (rs2.next()) {
                System.out.println("- "+rs2.getString("foodName")+", "+rs2.getString("foodPrice")+" KR");
                orderIDs.add(rs2.getInt("foodId"));
                sum += rs2.getInt("foodPrice");
            }
        }
        System.out.println("Total amount: "+sum+" KR");

        System.out.println("\nConfirm? (y/n)");
        String key = sc.nextLine();

        if (key.equalsIgnoreCase("y")) {

            for (Integer foodId : orderIDs) {
                sqlStatement.executeUpdate("INSERT INTO booking_food VALUES ("+this.bookingID+", "+foodId+");");
            }

            System.out.println("\nYour food is being prepared,\n"+sum+" KR was added to your bill");
            System.out.println("\nThank you and welcome back!");
        } else {
            System.out.println("\nWelcome back!");
        }
    }

    public boolean checkout() throws SQLException {
        boolean checkoutConfirmed = false;

        System.out.println("\n\nGuest "+this.guestID+", "+this.fname+" "+this.lname);

        // Hämtar checkoutdate för checkout preview
        ResultSet rs = sqlStatement.executeQuery("SELECT checkoutDate FROM booking WHERE customerId ="+guestID+";");
        if (rs.next()) {
            int diff = LocalDate.now().until(rs.getDate("checkoutDate").toLocalDate()).getDays();
            
            // Om en egentligen skulle stannat flera dagar "varnas" en för att en är på väg att checka ut i förtid
            if (diff > 0) System.out.println("Early checkout preview");
            else if (diff == 0) System.out.println("Checkout preview");
            else System.out.println("Late checkout preview");
        }
        
        // Visar (och skapar) en preview av notan för dagens datum
        this.bill = new Bill();
        bill.createReceipt(guestID);

        System.out.println("\n\nCheck out now? (y/n)");

        String val = sc.nextLine();

        if (val.equalsIgnoreCase("y")) {
            
            // Sätter checkoutDate till NOW() och hämtar rum-id:t
            sqlStatement.executeUpdate("UPDATE booking SET checkOutDate = NOW() WHERE customerId = " + guestID + ";");
            ResultSet rs2 = sqlStatement.executeQuery("SELECT roomId FROM booking WHERE customerId = " + guestID + ";");
            if (rs2.next()){

                // Metod som uppdaterar ett rums status både i databasen och i rum-listan
                Main.rooms.get(rs2.getInt("roomId") - 1).updateStatus(true);
        
                // Sparar kvittot på fil
                SaveLoadFromFile save = new SaveLoadFromFile();
                save.saveData(bill);

                System.out.println("\n\nCheckout confirmed\n");

                System.out.println("\nShow receipt? (y/n)");

                if (sc.nextLine().equalsIgnoreCase("y")) {
                    
                    // Laddar kvittot från fil
                    save.loadData(bill);

                    System.out.print("\n\nPress enter to continue ");
                    sc.nextLine();
                }

                return checkoutConfirmed = true;
            }
        }

        return checkoutConfirmed;
    }
    
    public void myBookingDetails() throws SQLException {

        ResultSet result = sqlStatement.executeQuery("SELECT * FROM allinfo WHERE customerId = " + guestID + ";");
        
        if (result.next()) {
            this.room = Main.rooms.get(result.getInt("roomId")-1); // behövs kanske inte då det inte används

            System.out.println("\n-------GUEST--------");
            System.out.println("Guest ID: " + guestID);
            System.out.println("First name: " + fname);
            System.out.println("Last name: " + lname);
            System.out.println("--------ROOM--------");
            System.out.println("Room ID: " + room.getId());
            System.out.println("Check In: " + result.getString("checkInDate"));
            System.out.println("Check Out: " + result.getString("checkOutdate"));
            
            boolean done;
            do {
                done = false;

                System.out.println("\nOptions:\nR - Room details, B - Bill, Other - return");
                String key = sc.nextLine();

                if (key.equalsIgnoreCase("R")) {
                    System.out.println("\n"+room.detailedInfo());
                    
                } else if (key.equalsIgnoreCase("B")) {

                    // Skapar och räknar ut notan, "false" är markerat för att den inte ska sparas som ett kvitto
                    this.bill = new Bill();
                    bill.totalRoomCost(guestID, false);
                    bill.totalFoodCost(bookingID, false);
                    bill.totalCost(false);
                } else {
                    done = true;
                }
            } while (!done);
        }
        else{
            System.out.println("\nGuest not found");
        }        
        
    }

    // getters & setters
    public int getGuestID() {
        return guestID;
    }
    public String getFname() {
        return fname;
    }
    public String getLname() {
        return lname;
    }
    public int getBookingID() {
        return bookingID;
    }
    public void setBookingID(int bookingID) {
        this.bookingID = bookingID;
    }

    @Override
    public String toString() {
        return "Guest ID: "+guestID+"\nName: "+fname+" "+lname+"\nBooking ID: "+bookingID;
    }

}
