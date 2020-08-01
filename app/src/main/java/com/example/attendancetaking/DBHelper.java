package com.example.attendancetaking;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "DPappDB";

    private static final String TABLE_NAME = "students";
    private static final String TABLE_NAME_MOD="modules";

    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_STUDID = "studID";
    public static final String COLUMN_EMAIL = "email";

    public static final String COLUMN_MOD_NAME = "modName";
    public static final String COLUMN_MOD_CODE = "modCode";
    public static final String COLUMN_MOD_TOTALPAX = "modTotalPax";



    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql1 = "CREATE TABLE students " +
                "( id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "studID INTEGER, " +
                "email TEXT ) ";
        String sql2 = "CREATE TABLE modules " +
                "( id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "modCode TEXT, " +
                "modName TEXT, " +
                "modTotalPax INTEGER ) ";
        db.execSQL(sql1);
        db.execSQL(sql2);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql1 = "DROP TABLE IF EXISTS students";
        String sql2 = "DROP TABLE IF EXISTS modules";
        db.execSQL(sql1);
        db.execSQL(sql2);
        onCreate(db);
    }

    public boolean insertRow(Student student){
        ContentValues values= new ContentValues();
        values.put("name",student.getName());
        values.put("studID",student.getStudID());
        values.put("email",student.getEmail());

        SQLiteDatabase db= this.getWritableDatabase();
        boolean success=db.insert(TABLE_NAME,null,values)>0;
        db.close();
        return success;
    }

    public int updateRow(Student s){
        ContentValues values= new ContentValues();
        values.put(COLUMN_NAME,s.getName());
        values.put(COLUMN_STUDID,s.getStudID());
        values.put(COLUMN_EMAIL,s.getEmail());

        SQLiteDatabase db=this.getWritableDatabase();
        int rowsChanged= db.update(TABLE_NAME,values,COLUMN_STUDID+" =?", new String[]{s.getStudID()});
        db.close();
        return rowsChanged;
    }

    public int deleteRow(String id){
        SQLiteDatabase db= this.getWritableDatabase();
        int rowsDeleted= db.delete(TABLE_NAME,COLUMN_STUDID+" =?",new String[]{id});
        db.close();
        return rowsDeleted;
    }

    public Cursor getAllRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM students",null);
    }


    //----------------MODULE DB Table Methods--------------
    public boolean insertModRow(Module m){
        ContentValues values= new ContentValues();
        values.put(COLUMN_MOD_CODE,m.getModCode());
        values.put(COLUMN_MOD_NAME,m.getModName());
        values.put(COLUMN_MOD_TOTALPAX,m.getModTotalPax());

        SQLiteDatabase db= this.getWritableDatabase();
        boolean success=db.insert(TABLE_NAME_MOD,null,values)>0;
        db.close();
        return success;
    }

    public int updateModRow(Module m){
        ContentValues values= new ContentValues();
        values.put(COLUMN_MOD_NAME,m.getModName());
        values.put(COLUMN_MOD_CODE,m.getModCode());
        values.put(COLUMN_MOD_TOTALPAX,m.getModTotalPax());

        SQLiteDatabase db=this.getWritableDatabase();
        int rowsChanged= db.update(TABLE_NAME_MOD,values,COLUMN_MOD_CODE+" =?", new String[]{m.getModCode()});
        db.close();
        return rowsChanged;
    }

    public int deleteModRow(String id){
        SQLiteDatabase db= this.getWritableDatabase();
        int rowsDeleted= db.delete(TABLE_NAME_MOD,COLUMN_MOD_CODE+" =?",new String[]{id});
        db.close();
        return rowsDeleted;
    }

    public Cursor getAllModuleRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM modules",null);
    }

    //---------------- METHODS FOR GETTING REQUIRED INFORMATION FOR DISPLAYING ATTENDANCES----------------------
    public Cursor getModuleName(String modCode){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT modName FROM modules WHERE modCode=? COLLATE NOCASE",new String [] {modCode});

    }
    public Cursor getModuleMaxCapacity(String modCode){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM modules WHERE modCode=? COLLATE NOCASE",new String [] {modCode});

    }
    public Cursor getStudentIDByName(String named){
        SQLiteDatabase db = this.getReadableDatabase();
        String namedFinal= '%'+named+'%';

        return db.rawQuery("SELECT DISTINCT studID FROM students WHERE name LIKE ? COLLATE NOCASE",new String [] {namedFinal});
    }

    public Cursor getAllStudentIDs(){
        SQLiteDatabase db= this.getReadableDatabase();
        return db.rawQuery("SELECT studID FROM students",null);
    }

    public Cursor getStudentDetailsById(String id){
        SQLiteDatabase db= this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM students WHERE studID=?",new String[]{id});
    }

    public Cursor getModuleDetailsById(String modCode){
        SQLiteDatabase db= this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM modules WHERE modCode=? COLLATE NOCASE",new String [] {modCode});
    }



}
