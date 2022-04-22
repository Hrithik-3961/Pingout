package com.example.pingout;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<Messages> arrayList;
    WindowManager windowManager;

    public MessagesAdapter(Context context, ArrayList<Messages> arrayList, WindowManager windowManager) {
        this.context = context;
        this.arrayList = arrayList;
        this.windowManager = windowManager;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Messages msg = arrayList.get(viewType);
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(msg.getSenderId())) {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_lyout_item, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_layout_item, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Messages msg = arrayList.get(position);
        Messages nxtMsg = new Messages();
        String date1, date2 = "";
        if (position != 0) {
            nxtMsg = arrayList.get(position - 1);
            date2 = getDate(nxtMsg.getTimestamp());
        }
        date1 = getDate(msg.getTimestamp());

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (holder.getClass() == SenderViewHolder.class) {
            SenderViewHolder viewHolder = (SenderViewHolder) holder;
            ConstraintLayout constraintLayout = viewHolder.constraintLayout;
            viewHolder.msg.setText(msg.getMessage());
            viewHolder.time.setText(getTime(msg.getTimestamp()));
            if (position != 0 && nxtMsg.getSenderId().equals(msg.getSenderId())) {
                constraintLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.sender_shape_continued));
                params.setMargins(getPix(60), getPix(10), getPix(5), 0);
                params.addRule(RelativeLayout.ALIGN_PARENT_END);
                constraintLayout.setLayoutParams(params);
                constraintLayout.setPadding(getPix(10), getPix(5), getPix(15), 0);
            }

            if (!date1.equals(date2)) {
                viewHolder.date.setText(date1);
                viewHolder.date.setVisibility(View.VISIBLE);
            }

        } else {
            ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
            ConstraintLayout constraintLayout = viewHolder.constraintLayout;
            viewHolder.msg.setText(msg.getMessage());
            viewHolder.time.setText(getTime(msg.getTimestamp()));
            if (position != 0 && nxtMsg.getSenderId().equals(msg.getSenderId())) {
                constraintLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.receiver_shape_continued));
                params.setMargins(getPix(5), getPix(10), getPix(60), 0);
                constraintLayout.setLayoutParams(params);
                constraintLayout.setPadding(getPix(15), getPix(5), getPix(10), 0);
            }

            if (!date1.equals(date2)) {
                viewHolder.date.setText(date1);
                viewHolder.date.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private int getPix(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    private String getTime(long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
        return sdf.format(date);
    }

    private String getDate(long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
        return sdf.format(date);
    }

    static class SenderViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout constraintLayout;
        TextView msg, time, date;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.sendBox);
            msg = itemView.findViewById(R.id.msg);
            time = itemView.findViewById(R.id.time);
            date = itemView.findViewById(R.id.date);
        }
    }

    static class ReceiverViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout constraintLayout;
        TextView msg, time, date;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.receiveBox);
            msg = itemView.findViewById(R.id.msg);
            time = itemView.findViewById(R.id.time);
            date = itemView.findViewById(R.id.date);
        }
    }

}
