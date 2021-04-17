package com.cattle_care.cattlecare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class MainPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView recyclerView;
    ViewPager viewPager,viewPager2;
    int[] layouts,layoutss;
    Adapter adapter;
    Adaptertwo adaptertwo;/*View Pager Adapter up*/
    Adaptertwo adapterthree;/*View Pager Adapter down*/
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView nav_view;
    Button helpline;
    TextView caught,registered;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        int [] arr={R.drawable.reportimage,R.drawable.donate,R.drawable.profilee,R.drawable.rephis,R.drawable.donatehis};
        String [] title={"Report a cattle","Donate to CattleCare Fund","Profile","Report History","Donation History"};
        String [] content={"Found stray Cattles in your area ? Report and Rescue the troubled animals !",
                "Help the animals by playing your role in the constructive cause",
                "Tap to view your profile",
                "View the status of the your reports",
                "Click here to view your donation initiatives"};

        recyclerView=findViewById(R.id.recyclerViewMainPage);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter(this,title,content,arr);
        recyclerView.setAdapter(adapter);
        caught=findViewById(R.id.cattleCaught);
        registered=findViewById(R.id.userReg);
        fStore=FirebaseFirestore.getInstance();

        viewPager=findViewById(R.id.viewPager);
        viewPager2=findViewById(R.id.viewPager2);
        helpline=findViewById(R.id.call);
        layouts=new int[]{
                R.layout.pageone,
                R.layout.pagetwo,
                R.layout.pagethree

        };
        layoutss=new int[]{
                R.layout.pagefour,
                R.layout.pagefive,
                R.layout.pagesix

        };
        adaptertwo=new Adaptertwo(this,layoutss);
        adapterthree=new Adaptertwo(this,layouts);
        viewPager.setAdapter(adaptertwo);
        viewPager2.setAdapter(adapterthree);


        drawerLayout=findViewById(R.id.drawer);
        nav_view=findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(this);

        toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();


        helpline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainPage.this);
                builder.setMessage("Are you sure you want call the helpline ?")
                        .setCancelable(false)
                        .setIcon(R.drawable.phone)
                        .setTitle("CALL HELPLINE ?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:8527029819"));
                                startActivity(intent);
                            }
                        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            //Nothing
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });


        fStore.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            task.getResult().size();
                            registered.setText(String.valueOf(task.getResult().size()));
                        } else {
                            Log.d("ERROR", "Error getting documents: ", task.getException());
                        }
                    }
                });

        fStore.collection("Completed Reports")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            task.getResult().size();
                            caught.setText(String.valueOf(task.getResult().size()));
                        } else {
                            Log.d("ERROR", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.info_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.userMenuInfo){
            startActivity(new Intent(getApplicationContext(),sliderActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.logout:
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
                break;

            case R.id.profile:
                startActivity(new Intent(getApplicationContext(),UserProfile.class));
                break;

            case R.id.report:
                startActivity(new Intent(getApplicationContext(),userMaps.class));
                break;

            case R.id.reportHistory:
                startActivity(new Intent(getApplicationContext(),userReportHistory.class));
                break;

            case R.id.paymentHistory:
                startActivity(new Intent(getApplicationContext(),paymentHistory.class));
                break;

            case R.id.donate:
                startActivity(new Intent(getApplicationContext(),razorPay.class));
                break;

            case R.id.bug:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"cattlecaregoa@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "Reporting bug(s) in the Cattle Care android application");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MainPage.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.news:
                startActivity(new Intent(getApplicationContext(),NewsActivity.class));
                break;

            case R.id.share:
                Intent intent =new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Share app");
                intent.putExtra(android.content.Intent.EXTRA_TEXT, "https://drive.google.com/file/d/1A-aEUwqpLFIYt2CqcvtsdXdJafuUlDfz/view");
                startActivity(Intent.createChooser(intent,"Share via"));
                break;

            case R.id.rate:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                break;

            default:
                Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
                break;

        }
        return false;
    }

}
