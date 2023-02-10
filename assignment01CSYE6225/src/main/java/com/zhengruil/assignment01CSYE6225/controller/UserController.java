package com.zhengruil.assignment01CSYE6225.controller;

import com.zhengruil.assignment01CSYE6225.entity.User;
import com.zhengruil.assignment01CSYE6225.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class UserController {
    @Autowired
    private UserRepository userRepo;

    /*
    create a user account
     */
    @PostMapping("/v1/user")
    public User createUser(
            @RequestParam("first_name")String firstName,
            @RequestParam("last_name")String lastName,
            @RequestParam("password")String password,
            @RequestParam("username")String username
            ) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);
//        user.setPassword(password);
        user.setUsername(username);

        return userRepo.save(user);
    }

    /*
    get user account information
     */
    @GetMapping(path = "/v1/user/{userId}")//, produces = "application/json"
    public Map<String, Object> getUserById(
            @PathVariable("userId")Integer id
    ) {
        Map<String, Object> userInfo = new HashMap<>();
        User user = userRepo.findById(id).orElse(null);

        userInfo.put("id", user.getId());
        userInfo.put("first_name", user.getFirstName());
        userInfo.put("last_name", user.getLastName());
        userInfo.put("username", user.getUsername());
        userInfo.put("account_created", user.getAccount_created());
        userInfo.put("account_updated", user.getAccount_updated());


        return userInfo;
    }

    /*
    create a user account
     */
    @PutMapping("/v1/user/{userId}")
    public Map<String, Object> updateUserById(
            @PathVariable("userId")Integer id,
            @RequestParam(value = "first_name", required = false)String firstName,
            @RequestParam(value = "last_name", required = false)String lastName,
            @RequestParam(value = "password", required = false)String password
    ) {
        //find user by id
        Optional<User> optionalUser = userRepo.findById(id);
        User user;

        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            return null;
        }

        //update user info
        if (firstName != null)
            user.setFirstName(firstName);

        if (lastName != null)
            user.setLastName(lastName);

        if (password != null)
            user.setPassword(password);

        User newUser = userRepo.save(user);

        //return dynamic json format
        Map<String, Object> userInfo = new HashMap<>();

        userInfo.put("id", newUser.getId());
        userInfo.put("first_name", newUser.getFirstName());
        userInfo.put("last_name", newUser.getLastName());
        userInfo.put("username", newUser.getUsername());
        userInfo.put("account_created", newUser.getAccount_created());
        userInfo.put("account_updated", newUser.getAccount_updated());

        return userInfo;
    }
}
