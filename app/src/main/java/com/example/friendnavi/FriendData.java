package com.example.friendnavi;

import com.google.gson.annotations.SerializedName;

public class FriendData {
    @SerializedName("friend")
    private String friendNickname;

    public FriendData(String friendNickname) {
        this.friendNickname = friendNickname;
    }

    public String getFriendNickname() {
        return friendNickname;
    }

    public void setFriendNickname(String friendNickname) {
        this.friendNickname = friendNickname;
    }

}
