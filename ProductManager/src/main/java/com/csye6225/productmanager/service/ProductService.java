package com.csye6225.productmanager.service;

import com.csye6225.productmanager.entity.Image;
import com.csye6225.productmanager.entity.Product;
import com.csye6225.productmanager.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository repo;

    public void save(Product product) {
        repo.save(product);
    }

    public Product getById(Integer id) {
        return repo.findById(id).get();
    }

    public void deleteById(Integer id) {
        repo.deleteById(id);
    }

    public List<Image> getImagesById(Integer id) {
        return this.getById(id).getImages();
    }
}