package com.example.playpauseexoplayer;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.RequestManager;
import java.io.File;
import static android.app.NotificationManager.IMPORTANCE_HIGH;
import static android.content.Context.DOWNLOAD_SERVICE;


public class PlayerViewHolder extends RecyclerView.ViewHolder {


    public ImageView mediaCoverImage;
    public RequestManager requestManager;
    private TextView title;
    private View parent;
    private Button btn_download;
    private Context context;
    public static long downloadID;
    public static NotificationManager mNotificationManager;


    public PlayerViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        parent = itemView;
        this.context = context;
        mediaCoverImage = itemView.findViewById(R.id.imageViewThumbnail);
        title = itemView.findViewById(R.id.Title);
        btn_download = itemView.findViewById(R.id.btn_download);

    }

    void onBind(final VideoModel videoModel, RequestManager requestManager) {
        this.requestManager = requestManager;
        parent.setTag(this);

        this.requestManager
                .load(videoModel.getVideoThumb())
                .into(mediaCoverImage);
        title.setText(videoModel.getTitle());


        String name = videoModel.getUserHandle() + ".mp4";
        File file = new File(context.getExternalFilesDir(null), name);
        if (file.exists())
            btnBackgroundGreen();
        else
            btnBackgroundWhite();


        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                beginDownload(videoModel.getFilePath(), videoModel.getUserHandle(), videoModel.getTitle());


            }
        });
    }


    private void beginDownload(String vidurl, String userHandle, String title) {

        String name = userHandle + ".mp4";
        File file = new File(context.getExternalFilesDir(null), name);

        if (file.exists()) {
            Toast.makeText(context, "File already downloaded", Toast.LENGTH_SHORT).show();
            btnBackgroundGreen();

        } else {
        /*
        Create a DownloadManager.Request with all the information necessary to start the download
         */

            btnBackgroundWhite();

            createNotification("Downloading", "Please wait downloading", 0);

            DownloadManager.Request request = null;// Set if download is allowed on roaming network
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                request = new DownloadManager.Request(Uri.parse(vidurl))
                        .setTitle(title)// Title of the Download Notification
                        .setDescription("Downloading...")// Description of the Download Notification
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)// Visibility of the download Notification
                        .setDestinationUri(Uri.fromFile(file))// Uri of the destination file
                        .setVisibleInDownloadsUi(true)
                        .setRequiresCharging(false)// Set if charging is required to begin the download
                        .setAllowedOverMetered(true)// Set if download is allowed on Mobile network
                        .setAllowedOverRoaming(true);
            }
            final DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
            downloadID = downloadManager.enqueue(request);// enqueue puts the download request in the queue.


        }
    }


    private void btnBackgroundGreen() {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        p.weight = (float) 0.3;

        btn_download.setLayoutParams(p);
        btn_download.setBackgroundResource(R.drawable.downloaded);
    }

    private void btnBackgroundWhite() {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        p.weight = (float) 0.3;

        btn_download.setLayoutParams(p);
        btn_download.setBackgroundResource(R.drawable.ic_arrow_downward_black_24dp);
    }


    private void createNotification(String contentTitle, String contentText, int cancel) {

        int NOTIFICATION_ID = 1;
        String channel_Id = "downloadChannel";

        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

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
            builder = new Notification.Builder(context)
                    .setSmallIcon(android.R.drawable.stat_sys_download)
                    .setContentTitle(contentTitle)
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .setContentText(contentText)
                    .setChannelId(channel_Id);
        } else {

            //Build the notification using Notification.Builder
            builder = new Notification.Builder(context)
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
        }

    }


}





