package com.relatos.catalogue.exception;

@SuppressWarnings("serial")
public class DuplicateBookException extends RuntimeException {
    public DuplicateBookException(String message) {
        super(message);
    }
}