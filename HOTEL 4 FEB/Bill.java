package com.company;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Robban
 */
public class Bill {

    List<String> rows = null;

    private int roomCost = 0;
    private long roomTotalCost = 0;
    private long daysBetween = 0;
    private int foodCost = 0;

    private int bookingId;

    public int getBookingId() {
        return bookingId;
    }

    void createReceipt(int guestId) throws SQLException {

        // Rader i kvittot
        rows = new ArrayList<>();

        rows.add("============= <§> =============");
        rows.add("          ELITE HOTEL          ");
        rows.add("============= <§> =============");
        rows.add("");

        // true innebär att kvittot sparas för att sparas på fil
        totalRoomCost(guestId, true);
        totalFoodCost(guestId, true);
        totalCost(true);

    }

    void totalRoomCost(int guestId, boolean printBill) throws SQLException {

        int roomId;

        ResultSet rs = Main.getSqlStatement().executeQuery(" SELECT * FROM allinfo where customerId = " + guestId +";");

        if (rs.next()){

            this.bookingId = rs.getInt("bookingId");

            if (printBill) {
                rows.add(rs.getString("firstName")+" "+rs.getString("lastName")+", ID "+rs.getInt("customerId"));
                rows.add(Main.rooms.get(rs.getInt("roomId")-1).toString());
                rows.add(Main.rooms.get(rs.getInt("roomId")-1).getPricePerDay()+" kr / night");
                rows.add("");

            }

            Timestamp tsCheckIn = rs.getTimestamp("checkinDate");
            Timestamp tsCheckout = rs.getTimestamp("checkoutDate");

            LocalDate ldtCheckin = tsCheckIn.toLocalDateTime().toLocalDate();
            
            // Om vi är i checkout preview men inte har checkat ut
            LocalDate ldtCheckout = (printBill) ? LocalDate.now() : tsCheckout.toLocalDateTime().toLocalDate();

            //daysBetween = Duration.between(ldtCheckin, ldtCheckout).toDays();
            Period diff = ldtCheckin.until(ldtCheckout);
            daysBetween = diff.getDays();

            String daysBookedString = "Days booked: " + daysBetween;
            System.out.println("\n"+daysBookedString);

            roomId = rs.getInt("roomId");
            roomCost = Main.rooms.get(roomId - 1).getPricePerDay();

            roomTotalCost = roomCost * daysBetween;
            String totalRoomString = "Total room price = " + roomTotalCost + " kr";

            System.out.println(totalRoomString);

            if (printBill) {
                rows.add(daysBookedString);
                rows.add(totalRoomString);
                rows.add("");

            }

        }
    }

    void totalFoodCost(int guestID, boolean printBill) throws SQLException {
        ResultSet rs = Main.getSqlStatement().executeQuery("SELECT foodName, foodPrice FROM allinfo WHERE customerId = "+guestID+";");

        foodCost = 0;

        String foodOrderedString = "Food ordered:";
        System.out.println("\n"+foodOrderedString);

        if (printBill) {
            rows.add(foodOrderedString);
        }

        while (rs.next()) {
            String oneFoodAndPrice = (rs.getString("foodName") == null) ?
                " --- " : "- "+rs.getString("foodName")+" : "+rs.getInt("foodPrice")+" kr";
            
            System.out.println(oneFoodAndPrice);

            foodCost += rs.getInt("foodPrice");

            if (printBill) {
                rows.add(oneFoodAndPrice);
            }
        }

        String totalFoodString = "Total food cost = "+foodCost+" kr";
        System.out.println(totalFoodString);

        if (printBill) {
            rows.add(totalFoodString);
            rows.add("");
        }
    }

    void totalCost(boolean printBill) {
        String totalAmountString = "Total amount = " + (roomTotalCost + foodCost);
        System.out.println("\n"+totalAmountString);

        if (printBill) {
            rows.add(totalAmountString);
            rows.add("");
            rows.add(LocalDate.now()+" "+LocalTime.now().withNano(0));
        }
    }

    /*

    Mio Tholerus, ID 12
    Room 4 : Standard single room
    500 KR / night

    Days booked = 3
    Total room cost = 1500

    Food ordered
    - Pizza : 80 kr
    - Pizza : 80 kr
    - Beer : 50 kr
    Total food cost = 210

    Total cost
    1710 kr

    2014-02-16 13:37:02

    */

}

