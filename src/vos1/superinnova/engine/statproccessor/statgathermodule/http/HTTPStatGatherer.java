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
public class HTTPStatGatherer extends StatGatherer{
    final static Logger logger = Logger.getLogger(HTTPStatGatherer.class);

    String url=null;
    public HTTPStatGatherer(String url){
        this.url=url;
    }
    public String gather(){
        //System.out.println("GATHER Call");
        this.gathererStatus=StatGatherer.STATUS_STARTED;
        try{
            this.gatherOutput=HTTPReader.openConnection(this.url);
//            logger.debug("Output [" + this.gatherOutput + "]");
            //System.out.println(this.gatherOutput);
        }
        catch(Exception e){
            logger.error("Get statistic error :" + this.url);
            this.gathererStatus=StatGatherer.STATUS_ERROR;
        }
        this.gathererStatus=StatGatherer.STATUS_FINISHED;
        //System.out.println("GATHER OUTPUT :"+this.gatherOutput);
        return this.gatherOutput;
        
    }
    public static void main(String[] args){
        StatGatherer sg = new HTTPStatGatherer("http://localhost:9016/equinoxStat?nodeType=OCF&hostname=OCF201");
        sg.gather();
        System.out.println(sg.getGatherOutput());
        
    }
}
