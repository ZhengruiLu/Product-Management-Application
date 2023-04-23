package com.csye6225.productmanager.utils;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class Hash {
    private static int workload = 12;

    public static String hashPassword(String password) {
        String salt = BCrypt.gensalt(workload);
        String hashedpassword = BCrypt.hashpw(password,salt);
        return hashedpassword;
    }
    public static boolean checkPassword(String password, String oldPassword) {
        return BCrypt.checkpw(password, oldPassword);
    }
}
