package com.traintickets.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.traintickets.R;

import java.util.Calendar;
import java.util.Locale;

/**
 * Главный экран — поиск маршрута.
 * Пользователь указывает: откуда, куда, дата, число пассажиров.
 */
public class MainActivity extends BaseActivity {

    private TextInputEditText etFrom, etTo, etDate, etPassengers;
    private TextInputLayout tilFrom, tilTo, tilDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Views
        etFrom       = findViewById(R.id.etFrom);
        etTo         = findViewById(R.id.etTo);
        etDate       = findViewById(R.id.etDate);
        etPassengers = findViewById(R.id.etPassengers);
        tilFrom      = findViewById(R.id.tilFrom);
        tilTo        = findViewById(R.id.tilTo);
        tilDate      = findViewById(R.id.tilDate);

        // Кнопка смены направления ⇄
        findViewById(R.id.btnSwap).setOnClickListener(v -> {
            String from = etFrom.getText() != null ? etFrom.getText().toString() : "";
            String to   = etTo.getText()   != null ? etTo.getText().toString()   : "";
            etFrom.setText(to);
            etTo.setText(from);
        });

        // Выбор даты через DatePickerDialog
        etDate.setOnClickListener(v -> showDatePicker());

        // Кнопка поиска
        findViewById(R.id.btnSearch).setOnClickListener(v -> {
            if (validateInput()) {
                openResults();
            }
        });
    }

    /** Открывает DatePickerDialog для выбора даты */
    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String date = String.format(Locale.getDefault(),
                            "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    etDate.setText(date);
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        );
        // Запрет выбора прошлых дат
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dialog.show();
    }

    /** Проверяет заполненность обязательных полей */
    private boolean validateInput() {
        boolean valid = true;

        String from = etFrom.getText() != null ? etFrom.getText().toString().trim() : "";
        String to   = etTo.getText()   != null ? etTo.getText().toString().trim()   : "";
        String date = etDate.getText() != null ? etDate.getText().toString().trim() : "";

        if (TextUtils.isEmpty(from)) {
            tilFrom.setError(getString(R.string.from_hint));
            valid = false;
        } else {
            tilFrom.setError(null);
        }

        if (TextUtils.isEmpty(to)) {
            tilTo.setError(getString(R.string.to_hint));
            valid = false;
        } else {
            tilTo.setError(null);
        }

        if (TextUtils.isEmpty(date)) {
            tilDate.setError(getString(R.string.date_hint));
            valid = false;
        } else {
            tilDate.setError(null);
        }

        if (from.equalsIgnoreCase(to) && !TextUtils.isEmpty(from)) {
            Toast.makeText(this, "Станции отправления и назначения совпадают", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }

    /** Открывает экран результатов с параметрами поиска */
    private void openResults() {
        String from       = etFrom.getText().toString().trim();
        String to         = etTo.getText().toString().trim();
        String date       = etDate.getText().toString().trim();
        String passengers = etPassengers.getText() != null
                ? etPassengers.getText().toString().trim() : "1";
        if (TextUtils.isEmpty(passengers)) passengers = "1";

        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra(ResultsActivity.EXTRA_FROM,       from);
        intent.putExtra(ResultsActivity.EXTRA_TO,         to);
        intent.putExtra(ResultsActivity.EXTRA_DATE,       date);
        intent.putExtra(ResultsActivity.EXTRA_PASSENGERS, passengers);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
