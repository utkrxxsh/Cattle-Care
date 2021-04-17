package com.cattle_care.cattlecare;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.firestore.auth.User;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.List;

public class UserType extends AppCompatActivity {

    ImageView pic;
    Button user,driver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type);

        user=findViewById(R.id.user);
        driver=findViewById(R.id.driver);
        pic = findViewById(R.id.imageView);

        pic.animate().translationXBy(-800).setDuration(2000);

        checkPermission();

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),PhoneAuth.class));
                finish();
            }
        });
        driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),loginPage.class));
                finish();
            }
        });
    }


    public void checkPermission(){
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //Toast.makeText(UserType.this, "Granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(UserType.this, "Denied", Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(UserType.this)
                .setPermissionListener(permissionListener)
                .setPermissions(Manifest.permission.SEND_SMS,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA)
                .check();

    }


}
