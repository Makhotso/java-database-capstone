package com.project.back_end.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String username;

    @NotNull
    private String email; //  FIXED (added NotNull)

    @NotNull
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    // No-args constructor
    public Admin() {}

    //  FIXED constructor (includes email)
    public Admin(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {   //  ADD THIS
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {   //  ADD THIS
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}