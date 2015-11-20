/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.statproccessor.predefinedengine;

import org.apache.log4j.Logger;
import vos1.superinnova.engine.statproccessor.SuperInnovaStatCore;
import vos1.superinnova.engine.statproccessor.SuperInnovaStatEngine;
import vos1.superinnova.engine.statproccessor.SuperInnovaStatEngineConfiguration;
import vos1.superinnova.engine.statproccessor.predefinedtype.GeneralSuperNovaStatProcessor;
import vos1.superinnova.engine.statproccessor.statgathermodule.StatGatherConfiguration;
import vos1.superinnova.engine.statsummarizer.StatSummarizerConfiguration;

import java.util.Properties;

/**
 *
 * @author HugeScreen
 */
public class GeneralSuperInnovaStatEngine extends SuperInnovaStatEngine {

    final static Logger logger = Logger.getLogger(GeneralSuperInnovaStatEngine.class);

    public static final String SITE_KEYWORD="SITE";
    public static final String BLOCK_KEYWORD="BLOCK";
    public static final String SUBBLOCK_KEYWORD="SUBBLOCK";
    public static final String UNKNOWN_KEYWORD="UNKNOWN";
    public static final String SITE_REVERSE_KEYWORD="SITE-REVERSE";
    public static final String BLOCK_REVERSE_KEYWORD="BLOCK-REVERSE";
    public static final String SUBBLOCK_REVERSE_KEYWORD="SUBBLOCK-REVERSE";
    public static final String ENGINE_KEYWORD="ENGINE";
    public static final String BLOCK_PARENT_KEYWORD="BLOCK-PARENT";
    public static final String SUBBLOCK_PARENT_KEYWORD="SUBBLOCK-PARENT";
    
            
    
    public GeneralSuperInnovaStatEngine(SuperInnovaStatCore superInnovaStatCore,
                                        SuperInnovaStatEngineConfiguration superInnovaStatEngineConfiguration,
                                        StatGatherConfiguration[] statGatherConfiguartionArray,
                                        StatSummarizerConfiguration[]  statSummarizerConfigurationArray){

        this.statGatherConfiguartionArray = statGatherConfiguartionArray;
        this.superInnovaStatCore = superInnovaStatCore;
        this.statSummarizerConfigurationArray=statSummarizerConfigurationArray;
        this.superInnovaStatEngineConfiguration=superInnovaStatEngineConfiguration;

        makeLookup();

//        Properties p = this.superInnovaStatEnginePropertiesLookup.getCategory(SITE_KEYWORD);
//        logger.debug("Size : " + p.size());
//
//        Enumeration e = p.keys();
//        while(e.hasMoreElements()){
//            String log = (String) e.nextElement();
//            //System.out.println(log);
//            logger.debug(log);
//        }
//        logger.debug("END DEBUG");
//        System.out.println("END DEBUG");

        // Need to run after makeLookup Process
        logger.info("Run StatProcessor");
        this.superInnovaStatProcessor= new GeneralSuperNovaStatProcessor(this); 
        
    }
    
  
   
    @Override
    public String getTextResponse(String input){
        return "Hello I'm OCF";
    }

    @Override
    public void run(){
        this.superInnovaStatProcessor.run();
    }

    public void put(String category, Object key, Object value){
        this.superInnovaStatEnginePropertiesLookup.put(category, key, value);
    }

    public Object get(String category, Object key){
        return this.get(category, key);
    }

