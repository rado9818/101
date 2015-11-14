package com.radoslav.itmaps;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class newEvent extends AppCompatActivity {


    private static final String url_add_event = "http://192.168.5.119/itMaps/create_event.php";
    private static final String TAG_SUCCESS = "success";


    private String nameToSend , latitudeToSend, longitudeToSend, descriptionToSend, lecturersToSend;


    private EditText name, latitude, longitude, description, lecturers;
    private Button submit, map, reset;

    JSONParser jsonParser = new JSONParser();

    private ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        name = (EditText) findViewById(R.id.inputName);
        latitude = (EditText) findViewById(R.id.inputLatitude);
        longitude = (EditText) findViewById(R.id.inputLongitude);
        description = (EditText) findViewById(R.id.inputDescription);
        lecturers = (EditText) findViewById(R.id.inputLecturers);
        map = (Button) findViewById(R.id.map);
        reset = (Button) findViewById(R.id.reset);
        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name.setError(null);
                latitude.setError(null);
                longitude.setError(null);
                description.setError(null);
                lecturers.setError(null);

                //some validation
                if(isNetworkAvailable())
                {
                if (!name.getText().toString().equals("")) {
                    if (!latitude.getText().toString().equals("") && latitudeValid()) {
                        if (!longitude.getText().toString().equals("") && longitudeValid()) {

                            if (!description.getText().toString().equals("")) {
                                if (!lecturers.getText().toString().equals("")) {
                                    new createNewTask().execute();
                                } else {
                                    lecturers.setError(getString(R.string.not_empty));
                                }
                            } else {
                                description.setError(getString(R.string.not_empty));
                            }
                        } else {
                            longitude.setError(getString(R.string.not_empty));
                        }

                    } else {
                        latitude.setError(getString(R.string.not_empty));
                    }

                } else {
                    name.setError(getString(R.string.not_empty));
                }
            }
                else
{

}
            }

        });

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(newEvent.this, chooseFromMap.class);
                startActivityForResult(intent, 100);
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.setText("");
                latitude.setText("");
                longitude.setText("");
                description.setText("");
                lecturers.setText("");;
            }
        });
    }

private Boolean latitudeValid()
{
    Double lat = Double.valueOf(latitude.getText().toString());
    if(lat > -91 && lat < 91)
    {
        return true;
    }
    return false;
}
    private Boolean longitudeValid()
    {
        Double lat = Double.valueOf(latitude.getText().toString());
        if(lat > -181 && lat < 181)
        {
            return true;
        }
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      //when there is a response from an activity started by startActivityForResult()
        if(requestCode == 100 && resultCode == RESULT_OK)
        {
            String latitudeText = data.getStringExtra("latitude");
            latitude.setText(latitudeText);
            String longitudeText = data.getStringExtra("longitude");
            longitude.setText(longitudeText);
            String country = data.getStringExtra("country");
            name.setText(country);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    class createNewTask extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(newEvent.this);
            pDialog.setMessage(getString(R.string.creating_event));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }


        protected String doInBackground(String... args) {

                    nameToSend = name.getText().toString();
                    latitudeToSend = latitude.getText().toString();
                    longitudeToSend = longitude.getText().toString();
                    descriptionToSend = description.getText().toString();
                    lecturersToSend = lecturers.getText().toString();

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("name", nameToSend));
            params.add(new BasicNameValuePair("latitude", latitudeToSend));
            params.add(new BasicNameValuePair("longitude", longitudeToSend));
            params.add(new BasicNameValuePair("description", descriptionToSend));
            params.add(new BasicNameValuePair("lecturers", lecturersToSend));



            JSONObject json = jsonParser.makeHttpRequest(url_add_event,
                    "POST", params);


            try {
                int success = json.getInt(TAG_SUCCESS);

                System.out.println("success:   " + success);

                if (success == 1) {


                } else {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }


        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
