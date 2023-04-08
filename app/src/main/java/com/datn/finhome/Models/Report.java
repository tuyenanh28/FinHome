package com.datn.finhome.Models;

public class Report {
    String idReport;
    String idUser;
    String report;
    String idRoom;

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public Report() {
    }

    public Report( String idUser, String report, String idRoom) {
        this.idUser = idUser;
        this.report = report;
        this.idRoom = idRoom;
    }

    public String getIdReport() {
        return idReport;
    }

    public void setIdReport(String idReport) {
        this.idReport = idReport;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }



    public String getIdRoom() {
        return idRoom;
    }

    public void setIdRoom(String idRoom) {
        this.idRoom = idRoom;
    }
}
