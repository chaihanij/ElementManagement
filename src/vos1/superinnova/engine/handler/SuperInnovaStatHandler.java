/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.handler;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import vos1.superinnova.engine.statproccessor.SuperInnovaStatEngine;
import vos1.superinnova.engine.statproccessor.simulator.OCFEngine;
import vos1.superinnova.engine.statproccessor.simulator.PCRFEngine;

/**
 *
 * @author HugeScreen
 */
public class SuperInnovaStatHandler {
    protected SuperInnovaStatEngine[] superInnovaStatEngine=null;
    public SuperInnovaStatHandler(){
        this.superInnovaStatEngine=new SuperInnovaStatEngine[2];
        this.superInnovaStatEngine[0] = new OCFEngine();
        this.superInnovaStatEngine[1] = new PCRFEngine();
    }
    public void test(){
        for(int i=0;i<superInnovaStatEngine.length;i++){
            System.out.println(this.superInnovaStatEngine[i].getTextResponse("Hello"));
        }
    }
    
    public ServletContextHandler getServletContextHandler(){
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.addServlet(new ServletHolder(new SuperInnovaStatHandlerHTTPServlet(this)),"/query/*");
    
        return context;
    }
 
    public static void main(String[] args){
        System.out.println("Hello World");
        vos1.superinnova.engine.statproccessor.simulator.QueryHandler queryHandler = new vos1.superinnova.engine.statproccessor.simulator.QueryHandler();
        queryHandler.test();
    }
}

class SuperInnovaStatHandlerHTTPServlet extends HttpServlet
{
    SuperInnovaStatHandler superInnovaStatHandler=null;
    String greeting = "Hello";

    public SuperInnovaStatHandlerHTTPServlet(SuperInnovaStatHandler superInnovaStatHandler)
    {
        this.superInnovaStatHandler = superInnovaStatHandler;
    }

    public SuperInnovaStatHandlerHTTPServlet(String hi)
    {
        greeting = hi;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("Input Parameter + "+request.getQueryString()+"<br>");
        String queryString=request.getQueryString();
        String[] queryStringArray=queryString.split("&");
        
        String targetEngine="";
        for(int i=0;i<queryStringArray.length;i++){
            String[] AVP = queryStringArray[i].split("=");
            if(AVP[0]!=null && AVP[0].compareToIgnoreCase("engine")==0 && AVP[1]!=null){
                targetEngine=AVP[1];
                break;
            }
        }
        response.getWriter().println("targetEngine=" + targetEngine +"<br>");
        if(targetEngine.compareToIgnoreCase("OCF")==0){
            response.getWriter().print("Text Response : "+this.superInnovaStatHandler.superInnovaStatEngine[0].getTextResponse("Hello")+"<br>");
        }
        else if(targetEngine.compareToIgnoreCase("PCRF")==0){
            response.getWriter().print("Text Response : "+this.superInnovaStatHandler.superInnovaStatEngine[1].getTextResponse("Hello")+"<br>");
        }
        
        response.getWriter().println("session=" + request.getSession(true).getId());
    }
}
