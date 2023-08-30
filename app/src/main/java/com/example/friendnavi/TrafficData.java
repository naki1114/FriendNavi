package com.example.friendnavi;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class TrafficData implements Serializable {

    private static final long serialVersionUID = 1L;

    @SerializedName("code")
    int code;

    @SerializedName("message")
    String message;

    @SerializedName("currentDateTime")
    String currentDateTime;

    @SerializedName("route")
    Route route;

    public TrafficData(int code, String message, String currentDateTime, Route route) {
        this.code = code;
        this.message = message;
        this.currentDateTime = currentDateTime;
        this.route = route;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCurrentDateTime() {
        return currentDateTime;
    }

    public void setCurrentDateTime(String currentDateTime) {
        this.currentDateTime = currentDateTime;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public class Route implements Serializable {

        // 실시간 빠른길
        @SerializedName("trafast")
        ArrayList<Trafast> trafast;

        // 실시간 편한길
        @SerializedName("tracomfort")
        ArrayList<Tracomfort> tracomfort;

        // 실시간 최적
        @SerializedName("traoptimal")
        ArrayList<Traoptimal> traoptimal;

        public ArrayList<Trafast> getTrafast() {
            return trafast;
        }

        public void setTrafast(ArrayList<Trafast> trafast) {
            this.trafast = trafast;
        }

        public ArrayList<Tracomfort> getTracomfort() {
            return tracomfort;
        }

        public void setTracomfort(ArrayList<Tracomfort> tracomfort) {
            this.tracomfort = tracomfort;
        }

        public ArrayList<Traoptimal> getTraoptimal() {
            return traoptimal;
        }

        public void setTraoptimal(ArrayList<Traoptimal> traoptimal) {
            this.traoptimal = traoptimal;
        }

    }

    public class Option implements Serializable {

        @SerializedName("summary")
        Summary summary;

        @SerializedName("path")
        double[][] path;

        @SerializedName("section")
        ArrayList<Section> section;

        @SerializedName("guide")
        ArrayList<Guide> guide;

        public Option(Summary summary, double[][] path, ArrayList<Section> section, ArrayList<Guide> guide) {
            this.summary = summary;
            this.path = path;
            this.section = section;
            this.guide = guide;
        }

        public Summary getSummary() {
            return summary;
        }

        public void setSummary(Summary summary) {
            this.summary = summary;
        }

        public double[][] getPath() {
            return path;
        }

        public void setPath(double[][] path) {
            this.path = path;
        }

        public ArrayList<Section> getSection() {
            return section;
        }

        public void setSection(ArrayList<Section> section) {
            this.section = section;
        }

        public ArrayList<Guide> getGuide() {
            return guide;
        }

        public void setGuide(ArrayList<Guide> guide) {
            this.guide = guide;
        }

    }

    public class Trafast extends Option {

        public Trafast(Summary summary, double[][] path, ArrayList<Section> section, ArrayList<Guide> guide) {
            super(summary, path, section, guide);
        }

    }

    public class Tracomfort extends Option {

        public Tracomfort(Summary summary, double[][] path, ArrayList<Section> section, ArrayList<Guide> guide) {
            super(summary, path, section, guide);
        }

    }

    public class Traoptimal extends Option {

        public Traoptimal(Summary summary, double[][] path, ArrayList<Section> section, ArrayList<Guide> guide) {
            super(summary, path, section, guide);
        }

    }

    public class Summary implements Serializable {

        @SerializedName("distance")
        int distance;

        @SerializedName("duration")
        int duration;

        @SerializedName("etaServiceType")
        int etaServiceType;

        @SerializedName("departureTime")
        String departureTime;

        @SerializedName("tollFare")
        int tollFare;

        @SerializedName("taxiFare")
        int taxiFare;

        @SerializedName("fuelPrice")
        int fuelPrice;

        public Summary (int distance, int duration, int etaServiceType, String departureTime, int tollFare, int taxiFare, int fuelPrice) {
            this.distance = distance;
            this.duration = duration;
            this.etaServiceType = etaServiceType;
            this.departureTime = departureTime;
            this.tollFare = tollFare;
            this.taxiFare = taxiFare;
            this.fuelPrice = fuelPrice;
        }

        public int getDistance() {
            return distance;
        }

        public void setDistance(int distance) {
            this.distance = distance;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public int getEtaServiceType() {
            return etaServiceType;
        }

        public void setEtaServiceType(int etaServiceType) {
            this.etaServiceType = etaServiceType;
        }

        public String getDepartureTime() {
            return departureTime;
        }

        public void setDepartureTime(String departureTime) {
            this.departureTime = departureTime;
        }

        public int getTollFare() {
            return tollFare;
        }

        public void setTollFare(int tollFare) {
            this.tollFare = tollFare;
        }

        public int getTaxiFare() {
            return taxiFare;
        }

        public void setTaxiFare(int taxiFare) {
            this.taxiFare = taxiFare;
        }

        public int getFuelPrice() {
            return fuelPrice;
        }

        public void setFuelPrice(int fuelPrice) {
            this.fuelPrice = fuelPrice;
        }

    }

    public class Section implements Serializable {

        @SerializedName("pointIndex")
        int pointIndex;

        @SerializedName("pointCount")
        int pointCount;

        @SerializedName("distance")
        int distance;

        @SerializedName("name")
        String name;

        @SerializedName("congestion")
        int congestion;

        @SerializedName("speed")
        int speed;

        public Section (int pointIndex, int pointCount, int distance, String name, int congestion, int speed) {
            this.pointIndex = pointIndex;
            this.pointCount = pointCount;
            this.distance = distance;
            this.name = name;
            this.congestion = congestion;
            this.speed = speed;
        }

        public int getPointIndex() {
            return pointIndex;
        }

        public void setPointIndex(int pointIndex) {
            this.pointIndex = pointIndex;
        }

        public int getPointCount() {
            return pointCount;
        }

        public void setPointCount(int pointCount) {
            this.pointCount = pointCount;
        }

        public int getDistance() {
            return distance;
        }

        public void setDistance(int distance) {
            this.distance = distance;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getCongestion() {
            return congestion;
        }

        public void setCongestion(int congestion) {
            this.congestion = congestion;
        }

        public int getSpeed() {
            return speed;
        }

        public void setSpeed(int speed) {
            this.speed = speed;
        }

    }

    public class Guide implements Serializable {

        @SerializedName("pointIndex")
        int pointIndex;

        @SerializedName("type")
        int type;

        @SerializedName("instructions")
        String instructions;

        @SerializedName("distance")
        int distance;

        @SerializedName("duration")
        int duration;

        public Guide (int pointIndex, int type, String instructions, int distance, int duration) {
            this.pointIndex = pointIndex;
            this.type = type;
            this.instructions = instructions;
            this.distance = distance;
            this.duration = duration;
        }

        public int getPointIndex() {
            return pointIndex;
        }

        public void setPointIndex(int pointIndex) {
            this.pointIndex = pointIndex;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getInstructions() {
            return instructions;
        }

        public void setInstructions(String instructions) {
            this.instructions = instructions;
        }

        public int getDistance() {
            return distance;
        }

        public void setDistance(int distance) {
            this.distance = distance;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

    }

}
