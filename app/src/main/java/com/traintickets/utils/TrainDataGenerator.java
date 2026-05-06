package com.traintickets.utils;

import com.traintickets.models.Train;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Генератор тестовых данных о поездах.
 * Имитирует ответ сервера при поиске маршрута.
 */
public class TrainDataGenerator {

    private static final Random rnd = new Random();

    /**
     * Сгенерировать список поездов для заданного маршрута и даты.
     */
    public static List<Train> generateTrains(String from, String to, String date) {
        List<Train> trains = new ArrayList<>();

        String[][] schedule = {
                {"001А", "Фирменный",    "07:00", "15:30", "8ч 30мин",  "38", "62", "95"},
                {"005Э", "Скорый",       "09:15", "19:45", "10ч 30мин", "29", "48", "80"},
                {"033А", "Скорый",       "12:30", "21:00", "8ч 30мин",  "34", "55", "88"},
                {"107Ч", "Пассажирский", "14:00", "06:20", "16ч 20мин", "18", "32",  "0"},
                {"055Е", "Фирменный",    "17:45", "02:15", "8ч 30мин",  "42", "68", "110"},
                {"023Д", "Скорый",       "21:00", "07:30", "10ч 30мин", "27", "46", "75"},
                {"041Н", "Пассажирский", "23:30", "15:50", "16ч 20мин", "15", "28",  "0"},
        };

        for (String[] row : schedule) {
            int platzkart = Integer.parseInt(row[5]);
            int coupe     = Integer.parseInt(row[6]);
            int sv        = Integer.parseInt(row[7]);

            // Небольшая случайная вариация цены ±5%
            platzkart = varyPrice(platzkart);
            coupe     = varyPrice(coupe);
            sv        = sv > 0 ? varyPrice(sv) : 0;

            trains.add(new Train(
                    row[0], row[1],
                    from, to,
                    row[2], row[3], row[4],
                    platzkart, coupe, sv,
                    date
            ));
        }
        return trains;
    }

    private static int varyPrice(int base) {
        int delta = (int)(base * 0.05);
        return base + rnd.nextInt(delta * 2 + 1) - delta;
    }
}
