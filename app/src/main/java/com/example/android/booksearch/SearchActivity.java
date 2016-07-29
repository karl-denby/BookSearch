package com.example.android.booksearch;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

    // Stores our search result string, global, save onPause and restore onResume
    String http_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);// Link: Search button and Text entered in TextEdit
        //       Define an onClick that makes a url, then calls UI update.
        final EditText edt_title = (EditText) findViewById(R.id.edt_title);
        Button btn_search = (Button) findViewById(R.id.btn_search);

        assert btn_search != null;
        btn_search.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                URL url;
                String search_string;

                // Check for internet connectivity
                // .. then check for valid input
                // ..  both ok then run the search
                if (!networkAvailable()) {
                    Toast.makeText(SearchActivity.this, getString(R.string.no_internet),
                            Toast.LENGTH_SHORT).show();
                } else if (edt_title.getText().toString().equals("")) {
                    Toast.makeText(SearchActivity.this, getString(R.string.no_search_terms),
                            Toast.LENGTH_SHORT).show();
                } else {
                    search_string = edt_title.getText().toString();
                    Toast.makeText(SearchActivity.this, getString(R.string.searching_for) + search_string,
                            Toast.LENGTH_SHORT).show();

                    // Make a search URL
                    url = makeURL(search_string);

                    // Pass to the UI update code which should get data, then update the display
                    BooksAsyncTask results = new BooksAsyncTask();
                    results.execute(url);
                }
            }
        });

        // Create a TextView with instructions
        TextView txtInstruct = (TextView) findViewById(R.id.instructions);
        txtInstruct.setText(R.string.instructions);

        // Display this textView in the listView when its empty
        ListView list_item = (ListView) findViewById(R.id.list_item);
        list_item.setEmptyView(txtInstruct);
    } //onCreate

    @Override
    protected void onPause() {
        super.onPause();

        // Save the book values
        SharedPreferences sharedPref = SearchActivity.this.getSharedPreferences("BookSearch", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("http_result", http_result);
        editor.apply();

    } // onPause

    @Override
    protected void onResume() {
        super.onResume();

        // Restore the book values
        SharedPreferences sharedPref = SearchActivity.this.getSharedPreferences("BookSearch", Context.MODE_PRIVATE);
        http_result = sharedPref.getString("http_result", "{}");
        updateUI(http_result);

    } // onResume

    /**
     * Will check for null result which mean no interface is online
     */
    private boolean networkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Run the Query in a Thread and when it returns store result and call UI update code
     */
    private class BooksAsyncTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {

            String result = "";
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
            http_result = result;  // save for later restoration
            updateUI(result);
        }
    } // BooksAsyncTask

    /**
     * @param url where is our source data
     * @return string with our JSON data as a String (can save this easily)
     * @throws IOException, if something went wrong on the Internet
     */
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
            Log.e(LOG_TAG, "HTTP IOException: ", e);
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

    /**
     * @param search_string what they user typed in
     * @return url for google books api
     */
    public URL makeURL(String search_string) {
        String base_url = "https://www.googleapis.com/books/v1/volumes?q=";
        String end_url = "&maxResults=10";
        String search_terms[];
        String search_term_sep = "+";
        String queryUrl = "";
        URL url;

        // break up search terms
        search_terms = search_string.split(" ");

        // if one word assemble they query
        if (search_terms.length == 1) {
            queryUrl = base_url + search_string + end_url;
        } else {
            // Multiple terms, same start to the url
            queryUrl += base_url;

            // include the first term
            queryUrl += search_terms[0];

            // add separator plus the next term
            for (int i = 1; i < search_terms.length; i++) {
                queryUrl += search_term_sep + search_terms[i] ;
            }

            // same end to the url
            queryUrl += end_url;
        }

        try {
            url = new URL(queryUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
        return url;
    } // makeURL

    /**
     * @param http_result: Convert into bookAdapter and assign it to listView.
     */
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
    } //readFromStream

}