package com.example.attendancetaking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class ActualModuleManagement extends AppCompatActivity {

    final protected DBHelper dbhelper= new DBHelper(ActualModuleManagement.this);
    private ArrayList<Module> modList= new ArrayList<Module>();
    private ListView listview;
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actual_module);
        setTitle("Manage Modules");



        Button fab = (Button)findViewById(R.id.addModuleBtn);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActualModuleManagement.this, ModuleDetails.class);
                startActivity(intent);
            }
        });
    }

    public void loadModuleList() {
        modList.clear();
        Cursor cursor = dbhelper.getAllModuleRows();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            // do what you need with the cursor here
            Module m = new Module();
            m.setModCode(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_MOD_CODE)));
            m.setModName(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_MOD_NAME)));
            m.setModTotalPax(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_MOD_TOTALPAX)));
            modList.add(m);
        }
        listview = findViewById(R.id.listViewModuleAll);
        listview.setAdapter(new ModuleAdapter(ActualModuleManagement.this, modList));
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int pos, long id) {
                final Intent intent = new Intent(v.getContext(), ModuleDetails.class);
                intent.putExtra("modId", modList.get(pos).getModCode());
                intent.putExtra("modName", modList.get(pos).getModName());
                intent.putExtra("modTotalPax", modList.get(pos).getModTotalPax());
                startActivity(intent);

            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        loadModuleList();
    }
}
