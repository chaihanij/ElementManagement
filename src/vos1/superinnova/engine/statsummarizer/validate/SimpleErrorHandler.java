package vos1.superinnova.engine.statsummarizer.validate;


import org.apache.log4j.Logger;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Created by Wachirawat on 12/15/2015 AD.
 */
public class SimpleErrorHandler implements ErrorHandler {
    final static Logger logger = Logger.getLogger(SimpleErrorHandler.class);

    public void warning(SAXParseException e) throws SAXException {
        logger.warn(e.getMessage());
    }

    public void error(SAXParseException e) throws SAXException {
        logger.error(e.getMessage());
    }

    public void fatalError(SAXParseException e) throws SAXException {
        logger.fatal(e.getMessage());
    }
}