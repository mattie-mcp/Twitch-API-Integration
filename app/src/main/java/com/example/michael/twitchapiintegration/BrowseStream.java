package com.example.michael.twitchapiintegration;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.*;

import org.json.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI;
import java.util.ArrayList;

public class BrowseStream extends AppCompatActivity {
    private String channelId;
    private String vUrl;
    private String vHeight;
    private String streamTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_stream);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        channelId = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        toolbar.setTitle(channelId + " Stream");
        RetrieveStreams t = new RetrieveStreams();
        t.execute(channelId);
        //MainActivity.getData data = new MainActivity.getData();

        WebView video = (WebView) findViewById(R.id.webView);
        TextView title = (TextView) findViewById(R.id.Streamtitle);

        title.setText(streamTitle);
        try{
            video.setWebChromeClient(new WebChromeClient());
            WebSettings webSettings = video.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setLoadWithOverviewMode(true);
            video.loadUrl(vUrl);
        }
        catch(Exception ex){

        }
        //video.setMinimumHeight();
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
                URL url = new URL("https://api.twitch.tv/kraken/streams/" + channelId); //url for the API
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

            findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
            super.onPostExecute(s);
            try {
                JSONObject json = new JSONObject(s);
                JSONArray streams= json.getJSONArray("stream"); //grabs the "streams" array from the main JSON object
                for(int i=0; i < streams.length(); i++) { //itterates through JSON objects in streams
                    //String name = streams.getJSONObject(i).getJSONObject("channel").getString("display_name");//grabs the displayname of the streamers
                    vUrl = streams.getJSONObject(i).getJSONObject("channel").getString("url");
                    vHeight = streams.getJSONObject(i).getJSONObject("channel").getString("url");
                    //previews.add(streams.getJSONObject(i).getJSONObject("preview").getString("large"));
                    //channels.add(name);
                    //gameTitles.add(game);
                }
               // mAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                Log.e("JSONException", "Error: " + e.toString());
            } // catch (JSONException e)
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        }
    }

}
