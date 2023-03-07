package com.csye6225.productmanager.config;

import com.csye6225.productmanager.entity.User;
import com.csye6225.productmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("userSecurity")
public class UserSecurity {
    @Autowired
    private UserRepository repo;

    public boolean hasUserId(Authentication authentication, Integer userId) {
        // do your check(s) here
        Optional<User> optionalUser = repo.findById(userId);
        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            return false;
        }

        String currUserName = authentication.getName();
        if (!currUserName.equals(user.getUsername())){
            return false;
        }

        return true;
    }
}
