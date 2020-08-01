package com.example.attendancetaking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private ExtendedFloatingActionButton eBtn;
    private ExtendedFloatingActionButton analyticBtn;
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Exiting DP App");
        builder.setMessage("Confirm to exit app? ");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Welcome");

        LinearLayout attendanceLayout=(LinearLayout)findViewById(R.id.layoutAttendance);
        attendanceLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainToAttendance.class);
                startActivity(intent);
            }
        });
        LinearLayout reportLayout=(LinearLayout)findViewById(R.id.layoutManageModules);
        reportLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ActualModuleManagement.class);
                startActivity(intent);
            }
        });
        LinearLayout manageDataLayout=(LinearLayout)findViewById(R.id.layoutManageData);
        manageDataLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ModuleManagement.class);
                startActivity(intent);
            }
        });

        eBtn= findViewById(R.id.extended_btn_email);
        eBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(eBtn.isExtended())
                {
                    Context context = getApplicationContext();
                    CharSequence text = "Proceed to send email";
                    int duration = Toast.LENGTH_SHORT;
                    Toast.makeText(context, text, duration).show();
                    Intent intent = new Intent(MainActivity.this, CustomizeReport.class);
                    startActivity(intent);
                }
                else{
                    eBtn.extend();
                }
            }
        });


        analyticBtn= findViewById(R.id.extended_btn_analytic);
        analyticBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(analyticBtn.isExtended())
                {
                    Context context = getApplicationContext();
                    CharSequence text = "Analyze Attendance";
                    int duration = Toast.LENGTH_SHORT;
                    Toast.makeText(context, text, duration).show();
                    Intent intent = new Intent(MainActivity.this, DataAnalytics.class);
                    startActivity(intent);
                }
                else{
                    eBtn.extend();
                }
            }
        });
    }
}
