package com.example.imp_project.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.imp_project.model.Diary;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "my_diary.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_NOTE_CREATE =
            "CREATE TABLE " + TableInfo
                    .DiaryEntry.TABLE_NAME + " (" +
                    TableInfo.DiaryEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TableInfo.DiaryEntry.COLUMN_TITLE + " TEXT, " +
                    TableInfo.DiaryEntry.COLUMN_DESCRIPTION + " TEXT, " +
                    TableInfo.DiaryEntry.COLUMN_EMOJI+ " TEXT, " +
                    TableInfo.DiaryEntry.COLUMN_PASSWORD+ " TEXT, " +
                    TableInfo.DiaryEntry.COLUMN_URI+ " TEXT, " +
                    TableInfo.DiaryEntry.COLUMN_LOCATION+ " TEXT, " +
                    TableInfo.DiaryEntry.COLUMN_DATE + " TEXT DEFAULT CURRENT_TIMESTAMP" +
                    ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_NOTE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TableInfo.DiaryEntry.TABLE_NAME);

        onCreate(db);
    }

    public Diary addNote(Diary diary) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TableInfo.DiaryEntry.COLUMN_TITLE, diary.getTitle().trim());
        cv.put(TableInfo.DiaryEntry.COLUMN_EMOJI, diary.getModeEmoji().trim());
        cv.put(TableInfo.DiaryEntry.COLUMN_DESCRIPTION, diary.getDescription().trim());
        cv.put(TableInfo.DiaryEntry.COLUMN_PASSWORD, diary.getPassword().trim());
        cv.put(TableInfo.DiaryEntry.COLUMN_DATE, diary.getDate().toString());
        cv.put(TableInfo.DiaryEntry.COLUMN_LOCATION,diary.getLocation());
        cv.put(TableInfo.DiaryEntry.COLUMN_URI, diary.getUri());
        long result = db.insert(TableInfo.DiaryEntry.TABLE_NAME, null, cv);

        if (result > -1)
            Log.i("DatabaseHelper", "Anı başarıyla kaydedildi");
        else
            Log.i("DatabaseHelper", "Anı kaydedilemedi");

        db.close();
        return diary;
    }

    public void deleteNote(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TableInfo.DiaryEntry.TABLE_NAME, TableInfo.DiaryEntry.COLUMN_ID + "=?", new String[]{id.toString()});
        db.close();
    }

    public void setPassword(Diary diary,String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TableInfo.DiaryEntry.COLUMN_PASSWORD,password);


        db.update(TableInfo.DiaryEntry.TABLE_NAME,cv,TableInfo.DiaryEntry.COLUMN_ID+"= ?", new String[]{diary.getId().toString()});
        db.close();
    }
    public void update(Diary diary) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TableInfo.DiaryEntry.TABLE_NAME, TableInfo.DiaryEntry.COLUMN_ID + "=?", new String[]{diary.getId().toString()});


        ContentValues cv = new ContentValues();
        cv.put(TableInfo.DiaryEntry.COLUMN_ID,diary.getId());
        cv.put(TableInfo.DiaryEntry.COLUMN_TITLE, diary.getTitle().trim());
        cv.put(TableInfo.DiaryEntry.COLUMN_EMOJI, diary.getModeEmoji().trim());
        cv.put(TableInfo.DiaryEntry.COLUMN_DESCRIPTION, diary.getDescription().trim());
        cv.put(TableInfo.DiaryEntry.COLUMN_PASSWORD, diary.getPassword().trim());
        cv.put(TableInfo.DiaryEntry.COLUMN_DATE, diary.getDate().toString());
        cv.put(TableInfo.DiaryEntry.COLUMN_URI, diary.getUri());
        long result = db.insert(TableInfo.DiaryEntry.TABLE_NAME, null, cv);

        if (result > -1)
            Log.i("DatabaseHelper", "Anı başarıyla güncellendi");
        else
            Log.i("DatabaseHelper", "Anı güncellenemedi");

        db.close();

    }

    public ArrayList<Diary> getNoteList() {
        ArrayList<Diary> data = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                TableInfo.DiaryEntry.COLUMN_ID,
                TableInfo.DiaryEntry.COLUMN_TITLE,
                TableInfo.DiaryEntry.COLUMN_DESCRIPTION,
                TableInfo.DiaryEntry.COLUMN_PASSWORD,
                TableInfo.DiaryEntry.COLUMN_EMOJI,
                TableInfo.DiaryEntry.COLUMN_LOCATION,
                TableInfo.DiaryEntry.COLUMN_URI,
                TableInfo.DiaryEntry.COLUMN_DATE,};

        Cursor c = db.query(TableInfo.DiaryEntry.TABLE_NAME, projection, null, null, null, null, null);
        while (c.moveToNext()) {
            data.add(new Diary(c.getInt(c.getColumnIndexOrThrow(TableInfo.DiaryEntry.COLUMN_ID)),
                    c.getString(c.getColumnIndexOrThrow(TableInfo.DiaryEntry.COLUMN_TITLE)),
                    c.getString(c.getColumnIndexOrThrow(TableInfo.DiaryEntry.COLUMN_EMOJI)),
                    c.getString(c.getColumnIndexOrThrow(TableInfo.DiaryEntry.COLUMN_URI)),
                    c.getString(c.getColumnIndexOrThrow(TableInfo.DiaryEntry.COLUMN_DESCRIPTION)),
                    c.getString(c.getColumnIndexOrThrow(TableInfo.DiaryEntry.COLUMN_PASSWORD)),
                    LocalDateTime.parse(c.getString(c.getColumnIndexOrThrow(TableInfo.DiaryEntry.COLUMN_DATE))),
                    c.getString(c.getColumnIndexOrThrow(TableInfo.DiaryEntry.COLUMN_LOCATION))
                    ));
        }

        c.close();
        db.close();

        return data;
    }
}
