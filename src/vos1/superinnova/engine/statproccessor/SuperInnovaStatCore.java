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
 * @author HugeScreen
 */
public final class SuperInnovaStatCore extends Thread {

    final static Logger logger = Logger.getLogger(SuperInnovaStatCore.class);

    ExecutorService statGatherExecutorServiceNormalPriority;
    ExecutorService statGatherExecutorServiceHighPriority;
    ExecutorService statProcessorExecutorServiceNormalPriority;
    ExecutorService statProcessorExecutorServiceHighPriority;

    int statGatherNormalPriorityWorker = 10;
    int statGatherHighPriorityWorker = 5;
    int statProcessorNormalPriorityWorker = 10;
    int statProcessorHighPriorityWorker = 5;

    boolean useCachedThreadPool = false;

    SuperInnovaStatCoreConfigurator superInnovaStatCoreConfigurator = null;
    SuperInnovaStatEngine[] superInnovaStatEngineArray = null;
    int superInnovaStatEngineCounter = 0;

    String rootConfigurationPath = null;
    Properties engineNameMap = null;

    SuperInnovaStatInquiryCore superInnovaStatInquiryCore = null;

    static int MAXIMUM_ENGINE = 2048;

    public SuperInnovaStatEngine getSuperInnovaStatEngine(String engineName) {
        Integer engineIndex = (Integer) engineNameMap.get(engineName);
        //System.out.println("engine "+engineName+" found at "+engineIndex);
        if (engineIndex != null) {
            return this.superInnovaStatEngineArray[engineIndex];
        } else {
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

    public void initSuperInnovaStatInquiryCore() {
        superInnovaStatInquiryCore = new SuperInnovaStatInquiryCore(this);
    }

    public SuperInnovaStatCore(String rootConfigurationPath) {
        logger.info("Element Management Version " + GlobalVariable.VERSION_ID);

        try {
            Thread.sleep(2000);
        } catch (Exception e) {

        }
        this.rootConfigurationPath = rootConfigurationPath;
        superInnovaStatCoreConfigurator = new SuperInnovaStatCoreConfigurator(this.rootConfigurationPath);

        initCore();
        initSuperInnovaStatInquiryCore();
    }

    public Properties getEngineCoreConfiguration() {
        return this.superInnovaStatCoreConfigurator.getEngineCoreConfiguration();
    }

    public int getEngineConfigurationCounter() {
        return this.superInnovaStatCoreConfigurator.getEngineConfigurationCounter();
    }

    public void doSuperInnovaStatCoreConfigurationProcess() {
        logger.info("doSuperInnovaStatCoreConfigurationProcess");
        int engineConfigurationCounter = this.superInnovaStatCoreConfigurator.getEngineConfigurationCounter();
        superInnovaStatEngineArray = new SuperInnovaStatEngine[engineConfigurationCounter];
        for (int i = 0; i < engineConfigurationCounter; i++) {
            String  engineName =  null;
            engineName = this.superInnovaStatCoreConfigurator.engineNameList[i];

            try {
                logger.debug("Set superInnovaStatEngineArray[" + i + "] name [" + engineName + "]");
                superInnovaStatEngineArray[i] = makeSuperNovaStatEngineFromConfiguration(
                        this.superInnovaStatCoreConfigurator.getSuperInnovaStatEngineConfigurationArray(i),
                        this.superInnovaStatCoreConfigurator.getStatConfigurationArray(i),
                        this.superInnovaStatCoreConfigurator.getStatSummarizerConfigurationArray(i));
                engineNameMap.put(engineName, superInnovaStatEngineCounter);
                logger.info("Properties engineNameMap name["+ engineName+"] value["+ superInnovaStatEngineCounter +"]");
            } catch (Exception e) {
                logger.error("Error engine[" + i + "]" + engineName );
            }
            superInnovaStatEngineCounter++;
        }
    }

    private SuperInnovaStatEngine makeSuperNovaStatEngineFromConfiguration(
            SuperInnovaStatEngineConfiguration superInnovaStatEngineConfiguration,
            StatGatherConfiguration[] statGatherConfiguartionArray,
            StatSummarizerConfiguration[] statSummarizerConfigurationArray
    ) throws Exception {

        logger.info("makeSuperNovaStatEngineFromConfiguration");
        SuperInnovaStatEngine superNovaStatEngine = null;

        if (superInnovaStatEngineConfiguration.getEngineType().compareToIgnoreCase("SUPERNOVA") == 0)
            superNovaStatEngine = new GeneralSuperInnovaStatEngine(this,
                    superInnovaStatEngineConfiguration,
                    statGatherConfiguartionArray,
                    statSummarizerConfigurationArray);
        else if (superInnovaStatEngineConfiguration.getEngineType().compareToIgnoreCase("PLAYBOX") == 0)
            superNovaStatEngine = new GeneralSuperInnovaStatEngine(this,
                    superInnovaStatEngineConfiguration,
                    statGatherConfiguartionArray,
                    statSummarizerConfigurationArray);
        else
            logger.error("Unknow EngineType" + "[" + superInnovaStatEngineConfiguration.getEngineType() + "]");

        return superNovaStatEngine;
    }

    @Override
    public void run() {
        for (int i = 0; i < superInnovaStatEngineCounter; i++) {
            // System.out.println("*** start Engine : "+i);
            logger.info("Start Thread Engine :" + this.superInnovaStatEngineArray[i].getSuperInnovaStatEngineConfiguration().getEngineName());
            this.superInnovaStatEngineArray[i].run();
        }
    }

    public void initCore() {
        logger.info("initCore");
        engineNameMap = new Properties();
        //statGatherExecutorServiceNormalPriority=new Executors.newFixedThreadPool(2);
        initThreadPoolExecutor();
        //Must doSuperInnovaStatCoreConfigurationProcess() after initThreadPoolExecutor();
        doSuperInnovaStatCoreConfigurationProcess();
    }

    public void initThreadPoolExecutor() {
        logger.info("initThreadPoolExecutor");
        if (useCachedThreadPool == true) {
            logger.info("ThreadPoolExecutor newCachedThreadPool");
            statGatherExecutorServiceNormalPriority = Executors.newCachedThreadPool();
            statGatherExecutorServiceHighPriority = Executors.newCachedThreadPool();
            statProcessorExecutorServiceNormalPriority = Executors.newCachedThreadPool();
            statProcessorExecutorServiceHighPriority = Executors.newCachedThreadPool();
        } else {
            logger.info("ThreadPoolExecutor newFixedThreadPool");
            statGatherExecutorServiceNormalPriority = Executors.newFixedThreadPool(statGatherNormalPriorityWorker);
            statGatherExecutorServiceHighPriority = Executors.newFixedThreadPool(statGatherHighPriorityWorker);
            statProcessorExecutorServiceNormalPriority = Executors.newFixedThreadPool(statProcessorNormalPriorityWorker);
            statProcessorExecutorServiceHighPriority = Executors.newFixedThreadPool(statProcessorHighPriorityWorker);
        }
    }

    public static void main(String[] args) {
        if (args != null && args.length > 0) {
            String rootPath = args[0];

            String logName = null;

            String logLevel = null;


            logName = args[1];
            if (args.length >= 3) {
                logLevel = args[2];
            }

            initialLogConfiguration(logName, logLevel);

            if (rootPath != null && rootPath.length() > 0) {

                SuperInnovaStatCore superInnovaStatCore = new SuperInnovaStatCore(rootPath);
                superInnovaStatCore.start();
            } else {
                logger.error("rootPath Length Error");
                System.out.println("Error : rootPath Length Error");
            }
        } else {
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
        pro.setProperty("log4j.rootLogger", "ALL, stdout, file");
        if (logLevel != null) {
            if (logLevel.toUpperCase().equals("FATAL") || logLevel.toUpperCase().equals("ERROR")
                    || logLevel.toUpperCase().equals("WARN") || logLevel.toUpperCase().equals("INFO")
                    || logLevel.toUpperCase().equals("DEBUG") || logLevel.toUpperCase().equals("TRACE")
                    || logLevel.toUpperCase().equals("OFF")) {
                pro.setProperty("log4j.rootLogger", logLevel.toLowerCase() + ", stdout, file");
            }
        }

        // Redirect log messages to console
        pro.setProperty("log4j.appender.stdout", "org.apache.log4j.ConsoleAppender");
        pro.setProperty("log4j.appender.stdout.Target", "System.out");
        pro.setProperty("log4j.appender.stdout.layout", "org.apache.log4j.PatternLayout");
        pro.setProperty("log4j.appender.stdout.layout.ConversionPattern", "%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n");

        // Redirect log messages to a log file, support file rolling.
        pro.setProperty("log4j.appender.file", "org.apache.log4j.DailyRollingFileAppender");
        pro.setProperty("log4j.appender.file.File", "/Users/Wachirawat/Desktop/PresentationEM/Build/SuperInnovaStatEngine/log/" + logName + ".error");
        // pro.setProperty("log4j.appender.file.File", "/opt/elementManagement/log/" + logName +".error");
        pro.setProperty("log4j.appender.file.DatePattern", "'.'yyyy-MM-dd");
        pro.setProperty("log4j.appender.file.layout", "org.apache.log4j.PatternLayout");
        pro.setProperty("log4j.appender.file.layout.ConversionPattern", "%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n");
        PropertyConfigurator.configure(pro);
    }
}
