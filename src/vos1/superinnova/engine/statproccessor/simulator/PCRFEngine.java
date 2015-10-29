/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.statproccessor.simulator;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import vos1.superinnova.engine.statproccessor.SuperInnovaStatEngine;



/**
 *
 * @author HugeScreen
 */
public class PCRFEngine extends SuperInnovaStatEngine{
    public String getTextResponse(String input){
        return "I'm not OCF, But I'm PCRF";
    }    
    
    @Override
    public void run(){
        
    }

    @Override
    public String getTextResponse(Properties inputParam) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
