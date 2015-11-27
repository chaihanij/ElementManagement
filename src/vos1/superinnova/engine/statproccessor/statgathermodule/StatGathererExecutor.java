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


            // Select RawTable Before Truncate
            try {

                StatGathererParser statGathererParser = new SuperInnovaStatParser(this);
                
                /* ===== Test Dump Result Set =====
                String[] selectRawTableSQL = statGathererParser.getSelectRawTableSQL(this.superInnovaStatProcessor.getSuperInnovaStatEnginePropertiesLookup());
                if(selectRawTableSQL!=null && selectRawTableSQL.length>0){
                    for(int i=0;i<selectRawTableSQL.length;i++){
                        System.out.println("BEGINE DUMP RESULTSET");
                        ResultSet kak =this.superInnovaStatProcessor.queryDatabse(selectRawTableSQL[i]);
                        if(kak!=null){
                            HSQLDBManager.dump(kak);
                        }
                        System.out.println("DONE DUMP RESULTSET");
                    }
                }
                * ===== Test Dump Result Set =====*/
                // Truncate RawTable Before New Insert
                String[] truncateDatabaseSQL = statGathererParser.getTruncateRawTableSQL(this.superInnovaStatProcessor.getSuperInnovaStatEnginePropertiesLookup());

                StringBuilder _logDebug = new StringBuilder();
                for (String _truncateDatabaseSQL : truncateDatabaseSQL)
                    _logDebug.append(_truncateDatabaseSQL);
                logger.debug(_logDebug);

                if (truncateDatabaseSQL != null) {
                    for (int i = 0; i < truncateDatabaseSQL.length; i++) {
                        if (truncateDatabaseSQL[i] != null && truncateDatabaseSQL[i].length() > 0) {
                            this.superInnovaStatProcessor.updateDatabase(truncateDatabaseSQL[i]);
                        }
                    }
                }

            } catch (Exception e) {
                logger.error(e);
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
//                      System.out.println("==*==*==*== All Thread Complete : "+this.getSuperInnovaStatProcessor().getSuperInnovaStatEngine().getSuperInnovaStatEngineConfiguration().getEngineName()+" ==================== ( "+this.completedThreadCounter+" / "+maxThreadCounter+" )");
                        logger.info("Start gather statistics engine  : " + this.getSuperInnovaStatProcessor().getSuperInnovaStatEngine().getSuperInnovaStatEngineConfiguration().getEngineName() + "complete [" + this.completedThreadCounter + "/" + maxThreadCounter + "]");
                        this.superInnovaStatProcessor.beginStatSummarizationProcess();
                    } else {
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            logger.error(e);
//                            e.printStackTrace();
                        }
                    }
                }

            }
        } catch (Exception e) {
            logger.error(e);
//            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        /*
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        
        StatGathererExecutor sge = new StatGathererExecutor();
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        String tmp = sdf.format(new java.util.Date());
        System.out.println("Start : "+tmp);          
        ScheduledFuture<?> schHandler = scheduler.scheduleAtFixedRate(sge, TimeUtil.calculateDelayInMillisecToNextLaunch(5), 5000, TimeUnit.MILLISECONDS);
        * */
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

                    // Gather
                    statGatherer = new HTTPStatGatherer(statGatherConfiguartion.getUrl());
                    output = statGatherer.gather();
                    // Prepare Parser Output
                } else {
                    logger.error("ERROR : UNKNOWN FETCH TYPE");
//                    System.out.println("ERROR : UNKNOWN FETCH TYPE");
                    return;
                }

                // Prepare Parser Output
                StatGathererParser statGathererParser = new SuperInnovaStatParser(this.statGathererExecutor);
                String[] insertIntoRawTableSQL = null;
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
//                                System.out.println("Error SQL : " +
                                logger.error(e);
//                                e.printStackTrace();
                            }
                        }
                    }

                    if (insetIntoRawTableSQLSuccess >= insertIntoRawTableSQL.length) {
                        this.statGathererExecutor.tickCompletedThreadCounter();
                        return;
                    }
                }// End If insertIntoRawTableSQL            
            }
        } catch (Exception e) {
            this.statGathererExecutor.tickFailedThreadCounter();
            logger.error(e);
//            e.printStackTrace();
        }
        this.statGathererExecutor.tickFailedThreadCounter();
    }
}