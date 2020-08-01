package com.example.attendancetaking;

public class Student {

    private String name;
    private String studID;
    private String email;


    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public String getStudID(){
        return studID;
    }
    public void setStudID(String studID){
        this.studID=studID;
    }
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email=email;
    }
    public Student(String name,String studID,String email){
        this.name=name;
        this.studID= studID;
        this.email=email;
    }
    public Student(){

    }
}
