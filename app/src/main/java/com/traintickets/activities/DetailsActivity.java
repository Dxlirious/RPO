package com.traintickets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.traintickets.R;
import com.traintickets.database.DBHelper;
import com.traintickets.models.SavedTicket;
import com.traintickets.models.Train;

/**
 * Экран деталей рейса.
 * Показывает расписание, цены по типам вагонов.
 * Позволяет сохранить билет в локальную БД (SQLite).
 */
public class DetailsActivity extends BaseActivity {

    private DBHelper dbHelper;
    private Train train;
    private String date, passengers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        dbHelper = new DBHelper(this);

        // Получаем данные из Intent
        Intent intent = getIntent();
        train      = (Train) intent.getSerializableExtra(ResultsActivity.EXTRA_TRAIN);
        date       = intent.getStringExtra(ResultsActivity.EXTRA_DATE);
        passengers = intent.getStringExtra(ResultsActivity.EXTRA_PASSENGERS);

        if (train == null) {
            finish();
            return;
        }

        // Заполняем UI
        bindViews();

        // Кнопка «Сохранить»
        Button btnSave = findViewById(R.id.btnSave);

        String route = train.getFromStation() + " → " + train.getToStation()
                + " (Поезд " + train.getTrainNumber() + ")";

        // Проверяем, не сохранён ли уже этот рейс
        if (dbHelper.isAlreadySaved(route, date)) {
            btnSave.setText(getString(R.string.ticket_already_saved));
            btnSave.setEnabled(false);
        }

        btnSave.setOnClickListener(v -> {
            SavedTicket ticket = new SavedTicket(route, date, train.getMinPrice());
            long id = dbHelper.insertTicket(ticket);
            if (id != -1) {
                Toast.makeText(this, getString(R.string.ticket_saved), Toast.LENGTH_SHORT).show();
                btnSave.setText(getString(R.string.ticket_already_saved));
                btnSave.setEnabled(false);
            }
        });
    }

    /** Заполняет все текстовые поля данными из объекта Train */
    private void bindViews() {
        TextView tvTrainNumber = findViewById(R.id.tvTrainNumber);
        TextView tvDepTime     = findViewById(R.id.tvDepTime);
        TextView tvDepStation  = findViewById(R.id.tvDepStation);
        TextView tvArrTime     = findViewById(R.id.tvArrTime);
        TextView tvArrStation  = findViewById(R.id.tvArrStation);
        TextView tvDuration    = findViewById(R.id.tvDuration);
        TextView tvDate        = findViewById(R.id.tvDate);
        TextView tvPassengers  = findViewById(R.id.tvPassengers);
        TextView tvPlatzkart   = findViewById(R.id.tvPlatzkartPrice);
        TextView tvCoupe       = findViewById(R.id.tvCoupePrice);
        TextView tvSv          = findViewById(R.id.tvSvPrice);

        tvTrainNumber.setText(getString(R.string.train_number, train.getTrainNumber())
                + " · " + train.getTrainType());
        tvDepTime.setText(train.getDepartureTime());
        tvDepStation.setText(train.getFromStation());
        tvArrTime.setText(train.getArrivalTime());
        tvArrStation.setText(train.getToStation());
        tvDuration.setText(train.getDuration());
        tvDate.setText(date);
        tvPassengers.setText(passengers + " " + getPassengersLabel(passengers));

        tvPlatzkart.setText(train.getPlatzkartPrice() + " BYN");
        tvCoupe.setText(train.getCoupePrice() + " BYN");
        tvSv.setText(train.getSvPrice() > 0 ? train.getSvPrice() + " BYN" : "—");
    }

    /** Склонение слова «пассажир» */
    private String getPassengersLabel(String passengersStr) {
        int n = 1;
        try { n = Integer.parseInt(passengersStr); } catch (Exception ignored) {}
        if (n % 100 >= 11 && n % 100 <= 19) return "пассажиров";
        switch (n % 10) {
            case 1:  return "пассажир";
            case 2:
            case 3:
            case 4:  return "пассажира";
            default: return "пассажиров";
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) dbHelper.close();
    }
}
