package myPackage.models;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class User {

    private String about;
    private String email;
    private String fullname;
    private String nickname;
    private long id;

    @JsonCreator
    public User(
            @JsonProperty("id") long id,
            @JsonProperty("nickname") String nickname,
            @JsonProperty("about") String about,
            @JsonProperty("email") String email,
            @JsonProperty("fullname") String fullname
    ) {
        this.id = id;
        this.nickname = nickname;
        this.about = about;
        this.email = email;
        this.fullname = fullname;
    }


    public String getAbout() {
        return about;
    }

    public String getEmail() {
        return email;
    }

    public String getFullname() {
        return fullname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nick) {
        this.nickname = nick;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}