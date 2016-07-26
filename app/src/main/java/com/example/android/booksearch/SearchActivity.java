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

        // Make: Test books
        Book book1 = new Book("The Bible", "God");
        Book book2 = new Book("God Hates us All", "Hank Moody");

        // ArrayList >> Adapter >> ListView
        ArrayList<Book> arrayOfBooks = new ArrayList<>();

        // we need a class for a userAdapter
        BookAdapter bookAdapter = new BookAdapter(this, arrayOfBooks);

        // We need a listView
        ListView lvBook = (ListView) findViewById(R.id.list_item);
        lvBook.setAdapter(bookAdapter);

        bookAdapter.add(book1);
        bookAdapter.add(book2);

        // Link: Search button and Text entered in TextEdit
        //       Define an onClick that toasts whatever is entered.
        final EditText edt_title = (EditText) findViewById(R.id.edt_title);
        Button btn_search = (Button) findViewById(R.id.btn_search);

        assert btn_search != null;

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search_string;
                search_string = edt_title.getText().toString();

                Toast.makeText(SearchActivity.this, "You searched for " + search_string, Toast.LENGTH_SHORT).show();
            }
        });


    }
}
