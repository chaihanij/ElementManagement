/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.statproccessor.predefinedengine;

import java.util.Enumeration;
import java.util.Properties;
import vos1.superinnova.engine.statproccessor.SuperInnovaStatCore;
import vos1.superinnova.engine.statproccessor.SuperInnovaStatEngine;
import vos1.superinnova.engine.statproccessor.SuperInnovaStatEngineConfiguration;
import vos1.superinnova.engine.statproccessor.SuperInnovaStatProcessor;
import vos1.superinnova.engine.statproccessor.predefinedtype.GeneralSuperNovaStatProcessor;
import vos1.superinnova.engine.statproccessor.statgathermodule.StatGatherConfiguration;
import vos1.superinnova.engine.statsummarizer.StatSummarizerConfiguration;

/**
 *
 * @author HugeScreen
 */
public class GeneralSuperInnovaStatEngine extends SuperInnovaStatEngine{
    //StatGatherConfiguration[] statGatherConfiguartionArray=null;
    
    

    
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
    
            
    
    public GeneralSuperInnovaStatEngine(SuperInnovaStatCore superInnovaStatCore,SuperInnovaStatEngineConfiguration superInnovaStatEngineConfiguration,StatGatherConfiguration[] statGatherConfiguartionArray,StatSummarizerConfiguration[]  statSummarizerConfigurationArray){
        this.statGatherConfiguartionArray = statGatherConfiguartionArray;
        this.superInnovaStatCore = superInnovaStatCore;
        this.statSummarizerConfigurationArray=statSummarizerConfigurationArray;
        this.superInnovaStatEngineConfiguration=superInnovaStatEngineConfiguration;
        makeLookup();
        
        
        Properties p = this.superInnovaStatEnginePropertiesLookup.getCategory(SITE_KEYWORD);
        System.out.println("DEBUG Size : "+p.size());
        
        Enumeration e = p.keys();
        while(e.hasMoreElements()){
            System.out.println(e.nextElement());
        }
        System.out.println("END DEBUG");
        // Need to run after makeLookup Process
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
    public void put(String catgegory, Object key, Object value){
        this.superInnovaStatEnginePropertiesLookup.put(catgegory, key, value);
    }
    public Object get(String catgegory, Object key){
        return this.get(catgegory, key);
    }    
    public void makeLookup(){
        
        // InitateEngine Name
        this.superInnovaStatEnginePropertiesLookup.put(ENGINE_KEYWORD, "EngineName", superInnovaStatEngineConfiguration.getEngineName());
        
        
        int siteCounter=0;
        int blockCounter=0;      
        int subBlockCounter=0;        
        for(int i=0;i<this.statGatherConfiguartionArray.length;i++){
            System.out.println("CONFIG [ "+this.statGatherConfiguartionArray[i].getSite()+" , "+this.statGatherConfiguartionArray[i].getBlock()+" , "+this.statGatherConfiguartionArray[i].getSubBlock()+ " ]");
            // Site Check
             
             System.out.println("siteKey="+this.statGatherConfiguartionArray[i].getSite());            
             if(this.superInnovaStatEnginePropertiesLookup.get(GeneralSuperInnovaStatEngine.SITE_KEYWORD,this.statGatherConfiguartionArray[i].getSite())==null){
                this.superInnovaStatEnginePropertiesLookup.put(SITE_KEYWORD, this.statGatherConfiguartionArray[i].getSite(), siteCounter);
                this.superInnovaStatEnginePropertiesLookup.put(SITE_REVERSE_KEYWORD, siteCounter,this.statGatherConfiguartionArray[i].getSite());
                siteCounter++;
            }
            // block Check
             String blockUniqueKey=this.statGatherConfiguartionArray[i].getBlock();
             System.out.println("blockUniqueKey="+blockUniqueKey);
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
             System.out.println("subBlockUniqueKey="+subBlockUniqueKey);
             if(this.superInnovaStatEnginePropertiesLookup.get(GeneralSuperInnovaStatEngine.SUBBLOCK_KEYWORD,subBlockUniqueKey)==null){
                this.superInnovaStatEnginePropertiesLookup.put(SUBBLOCK_KEYWORD, subBlockUniqueKey, subBlockCounter);
                this.superInnovaStatEnginePropertiesLookup.put(SUBBLOCK_REVERSE_KEYWORD, subBlockCounter, subBlockUniqueKey);
                                
                // put subBlock Parent
                String subBlockParent=this.statGatherConfiguartionArray[i].getSite()+"."+this.statGatherConfiguartionArray[i].getBlock();
                this.superInnovaStatEnginePropertiesLookup.put(SUBBLOCK_PARENT_KEYWORD, subBlockParent, subBlockCounter);
                subBlockCounter++;                
            }
            System.out.println("=====================");
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
        
    }
    public static void main(String[] args){
        System.out.println("Hello World");
        StatGatherConfiguration[] statGatherConfiguartionArray;
            String[] hostList = new String[]{"OCF201","OCF202","OCF203","OCF204"};     
            statGatherConfiguartionArray = new StatGatherConfiguration[hostList.length];
            for(int i=0;i<hostList.length;i++){
                statGatherConfiguartionArray[i] = new StatGatherConfiguration(StatGatherConfiguration.FETCHTYPE_HTTP,"CWDC","VIP-1",hostList[i],"http://localhost:9016/equinoxStat?nodeType=OCF&hostname="+hostList[i],"OCF","SuperNovaStatParser",0,5,3);
            }
            
            
            //SuperInnovaStatEngine superInnovaStatEngine = new OCFSuperInnovaStatEngine(statGatherConfiguartionArray);
            //superInnovaStatEngine.run();
            
            
        
        //SuperInnovaStatProcessor sitp = new OCFStatProcessor();
    }    

    @Override
    public String getTextResponse(Properties inputParam) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
