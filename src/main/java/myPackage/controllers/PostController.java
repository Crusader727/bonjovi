package myPackage.controllers;

import myPackage.dao.ForumDao;
import myPackage.dao.PostDao;
import myPackage.dao.ThreadDao;
import myPackage.dao.UserDao;
import myPackage.models.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/post")
public class PostController {
    private final PostDao pdao;
    private final UserDao udao;
    private final ForumDao fdao;
    private final ThreadDao tdao;

    public PostController(PostDao pdao, UserDao udao, ForumDao fdao, ThreadDao tdao) {
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
        Post buf = pdao.getPostById(id);
        if (buf == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("cant find such user"));
        }
        Details dt = new Details(null, null, buf, null);
        if (related != null) {
            for (String st : related) {
                if (st.equals("user")) {
                    dt.setAuthor(udao.getUserByNick(buf.getAuthor()));
                } else if (st.equals("forum")) {
                    fdao.updateForum(buf.getForum());
                    dt.setForum(fdao.getForum(buf.getForum()));
                } else if (st.equals("thread")) {
                    dt.setThread(tdao.getThreadById((int) buf.getThread()));
                }
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(dt);
    }

}

