package com.example.tripper_android_app.trip;

import java.io.Serializable;

public class TripGroup implements Serializable {
    private static final long serialVersionUID = 1L;
    private String groupTransId;
    private String tripId;
    private String createDateTime;
    private int memberId;




    public TripGroup(String groupTransId, String tripId, int memberId) {
        super();
        this.groupTransId = groupTransId;
        this.tripId = tripId;
        this.memberId = memberId;
    }


    public TripGroup(String tripId, int memberId) {
        super();
        this.tripId = tripId;
        this.memberId = memberId;
    }

    public String getGroupTransId() {
        return groupTransId;
    }

    public void setGroupTransId(String groupTransId) {
        this.groupTransId = groupTransId;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(String createDateTime) {
        this.createDateTime = createDateTime;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

}
