/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.statinquiry;

import org.apache.log4j.Logger;
import vos1.superinnova.engine.statproccessor.SuperInnovaStatCore;

import java.util.Properties;

/**
 *
 * @author HugeScreen
 */
public class SuperInnovaStatInquiryCore {
    final static Logger logger = Logger.getLogger(SuperInnovaStatInquiryCore.class);
    SuperInnovaStatCore superInnovaStatCore=null;
    SuperInnovaStatInquiryServer[] superInnovaStatInquiryServerArray;
    public SuperInnovaStatInquiryCore(SuperInnovaStatCore superInnovaStatCore){
        this.superInnovaStatCore=superInnovaStatCore;
        initInquiryCore();
    }
    private void initInquiryCore(){
        if(this.superInnovaStatCore!=null){
            Properties superInnovaStatInquiryCoreConfiguration = this.superInnovaStatCore.getEngineCoreConfiguration();
            String inquiryPortString=superInnovaStatInquiryCoreConfiguration.getProperty("STAT_INQUIRY_PORT");
            logger.info("STAT_INQUIRY_PORT Port:" + inquiryPortString );
            String[] inquiryPortStringArray=inquiryPortString.split("\\|");
            superInnovaStatInquiryServerArray = new SuperInnovaStatInquiryServer[inquiryPortStringArray.length];
            for(int i=0;i<inquiryPortStringArray.length;i++){
                int portNumber=Integer.parseInt(inquiryPortStringArray[i]);
                superInnovaStatInquiryServerArray[i]= new SuperInnovaStatInquiryServer(this.superInnovaStatCore,portNumber);
                superInnovaStatInquiryServerArray[i].start();
            } // end for i
        }// end if SuperInnovaStatCore != null
    }
    
}
