/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.statproccessor;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import vos1.superinnova.engine.statinquiry.SuperInnovaStatInquiryCore;
import vos1.superinnova.engine.statproccessor.predefinedengine.GeneralSuperInnovaStatEngine;
import vos1.superinnova.engine.statproccessor.statgathermodule.StatGatherConfiguration;
import vos1.superinnova.engine.statsummarizer.StatSummarizerConfiguration;

/**
 *
 * @author HugeScreen
 */
public final class SuperInnovaStatCore extends Thread{
    
    final static Logger logger = Logger.getLogger(SuperInnovaStatCore.class);
    
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
        logger.info("Element Manament Version 1.0.0");
        logger.info("Last Update : 12/11/2015");
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
                engineNameMap.put(superInnovaStatEngineArray[i].getSuperInnovaStatEngineConfiguration().getEngineName(), superInnovaStatEngineCounter);                
            }
            catch(Exception e){
                e.printStackTrace();
                logger.error("Error engine["+ i +"]"+ this.superInnovaStatCoreConfigurator.engineNameList[i]);
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
            logger.error("Error : Unknow EngineTeyp");
            throw e;
        }

        return superNovaStatEngine;
    }
    
    @Override
    public void run(){
        for(int i=0;i<superInnovaStatEngineCounter;i++){
            System.out.println("*** start Engine : "+i);
            logger.info("Run Engine :" + this.superInnovaStatEngineArray[i].getSuperInnovaStatEngineConfiguration().getEngineName());
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
                logger.error("ERROR : Cannot initialize StatEngine");
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
            
            String logName= null;
            String logLevel= null;
            logName= args[1];
            if (args.length >= 3) {
                logLevel= args[2];
            }
            
            initialLogConfiguration(logName, logLevel);
            if(rootPath!=null && rootPath.length()>0){
                logger.info("Initial");
                SuperInnovaStatCore superInnovaStatCore = new SuperInnovaStatCore(rootPath);
                superInnovaStatCore.start();
            }
            else{
               logger.error("rootPath Length Error");
               System.out.println("Error : rootPath Length Error");
            }
        }
        else{
            logger.error("Error : Please input RootPath");
            System.out.println("Error : Please input RootPath");
        }
    }
    
    public static void initialLogConfiguration(String logName, String logLevel) {
      
         /**
         Levels used for identifying the severity of an event. Levels are organized from most specific to least:
         OFF (most specific)
         FATAL
         ERROR
         WARN
         INFO
         DEBUG
         TRACE
         ALL (least specific)
        */

        Properties pro = new Properties();
        pro.setProperty("log4j.rootLogger", "ERROR, stdout, file");
        if ( logLevel != null ){
            if (logLevel.toUpperCase().equals("FATAL")||logLevel.toUpperCase().equals("ERROR")
                ||logLevel.toUpperCase().equals("WARN")||logLevel.toUpperCase().equals("INFO")
                ||logLevel.toUpperCase().equals("DEBUG")||logLevel.toUpperCase().equals("TRACE")
                ||logLevel.toUpperCase().equals("OFF")) {
                pro.setProperty("log4j.rootLogger", logLevel.toLowerCase()+ ", stdout, file");
            }
        }
        
        // Redirect log messages to console
        pro.setProperty("log4j.appender.stdout","org.apache.log4j.ConsoleAppender");
        pro.setProperty("log4j.appender.stdout.Target","System.out");
        pro.setProperty("log4j.appender.stdout.layout","org.apache.log4j.PatternLayout");
        pro.setProperty("log4j.appender.stdout.layout.ConversionPattern","%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n");
        
        // Redirect log messages to a log file, support file rolling.
        pro.setProperty("log4j.appender.file","org.apache.log4j.DailyRollingFileAppender");
        pro.setProperty("log4j.appender.file.File","/Users/Wachirawat/Desktop/PresentationEM/Build/SuperInnovaStatEngine/log/" + logName +".error");
        pro.setProperty("log4j.appender.file.DatePattern","'.'yyyy-MM-dd");
        pro.setProperty("log4j.appender.file.layout","org.apache.log4j.PatternLayout");
        pro.setProperty("log4j.appender.file.layout.ConversionPattern", "%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n");
        PropertyConfigurator.configure(pro);

    }
   
}
