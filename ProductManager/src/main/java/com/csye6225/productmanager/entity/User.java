package com.csye6225.productmanager.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "user")
public class User {

    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER,mappedBy = "user")
    private List<Product> product;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "first_name", nullable = false, length = 20)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 20)
    private String lastName;
    @Column(nullable = false, length = 64)
    private String password;
    @Column(nullable = false, length = 45, unique = true)
    private String username;

    @Column(name = "account_created", updatable = false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @CreationTimestamp
    private Timestamp account_created;
    @Column(name = "account_updated")
    @UpdateTimestamp
    private Timestamp account_updated;

    public Integer getId() {
        return id;
    }

    // remaining getters and setters are not shown for brevity

    public User() {

    }

    public User(Integer id, String firstName, String lastName, String username, Timestamp account_created, Timestamp account_updated) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.account_created = account_created;
        this.account_updated = account_updated;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Timestamp getAccount_created() {
        return account_created;
    }

    public void setAccount_created(Timestamp account_created) {
        this.account_created = account_created;
    }

    public Timestamp getAccount_updated() {
        return account_updated;
    }

    public void setAccount_updated(Timestamp account_updated) {
        this.account_updated = account_updated;
    }

    public List<Product> getProduct() {
        return product;
    }

    public void setProduct(List<Product> product) {
        this.product = product;
    }
}
