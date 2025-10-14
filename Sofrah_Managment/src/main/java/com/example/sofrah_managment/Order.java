package com.example.sofrah_managment;

import java.util.Date;

public class Order {
    private int odrerID;
    private Date odrerDate;


    public Order (){}

    public Order(int odrerID, Date odrerDate) {
        this.odrerID = odrerID;
        this.odrerDate = odrerDate;
    }

    public int getOdrerID() {
        return odrerID;
    }

    public void setOdrerID(int odrerID) {
        this.odrerID = odrerID;
    }

    public Date getOdrerDate() {
        return odrerDate;
    }

    public void setOdrerDate(Date odrerDate) {
        this.odrerDate = odrerDate;
    }

    @Override
    public String toString() {
        return "Order{" +
                "odrerID=" + odrerID +
                ", odrerDate=" + odrerDate +
                '}';
    }
}