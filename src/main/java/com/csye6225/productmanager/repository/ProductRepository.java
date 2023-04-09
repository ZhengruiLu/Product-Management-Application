package com.csye6225.productmanager.repository;

import com.csye6225.productmanager.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {

}