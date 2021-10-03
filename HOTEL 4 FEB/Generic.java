
package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Scanner;

public class Generic <T>{
    
    T objekt;

    public Generic(T objekt) {
        this.objekt = objekt;
    }

    public Generic() {
    }

    public T getObjekt() {
        return this.objekt;
    }

    public void setObjekt(T objekt) {
        this.objekt = objekt;
    }
    
    
    
    






}