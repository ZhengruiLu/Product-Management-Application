package com.csye6225.productmanager.controller;

import com.csye6225.productmanager.config.DuplicateSkuException;
import com.csye6225.productmanager.entity.Product;
import com.csye6225.productmanager.repository.ProductRepository;
import com.csye6225.productmanager.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.support.QuerydslJpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@RestController
public class ProductController {
    @Autowired
    private ProductService service;

    @Autowired
    private ProductRepository repo;

    @GetMapping(value = "/v1/product/{productId}", produces = {MediaType.APPLICATION_JSON_VALUE})//, produces = "application/json"
    public ResponseEntity<Product> getProductById(
            @PathVariable(value = "productId")Integer id
    ) {
        Optional<Product> optionalProduct = repo.findById(id);
        Product product;

        if (optionalProduct.isPresent()) {
            product = optionalProduct.get();
        } else {
            return new ResponseEntity<Product>(HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<Product>(product, HttpStatus.OK);
    }

    @DeleteMapping(value = "/v1/product/{productId}", produces = {MediaType.APPLICATION_JSON_VALUE})//, produces = "application/json"
    public ResponseEntity<String> deleteProductById(
            @PathVariable(value = "productId")Integer id
    ) {
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
        Product product = new Product();
        try {
            product.setName(name);
            product.setDescription(description);
            product.setSku(sku);
            product.setManufacturer(manufacturer);

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
            @RequestParam(value = "name", required = false)String name,
            @RequestParam(value = "description", required = false)String description,
            @RequestParam(value = "sku", required = false)String sku,
            @RequestParam(value = "manufacturer", required = false)String manufacturer,
            @RequestParam(value = "quantity", required = false)Integer quantity
    ) {
        //find user by id
        Optional<Product> optionalProduct = repo.findById(id);
        Product product;

        if (optionalProduct.isPresent()) {
            product = optionalProduct.get();
        } else {
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }

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
            @RequestParam(value = "quantity", required = false)Integer quantity
    ) {
        //find user by id
        Optional<Product> optionalProduct = repo.findById(id);
        Product product;

        if (optionalProduct.isPresent()) {
            product = optionalProduct.get();
        } else {
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }

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

        repo.save(product);

        return new ResponseEntity<String>("Product update successfully!", HttpStatus.NO_CONTENT);
    }


}
