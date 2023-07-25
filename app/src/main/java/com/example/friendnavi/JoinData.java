package com.example.friendnavi;

import com.google.gson.annotations.SerializedName;

public class JoinData {
    @SerializedName("id")
    private String id;

    @SerializedName("nickname")
    private String nickname;

    @SerializedName("password")
    private String password;

    @SerializedName("passwordRe")
    private String passwordRe;

    @SerializedName("phone")
    private String phone;

    @SerializedName("addHome")
    private String addHome;

    @SerializedName("addComp")
    private String addComp;

    public JoinData(String id, String nickname, String password, String passwordRe, String phone, String addHome, String addComp) {
        this.id = id;
        this.nickname = nickname;
        this.password = password;
        this.passwordRe = passwordRe;
        this.phone = phone;
        this.addHome = addHome;
        this.addComp = addComp;
    }
}