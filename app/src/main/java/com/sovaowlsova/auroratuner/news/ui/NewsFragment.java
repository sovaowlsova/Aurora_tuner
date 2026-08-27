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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sovaowlsova.auroratuner.R;
import com.sovaowlsova.auroratuner.core.model.Exceptions.NoInternetException;
import com.sovaowlsova.auroratuner.news.data.NewsEntry;
import com.sovaowlsova.auroratuner.news.presentation.NewsViewModel;

import java.io.IOException;
import java.util.List;

public class NewsFragment extends Fragment {
    private NewsViewModel viewModel;
    private RecyclerView newsRecyclerView;
    private ProgressBar newsProgressBar;
    private TextView errorMainTextView;
    private TextView errorSecondaryTextView;
    private NewsRVAdapter newsRVAdapter;

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
        if (e instanceof NoInternetException) {
            errorMainTextView.setText(R.string.error);
            errorSecondaryTextView.setText(R.string.error_no_internet_connection);
        }
        showErrorText();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateNews(List<NewsEntry> newsEntries) {
        if (newsRVAdapter == null) {
            newsRVAdapter = new NewsRVAdapter(newsEntries);
            LinearLayoutManager layoutManager = new LinearLayoutManager(
                    getContext(),
                    LinearLayoutManager.VERTICAL,
                    false
                    );
            newsRecyclerView.setLayoutManager(layoutManager);
            newsRecyclerView.setAdapter(newsRVAdapter);
        }
        newsRVAdapter.notifyDataSetChanged();
        showNews();
    }

    @SuppressWarnings("ConstantConditions")
    private void setContextualVariables() {
        View view = getView();
        newsRecyclerView = view.findViewById(R.id.newsRecyclerView);
        newsProgressBar = view.findViewById(R.id.newsProgressBar);
        errorMainTextView = view.findViewById(R.id.newsErrorMainTextView);
        errorSecondaryTextView = view.findViewById(R.id.newsErrorSecondaryTextView);
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