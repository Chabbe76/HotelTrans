package com.company;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    // Lista över rum som används och uppdateras i hela programmet
    static List<Room> rooms = null;    

    private static Scanner sc = new Scanner(System.in);
    // Username
    private static final String USERNAME = "root";
    // password
    private static final String PASSWORD = "50+50hundra"; // mios: mysql12345! robbans: sql12345?
    // link till min databas
    private static final String LINK = "jdbc:mysql://localhost:3306/hoteltransylvania?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

    private static Connection connect = null;
    static Statement sqlStatement = null; 

    public static Statement getSqlStatement() {
        return sqlStatement;
    }
    public static Connection getConnect() {
        return connect;
    }

    public static void main(String[] args) throws SQLException {

        try {
            connect = DriverManager.getConnection(LINK, USERNAME, PASSWORD);
            System.out.println("\nConnected to database");
        
            sqlStatement = connect.createStatement();

            prepareRoomsList();
            Food.prepareMenu();

            startScreen();

            startMenu();
        
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
      
        if (connect != null) {
            connect.close();
        }
    }

    private static void prepareRoomsList() throws SQLException {
        int id;
        String size;
        String cat;

        rooms = new ArrayList<>();

        // Om checkout-datum (och tiden) har passerat sätts rummet till available
        sqlStatement.executeUpdate("UPDATE rooms JOIN booking ON rooms.roomId = booking.roomId "+
            "SET available = true WHERE checkoutDate < NOW();"
        );
        
        ResultSet rs = sqlStatement.executeQuery("SELECT * FROM rooms;");

        while (rs.next()) {
            id = rs.getInt("roomId");
            size = rs.getString("roomSize");
            cat = rs.getString("roomCategory");

            // Skapar instanser av rätt subklass beroende på vilken typ av rum det är
            if (size.equals("Double")) {
                rooms.add(
                    (cat.equals("Deluxe") ? new DeluxeDouble() : new StandardDouble())
                .construct(id)); // construct method from QueryObject<T> interface (generic)
            } else {
                rooms.add(
                    (cat.equals("Deluxe") ? new DeluxeSingle() : new StandardSingle())
                .construct(id));
            }
        }
    }

    private static void startScreen() {
        System.out.println(
            "\n\n\n                                             ."+
            "\n                                            /|\\"+
            "\n                                    __= +  /_|_\\  + =__"+
            "\n                                ¨¨¨+ === + \\ | / + === +¨¨¨"+
            "\n                          ================  \\|/  ================"+
            "\n   ________ ___      ___ _________ ________  '  ___   ___   _____  _________ ________ ___"+
            "\n   |$|   \\| |$|      |$| |/ |$| \\| |$|   \\|     |$|   |$|  /$/ \\$\\ |/ |$| \\| |$|   \\| |$|"+
            "\n   |$|___   |$|      |$|    |$|    |$|___       |$|___|$| |$|   |$|   |$|    |$|___   |$|"+
            "\n   |$|      |$|      |$|    |$|    |$|          |$|   |$| |$|   |$|   |$|    |$|      |$|"+
            "\n   |$|___/| |$|___/| |$|    |$|    |$|___/|     |$|   |$|  \\$\\_/$/    |$|    |$|___/| |$|___/|"+
            "\n   ¨¨¨¨¨¨¨¨ ¨¨¨¨¨¨¨¨ ¨¨¨    ¨¨¨    ¨¨¨¨¨¨¨¨  .  ¨¨¨   ¨¨¨   ¨¨¨¨¨     ¨¨¨    ¨¨¨¨¨¨¨¨ ¨¨¨¨¨¨¨¨"+
            "\n                          ================= <§> ================="+
            "\n                                             '"+
            "\n"+
            "\n        A.|<.A.   "+
            "\n  //   __    _______  _______  _______     __         ____                    __    __   __  \\\\"+
            "\n //   / |   |/    // |/    // |/    //     ||        //  \\\\    ..            / |   / |  / |   \\\\"+
            "\n||   /_ |     ___//    ___//       //      || ___   ||   /||  _||_   ___    /_ |  /_ | /_ |    ||"+
            "\n||     ||        \\\\       \\\\    ==//==     ||/  \\\\  ||  / || '=  =' //__\\\\    ||    ||   ||    ||"+
            "\n||    _||_  _     || _     ||    //        ||    || || /  ||   ||_ | ====='  _||_  _||_ _||_   ||"+
            "\n \\\\  |____| \\\\___//  \\\\___//    //         ||    ||  \\\\__//    \\_/  \\\\__// |____||____||____| //"+
            "\n  \\\\                                                                                         //\n\n"
        );
    }

    public static void startMenu() throws SQLException {
        boolean exit = false;

        System.out.println("\nWelcome");

        while (!exit) {
            
            System.out.println(
                "\nI am a"+
                "\n1. Guest"+
                "\n2. Portier"+
                "\n0. Exit"
            );

            int key = parseInputInt(); // samlad metod för att felhantera input av siffror

            switch (key) {
                case 1:
                    loginOrRegister();
                    break;
                case 2:
                    receptionLogin();
                    break;
                case 0:
                    exit = true;
                    break;
                default:
                    System.err.println("\nInvaild input: Please enter 0, 1 or 2");
                    break;
            }
        }
    }

    public static void loginOrRegister() throws SQLException {
        System.out.println(
            "\nGuest"+
            "\n1. Book a room"+
            "\n2. Log in"+
            "\n0. Go back"
        );

        switch (parseInputInt()) {
            case 1:
                System.out.println("\n\nWelcome to Elite Hotel, n00b\n");
                
                // BookARoom() returnerar ett nytt guest-objekt
                Guest newGuest = Reception.BookARoom();

                if (newGuest != null) {
                    System.out.println("\n\nLogging into My pages...");
                    System.out.println("\n"+newGuest);
                    
                    newGuest.menu();
                }
                break;
            case 2:
                guestLogin();
                break;
            default:
                break;
        }
    }

    public static void guestLogin() throws SQLException {

        System.out.println("\nLog in with your guest ID:");
        int id = parseInputInt();

	    // Hämtar kunddata + bookingId
        ResultSet rs = sqlStatement.executeQuery("SELECT * FROM customers "+
            "LEFT JOIN booking ON customers.customerId = booking.customerId WHERE customers.customerId = "+id
        );

        
        if (rs.next()) {
	        // Nytt guestobjekt med datan som attribut 
            Guest guest = new Guest().construct(id); // construct() från QueryObject<T> interface
            
            // Printar ut gästens uppgifter
            System.out.println("\n"+guest);
	    
	        // Loggar in med rätt gäst
            guest.menu();
        } else {
            System.err.println("\nNot found");
        }
    }

    private static void receptionLogin() throws SQLException {
        
        System.out.print("\nStaff password: ");
        sc.nextLine(); // *******

        Reception.menu();
    }
    
    static int parseInputInt() { // Samlad metod för felhantering av user inputs som ska vara siffror
        boolean loop;
        int input = 0;

        do {
            try {
                input = Integer.parseInt(sc.nextLine());
                loop = false;
            } catch (Exception e) {
                System.err.println("\nInvalid input: Only numbers are accepted");
                loop = true;
            }
        } while (loop);
        // går inte ur loopen förrän input har en integer

        return input;
    }

	public static void printAnyTable(ResultSet rs) throws SQLException {
        int s = 11; // smal kolumn
        int b = 20; // bred kolumn

        System.out.println("");
        
        // hämta antal kolumner
        int columnCount = rs.getMetaData().getColumnCount();

        // hämta kolumnernas namn, skapa en arrayList lika lång som columnCount
        String[] columnNames = new String[columnCount];

        // för varje nummer - lägg in columnens namn i array (columnNames)
        for (int i = 0; i < columnCount; i++) {
            columnNames[i] = rs.getMetaData().getColumnName(i + 1);
        }

        // skriv ut columnenrnas namn
        for (String columnName : columnNames) {

            // om kolumnnamnet innehåller "Id" blir kolumnen smalare
            String paddedColumnName = (columnName.contains("Id")) ? PadRight(columnName, s) : PadRight(columnName, b);
            System.out.print(paddedColumnName);
        }

        while (rs.next()) {
            System.out.println("");

            // hämta värdet i den nuvarande radens alla columner
            for (String columnName : columnNames) {
                String value = rs.getString(columnName);

                if (value == null) {
                    value = "-";
                }

                String paddedValue = (columnName.contains("Id")) ? PadRight(value, s) : PadRight(value, b);
                System.out.print(paddedValue);
            }
        }

        System.out.println("");
    }
    
    static String PadRight(String string, int totalStringLength) {
        int charsToPadd = totalStringLength - string.length();
        
        if (string.length() >= totalStringLength) {
            return string;
        }

        StringBuilder stringBuilder = new StringBuilder(string);
        for (int i = 0; i < charsToPadd; i++) {
            stringBuilder.append(" ");
        }

        return stringBuilder.toString();
    }

}
