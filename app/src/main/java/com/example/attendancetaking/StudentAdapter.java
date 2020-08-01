package com.example.attendancetaking;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class StudentAdapter extends BaseAdapter  {

    private ArrayList<Student> studList;
    private Context context;

    public StudentAdapter(Context context, ArrayList<Student> studList ) {
        this.context=context;
        this.studList=studList;
    }

    public int getItem(){
        return studList.size();
    }

    @Override
    public int getCount() {
        return studList.size();
    }

    @Override
    public Student getItem(int position) {
        return studList.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = mInflater.inflate(R.layout.list_row_student, null);
        }

        final TextView tvName = (TextView) v.findViewById(R.id.row_nameStud);
        final TextView tvID= (TextView) v.findViewById(R.id.row_idStud);
        final TextView tvEmail = (TextView) v.findViewById(R.id.row_emailStud);

        String name = studList.get(position).getName();
        String id= studList.get(position).getStudID();
        String email = studList.get(position).getEmail();

        tvName.setText(name);
        tvID.setText(id);
        tvEmail.setText(email);

        return v;
    }

}
