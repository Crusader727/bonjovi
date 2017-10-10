package myPackage.models;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Forum {
    private final long postCount;
    private final String slug;
    private final long threadCount;
    private final String title;
    private final String owner;


    public Forum() {
        this.slug = "a";
        this.postCount = 0;
        this.threadCount = 0;
        this.title = "a";
        this.owner = "a";
    }
    public Forum(String slug, String title, String owner, long pcount, long tcount) {
        this.slug = slug;
        this.postCount = pcount;
        this.threadCount = tcount;
        this.title = title;
        this.owner = owner;
    }
    @JsonCreator
    public Forum(
            @JsonProperty("slug") String slug,
            @JsonProperty("title") String title,
            @JsonProperty("owner") String owner
    ) {
        this.slug = slug;
        this.postCount = 0;
        this.threadCount = 0;
        this.title = title;
        this.owner = owner;
    }

    public Object getObj() {
        return this;
    }

    public long getPostCount() {
        return postCount;
    }

    public String getSlug() {
        return slug;
    }

    public long getThreadCount() {
        return threadCount;
    }

    public String getTitle() {
        return title;
    }

    public String getOwner() {
        return owner;
    }
}