package com.example.attendancetaking;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreen extends AppCompatActivity{

    final private Context context= SplashScreen.this;
    final private static int TIMEOUT= 2000;
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        handler= new Handler();
        runnable= new Runnable(){
            @Override
            public void run(){
                Intent intent= new Intent(context,MainActivity.class);
                startActivity(intent);
                finish();
            }
        };
        handler.postDelayed(runnable,TIMEOUT);
    }

    // IN CASE OF CALL BACK EVENT, DESTROY
    @Override
    protected void onDestroy(){
        if(handler!=null){
            handler.removeCallbacks(runnable);
        }
        super.onDestroy();
    }

    // IF BACK BUTTON PRESSED
    @Override
    public void onBackPressed(){
        if(handler!=null){
            handler.removeCallbacks(runnable);
        }
        super.onBackPressed();
    }
}
