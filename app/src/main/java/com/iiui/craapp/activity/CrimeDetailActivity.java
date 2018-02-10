package com.iiui.craapp.activity;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.iiui.craapp.R;
import com.iiui.craapp.model.CrimeDescriptionTable;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class CrimeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_detail);

        new HttpRequestTask().execute();
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
    private class HttpRequestTask extends AsyncTask<Void, Void, ResponseEntity<CrimeDescriptionTable>> {
        @Override
        protected ResponseEntity<CrimeDescriptionTable> doInBackground(Void... params) {

                //URL of the REST API GET method
                final String url = "http://192.168.56.1:45455/api/Crime/2";

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
                ResponseEntity<CrimeDescriptionTable> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, CrimeDescriptionTable.class);
                return responseEntity;



            //return null;
        }

        @Override
        protected void onPostExecute(ResponseEntity<CrimeDescriptionTable> crimeReturned) {
            TextView tvCrimeDescription =  findViewById(R.id.tvCrimeDesc);
            TextView tvCrimeType =  findViewById(R.id.tvCrimeType);
            TextView tvCrimeDateTime =  findViewById(R.id.tvCrimeDatetime);
            TextView tvCrimeAddress =  findViewById(R.id.tvCrimeLocation);

            // getBody() function is used to convert ResponseEntity into normal object
            CrimeDescriptionTable crime = crimeReturned.getBody();

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
