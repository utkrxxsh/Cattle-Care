package com.cattle_care.cattlecare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class signUpPage extends AppCompatActivity {

    TextView goToLogIn;
    EditText email,password,name,phone,uniqueKey;
    ImageButton keyInfo;
    Button signUp;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        goToLogIn=findViewById(R.id.takeToLogIn);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        name=findViewById(R.id.fullName);
        phone=findViewById(R.id.phoneNumber);
        uniqueKey=findViewById(R.id.uniqueKey);
        signUp=findViewById(R.id.signUpBtn);
        keyInfo=findViewById(R.id.infoBtn);
        progressBar=findViewById(R.id.progressBar);
        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();

        goToLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),loginPage.class));
            }
        });

        keyInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(signUpPage.this);
                builder.setMessage("Unique Key is a special code provided by the government authorities to " +
                        "be able to access the Cattle Care app. ")
                        .setCancelable(false)
                        .setTitle("Unique Key")
                        .setIcon(R.drawable.info)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        if(fAuth.getCurrentUser() != null){
            Toast.makeText(this, "User already registered . Taking to main Activity", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(),driverProfile.class));
            finish();
        }

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String Name = name.getText().toString();
                final String Phone = phone.getText().toString();
                final String Email = email.getText().toString().trim();
                String Password = password.getText().toString().trim();
                String Key = uniqueKey.getText().toString();


                if(Name.isEmpty()){
                    name.setError("Name is required");
                    return;
                }
                if(Phone.isEmpty()){
                    phone.setError("Phone no. is required");
                    return;
                }
                if(TextUtils.isEmpty(Email)){
                    email.setError("Email is required");
                    return;
                }
                if(TextUtils.isEmpty(Password)){
                    password.setError("Password is required");
                    return;
                }
                if (Key.isEmpty()){
                    uniqueKey.setError("Key is required");
                    return;
                }
                if(Password.length()<6){
                    password.setError("Password must contain at least 6 characters");
                    return;
                }
                if(!Key.equals("eastwind")){
                    uniqueKey.setError("Invalid Key!");
                    Toast.makeText(signUpPage.this, "Enter a valid key", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                signUp.setVisibility(View.INVISIBLE);

                fAuth.createUserWithEmailAndPassword(Email,Password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        userId=fAuth.getCurrentUser().getUid();
                        DocumentReference docRef = fStore.collection("Drivers").document(userId);
                        final Map<String,Object> driver = new HashMap<>();
                        driver.put("Name",Name);
                        driver.put("PhoneNumber",Phone);
                        driver.put("Email",Email);
                        docRef.set(driver).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(signUpPage.this, "User Created . Taking to main activity.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),driverProfile.class));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                signUp.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(signUpPage.this, "Error occured : " +e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        signUp.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(signUpPage.this, "Error occured : " +e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
}
