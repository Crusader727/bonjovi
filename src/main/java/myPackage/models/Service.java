package myPackage.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Service {

    private long user;
    private long post;
    private long forum;
    private long thread;

    @JsonCreator
    public Service(
            @JsonProperty("slug") long user,
            @JsonProperty("title") long post,
            @JsonProperty("user") long forum,
            @JsonProperty("postCount") long thread
    ) {
        this.post = post;
        this.forum = forum;
        this.thread = thread;
        this.user = user;
    }


    public long getUser() {
        return user;
    }

    public void setUser(long user) {
        this.user = user;
    }

    public long getPost() {
        return post;
    }

    public void setPost(long post) {
        this.post = post;
    }

    public long getForum() {
        return forum;
    }

    public void setForum(long forum) {
        this.forum = forum;
    }

    public long getThread() {
        return thread;
    }

    public void setThread(long thread) {
        this.thread = thread;
    }
}
