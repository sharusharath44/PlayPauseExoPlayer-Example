package com.example.playpauseexoplayer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.RequestManager;
import java.util.ArrayList;
import java.util.List;

public class VideoRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<VideoModel> videoList;
    RequestManager requestManager;
    private Context context;
    public static int itemID;


    public VideoRecyclerAdapter(List<VideoModel> videoList, RequestManager requestManager, Context context) {
        this.videoList = videoList;
        this.requestManager = requestManager;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View iView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_list_row, parent, false);

        return new PlayerViewHolder(iView, context);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {


        final VideoModel mVideo = videoList.get(position);
        itemID=position;

        AnimationUtil.animate(holder, true);

        ((PlayerViewHolder) holder).onBind(videoList.get(position), requestManager);


//        this.requestManager
//                .load(mVideo.getVideoThumb())
//                .into(holder.imageViewThumbnail);

        //    Glide.with(holder.itemView.getContext()).load(mVideo.getVideoThumb()).into(holder.imageViewThumbnail);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mPlayerIntent = PlayerActivity.getStartIntent(holder.itemView.getContext(), mVideo.getFilePath());
                holder.itemView.getContext().startActivity(mPlayerIntent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public void setVideoModel(ArrayList<VideoModel> videoModels) {
        this.videoList = videoModels;
    }

}