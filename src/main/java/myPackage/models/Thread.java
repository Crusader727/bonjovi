package myPackage.models;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Thread {
    private long votes;
    private long id;
    private final String slug;
    private final String owner;
    private final String forum;
    private final String created;
    private final String message;
    private final String title;



    @JsonCreator
    public Thread(
            @JsonProperty("slug") String slug,
            @JsonProperty("forum") String forum,
            @JsonProperty("title") String title,
            @JsonProperty("message") String message,
            @JsonProperty("owner") String owner
    ) {
        this.id = 0;
        this.slug = slug;
        this.forum = forum;
        this.created = "";
        this.message = message;
        this.votes = 0;
        this.title = title;
        this.owner = owner;
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

    public String getOwner() {
        return owner;
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
}