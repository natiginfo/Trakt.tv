package com.contafe.trakttv.activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
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

public class MainActivity extends AppCompatActivity implements ActivityInterface {
    private RecyclerView moviesRV;
    private LinearLayoutManager linearLayoutManager;
    private ConnectionDetector connectionDetector;
    private LinearLayout progressBarLayout;
    private ArrayList<Movie> moviesList;
    private int currentPage = 0;
    private int pageLimit = 1;
    private MoviesAdapter moviesAdapter;
    private AsyncHttpClient asyncHttpClient;
    private FloatingActionButton floatingActionButton;
    private CustomAlertDialog customAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeUI();
        initializeClickListeners();
        initializeUtilities();
        scrollListener();
        loadMovies(1);
    }

    private void loadMovies(int page) {
        if (connectionDetector.isConnectiongToInternet()) {
            // creating url for loading popular movies
            String pagination = "&page=" + page + "&limit=10";
            String url = Constants.API_URL + Constants.MOVIES + Constants.POPULAR_MOVIES + Constants.IMAGES + pagination;
            System.out.println("URL: " + url);
            // setting headers
            asyncHttpClient.addHeader(Constants.CONTENT_TYPE_HEADER, Constants.CONTENT_TYPE);
            asyncHttpClient.addHeader(Constants.API_VERSION_HEADER, Constants.API_VERSION);
            asyncHttpClient.addHeader(Constants.API_KEY_HEADER, Constants.API_KEY);
            // starting post
            asyncHttpClient.get(url, new AsyncHttpResponseHandler() {
                @Override
                public void onStart() {
                    System.out.println("Started");
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    // creating string from bytes
                    String response = new String(responseBody);
                    progressBarLayout.setVisibility(View.GONE);
                    System.out.println("Success");
                    for (Header header : headers) {
                        if (header.getName().equals("X-Pagination-Page")) {
                            currentPage = Integer.valueOf(header.getValue());
                        }
                        if (header.getName().equals("X-Pagination-Page-Count")) {
                            pageLimit = Integer.valueOf(header.getValue());
                        }
                    }
                    // printing current page
                    System.out.println("Current page: " + currentPage);
                    try {
                        JSONArray responseArray = new JSONArray(response);
                        for (int i = 0; i < responseArray.length(); i++) {
                            JSONObject movieObj = responseArray.getJSONObject(i);
                            Movie newMovie = new Movie(movieObj);
                            moviesList.add(newMovie);
                        }
                        System.out.println("Total elements: " + moviesList.size());
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

    @Override
    public void initializeUI() {
        // Recycler view
        moviesRV = (RecyclerView) findViewById(R.id.recyclerViewMoviesList);
        linearLayoutManager = new LinearLayoutManager(this);
        moviesRV.setLayoutManager(linearLayoutManager);
        progressBarLayout = (LinearLayout) findViewById(R.id.progressBarLayout);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fabSearch);
    }

    @Override
    public void initializeClickListeners() {
        // initializing click listener for floating action button
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(searchIntent);
            }
        });
    }

    @Override
    public void initializeUtilities() {
        // initializing connection detector for checking internet connection
        connectionDetector = new ConnectionDetector(this);
        // initializing arraylist
        moviesList = new ArrayList<Movie>();
        // initializing adapter
        moviesAdapter = new MoviesAdapter(this, moviesList);
        // attaching adapter to RecyclerView
        moviesRV.setAdapter(moviesAdapter);
        // initializing asynchttpclient
        asyncHttpClient = new AsyncHttpClient();
    }

    private void scrollListener() {
        // printing some details
        System.out.println("Limit:" + pageLimit);
        System.out.println("Current:" + currentPage);
        System.out.println(moviesList.size());
        // setting custom scroll listener
        moviesRV.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                if (currentPage <= pageLimit) {
                    System.out.println("Limit:" + pageLimit);
                    currentPage++;
                    loadMovies(currentPage);
                    System.out.println("Load more");
                }
                // show progressbar
                progressBarLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    // refreshing RecyclerView
    private void updateRecyclerView() {
        moviesAdapter.notifyDataSetChanged();
    }

    // implementing custom alert dialog
    private void showDialog(String message) {
        customAlertDialog = new CustomAlertDialog(this) {
            @Override
            public void buttonClicked() {
                loadMovies(1);
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
