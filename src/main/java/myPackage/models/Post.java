package myPackage.models;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.tools.corba.se.idl.constExpr.Times;

import java.sql.Timestamp;

public class Post {
    private long id;
    private long parent;
    private String author;
    private final String message;
    private boolean isedited;
    private String forum;
    private String created;
    private long thread;


    @JsonCreator
    public Post(
            @JsonProperty("id") long id,
            @JsonProperty("parent") long parent,
            @JsonProperty("thread") long thread,
            @JsonProperty("isedited") boolean isedited,
            @JsonProperty("author") String author,
            @JsonProperty("message") String message,
            @JsonProperty("forum") String forum,
            @JsonProperty("created") Timestamp created
    ) {
        this.id = id;
        this.parent = parent;
        this.forum = forum;
        if (created == null) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            this.created = timestamp.toInstant().toString() ;
        } else {
            this.created = created.toInstant().toString();

        }
        this.message = message;
        this.isedited = isedited;
        this.thread = thread;
        this.author = author;
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

    public boolean isIsedited() {
        return isedited;
    }

    public void setIsedited(boolean isedited) {
        this.isedited = isedited;
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

    public long getThread() {
        return thread;
    }

    public void setThread(long thread) {
        this.thread = thread;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}