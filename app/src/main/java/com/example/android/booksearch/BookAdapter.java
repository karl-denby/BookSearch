package com.example.android.booksearch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class BookAdapter extends ArrayAdapter<Book> {

    public BookAdapter(Context context, ArrayList<Book> Books) {
        super(context, 0, Books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Book Book = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.book_listing, parent, false);
        }
        // Lookup view for data population
        TextView tvTitle  = (TextView) convertView.findViewById(R.id.text1);
        TextView tvAuthor = (TextView) convertView.findViewById(R.id.text2);
        // Populate the data into the template view using the data object
        tvTitle.setText(Book.getTitle());
        tvAuthor.setText(Book.getAuthor());
        // Return the completed view to render on screen
        return convertView;
    }
}