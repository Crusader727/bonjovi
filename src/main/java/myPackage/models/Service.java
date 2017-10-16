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
//    CREATE EXTENSION IF NOT EXISTS citext;
//    create table users(
//        nickname citext primary key,
//        fullname text not null,
//        email citext not null UNIQUE ,
//        about text
//);
//    create table forum(
//        slug citext primary key,
//        title text not null,
//        postCount bigint,
//        threadCount bigint,
//        owner citext references users(nickname)
//);
//        create table thread(
//        tid SERIAL PRIMARY KEY ,
//        slug citext  unique ,
//        owner citext references users(nickname),
//        forum citext references forum(slug),
//        created TIMESTAMP WITH TIME ZONE,
//        message text not null,
//        title text not null,
//        votes bigint
//        );
//
//        create table post (
//        id SERIAL PRIMARY KEY ,
//        parent integer default 0,
//        owner citext references users(nickname),
//        message text,
//        isedited BOOLEAN,
//        forum citext references forum(slug),
//        created TIMESTAMP WITH TIME ZONE,
//        threadid integer references thread(tid)
//        );
//        create table vote (
//        nickname citext references users(nickname),
//        threadid integer references thread(tid),
//        votes int
//        );