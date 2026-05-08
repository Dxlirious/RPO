package com.traintickets.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.ListenerRegistration;
import com.traintickets.R;
import com.traintickets.adapters.SavedTicketAdapter;
import com.traintickets.database.DBHelper;
import com.traintickets.models.SavedTicket;
import com.traintickets.utils.LocaleHelper;
import com.traintickets.utils.FirebaseHelper;
import com.traintickets.utils.PrefsManager;

import java.util.List;

/**
 * Экран настроек.
 * Позволяет:
 *  - переключать тему (светлая / тёмная) с сохранением в SharedPreferences
 *  - переключать язык (RU / EN) с немедленным применением
 *  - просматривать, редактировать и удалять сохранённые билеты
 */
public class SettingsActivity extends BaseActivity {

    private DBHelper dbHelper;
    private SavedTicketAdapter savedAdapter;
    private List<SavedTicket> savedTickets;
    private RecyclerView rvSaved;
    private TextView tvNoSaved;
    private TextView tvFirestoreSync;
    private ListenerRegistration firestoreListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        dbHelper = new DBHelper(this);

        setupThemeSelector();
        setupLanguageSelector();
        tvFirestoreSync = findViewById(R.id.tvFirestoreSync);
        findViewById(R.id.btnPlatformApis).setOnClickListener(v ->
                startActivity(new Intent(this, PlatformApisActivity.class)));
        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(this, AuthActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        });
        setupSavedTickets();
    }

    // ───────────── THEME ─────────────

    private void setupThemeSelector() {
        RadioGroup rgTheme = findViewById(R.id.rgTheme);
        RadioButton rbLight = findViewById(R.id.rbLight);
        RadioButton rbDark  = findViewById(R.id.rbDark);

        // Установить текущее состояние
        if (prefsManager.isDarkTheme()) {
            rbDark.setChecked(true);
        } else {
            rbLight.setChecked(true);
        }

        rgTheme.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbLight) {
                prefsManager.saveTheme(PrefsManager.THEME_LIGHT);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else if (checkedId == R.id.rbDark) {
                prefsManager.saveTheme(PrefsManager.THEME_DARK);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            // Откладываем recreate() — нельзя вызывать внутри listener синхронно
            new Handler(Looper.getMainLooper()).post(this::recreate);
        });
    }

    // ───────────── LANGUAGE ─────────────

    private void setupLanguageSelector() {
        RadioGroup rgLang  = findViewById(R.id.rgLanguage);
        RadioButton rbRu   = findViewById(R.id.rbRussian);
        RadioButton rbEn   = findViewById(R.id.rbEnglish);

        String currentLang = prefsManager.getLanguage();
        if (PrefsManager.LANG_EN.equals(currentLang)) {
            rbEn.setChecked(true);
        } else {
            rbRu.setChecked(true);
        }

        rgLang.setOnCheckedChangeListener((group, checkedId) -> {
            String lang = (checkedId == R.id.rbEnglish)
                    ? PrefsManager.LANG_EN : PrefsManager.LANG_RU;
            prefsManager.saveLanguage(lang);

            // Перезапускаем приложение с нуля для применения новой локали
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firestoreListener = new FirebaseHelper().listenUserTickets(new FirebaseHelper.OnRealtimeTickets() {
            @Override
            public void onUpdate(List<SavedTicket> tickets) {
                runOnUiThread(() -> updateFirestoreText(tickets));
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() ->
                        tvFirestoreSync.setText(getString(R.string.cloud_error, message)));
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firestoreListener != null) {
            firestoreListener.remove();
            firestoreListener = null;
        }
    }

    private void updateFirestoreText(List<SavedTicket> tickets) {
        if (tickets == null || tickets.isEmpty()) {
            tvFirestoreSync.setText(R.string.cloud_empty);
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (SavedTicket t : tickets) {
            sb.append("• ").append(t.getRoute()).append("\n  ")
                    .append(t.getDate()).append(" — ")
                    .append(t.getPrice()).append(" BYN\n");
        }
        tvFirestoreSync.setText(sb.toString().trim());
    }

    // ───────────── SAVED TICKETS ─────────────

    private void setupSavedTickets() {
        rvSaved   = findViewById(R.id.rvSavedTickets);
        tvNoSaved = findViewById(R.id.tvNoSaved);

        savedTickets = dbHelper.getAllTickets();
        updateSavedTicketsUI();

        savedAdapter = new SavedTicketAdapter(
                savedTickets,
                this::onEditTicket,
                this::onDeleteTicket
        );
        rvSaved.setLayoutManager(new LinearLayoutManager(this));
        rvSaved.setAdapter(savedAdapter);
    }

    private void updateSavedTicketsUI() {
        if (savedTickets.isEmpty()) {
            tvNoSaved.setVisibility(View.VISIBLE);
            rvSaved.setVisibility(View.GONE);
        } else {
            tvNoSaved.setVisibility(View.GONE);
            rvSaved.setVisibility(View.VISIBLE);
        }
    }

    /** Редактирование: диалог для смены даты билета */
    private void onEditTicket(SavedTicket ticket) {
        // Простой диалог — можно заменить на полноценный DatePickerDialog
        android.widget.EditText etPrice = new android.widget.EditText(this);
        etPrice.setHint("Новая цена (руб)");
        etPrice.setText(String.valueOf(ticket.getPrice()));
        etPrice.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.edit_ticket))
                .setMessage(ticket.getRoute() + "\n" + ticket.getDate())
                .setView(etPrice)
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    String input = etPrice.getText().toString().trim();
                    if (!input.isEmpty()) {
                        try {
                            ticket.setPrice(Integer.parseInt(input));
                            dbHelper.updateTicket(ticket);
                            savedAdapter.notifyDataSetChanged();
                        } catch (NumberFormatException ignored) {}
                    }
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }

    /** Удаление: диалог подтверждения */
    private void onDeleteTicket(SavedTicket ticket) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.confirm_delete))
                .setMessage(ticket.getRoute())
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    dbHelper.deleteTicket(ticket.getId());
                    savedTickets.remove(ticket);
                    savedAdapter.notifyDataSetChanged();
                    updateSavedTicketsUI();
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) dbHelper.close();
    }
}
