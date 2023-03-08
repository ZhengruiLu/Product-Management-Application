package com.csye6225.productmanager.controller;

import com.csye6225.productmanager.config.DuplicateSkuException;
import com.csye6225.productmanager.entity.Product;
import com.csye6225.productmanager.entity.User;
import com.csye6225.productmanager.repository.ProductRepository;
import com.csye6225.productmanager.service.CustomUserDetails;
import com.csye6225.productmanager.service.ProductService;
import com.csye6225.productmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
public class ProductController {
    @Autowired
    private ProductService service;

    @Autowired
    private ProductRepository repo;


    @Autowired
    private UserService userService;



    @GetMapping(value = "/v1/product/{productId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Product> getProductById(
            @PathVariable(value = "productId")Integer id
    ) {
//        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
//        User currUser = userDetails.getUser();
//        Integer currUserId = currUser.getId();

        Optional<Product> optionalProduct = repo.findById(id);
        Product product;

        if (optionalProduct.isPresent()) {
            product = optionalProduct.get();
        } else {
            return new ResponseEntity<Product>(HttpStatus.FORBIDDEN);
        }

//        if (currUserId.equals(product.getOwnerUserId()))
        return new ResponseEntity<Product>(product, HttpStatus.OK);
//        else
//            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @DeleteMapping(value = "/v1/product/{productId}", produces = {MediaType.APPLICATION_JSON_VALUE})//, produces = "application/json"
    public ResponseEntity<String> deleteProductById(
            @PathVariable(value = "productId")Integer id,
            Authentication authentication
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer currUserId = userDetails.getUser().getId();

        //find user by id
        if (id == null || id < 0) {
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }

        Optional<Product> optionalProduct = repo.findById(id);
        Product product;

        if (optionalProduct.isPresent()) {
            product = optionalProduct.get();
        } else {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }

        if (!currUserId.equals(product.getOwnerUserId()))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        service.deleteById(id);

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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User currUser = userDetails.getUser();

        Product product = new Product();
//        System.out.println("authentication.getName(): " + authentication.getName());
//        System.out.println("userService.findByUserName(authentication.getName()): " + userService.findByUserName(authentication.getName()));
//        System.out.println("User Details: " + authentication.getDetails());

        try {
            product.setName(name);
            product.setDescription(description);
            product.setSku(sku);
            product.setManufacturer(manufacturer);
            product.setUser(currUser);

            if (quantity >= 0 && quantity <= 100)
                product.setQuantity(quantity);
            else
                return new ResponseEntity<Product>(HttpStatus.BAD_REQUEST);

            if (name == null || name == ""
                    || description == null || description == ""
                    || sku == null || sku == ""
                    || manufacturer == null || manufacturer == ""
            )
                return new ResponseEntity<Product>(HttpStatus.BAD_REQUEST);

            service.save(product);

            return new ResponseEntity<Product>(product, HttpStatus.CREATED);

        } catch (DataIntegrityViolationException ex) {
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
            return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
        }

        if (!currUserId.equals(product.getOwnerUserId()))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        try {
            if (name == null
                    || description == null || sku == null || manufacturer == null || quantity == null
            )
                return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);


            //update user info
//            if (name != null)
                product.setName(name);

//            if (description != null)
                product.setDescription(description);

//            if (sku != null)
                product.setSku(sku);

//            if (manufacturer != null)
                product.setManufacturer(manufacturer);

//            if (quantity != null) {
                if (quantity >= 0 && quantity <= 100)
                    product.setQuantity(quantity);
                else
                    return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
//            }

            repo.save(product);

            return new ResponseEntity<String>("Product update successfully!", HttpStatus.NO_CONTENT);
        }
        catch (DataIntegrityViolationException ex) {
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
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User currUser = userDetails.getUser();
        Integer currUserId = currUser.getId();

        //find user by id
        Optional<Product> optionalProduct = repo.findById(id);
        Product product;

        if (optionalProduct.isPresent()) {
            product = optionalProduct.get();
        } else {
            return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
        }

        if (!currUserId.equals(product.getOwnerUserId()))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        try {
            if (name == null
                    && description == null && sku == null && manufacturer == null && quantity == null
            )
                return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);

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
                else
                    return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
            }
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateSkuException("Product with SKU " + product.getSku() + " already exists");
        }

        repo.save(product);

        return new ResponseEntity<String>("Product updated successfully!", HttpStatus.NO_CONTENT);
    }
}
