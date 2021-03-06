package com.example.vinh.tripadvisorapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import DataPack.LocationItem;
import Modules.DirectionFinder;
import Modules.DirectionFinderListener;
import Modules.Route;

public class MapsActivity extends ActionBarActivity
        implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, DirectionFinderListener {

    private GoogleMap mMap;
    private Marker markerHcmus;
    private LinearLayout findPathLayoutAddress;
    private LinearLayout findPathLayoutInfo;

    EditText etOrigin;
    EditText etDestination;
    Button btnFindPath;
    TextView tvDistance;
    TextView tvDuration;

    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;

    private int SEARCH_RESULT = 0;
    private String INFORMATION_DETAIL_NAME = "1";
    private String INFORMATION_DETAIL_ADDRESS = "2";
    private String INFORMATION_DETAIL_WEBSITE = "3";
    private String INFORMATION_DETAIL_DESCRIPTION = "4";
    private String INFORMATION_DETAIL_PHONE = "5";
    private String INFORMATION_DETAIL_X = "6";
    private String INFORMATION_DETAIL_Y = "7";
    private String INFORMATION_DETAIL_INDEX = "8";


    private int numLocation = 0;
    private LocationItem aLocationItem[];
    private Marker aLocationMarker[];

    private void readInputData() {
        Scanner scan = new Scanner(
                getResources().openRawResource(R.raw.list_location));


        numLocation = Integer.parseInt(scan.nextLine());


        aLocationItem = new LocationItem[numLocation];


        for (int i = 0; i < numLocation; i++) {
            aLocationItem[i] = new LocationItem(
                    scan.nextLine(),
                    scan.nextLine(),
                    scan.nextLine(),
                    scan.nextLine(),
                    scan.nextLine(),
                    Float.parseFloat(scan.nextLine()),
                    Float.parseFloat(scan.nextLine())
                    );
        }

        scan.close();
    }



    private void createFindPathLayout() {
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        params2.setMargins(20, 5, 0, 0);

        ///
        findPathLayoutAddress = (LinearLayout) findViewById(R.id.find_patch_layout_address);
        findPathLayoutInfo = (LinearLayout) findViewById(R.id.find_patch_layout_info);

        findPathLayoutAddress.removeAllViews();
        findPathLayoutInfo.removeAllViews();

        etOrigin = new EditText(this);
        etOrigin.setHint("Origin address (limit location)");
        etOrigin.setLayoutParams(params1);
        findPathLayoutAddress.addView(etOrigin);

        etDestination = new EditText(this);
        etDestination.setHint("Destination address (limit location)");
        etDestination.setLayoutParams(params1);
        findPathLayoutAddress.addView(etDestination);

        ///


        btnFindPath = new Button(this);
        btnFindPath.setText("Find path");
        btnFindPath.setLayoutParams(params2);
        findPathLayoutInfo.addView(btnFindPath);

        ImageView ivDistance = new ImageView(this);
        ivDistance.setImageResource(R.drawable.ic_distance);
        ivDistance.setAdjustViewBounds(true);
        ivDistance.setMaxWidth(80);
        ivDistance.setMaxHeight(80);
        ivDistance.setLayoutParams(params2);
        findPathLayoutInfo.addView(ivDistance);

        tvDistance = new TextView(this);
        tvDistance.setText("0 km");
        tvDistance.setLayoutParams(params2);
        findPathLayoutInfo.addView(tvDistance);

        ImageView ivClock = new ImageView(this);
        ivClock.setImageResource(R.drawable.ic_clock);
        ivClock.setAdjustViewBounds(true);
        ivClock.setMaxWidth(80);
        ivClock.setMaxHeight(80);
        ivClock.setLayoutParams(params2);
        findPathLayoutInfo.addView(ivClock);

        tvDuration = new TextView(this);
        tvDuration.setText("0 min");
        tvDuration.setLayoutParams(params2);
        findPathLayoutInfo.addView(tvDuration);

        btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        readInputData();

        mMap = googleMap;

        // Add a marker in Sydney and move the camera

        aLocationMarker = new Marker[numLocation];
        for (int i = 0; i < numLocation; i++) {
            Marker m = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(
                                aLocationItem[i].mX, aLocationItem[i].mY))
                        );
            aLocationMarker[i] = m;
        }


        googleMap.setOnMarkerClickListener(this);

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng arg0) {
                findPathLayoutAddress.removeAllViews();
                findPathLayoutInfo.removeAllViews();
            }
        });

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                aLocationItem[0].mX,
                aLocationItem[0].mY),
                15));

        // set current position
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }


    // action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.add:
                createFindPathLayout();
                return(true);
            case R.id.reset:
                Intent intent = new Intent(this, SearchingResultActivity.class);
                //startActivity(intent);
                startActivityForResult(intent, SEARCH_RESULT);
                //finish();

                //Toast.makeText(this, "reset", Toast.LENGTH_LONG).show();
                return(true);
            case R.id.about:
                Toast.makeText(this, "about", Toast.LENGTH_LONG).show();
                return(true);
            case R.id.exit:
                finish();
                return(true);

        }
        return(super.onOptionsItemSelected(item));
    }

    // Handle marker click
    @Override
    public boolean onMarkerClick(final Marker marker) {

        for (int i = 0; i < numLocation; i++)
            if (marker.equals(aLocationMarker[i]))
            {
                Intent intent = new Intent(this, LocationDetailActivity.class);

                intent.putExtra(INFORMATION_DETAIL_NAME, aLocationItem[i].mName);
                intent.putExtra(INFORMATION_DETAIL_ADDRESS, aLocationItem[i].mAddress);
                intent.putExtra(INFORMATION_DETAIL_WEBSITE, aLocationItem[i].mWebsite);
                intent.putExtra(INFORMATION_DETAIL_DESCRIPTION, aLocationItem[i].mDescription);
                intent.putExtra(INFORMATION_DETAIL_PHONE, aLocationItem[i].mPhone);
                intent.putExtra(INFORMATION_DETAIL_X, aLocationItem[i].mX);
                intent.putExtra(INFORMATION_DETAIL_Y, aLocationItem[i].mY);
                intent.putExtra(INFORMATION_DETAIL_INDEX, i);
                startActivity(intent);
            }
        return true;
    }

    // receive data from another activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == SEARCH_RESULT) {
            String kq = data.getStringExtra("locationName").toString();
            Toast.makeText(getApplicationContext(), kq, Toast.LENGTH_SHORT).show();

            for (int i = 0; i < numLocation; i++)
                if (aLocationItem[i].mName.equals(kq))
                    mMap.moveCamera(CameraUpdateFactory
                        .newLatLngZoom(new LatLng(aLocationItem[i].mX,
                                aLocationItem[i].mY), 15));
        }
    }


    // find path
    private void sendRequest() {
        String origin = etOrigin.getText().toString();
        String destination = etDestination.getText().toString();
        if (origin.isEmpty()) {
            Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            new DirectionFinder(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            tvDuration.setText(route.duration.text);
            tvDistance.setText(route.distance.text);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }
}
