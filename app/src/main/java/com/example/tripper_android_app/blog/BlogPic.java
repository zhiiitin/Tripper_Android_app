package com.example.tripper_android_app.blog;

import java.io.Serializable;

public class BlogPic implements Serializable {
    private String blogId ;
    private String locId ;
    private String pic1 ;
    private String pic2 ;
    private String pic3 ;
    private String pic4 ;

    public BlogPic() {
        super();
    }


    public BlogPic(String pic1, String pic2, String pic3, String pic4,String blogId) {
        this.pic1 = pic1;
        this.pic2 = pic2;
        this.pic3 = pic3;
        this.pic4 = pic4;
    }

    public BlogPic(String blogId, String locId, String pic1, String pic2, String pic3, String pic4) {
        this.blogId = blogId;
        this.locId = locId;
        this.pic1 = pic1;
        this.pic2 = pic2;
        this.pic3 = pic3;
        this.pic4 = pic4;
    }



    public String getBlogId() {
        return blogId;
    }

    public void setBlogId(String blogId) {
        this.blogId = blogId;
    }

    public String getLocId() {
        return locId;
    }

    public void setLocId(String locId) {
        this.locId = locId;
    }

    public String getPic1() {
        return pic1;
    }

    public void setPic1(String pic1) {
        this.pic1 = pic1;
    }

    public String getPic2() {
        return pic2;
    }

    public void setPic2(String pic2) {
        this.pic2 = pic2;
    }

    public String getPic3() {
        return pic3;
    }

    public void setPic3(String pic3) {
        this.pic3 = pic3;
    }

    public String getPic4() {
        return pic4;
    }

    public void setPic4(String pic4) {
        this.pic4 = pic4;
    }
}
