package project2.twitter.com.twitter.Database;

/**
 * Created by alexwong on 11/12/15.
 */
public class Schema
{
    public static final String DATABASE_NAME = "project2.db";
    public static final int VERSION = 1;

    public static class UsersTable
    {
        public static final String NAME = "users";
        public static class Cols
        {
            public static final String ID = "id";
            public static final String EMAIL = "email";
            public static final String PASSWORD = "password";
            public static final String FULL_NAME = "full_name";
            public static final String BIRTH_DATE = "bday";
            public static final String PROFILE_PIC = "prof_pic";
            public static final String HOMETOWN = "hometown";
            public static final String BIO = "bio";
        }
    }

    public static class FeedItems {
        public static final String NAME = "feed_items";
        public static class Cols {
            public static final String ID = "id";
            public static final String EMAIL = "email";
            public static final String POSTED_DATE = "posted_date";
            public static final String CONTENT = "content";
            public static final String PHOTO_PATH = "photo_path";
        }
    }

    public static class Favorites {
        public static final String NAME = "favorites";
        public static class Cols {
            //should we do from_user, and liked_user???
            public static final String EMAIL = "email";
            public static final String FAVORITE = "favorite";
        }
    }
}
