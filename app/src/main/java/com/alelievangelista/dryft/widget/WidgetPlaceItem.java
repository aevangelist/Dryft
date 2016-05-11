package com.alelievangelista.dryft.widget;

/**
 * Created by aevangelista on 15-12-02.
 */
public class WidgetPlaceItem {
    private String placeName;
    private String placeCategory;
    private String placeAddress;
    private String placePhone;
    private String placePhoto;

    public WidgetPlaceItem(){
    }

    public WidgetPlaceItem(String name, String category, String address, String phone, String photo){
        this.placeName = name;
        this.placeCategory = category;
        this.placeAddress = address;
        this.placePhone = phone;
        this.placePhoto = photo;
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

    public String getPlacePhoto(){
        return placePhoto;
    }

    public void setPlaceName(String s){
        this.placeName = s;
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

    public void setPlacePhoto(String s){
        this.placePhoto = s;
    }


}
