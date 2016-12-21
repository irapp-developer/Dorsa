package ir.dorsa.dorsaworld.other;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

/**
 * Created by mehdi on 7/17/15 AD.
 */
public class Sql extends SQLiteOpenHelper {
    //------------------------------------------
    static final String dbName="Dorsa_2";
    //------------------------------------------
    static final String employeeTable="Employees";
    //------------------------------------------
    static final String colID="EmployeeID";
    static final String colName="EmployeeName";
    static final String colAge="Age";
    //------------------------------------------
    static final String deptTable="Dept";
    static final String colDept="Dept";
    static final String colDeptID="DeptID";
    static final String colDeptName="DeptName";
    //------------------------------------------
    static final String viewEmps="ViewEmps";
    //------------------------------------------
    public Sql(Context context) {
        super(context, dbName, null,1);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // TODO Auto-generated method stub

        db.execSQL("CREATE TABLE tbl_settings (name TEXT,dim TEXT)");
        db.execSQL("CREATE TABLE tbl_kids (ID INTEGER PRIMARY KEY   AUTOINCREMENT,isSelected TEXT,name TEXT,birthday TEXT,sex TEXT,settings TEXT)");
        db.execSQL("CREATE TABLE tbl_kid_apps (ID INTEGER PRIMARY KEY   AUTOINCREMENT,kidId TEXT,packageName TEXT,AccessInternet TEXT,className TEXT)");
        db.execSQL("CREATE TABLE tbl_schedual (ID INTEGER PRIMARY KEY   AUTOINCREMENT,kidId TEXT,packageName TEXT,className,dayIndex TEXT,timeIndex TEXT)");
        db.execSQL("CREATE TABLE tbl_daily_limit (ID INTEGER PRIMARY KEY   AUTOINCREMENT,kidId TEXT,packageName TEXT,className,limitPerDay TEXT,enable TEXT,lastDayUsed TEXT,usedSecound TEXT)");

        ContentValues cv=new ContentValues();
        cv.put("name", "password");
        cv.put("dim", "-1");
        db.insert("tbl_settings", null, cv);
        cv.clear();

        cv.put("name", "passmode");
        cv.put("dim", "-1");
        db.insert("tbl_settings", null, cv);
        cv.clear();


        cv.put("name", "phonenumber");
        cv.put("dim", "-1");
        db.insert("tbl_settings", null, cv);
        cv.clear();

        cv.put("name", "emergency_number");
        cv.put("dim", "-1");
        db.insert("tbl_settings", null, cv);
        cv.clear();
        

        cv.put("name", "register_imsi");
        cv.put("dim", "-1");
        db.insert("tbl_settings", null, cv);
        cv.clear();

        cv.put("name", "hint_question");
        cv.put("dim", "-1");
        db.insert("tbl_settings", null, cv);
        cv.clear();

        cv.put("name", "hint_answer");
        cv.put("dim", "-1");
        db.insert("tbl_settings", null, cv);
        cv.clear();

        cv.put("name", "intro_schedual");
        cv.put("dim", "false");
        db.insert("tbl_settings", null, cv);
        cv.clear();


        cv.put("name", "lastWrongPass");
        cv.put("dim", "-1");
        db.insert("tbl_settings", null, cv);
        cv.clear();

        cv.put("name", "present");
        cv.put("dim", "false");
        db.insert("tbl_settings", null, cv);
        cv.clear();

        cv.put("name", "parent_hint");
        cv.put("dim", "false");
        db.insert("tbl_settings", null, cv);
        cv.clear();
        
        
        Func.deleteNon_EmptyDir(new File(G.dir));

      /*  ContentValues cv=new ContentValues();
        cv.put("name", "userId");cv.put("dim", "-1");
        db.insert("Settings", null, cv);
        cv.clear();*/



    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


    }


}
