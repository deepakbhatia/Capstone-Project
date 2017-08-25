package com.obelix.receiptsbox;

public class Receipt  {

    public String cloud_id;

    public String receipt_id;
    public String title;
    public String date;
    public String place;
    public String type;

    public double amount;
    public int card_payment;
    public int archived;
    public int deleted;

    public long priority;
    public Receipt(){

    }

    public Receipt(String _id, String type, String title, String date, String place, double amount, int card_payment) {
        this.receipt_id = _id;
        this.type = type;
        this.title = title;

        this.date = date;
        this.place = place;
        this.amount = amount;
        this.card_payment = card_payment;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }



}
