package myPackage.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Vote {

    private String nickname;
    private long voice;
    private Integer threadid;
    private long id;
    private long userid;

    @JsonCreator
    public Vote(
            @JsonProperty("id") long id,
            @JsonProperty("userid") long userid,
            @JsonProperty("nickname") String nickname,
            @JsonProperty("voice") long voice,
            @JsonProperty("threadid") Integer tid
    ) {
        this.userid = userid;
        this.id = id;
        this.nickname = nickname;
        this.voice = voice;
        this.threadid = tid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public long getVoice() {
        return voice;
    }

    public void setVoice(long voice) {
        this.voice = voice;
    }

    public Integer getThreadid() {
        return threadid;
    }

    public void setThreadid(Integer threadid) {
        this.threadid = threadid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }
}
