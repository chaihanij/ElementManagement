/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.statsummarizer.validate;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

/**
 * @author Wachirawat
 */
public class CheckIfXMLIsWellFormed {
    final static Logger logger = Logger.getLogger(CheckIfXMLIsWellFormed.class);

    private String xmlString;

    public CheckIfXMLIsWellFormed(String xmlString) {
        this.xmlString = xmlString;
    }

    public Boolean isXML() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new SimpleErrorHandler());
            InputSource is = new InputSource(new StringReader(this.xmlString));
            builder.parse(is);
            return true;
        } catch (Exception e) {
            logger.error("XML output is not wellformed.");
            return false;
        }
    }
}
