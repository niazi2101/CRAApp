package com.iiui.craapp.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.iiui.craapp.R;
import com.iiui.craapp.model.CrimeModelClass;
import com.iiui.craapp.util.Config;
import com.iiui.craapp.util.NotificationUtils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class CrimeDetailActivity extends AppCompatActivity {


    //Notification
    private static final String TAG = CrimeDetailActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private TextView txtRegId, txtMessage;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_detail);

       txtRegId = (TextView) findViewById(R.id.tvFirebaseRegID);
       txtMessage = (TextView) findViewById(R.id.tvFirebasePush);



        //Receive Notification
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    displayFirebaseRegId();


                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");
                    String lattitude = intent.getStringExtra("lattitude");
                    String longitude = intent.getStringExtra("longitude");
                    long crimeID = intent.getLongExtra("crimeID",0);


                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();


                    txtMessage.setText("Notification: " + message +
                            "\nLattiude: " + lattitude +
                            "\nLongitude: " + longitude +
                            "\nCrimeID: " + crimeID );

                    new GetCrimeDetailAsync().execute();

                }
            }
        };

        displayFirebaseRegId();



        //Retrieve data from server
        //new HttpRequestTask().execute();
    }

    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e(TAG, "Firebase reg id: " + regId);

        if (!TextUtils.isEmpty(regId))
            txtRegId.setText("Firebase Reg Id: " + regId);
        else
            txtRegId.setText("Firebase Reg Id is not received yet!");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    public void sendMessage(View view) {
        //final String uri = "http://localhost:64042/api/Person/3";
        //new HttpRequestTask().execute();
    }


    // This is an inner class
    // It will execute in the background
    // It is called using HttpRequestTask.execute() method
    // Responsible to GET a single object from provided link
    // Also requires Authentication
    private class GetCrimeDetailAsync extends AsyncTask<Void, Void, ResponseEntity<CrimeModelClass>> {
        @Override
        protected ResponseEntity<CrimeModelClass> doInBackground(Void... params) {

                //URL of the REST API GET method
                final String url = "http://192.168.56.1:45455/api/Crime/25";

                //Used for converting Jackson to Http
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                //Headers are used to add additional parameters to a link
                //such as API keys, user name and password etc.
                HttpHeaders headers = new HttpHeaders();
                //headers.set("APIKey", "1234ABCD5678EFGH");

                //Attaching header with link
                HttpEntity<String> entity = new HttpEntity<>(headers);

                //Finally executing
                // It will take a URL, a method (GET,POST,PUT,DELETE), an entity(for parameters)
                // and a class for storing received data
                // It will return response which we can then use in onPostExecute method
                ResponseEntity<CrimeModelClass> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, CrimeModelClass.class);
                return responseEntity;



            //return null;
        }

        @Override
        protected void onPostExecute(ResponseEntity<CrimeModelClass> crimeReturned) {
            TextView tvCrimeDescription =  findViewById(R.id.tvCrimeDesc);
            TextView tvCrimeType =  findViewById(R.id.tvCrimeType);
            TextView tvCrimeDateTime =  findViewById(R.id.tvCrimeDatetime);
            TextView tvCrimeAddress =  findViewById(R.id.tvCrimeLocation);

            // getBody() function is used to convert ResponseEntity into normal object
            CrimeModelClass crime = crimeReturned.getBody();

            //Display
            /*
            String data = "\nID: " + person.getID()
                    + "\nFirst Name: " + person.getFirstName()
                    + "\nLast Name: " + person.getLastName()
                    + "\nPayRate: " + person.getPayRate()
                    + "\nStart Date: " + person.getStartDate()
                    + "\nEnd Date: " + person.getEndDate();
            */

            tvCrimeDescription.setText(crime.getCrimeDescription());
            tvCrimeType.setText(crime.getCrimeType());
            tvCrimeDateTime.setText(crime.getCrimeDateTime().toString());
            tvCrimeAddress.setText(crime.getLocationAddress());

        }

    }

}
