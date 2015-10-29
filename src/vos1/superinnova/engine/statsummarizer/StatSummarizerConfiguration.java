/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.statsummarizer;

import java.io.FileReader;
import java.util.Properties;

/**
 *
 * @author HugeScreen
 */
public class StatSummarizerConfiguration {
    boolean rootLevelSummarization=false;
    boolean siteSummarization=false;
    boolean vipSummarization=false;
    boolean blockSummarization=false;
    boolean subBlockSummarization=false;
    Properties additionalProperties=null;
    
    String statName=null;
    String summarizationModule=null;
    
    Properties prop=null;
    
    String statSummarizerConfigurationFileName=null;

    public String getStatName() {
        return statName;
    }

    public String getSummarizationModule() {
        return summarizationModule;
    }

    public String getStatSummarizerConfigurationFileName() {
        return statSummarizerConfigurationFileName;
    }

    public Properties getAdditionalProperties() {
        return additionalProperties;
    }
    
    public static StatSummarizerConfiguration makeStatSummarizerConfiguration(String filename){
        Properties prop = new Properties();
        try{
            prop.load(new FileReader(filename));
            String statname=prop.getProperty("name");
            String summarizationModule=prop.getProperty("summarization_module");
            String summarizatioLevel=prop.getProperty("summarization_level");
            
            boolean[] booleanArray=StatSummarizerConfiguration.textLevelToBooleanArray(summarizatioLevel);
            return new StatSummarizerConfiguration(statname,summarizationModule,booleanArray[0],booleanArray[1],booleanArray[2],booleanArray[3],prop);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static boolean[] textLevelToBooleanArray(String text){
        boolean[] booleanArray=new boolean[4];
        String[] token = text.split("\\|");
        for(int i=0;i<token.length;i++){
            booleanArray[i]=false;
        }   
        for(int i=0;i<token.length;i++){
        ///ROOT|SITE|BLOCK|SUBBLOCK
            if(token[i].compareToIgnoreCase("root")==0){
                booleanArray[0]=true;
            }
            else if(token[i].compareToIgnoreCase("site")==0){
                booleanArray[1]=true;
            }
            else if(token[i].compareToIgnoreCase("block")==0){
                booleanArray[2]=true;
            }
            else if(token[i].compareToIgnoreCase("subblock")==0){
                booleanArray[3]=true;
            }            
        }
        return booleanArray;
    }
    public StatSummarizerConfiguration(String statName,String summarizationModule,boolean rootLevelSummarization,boolean siteSummarization,boolean blockSummarization, boolean subBlockSummarization,Properties additionalProperties){
        this.statName=statName;
        this.summarizationModule=summarizationModule;
        this.rootLevelSummarization=rootLevelSummarization;
        this.siteSummarization=siteSummarization;
        this.blockSummarization=blockSummarization;
        this.subBlockSummarization=subBlockSummarization;
        this.additionalProperties=additionalProperties;
    }
    
    
    private void processConfigurationFile(){
        if(this.statSummarizerConfigurationFileName!=null && this.prop!=null){

            // Parse StatName
            String statName=prop.getProperty("STAT_NAME");
            if(statName!=null && statName.length()>0){
                this.statName=statName;
            }
            // Parse SummarizationModule
            String summarizationModule=prop.getProperty("SUMMARIZATION_MODULE");
            if(summarizationModule!=null && summarizationModule.length()>0){
                this.summarizationModule=summarizationModule;
            }
            
            
            // Parse Summarization Level
            String level=prop.getProperty("SUMMARIZATION_LEVEL");
            String[] levelToken=level.split("\\|");
            for(int i=0;i<levelToken.length;i++){
                if(levelToken[i].compareToIgnoreCase("SITE")==0){
                    siteSummarization=true;
                }
                else if(levelToken[i].compareToIgnoreCase("BLOCK")==0){
                    blockSummarization=true;
                }
                else if(levelToken[i].compareToIgnoreCase("SUBBLOCK")==0){
                    subBlockSummarization=true;
                }
            }// End For
            
        }// Enf if this.statSummarzation...
    }
    /*
     name=OCF_General_SuccessRate
summarization_module=successRate
summarization_level=ROOT|SITE|BLOCK|SUBBLOCK
param_attempt=attempt
param_success=success
param_error=error
     */
    @Override
    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append(statName);
        sb.append(",");        
        sb.append(summarizationModule);       
        sb.append(",");
        sb.append(rootLevelSummarization);
        sb.append(",");
        sb.append(siteSummarization);
        sb.append(",");
        sb.append(blockSummarization);
        sb.append(",");
        sb.append(subBlockSummarization);         
        return sb.toString();
    }
    
    
    
    
    
    public static void main(String[] args){
        System.out.println("Hello World");
        //StatSummarizerConfiguration smc = new StatSummarizerConfiguration("D:\\tmp\\OCF_SUCCESSRATE.conf");
    }
}
