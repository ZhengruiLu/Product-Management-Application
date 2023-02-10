package com.zhengruil.assignment01CSYE6225.repository;

import com.zhengruil.assignment01CSYE6225.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

}
