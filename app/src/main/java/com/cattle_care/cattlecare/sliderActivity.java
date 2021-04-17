package com.cattle_care.cattlecare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.DriverPropertyInfo;

public class sliderActivity extends AppCompatActivity {

    ViewPager viewPager;
    Button next;
    int[] layouts;
    Adaptertwo adapter;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Remove TitleBar and StatusBar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Removed :p

        setContentView(R.layout.activity_slider);

        viewPager=findViewById(R.id.viewPager);
        next=findViewById(R.id.next);
        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();

        layouts=new int[]{
                R.layout.slideone,
                R.layout.slidetwo,
                R.layout.slidethree,
                R.layout.slidefour
        };

        adapter=new Adaptertwo(this,layouts);
        viewPager.setAdapter(adapter);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewPager.getCurrentItem()+1<layouts.length){
                    viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
                }else{
                    if(fAuth.getCurrentUser()==null) {
                        startActivity(new Intent(getApplicationContext(), UserType.class));
                        finish();
                    }else{
                        DocumentReference docRefUser = fStore.collection("users")
                                .document(fAuth.getCurrentUser().getUid());
                        docRefUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot.exists()){
                                    startActivity(new Intent(getApplicationContext(),MainPage.class));
                                    finish();
                                }else{
                                    startActivity(new Intent(getApplicationContext(), driverProfile.class));
                                }
                            }
                        });

                    }
                }
            }
        });

        viewPager.addOnPageChangeListener(viewPagerChangeListener);

    }

    ViewPager.OnPageChangeListener viewPagerChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if(position==layouts.length-1){
                next.setText("GO");
            }else{
                next.setText("NEXT");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
