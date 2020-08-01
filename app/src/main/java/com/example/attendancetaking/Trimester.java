package com.example.attendancetaking;

public class Trimester {
    private String startDate;
    private String endDate;

    public Trimester(){}

    public Trimester(String startDate,String endDate){
        this.startDate=startDate;
        this.endDate=endDate;
    }

    public String getStartDate(){
        return startDate;
    }
    public String getEndDate(){
        return endDate;
    }

    public void setStartDate(String i){
        startDate=i;
    }
    public void setEndDate(String i){
        endDate=i;
    }
}
