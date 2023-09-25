package com.csye6225.productmanager.repository;

import com.csye6225.productmanager.entity.Image;
import com.csye6225.productmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {
}
