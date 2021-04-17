package com.cattle_care.cattlecare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cattle_care.cattlecare.model.UserDonationReport;
import com.cattle_care.cattlecare.model.UserReport;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class paymentHistory extends AppCompatActivity {

    RecyclerView userPaymentLists;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    FirestoreRecyclerAdapter<UserDonationReport, UserDonationReportViewHolder> userDonationReportAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_history);

        fStore=FirebaseFirestore.getInstance();
        fAuth=FirebaseAuth.getInstance();

        Query query=fStore.collection("users").document(fAuth.getCurrentUser().getUid())
                .collection("Donations").orderBy("name", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<UserDonationReport>userDonationReports = new FirestoreRecyclerOptions.Builder<UserDonationReport>()
                .setQuery(query,UserDonationReport.class)
                .build();

        userDonationReportAdapter = new FirestoreRecyclerAdapter<UserDonationReport, UserDonationReportViewHolder>(userDonationReports) {

            @Override
            protected void onBindViewHolder(@NonNull UserDonationReportViewHolder userDonationReportViewHolder, int i, @NonNull UserDonationReport userDonationReport) {
                userDonationReportViewHolder.payeeName.setText("Name : "+userDonationReport.getName());
                userDonationReportViewHolder.paidAmt.setText("Donated amount : Rs "+userDonationReport.getAmount());
                userDonationReportViewHolder.paymentDateTime.setText("Date & Time : "+userDonationReport.getDateTime());
            }

            @NonNull
            @Override
            public UserDonationReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.donation_history_list,parent,false);
                return new UserDonationReportViewHolder(view);
            }

        };

        userPaymentLists=findViewById(R.id.recyclerPayment);
        userPaymentLists.setLayoutManager(new LinearLayoutManager(this));
        userPaymentLists.setAdapter(userDonationReportAdapter);
    }

    public class UserDonationReportViewHolder extends RecyclerView.ViewHolder{
        TextView payeeName,paidAmt,paymentDateTime;
        View view;
        public UserDonationReportViewHolder(@NonNull View itemView) {
            super(itemView);
            payeeName=itemView.findViewById(R.id.payUsername);
            paidAmt=itemView.findViewById(R.id.amountPaid);
            paymentDateTime=itemView.findViewById(R.id.payTimeDate);
            view=itemView;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        userDonationReportAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(userDonationReportAdapter!=null){
            userDonationReportAdapter.stopListening();
        }
    }
}
