/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.statproccessor.statgathermodule.util;

/**
 *
 * @author HugeScreen
 */
public class TimeUtil {
    public static long calculateDelayInMillisecToNextLaunch(int inputSecond){
        long sleepTime=60000;
        long now= new java.util.Date().getTime();
        sleepTime = (inputSecond*1000)-now%(inputSecond*1000);
        //System.out.println("Now : "+now+", sleep :"+sleepTime);
        return sleepTime;
    }    
}
