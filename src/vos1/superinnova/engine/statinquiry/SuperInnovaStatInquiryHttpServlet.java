/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.statinquiry;

import java.io.IOException;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import vos1.superinnova.engine.statproccessor.SuperInnovaStatCore;
import vos1.superinnova.engine.statproccessor.SuperInnovaStatEngine;
import vos1.superinnova.engine.statsummarizer.StatSummarizationModule;
import vos1.superinnova.engine.statsummarizer.StatSummarizationSmartResultSet;
import vos1.superinnova.util.QueryStringUtil;

/**
 *
 * @author HugeScreen
 */
public class SuperInnovaStatInquiryHttpServlet extends HttpServlet{
    SuperInnovaStatCore superInnovaStatCore;
    public SuperInnovaStatInquiryHttpServlet(SuperInnovaStatCore superInnovaStatCore){
        this.superInnovaStatCore=superInnovaStatCore;
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        if(this.superInnovaStatCore!=null){
            //response.getWriter().println("SuperInnovaStat, It's work");
            Properties queryString=QueryStringUtil.convertQueryStringToProperties(request.getQueryString());
            if(queryString!=null){
                String engineName=queryString.getProperty("Engine");
//                //response.getWriter().println(QueryStringUtil.convertQueryStringToString(request.getQueryString(), "\n"));
                if(engineName!=null){
                    SuperInnovaStatEngine superInnovaStatEngine = this.superInnovaStatCore.getSuperInnovaStatEngine(engineName);
                    if(superInnovaStatEngine!=null){
                        //response.getWriter().println("*Found, Engine : "+superInnovaStatEngine.getSuperInnovaStatEngineConfiguration().getEngineName());
                        String statName=queryString.getProperty("StatName");
                        StatSummarizationModule statSummarizationModule = superInnovaStatEngine.getSuperInnovaStatProcessor().getStatSummarizationCore().getStatSummarizationModule(statName);
                        if(statSummarizationModule!=null)
                        {
                            //response.getWriter().println("*Found, StatName : "+statSummarizationModule.getStatSummarizerConfiguration().getStatName());
                            StatSummarizationSmartResultSet statSummarizationSmartResultSet = statSummarizationModule.getStatSummarizationSmartResultSet();
                            if(statSummarizationSmartResultSet!=null){
                                response.getWriter().println(statSummarizationSmartResultSet.getPRTGOutput(queryString));
                                
                            }
                            else{
                                response.getWriter().println("*Error : statSummarizationSmartResultSet is null");
                            }
                            
                        }
                        else{
                            response.getWriter().println("*Not Found, StatName : "+statName);
                        }
                    }
                    else{
                        response.getWriter().println("Error : superInnovaStatEngine is null.");
                    }
                }
                else{
                    response.getWriter().println("Error : Input Param is not Found.");
                }
            }
            else{
                response.getWriter().println("Error : This Servlet need queryString as Input.");
            }
        }
        else{
            response.getWriter().println("Error : SuperInnovaStatCore is null");
        }
    }
}
