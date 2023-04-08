package com.datn.finhome.Models.Address;

public class District {
    private int discode;
    private String disName;
    private int stcode;

    public District(int discode, String disName, int stcode) {
        this.discode = discode;
        this.disName = disName;
        this.stcode = stcode;
    }

    public District(int discode, String disName) {
        this.discode = discode;
        this.disName = disName;
    }

    public District() {
    }

    public int getDiscode() {
        return discode;
    }

    public void setDiscode(int discode) {
        this.discode = discode;
    }

    public String getDisName() {
        return disName;
    }

    public void setDisName(String disName) {
        this.disName = disName;
    }

    public int getStcode() {
        return stcode;
    }

    public void setStcode(int stcode) {
        this.stcode = stcode;
    }
}
