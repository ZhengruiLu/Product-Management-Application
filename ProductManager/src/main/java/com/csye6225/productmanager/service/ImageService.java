package com.csye6225.productmanager.service;

import com.csye6225.productmanager.entity.Image;
import com.csye6225.productmanager.entity.Product;
import com.csye6225.productmanager.repository.ImageRepository;
import com.csye6225.productmanager.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ImageService {
    @Autowired
    private ImageRepository repo;

    public void save(Image image) {
        repo.save(image);
    }

    public Image getById(Integer id) {
        return repo.findById(id).orElse(null);
    }

    public void deleteById(Integer id) {
        repo.deleteById(id);
    }


}
