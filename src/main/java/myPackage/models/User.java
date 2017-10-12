package myPackage.models;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class User {

    private  String about;
    private  String email;
    private  String fullname;
    private  String nickname;


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
}