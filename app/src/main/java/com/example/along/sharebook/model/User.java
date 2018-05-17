package com.example.along.sharebook.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class User {
    public String username;
    public String sdt;
    public int khuVuc;
    public int quan;
    public String moTa;
    public String hinhDaiDien;
    public ArrayList<String> curSell;

    public User() {
    }

    public User(String username, String sdt, int khuVuc, int quan, String moTa, String hinhDaiDien, ArrayList<String> curSell) {
        this.username = username;
        this.sdt = sdt;
        this.khuVuc = khuVuc;
        this.quan = quan;
        this.moTa = moTa;
        this.hinhDaiDien = hinhDaiDien;
        this.curSell = curSell;
    }

}
