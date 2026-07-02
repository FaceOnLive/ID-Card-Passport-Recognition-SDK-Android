package com.bio.idcardrecognition;

import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bio.idcardrecognition.R;
import com.bio.idcardrecognition.SectionedAdapter;

public class StickyHeaderDecoration extends RecyclerView.ItemDecoration {
    private SectionedAdapter adapter;

    public StickyHeaderDecoration(SectionedAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void onDrawOver(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int childCount = parent.getChildCount();
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();

        if (layoutManager == null || childCount == 0) return;

        String currentHeader = null;
        View headerView = null;

        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(child);

            if (position == RecyclerView.NO_POSITION) continue;

            // Get the header for this position
            String header = adapter.getHeaderForPosition(position);

            // Skip if no header is associated with this position
            if (header == null) continue;

            // Draw only the first visible header as the sticky header
            if (currentHeader == null || !header.equals(currentHeader)) {
                headerView = getHeaderView(parent, header);
                currentHeader = header;

                // Calculate the sticky position
                int headerHeight = headerView.getHeight();
                int top = Math.max(0, child.getTop() - headerHeight);

                // Push up if the next header overlaps
                if (i + 1 < childCount) {
                    int nextPosition = parent.getChildAdapterPosition(parent.getChildAt(i + 1));
                    String nextHeader = adapter.getHeaderForPosition(nextPosition);

                    if (nextHeader != null && !nextHeader.equals(header)) {
                        int nextHeaderTop = parent.getChildAt(i + 1).getTop() - headerHeight;
                        top = Math.min(top, nextHeaderTop);
                    }
                }

                // Draw the header at its calculated position
                canvas.save();
                canvas.translate(0, top);
                headerView.draw(canvas);
                canvas.restore();
                break; // Stop processing after drawing the sticky header
            }
        }
    }


    private View getHeaderView(RecyclerView parent, String headerText) {
        View headerView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_section_header, parent, false);

        TextView tvHeader = headerView.findViewById(R.id.tvSectionHeader);
        tvHeader.setText(headerText);

        // Measure and layout the header view
        int widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        headerView.measure(widthSpec, heightSpec);
        headerView.layout(0, 0, headerView.getMeasuredWidth(), headerView.getMeasuredHeight());

        return headerView;
    }
}
