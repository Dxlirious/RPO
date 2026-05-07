package com.traintickets.network;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.traintickets.models.Train;

public class ApiClient {

    private static final String TAG     = "ApiClient";
    private static final String BIN_ID  = "69fc5e40adc21f119a67042e ";
    private static final String API_KEY = "$2a$10$58XK4ssKinqmGBTFtLJ8Ae.N3pd58Emuv2oZ8bOYAEJfuORuTzoMG";
    private static final String BASE_URL =
            "https://api.jsonbin.io/v3/b/" + BIN_ID;
    private static final int TIMEOUT_MS = 10_000;

    public List<Train> fetchTrains(String from, String to, String date) throws Exception {
        URL url = new URL(BASE_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("X-Access-Key", API_KEY);
        conn.setConnectTimeout(TIMEOUT_MS);
        conn.setReadTimeout(TIMEOUT_MS);

        int code = conn.getResponseCode();
        android.util.Log.d(TAG, "API code = " + code);
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

        // JSONBin оборачивает данные в {"record": {...}, "metadata": {...}}
        JSONObject root   = new JSONObject(json);
        JSONObject record = root.getJSONObject("record");
        JSONArray  data   = record.getJSONArray("data");

        for (int i = 0; i < data.length(); i++) {
            JSONObject obj = data.getJSONObject(i);
            result.add(new Train(
                    obj.optString("train_number",   "—"),
                    obj.optString("train_type",     "Пассажирский"),
                    from, to,
                    obj.optString("departure_time", "—"),
                    obj.optString("arrival_time",   "—"),
                    obj.optString("duration",       "—"),
                    obj.optInt("platzkart_price", 0),
                    obj.optInt("coupe_price",     0),
                    obj.optInt("sv_price",        0),
                    date
            ));
        }
        Log.d(TAG, "Parsed " + result.size() + " trains");
        return result;
    }
}