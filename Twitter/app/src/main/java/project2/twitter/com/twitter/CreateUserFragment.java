package project2.twitter.com.twitter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Pattern;

import project2.twitter.com.twitter.DateDialog.DateDialogFragment;
import project2.twitter.com.twitter.model.User;
import project2.twitter.com.twitter.Database.Database;

public class CreateUserFragment extends Fragment
{
    private static final String tag = "createuseractivity:";
    private static final String KEY_mPhotoFile = "mphotofile";
    private File mPhotoFile;

    EditText met_email;
    EditText met_password;
    EditText met_fullname;
    EditText met_hometown;
    EditText met_bio;
    TextView mtv_date;
    TextView mtv_photostatus;

    Database mDatabase;
    mCallbackListener mCallback;
    Date mDate;
    String mDateStr;

    boolean mPhotoTaken;

    Button mb_pickdate;
    Button mb_takephoto;
    Button mb_clearall;
    Button mb_return;
    Button mb_submit;

    //ImageView miv_photo;

    public interface mCallbackListener
    {
        public void createuser(User user);
    }

    @Override
    public void onAttach(Context context) {
        mCallback = (mCallbackListener)context;
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_createuser, container, false);

        mDatabase = Database.get(getContext());

        met_email = (EditText) view.findViewById(R.id.et_create_email);
        met_password = (EditText) view.findViewById(R.id.et_create_pw);
        met_fullname = (EditText) view.findViewById(R.id.et_create_fullname);
        met_hometown = (EditText) view.findViewById(R.id.et_create_hometown);
        met_bio = (EditText) view.findViewById(R.id.et_create_biography);

        //miv_photo = (ImageView)view.findViewById(R.id.iv_create_image);

        mtv_date = (TextView) view.findViewById(R.id.tv_create_date);
        mtv_photostatus = (TextView)view.findViewById(R.id.tv_create_photostatus);

        if(savedInstanceState != null && !savedInstanceState.getString(KEY_mPhotoFile).equals(""))
        {
            Log.i(tag, "photo: " + savedInstanceState.getString(KEY_mPhotoFile));
            mPhotoTaken = true;
            mPhotoFile = new File(savedInstanceState.getString(KEY_mPhotoFile));
        }
        else
        {
            mPhotoTaken = false;
        }
        updatePhotoStatus();

        //TODO: regex check email is valid. (regex check password -eg. at least 1 num, 1 alphabet?)
        //SETUP Date
        if (mDate == null)
        {
            mDate = new Date();
        }
        mDateStr = DateFormat.getDateInstance().format(mDate);
        mtv_date.setText(mDateStr);

        mb_pickdate = (Button) view.findViewById(R.id.button_pickdate);
        mb_pickdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(tag, "pick calendar");
                DateDialogFragment dialog = DateDialogFragment.newInstance(mDate);
                dialog.setTargetFragment(CreateUserFragment.this, 0);
                dialog.show(getFragmentManager(), "DateDialog");
            }
        });


        mb_takephoto = (Button) view.findViewById(R.id.button_create_camera);
        mb_takephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        mb_submit = (Button) view.findViewById(R.id.button_create_submit);
        mb_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // UPDATE DATABASE
                User new_user = new User(UUID.randomUUID());

                String email = met_email.getText().toString();
                String pw = met_password.getText().toString();
                String fullname = met_fullname.getText().toString();
                String bio = met_bio.getText().toString();
                String hometown = met_hometown.getText().toString();

                new_user.setEmail(email);
                new_user.setPassword(pw);

                new_user.setBirthdate(mDateStr);
                new_user.setHometown(hometown);

                if(fullname.equals(""))
                    fullname = getString(R.string.fullname_def);
                new_user.setFullname(fullname);

                if(bio.equals(""))
                    bio = getString(R.string.bio_default_text) + fullname;
                new_user.setBio(bio);

                if(mPhotoFile == null)
                    new_user.setProfilepic("");
                else
                    new_user.setProfilepic(mPhotoFile.toString());

                if(email.equals("") || pw.equals(""))
                {
                    Toast.makeText(getActivity(), R.string.warning_noemailorpassword, Toast.LENGTH_SHORT).show();
                }
                else if(mDatabase.checkUserExist(email) == true)
                {
                    Toast.makeText(getActivity(), R.string.warning_emailused, Toast.LENGTH_SHORT).show();
                }
                //For this project, we'll assume that we have simple email formats.
                //In reality, the email format is ofcourse much more complicated.
                else if(!Pattern.matches("[a-zA-Z0-9.-_]+@(([a-zA-Z0-9]+.(com|net|org))|([a-zA-Z0-9]+.[a-zA-Z0-9]+.(com|net|org|edu)))", email))
                {
                    Toast.makeText(getActivity(), R.string.insertvalidemail_warning, Toast.LENGTH_SHORT).show();
                }
                else if(!Pattern.matches("(([0-9]*[a-z]+[0-9]*[A-Z]+[a-zA-Z0-9]*)|([0-9]*[A-Z]+[0-9]*[a-z]+[a-zA-Z0-9]*))", pw))
                {
                    Toast.makeText(getActivity(), R.string.password_warning, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mDatabase.insertUser(new_user);
                    //Add yourself, so that you can see your own post too.
                    mDatabase.insertFavorite(met_email.getText().toString(), met_email.getText().toString());

                    mCallback.createuser(new_user);
                }
            }
        });

        mb_return = (Button) view.findViewById(R.id.button_create_return);
        mb_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.createuser(null);
            }
        });

        mb_clearall = (Button)view.findViewById(R.id.button_create_cleartext);
        mb_clearall.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                met_email.setText("");
                met_password.setText("");
                met_fullname.setText("");
                met_hometown.setText("");
                met_bio.setText("");
                mPhotoTaken = false;
                updatePhotoStatus();
            }
        });

        return view;
    }

    public void updatePhotoStatus()
    {
        if(mPhotoTaken == true)
        {
            mtv_photostatus.setText("Photo Selected.");
        }
        else
        {
            mtv_photostatus.setText("No Photo Selected.");
            mPhotoFile = null;
        }
    }

    public void takePhoto()
    {
        Log.i(tag, "take photo - start activity from fragment");
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        String filename = "IMG_" + UUID.randomUUID().toString() + ".jpg";
        File pictureDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        mPhotoFile = new File(pictureDir, filename);

        Uri photoUri = Uri.fromFile(mPhotoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

        getActivity().startActivityForResult(intent, CreateUserActivity.RQ_CAMERA);
        Log.i(tag, "take photo - OK");
    }


    //A method called by parent activity upon receiving result from the Camera activity that we created in this class.
    public void photoTaken(boolean taken)
    {
        Log.i(tag, "phototaken");
        mPhotoTaken = taken;
        updatePhotoStatus();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //When the calendar widget pops up, we choose a date, and after we click OK, we want to update the mDate and mDateString immediately.
        if(resultCode == Activity.RESULT_OK)
        {
            Log.i(tag, "onActivityResult");
            //Get new date from fragment.
            mDate = (Date) data.getSerializableExtra(DateDialogFragment.EXTRA_DATE);
            if(mDate == null)
            {
                mDate = new Date();
            }

            mDateStr = DateFormat.getDateInstance().format(mDate);

            //Update TextView with string format of date.
            mtv_date.setText(mDateStr);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        if(mPhotoTaken)
            outState.putString(KEY_mPhotoFile, mPhotoFile.toString());
        else
            outState.putString(KEY_mPhotoFile, "");
    }
}
