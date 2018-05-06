package com.example.leesd.smp;

import android.graphics.drawable.Drawable;
import android.net.Uri;

/**
 * Created by leesd on 2018-03-23.
 */

public class ListViewItem {
    private String iconDrawbleUrl;
    private String title;
    private String address;

    public ListViewItem(){}
    public ListViewItem(String iconDrawbleUrl, String title, String address) {
        this.iconDrawbleUrl = iconDrawbleUrl;
        this.title = title;
        this.address = address;
    }

    public String getIcon() {
        return iconDrawbleUrl;
    }

    public void setIcon(String iconDrawble) {
        this.iconDrawbleUrl = iconDrawble;
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
