package com.example.pustakbhandar;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryUtils {

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e("My Activity:", "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    private QueryUtils() {
    }

    /**
     * Return a list of {@link Book} objects that has been built up from
     * parsing a JSON response.
     */
    private static List<Book> extractFeaturesFromJson(String bookJson) {

        if (TextUtils.isEmpty(bookJson))
            return null;

        // Create an empty ArrayList that we can start adding earthquakes to
        List<Book> books = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            JSONObject baseJsonResponse = new JSONObject(bookJson);

            // Extract the JSONArray associated with the key called "items",
            // which represents a list of books.
            JSONArray booksArray = baseJsonResponse.getJSONArray("items");

            // For each book in the booksArray, create an {@link Book} object
            for (int i = 0; i < booksArray.length(); i++) {

                // Get a single book at position i within the list of items (books)
                JSONObject currentBook = booksArray.getJSONObject(i);

                // For a given book, extract the JSONObject associated with the
                // key called "volumeInfo", which represents a list of all properties
                // for that book. + [authors] list
                JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");


                // Extract the value for the key called "author"
                String author = "";

                // Check if JSONArray exist
                if (volumeInfo.has("authors")) {
                    JSONArray authors = volumeInfo.getJSONArray("authors");

                    // Check JSONArray Returns true if this object has no mapping for name or if it has a mapping whose value is NULL
                    if (!volumeInfo.isNull("authors")) {
                        // Get 1st element
                        for (int j =0;j<authors.length();j++) {
                            author = author + authors.getString(j) + ", ";
                        }
                        author = author.toUpperCase();
                    } else {
                        // assign info about missing info about author
                        author = "*** unknown author ***";
                    }
                } else {
                    // assign info about missing info about author
                    author = "*** missing info of authors ***";
                }

                JSONObject saleInfo = currentBook.getJSONObject("saleInfo");
                JSONObject retailPrice = saleInfo.getJSONObject("retailPrice");

                String title = volumeInfo.getString("title");
                String language = volumeInfo.getString("language");

                double amount = retailPrice.getDouble("amount");
                String currency = retailPrice.getString("currencyCode");
                String price = amount + " " + currency;

                String buyLink = (String) saleInfo.get("buyLink");

                JSONObject image = volumeInfo.getJSONObject("imageLinks");
                String imageLink = image.getString("smallThumbnail");

                StringBuilder stringBuilder = new StringBuilder();

                Pattern p = Pattern.compile("id=(.*?)&");
                Matcher m = p.matcher(imageLink);
                if (m.matches()) {
                    String id = m.group(1);
                    imageLink = String.valueOf(stringBuilder.append("https://books.google.com/books/content/images/frontcover/").append(id).append("?fife=w300"));
                }

                Book bookItem = new Book(title, author, price, language, buyLink, imageLink);
                books.add(bookItem);

            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return books;
    }

    public static List<Book> fetchBookData(String requestUrl) {
        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return extractFeaturesFromJson(jsonResponse);
    }
}
