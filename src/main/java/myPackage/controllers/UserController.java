package myPackage.controllers;

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
    public ResponseEntity<String> createUser(@PathVariable("nickname") String nick, @RequestBody User body) {
        body.setNickname(nick);
        Integer result = udao.createUser(body);
        User us;
        if (result == 201) {
            return ResponseEntity.status(HttpStatus.CREATED).body(body.toString());
        } else {
            us = udao.getUserByNick(nick);
            if (us == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(udao.getUserByEmail(body.getEmail()).toString());
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).body(us.toString());
        }
    }

    @RequestMapping(path = "/{nickname}/profile", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<String> userProfile(@PathVariable("nickname") String nick) {
        User result = udao.getUserByNick(nick);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such User");
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(result.toString());
        }
    }

    @RequestMapping(path = "/{nickname}/profile", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity<String> changeProfile(@PathVariable("nickname") String nick, @RequestBody User body) {
        body.setNickname(nick);
        Integer result = udao.changeUser(body);
        if(result == 201) {
            return ResponseEntity.status(HttpStatus.OK).body(body.toString());
        }
        else if(result == 404) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cant find such User");
        }
        else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflicting with another user");
        }
    }
}
