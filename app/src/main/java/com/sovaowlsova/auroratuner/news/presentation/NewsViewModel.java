package com.sovaowlsova.auroratuner.news.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.sovaowlsova.auroratuner.news.data.NewsEntry;
import com.sovaowlsova.auroratuner.news.data.NewsFetcher;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class NewsViewModel extends ViewModel {
    private MutableLiveData<List<NewsEntry>> newsEntriesLiveData;

    public NewsViewModel(SavedStateHandle savedStateHandle) {
    }

    public interface ErrorCallback {
        void onError(IOException e);
    }

    public void fetchNewsAsync(ErrorCallback errorCallback) throws IOException {
        Thread thread = new Thread(() -> {
            List<NewsEntry> newsEntries;
            try {
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
