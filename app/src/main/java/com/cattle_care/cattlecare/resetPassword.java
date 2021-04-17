package com.cattle_care.cattlecare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AlertDialogLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class resetPassword extends AppCompatActivity {

    TextView signUp;
    Button resetPassword;
    EditText email;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        signUp=findViewById(R.id.signup);
        resetPassword=findViewById(R.id.reset);
        email=findViewById(R.id.email);
        firebaseAuth=FirebaseAuth.getInstance();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),signUpPage.class));
                finish();
            }
        });

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String mail=email.getText().toString();
                firebaseAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        AlertDialog.Builder linkSentDialog = new AlertDialog.Builder(v.getContext());
                        linkSentDialog.setTitle("Reset link sent");
                        linkSentDialog.setMessage("A link is sent to the email entered above . Follow the instructions there to reset your password and create a new password.");
                        linkSentDialog.setNeutralButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //redirect to the preceding page.
                            }
                        });
                        linkSentDialog.create().show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(resetPassword.this, "Error! Reset link not sent." + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
}
