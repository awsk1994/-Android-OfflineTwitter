# Offline Twitter Android App
Name: Alex Wong

## Brief Overview
In this assignment, we have to create an offline twitter. User should be able to:
- create their account

- view feeds

- post feeds as well as viewing a list of users which they can favorite (to follow) or unfavourite

- log out and then log back in with either same or different accoun

- use camera capabilities for user to take photo for their profile picture and/or their feed posts

SQLiteDatabase is used as database.

I affirm that I did not give or receive any unauthorized help on this project, and that all work will is my own.

## Detail Walkthrough: 

**Orientation Changes:**
In most cases (mainly any cases with fragments), the orientation change problem is more of the parent activity being able to retrieve the original fragment. This can be done by simply finding the fragment via getSupportFragment, and then referencing this fragment. Dealing with orientation changes in activity itself is simple - we simply need to set it into a bundle in onSaveInstance and then retrieve it in onCreate.

**createUserFragment:**
User has to input an email (as username) and a password. If these are not inserted, then the program will display an error message, and the account will not be created (yet). The other fields are open for users to enter it if they with to. To make things more organized, I set a default text format for each fields, so even if the user did not input a certain (or several) parameter(s), it will generate some generic messages. (e.g.: biography is “I am <name>.”, if there is no  inputs for biography). User also has the option to take photo for their profile picture in this page. Lastly, I added a clear-all, a submit and a go-back button. clear-all button clears all fields in the view. submit button submits the information in the textfield to send to the database. Lastly, go-back button simply directs you back to the mainActivity.
Every part of the app supports orientation changes. Mainly, it is the create-user-fragment and the news-feed-fragment that needs more work to apply this feature. One of the problem I faced was photo. Since we are accessing the camera activity from a fragment, it was necessary to use the parent activity’s startActivityForResult() method to start the camera activity. However, when there is a orientation change, it was necessary that the parent activity detects whether the fragment still exists or not. If it exists (meaning activity destroyed and created, but fragment is alive), then it will reply update the fragment to update its photo. In addition, although I assigned setRetainInstance
() to true, it didn’t restore the photo picture’s file path, and thus, I used a bundle in onSaveInstantState to retain the profile picture directory path.
After the user profile creation is completed (and submitted), it will return to mainActivity, and immediately log in straight away, instead of making the user re-type the email and password since this is redundant.

**MainActivity:**
Without logging in , the user will only be able to access the login page. To log in, one would have to enter a correct email/username and password, otherwise, it will output an error (toast) message to notify user that their provided information is incorrect. This is done by searching the database with the given username and password. If the database returns a null, then we know that this record does not exist, and thus, the username must have either typed in a wrong password or a wrong username. This application will actually notify the user of whether it is wrong password, or a wrong username by doing a second searching in database to see if the given username exists. Furthermore, users also have the option to create a new user account.

**base_Activity:**
Once logged in, the user can navigate between list of (normal and/or favorited) users and news feed. News feed is the first fragment to be loaded to screen by default when one logs in.
Once logged in, user also has the option to log out and return to the login page by clicking ‘log out’ in the menu tool bar options.
User also have an option to create a new user profile. 

**all_userlist_fragment**
This is a list of users that signed up to the app. There are two options here: see the list of ALL users, or see the list of only your favorite users. There is a button on the bottom on the screen that will toggle the filter for favorited users. On the right side of every user_view (xml file), you will also see a heart symbol. This basically tells the user whether they have favorited that user or not. You can always favorite or un-favorite simply by clicking/tapping the heart icon. To view the user’s profile, simply click/tap on their name or profile picture, and a profile dialog will pop up to display that user’s message. The reason for storing all the user information in a dialogFragment, rather than on the list is there may be too much information to display on a list. For example, if we put a biography in the list of users, then when there is a huge biography and will take up a lot of space on the view. This is not what we want.

**feedlist_fragment:**
Using recycler view, adapter, and view holder, we simply retrieve a list of posts messages and a list of favorite people. Since user will only be able to view the post of those he favorites, the program filters only to get posts of those who the users had favorited. Furthermore, users will be able to post feeds. On screen, users have the option to take  a photo and/or type some messages and/or post it into the feed if they with to as well. The feed will display the user, and post the feed post’s email, name and profile picture. Lastly, the list is to input to the adapter is also sorted based on date to allow the feed to be presented in reverse chronological order of date. 

**User:**
A class that defines user, has getter and setter methods.

**Feed:**
A class that defines feed, has getter and setter methods.

**DateDialog and DialogFragment:**
Both extends DialogFragment. When they are used, they pop up in the screen, but they will not take up an entire space.
DateDialog Fragment is used for picking dates from a  calendar (e.g.: birthday). DialogFragment is used to display profile user information. 

**Database:**
Database.java contains many SQLite query functions, and CursorWrapper. This is essentially where most information is transferred to and/or transfer back to the user. 

**OpenHelper:**
OpenHelper is simply there to create tables if it does not exist, and update table if the table numbers don’t match up.

**Scheme:**
Simply set the columns and assign a name to the column. 

**MVC:**
In this project, I used the mvc design pattern to design my twitter app.

**Model:**
This is mostly the database (backend) of the application. The database is very important as this is where the feed, user profile information and favorite list is located. Whenever a user profile is created, the application will insert a user record into the database. When one accesses the list of users, the application will fetch the list of user from the database via cursorWrapper. To view favorite users only, it will retrieve both favorite list and users list and filter out the non-favorite users. The same concept applies to how the application filters out feeds so one only sees the feed of the ones the user favorited. 

Moreover, the Feed and User class is also key to the operation of the database, as without these two classes to define what Feed and User has, it would be much more difficult to implement the database.

The database is also used to check whether certain things the user has inputted exists. For example, checking whether the user has entered the correct password and username, checking whether the email one has typed for user profile creation has already been used before and so forth. 

**Controller:**
This is the medium between model and view. This is mostly the buttons, toolbar options and onClick-related objects.

**View:**
This is what is displayed on the screen. This is mostly the viewHolders, and all the button/text/.etc that you see on the screen. Although it seem very simple here, the application has to make sure it updates the view (via controller, by fetching information from the model) at the correct moment. For example, whenever we submit a new feed post. We want to see the feed post immediately. Thus, we need to update the view with the newest list of feed in order to inflate it to view. 

**OTHERS**
 - In Feed Views, I used two different feed views; portrait and landscape. This is to utilize the difference in space (view space, not memory space) better.
I  used two different kinds of views for the feed_view (xml file). By default, it is portrait, the image and the content (of post) are on top of each other. But when it is landscape (rotated), then the image will appear on the right of the content since the width is more than we need. 

 - In user profile creation, I used regex pattern check to make sure the email format and password format is correct.
I implemented a regex-pattern check to make sure that the email format and password format works. For email pattern matching, I implemented a very simple regex (not the full length email pattern check). For password pattern matching, the user will have to enter at least one UPPER and one LOWER case for more secure passwords.

 - In user profile creation, I used scrollview in user profile creation page (so that the field won’t be squished up and small just because there are to enough view on the display screen.)
Lastly, when you rotate the screen to landscape, there are not enough space to fit all the fields. Thus, I implemented a scrollview so that you can scroll through the fields even though there are not enough space on the screen.

