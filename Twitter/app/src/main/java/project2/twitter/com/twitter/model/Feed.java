package project2.twitter.com.twitter.model;

import java.util.Comparator;
import java.util.Date;
import java.util.UUID;

/**
 * Created by alexwong on 11/12/15.
 */

public class Feed{
    private UUID mId;
    private String mEmail;
    private Date mPostedDate;
    private String mContent;
    private String mPhotoPath;

    public Feed(UUID id)
    {
        mId = id;
    }

    public UUID getId()
    {
        return mId;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public Date getPostedDate() {
        return mPostedDate;
    }

    public void setPostedDate(Date postedDate) {
        mPostedDate = postedDate;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getPhotoPath() {
        return mPhotoPath;
    }

    public void setPhotoPath(String photoPath) {
        mPhotoPath = photoPath;
    }
}