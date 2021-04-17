package com.cattle_care.cattlecare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class driverProfile extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    TextView driverName,driverEmail,driverPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNavigationView=(BottomNavigationView) findViewById(R.id.bottomBar);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem=menu.getItem(3);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.maps:
                        startActivity(new Intent(getApplicationContext(),driverMapsActivity.class));
                        finish();
                        break;
                    case R.id.request:
                        startActivity(new Intent(getApplicationContext(),driverReports.class));
                        finish();
                        break;
                    case R.id.done:
                        startActivity(new Intent(getApplicationContext(),driverCompletedReports.class));
                        finish();
                        break;
                }


                return false;
            }
        });

        fAuth=FirebaseAuth.getInstance();
        fStore= FirebaseFirestore.getInstance();
        driverName=findViewById(R.id.mName);
        driverEmail=findViewById(R.id.mEmail);
        driverPhone=findViewById(R.id.mPhone);

        DocumentReference docRef=fStore.collection("Drivers").document(fAuth.getCurrentUser().getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    driverName.setText(documentSnapshot.getString("Name"));
                    driverEmail.setText(documentSnapshot.getString("Email"));
                    driverPhone.setText(documentSnapshot.getString("PhoneNumber"));
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.logoutt){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to Logout ?")
                    .setIcon(R.drawable.logout)
                    .setCancelable(false)
                    .setTitle("LOGOUT?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(getApplicationContext(),UserType.class));
                            finish();
                        }
                    }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //Nothing
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }else if(item.getItemId()==R.id.infoMenuDriver){
            startActivity(new Intent(getApplicationContext(),sliderActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
