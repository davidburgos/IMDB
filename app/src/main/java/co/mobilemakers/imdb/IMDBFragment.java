package co.mobilemakers.imdb;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class IMDBFragment extends Fragment {

    private final static String LOG_TAG = IMDBFragment.class.getSimpleName();
    private final static String IMDB_TITLE  = "Title";
    private final static String IMDB_YEAR   = "Year";
    private final static String IMDB_PLOT   = "Plot";
    private final static String IMDB_POSTER = "Poster";

    TextView mTitle, mYear, mPlot;

    public IMDBFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mTitle = (TextView)rootView.findViewById(R.id.text_view_title);
        mYear  = (TextView)rootView.findViewById(R.id.text_view_year);
        mPlot  = (TextView)rootView.findViewById(R.id.text_view_plot);
        new FetchReposTask().execute("Maze runner");
        return rootView;
    }


    private String readFullResponse(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String response ="";
        String line;
        while((line = bufferedReader.readLine()) != null){
            stringBuilder.append(line);
        }
        if(stringBuilder.length() > 0){
            response = stringBuilder.toString();
        }
        return response;
    }

    private String parseResponse(String response){

        List<String> repos = new ArrayList<>();
        try {
            JSONArray responseJsonArray = new JSONArray(response);
            JSONObject object;
            for(int i=0;i<responseJsonArray.length();i++){
                object = responseJsonArray.getJSONObject(i);
                mTitle.setText(object.getString(IMDB_TITLE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return TextUtils.join(", ", repos);
    }

    class FetchReposTask extends AsyncTask<String, Void, String> {

        String username="";
        String listOfRepos ="";

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            mTitle.setText(response);
            mYear.setText(response);
            mPlot.setText(response);
        }

        @Override
        protected String doInBackground(String... params) {
            String title,year;

            if(params.length >= 3){
                title = params[0];
                year  = params[1];
            }else{
                title = "Maze runner";
                year  = "2014";
            }

            try {
                URL url = ConstructURLQuery(title);
                HttpURLConnection URLConnection = (HttpURLConnection) url.openConnection();
               // HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                try {
                    String response = readFullResponse(URLConnection.getInputStream());
                    listOfRepos = parseResponse(response);
                } catch(IOException e){
                    e.printStackTrace();
                }finally{
                    URLConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return listOfRepos;
        }
    }

    private URL ConstructURLQuery(String title) throws MalformedURLException {
        final String IMDB_BASE_URL ="www.omdbapi.com";

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").
                authority(IMDB_BASE_URL).
                appendQueryParameter("t", title).
                appendQueryParameter("plot","short").
                appendQueryParameter("r","json");
        Uri uri = builder.build();
        Log.d(LOG_TAG, "Build URI: " + uri.toString());

        return new URL(uri.toString());
    }
}
