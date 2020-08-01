package com.example.attendancetaking;

public class Module {
    private String modCode;
    private String modName;
    private int modTotalPax;

    public String getModCode(){
        return modCode;
    }
    public String getModName(){
        return modName;
    }
    public int getModTotalPax(){return modTotalPax;}

    public void setModCode(String modCode){
        this.modCode=modCode;
    }
    public void setModName(String modName){
        this.modName=modName;
    }
    public void setModTotalPax(int modTotalPax){
        this.modTotalPax=modTotalPax;
    }

    public Module(){}

    public Module(String modCode,String modName,int modTotalPax){
        this.modCode=modCode;
        this.modName=modName;
        this.modTotalPax=modTotalPax;
    }
}
