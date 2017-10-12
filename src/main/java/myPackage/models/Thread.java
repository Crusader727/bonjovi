package myPackage.models;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Thread {
    private long votes;
    private long id;
    private final String slug;
    private final String author;
    private  String forum;
    private final String created;
    private final String message;
    private final String title;


    @JsonCreator
    public Thread(
            @JsonProperty("slug") String slug,
            @JsonProperty("forum") String forum,
            @JsonProperty("title") String title,
            @JsonProperty("message") String message,
            @JsonProperty("author") String author,
            @JsonProperty("id") long id,
            @JsonProperty("votes") long votes,
            @JsonProperty("created") String created
    ) {
        this.id = id;
        this.slug = slug;
        this.forum = forum;
        this.created = created;
        this.message = message;
        this.votes = votes;
        this.title = title;
        this.author = author;
    }


    public long getVotes() {
        return votes;
    }

    public void setVotes(long votes) {
        this.votes = votes;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public String getAuthor() {
        return author;
    }

    public String getForum() {
        return forum;
    }

    public String getCreated() {
        return created;
    }

    public String getMessage() {
        return message;
    }

    public String getTitle() {
        return title;
    }

    public void setForum(String forum) {
        this.forum = forum;
    }
}