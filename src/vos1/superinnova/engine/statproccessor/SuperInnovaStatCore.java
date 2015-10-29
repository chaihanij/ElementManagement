/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.statproccessor;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import vos1.superinnova.engine.statinquiry.SuperInnovaStatInquiryCore;
import vos1.superinnova.engine.statproccessor.predefinedengine.GeneralSuperInnovaStatEngine;
import vos1.superinnova.engine.statproccessor.statgathermodule.StatGatherConfiguration;
import vos1.superinnova.engine.statsummarizer.StatSummarizerConfiguration;

/**
 *
 * @author HugeScreen
 */
public class SuperInnovaStatCore extends Thread{
    ExecutorService statGatherExecutorServiceNormalPriority ;
    ExecutorService statGatherExecutorServiceHighPriority ;
    ExecutorService statProcessorExecutorServiceNormalPriority ;
    ExecutorService statProcessorExecutorServiceHighPriority ;
    
    int statGatherNormalPriorityWorker=10;
    int statGatherHighPriorityWorker=5;
    int sstatProcessorNormalPriorityWorker=10;
    int statProcessorHighPriorityWorker=5;
    
    boolean useCachedThreadPool=false;
    
    SuperInnovaStatCoreConfigurator superInnovaStatCoreConfigurator=null;
    SuperInnovaStatEngine[] superInnovaStatEngineArray=null;
    int superInnovaStatEngineCounter=0;
    
    String rootConfigurationPath=null;
    Properties engineNameMap=null;
    
    SuperInnovaStatInquiryCore superInnovaStatInquiryCore=null;
    
    static int MAXIMUM_ENGINE=2048;
    
    public SuperInnovaStatEngine getSuperInnovaStatEngine(String engineName){
        Integer engineIndex = (Integer)engineNameMap.get(engineName);
        //System.out.println("engine "+engineName+" found at "+engineIndex);
        if(engineIndex!=null){
            return this.superInnovaStatEngineArray[engineIndex];
        }
        else{
            return null;
        }
        
    }
    //============================================================================

    //============================================================================
    public ExecutorService getStatGatherExecutorServiceNormalPriority() {
        return statGatherExecutorServiceNormalPriority;
    }

    public ExecutorService getStatGatherExecutorServiceHighPriority() {
        return statGatherExecutorServiceHighPriority;
    }

    public ExecutorService getStatProcessorExecutorServiceNormalPriority() {
        return statProcessorExecutorServiceNormalPriority;
    }

    public ExecutorService getStatProcessorExecutorServiceHighPriority() {
        return statProcessorExecutorServiceHighPriority;
    }

    public String getRootConfigurationPath() {
        return rootConfigurationPath;
    }
    
    public void initSuperInnovaStatInquiryCore(){
        superInnovaStatInquiryCore = new SuperInnovaStatInquiryCore(this);
    }
    
    public SuperInnovaStatCore(String rootConfigurationPath){
        System.out.println("Element Manament Version 1.0.0");
        System.out.println("Last Update : 29/10/2015");
        System.out.println("========================================");
        try{
            Thread.sleep(2000);
        }
        catch(Exception e){
           
        }
        
        this.rootConfigurationPath=rootConfigurationPath;
        superInnovaStatCoreConfigurator = new SuperInnovaStatCoreConfigurator(rootConfigurationPath);
        initCore();
        initSuperInnovaStatInquiryCore();
    }
    public Properties getEngineCoreConfiguration(){
        return this.superInnovaStatCoreConfigurator.getEngineCoreConfiguration();
    }
    
    
    public int getEngineConfigurationCounter(){
        return this.superInnovaStatCoreConfigurator.getEngineConfigurationCounter();
    }
    public void doSuperInnovaStatCoreConfigurationProcess(){
        int engineConfigurationCounter = this.superInnovaStatCoreConfigurator.getEngineConfigurationCounter();
        superInnovaStatEngineArray = new SuperInnovaStatEngine[engineConfigurationCounter];
        for(int i=0;i<engineConfigurationCounter;i++){
            System.out.println("engine "+i+" : "+this.superInnovaStatCoreConfigurator.engineNameList[i]);
            try{
                superInnovaStatEngineArray[i]=makeSuperNovaStatEngineFromConfiguration(this.superInnovaStatCoreConfigurator.getSuperInnovaStatEngineConfigurationArray(i),this.superInnovaStatCoreConfigurator.getStatConfigurationArray(i),this.superInnovaStatCoreConfigurator.getStatSummarizerConfigurationArray(i));
                engineNameMap.put(superInnovaStatEngineArray[i].getSuperInnovaStatEngineConfiguration().getEngineName(), new Integer(superInnovaStatEngineCounter));                
            }
            catch(Exception e){
                e.printStackTrace();
            }
            superInnovaStatEngineCounter++;
        }
    }
    /*
      StatGatherConfiguration[] statGatherConfiguartionArray=null;;
            StatSummarizerConfiguration[] statSummarizerConfigurationArray=null;;
     */
    private SuperInnovaStatEngine makeSuperNovaStatEngineFromConfiguration(SuperInnovaStatEngineConfiguration superInnovaStatEngineConfiguration, StatGatherConfiguration[] statGatherConfiguartionArray, StatSummarizerConfiguration[] statSummarizerConfigurationArray) throws Exception{
        
        SuperInnovaStatEngine superNovaStatEngine=null;
        if(superInnovaStatEngineConfiguration.getEngineType().compareToIgnoreCase("SUPERNOVA")==0){
            superNovaStatEngine = new GeneralSuperInnovaStatEngine(this,superInnovaStatEngineConfiguration,statGatherConfiguartionArray,statSummarizerConfigurationArray);
        } else if (superInnovaStatEngineConfiguration.getEngineType().compareToIgnoreCase("PLAYBOX")==0){
            superNovaStatEngine = new GeneralSuperInnovaStatEngine(this,superInnovaStatEngineConfiguration,statGatherConfiguartionArray,statSummarizerConfigurationArray);
        } else {
            Exception e = new Exception("Error : Unknown SuperNovaStatEngineType "+superInnovaStatEngineConfiguration.getEngineType());
            e.fillInStackTrace();
            throw e;
        }

        return superNovaStatEngine;
    }
    
