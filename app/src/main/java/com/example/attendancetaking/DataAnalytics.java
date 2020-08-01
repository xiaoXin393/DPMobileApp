package com.example.attendancetaking;

import androidx.appcompat.app.AppCompatActivity;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DataAnalytics extends AppCompatActivity {

    final protected DBHelper dbhelper= new DBHelper(DataAnalytics.this);
    AutoCompleteTextView actvModCode;
    AutoCompleteTextView actvStudID;
    Button btnAnalyze;
    RadioGroup rgrp;
    TextInputLayout txtField;
    RadioButton rBtnChoice1;
    RadioButton rBtnChoice2;
    EditText tbxTriYear;
    DatePickerDialog dpDialog;
    long twoYears=63072000000L;
    TextView tbxShowTri;
    private int year;
    private int month;
    private int day;

    private int chosenDay;
    private int chosenMth;
    private int chosenYear;

    int choice;
    ArrayList<String> ids= new ArrayList<>();
    ArrayList<String> mods= new ArrayList<>();

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_analytics);
        setTitle("Data Analytics Customization");

        txtField=(TextInputLayout)findViewById(R.id.studentID_text_input_analysis) ;
        actvModCode=(AutoCompleteTextView)findViewById(R.id.modCode_analytics_autocomplete);
        actvStudID=(AutoCompleteTextView)findViewById(R.id.studentID_analytics_autocomplete);
        btnAnalyze=(Button)findViewById(R.id.btnAnalyseChart);

        rBtnChoice1=(RadioButton)findViewById(R.id.rBtnAll);
        rBtnChoice2=(RadioButton)findViewById(R.id.rBtnSelectOne);
        tbxTriYear=(EditText)findViewById(R.id.tbxTriYear);
        tbxShowTri=(TextView)findViewById(R.id.tbxShowTri);


        Cursor cursor = dbhelper.getAllStudentIDs();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            // do what you need with the cursor here
            String result= cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_STUDID));
            ids.add(result);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, ids);
        //Getting the instance of AutoCompleteTextView
        actvStudID.setThreshold(1);//will start working from first character
        actvStudID.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView

        Cursor cursorMod = dbhelper.getAllModuleRows();
        for (cursorMod.moveToFirst(); !cursorMod.isAfterLast(); cursorMod.moveToNext()) {
            // do what you need with the cursor here
            String result= cursorMod.getString(cursorMod.getColumnIndex(DBHelper.COLUMN_MOD_CODE))+"  "+cursorMod.getString(cursorMod.getColumnIndex(DBHelper.COLUMN_MOD_NAME));
            mods.add(result);
        }
        ArrayAdapter<String> adapterMod = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, mods);
        //Getting the instance of AutoCompleteTextView
        actvModCode.setThreshold(1);//will start working from first character
        actvModCode.setAdapter(adapterMod);//setting the adapter data into the AutoCompleteTextView

        rBtnChoice1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    txtField.setVisibility(View.GONE);
                    actvStudID.setVisibility(View.GONE);
                    rBtnChoice2.setChecked(false);
                    choice=1;
                }

            }
        });
        rBtnChoice2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    txtField.setVisibility(View.VISIBLE);
                    actvStudID.setVisibility(View.VISIBLE);
                    rBtnChoice1.setChecked(false);
                    choice=2;
                }

            }
        });
        /*
        rgrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = rgrp.findViewById(checkedId);
                int index = rgrp.indexOfChild(radioButton);
                switch (index) {
                    case 0:
                        // perform your action here
                        choice=1;
                        txtField.setVisibility(View.GONE);
                        actvStudID.setVisibility(View.GONE);
                        break;
                    case 1:
                        // perform your action here
                        choice=2;
                        txtField.setVisibility(View.VISIBLE);
                        actvStudID.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

         */

        tbxTriYear.setInputType(InputType.TYPE_NULL);
        tbxTriYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                day = cldr.get(Calendar.DAY_OF_MONTH);
                month = cldr.get(Calendar.MONTH);
                year = cldr.get(Calendar.YEAR);

                // date picker dialog
                dpDialog = new DatePickerDialog(DataAnalytics.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                tbxTriYear.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                chosenMth=monthOfYear+1;
                                chosenYear=year;
                                if(1<=(monthOfYear+1) && (4>=(monthOfYear+1))){
                                    tbxShowTri.setText("Trimester 1 (Jan-Apr) of "+year);
                                }
                                else if(5<=(monthOfYear+1) && (8>=(monthOfYear+1))){
                                    tbxShowTri.setText("Trimester 2 (May-Sep) of "+year);
                                }
                                else{
                                    tbxShowTri.setText("Trimester 3 (Oct-Dec) of "+year);
                                }

                            }
                        }, year, month, day);

                dpDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                dpDialog.getDatePicker().setMinDate(System.currentTimeMillis()-twoYears);
                dpDialog.show();
            }
        });

        btnAnalyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(choice==1) {

                        final Intent intent = new Intent(v.getContext(), GenerateAnalysis.class);
                        intent.putExtra("modCode", actvModCode.getText().toString());
                        intent.putExtra("choice", choice);
                        intent.putExtra("month",chosenMth);
                        intent.putExtra("year",chosenYear);
                        int i= actvModCode.getText().toString().indexOf(' ');
                        intent.putExtra("PUREMODCODE",actvModCode.getText().toString().substring(0,i));
                        startActivity(intent);

                }
                else{
                    if (!isEmptyChoice2()) {
                        Toast.makeText(getApplicationContext(), "Please check all fields have been completed to proceed", Toast.LENGTH_LONG).show();
                    } else {
                        final Intent intent = new Intent(v.getContext(), GenerateAnalysis.class);
                        intent.putExtra("modCode", actvModCode.getText().toString());
                        intent.putExtra("choice", choice);
                        intent.putExtra("studID",actvStudID.getText().toString());
                        intent.putExtra("month",chosenMth);
                        intent.putExtra("year",chosenYear);
                        int i= actvModCode.getText().toString().indexOf(' ');
                        intent.putExtra("PUREMODCODE",actvModCode.getText().toString().substring(0,i));
                        startActivity(intent);
                    }
                }
            }
        });
    }
    public boolean isEmptyChoice1(){
        boolean result= true;
        if(actvModCode.getText().length()<1){
            Toast.makeText(this,"Please select the module code to analyze the records",Toast.LENGTH_SHORT).show();
            result= false;
        }
        return result;
    }

    public boolean isEmptyChoice2(){
        boolean result= true;
        if(actvStudID.getText().length()<2){
            Toast.makeText(this,"Please enter or select a student ID to analyze the records",Toast.LENGTH_SHORT).show();
            result= false;
        }
        if(actvModCode.getText().length()<2){
            Toast.makeText(this,"Please select the module code to analyze the records",Toast.LENGTH_SHORT).show();
            result= false;
        }
        return result;
    }
}
