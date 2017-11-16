package myPackage.models;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Forum {
    private long posts;
    private final String slug;
    private long threads;
    private long id;
    private final String title;
    private String user;

    @JsonCreator
    public Forum(
            @JsonProperty("id") long id,
            @JsonProperty("slug") String slug,
            @JsonProperty("title") String title,
            @JsonProperty("user") String user,
            @JsonProperty("postCount") long postCount,
            @JsonProperty("threadCount") long threadCount
    ) {
        this.id = id;
        this.slug = slug;
        this.posts = postCount;
        this.threads = threadCount;
        this.title = title;
        this.user = user;
    }


    public long getPosts() {
        return posts;
    }

    public void setPosts(long posts) {
        this.posts = posts;
    }

    public String getSlug() {
        return slug;
    }

    public long getThreads() {
        return threads;
    }

    public void setThreads(long threads) {
        this.threads = threads;
    }

    public String getTitle() {
        return title;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}