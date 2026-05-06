package com.traintickets.models;

import java.io.Serializable;

/**
 * Модель данных поезда
 */
public class Train implements Serializable {

    private String trainNumber;   // Номер поезда (например, "001А")
    private String trainType;     // Тип: "Скорый", "Фирменный", "Пассажирский"
    private String fromStation;   // Станция отправления
    private String toStation;     // Станция назначения
    private String departureTime; // Время отправления (HH:mm)
    private String arrivalTime;   // Время прибытия (HH:mm)
    private String duration;      // Время в пути (например, "8ч 30мин")
    private int platzkartPrice;   // Цена плацкарта (руб)
    private int coupePrice;       // Цена купе (руб)
    private int svPrice;          // Цена СВ (руб)
    private String date;          // Дата рейса

    public Train() {}

    public Train(String trainNumber, String trainType, String fromStation, String toStation,
                 String departureTime, String arrivalTime, String duration,
                 int platzkartPrice, int coupePrice, int svPrice, String date) {
        this.trainNumber = trainNumber;
        this.trainType = trainType;
        this.fromStation = fromStation;
        this.toStation = toStation;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.duration = duration;
        this.platzkartPrice = platzkartPrice;
        this.coupePrice = coupePrice;
        this.svPrice = svPrice;
        this.date = date;
    }

    // Getters
    public String getTrainNumber() { return trainNumber; }
    public String getTrainType()   { return trainType; }
    public String getFromStation() { return fromStation; }
    public String getToStation()   { return toStation; }
    public String getDepartureTime() { return departureTime; }
    public String getArrivalTime() { return arrivalTime; }
    public String getDuration()    { return duration; }
    public int getPlatzkartPrice() { return platzkartPrice; }
    public int getCoupePrice()     { return coupePrice; }
    public int getSvPrice()        { return svPrice; }
    public String getDate()        { return date; }

    // Setters
    public void setTrainNumber(String trainNumber)   { this.trainNumber = trainNumber; }
    public void setTrainType(String trainType)       { this.trainType = trainType; }
    public void setFromStation(String fromStation)   { this.fromStation = fromStation; }
    public void setToStation(String toStation)       { this.toStation = toStation; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }
    public void setArrivalTime(String arrivalTime)   { this.arrivalTime = arrivalTime; }
    public void setDuration(String duration)         { this.duration = duration; }
    public void setPlatzkartPrice(int platzkartPrice) { this.platzkartPrice = platzkartPrice; }
    public void setCoupePrice(int coupePrice)        { this.coupePrice = coupePrice; }
    public void setSvPrice(int svPrice)              { this.svPrice = svPrice; }
    public void setDate(String date)                 { this.date = date; }

    /** Минимальная цена среди всех типов вагонов */
    public int getMinPrice() {
        return Math.min(platzkartPrice, Math.min(coupePrice, svPrice));
    }
}
