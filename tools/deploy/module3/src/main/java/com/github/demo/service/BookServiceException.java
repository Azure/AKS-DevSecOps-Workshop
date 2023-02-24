package com.github.demo.service;

/**
 * Custom BookService Exception for caturing failures in building/starting the books service.
 */
public class BookServiceException extends Exception {

    private static final long serialVersionUID = 1L;

    public BookServiceException(Exception e) {
        super(e);
    }

    public BookServiceException(String message) {
        super(message);
    }
}
