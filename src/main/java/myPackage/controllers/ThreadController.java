package myPackage.controllers;

import myPackage.dao.PostDao;
import myPackage.dao.ThreadDao;
import myPackage.dao.UserDao;
import myPackage.models.*;
import myPackage.models.Thread;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/thread")
public class ThreadController {
    private final ThreadDao tdao;
    private final PostDao pdao;
    private final UserDao udao;
    private final Message errorMessage;


    public ThreadController(ThreadDao tdao, PostDao pdao, UserDao udao) {
        this.tdao = tdao;
        this.pdao = pdao;
        this.udao = udao;
        this.errorMessage = new Message("---");
    }

    @RequestMapping(path = "/{slug_or_id}/create", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity<?> createPost(@PathVariable("slug_or_id") String slug_or_id,
                                        @RequestBody ArrayList<Post> bodyList) {
//        SlugOrID key = new SlugOrID(slug_or_id);
//        Thread buf;
//        if (key.IsLong) {
//            buf = tdao.getThreadById(key.id);
//        } else {
//            buf = tdao.getThreadBySlug(key.slug);
//        }
//        if (buf == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("No such thread"));
//        }
        Thread buf;
        try {
            buf = tdao.getThreadbySlugOrID(new SlugOrID(slug_or_id));
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }
//        for (Post body : bodyList) {
//            body.setForum(buf.getForum());
//            body.setThread(buf.getId());
//            body.setForumid(buf.getForumid());
//        }
        Integer res = pdao.createPosts(bodyList, buf);
        if (res == 409) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
        } else if (res == 404) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(bodyList);

    }

    @RequestMapping(path = "/{slug_or_id}/vote", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity<?> vote(@PathVariable("slug_or_id") String slug_or_id,
                                  @RequestBody Vote body) {
//        SlugOrID key = new SlugOrID(slug_or_id);
//        Boolean flag = false;
//        if (key.IsLong) {
//            flag = tdao.vote(key.id, null, body);
//        } else {
//            flag = tdao.vote(null, key.slug, body);
//        }
//        Thread thread;
//        if (key.IsLong) {
//            thread = tdao.getThreadById(key.id);
//        } else {
//            thread = tdao.getThreadBySlug(key.slug);
//        }
//        if (flag) {
//            return ResponseEntity.status(HttpStatus.OK).body(thread);
//        } else {
//            if (thread == null) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("No such thread"));
//            }
//            User user = udao.getUserIDbyNick(body.getNickname());
//            if (user == null) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("No such User"));
//            }
//        }
//        return ResponseEntity.status(HttpStatus.OK).body(thread);
        Thread thread;
        try {
            thread = tdao.getThreadbySlugOrID(new SlugOrID(slug_or_id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }
        try {
            tdao.vote(new SlugOrID(slug_or_id), body);
        }catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }
        return ResponseEntity.status(HttpStatus.OK).body(thread);
    }

    @RequestMapping(path = "/{slug_or_id}/details", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> getDetails(@PathVariable("slug_or_id") String slug_or_id) {
//        SlugOrID key = new SlugOrID(slug_or_id);
//        Thread buf;
//        if (key.IsLong) {
//            buf = tdao.getThreadById(key.id);
//        } else {
//            buf = tdao.getThreadBySlug(key.slug);
//        }
//        if (buf == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
//        }
//        return ResponseEntity.status(HttpStatus.OK).body(buf);
        try {
            return ResponseEntity.status(HttpStatus.OK).body(tdao.getThreadbySlugOrID(new SlugOrID(slug_or_id)));
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }
    }

    @RequestMapping(path = "/{slug_or_id}/details", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity<?> postDetails(@PathVariable("slug_or_id") String slug_or_id, @RequestBody Thread body) {
        SlugOrID key = new SlugOrID(slug_or_id);
        Thread buf;
        if (key.IsLong) {
            buf = tdao.getThreadById(key.id);
        } else {
            buf = tdao.getThreadBySlug(key.slug);
        }
        if (buf == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("No such thread"));
        }
        if (body.getMessage() != null) {
            buf.setMessage(body.getMessage());
        }
        if (body.getTitle() != null) {
            buf.setTitle(body.getTitle());
        }
        tdao.chagenThread(buf);
        return ResponseEntity.status(HttpStatus.OK).body(buf);
    }

    @RequestMapping(path = "/{slug_or_id}/posts", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> getPosts(@PathVariable("slug_or_id") String slug_or_id,
                                      @RequestParam(value = "limit", required = false) Integer limit,
                                      @RequestParam(value = "sort", required = false, defaultValue = "flat") String sort,
                                      @RequestParam(value = "desc", required = false, defaultValue = "false") Boolean desc,
                                      @RequestParam(value = "since", required = false) Integer since) {

//        SlugOrID key = new SlugOrID(slug_or_id);
//        Thread buf;
//        if (key.IsLong) {
//            buf = tdao.getThreadById(key.id);
//        } else {
//            buf = tdao.getThreadBySlug(key.slug);
//        }
        try {
            return ResponseEntity.status(HttpStatus.OK).body(tdao.getPosts(tdao.getThreadIDbySlugOrID(new SlugOrID(slug_or_id)), limit, since, sort, desc));
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }

    }


}

