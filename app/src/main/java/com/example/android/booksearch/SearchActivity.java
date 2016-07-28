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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
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
                    BooksAsyncTask results = new BooksAsyncTask();
                    results.execute(url);
                }
            }
        });
    } //onCreate

    private class BooksAsyncTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {

            String result = "";
            int count = urls.length;
            for (int i = 0; i < count; i++) {
                Log.v("loop", i + "" + urls[i].toString());
            }
            // Can't update the UI from here, only thread that made them (main OnCreate) can
            // update them, so just run query, get results and store them somewhere that the
            // main thread can access and use to update the UI.
            try {
                result = makeHttpRequest(urls[0]);
            } catch (IOException e) {
                Log.e(LOG_TAG,"HTTP error", e);
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            updateUI(result);
        }

    } // BooksAsyncTask

    private String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
            jsonResponse = readFromStream(inputStream);
        } catch (IOException e) {
            Log.e(LOG_TAG, "", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    } // makeHttpRequest

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
    } // makeURL

    public void updateUI(String http_result) {
        Log.v(LOG_TAG, "data provided is " + http_result.length() + " long");

        // ArrayList >> Adapter >> ListView
        ArrayList<Book> arrayOfBooks = QueryUtils.extractBooks(http_result);

        // we need a class for a userAdapter
        BookAdapter bookAdapter = new BookAdapter(this, arrayOfBooks);

        // We need a listView
        ListView lvBook = (ListView) findViewById(R.id.list_item);
        lvBook.setAdapter(bookAdapter);
    } // updateUI

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

}

