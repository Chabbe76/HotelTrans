package com.company;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

/**
 *
 * @author Robban
 */
public class SaveLoadFromFile {

    public void saveData(Bill bill) {
        
        try {

            // BufferedWriter ser till att koden blir i text format
            // FileWriter skriver bara in
            BufferedWriter bw = new BufferedWriter(new FileWriter("receipt"+bill.getBookingId()+".txt"));

            for (String row : bill.rows) {
                bw.write(row);
                bw.newLine();
            }

            bw.close();
        
        } catch (Exception e) {
            System.err.println("cant save\n"+e);
        }
    }



    public void loadData(Bill bill) {
        System.out.println("\n");

        try {
            // BufferedReader ser till att koden blir till kod format
            // FileReader läser bara in
            BufferedReader br = new BufferedReader(new FileReader("receipt"+bill.getBookingId()+".txt"));
            
            String read;
            while ((read = br.readLine()) != null) {
                System.out.println(read);
            }
        
            br.close();
            
        } catch (Exception e) {
            System.err.println("\nNä\n"+e);
        }
    } 
}