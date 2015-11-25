/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.statsummarizer.templates;

import org.apache.log4j.Logger;
import vos1.superinnova.engine.statproccessor.predefinedengine.GeneralSuperInnovaStatEngine;
import vos1.superinnova.engine.statsummarizer.*;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;

/**
 * @author HugeScreen
 */
public class SupernovaSuccessRateSummarizationModule extends StatSummarizationModule {

    final static Logger logger = Logger.getLogger(SupernovaSuccessRateSummarizationModule.class);

    int[] metaData = null;
    String[] columnName = null;
    String[] unitType = null;
    int row = 0;

    String[][] regexParam = null;
    String paramPrefix = null;
    public static final int REGEX_PARAM_ATTEMPT = 0;
    public static final int REGEX_PARAM_SUCCESS = 1;
    public static final int REGEX_PARAM_ERROR = 2;
    public static final String[] REGEX_PARAM_NAME = {"Attempt", "Success", "Error"};

    public static final int COL_SITE = 0;
    public static final int COL_BLOCK = 1;
    public static final int COL_SUBBLOCK = 2;
    public static final int COL_DATE = 3;
    public static final int COL_HOSTNAME = 4;
    public static final int COL_STATNAME = 5;
    public static final int COL_MINCOUNTER = 6;
    public static final int COL_MAXCOUNTER = 7;
    public static final int COL_AVERAGECOUNTER = 8;
    public static final int COL_SUMCOUNTER = 9;

    public static final int OUTPUT_COL_ATTEMPT = 0;
    public static final int OUTPUT_COL_SUCCESS = 1;
    public static final int OUTPUT_COL_ERROR = 2;
    public static final int OUTPUT_COL_PERCENTSUCCESS = 3;
    public static final int OUTPUT_COL_PERCENTERROR = 4;
    public static final int OUTPUT_COL_NODECOUNT = 5;
    //{"Attempt","Success","Error","%Success","%Error","NodeCount"

    boolean redundancy = false;

    public SupernovaSuccessRateSummarizationModule(StatSummarizationCore statSummarizationCore, StatSummarizerConfiguration statSummarizerConfiguration) {

        this.statSummarizationCore = statSummarizationCore;
        this.statSummarizerConfiguration = statSummarizerConfiguration;

        Properties siteProp = statSummarizationCore.getSuperInnovaStatProcessor().getSuperInnovaStatEnginePropertiesLookup().getCategory(GeneralSuperInnovaStatEngine.SITE_KEYWORD);
        Properties blockProp = statSummarizationCore.getSuperInnovaStatProcessor().getSuperInnovaStatEnginePropertiesLookup().getCategory(GeneralSuperInnovaStatEngine.BLOCK_KEYWORD);
        Properties subBlockProp = statSummarizationCore.getSuperInnovaStatProcessor().getSuperInnovaStatEnginePropertiesLookup().getCategory(GeneralSuperInnovaStatEngine.SUBBLOCK_KEYWORD);

        this.row = siteProp.size() - 1 + blockProp.size() - 1 + subBlockProp.size() - 1;

        columnName = new String[]{"Attempt", "Success", "Error", "%Success", "%Error", "NodeCount"};
        metaData = new int[]{StatSummarizationResultSet.TYPE_LONG, StatSummarizationResultSet.TYPE_LONG, StatSummarizationResultSet.TYPE_LONG, StatSummarizationResultSet.TYPE_FLOAT, StatSummarizationResultSet.TYPE_FLOAT, StatSummarizationResultSet.TYPE_INT};
        unitType = new String[]{"Transaction", "Transaction", "Transaction", "%", "%", "Server"};
        //statSummarizationResultSet = new StatSummarizationResultSet(row,metaData,columnName,unitType);

        regexParam = new String[3][];
        this.regexParam[REGEX_PARAM_ATTEMPT] = statSummarizerConfiguration.getAdditionalProperties().getProperty("param_attempt").split("\\|");
        this.regexParam[REGEX_PARAM_SUCCESS] = statSummarizerConfiguration.getAdditionalProperties().getProperty("param_success").split("\\|");
        this.regexParam[REGEX_PARAM_ERROR] = statSummarizerConfiguration.getAdditionalProperties().getProperty("param_error").split("\\|");
        this.paramPrefix = statSummarizerConfiguration.getAdditionalProperties().getProperty("param_prefix");
        if (this.paramPrefix != null) {
            for (int i = 0; i < columnName.length; i++) {
                columnName[i] = this.paramPrefix + "." + columnName[i];
            }
        }

        if (statSummarizerConfiguration.getAdditionalProperties().getProperty("enable_redundancy") != null) {
            redundancy = Boolean.valueOf(statSummarizerConfiguration.getAdditionalProperties().getProperty("enable_redundancy").toString());
        }
    }

