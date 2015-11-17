package project2.twitter.com.twitter;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import project2.twitter.com.twitter.model.User;

public class CreateUserActivity extends AppCompatActivity implements CreateUserFragment.mCallbackListener
{
    private static final String tag = "createuseractivity:";
    public static final int RQ_CAMERA = 0;
    CreateUserFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        fragment = (CreateUserFragment)getSupportFragmentManager().findFragmentById(R.id.createuser_fragment_container);
        if(fragment == null)
        {
            Log.i(tag, "create fragment");
            fragment = new CreateUserFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.createuser_fragment_container, fragment).commit();
        }
    }

    @Override
    public void createuser(User user) {
        if(user != null)
        {
            Intent intent = new Intent();
            intent.putExtra(MainActivity.KEY_USERNAME, user.getEmail());
            intent.putExtra(MainActivity.KEY_PASSWORD, user.getPassword());
            setResult(RESULT_OK, intent);
            finish();
        }
        else
        {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(tag, "activityresult");
        if(resultCode == RESULT_OK)
        {
            if(fragment == null)
            {
                fragment = (CreateUserFragment)getSupportFragmentManager().findFragmentById(R.id.createuser_fragment_container);
            }
            else
                Log.i(tag, "fragment ready");

            fragment.photoTaken(true);
        }
    }
}
