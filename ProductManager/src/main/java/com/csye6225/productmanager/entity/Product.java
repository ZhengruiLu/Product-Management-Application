package com.csye6225.productmanager.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.sql.Timestamp;

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
    private Integer owner_user_id;

    public Product() {
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

    public Integer getOwner_user_id() {
        return owner_user_id;
    }

    public void setOwner_user_id(Integer owner_user_id) {
        this.owner_user_id = owner_user_id;
    }
}
