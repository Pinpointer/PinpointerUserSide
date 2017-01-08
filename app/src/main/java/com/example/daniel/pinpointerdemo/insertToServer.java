package com.example.daniel.pinpointerdemo;

import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.models.nosql.PinpointerCodesDO;

/**
 * Created by Daniel on 1/7/17.
 */

public class insertToServer extends AsyncTask<String,Void,Integer> {
    @Override
    protected Integer doInBackground(String... codes) {
        final DynamoDBMapper dynamoDBMapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
        final PinpointerCodesDO note = new PinpointerCodesDO(); // Initialize the Notes Object

        // The userId has to be set to user's Cognito Identity Id for private / protected tables.
        // User's Cognito Identity Id can be fetched by using:
        // AWSMobileClient.defaultMobileClient().getIdentityManager().getCachedUserID()
        note.setPinpointercode(codes[0]);

        AmazonClientException lastException = null;

        try {
            dynamoDBMapper.save(note);
        } catch (final AmazonClientException ex) {
            Log.e("Code", "Failed saving item : " + ex.getMessage(), ex);
            lastException = ex;
        }
        return 1;
    }
}
