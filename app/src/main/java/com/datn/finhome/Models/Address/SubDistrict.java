package com.datn.finhome.Models.Address;

public class SubDistrict {
    private int sdiscode;
    private String sdisName;
    private int discode;

    public SubDistrict(int sdiscode, String sdisName, int discode) {
        this.sdiscode = sdiscode;
        this.sdisName = sdisName;
        this.discode = discode;
    }

    public SubDistrict() {
    }

    public int getSdiscode() {
        return sdiscode;
    }

    public void setSdiscode(int sdiscode) {
        this.sdiscode = sdiscode;
    }

    public String getSdisName() {
        return sdisName;
    }

    public void setSdisName(String sdisName) {
        this.sdisName = sdisName;
    }

    public int getDiscode() {
        return discode;
    }

    public void setDiscode(int discode) {
        this.discode = discode;
    }
}
