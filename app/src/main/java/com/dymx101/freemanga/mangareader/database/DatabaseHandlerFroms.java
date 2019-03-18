package com.dymx101.freemanga.mangareader.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by CHAUDHRY HAMDULLAH on 11/14/2017.
 */

public class DatabaseHandlerFroms
        extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "chapter";

    // Contacts table name
    private static final String TABLE_FIELDS = "chapter_images";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_CHAPTER_ID = "chapterid";
    private static final String KEY_IMAGE = "image";


    public DatabaseHandlerFroms(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_FIELDS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_CHAPTER_ID + " TEXT,"
                + KEY_IMAGE + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FIELDS);

        // Create tables again
        onCreate(db);
    }
    public long addContact(String chapterid,String image) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CHAPTER_ID, chapterid); // Contact Name
        values.put(KEY_IMAGE, image); // Contact Phone


        // Inserting Row
        long res= db.insert(TABLE_FIELDS, null, values);
        Log.e("hamdullah Form",res+"");
        db.close(); // Closing database connection
        return res;
    }
   public  ArrayList<String> getformdata(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
ArrayList<String> tempimages=new ArrayList<String>();
        Cursor cursor = db.query(TABLE_FIELDS, new String[] { KEY_ID,
                        KEY_CHAPTER_ID, KEY_IMAGE}, KEY_CHAPTER_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);


       if (cursor.moveToFirst()) {
           do {
               ;
               tempimages.add(cursor.getString(2));
           } while (cursor.moveToNext());
       }



        return tempimages;
    }



    public long deleteall(String id) {
        SQLiteDatabase db = this.getWritableDatabase();

        long res=db.delete(TABLE_FIELDS, KEY_CHAPTER_ID + " = ?",
                  new String[] { id});

        db.close();

        return res;
    }

    // Getting contacts Count

}