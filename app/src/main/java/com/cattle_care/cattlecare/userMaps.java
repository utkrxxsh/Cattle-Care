package com.cattle_care.cattlecare;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class userMaps extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    Button nxt;
    CheckBox agree;

    LocationManager locationManager;
    LocationListener locationListener;
    LatLng currLoc;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        nxt = findViewById(R.id.cntd);
        agree = findViewById(R.id.agreeCheck);

        nxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double longitude;
                double latitude;
                if (agree.isChecked()) {
                    try {
                        if (!String.valueOf(currLoc.latitude).isEmpty()
                                && !String.valueOf(currLoc.longitude).isEmpty()) {
                            latitude = currLoc.latitude;
                            longitude = currLoc.longitude;

                            String lat = String.valueOf(latitude);
                            String lng = String.valueOf(longitude);

                            if (!lat.isEmpty() && !lng.isEmpty()) {
                                Intent intent = new Intent(v.getContext(), userForm.class);
                                intent.putExtra("latitude", lat);
                                intent.putExtra("longitude", lng);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Toast.makeText(userMaps.this, "Please wait until your current location is loading"
                                    , Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(userMaps.this, "Setting location...", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(userMaps.this, "Agree to the declaration", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().isCompassEnabled();

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                int height = 200;
                int width = 150;
                Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.cowmarker);
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
                mMap.clear();
                currLoc = new LatLng(location.getLatitude(), location.getLongitude());
                Marker marker = mMap.addMarker(new MarkerOptions().position(currLoc).title("You are here").icon(smallMarkerIcon));

                marker.showInfoWindow();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currLoc, 18));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if (Build.VERSION.SDK_INT < 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }else{
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                Criteria criteria = new Criteria();
                String bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();
                Location lastKnownLocation = locationManager.getLastKnownLocation(bestProvider);
//                Location lastKnownLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastKnownLocation != null) {
                    int height = 200;
                    int width = 150;
                    Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.cowmarker);
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                    BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
                    mMap.clear();
                    currLoc = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                    Marker marker = mMap.addMarker(new MarkerOptions().position(currLoc).title("You are here").icon(smallMarkerIcon));
                    marker.showInfoWindow();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currLoc, 18));

                }
            }
        }
    }
}
