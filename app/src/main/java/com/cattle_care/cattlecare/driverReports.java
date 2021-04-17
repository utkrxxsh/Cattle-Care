package com.cattle_care.cattlecare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
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
import com.cattle_care.cattlecare.model.DriverReport;
import com.cattle_care.cattlecare.model.UserReport;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class driverReports extends AppCompatActivity {

    RecyclerView driverReportLists;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    FirestoreRecyclerAdapter<DriverReport,DriverReportViewHolder> driverReportAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_reports);
        Toolbar toolbar = findViewById(R.id.toolBarTop);
        setSupportActionBar(toolbar);

        fStore=FirebaseFirestore.getInstance();
        fAuth=FirebaseAuth.getInstance();

        Query query=fStore.collection("Cattle reports").orderBy("location", Query.Direction.DESCENDING);

        final FirestoreRecyclerOptions<DriverReport> driverReports=new FirestoreRecyclerOptions.Builder<DriverReport>()
                .setQuery(query,DriverReport.class)
                .build();

        driverReportAdapter = new FirestoreRecyclerAdapter<DriverReport, DriverReportViewHolder>(driverReports) {

            @Override
            protected void onBindViewHolder(@NonNull final DriverReportViewHolder driverReportViewHolder, int i, @NonNull final DriverReport driverReport) {

                final String reportId = driverReportAdapter.getSnapshots().getSnapshot(i).getId();

                driverReportViewHolder.reportLocation.setText("Location : "+driverReport.getLocation());
                driverReportViewHolder.reportName.setText("Name : "+driverReport.getUsername());
                driverReportViewHolder.reportDateTime.setText("Timestamp : "+driverReport.getSubmitDateTime());

                Glide.with(getApplicationContext())
                        .asBitmap()
                        .load(driverReport.getImageURL())
                        .into(driverReportViewHolder.reportImage);

                driverReportViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(),driverReportDetails.class);

                        intent.putExtra("Username",driverReport.getUsername());
                        intent.putExtra("Phone Number", driverReport.getUserphone());
                        intent.putExtra("Number of Cattles", driverReport.getNumber());
                        intent.putExtra("Location Details", driverReport.getLocation());
                        intent.putExtra("Description", driverReport.getDescription());
                        intent.putExtra("Image URL", driverReport.getImageURL());
                        intent.putExtra("Date and Time",driverReport.getSubmitDateTime());
                        intent.putExtra("Latitude",driverReport.getLatitude());
                        intent.putExtra("Longitude",driverReport.getLongitude());
                        intent.putExtra("ReportId",reportId);

                        startActivity(intent);
                    }
                });

                driverReportViewHolder.callButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(driverReports.this);
                        builder.setMessage("Do you want to call : "+driverReport.getUserphone().trim()+" ?")
                                .setCancelable(false)
                                .setIcon(R.drawable.phone)
                                .setTitle("CALL")
                                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent intent = new Intent(Intent.ACTION_DIAL);
                                        intent.setData(Uri.parse("tel:"+driverReport.getUserphone().trim()));
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

                driverReportViewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        ActivityCompat.requestPermissions(driverReports.this,new String[]{Manifest.permission.SEND_SMS
                                ,Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);
                        String message = "Hi! "+ driverReport.getUsername() + " your request has been successfully resolved by our driver. \n CATTLE CARE";
                        String number = driverReport.getUserphone();
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(number,null,message,null,null);

                        final DocumentReference driverDocRef=fStore.collection("Drivers").document(fAuth.getCurrentUser().getUid())
                                .collection("Driver reports").document();
                        final DocumentReference completedDocRef=fStore.collection("Completed Reports").document();
                        final Map<String, Object> report = new HashMap<>();
                        report.put("username", driverReport.getUsername());
                        report.put("userphone", driverReport.getUserphone());
                        report.put("number", driverReport.getNumber());
                        report.put("location", driverReport.getLocation());
                        report.put("latitude", driverReport.getLatitude());
                        report.put("longitude", driverReport.getLongitude());
                        report.put("description", driverReport.getDescription());
                        report.put("submitDateTime", driverReport.getSubmitDateTime());
                        report.put("imageURL", driverReport.getImageURL());
                        AlertDialog.Builder builder = new AlertDialog.Builder(driverReports.this);
                        builder.setMessage("Are you sure you want to delete the report ?")
                                .setCancelable(false)
                                .setIcon(R.drawable.delete)
                                .setTitle("DELETE")
                                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        driverDocRef.set(report).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                DocumentReference docRef = fStore.collection("Cattle reports").document(reportId);
                                                docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(v.getContext(), "Report deleted", Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(v.getContext(), "Error in deleting!", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(v.getContext(), "Error in deleting!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        completedDocRef.set(report).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //Nothing
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //Nothing
                                            }
                                        });
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
            }

            @NonNull
            @Override
            public DriverReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.driver_reports_list,parent,false);
                return new DriverReportViewHolder(view);

            }
        };

        driverReportLists=findViewById(R.id.recyclerView);
        driverReportLists.setLayoutManager(new LinearLayoutManager(this));
        driverReportLists.setAdapter(driverReportAdapter);



        BottomNavigationView bottomNavigationView=(BottomNavigationView) findViewById(R.id.bottomBar);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem=menu.getItem(1);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.maps:
                        startActivity(new Intent(getApplicationContext(),driverMapsActivity.class));
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

    public class DriverReportViewHolder extends RecyclerView.ViewHolder{
        TextView reportLocation,reportDateTime,reportName;
        ImageView reportImage;
        View view;
        Button callButton,deleteButton;
        public DriverReportViewHolder(@NonNull View itemView) {
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
        driverReportAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(driverReportAdapter!=null){
            driverReportAdapter.stopListening();
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
