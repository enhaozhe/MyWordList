package com.eazy.mywordlist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper: ";

    private static final String TABLE_NAME = "new_word_table";
    private static final String COL0 = "Word";
    private static final String COL1 = "Definition";
    private static final String COL2 = "LIST";

    DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE  IF NOT EXISTS " + TABLE_NAME  + "(" + COL0 + " TEXT PRIMARY KEY," + COL1 + " TEXT, " + COL2 + " INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion>oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

    int addData(String word, String def, int idx){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL0, word);
        contentValues.put(COL1, def);
        contentValues.put(COL2, idx);

        long result = db.insert(TABLE_NAME, null, contentValues);

        if(result == -1 ){
            return -1;
        }else{
            return 0;
        }
    }

    int updateData(Word word){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL0, word.getWord());
        cv.put(COL1,word.getDef());
        return db.update(TABLE_NAME, cv,  COL0 + " = ?", new String[]{word.getWord()});
    }

    void deleteData(Word word){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + COL0 + " = " + "'" + word.getWord() + "'");
    }

    Cursor getData(int idx){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL2.trim() + " = " + idx;
        return db.rawQuery(query, null);
    }

    public void deleteTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE " + TABLE_NAME);
    }

    void switchList(Word word, int newIdx){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL0, word.getWord());
        contentValues.put(COL1, word.getDef());
        contentValues.put(COL2, newIdx);
        db.update(TABLE_NAME, contentValues, COL0 + " = ?", new String[]{word.getWord()});

    }
}
