/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.statinquiry;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import vos1.superinnova.engine.statproccessor.SuperInnovaStatCore;

/**
 *
 * @author HugeScreen
 */
public class SuperInnovaStatInquiryServer extends Thread{
    int portNumber=-1;
    SuperInnovaStatCore superInnovaStatCore;
    public SuperInnovaStatInquiryServer(SuperInnovaStatCore superInnovaStatCore,int portNumber){
        this.superInnovaStatCore=superInnovaStatCore;
        this.portNumber=portNumber;
    }
    
    public void run(){
        startServer();
    }
    public void startServer(){

        Server server = new Server(this.portNumber);
        
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        
        // Add Inquiry Servlet
        SuperInnovaStatInquiryHttpServlet superInnovaStatInquiryHttpServlet = new SuperInnovaStatInquiryHttpServlet(this.superInnovaStatCore);
        context.addServlet(new ServletHolder(superInnovaStatInquiryHttpServlet),"/inquiryStat/*");
        
        try{
            server.start();
            server.join();        
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }    
    
}
