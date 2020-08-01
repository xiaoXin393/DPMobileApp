package com.example.attendancetaking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ModuleAdapter extends BaseAdapter {

    private ArrayList<Module> modList;
    private Context context;

    public ModuleAdapter(Context context, ArrayList<Module> modList ) {
        this.context=context;
        this.modList=modList;
    }

    public int getItem(){
        return modList.size();
    }

    @Override
    public int getCount() {
        return modList.size();
    }

    @Override
    public Module getItem(int position) {
        return modList.get(position);
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
            v = mInflater.inflate(R.layout.list_row_module, null);
        }

        final TextView tvModCode = (TextView) v.findViewById(R.id.row_modCode);
        final TextView tvModName= (TextView) v.findViewById(R.id.row_modName);
        final TextView tvModTotalPax= (TextView) v.findViewById(R.id.row_mod_total_pax);

        String mCode = modList.get(position).getModCode();
        String mName= modList.get(position).getModName();
        String mTotalPax= Integer.toString(modList.get(position).getModTotalPax());

        tvModCode.setText(mCode);
        tvModName.setText(mName);
        tvModTotalPax.setText("Total students enrolled: "+mTotalPax);

        return v;
    }

}

