package com.example.springsecurityexam.enumdata;

public enum ItemReportReason {

    FALSEHOOD("허위매물"), ETC("기타");

    private final String description;

    ItemReportReason(String description){
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
