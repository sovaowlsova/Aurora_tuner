package com.sovaowlsova.auroratuner.news.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.SavedStateViewModelFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sovaowlsova.auroratuner.R;
import com.sovaowlsova.auroratuner.core.model.Exceptions.HTTPException;
import com.sovaowlsova.auroratuner.core.model.Exceptions.NoInternetException;
import com.sovaowlsova.auroratuner.core.util.Constants;
import com.sovaowlsova.auroratuner.news.data.NewsEntry;
import com.sovaowlsova.auroratuner.news.presentation.NewsViewModel;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

public class NewsFragment extends Fragment {
    private NewsViewModel viewModel;
    private RecyclerView newsRecyclerView;
    private ProgressBar newsProgressBar;
    private TextView errorMainTextView;
    private TextView errorSecondaryTextView;
    private SwipeRefreshLayout newsSwipeRefresh;
    private NewsRVAdapter newsRVAdapter;
    List<NewsEntry> currentNewsEntries;

    public NewsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        System.out.println("Creating news fragment...");
        super.onCreate(savedInstanceState);
        SavedStateViewModelFactory viewModelFactory = new SavedStateViewModelFactory(
                requireActivity().getApplication(),
                this,
                getArguments()
        );
        viewModel = new ViewModelProvider(this, viewModelFactory).get(NewsViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setContextualVariables();
        newsSwipeRefresh.setOnRefreshListener(() -> {
            viewModel.fetchNewsAsync(this::handleFetchException, getContext());
        });

        showLoading();
        LifecycleOwner lifecycleOwner = getViewLifecycleOwner();
        viewModel.getNewsEntriesState().observe(lifecycleOwner, this::updateNews);

        viewModel.fetchNewsAsync(this::handleFetchException, getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news, container, false);
    }

    private void handleFetchException(IOException e) {
        requireActivity().runOnUiThread(() -> {
            if (e instanceof NoInternetException) {
                System.out.println("No internet!");
                errorMainTextView.setText(getString(R.string.error));
                errorSecondaryTextView.setText(getString(R.string.error_no_internet_connection));
            } else if (e instanceof HTTPException httpException) {
                System.out.println("HTTP error!");
                int httpCode = httpException.getCode();
                errorMainTextView.setText(getString(R.string.error_http, httpCode));
                if (Constants.httpCodeToStringId.containsKey(httpCode)) {
                    errorSecondaryTextView.setText(getString(Constants.httpCodeToStringId.get(httpCode)));
                } else {
                    errorSecondaryTextView.setText("");
                }
            } else if (e instanceof SocketTimeoutException) {
                System.out.println("Connection timeout!");
                errorMainTextView.setText(getString(R.string.error));
                errorSecondaryTextView.setText(getString(R.string.error_connection_timeout));
            } else {
                System.out.println("Unhandled news fetching exception: \"" + e.getMessage() + "\" with type of " + e.getClass().getSimpleName());
                errorMainTextView.setText(getString(R.string.error));
                errorSecondaryTextView.setText(e.getMessage());
            }
            if (newsSwipeRefresh.isRefreshing()) {
                newsSwipeRefresh.setRefreshing(false);
            }
            showErrorText();
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateNews(List<NewsEntry> newsEntries) {
        if (newsRVAdapter == null) {
            this.currentNewsEntries = newsEntries;
            newsRVAdapter = new NewsRVAdapter(newsEntries);
            LinearLayoutManager layoutManager = new LinearLayoutManager(
                    getContext(),
                    LinearLayoutManager.VERTICAL,
                    false
                    );
            newsRecyclerView.setLayoutManager(layoutManager);
            newsRecyclerView.setAdapter(newsRVAdapter);
        } else {
            currentNewsEntries.clear();
            currentNewsEntries.addAll(newsEntries);
            if (newsSwipeRefresh.isRefreshing()) {
                newsSwipeRefresh.setRefreshing(false);
            }
        }
        if (newsSwipeRefresh.isRefreshing()) {
            newsSwipeRefresh.setRefreshing(false);
        }
        newsRVAdapter.notifyDataSetChanged();
        showNews();
    }

    @SuppressWarnings("ConstantConditions")
    private void setContextualVariables() {
        View view = getView();
        newsRecyclerView = view.findViewById(R.id.news_recycler_view);
        newsProgressBar = view.findViewById(R.id.news_progress_bar);
        errorMainTextView = view.findViewById(R.id.news_error_main_text_view);
        errorSecondaryTextView = view.findViewById(R.id.news_error_secondary_text_view);
        newsSwipeRefresh = view.findViewById(R.id.news_swipe_refresh);
    }

    private void showErrorText() {
        newsProgressBar.setVisibility(View.GONE);
        newsRecyclerView.setVisibility(View.GONE);
        errorMainTextView.setVisibility(View.VISIBLE);
        errorSecondaryTextView.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        newsProgressBar.setVisibility(View.VISIBLE);
        newsRecyclerView.setVisibility(View.GONE);
        errorMainTextView.setVisibility(View.GONE);
        errorSecondaryTextView.setVisibility(View.GONE);
    }

    private void showNews() {
        newsProgressBar.setVisibility(View.GONE);
        newsRecyclerView.setVisibility(View.VISIBLE);
        errorMainTextView.setVisibility(View.GONE);
        errorSecondaryTextView.setVisibility(View.GONE);
    }
}