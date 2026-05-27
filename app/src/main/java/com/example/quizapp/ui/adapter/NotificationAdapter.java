package com.example.quizapp.ui.adapter;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quizapp.R;
import com.example.quizapp.data.remote.model.Notification;
import com.example.quizapp.databinding.ItemNotificationBinding;

public class NotificationAdapter extends ListAdapter<Notification, NotificationAdapter.ViewHolder> {

    public interface OnNotificationClickListener {
        void onNotificationClick(Notification notification);
    }

    private final OnNotificationClickListener listener;

    public NotificationAdapter(OnNotificationClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Notification> DIFF_CALLBACK = new DiffUtil.ItemCallback<Notification>() {
        @Override
        public boolean areItemsTheSame(@NonNull Notification oldItem, @NonNull Notification newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Notification oldItem, @NonNull Notification newItem) {
            return oldItem.is_read == newItem.is_read && oldItem.title.equals(newItem.title);
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNotificationBinding binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemNotificationBinding binding;

        public ViewHolder(ItemNotificationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Notification notification, OnNotificationClickListener listener) {
            binding.tvTitle.setText(notification.title);
            binding.tvMeta.setText(notification.type + " · " + notification.created_at);
            
            if (notification.is_read) {
                binding.dot.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFCCCCCC));
                binding.tvTitle.setTypeface(null, Typeface.NORMAL);
                itemView.setBackgroundColor(0xFFFAFAFA);
            } else {
                binding.dot.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF1565C0));
                binding.tvTitle.setTypeface(null, Typeface.BOLD);
                itemView.setBackgroundColor(0xFFFFFFFF);
            }

            // Icon and tint logic
            int iconRes = R.drawable.ic_home;
            int tintColor = 0xFF888888;
            
            switch (notification.type) {
                case "LEADERBOARD":
                    iconRes = R.drawable.ic_trophy;
                    tintColor = 0xFF854F0B;
                    break;
                case "STREAK":
                    tintColor = 0xFFE65100;
                    break;
                case "PROGRESS":
                    iconRes = R.drawable.ic_chart_bar;
                    break;
            }
            
            binding.ivIcon.setImageResource(iconRes);
            binding.ivIcon.setColorFilter(tintColor);

            itemView.setOnClickListener(v -> listener.onNotificationClick(notification));
        }
    }
}
