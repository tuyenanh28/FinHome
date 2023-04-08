package com.datn.finhome.Models;

public class ReviewModel {
    String idComment;
    String idUser;
    String reviews;
    String idRoom;
    String time;

    public ReviewModel() {
    }

    public ReviewModel(String idUser, String reviews, String idRoom) {
        this.idUser = idUser;
        this.reviews = reviews;
        this.idRoom = idRoom;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    //    public ReviewModel(String idUser, String trim, String id) {
//    }

    public String getIdComment() {
        return idComment;
    }

    public void setIdComment(String idComment) {
        this.idComment = idComment;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getReviews() {
        return reviews;
    }

    public void setReviews(String reviews) {
        this.reviews = reviews;
    }

    public String getIdRoom() {
        return idRoom;
    }

    public void setIdRoom(String idRoom) {
        this.idRoom = idRoom;
    }
}
