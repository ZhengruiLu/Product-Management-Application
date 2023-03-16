package com.csye6225.productmanager.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.sql.Timestamp;
import java.util.List;

@JsonIgnoreProperties({"owner_user"})
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Column(name = "description", nullable = false, length = 20)
    private String description;

    @Column(name = "sku", unique = true, nullable = false, length = 20)
    private String sku;

    @Column(name = "manufacturer", nullable = false, length = 20)
    private String manufacturer;

    @Min(0)
    @Max(100)
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "date_added", updatable = false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @CreationTimestamp
    private Timestamp date_added;

    // @Column(name = "date_last_updated", nullable = true, updatable = true, columnDefinition="on update current_timestamp")
    @UpdateTimestamp
    @Column(name = "date_last_updated")
    private Timestamp date_last_updated;

    @Column(name = "owner_user_id")
    private Integer ownerUserId;


    @ManyToOne
    @JoinColumn(name = "owner_user", nullable = false, updatable = false)
    private User user;

    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "image_product")
    private List<Image> images;

    public Integer getOwnerUserId() {
        return ownerUserId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        this.ownerUserId = user.getId();
    }



    public Product() {
    }



    public Product(Integer id, String name, String description, String sku, String manufacturer, Integer quantity, Timestamp date_added, Timestamp date_last_updated, Integer ownerUserId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.sku = sku;
        this.manufacturer = manufacturer;
        this.quantity = quantity;
        this.date_added = date_added;
        this.date_last_updated = date_last_updated;
        this.ownerUserId = ownerUserId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Timestamp getDate_added() {
        return date_added;
    }

    public void setDate_added(Timestamp date_added) {
        this.date_added = date_added;
    }

    public Timestamp getDate_last_updated() {
        return date_last_updated;
    }

    public void setDate_last_updated(Timestamp date_last_updated) {
        this.date_last_updated = date_last_updated;
    }

    public void setOwnerUserId(Integer ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }
}
