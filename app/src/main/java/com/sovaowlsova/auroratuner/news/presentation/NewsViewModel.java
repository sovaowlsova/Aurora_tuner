package com.sovaowlsova.auroratuner.news.presentation;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.sovaowlsova.auroratuner.core.model.Exceptions.NoInternetException;
import com.sovaowlsova.auroratuner.core.util.ConnectionManager;
import com.sovaowlsova.auroratuner.news.data.NewsEntry;
import com.sovaowlsova.auroratuner.news.data.NewsFetcher;

import java.io.IOException;
import java.util.List;

public class NewsViewModel extends ViewModel {
    private MutableLiveData<List<NewsEntry>> newsEntriesLiveData;

    public NewsViewModel(SavedStateHandle savedStateHandle) {
    }

    public interface errorCallback {
        void onError(IOException e);
    }

    public void fetchNewsAsync(errorCallback errorCallback, Context context) {
        Thread thread = new Thread(() -> {
            List<NewsEntry> newsEntries;
            try {
                if (!ConnectionManager.isConnected(context)) {
                    throw new NoInternetException();
                }
                newsEntries = NewsFetcher.fetchNews();
            } catch (IOException e) {
                errorCallback.onError(e);
                return;
            }

            newsEntriesLiveData.postValue(newsEntries);
        });

        thread.start();
    }

    public LiveData<List<NewsEntry>> getNewsEntriesState() {
        if (newsEntriesLiveData == null) {
            newsEntriesLiveData = new MutableLiveData<>();
        }

        return newsEntriesLiveData;
    }
}
