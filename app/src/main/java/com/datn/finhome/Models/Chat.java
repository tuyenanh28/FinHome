package com.datn.finhome.Models;

public class Chat {
    private String id, sender, receiver, message;
    private boolean isSeen;
    private  boolean checkMess;


    public Chat(String id, String sender, String receiver, String message,boolean isSeen) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isSeen = isSeen;
    }

    public boolean isCheckMess() {
        return checkMess;
    }

    public void setCheckMess(boolean checkMess) {
        this.checkMess = checkMess;
    }

    public Chat() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isIsSeen() {
        return isSeen;
    }

    public void isSeen(boolean isSeen) {
        this.isSeen = isSeen;
    }
}
