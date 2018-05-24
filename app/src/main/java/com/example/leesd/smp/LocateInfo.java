package com.example.leesd.smp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LocateInfo {

    @SerializedName("placeTitle")
    @Expose
    private String placeTitle;
    @SerializedName("placeLocation")
    @Expose
    private CharSequence placeLocation;
    @SerializedName("placeLatitude")
    @Expose
    public Double placeLatitude;
    @SerializedName("placeLongitude")
    @Expose
    public Double placeLongitude;
    @SerializedName("placeID")
    @Expose
    public String placeID;



    /**
     *
     * @param placeTitle
     * The placeTitle
     */
    public void setTitle(String placeTitle) {
        this.placeTitle = placeTitle;
    }

    /**
     *
     * @param placeLocation
     * The placeLocation
     */
    public void setLocation(CharSequence placeLocation) {
        this.placeLocation = placeLocation;
    }

    /**
     *
     * @param placeLatitude
     * The placeLatitude
     */
    public void setLatitude(double placeLatitude) {
        this.placeLatitude = placeLatitude;
    }

    /**
     *
     * @param placeLongitude
     * The placeLongitude
     */
    public void setLongitude(double placeLongitude) {
        this.placeLongitude = placeLongitude;
    }

    /**
     *
     * @param placeID
     * The placeID
     */
    public void setID(String placeID) {
        this.placeID = placeID;
    }


    /**
     *
     * @return
     * The placeTitle
     */
    public String getTitle() {
        return placeTitle;
    }

    /**
     *
     * @return
     * The placeLocation
     */
    public CharSequence getLocation() {
        return placeLocation;
    }

    /**
     *
     * @return
     * The placeLatitude
     */
    public double getLatitude() {
        return placeLatitude;
    }

    /**
     *
     * @return
     * The placeLongitude
     */
    public double getLongitude() {
        return placeLongitude;
    }

    /**
     *
     * @return
     * The placeID
     */
    public String getID() {
        return placeID;
    }

}
