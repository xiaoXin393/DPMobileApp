package com.example.attendancetaking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GenerateAnalysis extends AppCompatActivity {
    final protected DBHelper dbhelper= new DBHelper(GenerateAnalysis.this);
    protected DatabaseReference mDatabase;
    private AnyChartView chartView;
    protected int choice;
    protected String chosenStudID;
    private String chosenModCode;
    protected AnyChartView anychartView;
    private String passwd;
    private Attendance a;
    private String fullName;
    protected String email;
    private String aCode;
    private String mName;
    private String aTime;
    private String aDate;
    private String aType;
    private String test;
    private String pureModCode;
    private int year;
    private int month;
    protected int tri;
    protected String triStartDate;
    protected String triEndDate;
    Calendar cal;
    int check;
    ArrayList<Attendance> reportData= new ArrayList<>();
    SimpleDateFormat sdfDatepicker = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat sdfAttendance= new SimpleDateFormat("yyyy-MM-dd");
    Date start;
    Date end;
    int startDay;
    int startMth;

    HashMap<String,Integer> chartData= new HashMap<String,Integer>();

    int endDay;
    int endMth;

    private Date attendanceDate;
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, DataAnalytics.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_analysis);
        setTitle("Visualize Data");



        anychartView=(AnyChartView)findViewById(R.id.any_chart_view_analysis);
        anychartView.setProgressBar(findViewById(R.id.progress_bar_analysis));


        Intent intent= getIntent();
        choice= intent.getIntExtra("choice",0);
        month=intent.getIntExtra("month",0);
        year= intent.getIntExtra("year",0);
        pureModCode= intent.getStringExtra("PUREMODCODE");
        if(month>=1 && month<=4){
            triStartDate="01/01/"+year;
            triEndDate="30/04/"+year;
            tri=1;

            startDay=1;
            startMth=1;
            endDay=30;
            endMth=4;
        }
        else if(month>=5 && month<=8){
            triStartDate="01/05/"+year;
            triEndDate="31/08/"+year;
            tri=2;

            startDay=1;
            startMth=5;
            endDay=31;
            endMth=8;
        }
        else{
            triStartDate="01/09/"+year;
            triEndDate="31/12/"+year;
            tri=3;

            startDay=1;
            startMth=9;
            endDay=31;
            endMth=12;
        }

        if(choice==1){
            chosenModCode= intent.getStringExtra("modCode");
            choice1Analysis(chosenModCode);
        }
        else if(choice==2){
            chosenModCode= intent.getStringExtra("modCode");
            chosenStudID= intent.getStringExtra("studID");
            choice2Analysis(chosenModCode,chosenStudID);
        }
        else{
            Intent intentError = new Intent(this, DataAnalytics.class);
            Toast.makeText(this,"Error in loading data",Toast.LENGTH_SHORT).show();
            startActivity(intentError);
        }
    }

    protected void choice1Analysis(String m){
        final String modCode1= m;

        mDatabase= FirebaseDatabase.getInstance().getReference().child("attendance");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    // query for the presentStudents array results
                    //passwd= ds.getValue().toString().split("=")[0];
                    //passwd= passwd.substring(1);

                    // passcode of the session is the key
                    passwd= ds.getKey();

                    // check the count of the session, if >1 means have multiple sessions stored under the same passcode (e.g. CircuitDays)
                    long def= ds.getChildrenCount();
                    // only works if there is one session for that passcode
                    //String abc= ds.child(passwd).child("presentStudents").getValue(String.class);

                    // do a if-else for each condition- if only one session or if multiple sessions found....
                    if(def>1){

                        String abc= ds.getValue()+"";
                        for(int j=0;j<def;j++){
                            a= new Attendance();
                            String[] sects = abc.split("=\\{");
                            String sect= sects[j+1];


                            String studentsPart= sect.substring(sect.indexOf("presentStudents=") + 16);

                            // calculate the present students in that module session
                            int startIndex = studentsPart.lastIndexOf("=[");
                            int endIndex = studentsPart.indexOf("]");
                            //List<String> tags = new ArrayList<>();
                            String namePart = studentsPart.substring(startIndex + 2, endIndex);
                            String name= namePart.replace("\\", "");
                            String named= name.replace("\"", "");
                            String[] elements = named.split(",");
                            List<String> convertibleList = Arrays.asList(elements);
                            ArrayList<String> nametags = new ArrayList<String>(convertibleList);
                            ArrayList<String> uniqueNametags= new ArrayList<>();
                            for(String k:nametags){
                                k= k.trim();
                                if(!uniqueNametags.contains(k)){
                                    uniqueNametags.add(k);
                                }
                            }
                            a.setPresentStudents(uniqueNametags);


                            //Do what you want with the record

                            aCode= sect.substring(sect.indexOf("attendanceCode=") + 15);
                            aCode = aCode.substring(0, aCode.indexOf("}"));
                            aType= sect.substring(sect.indexOf("attendanceType=") + 15);
                            aType = aType.substring(0, aType.indexOf(","));
                            aDate= sect.substring(sect.indexOf("dated=") + 6);
                            aDate = aDate.substring(0, aDate.indexOf(","));
                            aTime= sect.substring(sect.indexOf("timed=") + 6);
                            aTime = aTime.substring(0, aTime.indexOf(","));
                            //String aCode = (String) ds.child(passwd).child("attendanceCode").getValue();
                            //String aType = (String) ds.child(passwd).child("attendanceType").getValue();
                            //String aDate = (String) ds.child(passwd).child("dated").getValue();
                            //String aTime = (String) ds.child(passwd).child("timed").getValue();
                            try{
                                end= sdfDatepicker.parse(triEndDate);
                                start=sdfDatepicker.parse(triStartDate);
                                Date attendanceDate= sdfAttendance.parse(aDate);

                                // if attendance date is before or equal end date range AND attendance date is after or equal start date range
                                if ((attendanceDate.compareTo(end) <= 0) && (attendanceDate.compareTo(start) >= 0)) {
                                    a.setAttendanceCode(aCode);
                                    a.setAttendanceType(aType);
                                    a.setTimed(aTime);
                                    a.setDated(aDate);
                                    reportData.add(a);
                                }
                            }catch(ParseException e){
                                e.printStackTrace();
                            }

                        }
                    }
                    else {

                        a = new Attendance();
                        String abc = ds.getValue() + "";
                        for (int j = 0; j < def; j++) {
                            String[] sects = abc.split("=\\{");
                            String sect = sects[j + 1];


                            String studentsPart = sect.substring(sect.indexOf("presentStudents=") + 16);

                            // calculate the present students in that module session
                            int startIndex = studentsPart.lastIndexOf("=[");
                            int endIndex = studentsPart.indexOf("]");
                            //List<String> tags = new ArrayList<>();
                            String namePart = studentsPart.substring(startIndex + 2, endIndex);
                            String name = namePart.replace("\\", "");
                            String named = name.replace("\"", "");
                            String[] elements = named.split(",");
                            List<String> convertibleList = Arrays.asList(elements);
                            ArrayList<String> nametags = new ArrayList<String>(convertibleList);
                            ArrayList<String> uniqueNametags = new ArrayList<>();
                            for (String k : nametags) {
                                k = k.trim();
                                if (!uniqueNametags.contains(k)) {
                                    uniqueNametags.add(k);
                                }
                            }
                            a.setPresentStudents(uniqueNametags);


                            //Do what you want with the record

                            aCode = sect.substring(sect.indexOf("attendanceCode=") + 15);
                            aCode = aCode.substring(0, aCode.indexOf("}"));
                            aType = sect.substring(sect.indexOf("attendanceType=") + 15);
                            aType = aType.substring(0, aType.indexOf(","));
                            aDate = sect.substring(sect.indexOf("dated=") + 6);
                            aDate = aDate.substring(0, aDate.indexOf(","));
                            aTime = sect.substring(sect.indexOf("timed=") + 6);
                            aTime = aTime.substring(0, aTime.indexOf(","));
                            //String aCode = (String) ds.child(passwd).child("attendanceCode").getValue();
                            //String aType = (String) ds.child(passwd).child("attendanceType").getValue();
                            //String aDate = (String) ds.child(passwd).child("dated").getValue();
                            //String aTime = (String) ds.child(passwd).child("timed").getValue();
                            try{
                                end= sdfDatepicker.parse(triEndDate);
                                start=sdfDatepicker.parse(triStartDate);
                                Date attendanceDate= sdfAttendance.parse(aDate);

                                // if attendance date is before or equal end date range AND attendance date is after or equal start date range
                                if ((attendanceDate.compareTo(end) <= 0) && (attendanceDate.compareTo(start) >= 0)) {
                                    a.setAttendanceCode(aCode);
                                    a.setAttendanceType(aType);
                                    a.setTimed(aTime);
                                    a.setDated(aDate);
                                    reportData.add(a);
                                }
                            }catch(ParseException e){
                                e.printStackTrace();
                            }
                                //a.setAttendanceCode(aCode);
                                //a.setAttendanceType(aType);
                                //a.setTimed(aTime);
                                //a.setDated(aDate);
                                //reportData.add(a);

                        }
                    }
                }

                // process the data for plotting waterfall chart, reportData already filters to attendances within the trimester date range and belonging to the student
                for(Attendance att:reportData){
                    String code= att.getAttendanceCode();

                    if(code.equals(pureModCode)) {
                        try {
                            attendanceDate = sdfAttendance.parse(att.getDated());
                            cal=Calendar.getInstance();

                            cal.set(Calendar.DAY_OF_MONTH, startDay);
                            cal.set(Calendar.MONTH, startMth - 1);
                            cal.set(Calendar.YEAR, year);
                            Calendar c2=Calendar.getInstance();
                            c2.set(Calendar.DAY_OF_MONTH, startDay+7);
                            c2.set(Calendar.MONTH, startMth - 1);
                            c2.set(Calendar.YEAR, year);
                            // if attendance date is before or equal end date range AND attendance date is after or equal start date range
                            while (c2.getTime().compareTo(end) <= 0) {

                                if ((attendanceDate.compareTo(cal.getTime()) >= 0) && (attendanceDate.compareTo(c2.getTime()) <= 0)) {
                                    //check+=1;
                                    String insert= sdfDatepicker.format(cal.getTime());
                                    Integer value = chartData.get(insert);
                                    if (value != null) {
                                        chartData.put(insert, new Integer(value + 1));
                                    } else {
                                        chartData.put(insert, 1);
                                    }
                                    cal.set(Calendar.DAY_OF_MONTH, startDay);
                                    cal.set(Calendar.MONTH, startMth - 1);
                                    cal.set(Calendar.YEAR, year);
                                    c2.set(Calendar.DAY_OF_MONTH, startDay+7);
                                    c2.set(Calendar.MONTH, startMth - 1);
                                    c2.set(Calendar.YEAR, year);
                                    break;
                                }else {
                                    String insert= sdfDatepicker.format(cal.getTime());
                                    chartData.put(insert,0);
                                    cal.add(Calendar.DATE, 7);
                                    c2.add(Calendar.DATE, 7);
                                }
                            }
                            // if attendance date is after or equal start date range
                            //else if (attendanceDate.compareTo(start) >= 0) {
                            //    check+=1;
                            //}
                            // if both out of range, then ignore

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                }
                if(chartData.size()<1){
                    AlertDialog alertDialog = new AlertDialog.Builder(GenerateAnalysis.this).create();
                    alertDialog.setTitle("Notice");
                    alertDialog.setMessage("There are no attendance records in this trimester");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Return",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent= new Intent(GenerateAnalysis.this,DataAnalytics.class);
                                    startActivity(intent);
                                }
                            });
                    alertDialog.show();
                }

                List<DataEntry> data = new ArrayList<>();
                for (Map.Entry<String, Integer> entry : chartData.entrySet())
                {
                    data.add(new ValueDataEntry(entry.getKey(),entry.getValue()));
                }

                Cartesian cartesian = AnyChart.column();
                Column column = cartesian.column(data);
                column.tooltip()
                        .titleFormat("Count")
                        .position(Position.CENTER_BOTTOM)
                        .anchor(Anchor.CENTER_BOTTOM)
                        .offsetX(0d)
                        .offsetY(5d)
                        .format("{%Value}{groupsSeparator: }");

                cartesian.animation(true);
                cartesian.title("All Students Attendance for "+modCode1+" (Trimester "+tri+")");

                cartesian.yScale().minimum(0d);

                cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }");

                cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
                cartesian.interactivity().hoverMode(HoverMode.BY_X);

                cartesian.xAxis(0).title("Dates (by week)");
                cartesian.yAxis(0).title("Attendance Captured");

                anychartView.setChart(cartesian);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    protected void choice2Analysis(String m,String n){
        final String modCode2=m;
        final String studID2=n;


        Cursor cursor = dbhelper.getStudentDetailsById(studID2);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            // do what you need with the cursor here
            fullName= cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME));
            email= cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_EMAIL));
        }

        mDatabase= FirebaseDatabase.getInstance().getReference().child("attendance");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    // query for the presentStudents array results
                    //passwd= ds.getValue().toString().split("=")[0];
                    //passwd= passwd.substring(1);

                    // passcode of the session is the key
                    passwd= ds.getKey();

                    // check the count of the session, if >1 means have multiple sessions stored under the same passcode (e.g. CircuitDays)
                    long def= ds.getChildrenCount();
                    // only works if there is one session for that passcode
                    //String abc= ds.child(passwd).child("presentStudents").getValue(String.class);

                    // do a if-else for each condition- if only one session or if multiple sessions found....
                    if(def>1){

                        String abc= ds.getValue()+"";
                        for(int j=0;j<def;j++){
                            a= new Attendance();
                            String[] sects = abc.split("=\\{");
                            String sect= sects[j+1];


                            String studentsPart= sect.substring(sect.indexOf("presentStudents=") + 16);

                            // calculate the present students in that module session
                            int startIndex = studentsPart.lastIndexOf("=[");
                            int endIndex = studentsPart.indexOf("]");
                            //List<String> tags = new ArrayList<>();
                            String namePart = studentsPart.substring(startIndex + 2, endIndex);
                            String name= namePart.replace("\\", "");
                            String named= name.replace("\"", "");
                            String[] elements = named.split(",");
                            List<String> convertibleList = Arrays.asList(elements);
                            ArrayList<String> nametags = new ArrayList<String>(convertibleList);
                            ArrayList<String> uniqueNametags= new ArrayList<>();
                            for(String k:nametags){
                                k= k.trim();
                                if(!uniqueNametags.contains(k)){
                                    uniqueNametags.add(k);
                                }
                            }
                            a.setPresentStudents(uniqueNametags);

                            if(studentsPart.contains(fullName)) {
                                //Do what you want with the record

                                aCode= sect.substring(sect.indexOf("attendanceCode=") + 15);
                                aCode = aCode.substring(0, aCode.indexOf("}"));
                                aType= sect.substring(sect.indexOf("attendanceType=") + 15);
                                aType = aType.substring(0, aType.indexOf(","));
                                aDate= sect.substring(sect.indexOf("dated=") + 6);
                                aDate = aDate.substring(0, aDate.indexOf(","));
                                aTime= sect.substring(sect.indexOf("timed=") + 6);
                                aTime = aTime.substring(0, aTime.indexOf(","));
                                //String aCode = (String) ds.child(passwd).child("attendanceCode").getValue();
                                //String aType = (String) ds.child(passwd).child("attendanceType").getValue();
                                //String aDate = (String) ds.child(passwd).child("dated").getValue();
                                //String aTime = (String) ds.child(passwd).child("timed").getValue();
                                try{
                                    end= sdfDatepicker.parse(triEndDate);
                                    start=sdfDatepicker.parse(triStartDate);
                                    Date attendanceDate= sdfAttendance.parse(aDate);

                                    // if attendance date is before or equal end date range AND attendance date is after or equal start date range
                                    if ((attendanceDate.compareTo(end) <= 0) && (attendanceDate.compareTo(start) >= 0)) {
                                        a.setAttendanceCode(aCode);
                                        a.setAttendanceType(aType);
                                        a.setTimed(aTime);
                                        a.setDated(aDate);
                                        reportData.add(a);
                                    }
                                }catch(ParseException e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    else {

                        a = new Attendance();
                        String abc = ds.getValue() + "";
                        for (int j = 0; j < def; j++) {
                            String[] sects = abc.split("=\\{");
                            String sect = sects[j + 1];


                            String studentsPart = sect.substring(sect.indexOf("presentStudents=") + 16);

                            // calculate the present students in that module session
                            int startIndex = studentsPart.lastIndexOf("=[");
                            int endIndex = studentsPart.indexOf("]");
                            //List<String> tags = new ArrayList<>();
                            String namePart = studentsPart.substring(startIndex + 2, endIndex);
                            String name = namePart.replace("\\", "");
                            String named = name.replace("\"", "");
                            String[] elements = named.split(",");
                            List<String> convertibleList = Arrays.asList(elements);
                            ArrayList<String> nametags = new ArrayList<String>(convertibleList);
                            ArrayList<String> uniqueNametags = new ArrayList<>();
                            for (String k : nametags) {
                                k = k.trim();
                                if (!uniqueNametags.contains(k)) {
                                    uniqueNametags.add(k);
                                }
                            }
                            a.setPresentStudents(uniqueNametags);

                            if (studentsPart.contains(fullName)) {
                                //Do what you want with the record

                                aCode = sect.substring(sect.indexOf("attendanceCode=") + 15);
                                aCode = aCode.substring(0, aCode.indexOf("}"));
                                aType = sect.substring(sect.indexOf("attendanceType=") + 15);
                                aType = aType.substring(0, aType.indexOf(","));
                                aDate = sect.substring(sect.indexOf("dated=") + 6);
                                aDate = aDate.substring(0, aDate.indexOf(","));
                                aTime = sect.substring(sect.indexOf("timed=") + 6);
                                aTime = aTime.substring(0, aTime.indexOf(","));
                                //String aCode = (String) ds.child(passwd).child("attendanceCode").getValue();
                                //String aType = (String) ds.child(passwd).child("attendanceType").getValue();
                                //String aDate = (String) ds.child(passwd).child("dated").getValue();
                                //String aTime = (String) ds.child(passwd).child("timed").getValue();
                                try{
                                    end= sdfDatepicker.parse(triEndDate);
                                    start=sdfDatepicker.parse(triStartDate);
                                    Date attendanceDate= sdfAttendance.parse(aDate);

                                    // if attendance date is before or equal end date range AND attendance date is after or equal start date range
                                    if ((attendanceDate.compareTo(end) <= 0) && (attendanceDate.compareTo(start) >= 0)) {
                                        a.setAttendanceCode(aCode);
                                        a.setAttendanceType(aType);
                                        a.setTimed(aTime);
                                        a.setDated(aDate);
                                        reportData.add(a);
                                    }
                                }catch(ParseException e){
                                    e.printStackTrace();
                                }
                                //a.setAttendanceCode(aCode);
                                //a.setAttendanceType(aType);
                                //a.setTimed(aTime);
                                //a.setDated(aDate);
                                //reportData.add(a);
                            }
                        }
                    }
                }




                // process the data for plotting waterfall chart, reportData already filters to attendances within the trimester date range and belonging to the student
                for(Attendance att:reportData){
                    String code= att.getAttendanceCode();
                    check=-1;
                    for (String s:att.getPresentStudents()) {
                        if (s.contains(fullName)) {
                            check=1;
                        }
                    }
                    if(code.equals(pureModCode) && check==1) {
                        try {
                            attendanceDate = sdfAttendance.parse(att.getDated());
                            cal=Calendar.getInstance();

                            cal.set(Calendar.DAY_OF_MONTH, startDay);
                            cal.set(Calendar.MONTH, startMth - 1);
                            cal.set(Calendar.YEAR, year);
                            Calendar c2=Calendar.getInstance();
                            c2.set(Calendar.DAY_OF_MONTH, startDay+7);
                            c2.set(Calendar.MONTH, startMth - 1);
                            c2.set(Calendar.YEAR, year);
                            // if attendance date is before or equal end date range AND attendance date is after or equal start date range
                            while (c2.getTime().compareTo(end) <= 0) {


                                if ((attendanceDate.compareTo(cal.getTime()) >= 0) && (attendanceDate.compareTo(c2.getTime()) <= 0)) {
                                    //check+=1;
                                    String insert= sdfDatepicker.format(cal.getTime());
                                    Integer value = chartData.get(insert);
                                    if (value != null) {
                                        chartData.put(insert, new Integer(value + 1));
                                    } else {
                                        chartData.put(insert, 1);
                                    }
                                    cal.set(Calendar.DAY_OF_MONTH, startDay);
                                    cal.set(Calendar.MONTH, startMth - 1);
                                    cal.set(Calendar.YEAR, year);
                                    c2.set(Calendar.DAY_OF_MONTH, startDay+7);
                                    c2.set(Calendar.MONTH, startMth - 1);
                                    c2.set(Calendar.YEAR, year);
                                    break;
                                }else {
                                    String insert= sdfDatepicker.format(cal.getTime());
                                    chartData.put(insert,0);
                                    cal.add(Calendar.DATE, 7);
                                    c2.add(Calendar.DATE, 7);
                                }
                            }
                            // if attendance date is after or equal start date range
                            //else if (attendanceDate.compareTo(start) >= 0) {
                            //    check+=1;
                            //}
                            // if both out of range, then ignore

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                }

                if(chartData.size()<1){
                    AlertDialog alertDialog = new AlertDialog.Builder(GenerateAnalysis.this).create();
                    alertDialog.setTitle("Notice");
                    alertDialog.setMessage("There are no attendance records in this trimester");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Return",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent= new Intent(GenerateAnalysis.this,DataAnalytics.class);
                                    startActivity(intent);
                                }
                            });
                    alertDialog.show();
                }

                List<DataEntry> data = new ArrayList<>();
                for (Map.Entry<String, Integer> entry : chartData.entrySet())
                {
                    data.add(new ValueDataEntry(entry.getKey(),entry.getValue()));
                }



                Cartesian cartesian = AnyChart.column();

                Column column = cartesian.column(data);

                column.tooltip()
                        .titleFormat("Count")
                        .position(Position.CENTER_BOTTOM)
                        .anchor(Anchor.CENTER_BOTTOM)
                        .offsetX(0d)
                        .offsetY(5d)
                        .format("{%Value}{groupsSeparator: }");

                cartesian.animation(true);
                cartesian.title("Student "+studID2+" Attendance for "+modCode2+" (Trimester "+tri+")");

                cartesian.yScale().minimum(0d);

                cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }");

                cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
                cartesian.interactivity().hoverMode(HoverMode.BY_X);

                cartesian.xAxis(0).title("Dates (by week)");
                cartesian.yAxis(0).title("Attendance Captured");

                anychartView.setChart(cartesian);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
