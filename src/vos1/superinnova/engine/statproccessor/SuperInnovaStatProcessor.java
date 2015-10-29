/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.statproccessor;

import vos1.superinnova.engine.statproccessor.statgathermodule.HSQLDBManager;
import vos1.superinnova.engine.statproccessor.statgathermodule.StatGatherConfiguration;
import vos1.superinnova.engine.statproccessor.statgathermodule.StatGathererParser;
import vos1.superinnova.engine.statsummarizer.StatSummarizationCore;
import vos1.superinnova.engine.statsummarizer.StatSummarizationModule;

/**
 *
 * @author HugeScreen
 */
public abstract class SuperInnovaStatProcessor implements SuperInnovaStatEngineQueryAble,Runnable{
    
    
    
    protected StatGatherConfiguration[] statGatherConfiguartionArray=null;
    protected String storageName;
    protected String storageType;
    protected HSQLDBManager dbConnection=null;
    protected StatGathererParser statGathererParser;
    protected SuperInnovaStatEngine superInnovaStatEngine;
    
    
    // Stat Summarizer
    //protected StatSummarizationModule[] statSummarizationModuleArray=null;
    protected StatSummarizationCore  statSummarizationCore=null;    

    public StatSummarizationCore getStatSummarizationCore() {
        return statSummarizationCore;
    }
    
    
    public StatGatherConfiguration[] getStatGatherConfiguartionArray(){
        return statGatherConfiguartionArray;
    }
    public SuperInnovaStatEngine getSuperInnovaStatEngine(){
        return this.superInnovaStatEngine;
    }
    public abstract Object lookupKeyValue(String category, Object key);
    public abstract SuperInnovaStatEnginePropertiesLookup getSuperInnovaStatEnginePropertiesLookup();
    
    public abstract void beginStatSummarizationProcess();
    
  

}
