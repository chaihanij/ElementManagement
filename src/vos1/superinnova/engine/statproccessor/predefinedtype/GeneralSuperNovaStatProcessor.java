/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.statproccessor.predefinedtype;

import org.apache.log4j.Logger;
import vos1.superinnova.engine.statproccessor.SuperInnovaStatEnginePropertiesLookup;
import vos1.superinnova.engine.statproccessor.SuperInnovaStatProcessor;
import vos1.superinnova.engine.statproccessor.predefinedengine.GeneralSuperInnovaStatEngine;
import vos1.superinnova.engine.statproccessor.statgathermodule.HSQLDBManager;
import vos1.superinnova.engine.statproccessor.statgathermodule.StatGathererExecutor;
import vos1.superinnova.engine.statproccessor.statgathermodule.statparser.predefined.SuperInnovaStatParser;
import vos1.superinnova.engine.statproccessor.statgathermodule.util.TimeUtil;
import vos1.superinnova.engine.statsummarizer.StatSummarizationCore;

import java.sql.ResultSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author HugeScreen
 */
public class GeneralSuperNovaStatProcessor extends SuperInnovaStatProcessor {

    final static Logger logger = Logger.getLogger(GeneralSuperNovaStatProcessor.class);


    String storageType = "mem";

    // Stat Gatherer
    StatGathererExecutor statGathererExecutor = null;
    GeneralSuperInnovaStatEngine ocfSuperInnovaStatEngine = null;

    public GeneralSuperNovaStatProcessor(GeneralSuperInnovaStatEngine ocfSuperInnovaStatEngine) {
        logger.info("Statictics processor engine : " + ocfSuperInnovaStatEngine.getSuperInnovaStatEngineConfiguration().getEngineName());
        this.storageName = ocfSuperInnovaStatEngine.getSuperInnovaStatEngineConfiguration().getEngineName();
        ocfSuperInnovaStatEngine.put("ENGINE", "StorageName", storageName);
        this.statGathererExecutor = new StatGathererExecutor(this);
        this.statGathererParser = new SuperInnovaStatParser(this.statGathererExecutor);
        this.statGatherConfiguartionArray = ocfSuperInnovaStatEngine.getStatGatherConfiguration();
        this.ocfSuperInnovaStatEngine = ocfSuperInnovaStatEngine;
        this.superInnovaStatEngine = ocfSuperInnovaStatEngine;
        initSuperInnovaStatDatabase();
        this.statSummarizationCore = new StatSummarizationCore(this);
    }


    @Override
    public SuperInnovaStatEnginePropertiesLookup getSuperInnovaStatEnginePropertiesLookup() {
        return this.ocfSuperInnovaStatEngine.getSuperInnovaStatEnginePropertiesLookup();
    }

    @Override
    public Object lookupKeyValue(String category, Object key) {
        return this.ocfSuperInnovaStatEngine.getSuperInnovaStatEnginePropertiesLookup().get(category, key);
    }

    public void initSuperInnovaStatDatabase() {
        logger.info("initSuperInnovaStatDatabase");

        dbConnection = new HSQLDBManager();
        logger.info("Connect databases. Storage type : [" + this.storageType + "] Storage name : [" + this.storageName + "]");
        dbConnection.connect(this.storageType, this.storageName);

        try {
            dbConnection.update("SET AUTOCOMMIT TRUE");
            dbConnection.update("SET DATABASE SQL LONGVAR IS LOB TRUE");
        } catch (Exception e) {
            logger.error("SET AUTOCOMMIT TRUE");
            logger.error("SET DATABASE SQL LONGVAR IS LOB TRUE");
            logger.fatal(e);
        }

        try {
            String[] createRawTableSQL = this.statGathererParser.getCreateRawTableSQL(this.ocfSuperInnovaStatEngine.getSuperInnovaStatEnginePropertiesLookup());
            StringBuilder _strCreateTable = new StringBuilder();
            for (String _ratTable : createRawTableSQL) {
                _strCreateTable.append(_ratTable + " ");
            }
            logger.debug(_strCreateTable.toString().replace("\n", " "));


            if (createRawTableSQL != null && createRawTableSQL.length > 0) {
                for (int i = 0; i < createRawTableSQL.length; i++) {
                    if (createRawTableSQL != null) {
                        dbConnection.update(createRawTableSQL[i]);
                    }
                }

            }

        } catch (Exception e) {
            logger.error("create data error");
            logger.fatal(e);
        }


    }

    public SuperInnovaStatEnginePropertiesLookup getStatEnginePropertiesLookup() {
        return this.getSuperInnovaStatEnginePropertiesLookup();
    }

    @Override
    public void run() {
        if (this.statGatherConfiguartionArray != null && this.statGatherConfiguartionArray[0] != null) {
            int fetchInterval = this.statGatherConfiguartionArray[0].getFetchInterval();
            if (fetchInterval > 0) {
                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                ScheduledFuture<?> schHandler = scheduler.scheduleAtFixedRate(statGathererExecutor, TimeUtil.calculateDelayInMillisecToNextLaunch(fetchInterval), fetchInterval * 1000, TimeUnit.MILLISECONDS);
            } else {
                logger.error("FetchInterval is Zero");
            }
        } else {
            logger.error("StatGathererConfiguration is null ");
        }
    }


    @Override
    public void initDatabase() {
        initSuperInnovaStatDatabase();
    }

    @Override
    public int updateDatabase(String sql) {
        //System.out.println("update SQL : "+sql);
        try {
            this.dbConnection.update(sql);
        } catch (Exception e) {
            logger.error("Update sql error : " + sql);
            logger.error(e);
            return -1;
        }
        return 0;
    }

    @Override
    public ResultSet queryDatabse(String sql) {
        try {
            return this.dbConnection.query(sql);
        } catch (Exception e) {
            logger.error("Query sql error : " + sql);
            logger.error(e);
            return null;
        }
    }


    @Override
    public void beginStatSummarizationProcess() {
        logger.debug("BEGIN STAT SUMMARIZATION PROCESS");
        this.statSummarizationCore.invokeStatSummarizationProcess();
    }
}
