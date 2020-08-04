package com.example.comp2100_6442_meeting_scheduling;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.comp2100_6442_meeting_scheduling.Data.Meeting;
import com.example.comp2100_6442_meeting_scheduling.Data.User;
import com.example.comp2100_6442_meeting_scheduling.NetworkOperation.FetchByID;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private List<UUID> list;
    public MeetingAdapter(Context context, List<UUID> list) {
        this.list = list;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        UUID uuid = list.get(position);

        try {
            Meeting meeting = new FetchByID(uuid).execute().get();
            holder.tv_name.setText(meeting.getTitle());
            holder.tv_email.setText(meeting.getDescription());
//            holder.cbx.setSelected(user.isChosen());

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener!=null){
                    onItemClickListener.onclickListener(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView cbx;
        private TextView tv_email;
        private TextView tv_name;
        public ViewHolder(View view) {
            super(view);
            cbx =  itemView.findViewById(R.id.cbx);
            tv_name = itemView. findViewById(R.id.tv_name);
            tv_email =  itemView.findViewById(R.id.tv_email);
        }
    }
    interface onItemClickListener{
        void onclickListener(int position);
    }
    onItemClickListener onItemClickListener;
    public void setOnItemClickListener(onItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
}