    @Override
    public void run(){
        for(int i=0;i<superInnovaStatEngineCounter;i++){
            System.out.println("*** start Engine : "+i);
            this.superInnovaStatEngineArray[i].run();
        }
    }
    
    public void initSuperInnovaStatEngineProcess(){
        int engineConfigurationCounter = superInnovaStatCoreConfigurator.getEngineConfigurationCounter();
        for(int i=0;i<engineConfigurationCounter;i++){
            StatGatherConfiguration[] statGatherConfiguartionArray=null;;
            StatSummarizerConfiguration[] statSummarizerConfigurationArray=null;;
            statGatherConfiguartionArray=superInnovaStatCoreConfigurator.getStatConfigurationArray(i);
            statSummarizerConfigurationArray=superInnovaStatCoreConfigurator.getStatSummarizerConfigurationArray(i);
            if(statGatherConfiguartionArray!=null && statSummarizerConfigurationArray!=null){
                
            }
            else{
                System.out.println("ERROR : Cannot initialize StatEngine");
            }
        }
    }
    /*
    public void testRun(){
        StatGatherConfiguration[] statGatherConfiguartionArray;
        StatSummarizerConfiguration[] statSummarizerConfigurationArray;
        
            String[] hostList = new String[]{"OCF201","OCF202","OCF203","OCF204"};     
            statGatherConfiguartionArray = new StatGatherConfiguration[hostList.length];
            for(int i=0;i<hostList.length;i++){
                statGatherConfiguartionArray[i] = new StatGatherConfiguration(StatGatherConfiguration.FETCHTYPE_HTTP,"CWDC","VIP-1",hostList[i],"http://localhost:9016/equinoxStat?nodeType=OCF&hostname="+hostList[i],"OCF","SuperNovaStatParser",0,3);
            }

            String[] moduleList = new String[]{"SUCCESSRATE"};
            String[] statNameList = new String[]{"OCF_SUCESSRATE"};
            statSummarizerConfigurationArray = new StatSummarizerConfiguration[moduleList.length];
            for(int i=0;i<moduleList.length;i++){
                //statSummarizerConfigurationArray[i]= new StatSummarizerConfiguration(statNameList[i],moduleList[i],true,true,true,true);
                
            }
            
            SuperInnovaStatEngine superInnovaStatEngine = new GeneralSuperInnovaStatEngine(this,statGatherConfiguartionArray,statSummarizerConfigurationArray);
            superInnovaStatEngine.run();        
    }
    */
    public void initCore(){
        engineNameMap = new Properties();
        //statGatherExecutorServiceNormalPriority=new Executors.newFixedThreadPool(2);
        initThreadPoolExecutor();
        //Must doSuperInnovaStatCoreConfigurationProcess() after initThreadPoolExecutor();
        doSuperInnovaStatCoreConfigurationProcess();
    }
    public void initThreadPoolExecutor(){
        if(useCachedThreadPool==true){
            statGatherExecutorServiceNormalPriority = Executors.newCachedThreadPool();
            statGatherExecutorServiceHighPriority = Executors.newCachedThreadPool();
            statProcessorExecutorServiceNormalPriority = Executors.newCachedThreadPool();
            statProcessorExecutorServiceHighPriority = Executors.newCachedThreadPool();
        }
        else{
            statGatherExecutorServiceNormalPriority = Executors.newFixedThreadPool(statGatherNormalPriorityWorker);
            statGatherExecutorServiceHighPriority = Executors.newFixedThreadPool(statGatherHighPriorityWorker) ;
            statProcessorExecutorServiceNormalPriority = Executors.newFixedThreadPool(sstatProcessorNormalPriorityWorker) ;
            statProcessorExecutorServiceHighPriority = Executors.newFixedThreadPool(statProcessorHighPriorityWorker);            
        }        
    }
    
    public static void main(String[] args){
        if(args!=null && args.length>0){
            String rootPath=args[0];
            if(rootPath!=null && rootPath.length()>0){
                SuperInnovaStatCore superInnovaStatCore = new SuperInnovaStatCore(rootPath);
                superInnovaStatCore.start();
            }
            else{
                System.out.println("Error : rootPath Length Error");
            }
        }
        else{
            System.out.println("Error : Please input RootPath");
        }
    }
}
