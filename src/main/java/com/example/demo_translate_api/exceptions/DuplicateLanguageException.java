package com.example.demo_translate_api.exceptions;

public class DuplicateLanguageException extends RuntimeException {

    public DuplicateLanguageException(String message) {
        super(message);
    }
}
