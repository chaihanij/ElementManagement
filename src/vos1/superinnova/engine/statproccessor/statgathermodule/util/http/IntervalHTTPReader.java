/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.statproccessor.statgathermodule.util.http;

/**
 *
 * @author HugeScreen
 */
public class IntervalHTTPReader extends Thread{
    
    int exitSignal=0;
    String url;
    long pollInterval=3000;

    public IntervalHTTPReader(String url,long pollInterval){
        this.url=url;
        if(pollInterval>0){
            this.pollInterval=pollInterval;
        }
    }
    
    @Override
    public void run(){
        try{
            while (exitSignal!=1){
                try{
                    System.out.println(HTTPReader.openConnection(this.url));
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                System.out.println(new java.util.Date());
                System.out.println("==============================================");
                Thread.sleep(pollInterval);
                
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
    
    
    
    
    public static void main(String[] args){
        IntervalHTTPReader iHttpReader = new IntervalHTTPReader("http://localhost:9016/equinoxStat?nodeType=OCF&hostname=OCF201",1000);
        iHttpReader.start();
    }
    
}
