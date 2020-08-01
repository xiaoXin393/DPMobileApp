package com.example.attendancetaking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AttendanceMain extends AppCompatActivity {

    final protected DBHelper dbhelper= new DBHelper(this);
    private ArrayList<Attendance> studList= new ArrayList<Attendance>();
    private ExpandableListView elv;
    private Attendance a= new Attendance();
    private AttendanceAdapter attAdapter;
    private DatabaseReference mDatabase;
    private String passcode;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainToAttendance.class);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendancemain);
        setTitle("Attendance Records");

        Intent intent= getIntent();
        passcode= intent.getStringExtra("pass");

        mDatabase= FirebaseDatabase.getInstance().getReference().child("attendance").child(passcode);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    a= new Attendance();
                    String valued= ds.getValue().toString();
                    // tags contain the names of the presentStudents using Regex to extract values in [] square bracket
                    //Matcher matcher = Pattern.compile("\\[([^\\]]+)").matcher(valued);
                    int startIndex = valued.lastIndexOf("=[");
                    int endIndex = valued.indexOf("]");
                    //List<String> tags = new ArrayList<>();
                    String namePart = valued.substring(startIndex + 2, endIndex);
                    //int pos = -1;
                    //while (matcher.find(pos+1)){
                    //    pos = matcher.start();
                    //    tags.add(matcher.group(1));
                    //}
                    // \\\"mom\\\", \\\"mom\\\", \\\"mom\\\", \\\"henry\\\"

                    String name= namePart.replace("\\", "");
                    String named= name.replace("\"", "");
                    String[] elements = named.split(",");
                    List<String> convertibleList = Arrays.asList(elements);
                    ArrayList<String> nametags = new ArrayList<String>(convertibleList);
                    ArrayList<String> uniqueNametags= new ArrayList<>();



                    // below line is the ds.getValue() output
                   // {attendanceType=tut, timed=20:10:01, dated=2020-03-19, presentStudents=[\\\"mom\\\", \\\"mom\\\", \\\"mom\\\", \\\"henry\\\"], attendanceCode=TLM1003}

                    // tags2 contain the individual components of the output separated by Regex at each comma
                    //int startIndex = valued.indexOf("present");
                    //int endIndex = valued.indexOf("]");
                    //String toBeReplaced = valued.substring(startIndex + 1, endIndex);
                    //String newValued= valued.replace(toBeReplaced, "");
                    //Matcher matcher2 = Pattern.compile("[\\S\\s]\\s*=\\s*([\\S\\s]+)").matcher(newValued);
                    //List<String> tags2 = new ArrayList<>();
                    //int pos2 = -1;
                    /*
                    while (matcher2.find(pos2+1)){
                        pos2 = matcher2.start();
                        tags2.add(matcher2.group(1));
                    }
                    for (String w : tags2) {
                        w.replace("\"", "");
                    } */
                    String aCode=(String)ds.child("attendanceCode").getValue();
                    String aType=(String)ds.child("attendanceType").getValue();
                    String aDate=(String)ds.child("dated").getValue();
                    String aTime=(String)ds.child("timed").getValue();
                    a.setAttendanceCode(aCode);
                    a.setAttendanceType(aType);
                    a.setTimed(aDate);
                    a.setDated(aTime);
                    /*
                    String[] names=((String[])ds.child("presentStudents").getValue());
                    String [] extractedNames= inputValidator(names);
                    ArrayList<String> formattedNames;
                    formattedNames= new ArrayList<>(Arrays.asList(extractedNames));
                    */
                    for(String k:nametags){
                        k= k.trim();
                        if(!uniqueNametags.contains(k)){
                            uniqueNametags.add(k);
                        }
                    }
                    a.setPasscode(passcode);
                    a.setPresentStudents(uniqueNametags);
                    studList.add(a);
                    initListData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // initialise the views
        initViews();
        // initialise expandable listview listeners
        initListeners();
        // initialising the objects
        initObjects();
        // prepare attendance list data
        initListData();
    }

    // initialise views
    private void initViews(){
        elv= findViewById(R.id.elvAttendance);
    }
    // initialise listeners
    private void initListeners(){
        elv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                return false;
            }
        });
        elv.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

            }
        });
    }

    private void initObjects(){
        // initializing the adapter object
        attAdapter = new AttendanceAdapter(AttendanceMain.this, studList);
        elv.setAdapter(attAdapter);
    }

    private void initListData(){
        attAdapter.notifyDataSetChanged();
    }

    private static String[] inputValidator(String[] line) {
        for(int i = 0; i < line.length; i++) {
            line[i] = line[i].replaceAll("[^a-zA-Z]", "").toLowerCase();
        }
        return line;
    }
}
