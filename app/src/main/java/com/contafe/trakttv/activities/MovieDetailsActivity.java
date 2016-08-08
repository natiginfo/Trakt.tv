package com.contafe.trakttv.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.contafe.trakttv.R;
import com.contafe.trakttv.defaults.Constants;
import com.contafe.trakttv.interfaces.ActivityInterface;
import com.contafe.trakttv.models.Movie;
import com.contafe.trakttv.utilities.ConnectionDetector;
import com.contafe.trakttv.utilities.CustomAlertDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MovieDetailsActivity extends AppCompatActivity implements ActivityInterface {
    private Toolbar toolbar;
    private ImageView thumbIV;
    private TextView movieDetailsTV;
    private ConnectionDetector connectionDetector;
    private AsyncHttpClient asyncHttpClient;
    private CustomAlertDialog customAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        initializeUI();
        initializeClickListeners();
        initializeUtilities();
        // checking for internet connection
        downloadDetails();
    }

    @Override
    public void initializeUI() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        // setting toolbar as actionbar
        setSupportActionBar(toolbar);

        thumbIV = (ImageView) findViewById(R.id.detailsThumbIV);
        // setting picture for thumbImageView
        Picasso.with(this).load(getIntent().getStringExtra("thumb"))
                .error(R.drawable.default_movie_thumb)
                .placeholder(R.drawable.default_movie_thumb)
                .into(thumbIV);

        movieDetailsTV = (TextView) findViewById(R.id.movieDetailsOverviewTV);
    }

    @Override
    public void initializeClickListeners() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void initializeUtilities() {
        // setting title as name of movie
        setTitle(getIntent().getStringExtra("title"));
        // initializing connection detector
        connectionDetector = new ConnectionDetector(this);
    }

    private void downloadDetails() {
        if (connectionDetector.isConnectiongToInternet()) {

            // creating api URL
            String movie = "/" + getIntent().getStringExtra("slug");
            String url = Constants.API_URL + Constants.MOVIES + movie + Constants.FULL_OVERVIEW;
            // initializing asynchttp client and headers
            asyncHttpClient = new AsyncHttpClient();
            asyncHttpClient.addHeader(Constants.CONTENT_TYPE_HEADER, Constants.CONTENT_TYPE);
            asyncHttpClient.addHeader(Constants.API_VERSION_HEADER, Constants.API_VERSION);
            asyncHttpClient.addHeader(Constants.API_KEY_HEADER, Constants.API_KEY);
            // starting getting data from url
            asyncHttpClient.get(url, new AsyncHttpResponseHandler() {
                @Override
                public void onStart() {
                    System.out.println("Started");
                    // default log warning is not necessary, because this method is just optional notification
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String response = new String(responseBody);
                    try {
                        JSONObject movieDetails = new JSONObject(response);
                        movieDetailsTV.setText("Movie: " + getTitle() + " ("
                                + movieDetails.getString("tagline") + ")\n\n"
                                + "Year: " + movieDetails.getString("year") + "\n\n"
                                + "Overview: " + movieDetails.getString("overview"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    String response = new String(responseBody);
                    Log.d("Error", response);

                }
            });
        } else {
            showDialog("You do not have internet connection. Please, check and try again.");
        }
    }

    // implementing custom alert dialog
    private void showDialog(String message) {
        customAlertDialog = new CustomAlertDialog(this) {
            @Override
            public void buttonClicked() {
                downloadDetails();
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
