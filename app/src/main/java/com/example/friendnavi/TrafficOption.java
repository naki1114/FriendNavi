package com.example.friendnavi;

public class TrafficOption {

    String option;      // 옵션
    String timeArrive;  // 도착 시간
    String duration;       // 소요 시간
    String distance;       // 거리
    String tollFare;       // 톨비

    public TrafficOption (String option, String timeArrive, String duration, String distance, String tollFare) {
        this.option = option;
        this.timeArrive = timeArrive;
        this.duration = duration;
        this.distance = distance;
        this.tollFare = tollFare;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getTimeArrive() {
        return timeArrive;
    }

    public void setTimeArrive(String timeArrive) {
        this.timeArrive = timeArrive;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getTollFare() {
        return tollFare;
    }

    public void setTollFare(String tollFare) {
        this.tollFare = tollFare;
    }

}
