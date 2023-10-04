package com.example.friendnavi;

import com.google.gson.annotations.SerializedName;

public class ChattingDataFromServer {

    @SerializedName("roomNo")
    String roomNo;

    @SerializedName("time")
    String savedTime;

    @SerializedName("content")
    String savedContent;

    @SerializedName("sendUser")
    String sendUser;

    public ChattingDataFromServer(String roomNo, String savedTime, String savedContent, String sendUser) {
        this.roomNo = roomNo;
        this.savedTime = savedTime;
        this.savedContent = savedContent;
        this.sendUser = sendUser;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public String getSavedTime() {
        return savedTime;
    }

    public void setSavedTime(String savedTime) {
        this.savedTime = savedTime;
    }

    public String getSavedContent() {
        return savedContent;
    }

    public void setSavedContent(String savedContent) {
        this.savedContent = savedContent;
    }

    public String getSendUser() {
        return sendUser;
    }

    public void setSendUser(String sendUser) {
        this.sendUser = sendUser;
    }

}
