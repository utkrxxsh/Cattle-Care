package com.cattle_care.cattlecare;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cattle_care.cattlecare.model.UserReport;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class userReportHistory extends AppCompatActivity {

    RecyclerView userReportLists;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    FirestoreRecyclerAdapter<UserReport,UserReportViewHolder>userReportAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_report_history);

        fStore=FirebaseFirestore.getInstance();
        fAuth=FirebaseAuth.getInstance();

        Query query=fStore.collection("users").document(fAuth.getCurrentUser().getUid())
                .collection("User Reports").orderBy("location", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<UserReport>userReports=new FirestoreRecyclerOptions.Builder<UserReport>()
                .setQuery(query,UserReport.class)
                .build();

        userReportAdapter = new FirestoreRecyclerAdapter<UserReport, UserReportViewHolder>(userReports) {
            @Override
            protected void onBindViewHolder(@NonNull UserReportViewHolder userReportViewHolder, int i, @NonNull UserReport userReport) {
                userReportViewHolder.reportLocation.setText("Location : "+userReport.getLocation());
                userReportViewHolder.reportDescription.setText("Description : "+userReport.getDescription());
                userReportViewHolder.reportNumOfCattle.setText("No. of Cattles : "+userReport.getNumber());
                userReportViewHolder.reportDateTime.setText("Timestamp : "+userReport.getSubmitDateTime());

                Glide.with(getApplicationContext())
                        .asBitmap()
                        .load(userReport.getImageURL())
                        .into(userReportViewHolder.reportImage);

                userReportViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(v.getContext(), "Item is clicked!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @NonNull
            @Override
            public UserReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_layout,parent,false);
                return new UserReportViewHolder(view);
            }
        };

        userReportLists=findViewById(R.id.recyclerView);
        userReportLists.setLayoutManager(new LinearLayoutManager(this));
        userReportLists.setAdapter(userReportAdapter);
    }

    public class UserReportViewHolder extends RecyclerView.ViewHolder{
        TextView reportDescription,reportLocation,reportNumOfCattle,reportDateTime;
        ImageView reportImage;
        View view;
        public UserReportViewHolder(@NonNull View itemView) {
            super(itemView);
            reportLocation=itemView.findViewById(R.id.location);
            reportDescription=itemView.findViewById(R.id.description);
            reportNumOfCattle=itemView.findViewById(R.id.numOfCattles);
            reportDateTime=itemView.findViewById(R.id.dateTime);
            reportImage=itemView.findViewById(R.id.image);
            view=itemView;

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        userReportAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(userReportAdapter!=null){
            userReportAdapter.stopListening();
        }
    }
}