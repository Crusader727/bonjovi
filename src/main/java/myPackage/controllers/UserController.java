package myPackage.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.org.apache.regexp.internal.RE;
import myPackage.dao.UserDao;
import myPackage.dao.UserDao;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import myPackage.models.User;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserDao udao;

    public UserController(UserDao udao) {
        this.udao = udao;
    }

    @RequestMapping(path = "/{nickname}/create", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity<?> createUser(@PathVariable("nickname") String nick, @RequestBody User body) {
        body.setNickname(nick);
        Integer result = udao.createUser(body);
        if (result == 201) {
            return ResponseEntity.status(HttpStatus.CREATED).body(body);
        } else {
            User us = udao.getUserByNick(nick);
            User us2 = udao.getUserByEmail(body.getEmail());
            if (us == null) {
                User[] usrs = {us2};
                return ResponseEntity.status(HttpStatus.CONFLICT).body(usrs);
            }
            if (us2 != null && !us.getEmail().equals(us2.getEmail())) {
                User[] usrs = {us,us2};
                return ResponseEntity.status(HttpStatus.CONFLICT).body(usrs);
            }
            User[] usrs2 = {us};
            return ResponseEntity.status(HttpStatus.CONFLICT).body(usrs2);
        }
    }

    @RequestMapping(path = "/{nickname}/profile", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> userProfile(@PathVariable("nickname") String nick) {
        User result = udao.getUserByNick(nick);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such User");
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }
    }

    @RequestMapping(path = "/{nickname}/profile", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity<?> changeProfile(@PathVariable("nickname") String nick, @RequestBody User body) {
        body.setNickname(nick);
        Integer result = udao.changeUser(body);
        if (result == 201) {
            return ResponseEntity.status(HttpStatus.OK).body(body);
        } else if (result == 404) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cant find such User");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflicting with another user");
        }
    }
}
