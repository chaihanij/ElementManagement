package vos1.superinnova.engine.statinquiry;

import org.apache.log4j.Logger;
import vos1.superinnova.engine.statproccessor.LogConfiguration;
import vos1.superinnova.util.QueryStringUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Wachirawat on 11/19/15 AD.
 */
public class LoglevelHttpServlet extends HttpServlet {

    final static Logger logger = Logger.getLogger(LoglevelHttpServlet.class);

    private String help = "Usage: /utils/?LOG_LEVEL=[param]\n" +
            "\tparam\tDescription\n" +
            "\t\tLevels used for identifying the severity of an event. Levels are organized from most specific to least" +
            "\tFATAL\t\n" +
            "\tERROR\t\n" +
            "\tWARN\t\n" +
            "\tINFO\t\n" +
            "\tDEBUG\t\n";

    public LoglevelHttpServlet() {

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Properties queryString = QueryStringUtil.convertQueryStringToProperties(req.getQueryString());
        if (queryString != null) {
            String level = null;
            level = queryString.getProperty("LOG_LEVEL").trim();
            if (level != null) {
                logger.info("Change log level : " + level);
                if (level.equals("FATAL")) {
                    LogConfiguration.setRootLogger("FATAL");
                    resp.getWriter().println("LOG LEVEL");
                    resp.getWriter().println("FATAL");
                } else if (level.equals("ERROR")) {
                    LogConfiguration.setRootLogger("ERROR");
                    resp.getWriter().println("LOG LEVEL");
                    resp.getWriter().println("FATAL|ERROR");
                } else if (level.equals("WARN")) {
                    LogConfiguration.setRootLogger("WARN");
                    resp.getWriter().println("FATAL|ERROR|WARN");
                } else if (level.equals("INFO")) {
                    LogConfiguration.setRootLogger("INFO");
                    resp.getWriter().println("FATAL|ERROR|WARN|INFO");
                } else if (level.equals("DEBUG")) {
                    LogConfiguration.setRootLogger("DEBUG");
                    resp.getWriter().println("FATAL|ERROR|WARN|INFO");
                } else {
                    resp.getWriter().println(help);
                }
            }
        } else {
            resp.getWriter().println(help);
        }
    }
}
