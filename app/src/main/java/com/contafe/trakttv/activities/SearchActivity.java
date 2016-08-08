package com.contafe.trakttv.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.contafe.trakttv.R;
import com.contafe.trakttv.adapters.MoviesAdapter;
import com.contafe.trakttv.defaults.Constants;
import com.contafe.trakttv.interfaces.ActivityInterface;
import com.contafe.trakttv.interfaces.EndlessRecyclerOnScrollListener;
import com.contafe.trakttv.models.Movie;
import com.contafe.trakttv.utilities.ConnectionDetector;
import com.contafe.trakttv.utilities.CustomAlertDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity implements ActivityInterface {
    private AsyncHttpClient asyncHttpClient;
    private EditText editText;
    private ConnectionDetector connectionDetector;
    private RecyclerView moviesRV;
    private LinearLayoutManager linearLayoutManager;
    private LinearLayout progressBarLayout;
    private ArrayList<Movie> moviesList;
    private int currentPage = 1;
    private int pageLimit = 1;
    private MoviesAdapter moviesAdapter;
    private CustomAlertDialog customAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initializeUI();
        initializeClickListeners();
        initializeUtilities();
        scrollListener();
        startSearch();
    }

    private void scrollListener() {
        System.out.println("Limit:" + pageLimit);
        System.out.println("Current:" + currentPage);
        System.out.println(moviesList.size());
        // setting custom scroll listener
        moviesRV.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                System.out.println("Load");
                if (currentPage <= pageLimit) {
                    System.out.println("Limit:" + pageLimit);
                    currentPage++;
                    loadMovies(editText.getText().toString(), currentPage);
                    System.out.println("Load more");
                }
                progressBarLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void loadMovies(String searchTerm, int page) {
        if (connectionDetector.isConnectiongToInternet()) {
            // building search url
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("https://api.trakt.tv/search/movie?query=");
            stringBuilder.append(searchTerm);
            stringBuilder.append("&page=" + page + "&limit=10&extended=images");
            System.out.println("URL: " + stringBuilder.toString());
            // adding headers
            asyncHttpClient.addHeader(Constants.CONTENT_TYPE_HEADER, Constants.CONTENT_TYPE);
            asyncHttpClient.addHeader(Constants.API_VERSION_HEADER, Constants.API_VERSION);
            asyncHttpClient.addHeader(Constants.API_KEY_HEADER, Constants.API_KEY);
            asyncHttpClient.get(stringBuilder.toString(), new AsyncHttpResponseHandler() {
                @Override
                public void onStart() {
                    System.out.println("Started");
                    // default log warning is not necessary, because this method is just optional notification
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String response = new String(responseBody);
                    progressBarLayout.setVisibility(View.GONE);
                    System.out.println("Success");

                    // checking for headers
                    for (Header header : headers) {
                        if (header.getName().equals("X-Pagination-Page")) {
                            currentPage = Integer.valueOf(header.getValue());
                        }
                        if (header.getName().equals("X-Pagination-Page-Count")) {
                            pageLimit = Integer.valueOf(header.getValue());
                        }
                    }

                    System.out.println("Current page: " + currentPage);
                    try {
                        JSONArray responseArray = new JSONArray(response);
                        for (int i = 0; i < responseArray.length(); i++) {
                            JSONObject movieObj = responseArray.getJSONObject(i);
                            Movie newMovie = new Movie(movieObj.getJSONObject("movie"));
                            moviesList.add(newMovie);
                        }
                        updateRecyclerView();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    String response = new String(responseBody);
                    progressBarLayout.setVisibility(View.GONE);
                    Log.d("Error", response);
                }
            });
        } else {
            // show error using my dialog
            showDialog("You do not have internet connection. Please, check and try again.");
        }
    }


    private void updateRecyclerView() {
        moviesAdapter.notifyDataSetChanged();
    }


    @Override
    public void initializeUI() {
        editText = (EditText) findViewById(R.id.searchEditText);
        // showing keyboard when activity is opened
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        // initializing recyclerview and it's helpers
        moviesRV = (RecyclerView) findViewById(R.id.searchRecyclerViewMoviesList);
        linearLayoutManager = new LinearLayoutManager(this);
        moviesRV.setLayoutManager(linearLayoutManager);
        progressBarLayout = (LinearLayout) findViewById(R.id.searchProgressBarLayout);
    }

    @Override
    public void initializeClickListeners() {

    }

    @Override
    public void initializeUtilities() {
        moviesList = new ArrayList<Movie>();
        connectionDetector = new ConnectionDetector(this);
        moviesAdapter = new MoviesAdapter(this, moviesList);
        moviesRV.setAdapter(moviesAdapter);
        asyncHttpClient = new AsyncHttpClient();
    }

    private void startSearch() {

        // setting changelistener
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editText.getText().toString().trim().length() == 0) {
                    // if edittext is empty, delete all loaded elements
                    moviesList.clear();
                    moviesAdapter.notifyDataSetChanged();
                    currentPage = 1;
                    pageLimit = 1;
                    asyncHttpClient.cancelAllRequests(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                System.out.println("After changed");
                System.out.println("Total elements: " + moviesList.size());
                if (editText.getText().toString().trim().length() != 0) {
                    // clearing movieList
                    moviesList.clear();
                    moviesAdapter.notifyDataSetChanged();
                    // resetting some parameters for pagination
                    currentPage = 1;
                    pageLimit = 1;
                    // cancelling pending requests
                    asyncHttpClient.cancelAllRequests(true);
                    // calling search method
                    loadMovies(editText.getText().toString(), 1);
                } else {
                    moviesList.clear();
                    moviesAdapter.notifyDataSetChanged();
                    currentPage = 1;
                    pageLimit = 1;
                    asyncHttpClient.cancelAllRequests(true);
                }
            }
        });
    }

    // implementing custom alert dialog
    private void showDialog(String message) {
        customAlertDialog = new CustomAlertDialog(this) {
            @Override
            public void buttonClicked() {
                loadMovies(editText.getText().toString(), 1);
            }
        };
        customAlertDialog.setIconResource(R.drawable.ic_info_outline_white_48dp);
        customAlertDialog.setBackgroundColor(R.color.colorPrimary);
        customAlertDialog.setTextColor(R.color.colorText);
        customAlertDialog.setTitle("Error");
        customAlertDialog.setMessage(message);
        customAlertDialog.setButtonText("Retry");
        customAlertDialog.setButtonStyle(getResources().getDrawable(R.drawable.dialog_button_style));
        customAlertDialog.setCancelable(false);
        customAlertDialog.show();
    }
}
