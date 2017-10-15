package myPackage.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Details {
    private Thread thread;
    private User author;
    private Forum forum;
    private Post post;

    @JsonCreator
    public Details(
            @JsonProperty("thread") Thread td,
            @JsonProperty("author") User us,
            @JsonProperty("post") Post pst,
            @JsonProperty("forum") Forum fr

    ) {
        this.author = us;
        this.thread = td;
        this.post = pst;
        this.forum = fr;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Forum getForum() {
        return forum;
    }

    public void setForum(Forum forum) {
        this.forum = forum;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
