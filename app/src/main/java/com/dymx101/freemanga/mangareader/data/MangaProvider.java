package com.dymx101.freemanga.mangareader.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.dymx101.freemanga.mangareader.data.MangaContract.ImagesContract.TABLE_NAME;

public class MangaProvider extends ContentProvider {

    public static final int MOVIES = 100;
    public static final int MOVIES_WITH_ID = 101;
    public static String MANGA_LIST_STRING = null;

    // CDeclare a static variable for the Uri matcher that you construct
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        //this method will be called first when activity is launched
        sUriMatcher.addURI(MangaContract.CONTENT_AUTHORITY, MangaContract.PATH_IMAGES, MOVIES);
        sUriMatcher.addURI(MangaContract.CONTENT_AUTHORITY, MangaContract.PATH_IMAGES + "/#", MOVIES_WITH_ID);

    }

    private MangaDbHelper mTaskDbHelper;

    @Override
    public boolean onCreate() {
        mTaskDbHelper = new MangaDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        final SQLiteDatabase db = mTaskDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            case MOVIES:
                retCursor = db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case MOVIES_WITH_ID:

                String normalizedUtcDateString = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{normalizedUtcDateString};

                retCursor = mTaskDbHelper.getReadableDatabase().query(
                        /* Table we are going to query */
                        TABLE_NAME,
                        projection,
                        MangaContract.ImagesContract.IMAGE_ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);


        }


        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIES:
                // directory
                return "vnd.android.cursor.dir" + "/" + MangaContract.CONTENT_AUTHORITY + "/" + MangaContract.PATH_IMAGES;
            case MOVIES_WITH_ID:
                // single item type
                return "vnd.android.cursor.item" + "/" + MangaContract.CONTENT_AUTHORITY + "/" + MangaContract.PATH_IMAGES;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mTaskDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIES:
                long id = db.insert(TABLE_NAME, null, values);
                returnUri = ContentUris.withAppendedId(MangaContract.ImagesContract.CONTENT_URI, id);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mTaskDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int tasksDeleted;

        switch (match) {

            case MOVIES:
                tasksDeleted = db.delete(
                        MangaContract.ImagesContract.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                tasksDeleted = db.delete(TABLE_NAME, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return tasksDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mTaskDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int tasksUpdated;

        switch (match) {

            case MOVIES:
                tasksUpdated = db.update(
                        MangaContract.ImagesContract.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;

            case MOVIES_WITH_ID:
                //update a single task by getting the id
                String id = uri.getPathSegments().get(1);
                //using selections
                tasksUpdated = mTaskDbHelper.getWritableDatabase().update(TABLE_NAME, values, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (tasksUpdated != 0) {
            //set notifications if a task was updated
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // return number of tasks updated
        return tasksUpdated;
    }
}
