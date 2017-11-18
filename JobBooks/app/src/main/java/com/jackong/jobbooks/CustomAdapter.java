package com.jackong.jobbooks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class CustomAdapter extends ArrayAdapter<ChatListModel>{

    private ArrayList<ChatListModel> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtLastMsg;
        TextView txtLastMsgTime;
        ImageView ProfilePic;
    }

    public CustomAdapter(ArrayList<ChatListModel> data, Context context) {
        super(context, R.layout.chat_rows, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ChatListModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag


        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.chat_rows, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.user_name);
            viewHolder.txtLastMsg = (TextView) convertView.findViewById(R.id.last_message);
            viewHolder.txtLastMsgTime = (TextView) convertView.findViewById(R.id.last_message_time);
            viewHolder.ProfilePic = (ImageView) convertView.findViewById(R.id.user_profile_pic);
            convertView.setTag(viewHolder);
        } else {

            viewHolder = (ViewHolder) convertView.getTag();

        }



        viewHolder.txtName.setText(dataModel.getUser_name());
        viewHolder.txtLastMsg.setText(dataModel.getLast_msg());
        viewHolder.txtLastMsgTime.setText(dataModel.getLast_msg_time());
        Picasso.with(mContext).load(dataModel.getImage_link()).into(viewHolder.ProfilePic);

        // Return the completed view to render on screen
        return convertView;
    }
}
