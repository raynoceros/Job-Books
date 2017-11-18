package com.jackong.jobbooks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class CompanyPostCustomAdapter extends ArrayAdapter<JobPostList>{

    private ArrayList<JobPostList> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtSpec;
        TextView txtTitle;
        TextView txtDesc;
        TextView txtTime;
    }

    public CompanyPostCustomAdapter(ArrayList<JobPostList> data, Context context) {
        super(context, R.layout.listofjobpost, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        JobPostList dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag


        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.listofjobpost, parent, false);
            viewHolder.txtSpec = (TextView) convertView.findViewById(R.id.job_specialization);
            viewHolder.txtTitle = (TextView) convertView.findViewById(R.id.job_title);
            viewHolder.txtDesc = (TextView) convertView.findViewById(R.id.job_desc);
            viewHolder.txtTime = (TextView) convertView.findViewById(R.id.job_time);
            convertView.setTag(viewHolder);
        } else {

            viewHolder = (ViewHolder) convertView.getTag();

        }



        viewHolder.txtSpec.setText(dataModel.getJob_spec());
        viewHolder.txtTitle.setText(dataModel.getJob_title());
        viewHolder.txtDesc.setText(dataModel.getJob_desc());
        viewHolder.txtTime.setText(dataModel.getJob_time());

        // Return the completed view to render on screen
        return convertView;
    }
}
