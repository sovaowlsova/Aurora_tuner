package com.sovaowlsova.auroratuner.news.data;

public class NewsEntry {
    private String title;
    private String article;
    private int timestamp;

    public String getTitle() {
        return title;
    }

    protected void setTitle(String title) {
        this.title = title;
    }

    public String getArticle() {
        return article;
    }

    protected void setArticle(String article) {
        this.article = article;
    }

    public int getTimestamp() {
        return timestamp;
    }

    protected void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }
}
