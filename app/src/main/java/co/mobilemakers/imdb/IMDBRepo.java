package co.mobilemakers.imdb;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by david.burgos on 10/02/2015.
 */
public class IMDBRepo {

    private String title;
    private String year;
    private String plot;
    private String url ;
    private Bitmap Image;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setImageFromURL(String stringURL){

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

        this.setUrl(stringURL);
        this.setImage(bitmap);
    }

    public Bitmap getImage() {
        return Image;
    }

    public void setImage(Bitmap image) {
        Image = image;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title + ": " + plot;
    }
}
