package com.example.android.booksearch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // ArrayList >> Adapter >> ListView
        ArrayList<Book> arrayOfBooks = QueryUtils.extractBooks();

        // we need a class for a userAdapter
        BookAdapter bookAdapter = new BookAdapter(this, arrayOfBooks);

        // We need a listView
        ListView lvBook = (ListView) findViewById(R.id.list_item);
        lvBook.setAdapter(bookAdapter);

        // Link: Search button and Text entered in TextEdit
        //       Define an onClick that toasts whatever is entered.
        final EditText edt_title = (EditText) findViewById(R.id.edt_title);
        Button btn_search = (Button) findViewById(R.id.btn_search);

        assert btn_search != null;

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search_string;

                if (edt_title.getText().toString().equals("")) {
                    Toast.makeText(SearchActivity.this, "No input ", Toast.LENGTH_SHORT).show();
                } else {
                    search_string = edt_title.getText().toString();
                    runQuery(search_string);
                }
            }
        });
    }

    public void runQuery(String search_string) {
        String queryURL;
        String base_url = "https://www.googleapis.com/books/v1/volumes?q=";
        String end_url = "&maxResults=10";

        queryURL = base_url + search_string + end_url;

        Toast.makeText(SearchActivity.this, queryURL, Toast.LENGTH_SHORT).show();

    }
}

