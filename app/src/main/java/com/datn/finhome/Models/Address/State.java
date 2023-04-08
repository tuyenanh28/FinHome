package com.datn.finhome.Models.Address;

public class State {
    private int stcode;
    private String stateName;

    public State(int stcode, String stateName) {
        this.stcode = stcode;
        this.stateName = stateName;
    }

    public State() {
    }

    public int getStcode() {
        return stcode;
    }

    public void setStcode(int stcode) {
        this.stcode = stcode;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }
}
