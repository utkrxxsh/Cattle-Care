package com.cattle_care.cattlecare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    ImageView splashlogo;
    TextView credits;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    LauncherManager launcherManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        splashlogo= findViewById(R.id.splashlogo);
        credits= findViewById(R.id.credits);
        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                splashlogo.animate().alpha(1).setDuration(1000);
                credits.animate().alpha(1).setDuration(1000);
            }
        },300);

        launcherManager=new LauncherManager(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(launcherManager.isFirstTime()) {
                    launcherManager.setFirstLaunch(false);
                    startActivity(new Intent(getApplicationContext(), sliderActivity.class));
                    finish();
                }else{
                    if(fAuth.getCurrentUser()!=null){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            checkUser();
                        }
                    },1000);
                }else{
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(getApplicationContext(),UserType.class));
                            finish();
                        }
                    },1000);
                }
                }
            }
        }, 2000);

    }
    private void checkUser(){
        DocumentReference docRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    startActivity(new Intent(getApplicationContext(),MainPage.class));
                    finish();
                }else{
                    checkEmailUser();
                }
            }
        });
    }
    private  void checkEmailUser(){
        DocumentReference docRef = fStore.collection("Drivers").document(fAuth.getCurrentUser().getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    startActivity(new Intent(getApplicationContext(),driverProfile.class));
                    finish();
                }else{
                    return;
                }
            }
        });
    }
}
