package com.example.springsecurityexam.service;

public interface DeleteAccount {

    boolean support(String type);
    boolean delete(long userId, String value);
}