    public void makeLookup(){
        logger.info("makeLookup");
        // InitateEngine Name

        this.superInnovaStatEnginePropertiesLookup.put(ENGINE_KEYWORD, "EngineName", superInnovaStatEngineConfiguration.getEngineName());

        int siteCounter=0;
        int blockCounter=0;      
        int subBlockCounter=0;

        for(int i=0;i<this.statGatherConfiguartionArray.length;i++){

            // Site Check
            // System.out.println("siteKey="+this.statGatherConfiguartionArray[i].getSite());
            StringBuilder _log = new StringBuilder();
            _log.append("Site :").append(this.statGatherConfiguartionArray[i].getSite());
            _log.append(", Block :").append(this.statGatherConfiguartionArray[i].getBlock());
            _log.append(", SubBlock :").append(this.statGatherConfiguartionArray[i].getSubBlock());
            logger.debug(_log);

            if(this.superInnovaStatEnginePropertiesLookup.get(GeneralSuperInnovaStatEngine.SITE_KEYWORD, this.statGatherConfiguartionArray[i].getSite()) == null){
                this.superInnovaStatEnginePropertiesLookup.put(SITE_KEYWORD, this.statGatherConfiguartionArray[i].getSite(), siteCounter);
                this.superInnovaStatEnginePropertiesLookup.put(SITE_REVERSE_KEYWORD, siteCounter,this.statGatherConfiguartionArray[i].getSite());
                siteCounter++;
            }
            // block Check
             String blockUniqueKey=this.statGatherConfiguartionArray[i].getBlock();
//             System.out.println("blockUniqueKey="+blockUniqueKey);
             if(this.superInnovaStatEnginePropertiesLookup.get(GeneralSuperInnovaStatEngine.BLOCK_KEYWORD,blockUniqueKey)==null){
                this.superInnovaStatEnginePropertiesLookup.put(BLOCK_KEYWORD, blockUniqueKey, blockCounter);
                this.superInnovaStatEnginePropertiesLookup.put(BLOCK_REVERSE_KEYWORD, blockCounter, blockUniqueKey);
                
                // put ParentKeyWord
                String blockParent=this.statGatherConfiguartionArray[i].getSite();
                this.superInnovaStatEnginePropertiesLookup.put(BLOCK_PARENT_KEYWORD, blockParent, blockUniqueKey);
                blockCounter++;
            }
            // subBlock Check
             String subBlockUniqueKey=this.statGatherConfiguartionArray[i].getSubBlock();
//             System.out.println("subBlockUniqueKey="+subBlockUniqueKey);
             if(this.superInnovaStatEnginePropertiesLookup.get(GeneralSuperInnovaStatEngine.SUBBLOCK_KEYWORD,subBlockUniqueKey)==null){
                this.superInnovaStatEnginePropertiesLookup.put(SUBBLOCK_KEYWORD, subBlockUniqueKey, subBlockCounter);
                this.superInnovaStatEnginePropertiesLookup.put(SUBBLOCK_REVERSE_KEYWORD, subBlockCounter, subBlockUniqueKey);
                                
                // put subBlock Parent
                String subBlockParent=this.statGatherConfiguartionArray[i].getSite()+"."+this.statGatherConfiguartionArray[i].getBlock();
                this.superInnovaStatEnginePropertiesLookup.put(SUBBLOCK_PARENT_KEYWORD, subBlockParent, subBlockCounter);
                subBlockCounter++;                
            }
//            System.out.println("=====================");
        }
        
        
        // ADD UNKNOWN KEYWORD
         // Add Unknown to Site
        if(this.superInnovaStatEnginePropertiesLookup.get(GeneralSuperInnovaStatEngine.SITE_KEYWORD,UNKNOWN_KEYWORD)==null){
            this.superInnovaStatEnginePropertiesLookup.put(SITE_KEYWORD, UNKNOWN_KEYWORD, siteCounter);
            this.superInnovaStatEnginePropertiesLookup.put(SITE_REVERSE_KEYWORD, siteCounter,UNKNOWN_KEYWORD);
            siteCounter++;
        }
        // Add Unknown to Block
        if(this.superInnovaStatEnginePropertiesLookup.get(GeneralSuperInnovaStatEngine.BLOCK_KEYWORD,UNKNOWN_KEYWORD)==null){
            this.superInnovaStatEnginePropertiesLookup.put(BLOCK_KEYWORD, UNKNOWN_KEYWORD, blockCounter);
            this.superInnovaStatEnginePropertiesLookup.put(BLOCK_REVERSE_KEYWORD, blockCounter,UNKNOWN_KEYWORD);
            blockCounter++;
        }
        // Add Unknown to SubBlock
        if(this.superInnovaStatEnginePropertiesLookup.get(GeneralSuperInnovaStatEngine.SUBBLOCK_KEYWORD,UNKNOWN_KEYWORD)==null){
             this.superInnovaStatEnginePropertiesLookup.put(SUBBLOCK_KEYWORD, UNKNOWN_KEYWORD, subBlockCounter);
             this.superInnovaStatEnginePropertiesLookup.put(SUBBLOCK_REVERSE_KEYWORD, subBlockCounter,UNKNOWN_KEYWORD);
             subBlockCounter++;
        }

        StringBuilder _gatherTarget = new StringBuilder();
        _gatherTarget.append("Target ").append("SITE size[" + siteCounter + "], ");
        _gatherTarget.append("BLOCK size[" + blockCounter + "], ");
        _gatherTarget.append("SUBBLOCK size[" + subBlockCounter + "]");
        logger.info(_gatherTarget);

    }

    @Override
    public String getTextResponse(Properties inputParam) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
