package com.example.android.booksearch;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    public final String LOG_TAG = "SearchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Link: Search button and Text entered in TextEdit
        //       Define an onClick that makes a url, then calls UI update.
        final EditText edt_title = (EditText) findViewById(R.id.edt_title);
        Button btn_search = (Button) findViewById(R.id.btn_search);

        assert btn_search != null;
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                URL url;
                String search_string;

                if (edt_title.getText().toString().equals("")) {
                    Toast.makeText(SearchActivity.this, "No input ", Toast.LENGTH_SHORT).show();
                } else {
                    search_string = edt_title.getText().toString();
                    Toast.makeText(SearchActivity.this, search_string, Toast.LENGTH_SHORT).show();

                    // Make a search URL
                    url = makeURL(search_string);

                    // Pass to the UI update code which should get data, then update the display
                    updateUI(url);
                }
            }
        });

    } //onCreate

    public void updateUI(URL url) {
        // ArrayList >> Adapter >> ListView
        ArrayList<Book> arrayOfBooks = QueryUtils.extractBooks();

        // we need a class for a userAdapter
        BookAdapter bookAdapter = new BookAdapter(this, arrayOfBooks);

        // We need a listView
        ListView lvBook = (ListView) findViewById(R.id.list_item);
        lvBook.setAdapter(bookAdapter);
    }

    public URL makeURL(String search_string) {

        String base_url = "https://www.googleapis.com/books/v1/volumes?q=";
        String end_url = "&maxResults=10";
        String queryUrl = base_url + search_string + end_url;
        URL url;

        try {
            url = new URL(queryUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
        return url;
    }


}

