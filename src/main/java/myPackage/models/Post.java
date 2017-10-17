package myPackage.models;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

public class Post {
    public Object[] getPath() {
        return path;
    }

    public void setPath(Object[] path) {
        this.path = path;
    }

    private long id;
    private long parent;
    private String author;
    private String message;
    private boolean isEdited;
    private String forum;
    private String created;
    private long thread;
    private Object[] path;


    @JsonCreator
    public Post(
            @JsonProperty("id") long id,
            @JsonProperty("parent") long parent,
            @JsonProperty("thread") long thread,
            @JsonProperty("isedited") boolean isedited,
            @JsonProperty("author") String author,
            @JsonProperty("message") String message,
            @JsonProperty("forum") String forum,
            @JsonProperty("created") Timestamp created,
            @JsonProperty("path") Object[] path
    ) {
        this.id = id;
        this.parent = parent;
        this.forum = forum;
        if (created == null) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            this.created = timestamp.toInstant().toString();
        } else {
            this.created = created.toInstant().toString();

        }
        this.message = message;
        this.isEdited = isedited;
        this.thread = thread;
        this.author = author;
        this.path = path;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getParent() {
        return parent;
    }

    public void setParent(long parent) {
        this.parent = parent;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("isEdited")
    public boolean getIsEdited() {
        return isEdited;
    }

    public void setEdited(boolean edited) {
        isEdited = edited;
    }

    public String getForum() {
        return forum;
    }

    public void setForum(String forum) {
        this.forum = forum;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public long getThread() {
        return thread;
    }

    public void setThread(long thread) {
        this.thread = thread;
    }


}