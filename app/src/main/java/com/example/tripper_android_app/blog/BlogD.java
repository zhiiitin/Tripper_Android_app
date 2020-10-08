package com.example.tripper_android_app.blog;

public class BlogD {

        private int dayCount;
        private int locationId;
        private String locationName;
        private String blogNote;
        private String s_Date;
        private int blogId;
        private int tripId;
    public BlogD(int blogId,int locationId, String locationName, String blogNote,String s_Date,int tripId) {
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

    public void setBlogId(int blogId) {
        this.blogId = blogId;
    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public BlogD(int dayCount, int locationId, String locationName, String blogNote) {
            super();
            this.dayCount = dayCount;
            this.locationId = locationId;
            this.locationName = locationName;
            this.blogNote = blogNote;
        }
    public int getBlogId(){
            return blogId;
    }

    public String getS_Date() {
        return s_Date;
    }

    public void setS_Date(String s_Date) {
        this.s_Date = s_Date;
    }

    public BlogD(int locationId, String locationName, String blogNote, String s_Date) {
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
        public int getLocationId() {
            return locationId;
        }
        public void setLocationId(int locationId) {
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

