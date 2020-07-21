package com.example.pustakbhandar;

public class Book {

    private String title;
    private String author;
    private String price;
    private String language;
    private String buyLink;
    private String imageLink;

    public Book(String title, String author, String price, String language, String buyLink, String imageLink) {
        this.title = title;
        this.author = author;
        this.price = price;
        this.language = language;
        this.buyLink = buyLink;
        this.imageLink = imageLink;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getPrice() {
        return price;
    }

    public String getLanguage() {
        return language;
    }

    public String getBuyLink() {
        return buyLink;
    }

    public String getImageLink() {
        return imageLink;
    }
}
