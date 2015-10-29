/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.httpserver;
import java.io.IOException;
import org.eclipse.jetty.server.Server;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import vos1.superinnova.engine.statproccessor.simulator.QueryHandler;
/**
 *
 * @author HugeScreen
 */
public class HTTPStatQueryServer {
    public static void main(String[] args) throws Exception
    {
        Server server = new Server(8016);
        
        QueryHandler qh = new QueryHandler();
        server.setHandler(qh.getServletContextHandler());
        server.start();
        server.join();
    }    
}
