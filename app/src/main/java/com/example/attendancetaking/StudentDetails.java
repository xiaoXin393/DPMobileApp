package com.example.attendancetaking;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class StudentDetails extends AppCompatActivity {

    private EditText tbxName;
    private EditText tbxID;
    private EditText tbxEmail;


    private DBHelper dbHandler= new DBHelper(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_details);

        tbxName= (EditText)findViewById(R.id.detail_tbxStudName);
        tbxID=(EditText)findViewById(R.id.detail_tbxStudID);
        tbxEmail=(EditText)findViewById(R.id.detail_tbxStudEmail);
        Button btnAdd= (Button)findViewById(R.id.btnStudAdd);
        Button btnUpdate= (Button)findViewById(R.id.btnStudUpdate);
        Button btnDelete = (Button) findViewById(R.id.btnStudDelete);

        Intent intent= getIntent();
        if(intent.hasExtra("id")){
            tbxName.setText(intent.getStringExtra("name"));
            tbxID.setText(intent.getStringExtra("id"));
            tbxEmail.setText(intent.getStringExtra("email"));
            btnAdd.setVisibility(View.GONE);
        }
        else{
            btnUpdate.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
        }
    }

    public void add(View v){
        String name= tbxName.getText().toString();
        String id= tbxID.getText().toString();
        String email= tbxEmail.getText().toString();

        Student s= new Student(name,id,email);
        boolean result= dbHandler.insertRow(s);
        if(result==true) {
            Toast.makeText(this, "Student added successfully", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Student failed to be added", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    public void update(View v){
        String name= tbxName.getText().toString();
        String id= tbxID.getText().toString();
        String email= tbxEmail.getText().toString();

        Student s= new Student(name,id,email);
        int rowsUpdated= dbHandler.updateRow(s);
        Toast.makeText(this,rowsUpdated+" rows updated under student "+id,Toast.LENGTH_SHORT).show();
        finish();
    }

    public void delete(View v){
        String id= tbxID.getText().toString();
        int rowsDeleted= dbHandler.deleteRow(id);
        Toast.makeText(this,rowsDeleted+" rows deleted under student "+id,Toast.LENGTH_SHORT).show();
        finish();
    }
}
