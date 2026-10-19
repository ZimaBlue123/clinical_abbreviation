package com.example.clinical_abbreviation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AbbreviationAdapter extends RecyclerView.Adapter<AbbreviationAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(AbbreviationModel item);
        void onItemLongClick(AbbreviationModel item);
    }

    public interface OnFilterResultListener {
        void onFilterResult(int count);
    }

    private final List<AbbreviationModel> fullList;
    private final List<AbbreviationModel> displayList;
    private OnItemClickListener itemClickListener;
    private OnFilterResultListener filterResultListener;

    public AbbreviationAdapter(List<AbbreviationModel> list) {
        this.fullList = list != null ? list : new ArrayList<>();
        this.displayList = new ArrayList<>(this.fullList);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setOnFilterResultListener(OnFilterResultListener listener) {
        this.filterResultListener = listener;
    }

    public void filter(String query) {
        List<AbbreviationModel> filteredList = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            filteredList.addAll(fullList);
        } else {
            String lowerCaseQuery = query.toLowerCase().trim();
            List<ScoredItem> scoredItems = new ArrayList<>();

            for (AbbreviationModel item : fullList) {
                String abbr = item.getAbbreviation().toLowerCase();
                String full = item.getFullForm().toLowerCase();
                String zh = item.getChinese().toLowerCase();

                int score = 0;
                if (abbr.equalsIgnoreCase(lowerCaseQuery)) {
                    score = 100;
                } else if (abbr.startsWith(lowerCaseQuery)) {
                    score = 80;
                } else if (abbr.contains(lowerCaseQuery)) {
                    score = 60;
                } else if (zh.contains(lowerCaseQuery)) {
                    score = 40;
                } else if (full.startsWith(lowerCaseQuery)) {
                    score = 30;
                } else if (full.contains(lowerCaseQuery)) {
                    score = 20;
                }

                if (score > 0) {
                    scoredItems.add(new ScoredItem(item, score));
                }
            }

            // 按相关度降序，同相关度按缩写字母升序排列
            Collections.sort(scoredItems, (o1, o2) -> {
                if (o2.score != o1.score) {
                    return Integer.compare(o2.score, o1.score);
                }
                return o1.item.getAbbreviation().compareToIgnoreCase(o2.item.getAbbreviation());
            });

            for (ScoredItem si : scoredItems) {
                filteredList.add(si.item);
            }
        }

        updateList(filteredList);

        if (filterResultListener != null) {
            filterResultListener.onFilterResult(filteredList.size());
        }
    }

    private static class ScoredItem {
        final AbbreviationModel item;
        final int score;

        ScoredItem(AbbreviationModel item, int score) {
            this.item = item;
            this.score = score;
        }
    }

    private void updateList(List<AbbreviationModel> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() { return displayList.size(); }
            @Override
            public int getNewListSize() { return newList.size(); }
            @Override
            public boolean areItemsTheSame(int oldPos, int newPos) {
                AbbreviationModel oldItem = displayList.get(oldPos);
                AbbreviationModel newItem = newList.get(newPos);
                // 临床缩写可能缩写名相同，故同时比对缩写与全称
                return oldItem.getAbbreviation().equalsIgnoreCase(newItem.getAbbreviation())
                        && oldItem.getFullForm().equalsIgnoreCase(newItem.getFullForm());
            }
            @Override
            public boolean areContentsTheSame(int oldPos, int newPos) {
                return displayList.get(oldPos).equals(newList.get(newPos));
            }
        });
        displayList.clear();
        displayList.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_abbreviation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AbbreviationModel item = displayList.get(position);
        holder.tvAbbreviation.setText(item.getAbbreviation());
        holder.tvFullForm.setText(item.getFullForm());
        holder.tvChinese.setText(item.getChinese());

        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(item);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemLongClick(item);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAbbreviation, tvFullForm, tvChinese;

        ViewHolder(View itemView) {
            super(itemView);
            tvAbbreviation = itemView.findViewById(R.id.tvAbbreviation);
            tvFullForm = itemView.findViewById(R.id.tvFullForm);
            tvChinese = itemView.findViewById(R.id.tvChinese);
        }
    }
}