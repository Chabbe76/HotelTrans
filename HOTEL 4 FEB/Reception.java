package com.company;

import java.io.File;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Scanner;

public class Reception {

    private static Statement sqlStatement = null;
    private static Scanner input = new Scanner(System.in);

    static void menu() throws SQLException {
        sqlStatement = Main.getConnect().createStatement();
        
        boolean exit = false;

        while (!exit) {
            System.out.println(
                "\nReception menu"+
                "\n1. Show guest details"+
                "\n2. Delete or update guest"+
                "\n3. Book a room"+
                "\n4. Checkout guest"+
                "\n5. Show rooms"+
                "\n6. Call Back Guest"+
                "\n0. Go back"
            );

            int select = Main.parseInputInt();

            switch (select) {
                case 1:
                    showGuestDetails();
                    break;
                case 2:
                    System.out.println("\n1. Delete Guest ");
                    System.out.println("2. Update Guest Details");
                    
                    int sel = Main.parseInputInt();
                    switch (sel) {
                        case 1:
                            deleteGuest();
                            break;
                        case 2:
                            updateGuest();
                            break;
                        case 0:
                            break;
                        default:
                            System.out.println("WROOOOONG");
                    }
                    break;
                case 3:
                    BookARoom();
                    break;
                case 4:
                    checkOut();
                    break;
                case 5:
                    System.out.println("\n1. Available Rooms ");
                    System.out.println("2. All Rooms ");
                    System.out.println("0. Go back ");

                    int choi = Main.parseInputInt();
                    switch (choi) {

                        case 1:
                            checkRoomAvailable();
                            break;
                        case 2:
                            ShowAllRooms();
                            break;
                        default:
                            break;
                    }
                    break;
                case 6:
                    callBackGuest();
                    break;
                case 0:
                    exit = true;
                    break;
                default:
                    System.out.println("\nWROOOOONG");
            }
        }
    }

    private static void showGuestDetails() throws SQLException {
        
        ShowAllCustomers();
        System.out.println("\nGuest ID: ");
        
        int guestId = Main.parseInputInt();

        ResultSet rs = sqlStatement.executeQuery("SELECT * FROM allinfo WHERE customerId = "+guestId+";");
        if (rs.next()) {
            Guest guest = new Guest().construct(guestId);
            guest.myBookingDetails();
        } else {
            System.out.println("\nInvalid guest ID");
        }

    }

