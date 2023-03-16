package com.csye6225.productmanager.controller;

import com.csye6225.productmanager.config.DuplicateSkuException;
import com.csye6225.productmanager.entity.User;
import com.csye6225.productmanager.repository.UserRepository;
import com.csye6225.productmanager.service.CustomUserDetails;
import com.csye6225.productmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User currUser = userDetails.getUser();
        Integer currUserId = currUser.getId();

        if (!currUserId.equals(userId)){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        Optional<User> optionalUser = repo.findById(userId);
        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            return new ResponseEntity<User>(HttpStatus.FORBIDDEN);
        }

        User retUser = new User(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getAccount_updated(),
                user.getAccount_updated()
        );

        return new ResponseEntity<User>(retUser, HttpStatus.OK);
    }

    @PostMapping(value = "/v1/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createUser(
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

        try {
            repo.save(user);
        } catch (Exception  e){
            return new ResponseEntity<String>("User already exists! The error is: " + e.getMessage(), HttpStatus.CONFLICT);
        }

        return new ResponseEntity<String>("User created successfully!", HttpStatus.CREATED);
    }

    @PutMapping(value = "/v1/user/{userId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> updateUserById(
            @PathVariable(value = "userId")Integer userId,
            @RequestParam(value = "first_name")String firstName,
            @RequestParam(value = "last_name")String lastName,
            @RequestParam(value = "password")String password,
//            @RequestParam(value = "username")String username,
            Authentication authentication
    ) {
        if (!isValid(userId)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        //find user by id
        Optional<User> optionalUser = repo.findById(userId);
        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User currUser = userDetails.getUser();
        Integer currUserId = currUser.getId();

        if (!currUserId.equals(user.getId())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        try {
            if (firstName == null || lastName == null
                    || password == null)
                return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);

            //update user info
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPassword(password);
//            user.setUsername(username);

            repo.save(user);

            return new ResponseEntity<String>("User updated successfully!", HttpStatus.NO_CONTENT);
        }
        catch (DataIntegrityViolationException ex) {
            throw new DuplicateSkuException("User with username " + user.getUsername() + " already exists");
        }
    }

    private boolean isValid(Integer id) {
        if (id == null || id < 0) return false;
        return true;
    }
}