    @Override
    public ResultSet fetchDataFromStorage() {
        String rawTableName = "raw_" + this.getStatSummarizationCore().getSuperInnovaStatProcessor().lookupKeyValue("ENGINE", "StorageName");
        String selectSQL = "select * from " + rawTableName;
        ResultSet selectResultSet = this.statSummarizationCore.getSuperInnovaStatProcessor().queryDatabse(selectSQL);
        return selectResultSet;
    }

    @Override
    public void summarizeData(ResultSet resultSet) {
        if (resultSet != null) {
            try {
//                HSQLDBManager.dump(resultSet);
                summarizeResultSet(resultSet);
                //this.statSummarizationSmartResultSet.dumpDataSet();
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    @Override
    public Object[] getSummarizationResultSet(String key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        return this.statSummarizerConfiguration.getStatName() + " : " + this.statSummarizerConfiguration.getSummarizationModule();
    }

    public void summarizeResultSet(ResultSet resultSet) {

        StatSummarizationSmartResultSet tmpStatSummarizationSmartResultSet = null;

        tmpStatSummarizationSmartResultSet = new StatSummarizationSmartResultSet(this.statSummarizationCore,
                this.row,
                this.metaData,
                this.columnName,
                this.unitType);
        //tmpStatSummarizationSmartResultSet = new StatSummarizationSmartResultSet(this.statSummarizationCore,this.row,this.metaData,this.columnName,this.unitType);

        try {

            for (; resultSet.next(); ) {
                // Check Date
                Timestamp dateTimeStamp = resultSet.getTimestamp(1 + SupernovaSuccessRateSummarizationModule.COL_DATE);
                if (tmpStatSummarizationSmartResultSet.getMaxDate() == null) {
                    tmpStatSummarizationSmartResultSet.setMinDate(dateTimeStamp);
                    tmpStatSummarizationSmartResultSet.setMaxDate(dateTimeStamp);
                } else {
                    if (tmpStatSummarizationSmartResultSet.getMaxDate().compareTo(dateTimeStamp) < 0) {
                        tmpStatSummarizationSmartResultSet.setMinDate(tmpStatSummarizationSmartResultSet.getMaxDate());
                        tmpStatSummarizationSmartResultSet.setMaxDate(dateTimeStamp);
                    }
                }

                // Check Regex
                String statName = (String) resultSet.getObject(1 + SupernovaSuccessRateSummarizationModule.COL_STATNAME);
//              logger.debug("StatName : " + statName);

                boolean foundMatchesRegex = false;
                for (int i = 0; i < this.regexParam.length; i++) {

                    for (int j = 0; j < this.regexParam[i].length; j++) {
                        //System.out.println(i+", "+j+", StatName : "+statName+", Regex : "+this.regexParam[i][j]);
                        if (statName.matches(this.regexParam[i][j]) == true) {
                            //System.out.println(statName+", "+REGEX_PARAM_NAME[i]);
                            int site = (Integer) resultSet.getObject(1 + SupernovaSuccessRateSummarizationModule.COL_SITE);
                            int block = (Integer) resultSet.getObject(1 + SupernovaSuccessRateSummarizationModule.COL_BLOCK);
                            int subBlock = (Integer) resultSet.getObject(1 + SupernovaSuccessRateSummarizationModule.COL_SUBBLOCK);
                            // In This Situation columnNumber is I
                            int columnNumber = -1;
                            if (i == REGEX_PARAM_ATTEMPT) {
                                columnNumber = SupernovaSuccessRateSummarizationModule.OUTPUT_COL_ATTEMPT;
                            } else if (i == REGEX_PARAM_SUCCESS) {
                                columnNumber = SupernovaSuccessRateSummarizationModule.OUTPUT_COL_SUCCESS;
                            } else if (i == REGEX_PARAM_ERROR) {
                                columnNumber = SupernovaSuccessRateSummarizationModule.OUTPUT_COL_ERROR;
                            }
                            int operation = StatSummarizationSmartResultSet.OPERATION_ADD;
                            //resultSet Start with 1, So we need to add 1 to Column Position
                            Object obj = resultSet.getObject(1 + SupernovaSuccessRateSummarizationModule.COL_SUMCOUNTER);
                            logger.debug("putObject : " + site + "," + block + "," + subBlock + "," + this.REGEX_PARAM_NAME[columnNumber] + ",ADD," + obj);
                            if (obj != null) {
                                tmpStatSummarizationSmartResultSet.putObject(site, block, subBlock, columnNumber, operation, obj);
                            } else {
                                logger.warn("Skip Null Object : " + site + "," + block + "," + subBlock + "," + columnNumber + "," + operation + "," + obj);
                            }
                            if (!this.redundancy) {
                                foundMatchesRegex = true;
                                break;
                            }
                        }
                    }
                    if (!this.redundancy) {
                        if (foundMatchesRegex == true) {
                            break;
                        }
                    }
                }
            }// End For #1
            
            /*
                 public static final int OUTPUT_COL_ATTEMPT=0;
    public static final int OUTPUT_COL_SUCCESS=1;
    public static final int OUTPUT_COL_ERROR=2;
    public static final int OUTPUT_COL_PERCENTSUCCESS=3;
    public static final int OUTPUT_COL_PERCENTERROR=4;    
    public static final int OUTPUT_COL_NODECOUNT=5; 
             */
            logger.debug("Row counter size : " + tmpStatSummarizationSmartResultSet.getRowCounter());
            //tmpStatSummarizationSmartResultSet.dumpDataSet();

            for (int i = 0; i < tmpStatSummarizationSmartResultSet.getRowCounter(); i++) {

                Long attempt = (Long) tmpStatSummarizationSmartResultSet.getObject(i, OUTPUT_COL_ATTEMPT);
                Long success = (Long) tmpStatSummarizationSmartResultSet.getObject(i, OUTPUT_COL_SUCCESS);
                Long error = (Long) tmpStatSummarizationSmartResultSet.getObject(i, OUTPUT_COL_ERROR);
                Float successRate = -1f;
                Float errorRate = -1f;

//                logger.debug("[DEBUG] i : +" + i + ", Attempt : " + OUTPUT_COL_ATTEMPT);
//                logger.debug("[DEBUG] i : +" + i + ", Success : " + OUTPUT_COL_SUCCESS);
//                logger.debug("[DEBUG] i : +" + i + ", Error : " + OUTPUT_COL_ERROR);

                logger.debug("Attempt : " + attempt + ", success : " + success + ", error : " + error);
                //tmpStatSummarizationSmartResultSet.dumpToString();
                if (attempt != null && attempt >= 0) {
                    successRate = (float) success / (float) attempt * 100f;

                    //errorRate=(float)((float)error/attempt*100);
                    if (successRate > 100) {
                        successRate = 100f;
                    }
                    errorRate = 100f - successRate;
                }
                logger.debug("Calculate success value =  " + successRate + ", error value = " + errorRate);

                tmpStatSummarizationSmartResultSet.putObject(i, OUTPUT_COL_PERCENTSUCCESS, successRate);
                tmpStatSummarizationSmartResultSet.putObject(i, OUTPUT_COL_PERCENTERROR, errorRate);

                // Determine Host Counter
                Integer hostCounter = -1;
                Integer[] siteBlockSubBlockArray = tmpStatSummarizationSmartResultSet.getSiteBlockSubBlockMappingFromRowNumber(i);
                for (int j = 0; j < siteBlockSubBlockArray.length; j++) {
                    if (siteBlockSubBlockArray[j] != null) {
                        hostCounter = tmpStatSummarizationSmartResultSet.getHostListProp(j, siteBlockSubBlockArray[j]).size();

                        /*
                        String[] cn = new String[]{"site","block","subblock"};
                        System.out.println("i : "+i+", j : "+j+", ["+cn[j]+"], hostcounter : "+hostCounter);
                        Enumeration<Object> e = tmpStatSummarizationSmartResultSet.getHostListProp(j, siteBlockSubBlockArray[j]).elements();
                        while(e.hasMoreElements()){
                            System.out.println(""+e.nextElement());
                        }
                        */

                                /*
                        if(j==StatSummarizationSmartResultSet.MAP_SITE){
                            hostCounter=tmpStatSummarizationSmartResultSet.getHostListProp(j, siteBlockSubBlockArray[j]).size();
                        }
                        else if(j==StatSummarizationSmartResultSet.MAP_BLOCK){
                            hostCounter=tmpStatSummarizationSmartResultSet.getHostListProp(j, siteBlockSubBlockArray[j]).size();
                        }
                        else if(j==StatSummarizationSmartResultSet.MAP_SUBBLOCK){
                            hostCounter=tmpStatSummarizationSmartResultSet.getHostListProp(j, siteBlockSubBlockArray[j]).size();
                        }
                        */
                    }
                }
                //System.out.println("putObject: row="+i+", columne="+OUTPUT_COL_NODECOUNT+", hostCounter="+hostCounter);
                tmpStatSummarizationSmartResultSet.putObject(i, OUTPUT_COL_NODECOUNT, hostCounter);

            }// End For #2
            this.statSummarizationSmartResultSet = tmpStatSummarizationSmartResultSet;
        } catch (Exception e) {
            this.statSummarizationSmartResultSet = null;
            logger.error(e);
        }
    }

}
