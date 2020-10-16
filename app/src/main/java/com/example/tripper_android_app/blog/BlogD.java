package com.example.tripper_android_app.blog;

public class BlogD {

        private int dayCount;
        private String locationId;
        private String locationName;
        private String blogNote;
        private String s_Date;
        private String blogId;
        private String tripId;
    public BlogD(String blogId,String locationId, String locationName, String blogNote,String s_Date,String tripId) {
        super();
        this.blogId = blogId;
        this.locationId = locationId;
        this.locationName = locationName;
        this.blogNote = blogNote;
        this.s_Date = s_Date;
        this.tripId = tripId;
    }
    public BlogD(String s_Date){
        this.s_Date= s_Date;
    }

    public void setBlogId(String blogId) {
        this.blogId = blogId;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }


    public String getBlogId(){
            return blogId;
    }

    public String getS_Date() {
        return s_Date;
    }

    public void setS_Date(String s_Date) {
        this.s_Date = s_Date;
    }

    public BlogD(String locationId, String locationName, String blogNote, String s_Date) {
            super();
            this.locationId = locationId;
            this.locationName = locationName;
            this.blogNote = blogNote;
            this.s_Date = s_Date;
        }
        public int getDayCount() {
            return dayCount;
        }
        public void setDayCount(int dayCount) {
            this.dayCount = dayCount;
        }
        public String getLocationId() {
            return locationId;
        }
        public void setLocationId(String locationId) {
            locationId = locationId;
        }
        public String getLocationName() {
            return locationName;
        }
        public void setLocationName(String locationName) {
            this.locationName = locationName;
        }
        public String getBlogNote() {
            return blogNote;
        }
        public void setBlogNote(String blogNote) {
            this.blogNote = blogNote;
        }




    }

