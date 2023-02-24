package com.github.demo;

import java.net.URL;
import java.net.URI;

import com.github.demo.servlet.*;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoServer {

    private static final Logger logger = LoggerFactory.getLogger(DemoServer.class);

    public static void main(String[] args) throws Exception {
        String portString = System.getenv("SERVER_PORT");
        if (portString == null) {
            portString = "8080";
        }

        int port = Integer.parseInt(portString);
        Server server = new Server(port);

        URI webRootUri;
        String staticResources = System.getenv("STATIC_RESOURCES");

        if (staticResources != null) {
            logger.info("Using environment variable for static resources; {}", staticResources);
            webRootUri = URI.create("file://" + staticResources);
        } else {
            logger.info("Using embedded static resources");
            URL url = DemoServer.class.getClassLoader().getResource("static/");
            webRootUri = url.toURI();
        }
        logger.info("Webserver static resources: {}", webRootUri);

        ServletContextHandler ctxHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        ctxHandler.setContextPath("/");
        ctxHandler.setBaseResource(Resource.newResource(webRootUri));

        ServletHolder staticFiles = new ServletHolder("static-home", DefaultServlet.class);
        staticFiles.setInitParameter("resourceBase", webRootUri.toString());
        staticFiles.setInitParameter("dirAllowed","true");
        staticFiles.setInitParameter("pathInfoOnly","true");
        ctxHandler.addServlet(staticFiles,"/static/*");

        ctxHandler.addServlet(StatusServlet.class, "/status");

        // Default servlet path, must be last
        ServletHolder books = new ServletHolder("/", BookServlet.class);
        books.setInitParameter("dirAllowed","false");
        books.setInitParameter("pathInfoOnly","true");
        ctxHandler.addServlet(books, "/");

        server.setHandler(ctxHandler);

        server.start();

        try {
            URI serverUri = server.getURI();
            if (serverUri != null) {
                logServerWithUri(serverUri);
            } else {
                logServerStartWithUnresolvedUri(port);
            }
        } catch (Exception e) {
            logger.error("Failure resolving URI: {}", e.getMessage(), e);
            logServerStartWithUnresolvedUri(port);
        }
        server.join();
    }

    private static void logServerStartWithUnresolvedUri(int port) {
        logger.info("**********************************************************************************************************");
        logger.info("Started DemoServer, but server URI could not be determined, try accessing on http://localhost:{}", port);
        logger.info("**********************************************************************************************************");
    }

    private static void logServerWithUri(URI serverUri) {
        logger.info("**********************************************************************************************************");
        logger.info("Started DemoServer; available at: {}://localhost:{}", serverUri.getScheme(), serverUri.getPort());
        logger.info("**********************************************************************************************************");
    }
}