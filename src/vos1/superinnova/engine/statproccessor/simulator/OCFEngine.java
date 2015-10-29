/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.statproccessor.simulator;

import java.util.Properties;
import vos1.superinnova.engine.statproccessor.SuperInnovaStatEngine;



/**
 *
 * @author HugeScreen
 */
public class OCFEngine extends SuperInnovaStatEngine{
    public String getTextResponse(String input){
        return "Hello I'm OCF";
    }

    @Override
    public void run(){
        
    }

    @Override
    public String getTextResponse(Properties inputParam) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

