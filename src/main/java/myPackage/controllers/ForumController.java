package myPackage.controllers;

import com.sun.org.apache.regexp.internal.RE;
import myPackage.dao.ForumDao;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import myPackage.models.Forum;

@RestController
@RequestMapping("/forum")
public class ForumController {
    private final ForumDao fdao;
    public ForumController( ForumDao fdao) {
        this.fdao = fdao;
    }

    @RequestMapping(path = "/create", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity<String> createForum(@RequestBody Forum body) {
        Integer result = fdao.createForum(body);
        if(result == 201) {
            return ResponseEntity.status(HttpStatus.CREATED).body(body.toString());
        }
        else if(result == 404) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cant find such User");
        }
        else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(fdao.getForum(body.getSlug()).toString());
        }
    }

    @RequestMapping(path = "/{slug}/details", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<String> forumDetails( @PathVariable("slug") String sl) {
        Forum result = fdao.getForum(sl);
        if(result == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such forum");
        }
        else {
            return ResponseEntity.status(HttpStatus.OK).body(result.toString());
        }
    }
}
