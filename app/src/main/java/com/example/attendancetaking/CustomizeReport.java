package com.example.attendancetaking;

import androidx.annotation.NonNull;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class CustomizeReport extends AppCompatActivity {
    final protected DBHelper dbhelper= new DBHelper(CustomizeReport.this);
    DatePickerDialog pickerStart;
    DatePickerDialog pickerEnd;
    AutoCompleteTextView actv;
    Button btnGenerateReport;

    EditText tbxDateStart;
    long minDateMillis;
    ArrayList<String> ids= new ArrayList<>();

    EditText tbxDateEnd;
    SimpleDateFormat format= new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize_report);
        setTitle("Customize Report");

        tbxDateStart=(EditText) findViewById(R.id.tbxDateStart);
        tbxDateStart.setInputType(InputType.TYPE_NULL);
        tbxDateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                pickerStart = new DatePickerDialog(CustomizeReport.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                tbxDateStart.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                pickerStart.getDatePicker().setMaxDate(System.currentTimeMillis());
                pickerStart.show();
            }
        });

        tbxDateEnd=(EditText) findViewById(R.id.tbxDateEnd);
        tbxDateEnd.setInputType(InputType.TYPE_NULL);
        tbxDateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);

                // date picker dialog
                pickerEnd = new DatePickerDialog(CustomizeReport.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                tbxDateEnd.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                try {
                    Date minDate= format.parse(tbxDateStart.getText().toString());
                    minDateMillis= minDate.getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                pickerEnd.getDatePicker().setMaxDate(System.currentTimeMillis());
                pickerEnd.getDatePicker().setMinDate(minDateMillis);
                pickerEnd.show();
            }
        });

        Cursor cursor = dbhelper.getAllStudentIDs();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            // do what you need with the cursor here
            String result= cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_STUDID));
            ids.add(result);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, ids);
        //Getting the instance of AutoCompleteTextView
        actv = (AutoCompleteTextView) findViewById(R.id.studentID_autocomplete);
        actv.setThreshold(1);//will start working from first character
        actv.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView


        btnGenerateReport=(Button)findViewById(R.id.btnGenerateReport);
        btnGenerateReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please check all fields have been completed to proceed", Toast.LENGTH_LONG).show();
                } else {
                    final Intent intent = new Intent(v.getContext(), ReportToPdf.class);
                    intent.putExtra("id", actv.getText().toString());
                    intent.putExtra("startDate",tbxDateStart.getText().toString());
                    intent.putExtra("endDate",tbxDateEnd.getText().toString());
                    startActivity(intent);
                }
            }
        });
    }

    public boolean isEmpty(){
        boolean result= true;
        if(actv.getText().length()<3){
            Toast.makeText(this,"Please enter or select the student ID to generate the report",Toast.LENGTH_SHORT).show();
            result= false;
        }
        if(tbxDateStart.getText().length()<3){
            Toast.makeText(this,"Please select the start date range to generate the report",Toast.LENGTH_SHORT).show();
            result= false;
        }
        if(tbxDateEnd.getText().length()<3){
            Toast.makeText(this,"Please select the end date range to generate the report",Toast.LENGTH_SHORT).show();
            result= false;
        }
        return result;
    }
}
