package com.cattle_care.cattlecare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class driverReportDetails extends AppCompatActivity {

    TextView name,phnum,loc,des,num,time;
    ImageView img;
    Button contact,del;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    FloatingActionButton navigate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_report_details);

        name=findViewById(R.id.Username);
        phnum=findViewById(R.id.Userphone);
        loc=findViewById(R.id.LocationUser);
        des=findViewById(R.id.UserDescription);
        num=findViewById(R.id.UserNumber);
        time=findViewById(R.id.UserdateNTime);
        img=findViewById(R.id.ReportImage);
        contact=findViewById(R.id.contact);
        del=findViewById(R.id.done);
        fStore=FirebaseFirestore.getInstance();
        fAuth=FirebaseAuth.getInstance();
        navigate=findViewById(R.id.fab);

        name.setText(getIntent().getStringExtra("Username"));
        phnum.setText(getIntent().getStringExtra("Phone Number"));
        loc.setText(getIntent().getStringExtra("Location Details"));
        des.setText(getIntent().getStringExtra("Description"));
        num.setText(getIntent().getStringExtra("Number of Cattles"));
        time.setText(getIntent().getStringExtra("Date and Time"));

        Glide.with(getApplicationContext())
                .asBitmap()
                .load(getIntent().getStringExtra("Image URL"))
                .into(img);

        navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),navigateActivity.class);
                intent.putExtra("Lat",getIntent().getStringExtra("Latitude"));
                intent.putExtra("Lng",getIntent().getStringExtra("Longitude"));
                startActivity(intent);
            }
        });

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(driverReportDetails.this);
                builder.setMessage("Do you want to call : "+getIntent().getStringExtra("Phone Number").trim()+" ?")
                        .setCancelable(false)
                        .setTitle("CALL")
                        .setIcon(R.drawable.phone)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:"+getIntent().getStringExtra("Phone Number").trim()));
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

        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                ActivityCompat.requestPermissions(driverReportDetails.this,new String[]{Manifest.permission.SEND_SMS
                        ,Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);
                    String message = "Hi! "+getIntent().getStringExtra("Username")+" your request has been successfully resolved by our driver. \n CATTLE CARE";
                    String number = getIntent().getStringExtra("Phone Number");
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(number,null,message,null,null);

                final DocumentReference driverDocRef=fStore.collection("Drivers").document(fAuth.getCurrentUser().getUid())
                        .collection("Driver reports").document();
                final DocumentReference completedDocRef=fStore.collection("Completed Reports").document();
                final Map<String, Object> report = new HashMap<>();
                report.put("username", getIntent().getStringExtra("Username"));
                report.put("userphone", getIntent().getStringExtra("Phone Number"));
                report.put("number", getIntent().getStringExtra("Number of Cattles"));
                report.put("location", getIntent().getStringExtra("Location Details"));
                report.put("latitude", getIntent().getStringExtra("Latitude"));
                report.put("longitude", getIntent().getStringExtra("Longitude"));
                report.put("description", getIntent().getStringExtra("Description"));
                report.put("submitDateTime", getIntent().getStringExtra("Date and Time"));
                report.put("imageURL", getIntent().getStringExtra("Image URL"));
                AlertDialog.Builder builder = new AlertDialog.Builder(driverReportDetails.this);
                builder.setMessage("Are you sure you want to delete the report ?")
                        .setCancelable(false)
                        .setIcon(R.drawable.delete)
                        .setTitle("DELETE")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                driverDocRef.set(report).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        DocumentReference docRef = fStore.collection("Cattle reports").document(getIntent().getStringExtra("ReportId"));
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
}


