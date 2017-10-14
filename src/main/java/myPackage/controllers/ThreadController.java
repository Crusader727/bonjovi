package myPackage.controllers;

import com.sun.org.apache.regexp.internal.RE;
import myPackage.dao.ForumDao;
import myPackage.dao.PostDao;
import myPackage.dao.ThreadDao;
import myPackage.dao.UserDao;
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

}

