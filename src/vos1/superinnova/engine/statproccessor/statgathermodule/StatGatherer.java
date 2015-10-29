/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.statproccessor.statgathermodule;

import java.util.concurrent.Callable;

/**
 *
 * @author HugeScreen
 */
public abstract class StatGatherer implements Callable< StatGatherer >{
    protected int gathererStatus=0;
    protected String gatherOutput=null;
    
    
    public static int STATUS_NOTSTARTED=0;
    public static int STATUS_STARTED=1;
    public static int STATUS_FINISHED=2;
    public static int STATUS_ERROR=9;
    
    
    
    public abstract String gather();
    
    @Override
    public StatGatherer call(){
        gather();
        return this;
    }
    
    public int getGathererStatus(){
        return gathererStatus;
    }
    
    public String getGatherOutput(){
        return this.gatherOutput;
    }
    
}
