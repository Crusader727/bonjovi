package myPackage.models;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Forum {
    private  long postCount;
    private final String slug;
    private  long threadCount;
    private final String title;
    private String user;

    @JsonCreator
    public Forum(
            @JsonProperty("slug") String slug,
            @JsonProperty("title") String title,
            @JsonProperty("user") String user,
            @JsonProperty("postCount") long postCount,
            @JsonProperty("threadCount") long threadCount
    ) {
        this.slug = slug;
        this.postCount = postCount;
        this.threadCount = threadCount;
        this.title = title;
        this.user = user;
    }


    public long getPostCount() {
        return postCount;
    }

    public void setPostCount(long postCount) {
        this.postCount = postCount;
    }

    public String getSlug() {
        return slug;
    }

    public long getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(long threadCount) {
        this.threadCount = threadCount;
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
}