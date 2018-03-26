package com.example.leesd.smp;

import android.graphics.drawable.Drawable;

/**
 * Created by leesd on 2018-03-23.
 */

public class ListViewItem {
    private Drawable iconDrawble;
    private String title;
    private String address;

    public ListViewItem(){}
    public ListViewItem(Drawable iconDrawble, String title, String address) {
        this.iconDrawble = iconDrawble;
        this.title = title;
        this.address = address;
    }

    public Drawable getIcon() {
        return iconDrawble;
    }

    public void setIcon(Drawable iconDrawble) {
        this.iconDrawble = iconDrawble;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
