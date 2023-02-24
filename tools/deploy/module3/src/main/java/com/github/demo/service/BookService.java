package com.github.demo.service;

import com.github.demo.model.Book;

import java.util.List;

public class BookService {

    private BookDatabase booksDatabase;

    public BookService() throws BookServiceException {
        String databaseUrl = System.getenv("DATABASE_URL");
        String databaseUser = System.getenv("DATABASE_USER");
        String databasePassword = System.getenv("DATABASE_PASSWORD");

        try {
            booksDatabase = new BookDatabaseImpl(databaseUrl, databaseUser, databasePassword);
        } catch (BookServiceException e) {
            throw new BookServiceException(e);
        }
    }

    public List<Book> getBooks() throws BookServiceException {
        return this.booksDatabase.getAll();
    }

    public List<Book> searchBooks(String name) throws BookServiceException {
        return this.booksDatabase.getBooksByTitle(name);
    }
}