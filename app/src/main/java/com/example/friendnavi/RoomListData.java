package com.example.friendnavi;

public class RoomListData {

    String roomNumber;  // 방 번호 (난수)
    String roomName;    // 방 이름
    String content;     // 마지막 메신져 내용
    String time;        // 시간

    public RoomListData(String roomNumber, String roomName, String content, String time) {
        this.roomNumber = roomNumber;
        this.roomName = roomName;
        this.content = content;
        this.time = time;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
