package com.jackong.jobbooks;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by User on 17/11/2017.
 */

public class ChatWindowCustomAdapter extends ArrayAdapter<ChatListViewItem> {

    public static final int message_type_receive = 0;
    public static final int message_type_sender = 1;

    private ArrayList<ChatListViewItem> dataSet;
    private ChatListViewItem chatlistViewItem;
    private Context mContext;

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getMessage_type();
    }

    public ChatWindowCustomAdapter(ArrayList<ChatListViewItem> data,int resource, Context context) {
        super(context, resource, data);
        this.dataSet = data;
        this.mContext = context;
    }

    public class ViewHolder {
        TextView time;
        TextView msg;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        chatlistViewItem = getItem(position);
        int listViewItemType = chatlistViewItem.getMessage_type();


        if (convertView == null) {
            viewHolder = new ViewHolder();
            if (listViewItemType == message_type_receive) {

                convertView = LayoutInflater.from(getContext()).inflate(R.layout.receiver_bubble_rows, null);
                viewHolder.time = (TextView) convertView.findViewById(R.id.receiver_time);
                viewHolder.msg = (TextView) convertView.findViewById(R.id.receiver_msg);
                convertView.setTag(viewHolder);
            } else if (listViewItemType == message_type_sender) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.sender_bubble_rows, null);
                viewHolder.time = (TextView) convertView.findViewById(R.id.sender_time);
                viewHolder.msg = (TextView) convertView.findViewById(R.id.sender_msg);
                convertView.setTag(viewHolder);
            }

        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.time.setText(chatlistViewItem.getChat_time());
        viewHolder.msg.setText(chatlistViewItem.getChat_message());

        return convertView;
    }
}