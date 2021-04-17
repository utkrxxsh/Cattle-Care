package com.cattle_care.cattlecare;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    private String[] sTitles;
    private String[] sContent;
    private int[] sImages;

    Adapter(Context context, String[]titles, String[]contents,int[]sImages){
        this.layoutInflater=LayoutInflater.from(context);
        this.sTitles=titles;
        this.sContent=contents;
        this.sImages=sImages;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = layoutInflater.inflate(R.layout.custom_view,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.cardImage.setImageResource(sImages[position]);
        String title=sTitles[position];
        String content=sContent[position];
        holder.cardTitle.setText(title);
        holder.cardContent.setText(content);
    }

    @Override
    public int getItemCount() {
        return sTitles.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView cardTitle,cardContent;
        ImageView cardImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(getAdapterPosition()==0){
                        Intent i = new Intent(v.getContext(),userMaps.class);
                        v.getContext().startActivity(i);
                    }
                    if(getAdapterPosition()==1){
                        Intent i = new Intent(v.getContext(),razorPay.class);
                        v.getContext().startActivity(i);
                    }
                    if(getAdapterPosition()==2){
                        Intent i = new Intent(v.getContext(),UserProfile.class);
                        v.getContext().startActivity(i);
                    }
                    if(getAdapterPosition()==3){
                        Intent i = new Intent(v.getContext(),userReportHistory.class);
                        v.getContext().startActivity(i);
                    }
                    if(getAdapterPosition()==4){
                        Intent i = new Intent(v.getContext(),paymentHistory.class);
                        v.getContext().startActivity(i);
                    }
                }
            });
            cardTitle=itemView.findViewById(R.id.title);
            cardContent=itemView.findViewById(R.id.content);
            cardImage=itemView.findViewById(R.id.cardImage);
        }

    }

}

