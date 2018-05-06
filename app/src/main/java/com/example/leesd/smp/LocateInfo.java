package com.example.leesd.smp;

public class LocateInfo {
    private String placeTitle;
    private CharSequence placeLocation;
    public Double placeLatitude, placeLongitude;
    public String placeID;


    //setter
    public void setTitle(String title) {
        placeTitle = title;
    }
    public void setLocation(CharSequence location) {
        placeLocation = location;
    }
    public void setLatitude(double latitude) {
        placeLatitude = latitude;
    }
    public void setLongitude(double longitude) {
        placeLongitude = longitude;
    }
    public void setID(String id) {
        placeID = id;
    }

    //getter
    public String getTitle() {
        return this.placeTitle;
    }
    public CharSequence getLocation() {
        return this.placeLocation;
    }
    public double getLatitude() {
        return this.placeLatitude;
    }
    public double getLongitude() {
        return this.placeLongitude;
    }
    public String getID() {
        return this.placeID;
    }

}
