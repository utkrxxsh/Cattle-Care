package com.cattle_care.cattlecare;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class ThankYouPage extends AppCompatActivity {

    TextView backToHome,backToHistory,username,userphone,location,description,numOfCattles,dateAndTime;
    ImageView imageSubmitted;
    Intent data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you_page);
        try {
            backToHome = findViewById(R.id.backToHome);
            backToHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), MainPage.class));
                    finish();
                }
            });

            backToHistory=findViewById(R.id.backToHistory);
            backToHistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(),userReportHistory.class));
                    finish();
                }
            });

            username = findViewById(R.id.username);
            userphone = findViewById(R.id.userphone);
            location = findViewById(R.id.location);
            description = findViewById(R.id.description);
            numOfCattles = findViewById(R.id.numOfCattles);
            imageSubmitted = findViewById(R.id.imageSubmitted);
            dateAndTime=findViewById(R.id.dateAndTime);
            data=getIntent();

            final String name = data.getStringExtra("Username");
            final String phno = data.getStringExtra("Phone Number");
            final String loc = data.getStringExtra("Location Details");
            final String dscrptn = data.getStringExtra("Description");
            final String noc = data.getStringExtra("Number of Cattles");
            final String imagePath = data.getStringExtra("Image URL");
            final String dateTime = data.getStringExtra("Date and Time");

            username.setText(name);
            userphone.setText(phno);
            location.setText(loc);
            description.setText(dscrptn);
            numOfCattles.setText(noc);
            dateAndTime.setText(dateTime);


            Glide.with(this)
                    .asBitmap()
                    .load(imagePath)
                    .into(imageSubmitted);
        }catch(Exception e){
            Toast.makeText(this, "Sorry ! Couldn't retrieve details", Toast.LENGTH_SHORT).show();
        }
    }
}
