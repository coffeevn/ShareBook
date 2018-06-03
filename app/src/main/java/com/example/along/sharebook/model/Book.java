package com.example.along.sharebook.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Book {
    public String bID;
    public String bName;
    public String bAuthor;
    public int bStatus;
    public String bUserSellUid;
    public String bCoverName;
    public String bDescription;
    public int bPrice;
    public String bDate;
    public int bLocate;
    public int bDistrict;

    public Book() {
    }

    public Book(String bID, String bName, String bAuthor, int bStatus, String bUserSellUid, String bCoverName, String bDescription, int bPrice, String bDate, int bLocate, int bDistrict) {
        this.bID = bID;
        this.bName = bName;
        this.bAuthor = bAuthor;
        this.bStatus = bStatus;
        this.bUserSellUid = bUserSellUid;
        this.bCoverName = bCoverName;
        this.bDescription = bDescription;
        this.bPrice = bPrice;
        this.bDate = bDate;
        this.bLocate = bLocate;
        this.bDistrict = bDistrict;
    }
}
