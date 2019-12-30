package com.example.demo_translate_api.core.dto;

public class StringResponse {

    private String response;

    public StringResponse() {
    }

    public StringResponse(String s) {
        this.response = s;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
