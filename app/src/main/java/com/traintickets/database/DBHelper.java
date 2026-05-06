package com.traintickets.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.traintickets.models.SavedTicket;
import com.traintickets.models.Train;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME    = "traintickets.db";
    private static final int    DB_VERSION = 2;

    public static final String TABLE_TICKETS = "saved_tickets";
    public static final String COL_ID    = "id";
    public static final String COL_ROUTE = "route";
    public static final String COL_DATE  = "date";
    public static final String COL_PRICE = "price";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_TICKETS + " (" +
                    COL_ID    + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_ROUTE + " TEXT NOT NULL, " +
                    COL_DATE  + " TEXT NOT NULL, " +
                    COL_PRICE + " INTEGER NOT NULL);";

    private static final String CREATE_CACHE =
            "CREATE TABLE train_cache (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "from_station TEXT, to_station TEXT, search_date TEXT, " +
                    "train_number TEXT, train_type TEXT, " +
                    "departure_time TEXT, arrival_time TEXT, duration TEXT, " +
                    "platzkart_price INTEGER DEFAULT 0, " +
                    "coupe_price INTEGER DEFAULT 0, " +
                    "sv_price INTEGER DEFAULT 0, " +
                    "saved_at INTEGER DEFAULT 0);";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_CACHE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL(CREATE_CACHE);
        }
    }

    // ════════════════════════════════════════════════
    // SAVED TICKETS — ЛР №1
    // ════════════════════════════════════════════════

    public long insertTicket(SavedTicket ticket) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_ROUTE, ticket.getRoute());
        cv.put(COL_DATE,  ticket.getDate());
        cv.put(COL_PRICE, ticket.getPrice());
        long id = db.insert(TABLE_TICKETS, null, cv);
        db.close();
        return id;
    }

    public List<SavedTicket> getAllTickets() {
        List<SavedTicket> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_TICKETS, null, null, null,
                null, null, COL_ID + " DESC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(new SavedTicket(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_ROUTE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRICE))
                ));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return list;
    }

    public boolean isAlreadySaved(String route, String date) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_TICKETS,
                new String[]{COL_ID},
                COL_ROUTE + "=? AND " + COL_DATE + "=?",
                new String[]{route, date}, null, null, null);
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        db.close();
        return exists;
    }

    public int updateTicket(SavedTicket ticket) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_ROUTE, ticket.getRoute());
        cv.put(COL_DATE,  ticket.getDate());
        cv.put(COL_PRICE, ticket.getPrice());
        int rows = db.update(TABLE_TICKETS, cv, COL_ID + "=?",
                new String[]{String.valueOf(ticket.getId())});
        db.close();
        return rows;
    }

    public int deleteTicket(long id) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(TABLE_TICKETS, COL_ID + "=?",
                new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    public void deleteAll() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_TICKETS, null, null);
        db.close();
    }

    // ════════════════════════════════════════════════
    // TRAIN CACHE — ЛР №2
    // ════════════════════════════════════════════════

    public void cacheTrains(String from, String to, String date, List<Train> trains) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("train_cache",
                "from_station=? AND to_station=? AND search_date=?",
                new String[]{from, to, date});
        long now = System.currentTimeMillis();
        for (Train train : trains) {
            ContentValues cv = new ContentValues();
            cv.put("from_station",    from);
            cv.put("to_station",      to);
            cv.put("search_date",     date);
            cv.put("train_number",    train.getTrainNumber());
            cv.put("train_type",      train.getTrainType());
            cv.put("departure_time",  train.getDepartureTime());
            cv.put("arrival_time",    train.getArrivalTime());
            cv.put("duration",        train.getDuration());
            cv.put("platzkart_price", train.getPlatzkartPrice());
            cv.put("coupe_price",     train.getCoupePrice());
            cv.put("sv_price",        train.getSvPrice());
            cv.put("saved_at",        now);
            db.insert("train_cache", null, cv);
        }
        db.close();
    }

    public List<Train> getCachedTrains(String from, String to, String date, long maxAgeMs) {
        List<Train> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String sel = "from_station=? AND to_station=? AND search_date=?";
        if (maxAgeMs > 0) {
            sel += " AND saved_at >= " + (System.currentTimeMillis() - maxAgeMs);
        }
        Cursor cursor = db.query("train_cache", null, sel,
                new String[]{from, to, date}, null, null, "departure_time ASC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(new Train(
                        cursor.getString(cursor.getColumnIndexOrThrow("train_number")),
                        cursor.getString(cursor.getColumnIndexOrThrow("train_type")),
                        cursor.getString(cursor.getColumnIndexOrThrow("from_station")),
                        cursor.getString(cursor.getColumnIndexOrThrow("to_station")),
                        cursor.getString(cursor.getColumnIndexOrThrow("departure_time")),
                        cursor.getString(cursor.getColumnIndexOrThrow("arrival_time")),
                        cursor.getString(cursor.getColumnIndexOrThrow("duration")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("platzkart_price")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("coupe_price")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("sv_price")),
                        date
                ));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return list;
    }

    public void clearCache() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("train_cache", null, null);
        db.close();
    }
}