package com.example.cameron.revolutionuc;

import android.app.Activity;
import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.cameron.revolutionuc.model.ParkingSpot;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends ListActivity {

    private GoogleMap map;
    private ArrayList<ParkingSpot> parkingSpots;
    private ArrayList<Marker> parkingMarkers;
    Marker marker1;
    Marker marker2;
    Marker marker3;
    ParkingSpot parkingSpot1;
    ParkingSpot parkingSpot2;
    ParkingSpot parkingSpot3;
    TimerTask mTimerTask1;
    TimerTask mTimerTask2;
    TimerTask mTimerTask3;
    Timer t1;
    Timer t2;
    Timer t3;
    boolean run;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        populateParkingSpots();
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        if (map != null) {
            map.setMyLocationEnabled(true);

            marker1 = map.addMarker(new MarkerOptions()
                    .position(new LatLng(39.135445, -84.521911))
                    .title("435-439 Riddle Rd")
                    .snippet("lost connection")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

            marker2 = map.addMarker(new MarkerOptions()
                    .position(new LatLng(39.127302, -84.520388))
                    .title("2380 Wheeler St")
                    .snippet("lost connection")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

            marker3 = map.addMarker(new MarkerOptions()
                    .position(new LatLng(39.132491, -84.510142))
                    .title("2823 Glendora Ave")
                    .snippet("lost connection")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

            parkingMarkers = new ArrayList<Marker>();
            parkingMarkers.add(marker1);
            parkingMarkers.add(marker2);
            parkingMarkers.add(marker3);

            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(39.1320, -84.5155));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo((float) 14.5);

            map.moveCamera(center);
            map.animateCamera(zoom);
        }

        Collections.sort(parkingSpots, new ParkingSpotDistanceComparator());
        ParkingSpotAdapter adapter = new ParkingSpotAdapter(this, parkingSpots);
        setListAdapter(adapter);
    }

    @Override
    public void onResume() {
        Log.d("onResume called", "hooray");
        super.onResume();
        run = true;
        mTimerTask1 = new TimerTask() {
            @Override
            public void run() {
                if (run) {
                    new ParkingSpotTask().execute(1);
                } else {
                    t1.cancel();
                    t1.purge();
                }
            }
        };
        t1 = new Timer();
        t1.schedule(mTimerTask1, 0, 750);

        mTimerTask2 = new TimerTask() {
            @Override
            public void run() {
                if (run) {
                    new ParkingSpotTask().execute(2);
                } else {
                    t2.cancel();
                    t2.purge();
                }
            }
        };
        t2 = new Timer();
        t2.schedule(mTimerTask2, 250, 750);

        mTimerTask3 = new TimerTask() {
            @Override
            public void run() {
                if (run) {
                    new ParkingSpotTask().execute(3);
                } else {
                    t3.cancel();
                    t3.purge();
                }
            }
        };
        t3 = new Timer();
        t3.schedule(mTimerTask3, 500, 750);
    }

    @Override
    public void onPause() {
        Log.d("onPause called", "hooray");
        run = false;
        t1.cancel();
        t1.purge();
        t1 = null;
        mTimerTask1.cancel();
        mTimerTask1 = null;
        t2.cancel();
        t2.purge();
        t2 = null;
        mTimerTask2.cancel();
        mTimerTask2 = null;
        t3.cancel();
        t3.purge();
        t3 = null;
        mTimerTask3.cancel();
        mTimerTask3 = null;
        super.onPause();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ParkingSpot selectedParkingSpot = parkingSpots.get(position);
        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(selectedParkingSpot.getLat(),
                selectedParkingSpot.getLng()));

        for (Marker marker : parkingMarkers) {
            if (marker.getPosition().latitude == selectedParkingSpot.getLat()) {
                marker.showInfoWindow();
            } else {
                marker.hideInfoWindow();
            }
        }

        map.animateCamera(center, 1000, null);
    }

    private void populateParkingSpots() {
        parkingSpots = new ArrayList<ParkingSpot>();
        parkingSpot1 = new ParkingSpot("55ff70065075555329431787", "435-439 Riddle Rd", 39.135445, -84.521911);
        parkingSpot2 = new ParkingSpot("54ff73066667515149361367", "2380 Wheeler St", 39.127302, -84.520388);
        parkingSpot3 = new ParkingSpot("53ff70065075535113331387", "2823 Glendora Ave", 39.132491, -84.510142);
    }

    private class ParkingSpotTask extends AsyncTask<Integer, Void, Integer> {

        private String accessToken = "44c73e488bd939af04dc6886f03ecf3923d7146d";
        Marker marker;
        ParkingSpot parkingSpot;

        /**
         * A method to download json data from url
         */
        private String downloadUrl(String strUrl) throws IOException {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(strUrl);

                // Creating an http connection to communicate with url
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(2000);
                urlConnection.setReadTimeout(2000);

                // Connecting to url
                urlConnection.connect();

                // Reading data from url
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(
                        iStream));

                StringBuffer sb = new StringBuffer();

                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                data = sb.toString();

                br.close();

            } catch (Exception e) {
                Log.d("Exception while downloading url", e.toString());
            } finally {
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }

        @Override
        protected Integer doInBackground(Integer... spot) {

            // For storing data from web service
            String data = "";

            String url;
            switch(spot[0]) {
                case 1:
                    url = "https://api.spark.io/v1/devices/55ff70065075555329431787/buttonPresse?access_token=" + accessToken;
                    marker = marker1;
                    parkingSpot = parkingSpot1;
                    break;
                case 2:
                    url = "https://api.spark.io/v1/devices/54ff73066667515149361367/buttonPresse?access_token=" + accessToken;
                    marker = marker2;
                    parkingSpot = parkingSpot2;
                    break;
                default:
                    url = "https://api.spark.io/v1/devices/53ff70065075535113331387/buttonPresse?access_token=" + accessToken;
                    marker = marker3;
                    parkingSpot = parkingSpot3;
                    break;
            }
            try {
                // Fetching the data from we service
                data = downloadUrl(url);
            } catch (Exception e) {
                Log.d("checkParkingSpot()", e.toString());
            }

            int buttonPressed = -1;

            try {
                JSONObject jData = new JSONObject(data);
                buttonPressed = jData.getInt("result");
            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }

            Log.d("Button Pressed", String.valueOf(buttonPressed));
            return buttonPressed;
        }

        @Override
        protected void onPostExecute(Integer buttonPressed) {
            boolean updateList = false;
            boolean showInfo = marker.isInfoWindowShown();
            if (buttonPressed == -1 && marker.getSnippet() != "lost connection") {
                marker.setSnippet("lost connection");
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                if (parkingSpots.contains(parkingSpot)) {
                    parkingSpots.remove(parkingSpot);
                    updateList = true;
                }
            }

            if (buttonPressed == 1 && marker.getSnippet() != "taken") {
                marker.setSnippet("taken");
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                if (parkingSpots.contains(parkingSpot)) {
                    parkingSpots.remove(parkingSpot);
                    updateList = true;
                }
            }

            if (buttonPressed == 0 && marker.getSnippet() != "open") {
                marker.setSnippet("open");
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                if (!parkingSpots.contains(parkingSpot)) {
                    parkingSpots.add(parkingSpot);
                    updateList = true;
                }
            }

            if (showInfo) {
                marker.showInfoWindow();
            }

            if (updateList) {
                Collections.sort(parkingSpots, new ParkingSpotDistanceComparator());
                ParkingSpotAdapter adapter = new ParkingSpotAdapter(getBaseContext(), parkingSpots);
                setListAdapter(adapter);
            }
        }
    }

    private class ParkingSpotDistanceComparator implements Comparator<ParkingSpot> {
        public int compare(ParkingSpot left, ParkingSpot right) {
            return left.getDistance().compareTo(right.getDistance());
        }
    }
}

