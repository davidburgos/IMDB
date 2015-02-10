package co.mobilemakers.imdb;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class IMDBFragment extends ListFragment {

    private final static String LOG_TAG = IMDBFragment.class.getSimpleName();
    private final static String IMDB_TITLE  = "Title";
    private final static String IMDB_PLOT   = "Plot";
    private final static String IMDB_YEAR   = "Year";
    private final static String IMDB_POSTER = "Poster";

    IMDBRepoAdapter mAdapter;

    TextView mTitle, mYear, mPlot;
    ImageView mPoster;
    EditText mEditTextTitle, mEditTextYear;

    public IMDBFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        wireUpViews(rootView);
        prepareButton(rootView);
        return rootView;
    }

    private void prepareButton(View rootView) {
        ImageButton button = (ImageButton)rootView.findViewById(R.id.imageButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchReposInQueue(mEditTextTitle.getText().toString(),
                                  mEditTextYear.getText().toString());
            }
        });
    }

    private void wireUpViews(View rootView) {
        mEditTextTitle = (EditText)rootView.findViewById(R.id.edit_text_title);
        mEditTextYear = (EditText)rootView.findViewById(R.id.edit_text_year);
        mTitle  = (TextView)rootView.findViewById(R.id.text_view_title_movie);
        mYear   = (TextView)rootView.findViewById(R.id.text_view_year);
        mPlot   = (TextView)rootView.findViewById(R.id.text_view_plot);
        mPoster = (ImageView)rootView.findViewById(R.id.image_view_poster);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        prepareListView();
    }

    private void prepareListView() {
        List<IMDBRepo> repos = new ArrayList<>();
        mAdapter = new IMDBRepoAdapter(getActivity(), repos);
        setListAdapter(mAdapter);
    }

    private void fetchReposInQueue(String title, String year){
        try {
            URL url = ConstructURLQuery(title, year);
            Request request = new Request.Builder().url(url.toString()).build();
            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    String responseString = response.body().string();
                    final List<IMDBRepo> listOfRepos = parseResponse(responseString);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.clear();
                            mAdapter.addAll(listOfRepos);
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private List<IMDBRepo> parseResponse(String response){
        List<IMDBRepo> repos = new ArrayList<>();
        IMDBRepo repo;

        try {

            JSONObject responseJsonArray = new JSONObject(response);

            repo = new IMDBRepo();
            repo.setTitle(responseJsonArray.getString(IMDB_TITLE));
            repo.setPlot(responseJsonArray.getString(IMDB_PLOT));
            repo.setYear(responseJsonArray.getString(IMDB_YEAR));
            repo.setImageFromURL(responseJsonArray.getString(IMDB_POSTER));
            repos.add(repo);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return repos;
    }

    private URL ConstructURLQuery(String title, String year) throws MalformedURLException {

        final String IMDB_TITLE  = "t";
        final String IMDB_PLOT   = "plot";
        final String IMDB_RETURN = "r";
        final String IMDB_YEAR   = "y";

        final String IMDB_PLOT_VALUE   = "short";
        final String IMDB_RETURN_VALUE = "json";

        final String IMDB_BASE_URL = "www.omdbapi.com";
        final String IMDB_BASE_PROTOCOL = "http";

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(IMDB_BASE_PROTOCOL).
                authority(IMDB_BASE_URL).
                appendQueryParameter(IMDB_TITLE, title).
                appendQueryParameter(IMDB_PLOT,IMDB_PLOT_VALUE).
                appendQueryParameter(IMDB_RETURN,IMDB_RETURN_VALUE);

        if(!year.isEmpty()){
            builder.appendQueryParameter(IMDB_YEAR,year);
        }

        Uri uri = builder.build();
        Log.d(LOG_TAG, "Build URI: " + uri.toString());

        return new URL(uri.toString());
    }
}
