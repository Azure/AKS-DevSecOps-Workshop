package com.github.demo.service;

import com.github.demo.model.Book;

import java.util.List;
import java.util.Collection;

public interface BookDatabase {

    List<Book> getAll() throws BookServiceException;

    List<Book> getBooksByTitle(String name) throws BookServiceException;

    void populate(Collection<Book> books) throws BookServiceException;

    void destroy() throws BookServiceException;
}