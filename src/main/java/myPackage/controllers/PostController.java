package myPackage.controllers;

import myPackage.dao.ForumDao;
import myPackage.dao.PostDao;
import myPackage.dao.ThreadDao;
import myPackage.dao.UserDao;
import myPackage.models.*;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;


@RestController
@RequestMapping("/api/post")
public class PostController {
    private final PostDao pdao;
    private final UserDao udao;
    private final ForumDao fdao;
    private final ThreadDao tdao;
    private final Message err;

    public PostController(PostDao pdao, UserDao udao, ForumDao fdao, ThreadDao tdao) {
        err = new Message("---");
        this.pdao = pdao;
        this.fdao = fdao;
        this.tdao = tdao;
        this.udao = udao;
    }


    @RequestMapping(path = "/{id}/details", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity<?> updatePost(@PathVariable("id") long id,
                                        @RequestBody Post body) {
        Post buf = pdao.getPostById(id);
        if (buf == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("cant find such user"));
        }

        if (body.getMessage() != null && !buf.getMessage().equals(body.getMessage())) {
            buf.setMessage(body.getMessage());
            buf.setEdited(true);
            pdao.changePost(buf);
        }
        return ResponseEntity.status(HttpStatus.OK).body(buf);
    }

    @RequestMapping(path = "/{id}/details", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> getDetails(@PathVariable("id") long id,
                                        @RequestParam(value = "related", required = false) String[] related) {
//        Post buf = pdao.getPostById(id);
//        if (buf == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
//        }
        Post buf;
        try {
            buf = pdao.getPostByIdPerf(id);
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
        }
        Details dt = new Details(null, null, buf, null);
        if (related != null) {
            if (Arrays.asList(related).contains("user")) {
                dt.setAuthor(udao.getUserByNickPerf(buf.getAuthor()));
            }
            if (Arrays.asList(related).contains("forum")) {
                dt.setForum(fdao.getForumById(buf.getForumid()));
            }
            if (Arrays.asList(related).contains("thread")) {
                dt.setThread(tdao.getThreadById(buf.getThread()));
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(dt);
    }

}

