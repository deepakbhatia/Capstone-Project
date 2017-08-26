package com.obelix.receiptsbox;

public class Receipt  {

    public String cloud_id;

    public long _id;
    public String title;
    public long date;
    public String place;
    public String type;

    public double amount;
    public int card_payment;
    public int archived;
    public int deleted;

    public long priority;
    public Receipt(){

    }

    public Receipt(long _id, String type, String title, long date, String place, double amount, int card_payment) {
        this._id = _id;
        this.type = type;
        this.title = title;

        this.date = date;
        this.place = place;
        this.amount = amount;
        this.card_payment = card_payment;
    }


}
