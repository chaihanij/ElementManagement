/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.statproccessor.statgathermodule.http;

import org.apache.log4j.Logger;
import vos1.superinnova.engine.statproccessor.statgathermodule.StatGatherer;
import vos1.superinnova.engine.statproccessor.statgathermodule.util.http.HTTPReader;

/**
 *
 * @author HugeScreen
 */
public class HTTPStatGatherer extends StatGatherer {

    final static Logger logger = Logger.getLogger(HTTPStatGatherer.class);

    private String url = null;
    private int fetchTimeOut;

    public HTTPStatGatherer(String url, int fetchTimeOut) {
        this.url = url;
        this.fetchTimeOut = fetchTimeOut;
    }

    @Override
    public String gather() {

        this.gathererStatus = StatGatherer.STATUS_STARTED;
        try {
            logger.debug("Gatherer statistics for: " + this.url);
            this.gatherOutput = new HTTPReader().openConnection(this.url, this.fetchTimeOut);
            logger.debug("Output statistics  [" + this.gatherOutput + "]");

        } catch (Exception e) {
            logger.error("Gatherer statistics error :" + this.url);
            logger.error("Message error:" + e.getMessage());
            this.gathererStatus = StatGatherer.STATUS_ERROR;
        }
        this.gathererStatus = StatGatherer.STATUS_FINISHED;
        return this.gatherOutput;
    }
}
