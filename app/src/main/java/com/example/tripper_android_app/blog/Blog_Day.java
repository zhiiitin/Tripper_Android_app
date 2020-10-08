package com.example.tripper_android_app.blog;

import java.io.Serializable;

public class Blog_Day implements Serializable {
    String dayTitle ;

    public Blog_Day(String dayTitle) {
        this.dayTitle = dayTitle;
    }

    public String getDayTitle() {
        return dayTitle;
    }

    public void setDayTitle(String dayTitle) {
        this.dayTitle = dayTitle;
    }
}
