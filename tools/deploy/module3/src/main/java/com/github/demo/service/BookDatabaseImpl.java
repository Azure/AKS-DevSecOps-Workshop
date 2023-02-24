package com.github.demo.service;

import com.github.demo.model.Book;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Properties;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

public class BookDatabaseImpl implements BookDatabase {

    public static final Logger logger = LoggerFactory.getLogger(BookDatabaseImpl.class);

    /** The maximum number of retries for connecting with the database. */
    private static final int MAX_CONNECTION_RETRIES = 10;

    /** The time in ms to backoff on a connection failure to the database. */
    private static final int RETRY_BACKOFF = 3000;

    /** The connection for the database. */
    private Connection connection;

    /**
     * Obtain the maximum number of database connection retires before giving up
     * @return The number of times to attempt to reconnect to the database.
     */
    private static final int getMaxRetries() {
        String retries = System.getenv("DATABASE_RETRIES");
        if (retries != null) {
            return Integer.parseInt(retries);
        } else {
            return MAX_CONNECTION_RETRIES;
        }
    }

    public BookDatabaseImpl() throws BookServiceException {
        this(null, null, null);
    }

    public BookDatabaseImpl(String url, String username, String password) throws BookServiceException {
        Properties props = new Properties();

        if (username != null) {
            props.setProperty("user", username);
        }
        if (password != null) {
            props.setProperty("password", password);
        }
        // This is a postgres specific setting, but SQLlite tolerates it
        props.setProperty("ssl", "false");

        // Default to a sqlite in memory database if no database url has been provided
        if (url == null) {
            url = "jdbc:sqlite::memory:";
        }

        connection = getConnection(url, props);

        // Populate our in-memory database with data, if this is what we are
        if (connection != null && url.indexOf(":memory:") > -1) {
            initializeAndPopulateDatabase();
        }
    }

    public boolean isValid() {
        return connection != null;
    }

    @Override
    public List<Book> getAll() throws BookServiceException {
        List<Book> books = new ArrayList<Book>();

        if (!isValid()) {
            throw new BookServiceException("Database connection is not valid, check logs for failure details.");
        }

        try {
            Statement stmt = connection.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT * FROM books");
            while (rs.next()) {
                Book book = new Book(
                    rs.getString("author"),
                    rs.getString("title"),
                    rs.getString("image")
                );
                books.add(book);
            }
        } catch (SQLException e) {
            logger.error("Failed to obtain books", e);
            throw new BookServiceException(e);
        }
        return books;
    }

    @Override
    public List<Book> getBooksByTitle(String name) throws BookServiceException {
        List<Book> books = new ArrayList<Book>();

        if (!isValid()) {
            throw new BookServiceException("Database connection is not valid, check logs for failure details.");
        }

        Statement stmt = null;

        try {
            stmt = connection.createStatement();
            String query = "SELECT * FROM books WHERE title LIKE '%" + name + "%'";

            ResultSet results = stmt.executeQuery(query);

            while (results.next()) {
                Book book = new Book(
                    results.getString("author"),
                    results.getString("title"),
                    results.getString("image")
                );
                books.add(book);
            }
        } catch (SQLException e) {
            logger.error("Failed while searching for {}'", name);
            throw new BookServiceException(e);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se) {
                // Do nothing
            } finally {
                stmt = null;
            }
        }
        return books;
    }

    @Override
    public void destroy() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            // Ignore
            connection = null;
        }
    }

    @Override
    public void populate(Collection<Book> books) throws BookServiceException {
        if (books != null && books.size() > 0) {
            PreparedStatement ps = null;

            try {
                ps = connection.prepareStatement("INSERT INTO books (title, author, image, rating) VALUES(?, ?, ?, ?)");

                for (Book book : books) {
                    logger.info("Adding book to database: {}", book.getTitle());
                    ps.setString(1, book.getTitle());
                    ps.setString(2, book.getAuthor());
                    ps.setString(3, book.getCover());
                    ps.execute();
                }
                logger.info("Database populated.");
            } catch (SQLException se) {
                logger.error("Failure when populating database", se);
                throw new BookServiceException(se);
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                } catch (SQLException e) {
                    // Do nothing
                    ps = null;
                }
            }
        }
    }

    /**
     * Initializes the internal database structure and populates it with our default
     * data.
     */
    private void initializeAndPopulateDatabase() throws BookServiceException {
        Statement statement = null;
        try {
            // Initialize the database tables for in memory database
            statement = connection.createStatement();

            statement.execute("CREATE TABLE IF NOT EXISTS books ("
                + "id INTEGER PRIMARY KEY, "
                + "title TEXT NOT NULL, "
                + "author TEXT, "
                + "image TEXT, "
                + "rating, INTEGER "
                + ")"
            );
            // Populate the database with some sample data
            populate(BookUtils.getSampleBooks());
        } catch (SQLException e) {
            if (statement != null) {
                try {
                    statement.close();
                } catch(SQLException se) {
                    // ignore
                }
            }
            throw new BookServiceException(e);
        }
    }

    private Connection getConnection(String url, Properties props) throws BookServiceException {
        Connection connection = null;
        int retryCount = 0;
        int maxRetries = getMaxRetries();

        logger.debug("Connecting to database: " + url);

        do {
            try {
                connection = DriverManager.getConnection(url, props);
            } catch (SQLException e) {
                retryCount++;

                logger.warn("Failed to connect to database, reties: " + retryCount);
                logger.warn(e.getMessage());

                try {
                    logger.info("Backing off before retrying database connection for " + RETRY_BACKOFF + "ms.");
                    Thread.sleep(RETRY_BACKOFF);
                } catch (InterruptedException e1) {
                    logger.error("Failed to sleep: " + e1.getMessage());
                }
            }
        }
        while (connection == null && retryCount < maxRetries);

        logger.info("Database Connection successful? " + (connection != null));
        return connection;
    }
}
