/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.statinquiry;

import org.apache.log4j.Logger;
import vos1.superinnova.engine.statproccessor.SuperInnovaStatCore;
import vos1.superinnova.engine.statproccessor.SuperInnovaStatEngine;
import vos1.superinnova.engine.statsummarizer.StatSummarizationModule;
import vos1.superinnova.engine.statsummarizer.StatSummarizationSmartResultSet;
import vos1.superinnova.engine.statsummarizer.validate.CheckIfXMLIsWellFormed;
import vos1.superinnova.util.QueryStringUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

/**
 * @author HugeScreen
 */
public class SuperInnovaStatInquiryHttpServlet extends HttpServlet {

    final static Logger logger = Logger.getLogger(SuperInnovaStatInquiryHttpServlet.class);

    SuperInnovaStatCore superInnovaStatCore;

    public SuperInnovaStatInquiryHttpServlet(SuperInnovaStatCore superInnovaStatCore) {
        logger.debug("Init statistics in query");
        this.superInnovaStatCore = superInnovaStatCore;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Date date = new Date();
        response.setContentType("text/xml");
        if (this.superInnovaStatCore != null) {
            Properties queryString = QueryStringUtil.convertQueryStringToProperties(request.getQueryString());
            logger.debug("Interface : " + request.getQueryString());
            if (queryString != null) {
                String engineName = queryString.getProperty("Engine");
//                //response.getWriter().println(QueryStringUtil.convertQueryStringToString(request.getQueryString(), "\n"));
                if (engineName != null) {
                    SuperInnovaStatEngine superInnovaStatEngine = this.superInnovaStatCore.getSuperInnovaStatEngine(engineName);
                    if (superInnovaStatEngine != null) {
                        //response.getWriter().println("*Found, Engine : "+superInnovaStatEngine.getSuperInnovaStatEngineConfiguration().getEngineName());
                        String statName = queryString.getProperty("StatName");
                        StatSummarizationModule statSummarizationModule = superInnovaStatEngine.getSuperInnovaStatProcessor()
                                .getStatSummarizationCore()
                                .getStatSummarizationModule(statName);

                        if (statSummarizationModule != null) {

                            StatSummarizationSmartResultSet statSummarizationSmartResultSet = statSummarizationModule.getStatSummarizationSmartResultSet();

                            if (statSummarizationSmartResultSet != null) {
                                StringBuilder xmlOutPut = new StringBuilder();
                                xmlOutPut.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
                                xmlOutPut.append(statSummarizationSmartResultSet.getPRTGOutput(queryString));
                                CheckIfXMLIsWellFormed checkIfXMLIsWellFormed = new CheckIfXMLIsWellFormed(xmlOutPut.toString());

                                if (checkIfXMLIsWellFormed.isXML()) {
                                    response.getWriter().println(xmlOutPut.toString());
                                } else {
                                    response.getWriter().println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
                                    response.getWriter().println("<PRTG><text>[" + date.toString() + "] EM Tools builder data error</text></PRTG>");
                                }
                            } else {
                                String tmpString = "<?xml version=\"1.0\" encoding=\"utf-8\"?><PRTG><text>[" + date.toString() + "]No Raw Data Received for EM Tools and Please check configurations</text></PRTG>";
                                response.getWriter().println(tmpString);
                            }
                        } else {
                            String tmpString = "<?xml version=\"1.0\" encoding=\"utf-8\"?><PRTG><text>[" + date.toString() + "] EM tools parameter StatName : " + statName + " not found</text></PRTG>";
                            response.getWriter().println(tmpString);
                        }
                    } else {
                        String tmpString = "<?xml version=\"1.0\" encoding=\"utf-8\"?><PRTG><text>["+ date.toString() + "] EM tools parameter engine not found</text></PRTG>";
                        response.getWriter().println(tmpString);
                    }
                } else {
                    String tmpString = "<?xml version=\"1.0\" encoding=\"utf-8\"?><PRTG><text>[" + date.toString() + "] EM tools parameter is not found</text></PRTG>";
                    response.getWriter().println(tmpString);
                }
            } else {
                String tmpString = "<?xml version=\"1.0\" encoding=\"utf-8\"?><PRTG><text>[" + date.toString() + "] EM tools need to parameter in interface?</text></PRTG>";
                response.getWriter().println(tmpString);
            }
        } else {
            String tmpString = "<?xml version=\"1.0\" encoding=\"utf-8\"?><PRTG><text>[" + date.toString() + "] EM tools process is null</text></PRTG>";
            response.getWriter().println(tmpString);
        }
    }
}
