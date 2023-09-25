package com.csye6225.productmanager.controller;

import com.csye6225.productmanager.config.DuplicateSkuException;
import com.csye6225.productmanager.entity.User;
import com.csye6225.productmanager.repository.UserRepository;
import com.csye6225.productmanager.service.CustomUserDetails;
import com.csye6225.productmanager.service.UserService;
import com.timgroup.statsd.StatsDClient;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class UserController {
    private final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private StatsDClient statsDClient;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository repo;

    @GetMapping(value = "/v1/user/{userId}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> getUserById(
            @PathVariable("userId") Integer userId,
            Authentication authentication
    ) {
        logger.info("getUserById method called with userId {}", userId);
        statsDClient.incrementCounter("endpoint.homepage.http.get");

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User currUser = userDetails.getUser();
        Integer currUserId = currUser.getId();

        if (!currUserId.equals(userId)){
            logger.warn("Unauthorized access attempted with userId {}", userId);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        Optional<User> optionalUser = repo.findById(userId);
        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            logger.warn("User with userId {} not found", userId);
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

        logger.info("User with userId {} retrieved successfully", userId);
        return new ResponseEntity<>(retUser, HttpStatus.OK);
    }

    @PostMapping(value = "/v1/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createUser(
            @RequestParam(value = "first_name") String first_name,
            @RequestParam(value = "last_name") String last_name,
            @RequestParam(value = "password")String password,
            @RequestParam(value = "username") String username
    ){
        statsDClient.incrementCounter("endpoint.homepage.http.post");

        User user = new User();

        if(first_name ==null||first_name.equals("")
                ||last_name ==null||last_name.equals("")
                ||username ==null||username.equals("")
                ||password ==null||password.equals("")
        ) {
            logger.warn("Invalid input parameters while creating new user");
            return new ResponseEntity<>("No components can be null!", HttpStatus.BAD_REQUEST);
        }

        user.setFirstName(first_name);
        user.setLastName(last_name);
        user.setUsername(username);

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encryptedPwd = passwordEncoder.encode(password);
        user.setPassword(encryptedPwd);

        try {
            logger.info("User created with id " + user.getId());
            repo.save(user);
        } catch (Exception  e){
            logger.error("Error creating user: " + e.getMessage());
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
            Authentication authentication
    ) {
        logger.info("Update user request received for userId: {}", userId);
        statsDClient.incrementCounter("endpoint.homepage.http.put");

        if (!isValid(userId)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        //find user by id
        Optional<User> optionalUser = repo.findById(userId);
        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            logger.warn("User not found for userId: {}", userId);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User currUser = userDetails.getUser();
        Integer currUserId = currUser.getId();

        if (!currUserId.equals(user.getId())){
            logger.warn("User with userId: {} is not authorized to update this user", currUserId);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        try {
            if (firstName == null || lastName == null
                    || password == null)
            {
                logger.warn("Invalid input for updating user with userId: {}", userId);
                return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
            }

            //update user info
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPassword(password);

            repo.save(user);

            logger.info("User with userId: {} updated successfully", userId);
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



