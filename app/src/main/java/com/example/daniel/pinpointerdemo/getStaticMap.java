package com.example.daniel.pinpointerdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Daniel on 1/7/17.
 */

public class getStaticMap extends AsyncTask<String,Void,Bitmap> {
    private Exception exception;
    private ImageView image;

    public getStaticMap(ImageView image){
        this.image = image;
    }

    protected Bitmap doInBackground(String... urls) {

        try {
            URL url = new URL(urls[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    protected void onPostExecute(Bitmap map) {
        // TODO: check this.exception
        image.setImageBitmap(map);
    }
}
