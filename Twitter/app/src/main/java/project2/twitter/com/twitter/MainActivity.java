package project2.twitter.com.twitter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

import project2.twitter.com.twitter.Database.Database;
import project2.twitter.com.twitter.Database.Schema;
import project2.twitter.com.twitter.model.User;
import project2.twitter.com.twitter.MainClass.base_Activity;

public class MainActivity extends AppCompatActivity
{
    EditText met_username;
    EditText met_password;
    Button mb_login;
    Button mb_create;

    //temp
    Database mDatabase;

    private static final String tag = "mainActivity:";
    private static final int RQ_CREATE = 1;

    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";

    private static final String KEY_USERNAME_INPUT = "username_input";
    private static final String KEY_PASSWORD_INPUT = "password_input";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = Database.get(MainActivity.this);

        met_username = (EditText)findViewById(R.id.et_login_username);
        met_password = (EditText)findViewById(R.id.et_login_password);

        if(savedInstanceState != null)
        {
            met_username.setText(savedInstanceState.getString(KEY_USERNAME_INPUT));
            met_password.setText(savedInstanceState.getString(KEY_PASSWORD_INPUT));
        }

        mb_login = (Button)findViewById(R.id.button_login_OK);
        mb_create = (Button)findViewById(R.id.button_login_create);

        mb_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CreateUserActivity.class);
                startActivityForResult(intent, RQ_CREATE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_CANCELED)
        {
            switch(requestCode)
            {
                case RQ_CREATE:

                    Log.i(tag, "Create User completed.");
                    // Login with the returned information.
                    String username = data.getStringExtra(KEY_USERNAME);
                    String password = data.getStringExtra(KEY_PASSWORD);
                    login_auth(username, password);
                    break;

                default:
                    break;
            }
        }
    }

    public void login_auth(String username, String password)
    {
        boolean verified = mDatabase.verifyUserLogin(username, password);
        if(!verified)
        {
            if(mDatabase.checkUserExist(username))
            {
                Toast.makeText(MainActivity.this, "Wrong password. Please try again!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(MainActivity.this, "Sorry, but " + username + " does not exist in our database.", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(MainActivity.this, "LOGIN to " + username + " SUCCESS!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, base_Activity.class);
            intent.putExtra(KEY_USERNAME, username);
            startActivity(intent);
        }
    }

    public void login(View v)
    {
        Log.i(tag, "Get specific user: " + met_username.getText().toString());
        login_auth(met_username.getText().toString(), met_password.getText().toString());
        met_username.setText("");
        met_password.setText("");
    }

 //   /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean handled = false;

        switch(item.getItemId())
        {
            case R.id.menubar_reset:
                mDatabase.clearData(Schema.UsersTable.NAME);
                mDatabase.clearData(Schema.Favorites.NAME);
                mDatabase.clearData(Schema.FeedItems.NAME);

                Toast.makeText(this, "CLEARING ALL DATA (feed, usertable and favourite)", Toast.LENGTH_SHORT).show();
                handled = true;
                break;
            case R.id.menubar_testcase:
                User user1 = new User(UUID.randomUUID());
                user1.setEmail("aa");
                user1.setPassword("aa");
                user1.setProfilepic("");
                user1.setBio("");
                user1.setBirthdate("");
                user1.setHometown("");
                user1.setFullname("");
                mDatabase.insertUser(user1);

                User user2 = new User(UUID.randomUUID());
                user2.setEmail("bb");
                user2.setPassword("aa");
                user2.setProfilepic("");
                user2.setBio("");
                user2.setBirthdate("");
                user2.setHometown("");
                user2.setFullname("");

                mDatabase.insertUser(user2);
                break;
            default:
                handled = super.onOptionsItemSelected(item);
                break;
        }

        return handled;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_layout, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_USERNAME_INPUT, met_username.getText().toString());
        outState.putString(KEY_PASSWORD_INPUT, met_password.getText().toString());
        super.onSaveInstanceState(outState);
    }

    //    */

}
