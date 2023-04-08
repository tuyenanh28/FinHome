package com.datn.finhome.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class RoomModel  implements Serializable{
    String id;
    String title;
    String address;
    String description;
    String img;
    String name;
    String price;
    String sizeRoom;
    String uid;
    String Time;
    boolean Browser;

    public boolean isBrowser() {
        return Browser;
    }

    public void setBrowser(boolean browser) {
        Browser = browser;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public RoomModel() {
    }

    public RoomModel(String id, String title, String address, String description, String img, String name, String price, String sizeRoom, String uid, String time, boolean browser) {
        this.id = id;
        this.title = title;
        this.address = address;
        this.description = description;
        this.img = img;
        this.name = name;
        this.price = price;
        this.sizeRoom = sizeRoom;
        this.uid = uid;
        Time = time;
        Browser = browser;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSizeRoom() {
        return sizeRoom;
    }

    public void setSizeRoom(String sizeRoom) {
        this.sizeRoom = sizeRoom;
    }
}

