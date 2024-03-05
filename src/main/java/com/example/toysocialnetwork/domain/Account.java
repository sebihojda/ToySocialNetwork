package com.example.toysocialnetwork.domain;

import java.time.LocalDateTime;

public class Account extends Entity<Long>{

    private Long user_id;
    private String username;
    private String password;
    private String state = null;
    private LocalDateTime registered_date;
    private LocalDateTime locked_date = null;
    private Long locked_time = 0L;

    public Account(Long user_id, String username, String password, LocalDateTime registered_date) {
        this.user_id = user_id;
        this.username = username;
        this.password = password;
        this.registered_date = registered_date;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public LocalDateTime getRegistered_date() {
        return registered_date;
    }

    public void setRegistered_date(LocalDateTime registered_date) {
        this.registered_date = registered_date;
    }

    public LocalDateTime getLocked_date() {
        return locked_date;
    }

    public void setLocked_date(LocalDateTime locked_date) {
        this.locked_date = locked_date;
    }

    public Long getLocked_time() {
        return locked_time;
    }

    public void setLocked_time(Long locked_time) {
        this.locked_time = locked_time;
    }
}
