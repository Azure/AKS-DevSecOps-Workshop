package com.github.demo.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A status page servlet that will report success if the application has been instantiated.
 * this provides a useful status check for containers and deployment purposes.
 */
public class StatusServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public StatusServlet() {
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text/html; charset=UTF-8");
        resp.getWriter().write("ok");
    }
}
