package com.example.attendancetaking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainToAttendance extends AppCompatActivity {

    private String passwd;
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_to_attendance);
        setTitle("Attendance Records");

        Button btn = (Button)findViewById(R.id.btnGetAttendance);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                EditText pass= findViewById((R.id.txtPASSWORD));
                passwd= pass.getText().toString();
                if(passwd.matches("")){
                    Toast.makeText(getApplicationContext(),"Enter your passcode for activating the Raspi camera program for that session", Toast.LENGTH_LONG).show();
                }
                else {
                    final Intent intent = new Intent(v.getContext(), AttendanceMain.class);
                    intent.putExtra("pass", passwd);
                    startActivity(intent);
                }
            }
        });
    }
}