    public static Guest BookARoom() throws SQLException {
        sqlStatement = Main.getConnect().createStatement();

        String guestFirstName;
        String guestLastName;
        int dayNo;
        int roomId = 0;
        boolean loop = false; 
        Guest newGuest = null;

        do {
            showAvailableRooms();
        
            System.out.println("\nEnter room ID: (R - show room details, 0 - Cancel)");
            String key = input.nextLine(); // Kraschar?

            if (key.equalsIgnoreCase("R")) {
                Room.roomTypeDetails();
                loop = true;

            } else if (key.equals("0")) {
                System.out.println("\nBooking cancelled");
                loop = false;
            } else {
                try {
                    roomId = Integer.parseInt(key);
                    loop = false;

                    if (isRoomAvailable(roomId)){
                        System.out.println("\nEnter name for room " + roomId);
        
                        System.out.print("\nFirst name: "); // SKAPA NY GÄST
                        guestFirstName = input.nextLine();
                        System.out.print("Last name: ");
                        guestLastName = input.nextLine();
        
                        System.out.println("\n------BOOKING-------");
                        System.out.println(Main.rooms.get(roomId-1));
                        System.out.println("First name: " + guestFirstName);
                        System.out.println("Last name: " + guestLastName);
        
                        System.out.println("\nPlease enter number of nights: ");
                        dayNo = Main.parseInputInt(); // Här kan gästen skriva in hur många dagar den vill stanna
        
                        Calendar calendar = Calendar.getInstance();
                        System.out.println("\nToday is : " + calendar.getTime());
                        calendar.add(Calendar.DATE, +(dayNo));
                        System.out.println("Check out : " + calendar.getTime());

                        System.out.println("\nTotal estimated room cost = " + (dayNo * Main.rooms.get(roomId-1).getPricePerDay()));
        
                        System.out.println("\nConfirm booking? (y/n)");
                        key = input.nextLine();
                        if (key.equalsIgnoreCase("y")) {
                            // Lägger in en ny gäst i databasen - kan enkelt refereras till med newGuest-objektet
                            newGuest = new Guest(guestFirstName, guestLastName);
                            newGuest.insert();
        
                            // Lägger in ny bokning i databasen och hämtar senaste ID:t
                            sqlStatement.executeUpdate("INSERT INTO booking (customerId, roomId, checkinDate, checkoutDate) VALUES ("+newGuest.getGuestID()+", "+roomId+", NOW(), (NOW() + INTERVAL "+dayNo+" DAY));");
                            
                            // Samlad metod för att uppdatera ett rums status
                            Main.rooms.get(roomId-1).updateStatus(false);

                            System.out.println("\nBooking confirmed!\n");
                            loop = false;
                            
                            // Hämtar senaste bookingId:t och sätter guestobjektets bookingID
                            ResultSet rs = sqlStatement.executeQuery("SELECT bookingId FROM booking WHERE customerId = "+newGuest.getGuestID()+" ORDER BY bookingId DESC LIMIT 1;");
                            if (rs.next()); {
                                newGuest.setBookingID(rs.getInt("bookingID"));
                            }
                            
                        } else {
                            System.out.println("\nBooking cancelled\n");
                            loop = false;
                        }
                    } else {
                        System.err.println("\nThe Room isn't Available or doesn't Exist ");
                        loop = true;
                    }
                    
                } catch (Exception e) {
                    System.err.println("\nInvalid input!");
                    loop = true;
                }
            }
        } while (loop);
    

        return newGuest;
    }

    private static void deleteGuest() throws SQLException {
        boolean loop;

        ShowAllCustomers();

        System.out.println("\nEnter Guest ID for delete: (0 - Go back)");

        do {
            int guestId = Main.parseInputInt();
            ResultSet rs = sqlStatement.executeQuery("SELECT * FROM allinfo WHERE customerId = "+guestId+";");

            if (rs.next()) {
                int roomId = rs.getInt("roomId");

                sqlStatement.executeUpdate("DELETE FROM customers WHERE customerId =" + guestId + ";");

                System.out.println("\nGuest ID number: " + guestId + " was deleted!");
                
                Main.rooms.get(roomId-1).updateStatus(true);
                
                loop = false;

            } else if (guestId == 0) {
                loop = false; // Going back
            } else {
                System.out.println("Please give a correct Id number");
                loop = true;
            }
            
        } while (loop);
    }

    private static void updateGuest() throws SQLException {

        System.out.println("");
        ShowAllCustomers();
        
        System.out.print("\nEnter Guest ID for Update: ");
        int guestId = Main.parseInputInt();
        
        ResultSet rs = sqlStatement.executeQuery("SELECT * FROM allinfo WHERE customerId = "+guestId+";");
        if (rs.next()) {
            System.out.println("\n1. Update Name ");
            System.out.println("2. Update Room ");
            int sel = Main.parseInputInt();
            switch (sel) {
                case 1:
                    System.out.println("\nCurrent name is "+rs.getString("firstName")+" "+rs.getString("lastName"));
                    //Scanner input = new Scanner(System.in);

                    System.out.print("\nEnter first name: ");
                    String GuestfirsName = input.nextLine();
                    
                    System.out.print("Enter last name: ");
                    String GuestlastName = input.nextLine();
                    
                    int updated = sqlStatement.executeUpdate("UPDATE `hoteltransylvania`.`customers` SET firstName ='" + GuestfirsName + "', lastName = '" + GuestlastName + "' WHERE (customerId = '" + guestId + "');");

                    System.out.println((updated > 0) ? "\nName updated!" : "\nSomething went wrong");
                    break;
                case 2:
                    int oldRoomId = rs.getInt("roomId");

                    System.out.println("\nCurrent room:");
                    System.out.println(Main.rooms.get(oldRoomId-1));

                    showAvailableRooms();
                    
                    System.out.print("\nEnter the new room ID: ");
                    int newRoomId = Main.parseInputInt();

                    if (isRoomAvailable(newRoomId)) {

                        sqlStatement.executeUpdate("UPDATE `hoteltransylvania`.`booking` SET roomId = '" + newRoomId + "' WHERE (`customerId` = '" + guestId + "');");
                        
                        // Gör det gamla rummet ledigt och det nya rummet oledigt
                        Main.rooms.get(oldRoomId-1).updateStatus(true);
                        Main.rooms.get(newRoomId-1).updateStatus(false);

                        System.out.println("\nGuest ID " + guestId + " room changed to : " + newRoomId + " / Guest Sign: ");
                    } else {
                        System.out.println("Invalid room ID :c");
                    }
                    break;
                default:
                    System.out.println("Come ooonnn  WROOOOONG");
            }
        } else {
            System.out.println("Guest not found");
        }

    }

