package com.traintickets.models;

/**
 * Модель сохранённого билета (хранится в SQLite)
 * Поля: route (маршрут), date (дата), price (цена)
 */
public class SavedTicket {

    private long id;       // PRIMARY KEY (автоинкремент)
    private String route;  // Маршрут: "Москва → Санкт-Петербург (Поезд 001А)"
    private String date;   // Дата поездки: "2026-05-01"
    private int price;     // Цена в рублях

    public SavedTicket() {}

    public SavedTicket(String route, String date, int price) {
        this.route = route;
        this.date = date;
        this.price = price;
    }

    public SavedTicket(long id, String route, String date, int price) {
        this.id = id;
        this.route = route;
        this.date = date;
        this.price = price;
    }

    // Getters
    public long getId()     { return id; }
    public String getRoute() { return route; }
    public String getDate()  { return date; }
    public int getPrice()    { return price; }

    // Setters
    public void setId(long id)       { this.id = id; }
    public void setRoute(String route) { this.route = route; }
    public void setDate(String date)   { this.date = date; }
    public void setPrice(int price)    { this.price = price; }
}
