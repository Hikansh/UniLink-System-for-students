package models;

import java.util.*;

public abstract class Post {
    private String ID;
    private String title;
    private String description;
    private String creator_ID;
    private String status;
    private String imgName;
    private ArrayList<Reply> replies= new ArrayList<Reply>();

    //Constructor
    public Post(String iD, String title, String description, String creator_ID,String status,String imgName) {
        ID = iD;
        this.title = title;
        this.description = description;
        this.creator_ID = creator_ID;
        this.status = status;
        this.imgName = imgName;
    }

    //Getters and setters
    public String getID() {
        return ID;
    }

    public String getCreator_ID() {
        return creator_ID;
    }

    public String getImgName() {
        return imgName;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public abstract String handleReply(Reply reply);

}
