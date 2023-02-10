package com.zhengruil.assignment01CSYE6225.controller;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class UserControllerTest {
    @Autowired
    private UserController userController;
    @Test
    void createUser() {
    }

    @Test
    void getUserById() {
        Assert.assertNotNull(userController.getUserById(1));
        System.out.println("Pass test - getUserById!");

        Assert.assertNotNull(userController.getUserById(10));

    }

    @Test
    void updateUserById() {
    }
}