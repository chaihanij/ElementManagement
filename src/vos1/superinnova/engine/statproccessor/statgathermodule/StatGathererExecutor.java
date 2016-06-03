/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.statproccessor.statgathermodule;

import org.apache.log4j.Logger;
import vos1.superinnova.engine.statproccessor.SuperInnovaStatProcessor;
import vos1.superinnova.engine.statproccessor.statgathermodule.http.HTTPStatGatherer;
import vos1.superinnova.engine.statproccessor.statgathermodule.statparser.predefined.SuperInnovaStatParser;

import java.util.concurrent.ExecutorService;

/**
 * @author HugeScreen
 */
public class StatGathererExecutor extends Thread {

    final static Logger logger = Logger.getLogger(StatGathererExecutor.class);

    SuperInnovaStatProcessor superInnovaStatProcessor = null;
    int failedThreadCounter = -1;
    int completedThreadCounter = -1;
    int maxThreadCounter = -1;

    public StatGathererExecutor(SuperInnovaStatProcessor superInnovaStatProcessor) {
        this.superInnovaStatProcessor = superInnovaStatProcessor;
    }

    public Object lookupKeyValue(String category, Object key) {
        return this.superInnovaStatProcessor.lookupKeyValue(category, key);
    }

    public SuperInnovaStatProcessor getSuperInnovaStatProcessor() {
        return superInnovaStatProcessor;
    }

    public synchronized void tickCompletedThreadCounter() {
        //System.out.println("+++++++++++++ Call OMG");
        this.completedThreadCounter++;
    }

    public synchronized void resetCompletedThreadCounter() {
        this.completedThreadCounter = 0;
    }

    public synchronized void tickFailedThreadCounter() {
        //System.out.println("+++++++++++++ Call Failed OMG");
        this.failedThreadCounter++;
    }

    public synchronized void resetFailedThreadCounter() {
        this.failedThreadCounter = 0;
    }

