package com.example.leesd.smp.DetailSearch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by leesd on 2018-05-06.
 */

public class AddressComponent {

    @SerializedName("long_name")
    @Expose
    private String longName;
    @SerializedName("short_name")
    @Expose
    private String shortName;
    @SerializedName("types")
    @Expose
    private List<String> types = null;

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

}
