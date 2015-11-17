package project2.twitter.com.twitter.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import project2.twitter.com.twitter.Database.Schema.UsersTable;

/**
 * Created by alexwong on 11/12/15.
 */
public class OpenHelper extends SQLiteOpenHelper
{
    private static final String tag = "SQLHelper:";

    public OpenHelper(Context context)
    {
        super(context, Schema.DATABASE_NAME, null, Schema.VERSION);
        Log.i(tag, "constructor");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // do nothing for now...
        Log.i(tag, "onupdgrade");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(tag, "oncreate");
        db.execSQL("create table " + UsersTable.NAME + "(_id integer primary key autoincrement, "
                        + UsersTable.Cols.ID + ", "
                        + UsersTable.Cols.EMAIL + ", "
                        + UsersTable.Cols.PASSWORD + ", "
                        + UsersTable.Cols.FULL_NAME + ", "
                        + UsersTable.Cols.BIRTH_DATE + ", "
                        + UsersTable.Cols.PROFILE_PIC + ", "
                        + UsersTable.Cols.HOMETOWN + ", "
                        + UsersTable.Cols.BIO + ")"
        );
        Log.i(tag, "Users table created");


        db.execSQL("CREATE TABLE " + Schema.FeedItems.NAME
                        + "(_id integer primary key autoincrement, "
                        + Schema.FeedItems.Cols.ID + ", "
                        + Schema.FeedItems.Cols.EMAIL + ", "
                        + Schema.FeedItems.Cols.POSTED_DATE + ", "
                        + Schema.FeedItems.Cols.CONTENT + ", "
                        + Schema.FeedItems.Cols.PHOTO_PATH + ")"
        );
        Log.i(tag, "FeedItems table created.");

        db.execSQL("CREATE TABLE " + Schema.Favorites.NAME
                        + "(_id integer primary key autoincrement, "
                        + Schema.Favorites.Cols.EMAIL + ", "
                        + Schema.Favorites.Cols.FAVORITE + ")"
        );
    }
}
