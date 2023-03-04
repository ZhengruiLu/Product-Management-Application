package com.csye6225.productmanager.controller;

import com.csye6225.productmanager.entity.Image;
import com.csye6225.productmanager.entity.Product;
import com.csye6225.productmanager.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/v1/product")
public class ImageController {

    @Autowired
    private ImageRepository imageRepository;

    @PostMapping("/{product_id}/image")
    public ResponseEntity<?> uploadImage(@PathVariable("product_id") Integer productId,
                                         @RequestBody(required = true) String file,
                                         @RequestBody(required = true) Object fileType
                                         ) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Check if user is authorized to upload image for this product
        // You can add your authorization logic here

        try {
            Image image = new Image();
            image.setProduct(new Product(productId));
            image.setFileName(file);
            image.setS3BucketPath("s3://example-bucket/" + file);
            image = imageRepository.save(image);

            // Save file to S3 or other storage
            // You can add your storage logic here

            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(image.getImage_id())
                    .toUri();
            return ResponseEntity.created(location).body(image);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{product_id}/image")
    public ResponseEntity<?> getProductImages(@PathVariable("product_id") Integer productId) {
        try {
            List<Image> images = imageRepository.findByProductId(productId);
            if (images.isEmpty()) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.ok().body(images);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}

