package com.csye6225.productmanager.controller;

import com.csye6225.productmanager.config.DuplicateSkuException;
import com.csye6225.productmanager.entity.User;
import com.csye6225.productmanager.repository.UserRepository;
import com.csye6225.productmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@Controller
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository repo;

    @GetMapping(value = "/v1/user/{userId}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> getUserById(
            @PathVariable("userId") Integer userId,
            Authentication authentication
    ) {
        String currUserName = authentication.getName();

        Optional<User> optionalUser = repo.findById(userId);
        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            return new ResponseEntity<User>(HttpStatus.FORBIDDEN);
        }

        if (currUserName.equals(user.getUsername())) {
            return new ResponseEntity<User>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<User>(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping(value = "/v1/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createUser(
//                                            @RequestBody User user
                                            @RequestParam(value = "first_name", required = true) String first_name,
                                           @RequestParam(value = "last_name", required = true) String last_name,
                                           @RequestParam(value = "password", required = true)String password,
                                           @RequestParam(value = "username", required = true) String username
    ){

        User user = new User();

        if(first_name ==null||first_name ==""
            ||last_name ==null||last_name ==""
            ||username ==null||username ==""
            ||password ==null||password ==""
            )
            return new ResponseEntity<>("No components can be null!", HttpStatus.BAD_REQUEST);

        user.setFirstName(first_name);
        user.setLastName(last_name);
        user.setUsername(username);

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encryptedPwd = passwordEncoder.encode(password);
        user.setPassword(encryptedPwd);
//        if (user == null)
//            return new ResponseEntity<>("No components can be null!", HttpStatus.BAD_REQUEST);

        repo.save(user);

        return new ResponseEntity<String>("User created successfully!", HttpStatus.CREATED);
    }

    @PutMapping(value = "/v1/user/{userId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> updateUserById(
            @PathVariable(value = "userId")Integer userId,
            @RequestParam(value = "first_name", required = false)String firstName,
            @RequestParam(value = "last_name", required = false)String lastName,
            @RequestParam(value = "password", required = false)String password,
            @RequestParam(value = "username", required = false)String username
    ) {
        //find user by id
        Optional<User> optionalUser = repo.findById(userId);
        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            if (firstName == null && lastName == null
                    && password == null && username == null)
                return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);

            //update user info
            if (firstName != null)
                user.setFirstName(firstName);
            if (lastName != null)
                user.setLastName(lastName);
            if (password != null)
                user.setPassword(password);
            if (username != null)
                user.setUsername(username);

            repo.save(user);

            return new ResponseEntity<String>("User update successfully!", HttpStatus.NO_CONTENT);
        }
        catch (DataIntegrityViolationException ex) {
            throw new DuplicateSkuException("User with username " + user.getUsername() + " already exists");
        }
    }
}



