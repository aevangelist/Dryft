package com.alelievangelista.dryft.ui;

/**
 * Created by aevangelista on 16-04-29.
 */
public class Hour {

    private String id;
    private String day;
    private String hours;

    public Hour (String i, String d, String h) {
        this.id = i;
        this.day = d;
        this.hours = h;
    }

    public Hour() {

    }

    @Override
    public String toString() {
        return "Place ID: " + this.id + "\n" +
                "Day: " + this.day  + "\n" +
                "Hours: " + this.hours;

    }

    public String getHours(){
        return this.hours;
    }

    public String getDay(){
        return this.day;
    }
}
