/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.statproccessor;

import org.apache.log4j.Logger;
import vos1.superinnova.engine.statinquiry.SuperInnovaStatInquiryCore;
import vos1.superinnova.engine.statproccessor.predefinedengine.GeneralSuperInnovaStatEngine;
import vos1.superinnova.engine.statproccessor.statgathermodule.StatGatherConfiguration;
import vos1.superinnova.engine.statsummarizer.StatSummarizerConfiguration;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

        if (engineIndex != null) {
            logger.debug("Engine "+engineName+" found at " + engineIndex);
            return this.superInnovaStatEngineArray[engineIndex];
        } else {
            logger.error("Engine "+engineName+"not found.");
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

    public static String logName = null;
    public static String logLevel = null;

    public static void main(String[] args) {
        if (args != null && args.length > 0) {
            String rootPath = args[0];

             logName = null;

             logLevel = null;


            logName = args[1];
            if (args.length >= 3) {
                logLevel = args[2];
            }

            LogConfiguration.initialLogConfiguration(logName, logLevel);

            if (rootPath != null && rootPath.length() > 0) {

                SuperInnovaStatCore superInnovaStatCore = new SuperInnovaStatCore(rootPath);
                superInnovaStatCore.start();
            } else {
                logger.error("Please check configuration path.");
            }
        } else {
            logger.error("Please input configuration path.");
        }
    }
}
