package com.example.newsgateway;

import java.io.Serializable;

public class Article implements Serializable {

    private String author;
    private String title;
    private String description;
    private String url;
    private String urlToImage;
    private String publishedAt;

    Article(String author, String title, String description, String url, String urlToImage, String publishedAt) {
        this.author = author;
        this.title = title;
        this.description = description;
        this.url = url;
        this.urlToImage = urlToImage;
        this.publishedAt = publishedAt;
    }

    public String getAuthor() { return author; }

    public String getTitle() { return title; }

    public String getDescription() { return description; }

    public String getUrl() { return url; }

    public String getUrlToImage() { return urlToImage; }

    public String getPublishedAt() { return publishedAt; }
}