    public void run() {
        try {
            try {

                StatGathererParser statGathererParser = new SuperInnovaStatParser(this);

                String[] truncateDatabaseSQL = statGathererParser.getTruncateRawTableSQL(this.superInnovaStatProcessor.getSuperInnovaStatEnginePropertiesLookup());

                StringBuilder _logDebug = new StringBuilder();
                for (String _truncateDatabaseSQL : truncateDatabaseSQL) {
                    _logDebug.append(_truncateDatabaseSQL);
                }
                logger.debug(_logDebug);

                if (truncateDatabaseSQL != null) {
                    for (String truncateDatabaseSQL1 : truncateDatabaseSQL) {
                        if (truncateDatabaseSQL1 != null && truncateDatabaseSQL1.length() > 0) {
                            this.superInnovaStatProcessor.updateDatabase(truncateDatabaseSQL1);
                        }
                    }
                }

            } catch (Exception e) {
                logger.error("Error messages: " + e.getMessage());
            }

            // Gather & Insert
            if (this.superInnovaStatProcessor != null) {
                StatGatherConfiguration[] statGatherConfiguartionArray = this.superInnovaStatProcessor.getStatGatherConfiguartionArray();
                resetCompletedThreadCounter();
                resetFailedThreadCounter();
                this.maxThreadCounter = statGatherConfiguartionArray.length;
                logger.info("Start gather statistics engine [ " + this.getSuperInnovaStatProcessor().getSuperInnovaStatEngine().getSuperInnovaStatEngineConfiguration().getEngineName() + " : " + this.completedThreadCounter + " / " + this.maxThreadCounter + "]");
                for (int i = 0; i < statGatherConfiguartionArray.length; i++) {
                    ExecutorService executorService = null;
                    StatGathererExecutorChild statGathererExecutorChild = new StatGathererExecutorChild(this, statGatherConfiguartionArray[i]);
                    if (statGatherConfiguartionArray[i].getThreadPriority() == StatGatherConfiguration.PRIORITY_HIGH) {
                        executorService = this.superInnovaStatProcessor
                                .getSuperInnovaStatEngine()
                                .getSuperInnovaStatCore()
                                .getStatGatherExecutorServiceHighPriority();
                    } else {
                        executorService = this.superInnovaStatProcessor
                                .getSuperInnovaStatEngine()
                                .getSuperInnovaStatCore()
                                .getStatGatherExecutorServiceNormalPriority();
                    }
                    executorService.execute(statGathererExecutorChild);
                }

                boolean completeAllThread = false;
                while (completeAllThread == false) {
                    if ((this.failedThreadCounter + this.completedThreadCounter) >= maxThreadCounter) {
                        completeAllThread = true;
                        logger.info("Start gather statistics engine  : " + this.getSuperInnovaStatProcessor().getSuperInnovaStatEngine().getSuperInnovaStatEngineConfiguration().getEngineName() + "complete [" + this.completedThreadCounter + "/" + maxThreadCounter + "]");
                        this.superInnovaStatProcessor.beginStatSummarizationProcess();
                    } else {
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            logger.error("Error messages: " + e.getMessage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error messages: " + e.getMessage());
        }
    }
}

class StatGathererExecutorChild implements Runnable {

    final static Logger logger = Logger.getLogger(StatGathererExecutorChild.class);

    StatGathererExecutor statGathererExecutor;
    StatGatherConfiguration statGatherConfiguartion;

    public StatGathererExecutorChild(StatGathererExecutor statGathererExecutor, StatGatherConfiguration statGatherConfiguartion) {
        this.statGathererExecutor = statGathererExecutor;
        this.statGatherConfiguartion = statGatherConfiguartion;
    }

    public void run() {
        try {
            if (this.statGatherConfiguartion != null) {
                logger.debug("Gather statistics : " + statGatherConfiguartion.toString());
                StatGatherer statGatherer = null;
                String output = null;
                if (this.statGatherConfiguartion.getFetchType() == StatGatherConfiguration.FETCHTYPE_HTTP) {
                    statGatherer = new HTTPStatGatherer(statGatherConfiguartion.getUrl(), this.statGatherConfiguartion.fetchTimeOut);
                    output = statGatherer.gather();
                } else {
                    logger.error("ERROR : UNKNOWN FETCH TYPE");
                    return;
                }

                StatGathererParser statGathererParser = new SuperInnovaStatParser(this.statGathererExecutor);
                String[] insertIntoRawTableSQL = null;

                logger.debug("Insert data to memory");
                if (statGathererParser != null && output != null) {
                    insertIntoRawTableSQL = statGathererParser.getInsertRawTableSQL(output, statGatherConfiguartion, this.statGathererExecutor.superInnovaStatProcessor.getSuperInnovaStatEnginePropertiesLookup());

                }

                if (insertIntoRawTableSQL != null && insertIntoRawTableSQL.length > 0) {
                    int insetIntoRawTableSQLSuccess = 0;
                    for (int i = 0; i < insertIntoRawTableSQL.length; i++) {
                        if (insertIntoRawTableSQL[i] != null) {
                            try {
                                if (insertIntoRawTableSQL[i] != null && insertIntoRawTableSQL[i].length() > 0) {
                                    this.statGathererExecutor.getSuperInnovaStatProcessor().updateDatabase(insertIntoRawTableSQL[i]);
                                    insetIntoRawTableSQLSuccess++;
                                }
                            } catch (Exception e) {
                                logger.error("Error SQL updateDatabase: " + insertIntoRawTableSQL[i]);
                                logger.error("Error messages: " + e.getMessage());
                            }
                        }
                    }

                    if (insetIntoRawTableSQLSuccess >= insertIntoRawTableSQL.length) {
                        this.statGathererExecutor.tickCompletedThreadCounter();
                        return;
                    }
                }
            }
        } catch (Exception e) {
            this.statGathererExecutor.tickFailedThreadCounter();
            logger.error("Error messages: " + e.getMessage());
        }
        this.statGathererExecutor.tickFailedThreadCounter();
    }
}
