package project2.twitter.com.twitter.MainClass;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import project2.twitter.com.twitter.CreateUserActivity;
import project2.twitter.com.twitter.Database.Database;
import project2.twitter.com.twitter.Database.Schema;
import project2.twitter.com.twitter.R;
import project2.twitter.com.twitter.model.Feed;
import project2.twitter.com.twitter.model.User;

/**
 * Created by alexwong on 11/13/15.
 */
public class FeedList_Fragment extends Fragment
{
    private static final String tag = "feedlist_fragment:";
    public static final String ARG_STATUS = "status";
    private static final String KEY_PHOTO = "photo";

    private FeedAdapter mAdapter;
    private RecyclerView mRecyclerView;

    ImageButton mib_poststatus;
    ImageButton mib_takephoto;
    ImageView miv_feedphoto;

    File mPhotoFile;

    boolean mPhotoTaken;
    Date mDate;
    String mDateStr;

    Database mDatabase;
    EditText met_status;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.i(tag, "onCreateView");

        View view = inflater.inflate(R.layout.fragment_feedlist, container, false);

        mDatabase = Database.get(getContext());

        met_status = (EditText)view.findViewById(R.id.et_feedlist_status);
        miv_feedphoto = (ImageView)view.findViewById(R.id.iv_feedlist_feedphoto);

        if(savedInstanceState != null && !savedInstanceState.getString(KEY_PHOTO).equals(""))
        {
            Log.i(tag, "saveInstance - getPhoto");
            mPhotoTaken = true;
            mPhotoFile = new File(savedInstanceState.getString(KEY_PHOTO));
        }
        else
        {
            Log.i(tag, "no  saveInstance - no photo");
            mPhotoTaken = false;
            mPhotoFile = null;
        }
        updatePhoto();


        mib_takephoto = (ImageButton)view.findViewById(R.id.ib_feedlist_takephoto);
        mib_takephoto.setOnClickListener(new View.OnClickListener()
        {
             @Override
             public void onClick(View v) {
                takePhoto();
             }
        });

        mib_poststatus = (ImageButton) view.findViewById(R.id.ib_feedlist_poststatus);
        mib_poststatus.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Feed newStatus = new Feed(UUID.randomUUID());
                //TODO: If there is no photo and no content, then send error -> there is nothing to post.
                //Create Date - should by default configure to current date.
                mDate = new Date();
                mDateStr = DateFormat.getDateInstance().format(mDate);
                newStatus.setPostedDate(mDate);
                //Get user email from base_activity (when we log in)
                newStatus.setEmail(base_Activity.username);
                newStatus.setContent(met_status.getText().toString());

                if(mPhotoFile == null)
                    newStatus.setPhotoPath("");
                else
                    newStatus.setPhotoPath(mPhotoFile.toString());

                //Insert the Feed into database
                mDatabase.insertFeedItem(newStatus);

                //RESET photo
                mPhotoTaken = false;
                mPhotoFile = null;
                updatePhoto();

