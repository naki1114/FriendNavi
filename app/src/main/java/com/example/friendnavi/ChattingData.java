package com.example.friendnavi;

public class ChattingData {

    String time;
    String msg;
    boolean type;

    public ChattingData(String time, String msg, boolean type) {
        this.time = time;
        this.msg = msg;
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean getType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }

}
