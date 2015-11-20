package vos1.superinnova.engine.statproccessor;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.util.Properties;

/**
 * Created by Wachirawat on 11/18/15 AD.
 */
public class LogConfiguration {

    final static Logger logger = Logger.getLogger(LogConfiguration.class);
    public static Properties proLog4j = new Properties();
    public static void initialLogConfiguration(String logName, String logLevel) {

        /**
         Levels used for identifying the severity of an event. Levels are organized from most specific to least:
         OFF (most specific)
         FATAL
         ERROR
         WARN
         INFO
         DEBUG
         TRACE
         ALL (least specific)
         */


        proLog4j.setProperty("log4j.rootLogger", "INFO, file");
        if (logLevel != null) {
            if (logLevel.toUpperCase().equals("FATAL") || logLevel.toUpperCase().equals("ERROR")
                    || logLevel.toUpperCase().equals("WARN") || logLevel.toUpperCase().equals("INFO")
                    || logLevel.toUpperCase().equals("DEBUG") || logLevel.toUpperCase().equals("TRACE")
                    || logLevel.toUpperCase().equals("OFF")) {
                proLog4j.setProperty("log4j.rootLogger", logLevel.toLowerCase() + ", file");
            }
        }

//        // Redirect log messages to console
//        proLog4j.setProperty("log4j.appender.stdout", "org.apache.log4j.ConsoleAppender");
//        proLog4j.setProperty("log4j.appender.stdout.Target", "System.out");
//        proLog4j.setProperty("log4j.appender.stdout.layout", "org.apache.log4j.PatternLayout");
//        proLog4j.setProperty("log4j.appender.stdout.layout.ConversionPattern", "%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n");

        // Redirect log messages to a log file, support file rolling.

        proLog4j.setProperty("log4j.appender.file", "org.apache.log4j.DailyRollingFileAppender");
//        proLog4j.setProperty("log4j.appender.file.File", "/Users/Wachirawat/Desktop/PresentationEM/Build/SuperInnovaStatEngine/log/" + logName + ".log");

        proLog4j.setProperty("log4j.appender.file.File", GlobalVariable.BASE_LOG_PATH  + logName +".log");
        proLog4j.setProperty("log4j.appender.file.DatePattern", "'.'yyyy-MM-dd");
        proLog4j.setProperty("log4j.appender.file.layout", "org.apache.log4j.PatternLayout");
        proLog4j.setProperty("log4j.appender.file.layout.ConversionPattern", "%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n");
        PropertyConfigurator.configure(proLog4j);
    }

    public static void setRootLogger(String logLevel){
        if (logLevel != null) {
            if (logLevel.toUpperCase().equals("FATAL") || logLevel.toUpperCase().equals("ERROR")
                    || logLevel.toUpperCase().equals("WARN") || logLevel.toUpperCase().equals("INFO")
                    || logLevel.toUpperCase().equals("DEBUG") || logLevel.toUpperCase().equals("TRACE")
                    || logLevel.toUpperCase().equals("OFF")) {
                proLog4j.setProperty("log4j.rootLogger", logLevel.toLowerCase() + ", file");
            }
            PropertyConfigurator.configure(proLog4j);
        }
    }
}
