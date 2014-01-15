package com.pocketknife.state;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import com.pocketknife.state.StateContract;

/**
 * Created by Thomas Moulia on 1/9/14.
 */
public class StateProvider extends ContentProvider {

    static final String TAG = "com.pocketknife.state.StateProvider";

    static final String DB_NAME = "state.db";
    static final int DB_VERSION = 2;
    static final String TABLE = "notes";

    public static class Seeder {

        static final ContentValues[] seeds;

        static {
            Log.i(TAG, "Seeding database...");
            seeds = new ContentValues[]{
                    new ContentValues(),
                    new ContentValues(),
                    new ContentValues()};

            for (int i = 0; i < seeds.length; i++) {
                seeds[i].put(
                        StateContract.Notes.TITLE,
                        "Note: " + String.valueOf(i));
                seeds[i].put(
                        StateContract.Notes.BODY,
                        "This is the body. I wish I had an ipsum gen" );
            }
        }

        static void seed(ContentResolver contentResolver) {
            for (ContentValues contentValues : seeds) {
                Log.d(TAG, "Inserting: " + contentValues.toString());
                contentResolver.insert(StateContract.Notes.CONTENT_URI, contentValues);
            }
        }
    }

    public class DbHelper extends SQLiteOpenHelper {
        public DbHelper(Context ctx) {
            super(ctx, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(String.format(
                    "CREATE TABLE %s ("
                            + " %s INTEGER PRIMARY KEY"
                            + ", %s title TEXT NOT NULL"
                            + ", %s body TEXT NOT NULL"
                            + ", %s created DATETIME NOT NULL"
                            + ", %s updated DATETIME NOT NULL"
                            + ")",
                    TABLE,
                    StateContract.Notes._ID,
                    StateContract.Notes.TITLE,
                    StateContract.Notes.BODY,
                    StateContract.Notes.CREATED,
                    StateContract.Notes.UPDATED));
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVsn, int newVsn) {
            Log.w(TAG, "Upgrading db -- dropping");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE);
            onCreate(db);
        }
    }


    // URI Handling
    static final int NOTES_MATCH = 1;
    static final int NOTE_MATCH = 2;
    private static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);


    static {
        uriMatcher.addURI(
                StateContract.AUTHORITY,
                StateContract.Notes.CONTENT_URI_SUFFIX,
                NOTES_MATCH);
        uriMatcher.addURI(
                StateContract.AUTHORITY,
                StateContract.Notes.CONTENT_URI_SUFFIX + "/*",
                NOTE_MATCH);
    }

    /**
     * Instance Variables
     */
    private SQLiteDatabase mDb;


    @Override
    public boolean onCreate() {
         mDb = new DbHelper(getContext()).getWritableDatabase();
         return true;
    }

    @Override
    // TODO -  Implement
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "Being queried: " + Arrays.toString(projection));
        switch (uriMatcher.match(uri)) {
            case NOTES_MATCH:
                return mDb.query(
                        TABLE,
                        projection,
                        selection, selectionArgs,
                        "", "",
                        sortOrder);

            default:
                throw new IllegalArgumentException("Invalid URI");
        }
    }

    @Override
    public String getType(Uri uri) {
        Log.d(TAG, "Get type called for: " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case NOTES_MATCH:
                return StateContract.Notes.MIME_TYPE;

            default:
                throw new IllegalArgumentException("invalid uri");
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        switch (uriMatcher.match(uri)) {
            case NOTES_MATCH:
                Log.d(TAG, "Inserting Note");

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                String now = dateFormat.format(date);

                if (contentValues.get(StateContract.Notes.CREATED) == null) {
                    contentValues.put(StateContract.Notes.CREATED, now);
                }
                if (contentValues.get(StateContract.Notes.UPDATED) == null) {
                    contentValues.put(StateContract.Notes.UPDATED, now);
                }

                long row = mDb.insertOrThrow(TABLE, null, contentValues);
                // TODO - row == _ID ? Verify.... with tests!
                return Uri.withAppendedPath(uri, String.valueOf(row));

            default:
                throw new IllegalArgumentException("invalid uri");
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case NOTES_MATCH:
                Log.w(TAG, "Deleting all notes. I don't like this, either");
                return mDb.delete(TABLE, null, new String[]{});


            case NOTE_MATCH:
                if (selection != null) {
                    throw new IllegalArgumentException(
                            "selection isn't currently supported");
                }
                String whereClause = String.format("%s = ?", StateContract.Notes._ID);
                // XXX - I'm flipping back and forth between strings and ints
                String[] whereArgs = {String.valueOf(getNoteId(uri))};
                return mDb.delete(TABLE,  whereClause, whereArgs);

            default:
                Log.e(TAG, "Can't delete -- unknown uri");
                return 0;

        }
    }

    @Override
    public int update(
            Uri uri,
            ContentValues contentValues,
            String selection,
            String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case NOTE_MATCH:
                if (selection == null && selectionArgs.length == 0) {
                    selection = String.format("%s = ?", StateContract.Notes._ID);
                    selectionArgs = new String[]{String.valueOf(getNoteId(uri))};
                    int updates = mDb.update(TABLE, contentValues, selection, selectionArgs);

                    // If updates are made, notify observers
                    if (updates > 0) {
                        getContext().getContentResolver().notifyChange(uri, null);
                        getContext().getContentResolver().notifyChange(
                                StateContract.CONTENT_URI, null);
                    }

                    return updates;
                } else {
                    throw new IllegalArgumentException("can't accept selection if id is provided");
                }

            default:
                Log.e(TAG, "Can't update -- unknown uri");
                return 0;
        }
    }

    /**
     * Parses out and returns the id provided in the uri. Will
     * fail if the last segment isn't an int.
     *
     * @param uri A content uri
     * @return The id provided in the uri
     */
    private int getNoteId(Uri uri) {
        return Integer.parseInt(uri.getLastPathSegment());
    }
}
