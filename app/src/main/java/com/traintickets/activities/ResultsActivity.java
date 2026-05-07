package com.traintickets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresPermission;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.traintickets.R;
import com.traintickets.adapters.TrainAdapter;
import com.traintickets.models.Train;
import com.traintickets.mvp.contract.SearchContract;
import com.traintickets.mvp.presenter.SearchPresenter;

import java.util.List;

public class ResultsActivity extends BaseActivity implements SearchContract.View {

    public static final String EXTRA_FROM       = "extra_from";
    public static final String EXTRA_TO         = "extra_to";
    public static final String EXTRA_DATE       = "extra_date";
    public static final String EXTRA_PASSENGERS = "extra_passengers";
    public static final String EXTRA_TRAIN      = "extra_train";

    private SearchPresenter presenter;

    private RecyclerView rvTrains;
    private TextView     tvNoResults;
    private TextView     tvOfflineBanner;
    private ProgressBar  progressBar;

    private String from, to, date, passengers;

    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Views
        rvTrains        = findViewById(R.id.rvTrains);
        tvNoResults     = findViewById(R.id.tvNoResults);
        tvOfflineBanner = findViewById(R.id.tvOfflineBanner);
        progressBar     = findViewById(R.id.progressBar);

        rvTrains.setLayoutManager(new LinearLayoutManager(this));

        // Параметры из Intent
        Intent intent = getIntent();
        from       = intent.getStringExtra(EXTRA_FROM);
        to         = intent.getStringExtra(EXTRA_TO);
        date       = intent.getStringExtra(EXTRA_DATE);
        passengers = intent.getStringExtra(EXTRA_PASSENGERS);

        // Заголовок маршрута
        TextView tvHeader = findViewById(R.id.tvRouteHeader);
        tvHeader.setText(from + " → " + to + "  ·  " + date + "  ·  " + passengers + " пас.");

        // Запуск MVP
        presenter = new SearchPresenter(this, this);
        presenter.searchTrains(from, to, date, passengers);
    }

    // ─── SearchContract.View ──────────────────────────────────────

    @Override
    public void showTrains(List<Train> trains) {
        rvTrains.setVisibility(View.VISIBLE);
        tvNoResults.setVisibility(View.GONE);
        TrainAdapter adapter = new TrainAdapter(trains, train -> {
            Intent detailIntent = new Intent(this, DetailsActivity.class);
            detailIntent.putExtra(EXTRA_TRAIN,      train);
            detailIntent.putExtra(EXTRA_DATE,       date);
            detailIntent.putExtra(EXTRA_PASSENGERS, passengers);
            startActivity(detailIntent);
        });
        rvTrains.setAdapter(adapter);
    }

    @Override
    public void showError(String message) {
        tvNoResults.setVisibility(View.VISIBLE);
        tvNoResults.setText(message);
        rvTrains.setVisibility(View.GONE);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        if (isLoading) rvTrains.setVisibility(View.GONE);
    }

    @Override
    public void showOfflineBanner(boolean isOffline) {
        tvOfflineBanner.setVisibility(isOffline ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showEmpty() {
        tvNoResults.setVisibility(View.VISIBLE);
        rvTrains.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) presenter.detachView();
    }
}