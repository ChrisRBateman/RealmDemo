RealmDemo
=========

This demo uses the <a href="https://realm.io/docs/java/latest">Realm Database for Android</a> to manage a list of contacts.
Contacts are displayed in a RecyclerView which uses a RealmRecyclerViewAdapter to automatically syncronize
with any storage operations. The app does basic CRUD (create, read, update, and delete) operations on the contact list. 

<img src="screenshots/device-2019-03-07-110237.png" width="160" height="284" title="Screen Shot 1">  <img src="screenshots/device-2019-03-06-143854.png" width="160" height="284" title="Screen Shot 2">

I'm also using a ConstraintLayout to position all of the view elements.
Written in <b>Java</b> using Android Studio 3.3.2 and tested on Android 7.0.