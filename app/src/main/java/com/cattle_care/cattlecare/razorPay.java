package com.cattle_care.cattlecare;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.valueOf;

public class razorPay extends AppCompatActivity implements PaymentResultListener {

    Button pay;
    TextView amount;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String f,txt;
    final String[] name = new String[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_razor_pay);
        Checkout.preload(getApplicationContext());
        pay = findViewById(R.id.payBtn);
        amount=findViewById(R.id.amt);
        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt = amount.getText().toString();

                if(txt.isEmpty()){
                    amount.setError("Please fill a valid amount!");
                    return;
                }

                int amt = valueOf(txt)*100;
                f=String.valueOf(amt);
                startPayment(f);
            }
        });

        DocumentReference docRef=fStore.collection("users").document(fAuth.getCurrentUser().getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    name[0] = documentSnapshot.getString("firstName")+" "+documentSnapshot.getString("lastName");
                }
            }
        });

    }

    public void startPayment(String amount) {
        //checkout.setKeyID("<YOUR_KEY_ID>");
        Checkout checkout = new Checkout();

        //checkout.setImage(R.drawable.logo);

        final Activity activity = this;

        try {
            JSONObject options = new JSONObject();

            options.put("name", "Cattle Care Fund");
            options.put("description", "Donation for welfare of animals");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            //options.put("order_id", "order_DBJOWzybf0sJbb");//from response of step 3.
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            options.put("amount", amount);//pass amount in currency subunits
            //options.put("prefill.email", "gaurav.kumar@example.com");
            //options.put("prefill.contact","9988776655");
            checkout.open(activity, options);
        } catch(Exception e) {
            Log.e("TAG", "Error in starting Razorpay Checkout", e);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onPaymentSuccess(String s) {

        startActivity(new Intent(getApplicationContext(),thankYouDonation.class));
        finish();

        DocumentReference paymentDocRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid())
                .collection("Donations").document();

        final Map<String, Object> payment = new HashMap<>();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        final String dateTime=dtf.format(now).toString();

        payment.put("amount", txt);
        payment.put("dateTime", dateTime);
        payment.put("name",name[0]);

        paymentDocRef.set(payment).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(razorPay.this, "Check details in PAYMENT HISTORY", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Nothing to show
            }
        });

        Toast.makeText(this, "Success !", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, " Mission Abort", Toast.LENGTH_SHORT).show();
    }
}
