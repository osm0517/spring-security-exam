package com.example.springsecurityexam.service;

public interface DeleteAccount {

    boolean support(String type);
    boolean delete(String userId, String value);
}
