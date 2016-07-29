/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.booksearch;

        import android.util.Log;
        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;
        import java.util.ArrayList;

/**
 * Helper methods related to requesting and receiving book data from USGS.
 */
public final class QueryUtils {

    /** Sample JSON response for a Google Books API query for testing without going out to internet*/
    private static final String SAMPLE_JSON_RESPONSE =
            "{ \"kind\": \"books#volumes\", \"totalItems\": 1140, \"items\": [ " +
            "{ \"kind\": \"books#volume\", \"id\": \"EF5txSsyBFUC\", \"etag\": \"B+goGXuajhk\", \"selfLink\": \"https://www.googleapis.com/books/v1/volumes/EF5txSsyBFUC\", \"volumeInfo\": { \"title\": \"Beginning Android 4\", \"authors\": [ \"Mark Murphy\", \"Grant Allen\" ], \"publisher\": \"Apress\", \"publishedDate\": \"2012-03-15\", \"description\": \"Beginning Android 4 is an update to Beginning Android 3, originally written by Mark Murphy. It is your first step on the path to creating marketable apps for the burgeoning Android Market, Amazon's Android Appstore, and more. Google’s Android operating-system has taken the industry by storm, going from its humble beginnings as a smartphone operating system to its current status as a platform for apps that run across a gamut of devices from phones to tablets to netbooks to televisions, and the list is sure to grow. Smart developers are not sitting idly by in the stands, but are jumping into the game of creating innovative and salable applications for this fast-growing, mobile- and consumer-device platform. If you’re not in the game yet, now is your chance! Beginning Android 4 is fresh with details on the latest iteration of the Android platform. Begin at the beginning by installing the tools and compiling a skeleton app. Move through creating layouts, employing widgets, taking user input, and giving back results. Soon you’ll be creating innovative applications involving multi-touch, multi-tasking, location-based feature sets using GPS. You’ll be drawing data live from the Internet using web services and delighting your customers with life-enhancing apps. Not since the PC era first began has there been this much opportunity for the common developer. What are you waiting for? Grab your copy of Beginning Android 4 and get started!\", \"industryIdentifiers\": [ { \"type\": \"ISBN_13\", \"identifier\": \"9781430239857\" }, { \"type\": \"ISBN_10\", \"identifier\": \"1430239859\" } ], \"readingModes\": { \"text\": true, \"image\": true }, \"pageCount\": 604, \"printType\": \"BOOK\", \"categories\": [ \"Computers\" ], \"maturityRating\": \"NOT_MATURE\", \"allowAnonLogging\": true, \"contentVersion\": \"1.4.3.0.preview.3\", \"imageLinks\": { \"smallThumbnail\": \"http://books.google.ie/books/content?id=EF5txSsyBFUC&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api\", \"thumbnail\": \"http://books.google.ie/books/content?id=EF5txSsyBFUC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api\" }, \"language\": \"en\", \"previewLink\": \"http://books.google.ie/books?id=EF5txSsyBFUC&pg=PA206&dq=android&hl=&cd=1&source=gbs_api\", \"infoLink\": \"http://books.google.ie/books?id=EF5txSsyBFUC&dq=android&hl=&source=gbs_api\", \"canonicalVolumeLink\": \"http://books.google.ie/books/about/Beginning_Android_4.html?hl=&id=EF5txSsyBFUC\" }, \"saleInfo\": { \"country\": \"IE\", \"saleability\": \"FOR_SALE\", \"isEbook\": true, \"listPrice\": { \"amount\": 39.77, \"currencyCode\": \"EUR\" }, \"retailPrice\": { \"amount\": 27.84, \"currencyCode\": \"EUR\" }, \"buyLink\": \"http://books.google.ie/books?id=EF5txSsyBFUC&dq=android&hl=&buy=&source=gbs_api\", \"offers\": [ { \"finskyOfferType\": 1, \"listPrice\": { \"amountInMicros\": 3.977E7, \"currencyCode\": \"EUR\" }, \"retailPrice\": { \"amountInMicros\": 2.784E7, \"currencyCode\": \"EUR\" } } ] }, \"accessInfo\": { \"country\": \"IE\", \"viewability\": \"PARTIAL\", \"embeddable\": true, \"publicDomain\": false, \"textToSpeechPermission\": \"ALLOWED\", \"epub\": { \"isAvailable\": true, \"acsTokenLink\": \"http://books.google.ie/books/download/Beginning_Android_4-sample-epub.acsm?id=EF5txSsyBFUC&format=epub&output=acs4_fulfillment_token&dl_type=sample&source=gbs_api\" }, \"pdf\": { \"isAvailable\": true, \"acsTokenLink\": \"http://books.google.ie/books/download/Beginning_Android_4-sample-pdf.acsm?id=EF5txSsyBFUC&format=pdf&output=acs4_fulfillment_token&dl_type=sample&source=gbs_api\" }, \"webReaderLink\": \"http://books.google.ie/books/reader?id=EF5txSsyBFUC&hl=&printsec=frontcover&output=reader&source=gbs_api\", \"accessViewStatus\": \"SAMPLE\", \"quoteSharingAllowed\": false }, \"searchInfo\": { \"textSnippet\": \"&lt;manifest xmlns:\\u003cb\\u003eandroid\\u003c/b\\u003e=&quot;http://schemas.\\u003cb\\u003eandroid\\u003c/b\\u003e.com/apk/res/\\u003cb\\u003eandroid\\u003c/b\\u003e&quot; package\\u003cbr\\u003e\\n=&quot;com.commonsware.\\u003cb\\u003eandroid\\u003c/b\\u003e.rotation.three&quot; \\u003cb\\u003eandroid\\u003c/b\\u003e:versionCode=&quot;1&quot;\\u003cb\\u003eandroid\\u003c/b\\u003e:\\u003cbr\\u003e\\nversionName=&quot;1.0.0&quot;&gt; &lt;uses-sdk \\u003cb\\u003eandroid\\u003c/b\\u003e:minSdkVersion=&quot;5&quot;&nbsp;...\" } }" +
            "," +
            "{ \"kind\": \"books#volume\", \"id\": \"IEk2m00o9_IC\", \"etag\": \"HXa3MDUWPSQ\", \"selfLink\": \"https://www.googleapis.com/books/v1/volumes/IEk2m00o9_IC\", \"volumeInfo\": { \"title\": \"Android Apps Security\", \"authors\": [ \"Sheran Gunasekera\" ], \"publisher\": \"Apress\", \"publishedDate\": \"2012-09-12\", \"description\": \"Android Apps Security provides guiding principles for how to best design and develop Android apps with security in mind. It explores concepts that can be used to secure apps and how developers can use and incorporate these security features into their apps. This book will provide developers with the information they need to design useful, high-performing, and secure apps that expose end-users to as little risk as possible. Overview of Android OS versions, features, architecture and security. Detailed examination of areas where attacks on applications can take place and what controls should be implemented to protect private user data In-depth guide to data encryption, authentication techniques, enterprise security and applied real-world examples of these concepts What you’ll learn How to identify data that should be secured How to use the Android APIs to ensure confidentiality and integrity of data How to build secure apps for the enterprise About Public Key Infrastructure, encryption APIs and how to implement them in apps About owners, access control lists and permissions to allow user control over App properties About client-server apps and how to manage authentication, transport layer encryption and server-side security Who this book is for This book is for intermediate and experienced Android app developers that are already familiar with writing apps from scratch. It discusses mechanisms on how apps can be secured so that private, end-user data is kept secure on the device and while in transit. If you’re just embarking on the path to Android development, then this book may prove to be a useful companion to other developer guides. Table of Contents Android Architecture & Security Controls The Foundation of an App Who Has Access? Designing and Developing 3 Sample Apps Using PKI & Encryption Interfacing with Web Services Writing for the Enterprise Designing and Developing 3 More Sample Apps Publishing and Selling Your Apps Malware, Spyware and Your End-User API Reference\", \"industryIdentifiers\": [ { \"type\": \"ISBN_13\", \"identifier\": \"9781430240624\" }, { \"type\": \"ISBN_10\", \"identifier\": \"1430240628\" } ], \"readingModes\": { \"text\": true, \"image\": true }, \"pageCount\": 248, \"printType\": \"BOOK\", \"categories\": [ \"Computers\" ], \"maturityRating\": \"NOT_MATURE\", \"allowAnonLogging\": true, \"contentVersion\": \"1.1.1.0.preview.3\", \"imageLinks\": { \"smallThumbnail\": \"http://books.google.ie/books/content?id=IEk2m00o9_IC&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api\", \"thumbnail\": \"http://books.google.ie/books/content?id=IEk2m00o9_IC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api\" }, \"language\": \"en\", \"previewLink\": \"http://books.google.ie/books?id=IEk2m00o9_IC&printsec=frontcover&dq=android&hl=&cd=2&source=gbs_api\", \"infoLink\": \"http://books.google.ie/books?id=IEk2m00o9_IC&dq=android&hl=&source=gbs_api\", \"canonicalVolumeLink\": \"http://books.google.ie/books/about/Android_Apps_Security.html?hl=&id=IEk2m00o9_IC\" }, \"saleInfo\": { \"country\": \"IE\", \"saleability\": \"NOT_FOR_SALE\", \"isEbook\": false }, \"accessInfo\": { \"country\": \"IE\", \"viewability\": \"PARTIAL\", \"embeddable\": true, \"publicDomain\": false, \"textToSpeechPermission\": \"ALLOWED\", \"epub\": { \"isAvailable\": true, \"acsTokenLink\": \"http://books.google.ie/books/download/Android_Apps_Security-sample-epub.acsm?id=IEk2m00o9_IC&format=epub&output=acs4_fulfillment_token&dl_type=sample&source=gbs_api\" }, \"pdf\": { \"isAvailable\": true, \"acsTokenLink\": \"http://books.google.ie/books/download/Android_Apps_Security-sample-pdf.acsm?id=IEk2m00o9_IC&format=pdf&output=acs4_fulfillment_token&dl_type=sample&source=gbs_api\" }, \"webReaderLink\": \"http://books.google.ie/books/reader?id=IEk2m00o9_IC&hl=&printsec=frontcover&output=reader&source=gbs_api\", \"accessViewStatus\": \"SAMPLE\", \"quoteSharingAllowed\": false }, \"searchInfo\": { \"textSnippet\": \"This book will provide developers with the information they need to design useful, high-performing, and secure apps that expose end-users to as little risk as possible. Overview of Android OS versions, features, architecture and security.\" } } " +
            " ]\n }";

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Return a list of {@link Book} objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<Book> extractBooks(String query_results) {

        // Create an empty ArrayList that we can start adding books to
        ArrayList<Book> books = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the SAMPLE_JSON_RESPONSE string
            JSONObject baseJsonResponse = new JSONObject(query_results);

            // Extract the JSONArray associated with the key called "items",
            // which represents a list of features (or books).
            JSONArray bookArray = baseJsonResponse.getJSONArray("items");

            // For each book in the bookArray, create an {@link Book} object
            for (int i = 0; i < bookArray.length(); i++) {

                // Get a single book at position i within the list of books
                JSONObject currentBook = bookArray.getJSONObject(i);

                // For a given book, extract the JSONObject associated with the
                // key called "properties", which represents a list of all properties
                // for that book.
                JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");

                // Extract the value for the key called "title"
                String title = volumeInfo.getString("title");

                // Extract array of authors and turn it into a single string...
                String authors = "";
                try {
                    JSONArray authorsArray = volumeInfo.getJSONArray("authors");
                    // authors key exists so..
                    // Except for first(0) add a comma and a space between entries
                    for (int j = 0; j < authorsArray.length(); j++) {
                        if (j >0){
                            // not the first so add a comma and space
                            authors += ", ";
                        }
                        // add author to the string
                        authors += authorsArray.getString(j);
                    }
                } catch (JSONException e) {
                    // authors key doesn't exist so return an empty list
                    authors = "";
                }

                // Create a new {@link Book} object with the title and authors from the response.
                Book book = new Book(title, authors);

                // Add the new {@link Book} to the list of books.
                books.add(book);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the book JSON results", e);
        }

        // Return the list of books
        return books;
    }

}