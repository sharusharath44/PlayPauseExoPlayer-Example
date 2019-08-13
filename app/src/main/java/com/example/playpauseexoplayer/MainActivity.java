package com.example.playpauseexoplayer;

import android.Manifest;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.Arrays;

import static android.app.NotificationManager.IMPORTANCE_HIGH;

public class MainActivity extends AppCompatActivity {


    private static final int REQUEST_PERMISSIONS = 101;
    ArrayList<VideoModel> mVideoList;
    VideoRecyclerAdapter mAdapter;
    RecyclerView mRecyclerView;
    GridLayoutManager recyclerViewLayoutManager;
    Resources resources;
    VideoModel videoModel;
    ArrayList<VideoModel> videoModels;
    public static NotificationManager mNotificationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resources = new Resources();
        mRecyclerView = findViewById(R.id.recyclerViewGallery);


        registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        recyclerViewLayoutManager = new GridLayoutManager(MainActivity.this, 1, GridLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(recyclerViewLayoutManager);
        mVideoList = new ArrayList<>();
        videoModels = new ArrayList<VideoModel>(Arrays.asList(Resources.VIDEO_MODELS));

        checkPermission();

    }

    private void checkPermission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE))) {

            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_PERMISSIONS);
                }
            }
        } else {
            // all permission granted
            getAllVideoFromGallery();

        }
    }

    public void getAllVideoFromGallery() {


        // below code to get all videos from sd card
/*        Uri uri;
        Cursor mCursor;
        int COLUMN_INDEX_DATA, COLUMN_INDEX_NAME, COLUMN_ID, COLUMN_THUMB;
        String absolutePathOfFile = null;
        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.Media._ID, MediaStore.Video.Thumbnails.DATA};

        final String orderBy = MediaStore.Video.Media.DATE_TAKEN;
        mCursor = getApplicationContext().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");
        COLUMN_INDEX_DATA = mCursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        COLUMN_INDEX_NAME = mCursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
        COLUMN_ID = mCursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
        COLUMN_THUMB = mCursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);

        while (mCursor.moveToNext()) {
            absolutePathOfFile = mCursor.getString(COLUMN_INDEX_DATA);
            Log.e("Column", absolutePathOfFile);
            Log.e("Folder", mCursor.getString(COLUMN_INDEX_NAME));
            Log.e("column_id", mCursor.getString(COLUMN_ID));
            Log.e("thum", mCursor.getString(COLUMN_THUMB));

            VideoModel mVideo = new VideoModel();
            mVideo.setSelected(false);
            mVideo.setFilePath(absolutePathOfFile);
            mVideo.setVideoThumb(mCursor.getString(COLUMN_THUMB));
            mVideoList.add(mVideo);*/


        mAdapter = new VideoRecyclerAdapter(videoModels, initGlide(), MainActivity.this);
        mAdapter.setVideoModel(videoModels);
        mRecyclerView.setAdapter(mAdapter);
        SnapHelper snapHelper = new LinearSnapHelper();
        if (mRecyclerView.getOnFlingListener() == null)
            snapHelper.attachToRecyclerView(mRecyclerView);

    }

    private RequestManager initGlide() {
        RequestOptions options = new RequestOptions();

        return Glide.with(this)
                .setDefaultRequestOptions(options);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        getAllVideoFromGallery();
                    } else {
                        Toast.makeText(MainActivity.this, "The app was not allowed to read or write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Fetching the download id received with the broadcast
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (PlayerViewHolder.downloadID == id) {

                createNotification("Downloading", "Download successfull", 1);

                Toast.makeText(context, "Download Completed", Toast.LENGTH_SHORT).show();
                mAdapter = new VideoRecyclerAdapter(videoModels, initGlide(), MainActivity.this);
                mAdapter.setVideoModel(videoModels);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.scrollToPosition(VideoRecyclerAdapter.itemID);
                unregisterReceiver(onDownloadComplete);

                Log.e("complete", String.valueOf(onDownloadComplete));

            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onDownloadComplete);
    }


    //Notification
    private void createNotification(String contentTitle, String contentText, int cancel) {

        int NOTIFICATION_ID = 1;
        String channel_Id = "downloadChannel";

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        final String name = "downloadNotification";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (mNotificationManager.getNotificationChannel(name) == null) {
                NotificationChannel channel;
                channel = new NotificationChannel(channel_Id, name, IMPORTANCE_HIGH);
                mNotificationManager.createNotificationChannel(channel);
            }
        }


        Notification.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //Build the notification using Notification.Builder
            builder = new Notification.Builder(MainActivity.this)
                    .setSmallIcon(android.R.drawable.stat_sys_download)
                    .setContentTitle(contentTitle)
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .setContentText(contentText)
                    .setChannelId(channel_Id);
        } else {

            //Build the notification using Notification.Builder
            builder = new Notification.Builder(MainActivity.this)
                    .setSmallIcon(android.R.drawable.stat_sys_download)
                    .setContentTitle(contentTitle)
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .setContentText(contentText);
        }

        if (cancel != 1)
            mNotificationManager.notify(NOTIFICATION_ID, builder.getNotification());
        else {
            mNotificationManager.cancel(1);
//	    	Toast.makeText(Login.this, "New version of application is downloaded", Toast.LENGTH_LONG).show();
        }

    }

}


