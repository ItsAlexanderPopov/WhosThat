package com.example.whosthat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.ViewHolder> {
    private List<Achievement> achievements;
    private Context context;
    private Map<String, Integer> iconResourceMap;

    public AchievementAdapter(Context context, List<Achievement> achievements) {
        this.context = context;
        this.achievements = achievements;
        initializeIconResourceMap();
    }

    private void initializeIconResourceMap() {
        iconResourceMap = new HashMap<>();
        iconResourceMap.put("i5lol", R.drawable.i5lol);
        iconResourceMap.put("i10lol", R.drawable.i10lol);
        iconResourceMap.put("i15lol", R.drawable.i15lol);
        iconResourceMap.put("i20lol", R.drawable.i20lol);
        iconResourceMap.put("i5poke", R.drawable.i5poke);
        iconResourceMap.put("i10poke", R.drawable.i10poke);
        iconResourceMap.put("i15poke", R.drawable.i15poke);
        iconResourceMap.put("i20poke", R.drawable.i20poke);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.achievement_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Achievement achievement = achievements.get(position);
        holder.descriptionView.setText(achievement.getDescription());

        Integer resourceId = iconResourceMap.get(achievement.getIconName());
        if (resourceId != null) {
            holder.iconView.setImageResource(resourceId);
        } else {
            // Set a default image or handle the error
            holder.iconView.setImageResource(R.drawable.secret);
        }

        MaterialCardView cardView = (MaterialCardView) holder.itemView;
        if (achievement.isUnlocked()) {
            cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.blue));
            holder.iconView.setColorFilter(null);
            holder.descriptionView.setTextColor(ContextCompat.getColor(context, R.color.black));
        } else {
            cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.gray));
            holder.iconView.setColorFilter(ContextCompat.getColor(context, R.color.transparent_gray));
            holder.descriptionView.setTextColor(ContextCompat.getColor(context, R.color.beige));
        }
    }

    @Override
    public int getItemCount() {
        return achievements.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView iconView;
        public TextView descriptionView;

        public ViewHolder(View itemView) {
            super(itemView);
            iconView = itemView.findViewById(R.id.achievement_icon);
            descriptionView = itemView.findViewById(R.id.achievement_description);
        }
    }
}