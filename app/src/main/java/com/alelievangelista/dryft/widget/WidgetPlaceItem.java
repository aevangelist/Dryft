package com.alelievangelista.dryft.widget;

/**
 * Created by aevangelista on 15-12-02.
 */
public class WidgetPlaceItem {
    private String placeName;
    private String placeCategory;
    private String placeAddress;
    private String placePhone;

    public WidgetPlaceItem(){
    }

    public WidgetPlaceItem(String name, String category, String address, String phone){
        this.placeName = name;
        this.placeCategory = category;
        this.placeAddress = address;
        this.placePhone = phone;
    }

    public String getPlaceName(){
        return placeName;
    }

    public String getPlaceCategory(){
        return placeCategory;
    }

    public String getPlaceAddress(){
        return placeAddress;
    }

    public String getPlacePhone(){
        return placePhone;
    }

    public void setPlaceName(String s){
        this.placePhone = s;
    }

    public void setPlaceCategory(String s){
        this.placeCategory = s;
    }

    public void setPlaceAddress(String s){
        this.placeAddress = s;
    }

    public void setPlacePhone(String s){
        this.placePhone = s;
    }

}
