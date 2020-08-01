package com.example.attendancetaking;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

public class ModuleManagement extends AppCompatActivity {

    final protected DBHelper dbhelper= new DBHelper(this);
    private ArrayList<Student> studList= new ArrayList<Student>();
    private ListView listview;
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module_management);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Manage Students");


        Button fab = (Button)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ModuleManagement.this, StudentDetails.class);
                startActivity(intent);
            }
        });

    }

    public void loadStudentList() {
        studList.clear();
        Cursor cursor = dbhelper.getAllRows();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            // do what you need with the cursor here
            Student s = new Student();
            s.setName(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME)));
            s.setStudID(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_STUDID)));
            s.setEmail(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_EMAIL)));
            studList.add(s);
        }
        listview = findViewById(R.id.listViewStudentAll);
        listview.setAdapter(new StudentAdapter(ModuleManagement.this, studList));
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int pos, long id) {
                final Intent intent = new Intent(v.getContext(), StudentDetails.class);
                intent.putExtra("id", studList.get(pos).getStudID());
                intent.putExtra("name", studList.get(pos).getName());
                intent.putExtra("email", studList.get(pos).getEmail());
                startActivity(intent);

            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        loadStudentList();
    }
}
