package com.example.pustakbhandar;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    private static String BOOK_REQUEST_URL = "";
    private BookAdapter mAdapter;
    private EditText search_text_view;
    private Button search;
    private static final int BOOK_LOADER_ID = 1;
    private TextView mEmptyState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        search_text_view = findViewById(R.id.search_text);
        search = findViewById(R.id.search);
        mEmptyState = findViewById(R.id.empty_view);

        mAdapter = new BookAdapter(this, new ArrayList<Book>());
        ListView bookList = findViewById(R.id.list);
        bookList.setAdapter(mAdapter);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(BOOK_LOADER_ID, null, this);
        } else {
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            mEmptyState.setText(R.string.no_internet_connection);
        }

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (networkInfo != null && networkInfo.isConnected()) {
                    String searchValue = search_text_view.getText().toString();
                    if (searchValue.contains(" ")) {
                        searchValue = searchValue.replace(" ", "+");
                    }

                    StringBuilder sb = new StringBuilder();
                    sb.append("https://www.googleapis.com/books/v1/volumes?q=").append(searchValue).append("&filter=paid-ebooks&maxResults=40");
                    BOOK_REQUEST_URL = sb.toString();
                    restartLoader();
                    //  Log.i(LOG_TAG, "Search value: " + mSearchViewField.getQuery().toString());
                } else {
                    // Clear the adapter of previous book data
                    mAdapter.clear();
                    // Set mEmptyStateTextView visible
                    mEmptyState.setVisibility(View.VISIBLE);
                    // ...and display message: "No internet connection."
                    mEmptyState.setText(R.string.no_internet_connection);
                }

            }
        });


        bookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book book = mAdapter.getItem(position);
                assert book != null;
                assert book.getBuyLink() != null;
                Uri earthquakeUri = Uri.parse(book.getBuyLink());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);
                startActivity(websiteIntent);
            }
        });


    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle bundle) {
        return new BookLoader(this, BOOK_REQUEST_URL);
    }


    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> data) {
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);
        mAdapter.clear();
        if (data != null && !data.isEmpty()) {
            mAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        mAdapter.clear();
    }


    public void restartLoader() {
        mEmptyState.setVisibility(View.GONE);
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.VISIBLE);
        getLoaderManager().restartLoader(BOOK_LOADER_ID, null, MainActivity.this);
    }

}
