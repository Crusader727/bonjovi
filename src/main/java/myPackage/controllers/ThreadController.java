package myPackage.controllers;

import myPackage.dao.PostDao;
import myPackage.dao.ThreadDao;
import myPackage.models.*;
import myPackage.models.Thread;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/thread")
public class ThreadController {
    private final ThreadDao tdao;
    private final PostDao pdao;

    public ThreadController(ThreadDao tdao, PostDao pdao) {
        this.tdao = tdao;
        this.pdao = pdao;
    }

    @RequestMapping(path = "/{slug_or_id}/create", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity<?> createPost(@PathVariable("slug_or_id") String slug_or_id,
                                             @RequestBody ArrayList<Post> bodyList) {
        SlugOrID key = new SlugOrID(slug_or_id);
        Thread buf;
        if (key.IsLong) {
           buf =  tdao.getThreadById(key.id);
        }
        else {
            buf =  tdao.getThreadBySlug(key.slug);
        }
        if(buf == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("No such thread"));
        }
        for (Post body: bodyList) {
            body.setForum(buf.getForum());
            body.setThread(buf.getId());
        }
         pdao.createPosts(bodyList);
            return ResponseEntity.status(HttpStatus.CREATED).body(bodyList);

    }

    @RequestMapping(path = "/{slug_or_id}/vote", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity<?> vote(@PathVariable("slug_or_id") String slug_or_id,
                                        @RequestBody Vote body) {
        SlugOrID key = new SlugOrID(slug_or_id);
        Thread buf;
        if (key.IsLong) {
            buf =  tdao.getThreadById(key.id);
        }
        else {
            buf =  tdao.getThreadBySlug(key.slug);
        }
        if(buf == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("No such thread"));
        }
        tdao.vote(buf, body);

        return ResponseEntity.status(HttpStatus.OK).body(tdao.getThreadBySlug(buf.getSlug()));

    }

    @RequestMapping(path = "/{slug_or_id}/details", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> getDetails(@PathVariable("slug_or_id") String slug_or_id) {
        SlugOrID key = new SlugOrID(slug_or_id);
        Thread buf;
        if (key.IsLong) {
            buf =  tdao.getThreadById(key.id);
        }
        else {
            buf =  tdao.getThreadBySlug(key.slug);
        }
        if(buf == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("No such thread"));
        }
        return ResponseEntity.status(HttpStatus.OK).body(buf);
    }

    @RequestMapping(path = "/{slug_or_id}/details", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity<?> postDetails(@PathVariable("slug_or_id") String slug_or_id, @RequestBody Thread body) {
        SlugOrID key = new SlugOrID(slug_or_id);
        Thread buf;
        if (key.IsLong) {
            buf =  tdao.getThreadById(key.id);
        }
        else {
            buf =  tdao.getThreadBySlug(key.slug);
        }
        if(buf == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("No such thread"));
        }
        if(body.getMessage() != null) {
            buf.setMessage(body.getMessage());
        }
        if(body.getTitle() != null) {
            buf.setTitle(body.getTitle());
        }
        tdao.chagenThread(buf);
        return ResponseEntity.status(HttpStatus.OK).body(buf);
    }

}

