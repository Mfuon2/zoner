package com.kuzasystems.zoner;

/**
 * Created by victor on 31-Oct-18.
 */

public class Post {
    int id;
    int User;
    String Image;
    String postText;
    String addedOn;
    int status;

    public Post(int id, int user, String image, String postText, String addedOn, int status) {
        this.id = id;
        User = user;
        Image = image;
        this.postText = postText;
        this.addedOn = addedOn;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser() {
        return User;
    }

    public void setUser(int user) {
        User = user;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public String getAddedOn() {
        return addedOn;
    }

    public void setAddedOn(String addedOn) {
        this.addedOn = addedOn;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
