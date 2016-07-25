package com.example.android.booksearch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Make: Test books
        Book result1 = new Book("The Bible", "God");
        Book result2 = new Book("God Hates us All", "Hank Moody");

        // Make: Book list
        List bookQueryResults = new ArrayList();
        bookQueryResults.add(result1);
        bookQueryResults.add(result2);

        // Link: Search button and Text entered in TextEdit
        //       Define an onClick that toasts whatever is entered.
        final EditText edt_title = (EditText) findViewById(R.id.edt_title);
        Button btn_search = (Button) findViewById(R.id.btn_search);

        assert btn_search != null;
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SearchActivity.this, edt_title.getText(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
