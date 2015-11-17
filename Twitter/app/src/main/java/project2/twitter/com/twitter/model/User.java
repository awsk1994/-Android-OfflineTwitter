package project2.twitter.com.twitter.model;

import java.util.UUID;

/**
 * Created by alexwong on 11/12/15.
 */
public class User
{
    private String mEmail;
    private String mPassword;
    private String mFullname;
    private String mBirthdate;
    private String mProfilepic;
    private String mBio;
    private String mHometown;
    private UUID mId;

    public User(UUID id)
    {
        mId = id;
    }
    public UUID getID()
    {
        return mId;
    }

    public String getEmail()
    {
        return mEmail;
    }

    public String getPassword()
    {
        return mPassword;
    }

    public String getFullname()
    {
        return mFullname;
    }
    public String getBirthdate()
    {
        return mBirthdate;
    }
    public String getProfilepic()
    {
        return mProfilepic;
    }
    public String getBio()
    {
        return mBio;
    }
    public String getHomeTown()
    {
        return mHometown;
    }

    public void setEmail(String email)
    {
        mEmail = email;
    }

    public void setPassword(String pw)
    {
        mPassword = pw;
    }

    public void setFullname(String name)
    {
        mFullname = name;
    }

    public void setBirthdate(String bday)
    {
        mBirthdate = bday;
    }

    public void setProfilepic(String pic)
    {
        mProfilepic = pic;
    }

    public void setBio(String bio)
    {
        mBio = bio;
    }

    public void setHometown(String hometown)
    {
        mHometown = hometown;
    }

}
