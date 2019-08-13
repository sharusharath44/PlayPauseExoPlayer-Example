package com.example.playpauseexoplayer;

public class VideoModel {
    private boolean selected;
    private int Id;
    private String userHandle;
    private String Title;
    private String FilePath;
    private String VideoThumb;

    public VideoModel(boolean selected, int id, String userHandle, String title, String filePath, String videoThumb) {
        this.selected = selected;
        Id = id;
        this.userHandle = userHandle;
        Title = title;
        FilePath = filePath;
        VideoThumb = videoThumb;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getUserHandle() {
        return userHandle;
    }

    public void setUserHandle(String userHandle) {
        this.userHandle = userHandle;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getFilePath() {
        return FilePath;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
    }

    public String getVideoThumb() {
        return VideoThumb;
    }

    public void setVideoThumb(String videoThumb) {
        VideoThumb = videoThumb;
    }

}