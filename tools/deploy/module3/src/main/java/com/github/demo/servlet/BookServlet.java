package com.github.demo.servlet;

import com.github.demo.model.Book;
import com.github.demo.service.BookService;
import com.github.demo.service.BookServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;


public class BookServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(BookServlet.class);

    private BookService bookService;

    public BookServlet() throws Exception {
        logger.info("Starting Bookstore Servlet...");
        try {
            bookService = new BookService();
        } catch (BookServiceException e) {
            logger.error("Failed to instantiate BookService: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Properties versionProperties = new Properties();
        versionProperties.load(getClass().getResourceAsStream("/version.properties"));

        ServletContextTemplateResolver resolver = new ServletContextTemplateResolver(req.getServletContext());
        resolver.setPrefix("/");
        resolver.setSuffix(".html");

        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(resolver);

        WebContext ctx = new WebContext(req, resp, getServletContext(), req.getLocale());
        ctx.setVariable("modified", Calendar.getInstance());
        ctx.setVariable("version", versionProperties.getProperty("version"));

        resp.setContentType("text/html; charset=UTF-8");

        try {
            List<Book> books = bookService.getBooks();
            ctx.setVariable("books", books);
            engine.process("books", ctx, resp.getWriter());
        } catch (BookServiceException e) {
            ctx.setVariable("error", e.getMessage());
            engine.process("error", ctx, resp.getWriter());
        }
    }
}
