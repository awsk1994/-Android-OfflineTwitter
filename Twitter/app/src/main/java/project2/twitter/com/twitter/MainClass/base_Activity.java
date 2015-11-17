package project2.twitter.com.twitter.MainClass;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.UUID;

import project2.twitter.com.twitter.Database.Database;
import project2.twitter.com.twitter.Database.Schema;
import project2.twitter.com.twitter.MainActivity;
import project2.twitter.com.twitter.R;
import project2.twitter.com.twitter.model.Feed;
import project2.twitter.com.twitter.model.User;

public class base_Activity extends AppCompatActivity
{
    private static final String tag = "feed_activity:";
    public static final int RQ_CAMERA = 0;

    //To keep track of current fragment
    private static String currentFrag;
    private static String KEY_CURR_FRAG = "current_flag";

    //class instance of fragments.
    FeedList_Fragment feedFrag;
    all_userlist_Fragment listFrag;

    Database mDatabase;

    public static String username;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_);

        if(savedInstanceState != null)
            currentFrag = savedInstanceState.getString(KEY_CURR_FRAG);

        mDatabase = Database.get(getApplicationContext());

        //Get username - so we know what user we are.
        Intent intent = getIntent();
        username = intent.getStringExtra(MainActivity.KEY_USERNAME);

        //Default - we create feedFragment to display feed when we sign in.
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.allfragment_container);
        if(fragment == null)
        {
            Log.i(tag, "create new fragment");
            feedFrag = new FeedList_Fragment();

            //Set string for current Frag
            currentFrag = "feed";
            getSupportFragmentManager().beginTransaction().add(R.id.allfragment_container, feedFrag).commit();
        }
        else if(currentFrag.equals("alluserlist"))
        {
            listFrag = (all_userlist_Fragment)getSupportFragmentManager().findFragmentById(R.id.allfragment_container);
        }
        else
        {
            feedFrag = (FeedList_Fragment)getSupportFragmentManager().findFragmentById(R.id.allfragment_container);
        }
    }

    //This is the base activity to launch different fragments - feed and list of user fragments.
    //This method will allow the activity to switch between the fragments.
    public void switchFragment(String switchTo)
    {
        //If we are switching to the same fragment, then don't need to switch.
        if(!currentFrag.equals(switchTo))
        {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.allfragment_container);
            //To switch fragment, we simply replace the previous fragment.
            if(fragment != null)
            {
                if(switchTo.equals("feed")) {
                    listFrag = null;
                    feedFrag = new FeedList_Fragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.allfragment_container, feedFrag).commit();
                }
                else if(switchTo.equals("alluserlist"))
                {
                    feedFrag = null;
                    listFrag = new all_userlist_Fragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.allfragment_container, listFrag).commit();
                }
                else
                    feedFrag = null;

                currentFrag = switchTo;
            }
            //fragment == null is impossible. But incase it is...we'll add a fragment.
            else
            {
                if(switchTo.equals("feed")) {
                    listFrag = null;
                    feedFrag = new FeedList_Fragment();
                    getSupportFragmentManager().beginTransaction().add(R.id.allfragment_container, feedFrag).commit();
                }
                else if(switchTo.equals("alluserlist"))
                {
                    feedFrag = null;
                    listFrag = new all_userlist_Fragment();
                    getSupportFragmentManager().beginTransaction().add(R.id.allfragment_container, listFrag).commit();
                }
                else
                    feedFrag = null;

                currentFrag = switchTo;
            }
        }
        else
        {
            Toast.makeText(this, R.string.switchtosamefragment_warning, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean handled = false;

        switch(item.getItemId())
        {
            case R.id.bar_alluser:
                switchFragment("alluserlist");
                handled=true;
                break;
            case R.id.bar_feed:
                switchFragment("feed");
                handled=true;
                break;
            case R.id.bar_logout:
                username = "";
                finish();
                break;
            default:
                handled = super.onOptionsItemSelected(item);
                break;
        }

        return handled;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bar_activity, menu);
        return true;
    }

    //In FeedFragment, the feedfragment starts CAMERA activity. The camera activity will return to here, and then, sent to feedFragment to be used.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_CANCELED) {

            if(feedFrag != null)
                feedFrag.photoTaken(true);
            else
                Log.i(tag, "Can't trigger feedfrag - it is null");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_CURR_FRAG, currentFrag);
        super.onSaveInstanceState(outState);
    }
}
