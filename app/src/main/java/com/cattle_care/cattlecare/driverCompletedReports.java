package com.cattle_care.cattlecare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cattle_care.cattlecare.model.DriverCompletedReport;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class driverCompletedReports extends AppCompatActivity {

    RecyclerView driverCompletedLists;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    FirestoreRecyclerAdapter<DriverCompletedReport, DriverCompletedReportViewHolder> driverCompletedAdapter;
    TextView empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_completed_reports);
        Toolbar toolbar = findViewById(R.id.toolBarTop);
        setSupportActionBar(toolbar);

        fStore=FirebaseFirestore.getInstance();
        fAuth=FirebaseAuth.getInstance();

        Query query=fStore.collection("Drivers").document(fAuth.getCurrentUser().getUid())
                .collection("Driver reports").orderBy("location", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<DriverCompletedReport> driverCompletedReports=new FirestoreRecyclerOptions.Builder<DriverCompletedReport>()
                .setQuery(query,DriverCompletedReport.class)
                .build();

        driverCompletedAdapter = new FirestoreRecyclerAdapter<DriverCompletedReport, DriverCompletedReportViewHolder>(driverCompletedReports) {
            @Override
            protected void onBindViewHolder(@NonNull final DriverCompletedReportViewHolder driverCompletedReportViewHolder, int i, @NonNull DriverCompletedReport driverCompletedReport) {

                driverCompletedReportViewHolder.reportLocation.setText("Location : "+driverCompletedReport.getLocation());
                driverCompletedReportViewHolder.reportName.setText("Name : "+driverCompletedReport.getUsername());
                driverCompletedReportViewHolder.reportDateTime.setText("Timestamp : "+driverCompletedReport.getSubmitDateTime());

                Glide.with(getApplicationContext())
                        .asBitmap()
                        .load(driverCompletedReport.getImageURL())
                        .into(driverCompletedReportViewHolder.reportImage);

                driverCompletedReportViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(v.getContext(), "Item is clicked!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @NonNull
            @Override
            public DriverCompletedReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.driver_completed_reports_list,parent,false);
                return new DriverCompletedReportViewHolder(view);
            }
        };

        driverCompletedLists=findViewById(R.id.recyclerView);
        driverCompletedLists.setLayoutManager(new LinearLayoutManager(this));
        driverCompletedLists.setAdapter(driverCompletedAdapter);

        BottomNavigationView bottomNavigationView=(BottomNavigationView) findViewById(R.id.bottomBar);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem=menu.getItem(2);
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
                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(),driverProfile.class));
                        finish();
                        break;
                }


                return false;
            }
        });
    }

    public class DriverCompletedReportViewHolder extends RecyclerView.ViewHolder{
        TextView reportLocation,reportDateTime,reportName;
        ImageView reportImage;
        View view;
        Button callButton,deleteButton;
        public DriverCompletedReportViewHolder(@NonNull View itemView) {
            super(itemView);
            reportLocation=itemView.findViewById(R.id.location);
            reportDateTime=itemView.findViewById(R.id.dateTime);
            reportImage=itemView.findViewById(R.id.image);
            reportName=itemView.findViewById(R.id.name);
            view=itemView;
            callButton=itemView.findViewById(R.id.call);
            deleteButton=itemView.findViewById(R.id.done);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        driverCompletedAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(driverCompletedAdapter!=null){
            driverCompletedAdapter.stopListening();
        }
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
