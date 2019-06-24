package com.eazy.mywordlist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper: ";

    private static final String TABLE_NAME = "new_word_table";
    private static final String COL0 = "Word";
    private static final String COL1 = "Definition";

    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (Word TEXT PRIMARY KEY, " + COL1 + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public int addData(String word, String def){
        SQLiteDatabase db = this.getWritableDatabase();
/*
        String Query = "Select * from " + TABLE_NAME + " where " + COL0 + " = " + word;
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return -1;
        }
        cursor.close();*/

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL0, word);
        contentValues.put(COL1, def);

        Log.d(TAG,"Add Data : " + word + " meaning: " + def);

        long result = db.insert(TABLE_NAME, null, contentValues);

        if(result == -1 ){
            return -2;
        }else{
            return 0;
        }
    }

    public void deleteData(Word word){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + COL0 + " = " + "'" + word.getWord() + "'");
        db.close();
    }

    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT  * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }
}
