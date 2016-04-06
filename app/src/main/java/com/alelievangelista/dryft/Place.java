package com.alelievangelista.dryft;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by aevangelista on 16-04-04.
 */
public class Place implements Parcelable {

    private String id;
    private String name;
    private String desc;
    private String address;
    private String phone;
    private String website;


    public Place (String i, String n, String d, String a, String p, String w) {
        this.id = i;
        this.name = n;
        this.desc = d;
        this.address = a;
        this.phone = p;
        this.website = w;
    }

    @Override
    public String toString() {
        return "Place ID: " + this.id + "\n" +
                "Name: " + this.name  + "\n"
                + "Desc: " + this.desc + "\n" +
                "Address: " + this.address + "\n" +
                "Phone: " + this.phone + "\n" +
                "Website: " + this.website;


    }

    /**
     * Getters and Setters
     */


    /**
     * For use with Parcelable creator
     */
    private Place(Parcel in){
        id = in.readString();
        name = in.readString();
        desc = in.readString();
        address = in.readString();
        phone = in.readString();
        website = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel p, int flags) {
        p.writeString(id);
        p.writeString(name);
        p.writeString(desc);
        p.writeString(address);
        p.writeString(phone);
        p.writeString(website);
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
