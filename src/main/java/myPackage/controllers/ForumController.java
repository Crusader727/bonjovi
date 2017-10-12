package myPackage.controllers;

import com.sun.org.apache.regexp.internal.RE;
import myPackage.dao.ForumDao;
import myPackage.dao.UserDao;
import myPackage.models.Message;
import myPackage.models.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import myPackage.models.Forum;

@RestController
@RequestMapping("/forum")
public class ForumController {
    private final ForumDao fdao;
    private final UserDao udao;
    public ForumController( ForumDao fdao, UserDao udao) {
        this.fdao = fdao;
        this.udao = udao;
    }

    @RequestMapping(path = "/create", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity<?> createForum(@RequestBody Forum body) {
        User us = udao.getUserByNick(body.getUser());//TODO очень плохо надо будет подумать
        if(us != null) {
            body.setUser(us.getNickname());
        }
        Integer result = fdao.createForum(body);
        if(result == 201) {

            return ResponseEntity.status(HttpStatus.CREATED).body(body);
        }
        else if(result == 404) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("Cant find such User"));
        }
        else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(fdao.getForum(body.getSlug()));
        }
    }

    @RequestMapping(path = "/{slug}/details", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> forumDetails( @PathVariable("slug") String sl) {
        Forum result = fdao.getForum(sl);
        if(result == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("No such forum"));
        }
        else {
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }
    }
}
