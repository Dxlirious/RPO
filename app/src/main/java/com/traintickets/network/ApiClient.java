package com.traintickets.network;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.traintickets.models.Train;

public class ApiClient {

    private static final String TAG      = "ApiClient";
    private static final String BASE_URL = "https://trains-api1.p.rapidapi.com/trains";
    private static final String API_HOST = "trains-api1.p.rapidapi.com";
    private static final String API_KEY  = "YOUR_RAPIDAPI_KEY"; // ← заменить на свой
    private static final int    TIMEOUT_MS = 10_000;

    public List<Train> fetchTrains(String from, String to, String date) throws Exception {
        String urlStr = BASE_URL
                + "?from=" + URLEncoder.encode(from, "UTF-8")
                + "&to="   + URLEncoder.encode(to,   "UTF-8")
                + "&date=" + URLEncoder.encode(date,  "UTF-8");

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("X-RapidAPI-Host", API_HOST);
        conn.setRequestProperty("X-RapidAPI-Key",  API_KEY);
        conn.setConnectTimeout(TIMEOUT_MS);
        conn.setReadTimeout(TIMEOUT_MS);

        int code = conn.getResponseCode();
        if (code != HttpURLConnection.HTTP_OK) {
            throw new Exception("HTTP error: " + code);
        }

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) sb.append(line);
        reader.close();
        conn.disconnect();

        return parseTrains(sb.toString(), from, to, date);
    }

    private List<Train> parseTrains(String json, String from, String to, String date)
            throws Exception {
        List<Train> result = new ArrayList<>();
        JSONObject root = new JSONObject(json);
        JSONArray data  = root.getJSONArray("data");

        for (int i = 0; i < data.length(); i++) {
            JSONObject obj = data.getJSONObject(i);
            result.add(new Train(
                    obj.optString("train_number", "—"),
                    obj.optString("train_type",   "Пассажирский"),
                    from, to,
                    obj.optString("departure_time", "—"),
                    obj.optString("arrival_time",   "—"),
                    obj.optString("duration",        "—"),
                    obj.optInt("platzkart_price", 0),
                    obj.optInt("coupe_price",     0),
                    obj.optInt("sv_price",        0),
                    date
            ));
        }
        Log.d(TAG, "Parsed " + result.size() + " trains from API");
        return result;
    }
}