package com.csye6225.productmanager.controller;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.csye6225.productmanager.config.DuplicateSkuException;
import com.csye6225.productmanager.entity.Image;
import com.csye6225.productmanager.entity.Product;
import com.csye6225.productmanager.entity.User;
import com.csye6225.productmanager.repository.ProductRepository;
import com.csye6225.productmanager.service.*;
import com.csye6225.productmanager.utils.AWSConfig;
import com.csye6225.productmanager.utils.RandomStringGenerator;
import com.timgroup.statsd.StatsDClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ProductController {
    RandomStringGenerator random = new RandomStringGenerator();

//    private AmazonClient amazonClient;
//    @Autowired
//    ProductController(AmazonClient amazonClient) {
//        this.amazonClient = amazonClient;
//    }

    AmazonS3 s3Client = AWSConfig.awss3Client();

    @Value("${amazonProperties.bucketName}")
    private String bucketName;
    @Autowired
    private ImageService imageService;
    @Autowired
    private ProductService service;

    @Autowired
    private ProductRepository repo;

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    @Autowired
    private StatsDClient statsDClient;
    @GetMapping(value = "/v1/product/{productId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Product> getProductById(
            @PathVariable(value = "productId")Integer id
    ) {
        statsDClient.incrementCounter("endpoint.homepage.http.get");

        try {
            Optional<Product> optionalProduct = repo.findById(id);
            Product product;

            if (optionalProduct.isPresent()) {
                product = optionalProduct.get();
            } else {
                logger.warn("No product found with id: " + id);
                return new ResponseEntity<Product>(HttpStatus.FORBIDDEN);
            }

            Product retProduct = new Product(
                    product.getId(),
                    product.getName(),
                    product.getDescription(),
                    product.getSku(),
                    product.getManufacturer(),
                    product.getQuantity(),
                    product.getDate_added(),
                    product.getDate_last_updated(),
                    product.getOwnerUserId()
            );

            logger.info("Successfully retrieved product with id: " + id);
            return new ResponseEntity<Product>(retProduct, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving product with id: " + id, e);
            return new ResponseEntity<Product>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/v1/product/{product_id}/image", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<Image>> getImagesById(
            @PathVariable(value = "product_id")Integer product_id,
            Authentication authentication
    ) {
        statsDClient.incrementCounter("endpoint.homepage.http.get");

        Optional<Product> optionalProduct = repo.findById(product_id);
        Product product;

        if (optionalProduct.isPresent()) {
            product = optionalProduct.get();
        } else {
            logger.warn("Product with ID " + product_id + " not found");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer currUserId = userDetails.getUser().getId();
        String currUserPassword = userDetails.getUser().getPassword();

        if (!currUserId.equals(product.getOwnerUserId()) || !currUserPassword.equals(product.getUser().getPassword())){
            logger.warn("Unauthorized access to images of product with ID " + product_id);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        List<Image> images = service.getImagesById(product_id);
        logger.info("Images of product with ID " + product_id + " retrieved successfully");

        return new ResponseEntity<>(images, HttpStatus.OK);
    }

    @PostMapping(value = "/v1/product/{product_id}/image", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Image> createImage(
            @PathVariable(value = "product_id")Integer id,
            Authentication authentication,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        logger.info("Image - post");
        statsDClient.incrementCounter("endpoint.homepage.http.post");

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer currUserId = userDetails.getUser().getId();

        //find user by id
        if (id == null || id < 0) {
            logger.warn("Invalid ID " + id + " provided for product deletion");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<Product> optionalProduct = repo.findById(id);
        Product product;

        if (optionalProduct.isPresent()) {
            product = optionalProduct.get();
        } else {
            logger.warn("Product with ID " + id + " not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (!currUserId.equals(product.getOwnerUserId())){
            logger.warn("Unauthorized deletion of product with ID " + id);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        // upload the image to AWS
        String fileName = random.generateRandomString();

//        String s3_bucket_path = this.amazonClient.uploadFile(file);


        FileInputStream inputStream = null;
        PutObjectRequest request = new PutObjectRequest(bucketName, fileName, file.getInputStream(), null);

        try {
            s3Client.putObject(request);
        } catch (AmazonServiceException e) {
            e.printStackTrace();
            return new ResponseEntity<Image>(HttpStatus.BAD_REQUEST);
        }

        // store the image(details) to rds
        Image newImage = new Image();
        // set the image file name
        newImage.setFile_name(fileName);
        // set the product id
        newImage.setProduct_id(id);
        // set the s3 bucket path
//        newImage.setS3_bucket_path(s3_bucket_path);
        newImage.setS3_bucket_path(fileName);

        imageService.save(newImage);

        return new ResponseEntity<Image>(newImage, HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/v1/product/{product_id}/image/{image_id}", produces = {MediaType.APPLICATION_JSON_VALUE})//, produces = "application/json"
    public ResponseEntity<String> deleteImage(
            @PathVariable(value = "product_id")Integer id,
            @PathVariable(value = "image_id")Integer imageId,
            Authentication authentication,
            @RequestParam("url") String fileUrl
    ) {
        logger.info("Image - delete");
        statsDClient.incrementCounter("endpoint.homepage.http.delete");

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer currUserId = userDetails.getUser().getId();

        //find user by id
        if (id == null || id < 0) {
            logger.warn("Invalid ID " + id + " provided for product deletion");
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }

        Optional<Product> optionalProduct = repo.findById(id);
        Product product;

        if (optionalProduct.isPresent()) {
            product = optionalProduct.get();
        } else {
            logger.warn("Product with ID " + id + " not found");
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }

        if (!currUserId.equals(product.getOwnerUserId())){
            logger.warn("Unauthorized deletion of product with ID " + id);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        // check if the image exist
        Image returnedImage = service.getImagesByImageId(id, imageId);

        if (returnedImage == null) {
            logger.warn("Image with ID " + id + " not found");
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }

        //delete image from S3 and rds
        imageService.deleteById(imageId);

        //delete from S3
//        String deleteFromS3Record = this.amazonClient.deleteFileFromS3Bucket(fileUrl);
        logger.info("Image with ID " + imageId + " deleted successfully");

        return new ResponseEntity<String>("Image delete successfully!", HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(value = "/v1/product/{productId}", produces = {MediaType.APPLICATION_JSON_VALUE})//, produces = "application/json"
    public ResponseEntity<String> deleteProductById(
            @PathVariable(value = "productId")Integer id,
            Authentication authentication
    ) {
        statsDClient.incrementCounter("endpoint.homepage.http.delete");

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer currUserId = userDetails.getUser().getId();

        //find user by id
        if (id == null || id < 0) {
            logger.warn("Invalid ID " + id + " provided for product deletion");
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }

        Optional<Product> optionalProduct = repo.findById(id);
        Product product;

        if (optionalProduct.isPresent()) {
            product = optionalProduct.get();
        } else {
            logger.warn("Product with ID " + id + " not found");
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }

        if (!currUserId.equals(product.getOwnerUserId())){
            logger.warn("Unauthorized deletion of product with ID " + id);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        service.deleteById(id);
        logger.info("Product with ID " + id + " deleted successfully");

        return new ResponseEntity<String>("Product delete successfully!", HttpStatus.NO_CONTENT);
    }


    @PostMapping(value = "/v1/product", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Product> createProduct(
            @RequestParam(value = "name", required = true)String name,
            @RequestParam(value ="description", required = true)String description,
            @RequestParam(value ="sku", required = true)String sku,
            @RequestParam(value ="manufacturer", required = true)String manufacturer,
            @RequestParam(value ="quantity", required = true)Integer quantity
    ) {
        statsDClient.incrementCounter("endpoint.homepage.http.post");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User currUser = userDetails.getUser();

        Product product = new Product();
        logger.info("Creating product with name: {}, description: {}, sku: {}, manufacturer: {}, and quantity: {}", name, description, sku, manufacturer, quantity);

        try {
            Integer currUserId = userDetails.getUser().getId();
            String currUserPassword = userDetails.getUser().getPassword();

//            if (!currUserId.equals(product.getOwnerUserId()) || !currUserPassword.equals(product.getUser().getPassword())){
//                logger.warn("User with ID {} attempted to create a product for another user.", currUserId);
//                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
//            }

            if (quantity >= 0 && quantity <= 100)
                product.setQuantity(quantity);
            else{
                logger.warn("Invalid quantity value {} provided while creating product.", quantity);
                return new ResponseEntity<Product>(HttpStatus.BAD_REQUEST);
            }

            if (name == null || name == ""
                    || description == null || description == ""
                    || sku == null || sku == ""
                    || manufacturer == null || manufacturer == ""
            ) {
                logger.warn("Invalid input provided while creating product.");
                return new ResponseEntity<Product>(HttpStatus.BAD_REQUEST);
            }

            product.setName(name);
            product.setDescription(description);
            product.setSku(sku);
            product.setManufacturer(manufacturer);
            product.setUser(currUser);

            service.save(product);

            Product retProduct = new Product(
                    product.getId(),
                    product.getName(),
                    product.getDescription(),
                    product.getSku(),
                    product.getManufacturer(),
                    product.getQuantity(),
                    product.getDate_added(),
                    product.getDate_last_updated(),
                    product.getOwnerUserId()
            );
            logger.info("Product created with SKU: {}", retProduct.getSku());
            return new ResponseEntity<Product>(retProduct, HttpStatus.CREATED);
        } catch (DataIntegrityViolationException ex) {
            logger.error("Error creating product with SKU: {}, Error message: {}", product.getSku(), ex.getMessage());
            throw new DuplicateSkuException("Product with SKU " + product.getSku() + " already exists");
        }
    }

    @PutMapping(value = "/v1/product/{productId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> updateProductById(
            @PathVariable(value = "productId")Integer id,
            @RequestParam(value = "name")String name,
            @RequestParam(value = "description")String description,
            @RequestParam(value = "sku")String sku,
            @RequestParam(value = "manufacturer")String manufacturer,
            @RequestParam(value = "quantity")Integer quantity
    ) {

        logger.info("Updating product with ID: {}", id);

        statsDClient.incrementCounter("endpoint.homepage.http.put");


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User currUser = userDetails.getUser();
        Integer currUserId = currUser.getId();

        //find user by id
        Optional<Product> optionalProduct = repo.findById(id);
        Product product;

        if (optionalProduct.isPresent()) {
            product = optionalProduct.get();
        } else {
            logger.warn("Product with id {} not found.", id);
            return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
        }

        if (!currUserId.equals(product.getOwnerUserId())) {
            logger.warn("User with id {} is not authorized to update product with id {}.", currUserId, id);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        try {
            if (name == null
                    || description == null || sku == null || manufacturer == null || quantity == null
            ) {
                logger.warn("Product update failed due to invalid input. PUT requires all parameters");
                return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
            }

            product.setName(name);

            product.setDescription(description);

            product.setSku(sku);

            product.setManufacturer(manufacturer);

            if (quantity >= 0 && quantity <= 100)
                product.setQuantity(quantity);
            else {
                logger.warn("Product update failed due to invalid quantity input. Should be 0-100.");
                return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
            }

            repo.save(product);

            logger.info("Product with id {} updated successfully.", id);
            return new ResponseEntity<String>("Product update successfully!", HttpStatus.NO_CONTENT);
        }
        catch (DataIntegrityViolationException ex) {
            logger.error("Product update failed due to duplicate SKU.");
            throw new DuplicateSkuException("Product with SKU " + product.getSku() + " already exists");
        }
    }

    @PatchMapping(value = "/v1/product/{productId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> updateProductByIdByPatch(
            @PathVariable(value = "productId")Integer id,
            @RequestParam(value = "name", required = false)String name,
            @RequestParam(value = "description", required = false)String description,
            @RequestParam(value = "sku", required = false)String sku,
            @RequestParam(value = "manufacturer", required = false)String manufacturer,
            @RequestParam(value = "quantity", required = false)Integer quantity,
            Authentication authentication
    ) {
        logger.info("Updating product with id {}", id);

        statsDClient.incrementCounter("endpoint.homepage.http.patch");


        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User currUser = userDetails.getUser();
        Integer currUserId = currUser.getId();

        //find user by id
        Optional<Product> optionalProduct = repo.findById(id);
        Product product;

        if (optionalProduct.isPresent()) {
            product = optionalProduct.get();
        } else {
            logger.warn("Product with id {} not found", id);
            return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
        }

        if (!currUserId.equals(product.getOwnerUserId())) {
            logger.warn("User with id {} is not the owner of product with id {}", currUserId, id);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        try {
            if (name == null
                    && description == null && sku == null && manufacturer == null && quantity == null
            ){
                logger.warn("No updates provided for product with id {}", id);
                return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
            }

            //update user info
            if (name != null)
                product.setName(name);

            if (description != null)
                product.setDescription(description);

            if (sku != null)
                product.setSku(sku);

            if (manufacturer != null)
                product.setManufacturer(manufacturer);

            if (quantity != null) {
                if (quantity >= 0 && quantity <= 100)
                    product.setQuantity(quantity);
                else {
                    logger.warn("Product update failed due to invalid quantity input. Should be 0-100.");
                    return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
                }
            }
        } catch (DataIntegrityViolationException ex) {
            logger.error("Product update failed due to duplicate SKU.");
            throw new DuplicateSkuException("Product with SKU " + product.getSku() + " already exists");
        }

        repo.save(product);

        logger.info("Product with id {} updated successfully.", id);
        return new ResponseEntity<String>("Product updated successfully!", HttpStatus.NO_CONTENT);
    }
}