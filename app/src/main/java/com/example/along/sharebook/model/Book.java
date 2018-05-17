package com.example.along.sharebook.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Book {
    public String bName;
    public String bAuthor;
    public int bStatus;
    public String bUserSellUid;
    public String bCoverName;
    public String bDescription;
    public int bPrice;

    public Book() {
    }

    public Book(String bName, String bAuthor, int bStatus, String bUserSellUid, String bCoverName, String bDescription, int bPrice) {
        this.bName = bName;
        this.bAuthor = bAuthor;
        this.bStatus = bStatus;
        this.bUserSellUid = bUserSellUid;
        this.bCoverName = bCoverName;
        this.bDescription = bDescription;
        this.bPrice = bPrice;
    }
}
