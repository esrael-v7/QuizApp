package com.example.quizapp.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quizapp.data.remote.model.LeaderboardEntry;
import com.example.quizapp.databinding.ItemRankedUserBinding;

public class RankedUserAdapter extends ListAdapter<LeaderboardEntry, RankedUserAdapter.ViewHolder> {

    public RankedUserAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<LeaderboardEntry> DIFF_CALLBACK = new DiffUtil.ItemCallback<LeaderboardEntry>() {
        @Override
        public boolean areItemsTheSame(@NonNull LeaderboardEntry oldItem, @NonNull LeaderboardEntry newItem) {
            return oldItem.user_id == newItem.user_id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull LeaderboardEntry oldItem, @NonNull LeaderboardEntry newItem) {
            return oldItem.rank == newItem.rank && oldItem.score == newItem.score;
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRankedUserBinding binding = ItemRankedUserBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemRankedUserBinding binding;

        public ViewHolder(ItemRankedUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(LeaderboardEntry entry) {
            binding.tvRank.setText(String.valueOf(entry.rank));
            binding.tvName.setText(entry.full_name);
            binding.tvCategory.setText(entry.best_category);
            binding.tvPoints.setText(entry.score + " pts");
            
            // Avatar logic
            if (entry.full_name != null && !entry.full_name.isEmpty()) {
                String[] parts = entry.full_name.split(" ");
                String initials = "";
                if (parts.length > 0) initials += parts[0].substring(0, 1).toUpperCase();
                if (parts.length > 1) initials += parts[1].substring(0, 1).toUpperCase();
                binding.tvInitials.setText(initials);
            }

            int[] colors = {0xFF1565C0, 0xFF534AB7, 0xFFE65100, 0xFF0F6E56, 0xFF854F0B, 0xFF791F1F, 0xFF0F6E56, 0xFF37474F};
            int color = colors[entry.user_id % 8];
            binding.tvInitials.getBackground().setTint(color);
            
            // Alternating backgrounds
            itemView.setBackgroundColor(getAdapterPosition() % 2 == 0 ? 0xFFFFFFFF : 0xFFFAFAFA);
        }
    }
}
