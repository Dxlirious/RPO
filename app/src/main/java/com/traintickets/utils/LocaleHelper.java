package com.traintickets.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

/**
 * Утилита для программного переключения языка приложения.
 * Используется в BaseActivity для применения локали из SharedPreferences.
 */
public class LocaleHelper {

    /**
     * Применить сохранённый язык к контексту.
     * Вызывается в attachBaseContext каждой Activity.
     */
    public static Context applyLanguage(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration config = new Configuration(resources.getConfiguration());
        config.setLocale(locale);

        return context.createConfigurationContext(config);
    }
}
