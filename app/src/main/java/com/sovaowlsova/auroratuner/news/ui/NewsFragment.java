package com.sovaowlsova.auroratuner.news.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.SavedStateViewModelFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sovaowlsova.auroratuner.R;
import com.sovaowlsova.auroratuner.news.data.NewsEntry;
import com.sovaowlsova.auroratuner.news.presentation.NewsViewModel;

import java.io.IOException;
import java.util.List;

public class NewsFragment extends Fragment {
    private NewsViewModel viewModel;
    private RecyclerView newsRecyclerView;

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
        LifecycleOwner lifecycleOwner = getViewLifecycleOwner();
        viewModel.getNewsEntriesState().observe(lifecycleOwner, this::updateNews);
        setContextualVariables();

        try {
            viewModel.fetchNewsAsync(this::handleFetchException);
        } catch (IOException e) {
            handleFetchException(e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news, container, false);
    }

    private void handleFetchException(IOException e) {

    }

    private void updateNews(List<NewsEntry> newsEntries) {

    }

    @SuppressWarnings("ConstantConditions")
    private void setContextualVariables() {
        View view = getView();
        newsRecyclerView = view.findViewById(R.id.newsRecyclerView);
    }
}