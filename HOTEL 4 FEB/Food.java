package com.company;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Food implements QueryObject<Food> {

    private static List<Food> foods;
    private static List<Food> drinks;
    
    private int foodID;
    private String name;
    private String type; // dish, side, drink
    private int price;
    
    Food(int i, String n, String t, int p) {
        foodID = i;
        name = n;
        type = t;
        price = p;
    }
    
    // En konstrukur utan type OM vi väljer att slopa det
    Food(int i, String n, int p) {
        foodID = i;
        name = n;
        price = p;
    }

    public int getId() {
        return foodID;
    }
    public String getName() {
        return name;
    }
    public String getType() {
        return type;
    }
    public int getPrice() {
        return price;
    }

    public static void prepareMenu() throws SQLException { // körs en gång i början av programmet
        
        // Delar upp elementen efter typ food/drink
        foods = new ArrayList<>();
        drinks = new ArrayList<>();

        ResultSet rs = Main.getSqlStatement().executeQuery("SELECT * FROM food;");

        while (rs.next()) {
            Food f = new Food(rs.getInt("foodId"), rs.getString("foodName"), rs.getString("foodType"), rs.getInt("foodPrice"));
            if (rs.getString("foodType").equals("Food")) {
                foods.add(f);
            } else {
                drinks.add(f);
            }   
        }
    }

    public static void printMenu() {
        
        System.out.println(
            "\n=================================================="+
            "\n               -:: HOTEL MENU ::-                 "+
            "\n=================================================="
        );
        
        int maxRows = (foods.size() > drinks.size()) ? foods.size() : drinks.size();
        
        System.out.println(Main.PadRight("\nFoods & Snacks", 30)+" Drinks & Refreshers\n");
        for (int i = 0; i < maxRows; i++) {
            try {
                String nameA = foods.get(i).getName();
                int priceA = foods.get(i).getPrice();
                System.out.print(Main.PadRight(nameA+" : "+priceA+" KR", 30));
            } catch (IndexOutOfBoundsException e) {
                System.out.print(Main.PadRight("", 30));
            }
            try {
                String nameB = drinks.get(i).getName();
                int priceB = drinks.get(i).getPrice();
                System.out.print(nameB+" : "+priceB+" KR\n");
            } catch (IndexOutOfBoundsException e) {
                System.out.print("\n");
            }

        }
    }

    

    @Override
    public Food construct(int id) throws SQLException {
        ResultSet rs = Main.sqlStatement.executeQuery("SELECT * FROM food; WHERE foodId = "+id);

        if (rs.next()) {
            
            foodID = rs.getInt("foodId");
            name = rs.getString("foodName");
            type = rs.getString("foodType");
            price = rs.getInt("foodPrice");
        }
        
        return this;
    }

    
}