                //RESET editText
                met_status.setText("");
                updateUI();
            }
        });

        Log.i(tag, "Implement recyclerview");
        mRecyclerView = (RecyclerView)view.findViewById(R.id.feedlist_recyclerview_container);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        updateUI();

        return view;
    }

    public void takePhoto()
    {
        //Start intent via ACTION_IAMGE_CAPTURE
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        String filename = "IMG_" + UUID.randomUUID().toString() + ".jpg";
        File pictureDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        mPhotoFile = new File(pictureDir, filename);

        Uri photoUri = Uri.fromFile(mPhotoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

        //Use getActivity's startActivity to start this activity.
        //our parent activity will call a method in this class to forward the message back to this class.
        getActivity().startActivityForResult(intent, base_Activity.RQ_CAMERA);
    }

    public void updatePhoto()
    {
        Log.i(tag, "update photo");
        if(mPhotoTaken == true && mPhotoFile!= null)
        {
            Bitmap bitmap = BitmapFactory.decodeFile(mPhotoFile.toString());
            miv_feedphoto.setImageBitmap(bitmap);
        }
        else
        {
            mPhotoFile = null;
            miv_feedphoto.setImageDrawable(null);
        }
    }

    //Called by base_Activity. When this is called, it will update photo.
    public void photoTaken(boolean taken)
    {
        Log.i(tag, "phototaken -- detected: " + taken);
        mPhotoTaken = taken;
        updatePhoto();
    }

    //To update view - to show most updated version of the records in our SQLite Database.
    public void updateUI()
    {
        Log.i(tag, "updateUI()");
        List<Feed> feedList = mDatabase.getFeedList();
        List<String> favList = mDatabase.getFavList(base_Activity.username);

        List<Feed> newFeedList = new LinkedList<>();

        for(int i=0; i<feedList.size();i++)
        {
            for(int j=0; j<favList.size();j++)
            {
                if(favList.get(j).equals(feedList.get(i).getEmail()))
                {
                    newFeedList.add(feedList.get(i));
                    break;
                }
            }
        }
        feedList = newFeedList;

        Collections.sort(feedList, new Comparator<Feed>() {
            public int compare(Feed a, Feed b) {
                if (a.getPostedDate() == null || b.getPostedDate() == null) {
                    return 0;
                }
                // * (-1) so that it is not reverse chronological order.
                return (-1) * a.getPostedDate().compareTo(b.getPostedDate());
            }
        });

        if(mAdapter == null)
        {
            Log.i(tag, "no adapter, implementing it");
            mAdapter = new FeedAdapter(feedList);
            mRecyclerView.setAdapter(mAdapter);
            Log.i(tag, "feedlistsize: " + feedList.size());;
        }
        else
        {
            mAdapter.setFeeds(feedList);
            Log.i(tag, "feedlistsize: " + feedList.size());
        }
        Log.i(tag, "updateUI():: OK");
    }


    //GAME ADAPTER
    private class FeedAdapter extends RecyclerView.Adapter<FeedViewHolder>
    {
        private List<Feed> mFeedList;

        public FeedAdapter(List<Feed> newFeedList)
        {
            mFeedList = newFeedList;
        }

        @Override
        public FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View itemView = inflater.inflate(R.layout.feed_view, parent, false);
            return new FeedViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(FeedViewHolder holder, int position) {
            Feed feed = mFeedList.get(position);
            holder.bind(feed);
        }

        @Override
        public int getItemCount() {
            return mFeedList.size();
        }

        public void setFeeds(List<Feed> feedList)
        {
            mFeedList = feedList;
            notifyDataSetChanged();
        }
    }

    //GAME VIEW HOLDER
    public class FeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView mtv_email;
        private TextView mtv_date;
        private TextView mtv_content;
        private ImageView miv_profilepic;
        private ImageView miv_photo;

        private Feed mFeed;

        public FeedViewHolder(View itemView)
        {
            super(itemView);
            mtv_email = (TextView)itemView.findViewById(R.id.tv_feedview_email);
            mtv_date = (TextView)itemView.findViewById(R.id.tv_feedview_uploaddate);
            mtv_content = (TextView) itemView.findViewById(R.id.tv_feedview_content);
            miv_photo = (ImageView)itemView.findViewById(R.id.iv_feedview_picture);
            miv_profilepic = (ImageView)itemView.findViewById(R.id.iv_feedview_profilepic);
            itemView.setOnClickListener(this);
        }

        public void bind(Feed feed)
        {
            mFeed = feed;
            mtv_email.setText(feed.getEmail());
            mtv_email.setTextColor(Color.BLUE);
            mtv_email.setAllCaps(true);
            mtv_date.setText(feed.getPostedDate().toString());
            mtv_date.setTextColor(Color.BLUE);

            mtv_content.setText(feed.getContent());
            mtv_content.setTextColor(Color.BLACK);

            Bitmap photo = BitmapFactory.decodeFile(feed.getPhotoPath());
            miv_photo.setImageBitmap(photo);

            String profilepicpath = mDatabase.getUserPhoto(feed.getEmail());
            if(profilepicpath == null || profilepicpath.equals(""))
                miv_profilepic.setImageResource(R.mipmap.ic_launcher);
            else
            {
                Bitmap profilepic = BitmapFactory.decodeFile(profilepicpath);
                miv_profilepic.setImageBitmap(profilepic);
            }
        }

        @Override
        public void onClick(View v)
        {
            //Toast.makeText(getContext(), "CLICKED", Toast.LENGTH_SHORT).show();
            //Log.i(tag, "clicked.");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        if(mPhotoTaken)
            outState.putString(KEY_PHOTO, mPhotoFile.toString());
        else
            outState.putString(KEY_PHOTO, "");
        super.onSaveInstanceState(outState);
    }
}
