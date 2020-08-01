package com.example.attendancetaking;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Attendance {

    private String attendanceCode;
    private String attendanceType;
    private String passcode;
    private ArrayList<String> presentStudents = new ArrayList<String>();
    private String dated;
    private String timed;



    public String getDated() {
        return dated;
    }
    public void setDated(String dated){this.dated=dated;}

    public String getTimed(){ return timed;}
    public void setTimed(String timed) {
        this.timed = timed;
    }

    public String getAttendanceCode() {
        return attendanceCode;
    }
    public void setAttendanceCode(String attendanceCode) {
        this.attendanceCode = attendanceCode;
    }

    public ArrayList<String> getPresentStudents() {
        return presentStudents;
    }
    public void setPresentStudents(ArrayList<String> presentStudents) {
        this.presentStudents = presentStudents;
    }

    public String getAttendanceType() {
        return attendanceType;
    }
    public void setAttendanceType(String attendanceType) {
        this.attendanceType = attendanceType;
    }

    public String getPasscode(){
        return passcode;
    }
    public void setPasscode(String passcode){
        this.passcode=passcode;
    }

    public Attendance(String passcode, String attendanceCode, String attendanceType, String dated, String timed,ArrayList<String> presentStudents) {
        this.attendanceCode = attendanceCode;
        this.presentStudents = presentStudents;
        this.passcode=passcode;
        this.attendanceType=attendanceType;
        this.dated = dated;
        this.timed= timed;
    }

    public Attendance(){}
}
