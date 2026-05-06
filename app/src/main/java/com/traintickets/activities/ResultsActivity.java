package com.traintickets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.traintickets.R;
import com.traintickets.adapters.TrainAdapter;
import com.traintickets.models.Train;
import com.traintickets.utils.TrainDataGenerator;

import java.util.List;

/**
 * Экран результатов поиска.
 * Принимает параметры поиска через Intent-экстры и отображает
 * список поездов в RecyclerView.
 */
public class ResultsActivity extends BaseActivity {

    public static final String EXTRA_FROM       = "extra_from";
    public static final String EXTRA_TO         = "extra_to";
    public static final String EXTRA_DATE       = "extra_date";
    public static final String EXTRA_PASSENGERS = "extra_passengers";
    public static final String EXTRA_TRAIN      = "extra_train";

    private String from, to, date, passengers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // Toolbar с кнопкой «Назад»
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Получаем параметры поиска
        Intent intent = getIntent();
        from       = intent.getStringExtra(EXTRA_FROM);
        to         = intent.getStringExtra(EXTRA_TO);
        date       = intent.getStringExtra(EXTRA_DATE);
        passengers = intent.getStringExtra(EXTRA_PASSENGERS);

        // Заголовок маршрута
        TextView tvHeader = findViewById(R.id.tvRouteHeader);
        tvHeader.setText(from + " → " + to + "  ·  " + date + "  ·  " + passengers + " пас.");

        // Генерация тестовых данных
        List<Train> trains = TrainDataGenerator.generateTrains(from, to, date);

        RecyclerView rv       = findViewById(R.id.rvTrains);
        TextView tvNoResults  = findViewById(R.id.tvNoResults);

        if (trains.isEmpty()) {
            tvNoResults.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
        } else {
            rv.setLayoutManager(new LinearLayoutManager(this));
            TrainAdapter adapter = new TrainAdapter(trains, train -> {
                // Клик по «Подробнее» — открываем DetailsActivity
                Intent detailIntent = new Intent(ResultsActivity.this, DetailsActivity.class);
                detailIntent.putExtra(EXTRA_TRAIN,      train);
                detailIntent.putExtra(EXTRA_DATE,       date);
                detailIntent.putExtra(EXTRA_PASSENGERS, passengers);
                startActivity(detailIntent);
            });
            rv.setAdapter(adapter);
        }
    }
}
