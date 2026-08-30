package com.sovaowlsova.auroratuner.news.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sovaowlsova.auroratuner.R;
import com.sovaowlsova.auroratuner.core.util.DateWorkshop;
import com.sovaowlsova.auroratuner.news.data.NewsEntry;

import java.util.List;

public class NewsRVAdapter extends RecyclerView.Adapter<NewsRVAdapter.viewholder> {
    private final List<NewsEntry> newsEntries;

    public NewsRVAdapter(List<NewsEntry> newsEntries) {
        this.newsEntries = newsEntries;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item,  parent, false);
        return new viewholder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        NewsEntry newsEntry = newsEntries.get(position);

        holder.titleTextView.setText(newsEntry.getTitle());
        holder.articleTextView.setText(newsEntry.getArticle());
        holder.dateTextView.setText(
                DateWorkshop.getDateAccordingToLocalTimezone(newsEntry.getTimestamp())
        );
    }

    @Override
    public int getItemCount() {
        return newsEntries.size();
    }

    public static class viewholder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView articleTextView;
        private final TextView dateTextView;

        public viewholder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.news_title_text_view);
            articleTextView = itemView.findViewById(R.id.news_article_text_view);
            dateTextView = itemView.findViewById(R.id.news_date_text_view);
        }

    }
}
