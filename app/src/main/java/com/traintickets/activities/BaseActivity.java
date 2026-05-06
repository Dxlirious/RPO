package com.traintickets.activities;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.traintickets.utils.LocaleHelper;
import com.traintickets.utils.PrefsManager;

/**
 * Базовая Activity.
 * Применяет тему (светлая/тёмная) и локаль при создании каждой Activity.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected PrefsManager prefsManager;

    @Override
    protected void attachBaseContext(Context newBase) {
        PrefsManager prefs = new PrefsManager(newBase);
        String lang = prefs.getLanguage();
        Context context = LocaleHelper.applyLanguage(newBase, lang);
        super.attachBaseContext(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefsManager = new PrefsManager(this);

        // Применяем тему глобально — до super.onCreate
        // Используем статический вызов, он безопасен до inflate
        if (prefsManager.isDarkTheme()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
    }
}
