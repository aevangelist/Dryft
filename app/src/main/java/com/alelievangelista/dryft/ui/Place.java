package com.alelievangelista.dryft.ui;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by aevangelista on 16-04-04.
 */

/**
 * This is a mini object that I will use so I can grab pertinent information about places
 */
public class Place implements Parcelable {

    private String id;
    private String name;
    private String address;
    private String phone;
    private String website;
    private String latitude;
    private String longitude;
    private String category;
    private String photo;

    public Place (String i, String n, String p,String a, String cat,
    String lat, String lon, String photo) {
        this.id = i;
        this.name = n;
        this.phone = p;
        this.address = a;
        this.category = cat;
        this.latitude = lat;
        this.longitude = lon;
        this.photo = photo;
    }

    public Place() {

    }

    @Override
    public String toString() {
        return "Place ID: " + this.id + "\n" +
                "Name: " + this.name  + "\n" +
                "Address: " + this.address + "\n" +
                "Phone: " + this.phone + "\n" +
                "Coordinates: " + this.latitude + ", " + this.longitude + "\n" +
                "Category: " + this.category + "\n" +
                "Photo: " + this.photo;


    }

    /**
     * Getters and Setters
     */

    public String getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public String getLatitude(){
        return this.latitude;
    }

    public String getLongitude(){
        return this.longitude;
    }


    /**
     * For use with Parcelable creator
     */
    private Place(Parcel in){
        id = in.readString();
        name = in.readString();
        phone = in.readString();
        address = in.readString();
        phone = in.readString();
        website = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        category = in.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel p, int flags) {
        p.writeString(id);
        p.writeString(name);
        p.writeString(phone);
        p.writeString(address);
        p.writeString(phone);
        p.writeString(website);
        p.writeString(latitude);
        p.writeString(longitude);
        p.writeString(category);
    }

    /**
     * Receiving and decoding parcels
     */
    public final Parcelable.Creator<Place> CREATOR = new Parcelable.Creator<Place>(){

        @Override
        public Place createFromParcel(Parcel parcel) {
            return new Place(parcel);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };
}
