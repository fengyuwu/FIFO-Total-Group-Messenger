//Intensive testing passed 10/10 Every time it runs

//UI code disabled for performance
//Final Version
//UI/UN-need Exception handing codes can be comment out/delete to improve performance.

//ISIS algorithm code  mainly based on https://studylib.net/doc/7830646/isis-algorithm-for-total-ordering-of-messages
//ISIS algorithm code  mainly based on http://www.gecg.in/papers/ds5thedn.pdf
//ISIS algorithm code  mainly based on Lecture slides
//Exception handle code, some exception is just warning from write fail and read is more important,as i need to clean the content provider and queue.
//Also used to detect failed avd / process so that it will remove bad message.

//Socket message passing code  mainly based on old PA1,PA2A and https://docs.oracle.com/javase/tutorial/networking/sockets/index.html
//https://developer.android.com/reference/java/net/Socket.html
//https://developer.android.com/reference/java/net/ServerSocket.html
//Sockets mainly based on https://docs.oracle.com/javase/tutorial/networking/sockets/examples/EchoClient.java
//Server Sockets mainly based on https://docs.oracle.com/javase/tutorial/networking/sockets/examples/EchoServer.java

//Socket message passing code mainly base on https://docs.oracle.com/javase/7/docs/api/java/io/ObjectOutputStream.html
//https://docs.oracle.com/javase/7/docs/api/java/io/ObjectInputStream.html

//Debug using Toast, Toast code mainly based on https://developer.android.com/guide/topics/ui/notifiers/toasts.html
//URI,content provider code mainly base on PA1,PA2A.

package edu.buffalo.cse.cse486586.groupmessenger2;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentUris;

/**
 *
 * Created by fengyuwu on 2/8/18.
 *  * Welcome to Litte Feng chat room
 * Version 1
 * source: https://developer.android.com/training/data-storage/sqlite.html#java
 *         https://developer.android.com/guide/topics/data/data-storage.html#db
 *         https://developer.android.com/guide/topics/providers/content-provider-basics.html
 *         https://developer.android.com/guide/topics/providers/content-provider-creating.html#Insert
 *         https://developer.android.com/reference/android/content/ContentUris.html#withAppendedId(android.net.Uri, long)
 *         https://developer.android.com/reference/android/database/sqlite/SQLiteDatabase.html#insertWithOnConflict(java.lang.String, java.lang.String, android.content.ContentValues, int)
 *         https://developer.android.com/reference/android/database/sqlite/SQLiteOpenHelper.html
 *         https://developer.android.com/reference/android/util///Log.html
 *         https://developer.android.com/guide/topics/data/data-storage.html#db
 *         https://developer.android.com/training/data-storage/sqlite.html#java
 *         https://developer.android.com/reference/android/database/sqlite/SQLiteDatabase.html
 *         https://developer.android.com/reference/android/database/sqlite/SQLiteDatabase.html#CONFLICT_REPLACE
 *         https://developer.android.com/reference/android/database/sqlite/SQLiteOpenHelper.html#onCreate(android.database.sqlite.SQLiteDatabase)
 *         https://developer.android.com/studio/debug/am-logcat.html#format
 *         https://developer.android.com/reference/android/content/ContentProvider.html#query(android.net.Uri,%20java.lang.String[],%20java.lang.String,%20java.lang.String[],%20java.lang.String)
 *         https://developer.android.com/reference/android/database/sqlite/SQLiteDatabase.html#query(boolean, java.lang.String, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String, java.lang.String, java.lang.String, java.lang.String)
 *         https://developer.android.com/reference/android/content/ContentResolver.html
 *         https://developer.android.com/reference/android/database/MatrixCursor.html
 *         https://developer.android.com/reference/android/database/Cursor.html
 *         https://developer.android.com/reference/android/content/ContentProvider.html#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
 *
 * @author fengyuwu
 *
 */

public class GroupMessengerProvider extends ContentProvider {



    private MysqlHelper mOpenHelper;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        //Since I am inserting, so I need write access.

