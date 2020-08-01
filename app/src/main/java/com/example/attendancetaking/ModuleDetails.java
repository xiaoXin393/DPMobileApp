package com.example.attendancetaking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ModuleDetails extends AppCompatActivity {

    private EditText tbxCode;
    private EditText tbxName;
    private EditText tbxPax;
    private DBHelper dbHandler= new DBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module_details);
        setTitle("Module Details");

        tbxName= (EditText)findViewById(R.id.detail_tbxMName);
        tbxCode=(EditText)findViewById(R.id.detail_tbxMCode);
        tbxPax=(EditText)findViewById(R.id.detail_tbxMTotalPax);

        Button btnAdd= (Button)findViewById(R.id.btnModAdd);
        Button btnUpdate= (Button)findViewById(R.id.btnModUpdate);
        Button btnDelete = (Button) findViewById(R.id.btnModDelete);

        Intent intent= getIntent();
        if(intent.hasExtra("modId")){
            tbxCode.setText(intent.getStringExtra("modId"));
            tbxName.setText(intent.getStringExtra("modName"));
            tbxPax.setText(intent.getStringExtra("modTotalPax"));
            btnAdd.setVisibility(View.GONE);
        }
        else{
            btnUpdate.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
        }

    }
    public void addModule(View v){
        String name= tbxName.getText().toString();
        String code= tbxCode.getText().toString();
        String p= tbxPax.getText().toString();
        int pax= Integer.parseInt(p);
        Module m= new Module(code,name,pax);
        boolean result= dbHandler.insertModRow(m);
        if(result) {
            Toast.makeText(this, "Module added successfully", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Module failed to be added", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    public void updateModule(View v){
        String name= tbxName.getText().toString();
        String code= tbxCode.getText().toString();
        String p= tbxPax.getText().toString();
        int pax= Integer.parseInt(p);

        Module m= new Module(code,name,pax);
        int rowsUpdated= dbHandler.updateModRow(m);
        Toast.makeText(this,rowsUpdated+" rows updated under module "+code,Toast.LENGTH_SHORT).show();
        finish();
    }

    public void deleteModule(View v){
        String id= tbxCode.getText().toString();
        int rowsDeleted= dbHandler.deleteModRow(id);
        Toast.makeText(this,rowsDeleted+" rows deleted under module "+id,Toast.LENGTH_SHORT).show();
        finish();
    }

}