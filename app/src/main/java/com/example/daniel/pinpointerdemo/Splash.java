package com.example.daniel.pinpointerdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


/**
 * Created by Daniel on 11/9/16.
 */

//Splash Screen
public class Splash extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
