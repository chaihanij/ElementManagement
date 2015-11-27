/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.statinquiry;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import vos1.superinnova.engine.statproccessor.GlobalVariable;
import vos1.superinnova.engine.statproccessor.SuperInnovaStatCore;

/**
 * @author HugeScreen
 */
public class SuperInnovaStatInquiryServer extends Thread {

    final static Logger logger = Logger.getLogger(SuperInnovaStatInquiryServer.class);

    int portNumber = -1;
    SuperInnovaStatCore superInnovaStatCore;

    public SuperInnovaStatInquiryServer(SuperInnovaStatCore superInnovaStatCore, int portNumber) {
        this.superInnovaStatCore = superInnovaStatCore;
        this.portNumber = portNumber;
    }

    public void run() {
        try {
            startServer();
            logger.info("Start server success port : " + this.portNumber);
        } catch (Exception e) {
            logger.debug("Start server fail port : + " + this.portNumber);
            logger.error(e);
        }
    }

    public void startServer() throws Exception {

        Server server = new Server(this.portNumber);

        HandlerCollection handlers = new HandlerCollection();

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        handlers.addHandler(context);

        String _accessLogPath = GlobalVariable.BASE_LOG_PATH + "access_" + SuperInnovaStatCore.logName + "_" + this.portNumber + ".log";
        //        String _accessLogPath = GlobalVariable.BASE_LOG_PATH + "access_" + SuperInnovaStatCore.logName + "_" + this.portNumber + ".log";

//        String pwdDEV = "/Users/Wachirawat/Desktop/PresentationEM/Build/SuperInnovaStatEngine/log/";
//        String _accessLogPath = pwdDEV + "access_" + SuperInnovaStatCore.logName + "_" + this.portNumber + ".log";

        logger.info("access log path " + _accessLogPath);

        NCSARequestLog requestLog = new NCSARequestLog();
        requestLog.setFilename(_accessLogPath);
        requestLog.setFilenameDateFormat("yyyy_MM_dd");
        requestLog.setRetainDays(90);
        requestLog.setAppend(true);
        requestLog.setExtended(true);
        requestLog.setLogCookies(false);
        requestLog.setLogTimeZone("GMT+7");

        RequestLogHandler requestLogHandler = new RequestLogHandler();
        requestLogHandler.setRequestLog(requestLog);
        handlers.addHandler(requestLogHandler);

        // Add Inquiry Servlet
        SuperInnovaStatInquiryHttpServlet superInnovaStatInquiryHttpServlet = new SuperInnovaStatInquiryHttpServlet(this.superInnovaStatCore);
        LoglevelHttpServlet loglevelHttpServlet = new LoglevelHttpServlet();
        context.addServlet(new ServletHolder(superInnovaStatInquiryHttpServlet), "/inquiryStat/*");
        context.addServlet(new ServletHolder(loglevelHttpServlet), "/utils/*");

        server.setHandler(handlers);

        server.start();
        server.join();

    }


}
