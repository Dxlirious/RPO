package com.traintickets.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Менеджер настроек приложения (SharedPreferences).
 * Хранит: выбранную тему и язык интерфейса.
 */
public class PrefsManager {

    private static final String PREFS_NAME   = "train_prefs";
    private static final String KEY_THEME    = "theme";     // "light" | "dark"
    private static final String KEY_LANGUAGE = "language";  // "ru" | "en"

    public static final String THEME_LIGHT = "light";
    public static final String THEME_DARK  = "dark";
    public static final String LANG_RU     = "ru";
    public static final String LANG_EN     = "en";

    private final SharedPreferences prefs;

    public PrefsManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // ──────────────── THEME ────────────────

    public void saveTheme(String theme) {
        prefs.edit().putString(KEY_THEME, theme).apply();
    }

    public String getTheme() {
        return prefs.getString(KEY_THEME, THEME_LIGHT);
    }

    public boolean isDarkTheme() {
        return THEME_DARK.equals(getTheme());
    }

    // ──────────────── LANGUAGE ────────────────

    public void saveLanguage(String lang) {
        prefs.edit().putString(KEY_LANGUAGE, lang).apply();
    }

    public String getLanguage() {
        return prefs.getString(KEY_LANGUAGE, LANG_RU);
    }
}
