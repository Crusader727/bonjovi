package myPackage.models;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
    private final String nickname;
    private final String about;
    private final String email;
    private final String fullname;


    @JsonCreator
    public User(
            @JsonProperty("nickname") String nickname,
            @JsonProperty("about") String about,
            @JsonProperty("email") String email,
            @JsonProperty("fullname") String fullname
    ) {
        this.nickname = nickname;
        this.about = about;
        this.email = email;
        this.fullname = fullname;
    }


    public String getNickname() {
        return nickname;
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
}