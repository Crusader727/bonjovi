package myPackage.controllers;

import myPackage.dao.ForumDao;
import myPackage.dao.ThreadDao;
import myPackage.dao.UserDao;
import myPackage.models.Message;
import myPackage.models.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import myPackage.models.Forum;
import myPackage.models.Thread;

@RestController
@RequestMapping("/api/forum")
public class ForumController {
    private final ForumDao fdao;
    private final ThreadDao tdao;
    private final UserDao udao;
    private final Message err;

    public ForumController(ForumDao fdao, UserDao udao, ThreadDao tdao) {
        err = new Message("--");
        this.fdao = fdao;
        this.udao = udao;
        this.tdao = tdao;
    }

    @RequestMapping(path = "/create", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity<?> createForum(@RequestBody Forum body) {
        User us = udao.getUserByNick(body.getUser());//TODO очень плохо надо будет подумать
        if (us != null) {
            body.setUser(us.getNickname());
        }
        Integer result = fdao.createForum(body);
        if (result == 201) {

            return ResponseEntity.status(HttpStatus.CREATED).body(body);
        } else if (result == 404) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("Cant find such User"));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(fdao.getForum(body.getSlug()));
        }
    }

    @RequestMapping(path = "/{slug}/details", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> forumDetails(@PathVariable("slug") String sl) {
        Forum result = fdao.getForum(sl);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }
    }

    @RequestMapping(path = "/{forum}/create", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity<?> createThread(@RequestBody Thread body, @PathVariable("forum") String forum) {
        body.setForum(forum);
        Integer[] result = tdao.createThread(body);
        if (result[0] == 201) {
            body.setId(result[1]);
            body.setForum(fdao.getForum(forum).getSlug());
            return ResponseEntity.status(HttpStatus.CREATED).body(body);
        } else if (result[0] == 404) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("Cant find such User or thread"));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(tdao.getThreadBySlug(body.getSlug()));
        }
    }


    @RequestMapping(path = "/{forum}/threads", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> getThreads(@PathVariable String forum,
                                        @RequestParam(value = "limit", required = false) Integer limit,
                                        @RequestParam(value = "since", required = false) String since,
                                        @RequestParam(value = "desc", required = false, defaultValue = "false") Boolean desc) {
//        Forum fr = fdao.getForum(forum);
        Integer fr = fdao.getForumIdBySlug(forum);
        if (fr != null) {
            return ResponseEntity.status(HttpStatus.OK).body(tdao.getThreads(fr, limit, since, desc));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
        }
    }


    @RequestMapping(path = "/{forum}/users", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> getUsers(@PathVariable String forum,
                                      @RequestParam(value = "limit", required = false) Integer limit,
                                      @RequestParam(value = "since", required = false) String since,
                                      @RequestParam(value = "desc", required = false, defaultValue = "false") Boolean desc) {
//        Forum fr = fdao.getForum(forum);
        Integer fr = fdao.getForumIdBySlug(forum);
        if (fr == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
        }
        return ResponseEntity.status(HttpStatus.OK).body(fdao.getUsers(fr, limit, since, desc));
    }

}
