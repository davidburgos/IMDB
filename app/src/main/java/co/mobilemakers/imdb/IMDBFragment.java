package co.mobilemakers.imdb;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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
    ImageView mPoster;
    String[] mMovie;
    EditText mEditTextTitle, mEditTextYear;

    public IMDBFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        wireUpViews(rootView);
        ImageButton button = (ImageButton)rootView.findViewById(R.id.imageButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FetchReposTask().execute(mEditTextTitle.getText().toString(),
                                             mEditTextYear.getText().toString());
            }
        });
        return rootView;
    }

    private void wireUpViews(View rootView) {
        mEditTextTitle = (EditText)rootView.findViewById(R.id.edit_text_title);
        mEditTextYear = (EditText)rootView.findViewById(R.id.edit_text_year);
        mTitle  = (TextView)rootView.findViewById(R.id.text_view_title_movie);
        mYear   = (TextView)rootView.findViewById(R.id.text_view_year);
        mPlot   = (TextView)rootView.findViewById(R.id.text_view_plot);
        mPoster = (ImageView)rootView.findViewById(R.id.image_view_poster);
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

    private void parseResponse(String response){

        try {
            if(mMovie == null){
                mMovie = new String[4];
            }

            JSONObject responseJsonArray = new JSONObject(response);

            mMovie[0] = responseJsonArray.getString(IMDB_TITLE);
            mMovie[1] = responseJsonArray.getString(IMDB_YEAR);
            mMovie[2] = responseJsonArray.getString(IMDB_PLOT);
            mMovie[3] = responseJsonArray.getString(IMDB_POSTER);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class FetchReposTask extends AsyncTask<String, Void, String[]> {

        Bitmap mBitmap;

        @Override
        protected void onPostExecute(String[] response) {
            super.onPostExecute(response);

            mTitle.setText(mMovie[0]);
            mYear.setText(mMovie[1]);
            mPlot.setText(mMovie[2]);
            if(mBitmap != null){
                mPoster.setImageBitmap(mBitmap);
            }
        }

        private Bitmap downloadImage(String stringURL){
            Bitmap bitmap = null;
            try {
                URL url = new URL(stringURL);
                URI uri = new URI(url.getProtocol(), url.getHost(),url.getPath(), url.getQuery(), null);
                HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
                int bufferSize = 1024;
                byte[] buffer = new byte[bufferSize];
                int len;
                while ((len = input.read(buffer)) != -1) {
                    byteBuffer.write(buffer, 0, len);
                }
                byte[] img = byteBuffer.toByteArray();
                byteBuffer.flush();
                byteBuffer.close();
                input.close();
                bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
            }  catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected String[] doInBackground(String... params) {

            String title,year;
            final String[] result = new String[2];

            if(params.length >= 1){
                title = params[0];
                year  = params[1];
            }else{
                title = "Maze runner";
                year  = "2014";
            }

            try {
                URL url = ConstructURLQuery(title, year);
                HttpURLConnection URLConnection = (HttpURLConnection) url.openConnection();
                try {
                    String response = readFullResponse(URLConnection.getInputStream());
                    parseResponse(response);

                    if(!mMovie[3].isEmpty()){
                       mBitmap = downloadImage(mMovie[3]);
                    }
                } catch(IOException e){
                    e.printStackTrace();
                }finally{
                    URLConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }
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
