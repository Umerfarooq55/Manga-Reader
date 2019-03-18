package com.dymx101.freemanga.mangareader.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MangaDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "mangamania.db";

    public MangaDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MangaContract.ImagesContract.TABLE_NAME + " (" +
                MangaContract.ImagesContract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MangaContract.ImagesContract.IMAGE_ID + " TEXT UNIQUE NOT NULL," +
                MangaContract.ImagesContract.IMAGE_TITLE + " TEXT NOT NULL," +
                MangaContract.ImagesContract.MANGA_ID + " TEXT NOT NULL," +
                MangaContract.ImagesContract.IMG_THUMBNAIL + " TEXT NOT NULL," +
                "UNIQUE (" + MangaContract.ImagesContract.IMAGE_ID + ") ON CONFLICT IGNORE" +
                " );";
        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
