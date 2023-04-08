package com.datn.finhome.PayHistory;

public class PayHistory {
    private String idPay,idRoom, idUser, title, payTime, money;


    public PayHistory(String idRoom, String idUser, String title, String money) {
        this.idRoom = idRoom;
        this.idUser = idUser;
        this.title = title;
        this.money = money;
    }

    public PayHistory() {
    }

    public String getIdPay() {
        return idPay;
    }


    public void setIdPay(String idPay) {
        this.idPay = idPay;
    }

    public String getIdRoom() {
        return idRoom;
    }

    public void setIdRoom(String idRoom) {
        this.idRoom = idRoom;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

//    public String getUserName() {
//        return userName;
//    }
//
//    public void setUserName(String userName) {
//        this.userName = userName;
//    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
