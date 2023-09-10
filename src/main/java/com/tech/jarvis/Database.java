package com.tech.jarvis;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Parameters.db";
    public static final String TABLE_NAME = "Parameters";
    public static final String COL1 = "ATTRIBUTES";
    public static final String COL2 = "VALUE";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (ATTRIBUTES TEXT, VALUE TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    public boolean defaultValue() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("Select * from " + TABLE_NAME, null);
        while(res.moveToNext()) {
            String attri = res.getString(0);
            if(attri.equals("joke")) {
                return false;
            }
        }
        SQLiteDatabase db1 = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, "joke");
        contentValues.put(COL2, "Job Interviwer, And where would you see yourself in five years time Mr. Jeffries. Mr. Jeffries, Personally I believe my biggest weakness is in listening.");
        long result = db1.insert(TABLE_NAME, null, contentValues);
        contentValues.put(COL1, "joke");
        contentValues.put(COL2, "A mother asked her son anton, do you think i'm bad mom? son my name is paul.");
        result = db1.insert(TABLE_NAME, null, contentValues);
        contentValues.put(COL1, "joke");
        contentValues.put(COL2, "What is dengerous? Sneezing when you're having diarrhea!");
        result = db1.insert(TABLE_NAME, null, contentValues);
        contentValues.put(COL1, "joke");
        contentValues.put(COL2, "I dreamed I was forced to eat a giant marshamllow. When I woke up, my pillow was gone.");
        result = db1.insert(TABLE_NAME, null, contentValues);
        contentValues.put(COL1, "joke");
        contentValues.put(COL2, "A mother asked her son anton, do you think i'm bad mom? son my name is paul.");
        result = db1.insert(TABLE_NAME, null, contentValues);
        contentValues.put(COL1, "light");
        contentValues.put(COL2, "light");
        result = db1.insert(TABLE_NAME, null, contentValues);
        contentValues.put(COL1, "tv");
        contentValues.put(COL2, "tv");
        result = db1.insert(TABLE_NAME, null, contentValues);
        contentValues.put(COL1, "fan");
        contentValues.put(COL2, "fan");
        result = db1.insert(TABLE_NAME, null, contentValues);
        contentValues.put(COL1, "all");
        contentValues.put(COL2, "all");
        result = db1.insert(TABLE_NAME, null, contentValues);
        contentValues.put(COL1, "simnumber");
        contentValues.put(COL2, "+919696969696");
        result = db1.insert(TABLE_NAME, null, contentValues);
        contentValues.put(COL1, "first");
        contentValues.put(COL2, "first");
        result = db1.insert(TABLE_NAME, null, contentValues);
        contentValues.put(COL1, "last");
        contentValues.put(COL2, "last");
        result = db1.insert(TABLE_NAME, null, contentValues);
        contentValues.put(COL1, "email");
        contentValues.put(COL2, "email");
        result = db1.insert(TABLE_NAME, null, contentValues);
        contentValues.put(COL1, "contact");
        contentValues.put(COL2, "contact");
        result = db1.insert(TABLE_NAME, null, contentValues);
        contentValues.put(COL1, "firstpage");
        contentValues.put(COL2, "false");
        result = db1.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getAllData(String query) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery(query, null);
        return res;
    }

    public boolean updateSimNumber(String number) {
        SQLiteDatabase db1 = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, number);
        long result = db1.update(TABLE_NAME, contentValues, "ATTRIBUTES= ?", new String[]{"simnumber"});
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean updateControlSettings(String name1, String name2, String name3, String name4) {
        SQLiteDatabase db1 = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, name1);
        long result = db1.update(TABLE_NAME, contentValues, "ATTRIBUTES= ?", new String[]{"light1"});
        contentValues.put(COL2, name2);
        result = db1.update(TABLE_NAME, contentValues, "ATTRIBUTES= ?", new String[]{"light2"});
        contentValues.put(COL2, name3);
        result = db1.update(TABLE_NAME, contentValues, "ATTRIBUTES= ?", new String[]{"light3"});
        contentValues.put(COL2, name4);
        result = db1.update(TABLE_NAME, contentValues, "ATTRIBUTES= ?", new String[]{"light4"});
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean updateUser(String first, String last, String email, String contact) {
        SQLiteDatabase db1 = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, first);
        long result = db1.update(TABLE_NAME, contentValues, "ATTRIBUTES= ?", new String[]{"first"});
        contentValues.put(COL2, last);
        result = db1.update(TABLE_NAME, contentValues, "ATTRIBUTES= ?", new String[]{"last"});
        contentValues.put(COL2, email);
        result = db1.update(TABLE_NAME, contentValues, "ATTRIBUTES= ?", new String[]{"email"});
        contentValues.put(COL2, contact);
        result = db1.update(TABLE_NAME, contentValues, "ATTRIBUTES= ?", new String[]{"contact"});
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public void changePageState(String value) {
        SQLiteDatabase db1 = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, value);
        long result = db1.update(TABLE_NAME, contentValues, "ATTRIBUTES= ?", new String[]{"firstpage"});
    }
}
