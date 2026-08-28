package com.sovaowlsova.auroratuner.news.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sovaowlsova.auroratuner.R;
import com.sovaowlsova.auroratuner.core.model.Exceptions.HTTPException;
import com.sovaowlsova.auroratuner.core.util.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class NewsFetcher {
    private NewsFetcher() {}
    public static List<NewsEntry> fetchNews() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<NewsEntry> newsEntries;
        URL url = URI.create(Constants.NEWS_URL).toURL();
        HttpsURLConnection connection = null;

        try {
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);

            int status = connection.getResponseCode();
            if (status != 200) {
                throw new HTTPException(status);
            }

            InputStream inputStream = connection.getInputStream();
            newsEntries = objectMapper.readValue(inputStream, new TypeReference<>() {});
        } finally {
            if (connection != null) connection.disconnect();
        }

        return newsEntries;
    }
}
