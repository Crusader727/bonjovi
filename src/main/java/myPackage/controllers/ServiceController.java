package myPackage.controllers;

import myPackage.dao.PostDao;
import myPackage.dao.ServiceDao;
import myPackage.dao.ThreadDao;
import myPackage.models.*;
import myPackage.models.Thread;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/service")
public class ServiceController {
    private final ServiceDao sdao;

    public ServiceController(ServiceDao sdao) {
        this.sdao = sdao;
    }


    @RequestMapping(path = "/status", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> getDetails() {
        return ResponseEntity.status(HttpStatus.OK).body(sdao.getInfo());
    }

    @RequestMapping(path = "/clear", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity<?> clearDB() {
       // sdao.truncateDB();
        return ResponseEntity.status(HttpStatus.OK).body("CLEARED!!!");
    }
}

