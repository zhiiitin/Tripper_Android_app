package com.example.tripper_android_app.blog;

public class Blog_Comment {

    String blogId;
    String name;
    String content;
    String member_ID;
    int ivImage;
    String date;
    int comId;


    public int getComId() {
        return comId;
    }

    public void setComId(int comId) {
        this.comId = comId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Blog_Comment(){

    }
    public Blog_Comment(String name, String content,String member_ID,String date,int comID){

        this.name = name;
        this.content = content;
        this.member_ID = member_ID;
        this.date = date;
        this.comId = comID;

    }
    public Blog_Comment(String blogId,String name, String content,String member_ID){
        this.blogId= blogId;
        this.name = name;
        this.content = content;
        this.member_ID = member_ID;
    }
    public Blog_Comment(String name, String content,String member_ID){

        this.name = name;
        this.content = content;
        this.member_ID = member_ID;
    }
    public Blog_Comment(String blogId,String name, String content,String member_ID,String date , int comID){
        this.blogId= blogId;
        this.name = name;
        this.content = content;
        this.member_ID = member_ID;
        this.comId = comID;
        this.date  = date;

    }



    public Blog_Comment(String name, String content,int ivImage){
        this.name = name;
        this.content = content;
        this.ivImage= ivImage;
    }

    public String getBlogId() {
        return blogId;
    }

    public void setBlogId(String blogId) {
        this.blogId = blogId;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public int getIvImage() {
        return ivImage;
    }

    public void setIvImage(int ivImage) {
        this.ivImage = ivImage;
    }

    public String getMember_ID() {
        return member_ID;
    }

    public void setMember_ID(String member_ID) {
        this.member_ID = member_ID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}