/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.statproccessor;

/**
 *
 * @author HugeScreen
 */
public class SuperInnovaStatEngineConfiguration {
    
    String engineName=null;
    String engineType=null;

    public String getEngineName() {
        return engineName;
    }

    public String getEngineType() {
        return engineType;
    }
    
    public SuperInnovaStatEngineConfiguration(String engineName, String engineType){
        this.engineName=engineName;
        this.engineType=engineType;
    }
    @Override
    public String toString(){
        return this.engineName+" [ FORMAT : "+this.engineType+"]";
    }
}
