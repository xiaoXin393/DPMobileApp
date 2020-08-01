package com.example.attendancetaking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.anychart.chart.common.listener.Event;
import com.anychart.chart.common.listener.ListenersInterface;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
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

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

public class ReportToPdf extends AppCompatActivity {
    final protected DBHelper dbhelper= new DBHelper(ReportToPdf.this);
    String rangeStartDate;
    String rangeEndDate;
    String studID;
    ArrayList<Attendance> reportData= new ArrayList<>();
    HashMap<String,Integer> chartRelevantInfo= new HashMap<String,Integer>();
    String fullName;
    String email;
    String passwd;
    String totalNum;
    String aCode;
    String mName;
    String aTime;
    String aDate;
    String aType;
    String mCode;
    Attendance a;
    int check=0;
    private DatabaseReference mDatabase;
    private AnyChartView chartView;
    SimpleDateFormat sdfDatepicker = new SimpleDateFormat("d/M/yyyy");
    SimpleDateFormat sdfAttendance= new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, CustomizeReport.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_to_pdf);
        setTitle("Generate Report");



        Intent intent= getIntent();
        rangeStartDate= intent.getStringExtra("startDate");
        rangeEndDate= intent.getStringExtra("endDate");
        studID= intent.getStringExtra("id");

        // generate report and all info on attendances based on the dateStart, dateEnd range and the Student ID selected
        // select name with the student ID
        Cursor cursor = dbhelper.getStudentDetailsById(studID);
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
                            String sects[]= abc.split("=\\{");
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
                                a.setAttendanceCode(aCode);
                                a.setAttendanceType(aType);
                                a.setTimed(aTime);
                                a.setDated(aDate);
                                reportData.add(a);
                            }
                        }
                    }
                    else {
                        a = new Attendance();
                        String abc = ds.getValue() + "";
                        for (int j = 0; j < def; j++) {
                            String sects[] = abc.split("=\\{");
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
                                a.setAttendanceCode(aCode);
                                a.setAttendanceType(aType);
                                a.setTimed(aTime);
                                a.setDated(aDate);
                                reportData.add(a);
                            }
                        }
                    }
                }
                drawChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void drawChart(){
        chartView=(AnyChartView)findViewById(R.id.any_chart_view);
        chartView.setProgressBar(findViewById(R.id.progress_bar));
        Pie pie= AnyChart.pie();
        pie.setOnClickListener(new ListenersInterface.OnClickListener(new String[]{"x", "value"}) {
            @Override
            public void onClick(Event event) {
                Toast.makeText(ReportToPdf.this, event.getData().get("x") + ":" + event.getData().get("value"), Toast.LENGTH_SHORT).show();
            }
        });


        // process the data for entering into Pie Chart
        for(Attendance a:reportData){
            try {
                Date start = sdfDatepicker.parse(rangeStartDate);
                Date end = sdfDatepicker.parse(rangeEndDate);
                Date attendanceDate= sdfAttendance.parse(a.getDated());
                // if attendance date is before or equal end date range AND attendance date is after or equal start date range
                if ((attendanceDate.compareTo(end) <= 0) && (attendanceDate.compareTo(start) >= 0)) {
                    //check+=1;
                    String mod= a.getAttendanceCode();
                    Integer value= chartRelevantInfo.get(mod);
                    if(value!=null){
                        chartRelevantInfo.put(mod,new Integer(value+1));
                    }
                    else{
                        chartRelevantInfo.put(mod,1);
                    }
                }
                // if attendance date is after or equal start date range
                //else if (attendanceDate.compareTo(start) >= 0) {
                //    check+=1;
                //}
                // if both out of range, then ignore
                else {
                    check=0;
                }/*
                if(check==2){
                    String mod= a.getAttendanceCode();
                    Integer value= chartRelevantInfo.get(mod);
                    if(value!=null){
                        chartRelevantInfo.put(mod,new Integer(value+1));
                    }
                    else{
                        chartRelevantInfo.put(mod,1);
                    }
                    check=0;
                }*/
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        // get data for pie chart (using the date range (Start, End) and student ID, get the frequency of attendance in each module over this period of days
        List<DataEntry> data= new ArrayList<>();
        for (Map.Entry<String, Integer> entry : chartRelevantInfo.entrySet())
        {
            data.add(new ValueDataEntry(entry.getKey(),entry.getValue()));
        }
        pie.data(data);

        // set the attributes for the pie chart
        pie.title("Attendance Records of "+studID+" from "+rangeStartDate+"-"+rangeEndDate);
        pie.labels().position("outside");
        pie.legend().title().enabled(true);
        pie.legend().title()
                .text("Modules")
                .padding(0d, 0d, 10d, 0d);
        pie.legend()
                .position("center-bottom")
                .itemsLayout(LegendLayout.HORIZONTAL)
                .align(Align.CENTER);
        chartView.setChart(pie);
    }

    public void generatePdf(View view){

        File filed = new File(getExternalFilesDir(null) + "/DailyReport.pdf");
        if (filed.exists()){
            boolean result= filed.delete();
        }
        // Create new document.
        Document doc = new Document();
        // Path to the course report.
        String path = getExternalFilesDir(null) + "/DailyReport.pdf";


        // Check for path and write.
        try {
            // Get instance.
            PdfWriter.getInstance(doc, new FileOutputStream(path));


            // Open the document.
            doc.open();

            // LINE SEPARATOR
            LineSeparator lineSeparator = new LineSeparator();
            lineSeparator.setLineColor(new BaseColor(0, 0, 0, 60));

            // Adding Title
            Font font = new Font(Font.FontFamily.HELVETICA, 15,Font.BOLD,BaseColor.BLUE); // Heading Font
            Font detailsFont = new Font(Font.FontFamily.HELVETICA, 11); // Details Font
            Font headingFont = new Font(Font.FontFamily.HELVETICA, 12,Font.BOLD); // Details Font
            // Creating Chunk
            Chunk titleChunk = new Chunk(studID+" "+fullName+": Attendance Report ("+rangeStartDate+"-"+rangeEndDate+")", font);
            // Creating Paragraph to add...
            Paragraph titleParagraph = new Paragraph(titleChunk);
            // Setting Alignment for Heading
            titleParagraph.setAlignment(Element.ALIGN_CENTER);
            // Finally Adding that Chunk
            doc.add(titleParagraph);

            // Add Date for the report
            Date date = Calendar.getInstance().getTime();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss a");
            String strDate = dateFormat.format(date);
            Chunk dateChunk = new Chunk("Generated on: " + strDate, headingFont);
            // Creating Paragraph to add...
            Paragraph datePara = new Paragraph(dateChunk);
            // Setting Alignment for Heading
            datePara.setAlignment(Element.ALIGN_RIGHT);
            // Add to doc.
            doc.add(datePara);

            // Add the line separator.
            doc.add(new Paragraph(""));
            doc.add(new Chunk(lineSeparator));
            doc.add(new Paragraph(""));
            doc.add(new Paragraph(""));
            doc.add(new Paragraph(""));

            // Table
            PdfPTable table = new PdfPTable(6);
            // Header
            PdfPCell cell1 = new PdfPCell(new Phrase("Date"));
            PdfPCell cell2 = new PdfPCell(new Phrase("Time"));
            PdfPCell cell3 = new PdfPCell(new Phrase("Module Type"));
            PdfPCell cell4 = new PdfPCell(new Phrase("Module Code"));
            PdfPCell cell5 = new PdfPCell(new Phrase("Module Name"));
            PdfPCell cell6 = new PdfPCell(new Phrase("Attendance (Present)"));
            table.addCell(cell1);
            table.addCell(cell2);
            table.addCell(cell3);
            table.addCell(cell4);
            table.addCell(cell5);
            table.addCell(cell6);

            // Iterate over the attendance data.
            for (Attendance att : reportData) {
                Cursor d= dbhelper.getModuleDetailsById(att.getAttendanceCode());
                if (d.moveToFirst()) {
                    totalNum = d.getString(d.getColumnIndex(DBHelper.COLUMN_MOD_TOTALPAX));
                    mName= d.getString(d.getColumnIndex(DBHelper.COLUMN_MOD_NAME));
                }
                d.close();

                PdfPCell cell1Data = new PdfPCell(new Phrase(att.getDated()));
                table.addCell(cell1Data);
                PdfPCell cell2Data= new PdfPCell(new Phrase(att.getTimed()));
                table.addCell(cell2Data);
                PdfPCell cell3Data = new PdfPCell(new Phrase(att.getAttendanceType()));
                table.addCell(cell3Data);
                PdfPCell cell4Data= new PdfPCell(new Phrase(att.getAttendanceCode()));
                table.addCell(cell4Data);
                PdfPCell cell5Data= new PdfPCell(new Phrase(mName));
                table.addCell(cell5Data);
                String totalPaxPresent= att.getPresentStudents().size()+"/"+totalNum;
                PdfPCell cell6Data= new PdfPCell(new Phrase(totalPaxPresent));
                table.addCell(cell6Data);
                /*
                // Add Heading.
                Chunk tempChunk = new Chunk("Timestamp: " + a.getDated()+"  "+a.getTimed(), headingFont);
                // Creating Paragraph to add...
                Paragraph tempPara = new Paragraph(tempChunk);
                // Add to doc.
                doc.add(tempPara);
                doc.add(new Paragraph(""));

                // Add Details.
                // Total Data.
                tempChunk = new Chunk("Total Students= " + (temp.getPresentStudents().size() + temp.getAbsentStudents().size() + temp.getLeaveStudents().size()), detailsFont);
                // Creating Paragraph to add...
                tempPara = new Paragraph(tempChunk);
                // Add to doc.
                doc.add(tempPara);

                // Present Data.
                tempChunk = new Chunk("Present Students= " + temp.getPresentStudents().size(), detailsFont);
                // Creating Paragraph to add...
                tempPara = new Paragraph(tempChunk);
                // Add to doc.
                doc.add(tempPara);

                // Absent Data.
                tempChunk = new Chunk("Absent Students= " + temp.getAbsentStudents().size(), detailsFont);
                // Creating Paragraph to add...
                tempPara = new Paragraph(tempChunk);
                // Add to doc.
                doc.add(tempPara);

                // Leave Data.
                tempChunk = new Chunk("On Leave Students= " + temp.getLeaveStudents().size(), detailsFont);
                // Creating Paragraph to add...
                tempPara = new Paragraph(tempChunk);
                */

                // Add to doc.
                doc.add(table);
                doc.add(new Paragraph(""));
                doc.add(new Paragraph(""));
                doc.add(new Paragraph(""));
                doc.add(new Paragraph(""));
                doc.add(new Paragraph(""));
            }

            // Close the document.
            doc.close();

            // Toast Message for report successfully downloaded
            Toast.makeText(ReportToPdf.this, "PDF report Saved and Sent!", Toast.LENGTH_LONG).show();

            // Send email to the student (and the administrator- this is an assumption)
            Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            emailIntent.setType("message/rfc822");
            emailIntent.putExtra(Intent.EXTRA_EMAIL  , new String[]{email});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "CAMattendance- Attendance Records Report | "+fullName);
            emailIntent.putExtra(Intent.EXTRA_TEXT   , "Attached in this email is your attendance records captured by the CAMattendance system.");

            File file = new File(getExternalFilesDir(null) + "/DailyReport.pdf");
            Uri URI = Uri.parse("file://" + file.getAbsolutePath());
            emailIntent.putExtra(Intent.EXTRA_STREAM, URI);
            try {
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(ReportToPdf.this, "There are no email clients installed", Toast.LENGTH_SHORT).show();
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
