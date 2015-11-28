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
import java.util.Arrays;
import java.util.Properties;

/**
 * @author HugeScreen
 */
public class SupernovaStatCategorizationModule extends StatSummarizationModule {

    final static Logger logger = Logger.getLogger(SupernovaStatCategorizationModule.class);

    int[] metaData = null;
    String[] columnName = null;
    String[] unitType = null;
    int[] divideBy = null;
    int[] AverageTime = null;
    int aggregrateType = StatSummarizationSmartResultSet.OPERATION_ADD;
    int row = 0;

    String[][] inputSRegexParam = null;


    /*
    public static final int REGEX_PARAM_ATTEMPT=0;
    public static final int REGEX_PARAM_SUCCESS=1;
    public static final int REGEX_PARAM_ERROR=2;
    public static final String[] REGEX_PARAM_NAME={"Attempt","Success","Error"};
    */

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


    int categorySize = 0;


    public static int MAXIMUM_CATEGORY = 96;
    boolean redundancy = false;

    public SupernovaStatCategorizationModule(StatSummarizationCore statSummarizationCore, StatSummarizerConfiguration statSummarizerConfiguration) {
        this.statSummarizationCore = statSummarizationCore;
        this.statSummarizerConfiguration = statSummarizerConfiguration;


        Properties siteProp = statSummarizationCore.getSuperInnovaStatProcessor().getSuperInnovaStatEnginePropertiesLookup().getCategory(GeneralSuperInnovaStatEngine.SITE_KEYWORD);
        Properties blockProp = statSummarizationCore.getSuperInnovaStatProcessor().getSuperInnovaStatEnginePropertiesLookup().getCategory(GeneralSuperInnovaStatEngine.BLOCK_KEYWORD);
        Properties subBlockProp = statSummarizationCore.getSuperInnovaStatProcessor().getSuperInnovaStatEnginePropertiesLookup().getCategory(GeneralSuperInnovaStatEngine.SUBBLOCK_KEYWORD);

        this.row = siteProp.size() - 1 + blockProp.size() - 1 + subBlockProp.size() - 1;

        // Initailize SupernovaStatCategorizationModule
        String aggregrateTypeText = statSummarizerConfiguration.getAdditionalProperties().getProperty("aggregrateType");

        if (aggregrateTypeText.compareToIgnoreCase("sum") == 0) {
            this.aggregrateType = StatSummarizationSmartResultSet.OPERATION_ADD;
        } else if (aggregrateTypeText.compareToIgnoreCase("max") == 0) {
            this.aggregrateType = StatSummarizationSmartResultSet.OPERATION_MAX;
        } else {
            this.aggregrateType = StatSummarizationSmartResultSet.OPERATION_ADD;
        }

        if (statSummarizerConfiguration.getAdditionalProperties().getProperty("enable_redundancy") != null) {
            this.redundancy = Boolean.valueOf(statSummarizerConfiguration.getAdditionalProperties().getProperty("enable_redundancy").toString());
        }

        // Count Category Size
        for (int i = 0; i < SupernovaStatCategorizationModule.MAXIMUM_CATEGORY; i++) {
            String runningNumber = String.format("%02d", i + 1);

            if (statSummarizerConfiguration.getAdditionalProperties().getProperty("param_category_" + runningNumber + "_vartype") != null
                    && statSummarizerConfiguration.getAdditionalProperties().getProperty("param_category_" + runningNumber + "_name") != null
                    && statSummarizerConfiguration.getAdditionalProperties().getProperty("param_category_" + runningNumber + "_unit") != null
                    && statSummarizerConfiguration.getAdditionalProperties().getProperty("param_category_" + runningNumber + "_detectFrom") != null
                    ) {
                // If configuration was found, Then make 1 increment to category Size
                this.categorySize++;
            } else {
                // If Next Token is not Found Then, Just Break the Loop
                break;
            }
        }

        // if categorySize < 1 Then Return & Print Error Log
        if (this.categorySize < 1) {
            logger.error("StatCategorizationModule, categorySize is less than 1");
            return;
        }

        // Make Category Configuration 
        // * Remark : this.categorySize+1, Because we need to add nodeCount
        columnName = new String[this.categorySize + 1];
        metaData = new int[this.categorySize + 1];
        unitType = new String[this.categorySize + 1];
        inputSRegexParam = new String[this.categorySize + 1][];
        divideBy = new int[this.categorySize + 1];
        AverageTime = new int[this.categorySize + 1];
        //aggregateType = new int[this.categorySize+1];
        for (int i = 0; i < this.categorySize; i++) {
            String runningNumber = String.format("%02d", i + 1);
            columnName[i] = statSummarizerConfiguration.getAdditionalProperties().getProperty("param_category_" + runningNumber + "_name");
            unitType[i] = statSummarizerConfiguration.getAdditionalProperties().getProperty("param_category_" + runningNumber + "_unit");
            if (statSummarizerConfiguration.getAdditionalProperties().getProperty("param_category_" + runningNumber + "_vartype").compareToIgnoreCase("float") == 0) {
                metaData[i] = StatSummarizationResultSet.TYPE_FLOAT;
            } else {
                metaData[i] = StatSummarizationResultSet.TYPE_LONG;
            }
            this.inputSRegexParam[i] = statSummarizerConfiguration.getAdditionalProperties().getProperty("param_category_" + runningNumber + "_detectFrom").split("\\|");

            //divideBy
            divideBy[i] = 1;

            if (statSummarizerConfiguration.getAdditionalProperties().getProperty("param_category_" + runningNumber + "_divideBy") != null) {
                try {
                    divideBy[i] = Integer.parseInt(statSummarizerConfiguration
                            .getAdditionalProperties()
                            .getProperty("param_category_" + runningNumber + "_divideBy"));
                } catch (Exception e) {
                    logger.warn("divideBy[" + i + "]" + "error and set value = 1");
                    divideBy[i] = 1;
                    //e.printStackTrace();
                }
            }
//            AverageTime
            try {
                if (getStatSummarizerConfiguration().getAdditionalProperties().getProperty("param_category_" + runningNumber + "_AverageTime") != null) {
                    AverageTime[i] = Integer.parseInt(getStatSummarizerConfiguration()
                            .getAdditionalProperties()
                            .getProperty("param_category_" + runningNumber + "_AverageTime"));
                } else {
                    AverageTime[i] = 1;
                }
            } catch (Exception e) {
//                AverageTime
                AverageTime[i] = 1;
                logger.error("param_category_" + runningNumber + "_AverageTime error : " + e);
            }

        }

        // Last Column is Node Count
        columnName[this.categorySize] = "NodeCount";
        metaData[this.categorySize] = StatSummarizationResultSet.TYPE_INT;
        unitType[this.categorySize] = "Server";
        inputSRegexParam[this.categorySize] = null;
        divideBy[this.categorySize] = 1;
        
        
        
        
        /*
        for(int i=0;i<metaData.length;i++){
            System.out.print(","+metaData[i]);
        }
        System.out.println("");
        
        for(int i=0;i<columnName.length;i++){
            System.out.print(","+columnName[i]);
        }
        System.out.println("");
        
        for(int i=0;i<unitType.length;i++){
            System.out.print(","+unitType[i]);
        }
        System.out.println("");
        */

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
//                this.statSummarizationSmartResultSet.dumpDataSet();
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    public void summarizeResultSet(ResultSet resultSet) {


        StatSummarizationSmartResultSet tmpStatSummarizationSmartResultSet = null;
        tmpStatSummarizationSmartResultSet = new StatSummarizationSmartResultSet(this.statSummarizationCore,
                this.row,
                this.metaData,
                this.columnName,
                this.unitType);

        try {
            logger.debug("Category size = " + this.categorySize);

            for (int i = 0; i < this.categorySize; i++) {
                logger.debug("Column : " + this.columnName[i] + " " + this.inputSRegexParam[i].length + " " + Arrays.toString(this.inputSRegexParam[i]));
            }

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
                //System.out.println("StatName : "+statName);
                boolean foundMatchesRegex = false;

                for (int i = 0; i < this.categorySize; i++) {
                    //System.out.println("Loop : '"+columnName[i]+"' searching for matches pattern.");
                    // Loop Detected From StatName
                    for (int j = 0; j < this.inputSRegexParam[i].length; j++) {
//                        logger.debug("Category["+i +"]  " +  "Detect stat size = " + this.inputSRegexParam[i].length);
                        if (this.inputSRegexParam[i][j] != null && !this.inputSRegexParam[i][j].isEmpty())
                            if (statName.matches(this.inputSRegexParam[i][j]) == true) {
                                logger.debug("Column : " + this.columnName[i] + " , [" + this.inputSRegexParam[i][j] + "]" + "is  matched " + " , [" + statName + "]");
                                //System.out.println("matcher : i:"+i+", j:"+j);
                                //ystem.out.println(" - Matched");
                                //System.out.println(statName+", "+REGEX_PARAM_NAME[i]);
                                int site = (Integer) resultSet.getObject(1 + SupernovaSuccessRateSummarizationModule.COL_SITE);
                                int block = (Integer) resultSet.getObject(1 + SupernovaSuccessRateSummarizationModule.COL_BLOCK);
                                int subBlock = (Integer) resultSet.getObject(1 + SupernovaSuccessRateSummarizationModule.COL_SUBBLOCK);
                                //System.out.println("site,Block,SubBlock "+site+", "+block+", "+subBlock);
                                // In This Situation columnNumber is I


                                //int operation=StatSummarizationSmartResultSet.OPERATION_ADD;
                                int operation = this.aggregrateType;

                                //resultSet Start with 1, So we need to add 1 to Column Position
                                Object obj = resultSet.getObject(1 + SupernovaSuccessRateSummarizationModule.COL_SUMCOUNTER);

//                          logger.debug(String.format("putx : %d, %d, %d, %d, %d, %s",site,block,subBlock,i,operation,obj));
                                // Calulate TPS
                                if (divideBy[i] > 1) {
                                    switch (metaData[i]) {
                                        case StatSummarizationResultSet.TYPE_INT:
                                            // === Strat Round Up Process =============
                                            Integer obj_before = (Integer) obj;
                                            obj = obj_before / divideBy[i];
                                            try {
                                                float tryFloatValue = 0;
                                                tryFloatValue = obj_before / (float) divideBy[i];
                                                Double tmpDoubleValue = Math.ceil(tryFloatValue);
                                                //System.out.println("INTEGER : tryFloatValue : "+tryFloatValue+", tmpDoubleValue : "+tmpDoubleValue);
                                                obj = tmpDoubleValue.intValue();

                                            } catch (Exception e) {
                                                logger.error(e);
                                                //if This is Float
                                                //e.printStackTrace();
                                            }
                                            // === End Round Up Process =============
                                            break;
                                        case StatSummarizationResultSet.TYPE_LONG:

                                            // === Strat Round Up Process =============
                                            Long long_obj_before = (Long) obj;
                                            obj = (Long) obj / divideBy[i];
                                            try {
                                                double tryFloatValue = 0;
                                                //System.out.println(long_obj_before+" Divided by "+divideBy[i]);
                                                tryFloatValue = long_obj_before / (float) divideBy[i];
                                                //System.out.println("beforeMath.ceil : "+tryFloatValue);
                                                Double tmpDoubleValue = Math.ceil(tryFloatValue);
                                                //System.out.println("AfterMath.ceil : "+tmpDoubleValue);
                                                //System.out.println("LONG : tryFloatValue : "+tryFloatValue+", tmpDoubleValue : "+tmpDoubleValue);
                                                obj = tmpDoubleValue.longValue();
                                                //System.out.println("LONG : obj : "+obj+", : "+(Long)obj);
                                            } catch (Exception e) {
                                                logger.error(e);
                                            }
                                            break;
                                        case StatSummarizationResultSet.TYPE_FLOAT:
                                            obj = (Float.parseFloat(obj.toString())) / divideBy[i];
                                            break;
                                        case StatSummarizationResultSet.TYPE_DOUBLE:
                                            obj = (Double.parseDouble(obj.toString())) / divideBy[i];
                                            break;
                                    }
                                }

                                Object min = resultSet.getObject(1 + SupernovaSuccessRateSummarizationModule.COL_MINCOUNTER);
                                Object max = resultSet.getObject(1 + SupernovaSuccessRateSummarizationModule.COL_MAXCOUNTER);

//                               logger.debug("AverageTime[" + i + "]" + AverageTime[i]);

                                try {
                                    if (min != null && max != null) {
                                        if (AverageTime[i] > 1) {
                                            switch (metaData[i]) {
                                                case StatSummarizationResultSet.TYPE_INT:
                                                    try {
                                                        Integer intBeforeMin = Integer.parseInt(min.toString());
                                                        min = intBeforeMin / AverageTime[i];

                                                        Integer intBeforeMax = Integer.parseInt(max.toString());
                                                        max = intBeforeMax / AverageTime[i];

                                                        // min
                                                        float tryFloatValueMin = 0;
                                                        tryFloatValueMin = intBeforeMin / (float) AverageTime[i];
                                                        Double tmpDoubleValueMin = Math.ceil(tryFloatValueMin);
                                                        min = tmpDoubleValueMin.intValue();

                                                        // max
                                                        float tryFloatValueMax = 0;
                                                        tryFloatValueMax = intBeforeMax / (float) AverageTime[i];
                                                        Double tmpDoubleValueMax = Math.ceil(tryFloatValueMax);
                                                        max = tmpDoubleValueMax.intValue();

                                                    } catch (Exception e) {
                                                        logger.error("TYPE_INT" + e);
                                                    }
                                                    break;
                                                case StatSummarizationResultSet.TYPE_LONG:
                                                    try {
                                                        Long longBeforeMin = Long.parseLong(min.toString());
                                                        min = longBeforeMin / AverageTime[i];

                                                        Long longBeforeMax = Long.parseLong(max.toString());
                                                        max = longBeforeMax / AverageTime[i];

                                                        double tryFloatValueMin = 0;
                                                        tryFloatValueMin = longBeforeMin / (float) AverageTime[i];
                                                        Double tmpDoubleValueMin = Math.ceil(tryFloatValueMin);
                                                        min = tmpDoubleValueMin.longValue();

                                                        double tryFloatValueMax = 0;
                                                        tryFloatValueMax = longBeforeMax / (float) AverageTime[i];
                                                        Double tmpDoubleValueMax = Math.ceil(tryFloatValueMax);
                                                        max = tmpDoubleValueMax.longValue();

                                                    } catch (Exception e) {
                                                        logger.error("TYPE_LONG :" + e);
                                                    }
                                                    break;
                                                case StatSummarizationResultSet.TYPE_FLOAT:
                                                    try {
                                                        min = (Float.parseFloat(min.toString())) / AverageTime[i];
                                                        max = (Float.parseFloat(max.toString())) / AverageTime[i];
                                                    } catch (Exception e) {
                                                        logger.error("TYPE_FLOAT :" + e);
                                                    }
                                                    break;
                                                case StatSummarizationResultSet.TYPE_DOUBLE:
                                                    try {
                                                        min = (Double.parseDouble(min.toString())) / AverageTime[i];
                                                        max = (Double.parseDouble(max.toString())) / AverageTime[i];
                                                    } catch (Exception e) {
                                                        logger.error("TYPE_DOUBLE :" + e);
                                                    }
                                                    break;
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    logger.error("Error calculate min and max");
                                    logger.error(e);
                                }

                                logger.debug(String.format("PutObject : %d, %d, %d, %d, %d, %s", site, block, subBlock, i, operation, obj));
                                logger.debug(String.format("PutMinObject : %d, %d, %d, %d, %d, %s", site, block, subBlock, i, 5, min));
                                logger.debug(String.format("PutMaxObject : %d, %d, %d, %d, %d, %s", site, block, subBlock, i, 4, max));

                                tmpStatSummarizationSmartResultSet.putObject(site, block, subBlock, i, operation, obj);
                                tmpStatSummarizationSmartResultSet.putMinObject(site, block, subBlock, i, 5, min);
                                tmpStatSummarizationSmartResultSet.putMaxObject(site, block, subBlock, i, 4, max);

                                if (!this.redundancy) {
                                    foundMatchesRegex = true;
                                    break;
                                }
                            }
                        // Check if foundMatchesRegex
                        if (!this.redundancy) {
                            if (foundMatchesRegex == true) {
                                break;
                            }
                        }
                    }
                    // Check if foundMatchesRegex
                    if (!this.redundancy) {
                        if (foundMatchesRegex == true) {
                            break;
                        }
                    }
                }
            }


            for (int i = 0; i < tmpStatSummarizationSmartResultSet.getRowCounter(); i++) {
                // Determine Host Counter
                Integer hostCounter = -1;
                Integer[] siteBlockSubBlockArray = tmpStatSummarizationSmartResultSet.getSiteBlockSubBlockMappingFromRowNumber(i);
                for (int j = 0; j < siteBlockSubBlockArray.length; j++) {
                    if (siteBlockSubBlockArray[j] != null) {
                        hostCounter = tmpStatSummarizationSmartResultSet.getHostListProp(j, siteBlockSubBlockArray[j]).size();
                    }
                }
                tmpStatSummarizationSmartResultSet.putObject(i, this.categorySize, hostCounter);
            }

            for (int i = 0; i < tmpStatSummarizationSmartResultSet.getMinRowCounter(); i++) {
                // Determine Host Counter
                Integer hostMin = -1;
                Integer[] siteBlockSubBlockArray = tmpStatSummarizationSmartResultSet.getSiteBlockSubBlockMappingFromMinRowNumber(i);
                for (int j = 0; j < siteBlockSubBlockArray.length; j++) {
                    if (siteBlockSubBlockArray[j] != null) {
                        hostMin = tmpStatSummarizationSmartResultSet.getHostMinListProp(j, siteBlockSubBlockArray[j]).size();
                    }
                }
                tmpStatSummarizationSmartResultSet.putMinObject(i, this.categorySize, hostMin);
            }

            for (int i = 0; i < tmpStatSummarizationSmartResultSet.getMaxRowCounter(); i++) {
                // Determine Host Counter
                Integer hostMax = -1;
                Integer[] siteBlockSubBlockArray = tmpStatSummarizationSmartResultSet.getSiteBlockSubBlockMappingFromMaxRowNumber(i);
                for (int j = 0; j < siteBlockSubBlockArray.length; j++) {
                    if (siteBlockSubBlockArray[j] != null) {
                        hostMax = tmpStatSummarizationSmartResultSet.getHostMaxListProp(j, siteBlockSubBlockArray[j]).size();
                    }
                }
                tmpStatSummarizationSmartResultSet.putMaxObject(i, this.categorySize, hostMax);
            }
            this.statSummarizationSmartResultSet = tmpStatSummarizationSmartResultSet;
        } catch (Exception e) {
            logger.error("statSummarizationSmartResultSet = null");
            this.statSummarizationSmartResultSet = null;
            logger.error(e);
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

}
