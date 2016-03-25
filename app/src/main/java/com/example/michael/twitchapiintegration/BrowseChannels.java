package com.example.michael.twitchapiintegration;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.widget.*;
import org.json.*;
import java.io.*;
import java.net.*;

public class BrowseChannels extends AppCompatActivity {
    String channelId;
    String status;
    String profileImageUrl;
    String bannerImageUrl;
    String followers;
    String views;
    TextView followersView;
    TextView viewerText;
    TextView titleBanner;
    ImageView channelBanner;
    ImageView profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_channels);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        titleBanner = (TextView) findViewById(R.id.channelTitle);       //Title
        followersView = (TextView) findViewById(R.id.followerText);      //How many followers channel has
        viewerText = (TextView) findViewById(R.id.viewerText);        //How many viewers are watching stream
        profilePic = (ImageView) findViewById(R.id.profilePic);         //Profile Pic
        channelBanner = (ImageView) findViewById(R.id.channelBanner);   //Channel logo

        channelId = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        RetrieveStreams t = new RetrieveStreams();
        t.execute(channelId);
    }

    public class RetrieveStreams extends AsyncTask<String, String, String> {
        HttpURLConnection urlConnection;
        @Override
        protected String doInBackground(String... args) {
            StringBuilder result = new StringBuilder();
            try {
                //this is the section that grabs the JSON array from the link and makes it into a string to use later
                if (channelId.contains(" ")){
                    channelId = channelId.replace(" ", "%20");
                }
                URL url = new URL("https://api.twitch.tv/kraken/channels/" + channelId); //url for the API
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            }catch( Exception e) {
                e.printStackTrace();
            }
            finally {
                urlConnection.disconnect();
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String s) {

            //findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
            super.onPostExecute(s);
            try {
                JSONObject streams = new JSONObject(s);
                profileImageUrl = streams.getString("profile_banner");
                bannerImageUrl = streams.getString("logo");
                status = streams.getString("status");
                followers = streams.getString("followers");
                views = streams.getString("views");

                titleBanner.setText(channelId);
                viewerText.setText(views);
                followersView.setText(followers);
                if (!bannerImageUrl.equals("")){
                    channelBanner.setImageURI(Uri.parse(bannerImageUrl));
                }
                if (!profileImageUrl.equals("")){
                    profilePic.setImageURI(Uri.parse(profileImageUrl));
                }


            } catch (JSONException e) {
                Log.e("JSONException", "Error: " + e.toString());
            }
            //findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        }
    }


}
