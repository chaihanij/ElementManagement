package vos1.superinnova.engine.statproccessor;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Properties;

/**
 * Created by Wachirawat on 11/18/15 AD.
 */
public class Common {
    final static Logger logger = Logger.getLogger(Common.class);

    public static String getPropertyAsString(Properties prop) {

        StringWriter writer = new StringWriter();
        try {
            prop.store(writer, "");
        } catch (IOException e) {
            logger.error(e);
        }
        return writer.getBuffer().toString();
    }
}
