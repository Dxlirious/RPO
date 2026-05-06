package com.traintickets.mvp.model;

import android.content.Context;
import android.os.AsyncTask;

import com.traintickets.database.DBHelper;
import com.traintickets.models.Train;
import com.traintickets.network.ApiClient;
import com.traintickets.utils.NetworkUtils;
import com.traintickets.utils.TrainDataGenerator;

import java.util.List;

public class SearchModel {

    public interface Callback {
        void onSuccess(List<Train> trains, boolean fromCache);
        void onError(String message);
    }

    private static final long CACHE_MAX_AGE_MS = 24 * 60 * 60 * 1000L;

    private final Context context;
    private final DBHelper dbHelper;
    private final ApiClient apiClient;

    public SearchModel(Context context) {
        this.context   = context;
        this.dbHelper  = new DBHelper(context);
        this.apiClient = new ApiClient();
    }

    public void loadTrains(String from, String to, String date, Callback callback) {
        if (NetworkUtils.isConnected(context)) {
            new FetchTask(from, to, date, callback).execute();
        } else {
            List<Train> cached = dbHelper.getCachedTrains(from, to, date, CACHE_MAX_AGE_MS);
            if (!cached.isEmpty()) {
                callback.onSuccess(cached, true);
            } else {
                List<Train> demo = TrainDataGenerator.generateTrains(from, to, date);
                callback.onSuccess(demo, true);
            }
        }
    }

    private class FetchTask extends AsyncTask<Void, Void, List<Train>> {

        private final String from, to, date;
        private final Callback callback;
        private String errorMessage;

        FetchTask(String from, String to, String date, Callback callback) {
            this.from = from;
            this.to = to;
            this.date = date;
            this.callback = callback;
        }

        @Override
        protected List<Train> doInBackground(Void... voids) {
            try {
                List<Train> trains = apiClient.fetchTrains(from, to, date);
                if (!trains.isEmpty()) {
                    dbHelper.cacheTrains(from, to, date, trains);
                    return trains;
                }
            } catch (Exception e) {
                errorMessage = e.getMessage();
            }
            List<Train> cached = dbHelper.getCachedTrains(from, to, date, CACHE_MAX_AGE_MS);
            if (!cached.isEmpty()) return cached;
            return TrainDataGenerator.generateTrains(from, to, date);
        }

        @Override
        protected void onPostExecute(List<Train> trains) {
            boolean fromCache = (errorMessage != null);
            if (trains != null && !trains.isEmpty()) {
                callback.onSuccess(trains, fromCache);
            } else {
                callback.onError(errorMessage != null
                        ? "Ошибка загрузки: " + errorMessage
                        : "Данные не найдены");
            }
        }
    }

    public void release() {
        if (dbHelper != null) dbHelper.close();
    }
}