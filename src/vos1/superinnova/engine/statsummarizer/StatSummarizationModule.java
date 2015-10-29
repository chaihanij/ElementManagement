/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.statsummarizer;

import java.sql.ResultSet;

/**
 *
 * @author HugeScreen
 */
public abstract class StatSummarizationModule implements Runnable{
    protected StatSummarizationCore statSummarizationCore=null;
    protected StatSummarizationSmartResultSet statSummarizationSmartResultSet=null;
    protected StatSummarizerConfiguration statSummarizerConfiguration=null;
    
     
    
    public void StatSummarizationModule(StatSummarizationCore statSummarizationCore){
        this.statSummarizationCore=statSummarizationCore;
        
    }
    public StatSummarizationCore getStatSummarizationCore(){
        return this.statSummarizationCore;
    }
    
    public abstract ResultSet fetchDataFromStorage();
    public abstract void summarizeData(ResultSet resultSet);
    public abstract Object[] getSummarizationResultSet(String key);
    
    public void startStatSummarizationProcess(){
        System.out.println("startStatSummarizationProcess : "+this.statSummarizerConfiguration.toString());
        ResultSet rs = fetchDataFromStorage();
        summarizeData(rs);
    }
    
    public StatSummarizationSmartResultSet getStatSummarizationSmartResultSet(){
        return statSummarizationSmartResultSet;
    }

    public StatSummarizerConfiguration getStatSummarizerConfiguration() {
        return statSummarizerConfiguration;
    }
    
}
