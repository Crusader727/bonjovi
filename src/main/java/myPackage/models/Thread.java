package myPackage.models;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;
import java.time.ZonedDateTime;

public class Thread {
    private long votes;
    private long id;
    private long forumid;
    private final String slug;
    private final String author;
    private String forum;
    private final String created;
    private String message;
    private String title;


    @JsonCreator
    public Thread(
            @JsonProperty("slug") String slug,
            @JsonProperty("forum") String forum,
            @JsonProperty("title") String title,
            @JsonProperty("message") String message,
            @JsonProperty("author") String author,
            @JsonProperty("id") long id,
            @JsonProperty("votes") long votes,
            @JsonProperty("created") Timestamp created,
            @JsonProperty("forumid") long forumid

            ) {
        this.forumid = forumid;
        this.id = id;
        this.slug = slug;
        this.forum = forum;
        if (created == null) {
            this.created = null;//ZonedDateTime.now().toLocalDateTime().toString();
        } else {
            this.created = created.toInstant().toString();

        }
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

    public String getMessage() {
        return message;
    }

    public String getTitle() {
        return title;
    }

    public void setForum(String forum) {
        this.forum = forum;
    }

    public String getCreated() {
        return created;
    }

    public void voteIncr() {
        this.votes++;
    }

    public void voteDecr() {
        this.votes--;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getForumid() {
        return forumid;
    }

    public void setForumid(long forumid) {
        this.forumid = forumid;
    }
}