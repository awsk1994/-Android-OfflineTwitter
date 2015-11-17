package project2.twitter.com.twitter.MainClass;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import project2.twitter.com.twitter.Database.Database;
import project2.twitter.com.twitter.R;
import project2.twitter.com.twitter.dialog.Dialog_Fragment;
import project2.twitter.com.twitter.model.User;



public class all_userlist_Fragment extends Fragment
{
    private static final String tag = "alluserlist_fragment:";

    public int mFilterMode;
    // 1 = all user. 2 = favourites only
    //To filter between favourite users ONLY and all users.

    private RecyclerView mRecyclerView;
    Database mDatabase;
    UserAdapter mAdapter;

    List<String> mFavList;
    Button mb_Filter;

    public all_userlist_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_userlist, container, false);
        Log.i(tag, "onCreateView");
        mDatabase = Database.get(getContext());

        if (mFilterMode == 0) {
            mFilterMode = 1;
        }

        mRecyclerView = (RecyclerView) view.findViewById(R.id.alluserlist_recyclerview_container);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //When filter button clicked, filter mode changes
        mb_Filter = (Button) view.findViewById(R.id.alluserlist_button_filter);
        mb_Filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFilterMode == 1)
                    mFilterMode = 2;
                else
                    mFilterMode = 1;

                updateUI();
            }
        });

        updateUI();

        return view;
    }

    //To update view - to show most updated version of the records in our SQLite Database.
    public void updateUI()
    {
        List<User> userList = mDatabase.getUserList();

        //Retrieve Favourite list too
        mFavList = mDatabase.getFavList(base_Activity.username);
        List<User> favList = new LinkedList<>();

        //If favourite users only, then we filter out all the non-favourite users.
        if(mFilterMode == 2)
        {
            mb_Filter.setText(R.string.filter_clickUser);

            for(int i=0; i<mFavList.size();i++)
            {
                for(int j=0; j<userList.size();j++)
                {
                    if(mFavList.get(i).equals(userList.get(j).getEmail()))
                    {
                        favList.add(userList.get(j));
                        break;
                    }
                }
            }
            userList = favList;
        }
        else if(mFilterMode == 1)
        {
            mb_Filter.setText(R.string.filter_favUser);
        }
        else
        {
            Log.i(tag, "ERROR _ filter number ");
            mb_Filter.setText(R.string.filter_error);
        }

        if(mAdapter == null)
        {
            mAdapter = new UserAdapter(userList);
            mRecyclerView.setAdapter(mAdapter);
        }
        else
        {
            mAdapter.setUsers(userList);
        }
        Log.i(tag, "updateUI():: OK");
    }

    //GAME ADAPTER
    private class UserAdapter extends RecyclerView.Adapter<UserViewHolder>
    {
        private List<User> mUserList;

        public UserAdapter(List<User> newUserList)
        {
            mUserList = newUserList;
        }

        @Override
        public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View itemView = inflater.inflate(R.layout.user_view, parent, false);
            return new UserViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(UserViewHolder holder, int position) {
            User user = mUserList.get(position);
            holder.bind(user);
        }



        @Override
        public int getItemCount() {
            return mUserList.size();
        }

        public void setUsers(List<User> userList)
        {
            mUserList = userList;
            notifyDataSetChanged();
        }
    }

    //GAME VIEW HOLDER
    public class UserViewHolder extends RecyclerView.ViewHolder
    {
        TextView mtv_email;
        TextView mtv_fullname;
        ImageView miv_profilepic;
        ImageView miv_fav;

        private User mUser;

        public UserViewHolder(View itemView)
        {
            super(itemView);
            mtv_email = (TextView)itemView.findViewById(R.id.alluserlist_tv_name);
            mtv_fullname = (TextView)itemView.findViewById(R.id.alluserlist_tv_fullname);
            miv_fav = (ImageView)itemView.findViewById(R.id.alluserlist_iv_fav);
            miv_profilepic = (ImageView)itemView.findViewById(R.id.alluserlist_iv_profilepic);
        }

        public void bind(User user)
        {
            mUser = user;
            mtv_email.setText(user.getEmail());
            mtv_fullname.setText(user.getFullname());
            //You can click on user's name to view their profile in a dialog.
            mtv_email.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    boolean isFav = mDatabase.checkFav(base_Activity.username, mUser.getEmail().toString());
                    Dialog_Fragment dialog = Dialog_Fragment.newInstance(mUser.getEmail(), mUser.getFullname(), mUser.getProfilepic(), mUser.getBirthdate(), mUser.getBio(), mUser.getHomeTown());
                    dialog.setTargetFragment(all_userlist_Fragment.this, 0);
                    dialog.show(getFragmentManager(), mUser.getEmail().toString());
                }
            });
            mtv_fullname.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    boolean isFav = mDatabase.checkFav(base_Activity.username, mUser.getEmail().toString());
                    Dialog_Fragment dialog = Dialog_Fragment.newInstance(mUser.getEmail(), mUser.getFullname(), mUser.getProfilepic(), mUser.getBirthdate(), mUser.getBio(), mUser.getHomeTown());
                    dialog.setTargetFragment(all_userlist_Fragment.this, 0);
                    dialog.show(getFragmentManager(), mUser.getEmail().toString());
                }
            });

            String profilepicpath = user.getProfilepic();
            //If there is no profile picture, then by default, we will display the android icon.
            if(profilepicpath == null || profilepicpath.equals(""))
            {
                miv_profilepic.setImageResource(R.mipmap.ic_launcher);
            }
            else
            {
                Bitmap profilepic = BitmapFactory.decodeFile(profilepicpath);
                miv_profilepic.setImageBitmap(profilepic);
            }

            //To open dialog to view profile information.
            miv_profilepic.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Dialog_Fragment dialog = Dialog_Fragment.newInstance(mUser.getEmail(), mUser.getFullname(), mUser.getProfilepic(), mUser.getBirthdate(), mUser.getBio(), mUser.getHomeTown());
                    dialog.setTargetFragment(all_userlist_Fragment.this, 0);
                    dialog.show(getFragmentManager(), mUser.getEmail().toString());
                }
            });

            //To discern whether is favourited user or not.
            //Text color and icon will be different for favourited users.
            if(mFavList.contains(user.getEmail()))
            {
                mtv_email.setTextColor(Color.BLUE);
                miv_fav.setImageResource(R.mipmap.fav_1);
            }
            else
            {
                mtv_email.setTextColor(Color.BLACK);
                miv_fav.setImageResource(R.mipmap.no_fav1);
            }

            miv_fav.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    boolean isFav = mDatabase.checkFav(base_Activity.username, mUser.getEmail().toString());

                    if(!isFav)
                        mDatabase.insertFavorite(base_Activity.username, mUser.getEmail().toString());
                    else
                        mDatabase.unFav(base_Activity.username, mUser.getEmail().toString());

                    updateUI();
                }
            });
        }
    }



}
