package com.example.tripper_android_app.group;

import java.io.Serializable;

/**
 * 類別說明：Group_Bean
 * @author zhitin
 * @version 建立時間:Sep 9, 2020
 *
 */

public class Group implements Serializable {
    private String TRIP_ID ;
    private String TRIP_TITLE ;
    private String S_DATE ;
    private int P_MAX ;

    public Group (){
        super();
    }

    public String getTRIP_ID() {
        return TRIP_ID;
    }

    public void setTRIP_ID(String TRIP_ID) {
        this.TRIP_ID = TRIP_ID;
    }

    public String getTRIP_TITLE() {
        return TRIP_TITLE;
    }

    public void setTRIP_TITLE(String TRIP_TITLE) {
        this.TRIP_TITLE = TRIP_TITLE;
    }

    public String getS_DATE() {
        return S_DATE;
    }

    public void setS_DATE(String s_DATE) {
        S_DATE = s_DATE;
    }

    public int getP_MAX() {
        return P_MAX;
    }

    public void setP_MAX(int p_MAX) {
        P_MAX = p_MAX;
    }

    public int getP_COUNT() {
        return P_COUNT;
    }

    public void setP_COUNT(int p_COUNT) {
        P_COUNT = p_COUNT;
    }

    private int P_COUNT ;

}