        long row_ID = db.insertWithOnConflict("my_table", null, values, 5);//android dev website
        //This method is hard to debug... Thanks to log cat, It needs the Table name instead of
        //Database name!!!

        if (row_ID == -1){
            //Log.i("my Provider?", "insert failed! " + " -F.W");
            //Log.wtf("my Provider?", "The should not happen, Insert method is not good!" + "F.W.");
            System.exit(-1);
        }
        else{
            //Log.i("my Provider?", "insert success!!! " + " -F.W");
            return ContentUris.withAppendedId(uri, row_ID);

        }

        //Log.wtf("my Provider?", "The should not happen, Insert method is not good!" + "F.W.");
        db.close();
        return null; //it will never get here.
    }

    @Override
    public boolean onCreate() {
        //Log.i("my Provider?", "In onCreate: helper called" + "F.W.");

        mOpenHelper = new MysqlHelper(getContext());//gather all information that is necessary to form the database.



        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        //Log.i("my Provider?", "Executing mySQL statement..." + "F.W.");
        //Log.i("check provider query ", "value of query selection para:  "+selection);
        //MYSQL statement
        return mOpenHelper.getReadableDatabase().query(true, "my_table", null, "key = ?", new String[] {selection}, null, null, null, "1");
        //Since I am querying I only need read access.



    }
    //====================================================The code below is useless====================================================//
    //I don't need to implement the following method for this PA.
    //I can't remove them because it is required for the content provider to work.

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        //Log.wtf("my Provider?", "The should not happen, please debug at update method" + "F.W.");
//        System.out.print("WTF error");
//        System.exit(-1);
        return -9999;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mOpenHelper.getWritableDatabase();


                // Delete all rows that match the selection and selection args
                return database.delete("my_table", selection,selectionArgs);

        }


//
//        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
//        final int match = sUriMatcher.match(uri);
//        int numDeleted;
//        switch(match){
//            case FLAVOR:
//                numDeleted = db.delete(
//                        FlavorsContract.FlavorEntry.TABLE_FLAVORS, selection, selectionArgs);
//                // reset _ID
//                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
//                        FlavorsContract.FlavorEntry.TABLE_FLAVORS + "'");
//                break;
//            case FLAVOR_WITH_ID:
//                numDeleted = db.delete(FlavorsContract.FlavorEntry.TABLE_FLAVORS,
//                        FlavorsContract.FlavorEntry._ID + " = ?",
//                        new String[]{String.valueOf(ContentUris.parseId(uri))});
//                // reset _ID
//                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
//                        FlavorsContract.FlavorEntry.TABLE_FLAVORS + "'");
//
//                break;
//            default:
//                throw new UnsupportedOperationException("Unknown uri: " + uri);
//        }
//
//        return numDeleted;
////        int uriType = sURIMatcher.match(uri);
//        SQLiteDatabase sqlDB = database.getWritableDatabase();
//        int rowsDeleted = 0;
//        switch (uriType) {
//            case TODOS:
//                rowsDeleted = sqlDB.delete(TodoTable.TABLE_TODO, selection,
//                        selectionArgs);
//                break;
//            case TODO_ID:
//                String id = uri.getLastPathSegment();
//                if (TextUtils.isEmpty(selection)) {
//                    rowsDeleted = sqlDB.delete(
//                            TodoTable.TABLE_TODO,
//                            TodoTable.COLUMN_ID + "=" + id,
//                            null);
//                } else {
//                    rowsDeleted = sqlDB.delete(
//                            TodoTable.TABLE_TODO,
//                            TodoTable.COLUMN_ID + "=" + id
//                                    + " and " + selection,
//                            selectionArgs);
//                }
//                break;
//            default:
//                throw new IllegalArgumentException("Unknown URI: " + uri);
//        }
//        getContext().getContentResolver().notifyChange(uri, null);
//        return rowsDeleted;
//    }


    @Override
    public String getType(Uri uri) {
        //Log.wtf("my Provider?", "The should not happen, please debug at getType method" + "F.W.");
//        System.out.print("WTF error");
//        System.exit(-1);
        return "WTF";
    }
}

//stable feb 16
