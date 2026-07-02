package com.bio.idcardrecognition;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SectionedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private List<Object> itemList; // Combined list of headers and items

    public SectionedAdapter(List<Section> sections) {
        itemList = new ArrayList<>();
        for (Section section : sections) {
            itemList.add(section.getHeader()); // Add header
            itemList.addAll(section.getItems()); // Add items under the header
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (itemList.get(position) instanceof String) {
            return TYPE_HEADER; // Header
        } else {
            return TYPE_ITEM; // Item
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_section_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_section_item, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) {
            String header = (String) itemList.get(position);
            ((HeaderViewHolder) holder).tvSectionHeader.setText(header);
        } else {
            Item item = (Item) itemList.get(position);
            ((ItemViewHolder) holder).tvTitle.setText(item.getTitle());
            ((ItemViewHolder) holder).tvContent.setText(item.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    // ViewHolder for Header
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvSectionHeader;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSectionHeader = itemView.findViewById(R.id.tvSectionHeader);
        }
    }

    // ViewHolder for Item
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tvContent);
        }
    }
    public String getHeaderForPosition(int position) {
        if (position < 0 || position >= itemList.size()) {
            return null; // Out of bounds
        }

        Object item = itemList.get(position);

        // If it's a header, return it directly
        if (item instanceof String) {
            return (String) item;
        }

        // Otherwise, find the most recent header
        for (int i = position; i >= 0; i--) {
            if (itemList.get(i) instanceof String) {
                return (String) itemList.get(i);
            }
        }
        return null; // No header found (unlikely in valid data)
    }
}