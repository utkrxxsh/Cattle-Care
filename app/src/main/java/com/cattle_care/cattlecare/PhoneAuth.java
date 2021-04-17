package com.cattle_care.cattlecare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class PhoneAuth extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    EditText phoneNumber,codeEnter;
    Button nextBtn;
    ProgressBar progressBar;
    TextView state;
    CountryCodePicker codePicker;
    String verificationId;
    PhoneAuthProvider.ForceResendingToken token;
    Boolean verificationInProgress=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);

        fAuth = FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        phoneNumber=findViewById(R.id.phone);
        codeEnter=findViewById(R.id.codeEnter);
        progressBar=findViewById(R.id.progressBar);
        nextBtn=findViewById(R.id.nextBtn);
        state=findViewById(R.id.state);
        codePicker=findViewById(R.id.ccp);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!verificationInProgress){
                    if(!phoneNumber.getText().toString().isEmpty() && phoneNumber.getText().toString().length()==10){
                        String phoneNum = "+"+codePicker.getSelectedCountryCode() + phoneNumber.getText().toString();
                        Log.i("TAG","ph num"+phoneNum);
                        progressBar.setVisibility(View.VISIBLE);
                        state.setText("Sending OTP");
                        state.setVisibility(View.VISIBLE);
                        requestOTP(phoneNum);
                    }else{
                        phoneNumber.setError("Phone Number is not valid!");
                    }
                }else{
                    String userOTP=codeEnter.getText().toString();
                    if(!userOTP.isEmpty() && userOTP.length()==6){
                        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verificationId,userOTP);
                        verifyAuth(credential);
                    }else{
                        codeEnter.setError("Valid OTP is required");
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(fAuth.getCurrentUser()!=null){
            progressBar.setVisibility(View.VISIBLE);
            state.setText("Checking...");
            state.setVisibility(View.VISIBLE);
            checkUserProfile();
        }
    }

    private void verifyAuth(PhoneAuthCredential credential) {
        fAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    checkUserProfile();
                }else{
                    Toast.makeText(PhoneAuth.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkUserProfile() {
        DocumentReference docRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    startActivity(new Intent(getApplicationContext(),MainPage.class));
                    finish();
                }else{
                    startActivity(new Intent(getApplicationContext(),AddDetails.class));
                    finish();
                }
            }
        });
    }

    private void requestOTP(String phoneNum) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNum, 60L, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                progressBar.setVisibility(View.INVISIBLE);
                state.setVisibility(View.INVISIBLE);
                codeEnter.setVisibility(View.VISIBLE);
                verificationId=s;
                token=forceResendingToken;
                nextBtn.setText("Verify");
                verificationInProgress=true;
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                Toast.makeText(PhoneAuth.this, "OTP expired!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                verifyAuth(phoneAuthCredential);
            }
            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(PhoneAuth.this, "Cannot create account "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
