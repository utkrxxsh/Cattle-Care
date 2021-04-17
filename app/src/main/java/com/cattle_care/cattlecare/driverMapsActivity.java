package com.cattle_care.cattlecare;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class driverMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fStore=FirebaseFirestore.getInstance();

        BottomNavigationView bottomNavigationView=(BottomNavigationView) findViewById(R.id.bottomBar);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem=menu.getItem(0);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.request:
                        startActivity(new Intent(getApplicationContext(),driverReports.class));
                        finish();
                        break;
                    case R.id.done:
                        startActivity(new Intent(getApplicationContext(),driverCompletedReports.class));
                        finish();
                        break;
                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(),driverProfile.class));
                        finish();
                        break;
                }


                return false;
            }
        });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        final String[] latitude = new String[1];
        final String[] longitude = new String[1];
        final String[] username = new String[1];

//        Query query=fStore.collection("Cattle reports").orderBy("location", Query.Direction.DESCENDING);

        fStore.collection("Cattle reports")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                latitude[0] =document.getString("latitude");
                                longitude[0] =document.getString("longitude");
                                username[0] =document.getString("username");

                                //Each entry gives some value
                                Log.d("TAG","Latitude=>"+latitude[0]);
                                Log.d("TAG","Longitude=>"+longitude[0]);
                                Log.d("TAG","Username=>"+username[0]);

                                int height = 200;
                                int width = 150;
                                Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.cowmarker);
                                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

                                BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
                                LatLng user = new LatLng(Double.valueOf(latitude[0]),Double.valueOf(longitude[0]));
                                mMap.addMarker(new MarkerOptions().position(user)
                                        .title(username[0]).icon(smallMarkerIcon));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user, 15));
                                }
                            }
                        else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

}
