package com.traintickets.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.traintickets.R;
import com.traintickets.models.Train;

import java.util.List;

/**
 * Адаптер для RecyclerView на экране результатов поиска.
 * Отображает список поездов в виде карточек.
 */
public class TrainAdapter extends RecyclerView.Adapter<TrainAdapter.TrainViewHolder> {

    public interface OnTrainClickListener {
        void onDetailsClick(Train train);
    }

    private final List<Train> trains;
    private final OnTrainClickListener listener;

    public TrainAdapter(List<Train> trains, OnTrainClickListener listener) {
        this.trains   = trains;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TrainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_train, parent, false);
        return new TrainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrainViewHolder holder, int position) {
        Train train = trains.get(position);
        holder.bind(train, listener);
    }

    @Override
    public int getItemCount() {
        return trains.size();
    }

    // ─────────────── ViewHolder ───────────────

    static class TrainViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTrainNumber;
        private final TextView tvTrainType;
        private final TextView tvDepartureTime;
        private final TextView tvDepartureStation;
        private final TextView tvArrivalTime;
        private final TextView tvArrivalStation;
        private final TextView tvDuration;
        private final TextView tvPrice;
        private final Button   btnDetails;

        TrainViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTrainNumber      = itemView.findViewById(R.id.tvTrainNumber);
            tvTrainType        = itemView.findViewById(R.id.tvTrainType);
            tvDepartureTime    = itemView.findViewById(R.id.tvDepartureTime);
            tvDepartureStation = itemView.findViewById(R.id.tvDepartureStation);
            tvArrivalTime      = itemView.findViewById(R.id.tvArrivalTime);
            tvArrivalStation   = itemView.findViewById(R.id.tvArrivalStation);
            tvDuration         = itemView.findViewById(R.id.tvDuration);
            tvPrice            = itemView.findViewById(R.id.tvPrice);
            btnDetails         = itemView.findViewById(R.id.btnDetails);
        }

        void bind(Train train, OnTrainClickListener listener) {
            tvTrainNumber.setText(
                    itemView.getContext().getString(R.string.train_number, train.getTrainNumber()));
            tvTrainType.setText(train.getTrainType());
            tvDepartureTime.setText(train.getDepartureTime());
            tvDepartureStation.setText(train.getFromStation());
            tvArrivalTime.setText(train.getArrivalTime());
            tvArrivalStation.setText(train.getToStation());
            tvDuration.setText(train.getDuration());
            tvPrice.setText(
                    itemView.getContext().getString(R.string.price_from,
                            String.valueOf(train.getMinPrice())));

            btnDetails.setOnClickListener(v -> listener.onDetailsClick(train));
            // Клик по всей карточке тоже открывает детали
            itemView.setOnClickListener(v -> listener.onDetailsClick(train));
        }
    }
}
