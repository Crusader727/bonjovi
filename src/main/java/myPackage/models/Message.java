package myPackage.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Message {
    private final String message;

    @JsonCreator
    public Message(
            @JsonProperty("message") String mes
    ) {
        this.message = mes;
    }

    public String getMessage() {
        return message;
    }
}
