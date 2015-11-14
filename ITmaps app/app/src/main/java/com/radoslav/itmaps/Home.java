package com.radoslav.itmaps;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Home extends FragmentActivity {

    private GoogleMap mMap;
    private JSONArray products = null;

    JSONParser jParser = new JSONParser();


    private ProgressDialog pDialog;

    private static final String url_events = "http://192.168.5.119/itMaps/get_events.php";

    private static final String TAG_SUCCESS = "success";

    private ArrayList<String> IDs = new ArrayList<>();
    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<Double> latitudes = new ArrayList<>();
    private ArrayList<Double> longitudes = new ArrayList<>();
    private ArrayList<String> descriptions = new ArrayList<>();
    private ArrayList<String> lecturers = new ArrayList<>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        setUpMapIfNeeded();

        if(isNetworkAvailable())
        {
            new LoadAllEvents().execute();
        }
        else
        {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage(R.string.no_network);
            builder1.setCancelable(false);
            builder1.setPositiveButton(R.string.OK,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            finish();
                        }
                    });


            AlertDialog alert11 = builder1.create();
            alert11.show();        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        for(int i = 0; i < IDs.size(); i++)
        {
            mMap.addMarker(new MarkerOptions().position(new LatLng(latitudes.get(i), longitudes.get(i))).title(names.get(i)));

        }



        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                int position = latitudes.indexOf(marker.getPosition().latitude);


                if (position >= 0) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(Home.this);
                    builder1.setMessage(descriptions.get(position));
                    builder1.setCancelable(false);
                    builder1.setPositiveButton(R.string.OK,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });


                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
                return false;
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent;
        switch (item.getItemId()) {
            case R.id.new_group:
                intent = new Intent(this, newEvent.class);
                startActivity(intent);
                return true;
            case R.id.reload:
                recreate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class LoadAllEvents extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Home.this);
            pDialog.setMessage(getString(R.string.loading_events));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<>();


            JSONObject json = jParser.makeHttpRequest(url_events, "GET", params);


            try {
                int success = json.getInt(TAG_SUCCESS);


                if (success == 1) {

                    products = json.getJSONArray("events");


                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);

                        String id = c.getString("id");
                        IDs.add(id);

                        String name = c.getString("name");
                        names.add(name);

                        String latitude = c.getString("latitude");
                        latitudes.add(Double.valueOf(latitude));

                        String longitude = c.getString("longitude");
                        longitudes.add(Double.valueOf(longitude));

                        String description = c.getString("description");
                        descriptions.add(description);

                        String lookingForLecturers = c.getString("lookingForLecturers");
                        lecturers.add(lookingForLecturers);


                    }





                } else {
                    //nothing found
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), getString(R.string.no_events), Toast.LENGTH_LONG).show();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }


        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {

                    setUpMap();
                }
            });

        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
