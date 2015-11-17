package project2.twitter.com.twitter.Database;

/**
 * Created by alexwong on 11/12/15.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import project2.twitter.com.twitter.model.User;
import project2.twitter.com.twitter.model.Feed;

public class Database {
    private static final String tag = "database:";
    private static Database DATABASE;
    private static Context mContext;
    private final SQLiteDatabase mDatabase;
    private final List<User> mUserList;
    private final List<Feed> mFeedList;
    private final List<String> mFavList;

    private Database(Context context)
    {
        mDatabase = new OpenHelper(context).getWritableDatabase();
        mUserList = new LinkedList<>();
        mFeedList = new LinkedList<>();
        mFavList = new LinkedList<>();
    }

    public static synchronized Database get(Context context) {
        if(DATABASE == null) {
            Log.i(tag, "No Database, create new Database");
            mContext = context.getApplicationContext();

            DATABASE = new Database(mContext);
        }
        return DATABASE;
    }

    //USER GET FUNCTIONS
    public boolean verifyUserLogin(String email, String password) {
        if (mDatabase == null)
            Log.i(tag, "mdatabase is null");
        Cursor cursor = mDatabase.query(
                Schema.UsersTable.NAME, // table name
                null,
                "email=? AND password=?",
                new String[] { email, password},
                null,
                null,
                null
        );

        AllUseCursorWrapper wrapper = new AllUseCursorWrapper(cursor);
        User user;
        if(wrapper.getCount() > 0) {
            wrapper.moveToFirst();
            user = wrapper.getUser();
        }
        else {
            user = null;
        }
        wrapper.close();

        if(user == null)
            return false;
        else
            return true;
    }

    public String getUserPhoto(String email) {
        if (mDatabase == null)
            Log.i(tag, "mdatabase is null");
        Cursor cursor = mDatabase.query(
                Schema.UsersTable.NAME, // table name
                null,
                "email=?",
                new String[] { email},
                null,
                null,
                null
        );

        AllUseCursorWrapper wrapper = new AllUseCursorWrapper(cursor);
        User user;
        if(wrapper.getCount() > 0) {
            wrapper.moveToFirst();
            user = wrapper.getUser();
        }
        else {
            user = null;
        }
        wrapper.close();

        return user.getProfilepic();
    }

    public boolean checkUserExist(String email) {
        if (mDatabase == null)
            Log.i(tag, "mdatabase is null");
        Cursor cursor = mDatabase.query(
                Schema.UsersTable.NAME, // table name
                null,
                "email=?",
                new String[] { email},
                null,
                null,
                null
        );

        AllUseCursorWrapper wrapper = new AllUseCursorWrapper(cursor);
        User user;
        if(wrapper.getCount() > 0) {
            wrapper.moveToFirst();
            user = wrapper.getUser();
        }
        else {
            user = null;
        }
        wrapper.close();

        if(user==null)
            return false;
        else
            return true;
    }

    //OTHER GET FUNCTIONS
    public boolean checkFav(String name, String fav)
    {
        boolean check;
        Cursor cursor = mDatabase.query(
                Schema.Favorites.NAME,
                null,
                "email=? AND favorite=?",
                new String[] {name, fav},
                null,
                null,
                null);

        AllUseCursorWrapper wrapper = new AllUseCursorWrapper(cursor);
        if(wrapper.getCount() > 0)
        {
            check = true;
        }
        else
        {
            check = false;
        }
        wrapper.close();

        return check;
    }

    //GET LISTS FUNCTION
    public List<User> getUserList()
    {
        mUserList.clear();
        AllUseCursorWrapper wrapper = queryList(Schema.UsersTable.NAME, null, null);

        try
        {
            Log.i(tag,"wrapper");
            wrapper.moveToFirst();
            while(wrapper.isAfterLast() == false)
            {
                User user = wrapper.getUser();
                if(user != null)
                {
                    Log.i(tag, "add object to list");
                    mUserList.add(user);

                }
                else
                    Log.i(tag, "object got back is null");
                wrapper.moveToNext();
            }
        }
        finally {
            wrapper.close();
        }

        return mUserList;
    }

    public List<Feed> getFeedList()
    {
        mFeedList.clear();
        AllUseCursorWrapper wrapper = queryList(Schema.FeedItems.NAME, null, null);

        try
        {
            Log.i(tag,"wrapper");
            wrapper.moveToFirst();
            while(wrapper.isAfterLast() == false)
            {
                Feed feed = wrapper.getFeed();
                if(feed != null)
                {
                    Log.i(tag, "add object to list");
                    mFeedList.add(feed);

                }
                else
                    Log.i(tag, "object got back is null");
                wrapper.moveToNext();
            }
        }
        finally {
            wrapper.close();
        }

        return mFeedList;
    }

    public List<String> getFavList(String email)
    {
        mFavList.clear();
        AllUseCursorWrapper wrapper = queryList(Schema.Favorites.NAME, "email=?", new String[]{email});

        try
        {
            wrapper.moveToFirst();
            while(wrapper.isAfterLast() == false)
            {
                String fav = wrapper.getFav();
                if(fav != null)
                {
                    Log.i(tag, "Add object to list");
                    mFavList.add(fav);
                }
                else
                    Log.i(tag, "Table got back is null");
                wrapper.moveToNext();
            }
        }
        finally {
            wrapper.close();
        }

        return mFavList;
    }

    //USER INSERT FUNCTIONS
    public void insertUser(User user) {
        ContentValues values = getUserContentValues(user);
        mDatabase.insert(
                Schema.UsersTable.NAME,
                null,
                values
        );
    }

    public void insertFeedItem(Feed item) {
        ContentValues values = getFeedItemContentValues(item);
        Log.i(tag, "insert Feed: " + item.getContent().toString());
        mDatabase.insert(
                Schema.FeedItems.NAME,
                null,
                values
        );
    }

    public void insertFavorite(String email, String favorite) {
        ContentValues values = new ContentValues();
        values.put(Schema.Favorites.Cols.EMAIL, email);
        values.put(Schema.Favorites.Cols.FAVORITE, favorite);

        mDatabase.insert(
                Schema.Favorites.NAME,
                null,
                values
        );
    }

    //DELETE FAV
    public void unFav(String email, String fav)
    {
        mDatabase.delete(Schema.Favorites.NAME, "email=? AND favorite=?", new String[]{email, fav});
    }

    public void clearData(String tableName)
    {
        mDatabase.execSQL("delete from " + tableName);
    }

    //CURSOR WRAPPER
    private AllUseCursorWrapper queryList(String tableName, String where, String[] args)
    {
        Cursor cursor = mDatabase.query(
                tableName,
                null,
                where,
                args,
                null,
                null,
                null
        );

        return new AllUseCursorWrapper(cursor);
    }

    private static ContentValues getUserContentValues(User user) {
        ContentValues values = new ContentValues();
        values.put(Schema.UsersTable.Cols.ID, user.getID().toString());
        values.put(Schema.UsersTable.Cols.EMAIL, user.getEmail().toString());
        values.put(Schema.UsersTable.Cols.PASSWORD, user.getPassword().toString());
        values.put(Schema.UsersTable.Cols.FULL_NAME, user.getFullname().toString());
        values.put(Schema.UsersTable.Cols.BIRTH_DATE, user.getBirthdate().toString());
        values.put(Schema.UsersTable.Cols.PROFILE_PIC, user.getProfilepic().toString());
        values.put(Schema.UsersTable.Cols.BIO, user.getBio().toString());
        values.put(Schema.UsersTable.Cols.HOMETOWN, user.getHomeTown().toString());
        return values;
    }

    private static ContentValues getFeedItemContentValues(Feed item) {
        ContentValues values = new ContentValues();
        values.put(Schema.FeedItems.Cols.ID, item.getId().toString());
        values.put(Schema.FeedItems.Cols.EMAIL, item.getEmail());
        values.put(Schema.FeedItems.Cols.POSTED_DATE, item.getPostedDate().getTime());
        values.put(Schema.FeedItems.Cols.CONTENT, item.getContent());
        values.put(Schema.FeedItems.Cols.PHOTO_PATH, item.getPhotoPath());
        return values;
    }

    private static class AllUseCursorWrapper extends CursorWrapper {
        public AllUseCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public User getUser() {
            Log.i(tag, "getUser");
            UUID id = UUID.fromString(getString(getColumnIndex(Schema.UsersTable.Cols.ID)));
            User new_user = new User(id);

            int emailIndex = getColumnIndex(Schema.UsersTable.Cols.EMAIL);
            String email = getString(emailIndex);
            new_user.setEmail(email);
            new_user.setPassword(getString(getColumnIndex(Schema.UsersTable.Cols.PASSWORD)));
            new_user.setFullname(getString(getColumnIndex(Schema.UsersTable.Cols.FULL_NAME)));
            new_user.setBirthdate(getString(getColumnIndex(Schema.UsersTable.Cols.BIRTH_DATE)));
            new_user.setProfilepic(getString(getColumnIndex(Schema.UsersTable.Cols.PROFILE_PIC)));
            new_user.setHometown(getString(getColumnIndex(Schema.UsersTable.Cols.HOMETOWN)));
            new_user.setBio(getString(getColumnIndex(Schema.UsersTable.Cols.BIO)));

            Log.i(tag, "user: " + new_user.getEmail() +", " + new_user.getProfilepic());

            return new_user;
        }

        public Feed getFeed() {
            Log.i(tag, "getFeed");
            UUID id = UUID.fromString(getString(getColumnIndex(Schema.UsersTable.Cols.ID)));
            Feed feed = new Feed(id);
            feed.setEmail(getString(getColumnIndex(Schema.FeedItems.Cols.EMAIL)));
            feed.setContent(getString(getColumnIndex(Schema.FeedItems.Cols.CONTENT)));
            feed.setPhotoPath(getString(getColumnIndex(Schema.FeedItems.Cols.PHOTO_PATH)));
            feed.setPostedDate(new Date(getLong(getColumnIndex(Schema.FeedItems.Cols.POSTED_DATE))));
            return feed;
        }

        public String getFav()
        {
            Log.i(tag, "getFav");
            return getString(getColumnIndex(Schema.Favorites.Cols.FAVORITE));
        }
    }
}