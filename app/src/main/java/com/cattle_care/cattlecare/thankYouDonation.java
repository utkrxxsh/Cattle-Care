package com.cattle_care.cattlecare;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

public class thankYouDonation extends AppCompatActivity {

    ImageView thnx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you_donation);

        thnx=findViewById(R.id.imageView3);

        thnx.animate().alpha(1).setDuration(1000);

    }
}
