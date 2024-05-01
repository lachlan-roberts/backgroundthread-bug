/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.google.appengine.demos.guestbook;

import com.google.appengine.api.ThreadManager;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "BackendServlet2", urlPatterns = {"/backend2"})
public class BackendServlet2 extends HttpServlet {

    private final ExecutorService testThreadFactory;
    private final String threadFactoryMessage = "Task executed on thread factory";
    Logger log = Logger.getLogger(BackendServlet2.class.getName());

    public BackendServlet2() {
        testThreadFactory = //Executors.newSingleThreadExecutor(ThreadManager.backgroundThreadFactory());
                            Executors.newSingleThreadExecutor(ThreadManager.currentRequestThreadFactory());
    }

    @Override
    public void init(ServletConfig config) throws ServletException {

        super.init(config);
        log.info("Initializing BackendServlet");
    }

    private String runTask(ExecutorService service, String message) {
      String response = "";
      Future<String> future = service.submit(() -> {
        log.info("Returning message!");
        return message;
      });
      try {
          log.info("State Before "+ future.toString());
          response = future.get(10, TimeUnit.SECONDS);
          log.info("State After "+ future.toString());
      } catch (Exception e) {
          log.info("State Error "+ future.toString());
          log.log(Level.SEVERE, "Error callable on current thread factory", e);
      }
      log.info("Task finish "+ message);
      return response;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        log.info("Handling get request with path: " + req.getPathInfo());
        log.info(runTask(testThreadFactory, threadFactoryMessage));
        //shutdown(testThreadFactory);

        Thread worker = ThreadManager.createBackgroundThread(() -> {
          log.info("Running on background works!");
        });
        worker.start();

        resp.setStatus(200);
        resp.flushBuffer();
    }

}