    private static void checkOut() throws SQLException {

        // Visa lista på gäster som är ej utcheckade
        ShowAllCustomers();

        System.out.println("\nGuest ID for Check Out: ");
        
        int guestId = Main.parseInputInt();

	    // Hämtar kunddata + bookingId
        ResultSet rs = sqlStatement.executeQuery("SELECT customerId FROM allinfo WHERE customerId = "+guestId+";");

        if (rs.next()) {
	        // Nytt guestobjekt med datan som attribut
            Guest guest = new Guest().construct(guestId);

            guest.checkout();

        } else {
            System.out.println("\nGuest not found");
        }
       
    }

    private static void ShowAllCustomers() throws SQLException {
        // skriv in SQL kod till mySQL databasen
        ResultSet result = Main.sqlStatement.executeQuery(
            "SELECT DISTINCT(customerId), firstName, lastName, roomId FROM allinfo "
            + "WHERE checkoutDate > NOW();"
        );

        Main.printAnyTable(result);
    }
   
    private static void showAvailableRooms() throws SQLException {

        System.out.println("\nCurrent available rooms:");
        //Main.rooms.stream().filter(r -> r.getAvailable()).forEach(System.out::println);
        
        System.out.println("\n"+Main.PadRight("ID", 5)+Main.PadRight("Type", 25)+"KR / night");
        Main.rooms.stream().filter(r -> r.getAvailable()).forEach(Room::tableRow);

    }

    private static void ShowAllRooms() throws SQLException {
        // skriv in SQL kod till mySQL databasen
        ResultSet result = Main.sqlStatement.executeQuery("SELECT * FROM rooms;");

        Main.printAnyTable(result);
    }

    private static boolean isRoomAvailable(int roomId) throws SQLException {
        ResultSet result = sqlStatement.executeQuery("SELECT * FROM rooms WHERE roomId = " + roomId + ";");

        if (result.next()) {
            return result.getBoolean("available");
        } else {
            return false;
        }
    }

    private static void checkRoomAvailable() throws SQLException {
        ResultSet result = sqlStatement.executeQuery("SELECT * FROM rooms WHERE available = 1;");

        Main.printAnyTable(result);
    }

    private static void callBackGuest() {
        
        System.out.println("\nPhone number:");
        int phone = Main.parseInputInt();
        Generic<Integer> info = new Generic<Integer>(phone);
        System.out.println(info.getObjekt());
        //Läser in två olika Data typer och skickar det till GENERIC Class " T objekt"

        
        System.out.println("\nCustomer Name: ");
        Scanner sc = new Scanner(System.in);
        String informa = sc.nextLine();
        Generic<String> info2 = new Generic<String>(informa);
        System.out.println(info2.getObjekt());

        File file = new File("callBack.txt");

        try {
            PrintWriter call = new PrintWriter(file);
            call.println(phone);
            call.println(informa);
            System.out.println("\nGuest Phone: +46"+phone+" Guest Name: "+informa+" for call");
            call.close();
        } catch (Exception e) {
            System.out.println("\nCustomer NOT saved for call" + e);
        }
    }
}
