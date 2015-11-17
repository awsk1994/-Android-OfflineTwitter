package project2.twitter.com.twitter.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import project2.twitter.com.twitter.Database.Database;
import project2.twitter.com.twitter.R;
import project2.twitter.com.twitter.model.User;

public class Dialog_Fragment extends DialogFragment
{
    Database mDatabase;

    private static final String KEY_EMAIL = "key_email";
    private static final String KEY_FULLNAME = "key_fullname";
    private static final String KEY_PHOTPATH = "key_photopath";
    private static final String KEY_BDAY = "key_bday";
    private static final String KEY_BIO = "key_bio";
    private static final String KEY_HOMETOWN = "key_hometown";
    private static final String KEY_FAV = "key_fav";

    TextView mtv_email;
    TextView mtv_fullname;
    TextView mtv_bday;
    TextView mtv_bio;
    TextView mtv_hometown;
    ImageView miv_photo;

    public Dialog_Fragment() {
        // Required empty public constructor
    }

    public static Dialog_Fragment newInstance(String email, String fullname, String photopath, String bday, String bio, String hometown)
    {
        Bundle args = new Bundle();
        args.putString(KEY_EMAIL, email);
        args.putString(KEY_FULLNAME, fullname);
        args.putString(KEY_PHOTPATH, photopath);
        args.putString(KEY_BDAY, bday);
        args.putString(KEY_BIO, bio);
        args.putString(KEY_HOMETOWN, hometown);

        Dialog_Fragment dialog = new Dialog_Fragment();
        dialog.setArguments(args);
        return dialog;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.fragment_dialog, null);
        mDatabase = Database.get(getActivity());

        Bundle args = getArguments();

        mtv_email = (TextView)view.findViewById(R.id.tv_dialog_email);
        mtv_fullname = (TextView)view.findViewById(R.id.tv_dialog_fullname);
        mtv_bday = (TextView)view.findViewById(R.id.tv_dialog_bday);
        mtv_hometown = (TextView)view.findViewById(R.id.tv_dialog_hometown);
        mtv_bio = (TextView)view.findViewById(R.id.tv_dialog_bio);
        miv_photo = (ImageView)view.findViewById(R.id.iv_dialog_profilepic);

        mtv_email.setText(args.getString(KEY_EMAIL));
        mtv_fullname.setText(args.getString(KEY_FULLNAME));
        mtv_bday.setText(args.getString(KEY_BDAY));
        mtv_hometown.setText(args.getString(KEY_HOMETOWN));
        mtv_bio.setText(args.getString(KEY_BIO));

        String photopath = args.getString(KEY_PHOTPATH);
        if(!photopath.equals(""))
        {
            Bitmap image = BitmapFactory.decodeFile(photopath);
            miv_photo.setImageBitmap(image);
        }
        else
        {
            miv_photo.setImageResource(R.mipmap.ic_launcher);
        }

        return new AlertDialog.Builder(getActivity())
                .setTitle(args.getString(KEY_FULLNAME))
                .setView(view)
                .setPositiveButton(R.string.dialog_pos_button, null)
                .create();
    }
}
