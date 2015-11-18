/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.statproccessor;

import vos1.superinnova.engine.statproccessor.statgathermodule.StatGatherConfiguration;
import vos1.superinnova.engine.statsummarizer.StatSummarizerConfiguration;

/**
 *
 * @author HugeScreen
 */
public abstract class SuperInnovaStatEngine implements SuperInnovaStatTextResponseAble, Runnable{

    protected SuperInnovaStatCore superInnovaStatCore=null;
    protected SuperInnovaStatEnginePropertiesLookup superInnovaStatEnginePropertiesLookup= new SuperInnovaStatEnginePropertiesLookup();
    protected SuperInnovaStatEngineConfiguration superInnovaStatEngineConfiguration;
    
    protected StatGatherConfiguration[] statGatherConfiguartionArray;
    protected StatSummarizerConfiguration[]  statSummarizerConfigurationArray;
    protected SuperInnovaStatProcessor superInnovaStatProcessor=null;

    public SuperInnovaStatEngineConfiguration getSuperInnovaStatEngineConfiguration() {
        return superInnovaStatEngineConfiguration;
    }

    public StatGatherConfiguration[] getStatGatherConfiguration(){
        return this.statGatherConfiguartionArray;
    }

    public StatSummarizerConfiguration[] getStatSummarizerConfiguration(){
        return this.statSummarizerConfigurationArray;
    } 
    
    public SuperInnovaStatEnginePropertiesLookup getSuperInnovaStatEnginePropertiesLookup(){
        return this.superInnovaStatEnginePropertiesLookup;
    }

    public SuperInnovaStatCore getSuperInnovaStatCore(){
        return this.superInnovaStatCore;
    }

    public SuperInnovaStatProcessor getSuperInnovaStatProcessor() {
        return superInnovaStatProcessor;
    }
}
