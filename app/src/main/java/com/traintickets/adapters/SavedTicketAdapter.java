package com.traintickets.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.traintickets.R;
import com.traintickets.models.SavedTicket;

import java.util.List;

/**
 * Адаптер для списка сохранённых билетов на экране настроек.
 * Поддерживает редактирование и удаление через callback-интерфейсы.
 */
public class SavedTicketAdapter extends RecyclerView.Adapter<SavedTicketAdapter.SavedVH> {

    public interface OnEditListener   { void onEdit(SavedTicket ticket); }
    public interface OnDeleteListener { void onDelete(SavedTicket ticket); }

    private final List<SavedTicket>  tickets;
    private final OnEditListener     editListener;
    private final OnDeleteListener   deleteListener;

    public SavedTicketAdapter(List<SavedTicket> tickets,
                              OnEditListener editListener,
                              OnDeleteListener deleteListener) {
        this.tickets        = tickets;
        this.editListener   = editListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public SavedVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_saved_ticket, parent, false);
        return new SavedVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedVH holder, int position) {
        SavedTicket ticket = tickets.get(position);
        holder.bind(ticket, editListener, deleteListener);
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }

    // ─────────────── ViewHolder ───────────────

    static class SavedVH extends RecyclerView.ViewHolder {

        private final TextView     tvRoute;
        private final TextView     tvDate;
        private final TextView     tvPrice;
        private final ImageButton  btnEdit;
        private final ImageButton  btnDelete;

        SavedVH(@NonNull View itemView) {
            super(itemView);
            tvRoute   = itemView.findViewById(R.id.tvSavedRoute);
            tvDate    = itemView.findViewById(R.id.tvSavedDate);
            tvPrice   = itemView.findViewById(R.id.tvSavedPrice);
            btnEdit   = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        void bind(SavedTicket ticket,
                  OnEditListener editListener,
                  OnDeleteListener deleteListener) {
            tvRoute.setText(ticket.getRoute());
            tvDate.setText(ticket.getDate());
            tvPrice.setText(ticket.getPrice() + " BYN");

            btnEdit.setOnClickListener(v -> editListener.onEdit(ticket));
            btnDelete.setOnClickListener(v -> deleteListener.onDelete(ticket));
        }
    }
}
