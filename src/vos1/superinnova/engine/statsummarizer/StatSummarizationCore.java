/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.statsummarizer;

import java.util.Properties;
import vos1.superinnova.engine.statproccessor.SuperInnovaStatProcessor;
import vos1.superinnova.engine.statsummarizer.templates.SupernovaMultiSuccessRateSummarizationModule;
import vos1.superinnova.engine.statsummarizer.templates.SupernovaRegexFilterizationModule;
import vos1.superinnova.engine.statsummarizer.templates.SupernovaStatAddSubtractModule;
import vos1.superinnova.engine.statsummarizer.templates.SupernovaStatCategorizationModule;
import vos1.superinnova.engine.statsummarizer.templates.SupernovaSuccessRateSummarizationModule;


/**
 *
 * @author HugeScreen
 */
public class StatSummarizationCore {
    SuperInnovaStatProcessor superInnovaStatProcessor;
    StatSummarizationModule[] statSummarizationModuleArray = null;
    StatSummarizerConfiguration[] statSummarizerConfigurationArray=null;
    
    
    Properties statSummarizerNameMap=null;
    
    
    public  StatSummarizationCore(SuperInnovaStatProcessor superInnovaStatProcessor){
        this.superInnovaStatProcessor = superInnovaStatProcessor;
        this.statSummarizerNameMap = new Properties();
        // Make StatSummarizationModule
        this.statSummarizerConfigurationArray=this.superInnovaStatProcessor.getSuperInnovaStatEngine().getStatSummarizerConfiguration();
        makeStatSummarizationModule();
    }
    
    public void makeStatSummarizationModule(){
        if (this.statSummarizerConfigurationArray.length>0){
            this.statSummarizationModuleArray = new StatSummarizationModule[this.statSummarizerConfigurationArray.length];
            for(int i=0;i<this.statSummarizerConfigurationArray.length;i++){
                // Switch Type
                try{
                    this.statSummarizationModuleArray[i] = createStatSummarizationModuleFromConfiguration(this.statSummarizerConfigurationArray[i]);
                    System.out.println("PUT "+this.statSummarizerConfigurationArray[i].getStatName()+", index : "+new Integer(i));
                    statSummarizerNameMap.put(this.statSummarizerConfigurationArray[i].getStatName(), new Integer(i));
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                //darkMetamorphosis
            }
        }
    }
    public StatSummarizationModule createStatSummarizationModuleFromConfiguration(StatSummarizerConfiguration statSummarizerConfiguration) throws Exception{
        StatSummarizationModule statSummarizationModule = null;
        // Which StatSummarizationModuleType Will Be Using
        if(statSummarizerConfiguration.summarizationModule.compareToIgnoreCase("successRate")==0){
            // Create SuperNova SuccessRate Template
            statSummarizationModule=new SupernovaSuccessRateSummarizationModule(this,statSummarizerConfiguration);
        }
        else if(statSummarizerConfiguration.summarizationModule.compareToIgnoreCase("statCategorization")==0){
            // Create SuperNova SuccessRate Template
            statSummarizationModule=new SupernovaStatCategorizationModule(this,statSummarizerConfiguration);
        }       
        else if(statSummarizerConfiguration.summarizationModule.compareToIgnoreCase("multiSuccessRate")==0){
            // Create SuperNova Multi SuccessRate Template
            statSummarizationModule=new SupernovaMultiSuccessRateSummarizationModule(this,statSummarizerConfiguration);
        }     
        else if(statSummarizerConfiguration.summarizationModule.compareToIgnoreCase("errorAnalysis")==0){
            // Create SuperNova Multi SuccessRate Template
            statSummarizationModule=new SupernovaRegexFilterizationModule(this,statSummarizerConfiguration);
        }
        else if(statSummarizerConfiguration.summarizationModule.compareToIgnoreCase("addSubtractModule")==0){
            // Create SuperNova Multi SuccessRate Template
            statSummarizationModule=new SupernovaStatAddSubtractModule(this,statSummarizerConfiguration);
        }                  
        
        else{
            Exception e = new Exception("Error : cannot create StatSummarizationModule From Configuration "+statSummarizerConfiguration.statName+", "+statSummarizerConfiguration.summarizationModule);
            e.fillInStackTrace();
            throw e;
        }
        return statSummarizationModule;
    }
   
    public SuperInnovaStatProcessor getSuperInnovaStatProcessor(){
        return this.superInnovaStatProcessor;
    }
    
    
    // When Data in Database is Completed, Invoke This method to
    // Invoke Stat Summarization Process of Every Module, Under This StatSummarizationCore Controlled.
    public void invokeStatSummarizationProcess(){
        for(int i=0;i<this.statSummarizerConfigurationArray.length;i++){
            // Switch Type
            this.statSummarizationModuleArray[i].startStatSummarizationProcess();
        }        
        // Code Here
    }
    
    public StatSummarizationModule getStatSummarizationModule(String statSummarizationName){
        /*
        Enumeration e = this.statSummarizerNameMap.keys();
        System.out.println("**** DOG ******");
        while(e.hasMoreElements()){
            System.out.println("Keys : "+e.nextElement());
        }
        */
        if(statSummarizationName!=null){
            Integer index = (Integer)statSummarizerNameMap.get(statSummarizationName);
            //System.out.println("index : "+index);
            if(index!=null){
                return statSummarizationModuleArray[index];
            }
            else{
                return null;
            }
        }
        else{
            return null;
        }
    }
    

}
