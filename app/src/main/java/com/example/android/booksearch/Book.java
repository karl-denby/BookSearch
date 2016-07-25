package com.example.android.booksearch;

/**
 * Created by karld on 25/07/2016.
 * Simple Book class we will use for prototype
 * Later we will flesh this out and modify as needed.
 */
public class Book {
    private String mTitle;
    private String mAuthor;

    public Book(String title, String author) {
         mTitle = title;
         mAuthor = author ;
    }

    public String getTitle() {return mTitle;}

    public String getAuthor() {return mAuthor;}
}