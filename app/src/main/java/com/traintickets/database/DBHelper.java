package com.traintickets.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.traintickets.models.SavedTicket;

import java.util.ArrayList;
import java.util.List;

/**
 * SQLite-хелпер для управления сохранёнными билетами.
 * Таблица: saved_tickets
 * Поля: id, route, date, price
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME    = "traintickets.db";
    private static final int    DB_VERSION = 1;

    // Таблица и столбцы
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
            COL_PRICE + " INTEGER NOT NULL" +
            ");";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TICKETS);
        onCreate(db);
    }

    // ─────────────────────────── CREATE ───────────────────────────

    /**
     * Добавить новый билет.
     * @return rowId вставленной записи, или -1 при ошибке
     */
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

    // ─────────────────────────── READ ─────────────────────────────

    /**
     * Получить все сохранённые билеты (сортировка по id DESC — новые сверху).
     */
    public List<SavedTicket> getAllTickets() {
        List<SavedTicket> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_TICKETS,
                null,       // все столбцы
                null, null, // без фильтра
                null, null,
                COL_ID + " DESC"
        );
        if (cursor != null && cursor.moveToFirst()) {
            do {
                long   id    = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID));
                String route = cursor.getString(cursor.getColumnIndexOrThrow(COL_ROUTE));
                String date  = cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE));
                int    price = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRICE));
                list.add(new SavedTicket(id, route, date, price));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return list;
    }

    /**
     * Проверить, сохранён ли уже такой маршрут + дата.
     */
    public boolean isAlreadySaved(String route, String date) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_TICKETS,
                new String[]{COL_ID},
                COL_ROUTE + "=? AND " + COL_DATE + "=?",
                new String[]{route, date},
                null, null, null
        );
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        db.close();
        return exists;
    }

    // ─────────────────────────── UPDATE ───────────────────────────

    /**
     * Обновить существующий билет по id.
     * @return число затронутых строк
     */
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

    // ─────────────────────────── DELETE ───────────────────────────

    /**
     * Удалить билет по id.
     * @return число удалённых строк
     */
    public int deleteTicket(long id) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(TABLE_TICKETS, COL_ID + "=?",
                new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    /**
     * Удалить все билеты.
     */
    public void deleteAll() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_TICKETS, null, null);
        db.close();
    }
}
