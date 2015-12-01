/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.statsummarizer;

import org.apache.log4j.Logger;
import vos1.superinnova.engine.statproccessor.predefinedengine.GeneralSuperInnovaStatEngine;
import vos1.superinnova.engine.statproccessor.statgathermodule.StatGathererParser;
import vos1.superinnova.util.MinMaxAverageSumFinder;
import vos1.superinnova.util.PRTGUtil;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.Properties;

/**
 * @author HugeScreen
 */
public class StatSummarizationSmartResultSet extends StatSummarizationResultSet {

    final static Logger logger = Logger.getLogger(StatSummarizationSmartResultSet.class);

    StatSummarizationCore statSummarizationCore;

    Properties[] siteLevelHostListArray = null;
    Properties[] blockLevelHostListArray = null;
    Properties[] subBlockLevelHostListArray = null;

    Properties[] siteLevelMinHostListArray = null;
    Properties[] blockLevelMinHostListArray = null;
    Properties[] subBlockLevelMinHostListArray = null;

    Properties[] siteLevelMaxHostListArray = null;
    Properties[] blockLevelMaxHostListArray = null;
    Properties[] subBlockLevelMaxHostListArray = null;

    Properties reverseRowMapping = null;
    Properties reverseMinRowMapping = null;
    Properties reverseMaxRowMapping = null;

    public static final int MAP_SITE = 0;
    public static final int MAP_BLOCK = 1;
    public static final int MAP_SUBBLOCK = 2;

    int[] siteLevelLocationArray = null;
    int[] blockLevelLocationArray = null;
    int[] subBlockLevelLocationArray = null;

    int[] siteLevelMinLocationArray = null;
    int[] blockLevelMinLocationArray = null;
    int[] subBlockLevelMinLocationArray = null;

    int[] siteLevelMaxLocationArray = null;
    int[] blockLevelMaxLocationArray = null;
    int[] subBlockLevelMaxLocationArray = null;

    int rowCounter = 0;
    int rowMinCounter = 0;
    int rowMaxCounter = 0;

    public static final int OPERATION_REPLACE = 0;
    public static final int OPERATION_ADD = 1;
    public static final int OPERATION_SUBTRACT = 2;
    public static final int OPERATION_MAX = 3;

    public static final int OPERATION_MAXTPS = 4;
    public static final int OPERATION_MINTPS = 5;

    protected Timestamp minDate = null;
    protected Timestamp maxDate = null;

    public Timestamp getMinDate() {
        return minDate;
    }

    public void setMinDate(Timestamp minDate) {
        this.minDate = minDate;
    }

    public Timestamp getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(Timestamp maxDate) {
        this.maxDate = maxDate;
    }

    public int getRowCounter() {
        return rowCounter;
    }

    public int getMinRowCounter() {
        return rowMinCounter;
    }

    public int getMaxRowCounter() {
        return rowMaxCounter;
    }

    public Properties getHostListProp(int map, int i) {
        if (map == MAP_SITE) {
            return siteLevelHostListArray[i];
        } else if (map == MAP_BLOCK) {
            return blockLevelHostListArray[i];
        } else if (map == MAP_SUBBLOCK) {
            return subBlockLevelHostListArray[i];
        } else {
            return null;
        }
    }

    public Properties getHostMinListProp(int map, int i) {
        if (map == MAP_SITE) {
            return siteLevelMinHostListArray[i];
        } else if (map == MAP_BLOCK) {
            return blockLevelMinHostListArray[i];
        } else if (map == MAP_SUBBLOCK) {
            return subBlockLevelMinHostListArray[i];
        } else {
            return null;
        }
    }

    public Properties getHostMaxListProp(int map, int i) {
        if (map == MAP_SITE) {
            return siteLevelMaxHostListArray[i];
        } else if (map == MAP_BLOCK) {
            return blockLevelMaxHostListArray[i];
        } else if (map == MAP_SUBBLOCK) {
            return subBlockLevelMaxHostListArray[i];
        } else {
            return null;
        }
    }

    public StatSummarizationSmartResultSet(StatSummarizationCore statSummarizationCore, int row, int[] metaData, String[] columnName, String[] unitType) {
        super(row, metaData, columnName, unitType);

        this.statSummarizationCore = statSummarizationCore;

        Properties siteProp = statSummarizationCore.getSuperInnovaStatProcessor().getSuperInnovaStatEnginePropertiesLookup().getCategory(GeneralSuperInnovaStatEngine.SITE_KEYWORD);
        Properties blockProp = statSummarizationCore.getSuperInnovaStatProcessor().getSuperInnovaStatEnginePropertiesLookup().getCategory(GeneralSuperInnovaStatEngine.BLOCK_KEYWORD);
        Properties subBlockProp = statSummarizationCore.getSuperInnovaStatProcessor().getSuperInnovaStatEnginePropertiesLookup().getCategory(GeneralSuperInnovaStatEngine.SUBBLOCK_KEYWORD);

        int siteSize = siteProp.size() - 1;
        int blockSize = blockProp.size() - 1;
        int subBlockSize = subBlockProp.size() - 1;

        siteLevelLocationArray = new int[siteSize];
        blockLevelLocationArray = new int[blockSize];
        subBlockLevelLocationArray = new int[subBlockSize];

        siteLevelMinLocationArray = new int[siteSize];
        blockLevelMinLocationArray = new int[blockSize];
        subBlockLevelMinLocationArray = new int[subBlockSize];

        siteLevelMaxLocationArray = new int[siteSize];
        blockLevelMaxLocationArray = new int[blockSize];
        subBlockLevelMaxLocationArray = new int[subBlockSize];

        siteLevelHostListArray = new Properties[siteSize];
        blockLevelHostListArray = new Properties[blockSize];
        subBlockLevelHostListArray = new Properties[subBlockSize];

        siteLevelMinHostListArray = new Properties[siteSize];
        blockLevelMinHostListArray = new Properties[blockSize];
        subBlockLevelMinHostListArray = new Properties[subBlockSize];

        siteLevelMaxHostListArray = new Properties[siteSize];
        blockLevelMaxHostListArray = new Properties[blockSize];
        subBlockLevelMaxHostListArray = new Properties[subBlockSize];

        reverseRowMapping = new Properties();
        reverseMinRowMapping = new Properties();
        reverseMaxRowMapping = new Properties();

        clearCounter();
        //this.row=siteProp.stringPropertyNames().size()+blockProp.stringPropertyNames().size()+subBlockProp.stringPropertyNames().size()+1;
    }

    public void clearCounter() {
        minDate = null;
        maxDate = null;

        reverseRowMapping = new Properties();
        reverseMaxRowMapping = new Properties();
        reverseMinRowMapping = new Properties();

        for (int i = 0; i < this.siteLevelHostListArray.length; i++) {
            // Check if not null then clear
            if (this.siteLevelHostListArray[i] == null) {
                this.siteLevelHostListArray[i] = new Properties();
            } else {
                this.siteLevelHostListArray[i].clear();
            }
        }
        for (int i = 0; i < this.blockLevelHostListArray.length; i++) {
            // Check if not null then clear
            if (this.blockLevelHostListArray[i] == null) {
                this.blockLevelHostListArray[i] = new Properties();
            } else {
                this.blockLevelHostListArray[i].clear();
            }
        }
        for (int i = 0; i < this.subBlockLevelHostListArray.length; i++) {
            if (this.subBlockLevelHostListArray[i] == null) {
                this.subBlockLevelHostListArray[i] = new Properties();
            } else {
                this.subBlockLevelHostListArray[i].clear();
            }
        }
        for (int i = 0; i < this.siteLevelLocationArray.length; i++) {
            this.siteLevelLocationArray[i] = -1;
        }
        for (int i = 0; i < this.blockLevelLocationArray.length; i++) {
            this.blockLevelLocationArray[i] = -1;
        }
        for (int i = 0; i < this.subBlockLevelLocationArray.length; i++) {
            this.subBlockLevelLocationArray[i] = -1;
        }

//        MIN
        for (int i = 0; i < this.siteLevelMinHostListArray.length; i++) {
            // Check if not null then clear
            if (this.siteLevelMinHostListArray[i] == null) {
                this.siteLevelMinHostListArray[i] = new Properties();
            } else {
                this.siteLevelMinHostListArray[i].clear();
            }
        }
        for (int i = 0; i < this.blockLevelMinHostListArray.length; i++) {
            // Check if not null then clear
            if (this.blockLevelMinHostListArray[i] == null) {
                this.blockLevelMinHostListArray[i] = new Properties();
            } else {
                this.blockLevelMinHostListArray[i].clear();
            }
        }
        for (int i = 0; i < this.subBlockLevelMinHostListArray.length; i++) {
            if (this.subBlockLevelMinHostListArray[i] == null) {
                this.subBlockLevelMinHostListArray[i] = new Properties();
            } else {
                this.subBlockLevelMinHostListArray[i].clear();
            }
        }
        for (int i = 0; i < this.siteLevelMinLocationArray.length; i++) {
            this.siteLevelMinLocationArray[i] = -1;
        }
        for (int i = 0; i < this.blockLevelMinLocationArray.length; i++) {
            this.blockLevelMinLocationArray[i] = -1;
        }
        for (int i = 0; i < this.subBlockLevelMinLocationArray.length; i++) {
            this.subBlockLevelMinLocationArray[i] = -1;
        }

//        MAX
        for (int i = 0; i < this.siteLevelMaxHostListArray.length; i++) {
            // Check if not null then clear
            if (this.siteLevelMaxHostListArray[i] == null) {
                this.siteLevelMaxHostListArray[i] = new Properties();
            } else {
                this.siteLevelMaxHostListArray[i].clear();
            }
        }
        for (int i = 0; i < this.blockLevelMaxHostListArray.length; i++) {
            // Check if not null then clear
            if (this.blockLevelMaxHostListArray[i] == null) {
                this.blockLevelMaxHostListArray[i] = new Properties();
            } else {
                this.blockLevelMaxHostListArray[i].clear();
            }
        }
        for (int i = 0; i < this.subBlockLevelMaxHostListArray.length; i++) {
            if (this.subBlockLevelMaxHostListArray[i] == null) {
                this.subBlockLevelMaxHostListArray[i] = new Properties();
            } else {
                this.subBlockLevelMaxHostListArray[i].clear();
            }
        }
        for (int i = 0; i < this.siteLevelMaxLocationArray.length; i++) {
            this.siteLevelMaxLocationArray[i] = -1;
        }
        for (int i = 0; i < this.blockLevelMaxLocationArray.length; i++) {
            this.blockLevelMaxLocationArray[i] = -1;
        }
        for (int i = 0; i < this.subBlockLevelMaxLocationArray.length; i++) {
            this.subBlockLevelMaxLocationArray[i] = -1;
        }
    }

    public Integer[] getSiteBlockSubBlockMappingFromRowNumber(int rowNumber) {
        //System.out.println("Row Number : "+rowNumber);
        return (Integer[]) this.reverseRowMapping.get(rowNumber);
    }

    public Integer[] getSiteBlockSubBlockMappingFromMinRowNumber(int rowNumber) {
        //System.out.println("Row Number : "+rowNumber);
        return (Integer[]) this.reverseMinRowMapping.get(rowNumber);
    }

    public Integer[] getSiteBlockSubBlockMappingFromMaxRowNumber(int rowNumber) {
        //System.out.println("Row Number : "+rowNumber);
        return (Integer[]) this.reverseMaxRowMapping.get(rowNumber);
    }

    public void putObject(int site, int block, int subBlock, int columnNumber, int operation, Object obj) {
        if (this.siteLevelLocationArray[site] == -1) {
            this.siteLevelLocationArray[site] = rowCounter;
            Integer[] indexArray = new Integer[]{new Integer(site), null, null};
            this.reverseRowMapping.put(rowCounter, indexArray);
//            logger.debug("***PUT SITE " + site + ", rowCounter : " + rowCounter);
            this.rowCounter++;
        }
        if (this.blockLevelLocationArray[block] == -1) {
            this.blockLevelLocationArray[block] = rowCounter;
            Integer[] indexArray = new Integer[]{null, new Integer(block), null};
            this.reverseRowMapping.put(rowCounter, indexArray);
//            logger.debug("***PUT Block "+block+", rowCounter : "+rowCounter);
            this.rowCounter++;
        }
        if (this.subBlockLevelLocationArray[subBlock] == -1) {
            this.subBlockLevelLocationArray[subBlock] = rowCounter;
            Integer[] indexArray = new Integer[]{null, null, new Integer(subBlock)};
            this.reverseRowMapping.put(rowCounter, indexArray);
//            logger.debug("***PUT SUBBLOCK "+subBlock+", rowCounter : "+rowCounter);
            this.rowCounter++;
        }

        // Check if there is sub block inside site
        if (siteLevelHostListArray[site].get(subBlock) == null) {
            siteLevelHostListArray[site].put(new Integer(subBlock), true);
        }
        if (blockLevelHostListArray[block].get(subBlock) == null) {
            blockLevelHostListArray[block].put(new Integer(subBlock), true);
        }
        if (subBlockLevelHostListArray[subBlock].get(subBlock) == null) {
            subBlockLevelHostListArray[subBlock].put(new Integer(subBlock), true);
        }

        // Put Object // Site Put
        operationPut(this.siteLevelLocationArray[site], columnNumber, operation, obj);
        // Put Object // Block Put
        operationPut(this.blockLevelLocationArray[block], columnNumber, operation, obj);
        // Put Object // SubBlock Put
        operationPut(this.subBlockLevelLocationArray[subBlock], columnNumber, operation, obj);

    }

    public void putObject(int site, int block, int subBlock, int columnNumber, int operation, Object obj, Objects min, Object max) {
        if (this.siteLevelLocationArray[site] == -1) {
            this.siteLevelLocationArray[site] = rowCounter;
            Integer[] indexArray = new Integer[]{new Integer(site), null, null};
            this.reverseRowMapping.put(rowCounter, indexArray);
//            logger.debug("***PUT SITE " + site + ", rowCounter : " + rowCounter);
            this.rowCounter++;
        }
        if (this.blockLevelLocationArray[block] == -1) {
            this.blockLevelLocationArray[block] = rowCounter;
            Integer[] indexArray = new Integer[]{null, new Integer(block), null};
            this.reverseRowMapping.put(rowCounter, indexArray);
//            logger.debug("***PUT Block "+block+", rowCounter : "+rowCounter);
            this.rowCounter++;
        }
        if (this.subBlockLevelLocationArray[subBlock] == -1) {
            this.subBlockLevelLocationArray[subBlock] = rowCounter;
            Integer[] indexArray = new Integer[]{null, null, new Integer(subBlock)};
            this.reverseRowMapping.put(rowCounter, indexArray);
//            logger.debug("***PUT SUBBLOCK "+subBlock+", rowCounter : "+rowCounter);
            this.rowCounter++;
        }

        // Check if there is sub block inside site
        if (siteLevelHostListArray[site].get(subBlock) == null) {
            siteLevelHostListArray[site].put(new Integer(subBlock), true);
        }
        if (blockLevelHostListArray[block].get(subBlock) == null) {
            blockLevelHostListArray[block].put(new Integer(subBlock), true);
        }
        if (subBlockLevelHostListArray[subBlock].get(subBlock) == null) {
            subBlockLevelHostListArray[subBlock].put(new Integer(subBlock), true);
        }

        // Put Object // Site Put
        operationPut(this.siteLevelLocationArray[site], columnNumber, operation, obj);
        // Put Object // Block Put
        operationPut(this.blockLevelLocationArray[block], columnNumber, operation, obj);
        // Put Object // SubBlock Put
        operationPut(this.subBlockLevelLocationArray[subBlock], columnNumber, operation, obj);

    }

    public void operationPut(int i, int j, int operation, Object obj) {
        if (operation == OPERATION_ADD) {
            if (metaData[j] == TYPE_INT) {

                Integer ans = (Integer) this.getObject(i, j);
                //System.out.println("getObj : "+ans);
                if (ans != null) {
                    //System.out.println("getObj is not null, add "+(Integer)obj);
                    ans = ans + (Integer) obj;
                } else {
                    ans = (Integer) obj;
                }
                //System.out.println("==== putObj : "+ans);
                this.putObject(i, j, ans);
            } else if (metaData[j] == TYPE_LONG) {

                Long ans = (Long) this.getObject(i, j);
                if (ans != null) {
                    ans = ans + (Long) obj;
                } else {
                    ans = (Long) obj;
                }
                //System.out.println("put : "+ans);
                this.putObject(i, j, ans);
            } else {
                logger.error("Unknown : variable Type");
            }
        } else if (operation == OPERATION_MAX) {
            if (metaData[j] == TYPE_INT) {

                Integer ans = (Integer) this.getObject(i, j);
                //System.out.println("getObj : "+ans);
                Integer input = null;
                try {
                    input = (Integer) obj;
                } catch (Exception e) {
                    logger.error(e);
                }
                if (ans != null && input > ans) {
                    //System.out.println("getObj is not null, add "+(Integer)obj);
                    ans = (Integer) obj;
                } else {
                    ans = (Integer) obj;
                }
                //System.out.println("==== putObj : "+ans);
                this.putObject(i, j, ans);
            } else if (metaData[j] == TYPE_LONG) {
                Long input = null;
                try {
                    input = (Long) obj;
                } catch (Exception e) {
                    logger.error(e);
                }
                Long ans = (Long) this.getObject(i, j);
                if (ans != null && input > ans) {
                    ans = (Long) obj;
                } else {
                    ans = (Long) obj;
                }
                //System.out.println("put : "+ans);
                this.putObject(i, j, ans);
            } else {
                logger.error("Unknown : variable Type");
            }
        } else if (operation == OPERATION_SUBTRACT) {
            if (metaData[j] == TYPE_INT) {

                Integer ans = (Integer) this.getObject(i, j);
                //System.out.println("getObj : "+ans);
                if (ans != null) {
                    //System.out.println("getObj is not null, add "+(Integer)obj);
                    ans = ans - (Integer) obj;
                } else {
                    ans = (Integer) obj;
                }
                //System.out.println("==== putObj : "+ans);
                this.putObject(i, j, ans);
            } else if (metaData[j] == TYPE_LONG) {

                Long ans = (Long) this.getObject(i, j);
                if (ans != null) {
                    ans = ans - (Long) obj;
                } else {
                    ans = (Long) obj;
                }
                //System.out.println("put : "+ans);
                this.putObject(i, j, ans);
            } else {
                logger.error("Unknown : variable Type");
            }
        } else if (operation == OPERATION_MAXTPS) {
            try {

                Long maxDefault = Long.MIN_VALUE;
                Long max = maxDefault;
                if (this.getMaxObject(i, j) != null) {
                    max = Long.parseLong(this.getMaxObject(i, j).toString());
                }
                Long value = Long.parseLong(obj.toString());

                if (max != null) {
                    if (value > max) {
                        max = value;
                    }
                } else {
                    if (value > maxDefault) {
                        max = value;
                    }
                }
                this.putMaxObject(i, j, max);
            } catch (Exception e) {
                logger.error(e);
//                this.putMaxObject(i, j, obj);
            }

        } else if (operation == OPERATION_MINTPS) {
            try {
                Long minDefault = Long.MAX_VALUE;
                Long min = minDefault;
                if (this.getMinObject(i, j) != null) {
                    min = Long.parseLong(this.getMinObject(i, j).toString());
                }
                Long value = Long.parseLong(obj.toString());
                if (min != null) {
                    if (value < min) {
                        min = value;
                    }
                } else {
                    if (value < minDefault) {
                        min = value;
                    }
                }
                this.putMinObject(i, j, min);

            } catch (Exception e) {
                logger.error(e);
//                this.putMinObject(i, j, obj);
            }

        } else {
            logger.error("Unknown : put Operation");
        }
    }

    public void putMinObject(int site, int block, int subBlock, int columnNumber, int operation, Object obj) {

        if (this.siteLevelMinLocationArray[site] == -1) {
            this.siteLevelMinLocationArray[site] = rowMinCounter;
            Integer[] indexArray = new Integer[]{new Integer(site), null, null};
            this.reverseMinRowMapping.put(rowMinCounter, indexArray);
            this.rowMinCounter++;
        }
        if (this.blockLevelMinLocationArray[block] == -1) {
            this.blockLevelMinLocationArray[block] = rowMinCounter;
            Integer[] indexArray = new Integer[]{null, new Integer(block), null};
            this.reverseMinRowMapping.put(rowMinCounter, indexArray);
            this.rowMinCounter++;
        }
        if (this.subBlockLevelMinLocationArray[subBlock] == -1) {
            this.subBlockLevelMinLocationArray[subBlock] = rowMinCounter;
            Integer[] indexArray = new Integer[]{null, null, new Integer(subBlock)};
            this.reverseMinRowMapping.put(rowMinCounter, indexArray);
            this.rowMinCounter++;
        }

        // Check if there is sub block inside site
        if (siteLevelMinHostListArray[site].get(subBlock) == null) {
            siteLevelMinHostListArray[site].put(new Integer(subBlock), true);
        }
        if (blockLevelMinHostListArray[block].get(subBlock) == null) {
            blockLevelMinHostListArray[block].put(new Integer(subBlock), true);
        }
        if (subBlockLevelMinHostListArray[subBlock].get(subBlock) == null) {
            subBlockLevelMinHostListArray[subBlock].put(new Integer(subBlock), true);
        }

        // Put Object // Site Put
        operationPut(this.siteLevelMinLocationArray[site], columnNumber, operation, obj);
        // Put Object // Block Put
        operationPut(this.blockLevelMinLocationArray[block], columnNumber, operation, obj);
        // Put Object // SubBlock Put
        operationPut(this.subBlockLevelMinLocationArray[subBlock], columnNumber, operation, obj);

    }

    public void putMaxObject(int site, int block, int subBlock, int columnNumber, int operation, Object obj) {

        if (this.siteLevelMaxLocationArray[site] == -1) {
            this.siteLevelMaxLocationArray[site] = rowMaxCounter;
            Integer[] indexArray = new Integer[]{new Integer(site), null, null};
            this.reverseMaxRowMapping.put(rowMaxCounter, indexArray);
            this.rowMaxCounter++;
        }
        if (this.blockLevelMaxLocationArray[block] == -1) {
            this.blockLevelMaxLocationArray[block] = rowMaxCounter;
            Integer[] indexArray = new Integer[]{null, new Integer(block), null};
            this.reverseMaxRowMapping.put(rowMaxCounter, indexArray);
            this.rowMaxCounter++;
        }
        if (this.subBlockLevelMaxLocationArray[subBlock] == -1) {
            this.subBlockLevelMaxLocationArray[subBlock] = rowMaxCounter;
            Integer[] indexArray = new Integer[]{null, null, new Integer(subBlock)};
            this.reverseMaxRowMapping.put(rowMaxCounter, indexArray);
            this.rowMaxCounter++;
        }

        // Check if there is sub block inside site
        if (siteLevelMaxHostListArray[site].get(subBlock) == null) {
            siteLevelMaxHostListArray[site].put(new Integer(subBlock), true);
        }
        if (blockLevelMaxHostListArray[block].get(subBlock) == null) {
            blockLevelMaxHostListArray[block].put(new Integer(subBlock), true);
        }
        if (subBlockLevelMaxHostListArray[subBlock].get(subBlock) == null) {
            subBlockLevelMaxHostListArray[subBlock].put(new Integer(subBlock), true);
        }

        // Put Object // Site Put
        operationPut(this.siteLevelMaxLocationArray[site], columnNumber, operation, obj);
        // Put Object // Block Put
        operationPut(this.blockLevelMaxLocationArray[block], columnNumber, operation, obj);
        // Put Object // SubBlock Put
        operationPut(this.subBlockLevelMaxLocationArray[subBlock], columnNumber, operation, obj);

    }

    @Override
    public String toString() {
        //statSummarizationSmartResultSet
        return "";
    }

    public String dumpToString() {
        StringBuffer sb = new StringBuffer();
        String separator = ",";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        sb.append("DATE,IDENTIFIER");
        for (int i = 0; i < columnName.length; i++) {
            sb.append(separator);
            String tmp = columnName[i] + "-" + unitType[i] + "[" + NAME_OF_VARTYPE[metaData[i]] + "]";
            sb.append(tmp);
        }
        sb.append("\n");
        for (int i = 0; i < lastestDataSet.length; i++) {
            // Date,Site,Block,SubBlock
            if (minDate != null) {
                sb.append(sdf.format(minDate));
            } else {
                sb.append("null");
            }
            sb.append(",");
            Integer[] siteBlockSubBlockArray = (Integer[]) this.reverseRowMapping.get(i);
            String identifier = this.getIdentifier(siteBlockSubBlockArray);
            sb.append(identifier);
            // ResultSet
            for (int j = 0; j < lastestDataSet[i].length; j++) {
                sb.append(separator);

                switch (this.metaData[j]) {
                    case StatSummarizationResultSet.TYPE_DATE:
                        sb.append(lastestDataSet[i][j]);
                        break;
                    case StatSummarizationResultSet.TYPE_DOUBLE:
                        sb.append(lastestDataSet[i][j]);
                        break;
                    case StatSummarizationResultSet.TYPE_FLOAT:
                        sb.append(String.format("%.2f", (Float) lastestDataSet[i][j]));
                        break;
                    case StatSummarizationResultSet.TYPE_INT:
                        sb.append(lastestDataSet[i][j]);
                        break;
                    case StatSummarizationResultSet.TYPE_LONG:
                        sb.append(lastestDataSet[i][j]);
                        break;
                    case StatSummarizationResultSet.TYPE_STRING:
                        sb.append((String) lastestDataSet[i][j]);
                        break;
                    default:
                        logger.error("Unknown Type");
                }

            }// End J
            sb.append("\n");
        }// End I

        // Dump Time Stamp
        if (maxDate != null && maxDate != null) {
            sb.append("From : ");
            sb.append(sdf.format(minDate));
            sb.append(", To : ");
            sb.append(sdf.format(maxDate));
            sb.append("\n");
        }
        // Dump Time Stamp
        return sb.toString();
    }

    public String dumpMinToString() {
        StringBuffer sb = new StringBuffer();
        String separator = ",";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        sb.append("DATE,IDENTIFIER");
        for (int i = 0; i < columnName.length; i++) {
            sb.append(separator);
            String tmp = columnName[i] + "-" + unitType[i] + "[" + NAME_OF_VARTYPE[metaData[i]] + "]";
            sb.append(tmp);
        }
        sb.append("\n");
        for (int i = 0; i < minDataSet.length; i++) {
            // Date,Site,Block,SubBlock
            if (minDate != null) {
                sb.append(sdf.format(minDate));
            } else {
                sb.append("null");
            }
            sb.append(",");
            Integer[] siteBlockSubBlockArray = (Integer[]) this.reverseMinRowMapping.get(i);
            String identifier = this.getIdentifier(siteBlockSubBlockArray);
            sb.append(identifier);
            // ResultSet
            for (int j = 0; j < minDataSet[i].length; j++) {
                sb.append(separator);

                switch (this.metaData[j]) {
                    case StatSummarizationResultSet.TYPE_DATE:
                        sb.append(minDataSet[i][j]);
                        break;
                    case StatSummarizationResultSet.TYPE_DOUBLE:
                        sb.append(minDataSet[i][j]);
                        break;
                    case StatSummarizationResultSet.TYPE_FLOAT:
                        sb.append(String.format("%.2f", (Float) minDataSet[i][j]));
                        break;
                    case StatSummarizationResultSet.TYPE_INT:
                        sb.append(minDataSet[i][j]);
                        break;
                    case StatSummarizationResultSet.TYPE_LONG:
                        sb.append(minDataSet[i][j]);
                        break;
                    case StatSummarizationResultSet.TYPE_STRING:
                        sb.append((String) minDataSet[i][j]);
                        break;
                    default:
                        logger.error("Unknown Type");
                }

            }// End J
            sb.append("\n");
        }// End I

        // Dump Time Stamp
        if (maxDate != null && maxDate != null) {
            sb.append("From : ");
            sb.append(sdf.format(minDate));
            sb.append(", To : ");
            sb.append(sdf.format(maxDate));
            sb.append("\n");
        }
        // Dump Time Stamp
        return sb.toString();
    }

    public String dumpMaxToString() {
        StringBuffer sb = new StringBuffer();
        String separator = ",";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        sb.append("DATE,IDENTIFIER");
        for (int i = 0; i < columnName.length; i++) {
            sb.append(separator);
            String tmp = columnName[i] + "-" + unitType[i] + "[" + NAME_OF_VARTYPE[metaData[i]] + "]";
            sb.append(tmp);
        }
        sb.append("\n");
        for (int i = 0; i < maxDataSet.length; i++) {
            // Date,Site,Block,SubBlock
            if (minDate != null) {
                sb.append(sdf.format(minDate));
            } else {
                sb.append("null");
            }
            sb.append(",");
            Integer[] siteBlockSubBlockArray = (Integer[]) this.reverseMaxRowMapping.get(i);
            String identifier = this.getIdentifier(siteBlockSubBlockArray);
            sb.append(identifier);
            // ResultSet
            for (int j = 0; j < maxDataSet[i].length; j++) {
                sb.append(separator);

                switch (this.metaData[j]) {
                    case StatSummarizationResultSet.TYPE_DATE:
                        sb.append(maxDataSet[i][j]);
                        break;
                    case StatSummarizationResultSet.TYPE_DOUBLE:
                        sb.append(maxDataSet[i][j]);
                        break;
                    case StatSummarizationResultSet.TYPE_FLOAT:
                        sb.append(String.format("%.2f", (Float) maxDataSet[i][j]));
                        break;
                    case StatSummarizationResultSet.TYPE_INT:
                        sb.append(maxDataSet[i][j]);
                        break;
                    case StatSummarizationResultSet.TYPE_LONG:
                        sb.append(maxDataSet[i][j]);
                        break;
                    case StatSummarizationResultSet.TYPE_STRING:
                        sb.append((String) maxDataSet[i][j]);
                        break;
                    default:
                        logger.error("Unknown Type");
                }

            }// End J
            sb.append("\n");
        }// End I

        // Dump Time Stamp
        if (maxDate != null && maxDate != null) {
            sb.append("From : ");
            sb.append(sdf.format(minDate));
            sb.append(", To : ");
            sb.append(sdf.format(maxDate));
            sb.append("\n");
        }
        // Dump Time Stamp
        return sb.toString();
    }

    @Override
    public void dumpDataSet() {

        logger.debug("Value " + this.dumpToString());
        logger.debug("Min " + this.dumpMinToString());
        logger.debug("MAX " + this.dumpMaxToString());
    }

    public String getStringPrefixIdentifier(Integer[] siteBlockSubBlockArray, int prefixIdentifier) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < siteBlockSubBlockArray.length; i++) {
            sb.append(",");
            sb.append(siteBlockSubBlockArray[i]);
        }
        logger.debug("SBS [ " + sb.toString() + " ]");
        switch (prefixIdentifier) {
            case StatSummarizationSmartResultSet.MAP_SITE:
                return (String) this.statSummarizationCore.getSuperInnovaStatProcessor().getSuperInnovaStatEnginePropertiesLookup().get(GeneralSuperInnovaStatEngine.SITE_REVERSE_KEYWORD, siteBlockSubBlockArray[StatSummarizationSmartResultSet.MAP_SITE]);
            case StatSummarizationSmartResultSet.MAP_BLOCK:
                return (String) this.statSummarizationCore.getSuperInnovaStatProcessor().getSuperInnovaStatEnginePropertiesLookup().get(GeneralSuperInnovaStatEngine.BLOCK_REVERSE_KEYWORD, siteBlockSubBlockArray[StatSummarizationSmartResultSet.MAP_BLOCK]);
            case StatSummarizationSmartResultSet.MAP_SUBBLOCK:
                return (String) this.statSummarizationCore.getSuperInnovaStatProcessor().getSuperInnovaStatEnginePropertiesLookup().get(GeneralSuperInnovaStatEngine.SUBBLOCK_REVERSE_KEYWORD, siteBlockSubBlockArray[StatSummarizationSmartResultSet.MAP_SUBBLOCK]);

            default:
                return null;
        }

    }

    public String getIdentifier(Integer[] siteBlockSubBlockArray) {

        if (siteBlockSubBlockArray != null) {
            for (int j = 0; j < siteBlockSubBlockArray.length; j++) {
                if (siteBlockSubBlockArray[j] != null) {
                    if (j == StatSummarizationSmartResultSet.MAP_SITE) {
                        return (String) this.statSummarizationCore.getSuperInnovaStatProcessor().getSuperInnovaStatEnginePropertiesLookup().get(GeneralSuperInnovaStatEngine.SITE_REVERSE_KEYWORD, siteBlockSubBlockArray[j]);
                    } else if (j == StatSummarizationSmartResultSet.MAP_BLOCK) {
                        return (String) this.statSummarizationCore.getSuperInnovaStatProcessor().getSuperInnovaStatEnginePropertiesLookup().get(GeneralSuperInnovaStatEngine.BLOCK_REVERSE_KEYWORD, siteBlockSubBlockArray[j]);
                    } else if (j == StatSummarizationSmartResultSet.MAP_SUBBLOCK) {
                        return (String) this.statSummarizationCore.getSuperInnovaStatProcessor().getSuperInnovaStatEnginePropertiesLookup().get(GeneralSuperInnovaStatEngine.SUBBLOCK_REVERSE_KEYWORD, siteBlockSubBlockArray[j]);
                    }
                }
            }
        }// if !=null
        return null;
    }

    public String getPRTGOutput(Properties prop) {

        this.dumpDataSet();

        StringBuilder prtgOutput = new StringBuilder();
        boolean overidePRTGOutput = false;
        StringBuilder overridePRTGOutput = new StringBuilder();
        prtgOutput.append("<PRTG>");
        int mapLevel = -1;
        String[] identifierArray = null;
        if (prop != null) {
            String level = prop.getProperty("level");
            String filterRegex = prop.getProperty("filterRegex");
            String hideUnitString = prop.getProperty("hideUnit");
            String showSitePrefixString = prop.getProperty("showSitePrefix");
            String showBlockPrefixString = prop.getProperty("showBlockPrefix");
            String hideFloatTagString = prop.getProperty("hideFloatTag");

            String showEvaluateOnlyString = prop.getProperty("showEvaluateOnly");
            String showEvaluateMinString = prop.getProperty("showEvaluateMin");
            String showEvaluateMaxString = prop.getProperty("showEvaluateMax");
            String showEvaluateSumString = prop.getProperty("showEvaluateSum");
            String showEvaluateAverageString = prop.getProperty("showEvaluateAverage");
            String evaluatePrefix = prop.getProperty("evaluatePrefix");
            String evaluateAsIntegerText = prop.getProperty("evaluateAsInteger");

            String showMaxMinTPSString = prop.getProperty("showMaxMinTPS");
            String limitChannelString = prop.getProperty("limitChannel");
            MinMaxAverageSumFinder minMaxAverageSumFinder = new MinMaxAverageSumFinder();

            boolean showEvaluateOnly = false;
            boolean showEvaluateMin = false;
            boolean showEvaluateMax = false;
            boolean showEvaluateSum = false;
            boolean showEvaluateAverage = false;
            boolean evaluateAsInteger = false;

            boolean hideUnit = false;
            boolean hideFloatTag = false;
            boolean showSitePrefix = false;
            boolean showBlockPrefix = false;

            boolean showMaxMinTPS = false;
            int limitChannel = 0;

            if (limitChannelString != null && !limitChannelString.isEmpty()) {
                if (limitChannelString.matches("(\\d+|\\D+)")) limitChannel = Integer.parseInt(limitChannelString);
            }

            if (hideUnitString != null && hideUnitString.compareToIgnoreCase("true") == 0) {
                hideUnit = true;
            }

            if (hideFloatTagString != null && hideFloatTagString.compareToIgnoreCase("true") == 0) {
                hideFloatTag = true;
            }

            if (showSitePrefixString != null && showSitePrefixString.compareToIgnoreCase("true") == 0) {
                showSitePrefix = true;
            }

            if (showBlockPrefixString != null && showBlockPrefixString.compareToIgnoreCase("true") == 0) {
                showBlockPrefix = true;
            }

            if (showEvaluateOnlyString != null && showEvaluateOnlyString.compareToIgnoreCase("true") == 0) {
                showEvaluateOnly = true;
            }

            if (showEvaluateMinString != null && showEvaluateMinString.compareToIgnoreCase("true") == 0) {
                showEvaluateMin = true;
            }

            if (showEvaluateSumString != null && showEvaluateSumString.compareToIgnoreCase("true") == 0) {
                showEvaluateSum = true;
            }

            if (showEvaluateMaxString != null && showEvaluateMaxString.compareToIgnoreCase("true") == 0) {
                showEvaluateMax = true;
            }

            if (showEvaluateAverageString != null && showEvaluateAverageString.compareToIgnoreCase("true") == 0) {
                showEvaluateAverage = true;
            }
            if (evaluateAsIntegerText != null && evaluateAsIntegerText.compareToIgnoreCase("true") == 0) {
                evaluateAsInteger = true;
            }

            if (showMaxMinTPSString != null && showMaxMinTPSString.compareToIgnoreCase("true") == 0) {
                showMaxMinTPS = true;
            }
            // Init Select Column Process
            String selectColumnString = prop.getProperty("selectColumn");
            String[] selectColumnStringArray = null;
            if (selectColumnString != null) {
                selectColumnStringArray = selectColumnString.split("%7C");
                /*
                 for(int z=0;z<selectColumnStringArray.length;z++){
                 System.out.println("z["+z+"] : "+selectColumnStringArray[z]);
                 }
                 */
                if (selectColumnStringArray == null || selectColumnStringArray.length <= 1) {
                    selectColumnStringArray = selectColumnString.split("\\|");
                }
            }
            Properties selectColumnList = new Properties();
            if (selectColumnStringArray != null) {
                for (int i = 0; i < selectColumnStringArray.length; i++) {
                    //System.out.println("xPut : "+selectColumnStringArray[i]);
                    selectColumnList.put(selectColumnStringArray[i].replaceAll("%20", " "), true);

                }
            }

            Boolean getHostCounter = Boolean.parseBoolean(prop.getProperty("getNodeCounter"));
            if (getHostCounter == null) {
                getHostCounter = true;
            }

            int[] resultSet = null;
            int[] resultSetMin = null;
            int[] resultSetMax = null;

            int resultSetCounter = 0;
            int resultSetCounterMin = 0;
            int resultSetCounterMax = 0;

            int[] locationMapper = null;
            int[] locationMinMapper = null;
            int[] locationMaxMapper = null;

            if (level.compareToIgnoreCase("site") == 0) {
                resultSet = this.siteLevelLocationArray;
                resultSetMin = this.siteLevelMinLocationArray;
                resultSetMax = this.siteLevelMaxLocationArray;

                mapLevel = StatSummarizationSmartResultSet.MAP_SITE;

                locationMapper = this.siteLevelLocationArray;
                locationMinMapper = this.siteLevelMinLocationArray;
                locationMaxMapper = this.siteLevelMaxLocationArray;

            } else if (level.compareToIgnoreCase("block") == 0) {
                resultSet = this.blockLevelLocationArray;
                resultSetMin = this.blockLevelMinLocationArray;
                resultSetMax = this.blockLevelMaxLocationArray;

                mapLevel = StatSummarizationSmartResultSet.MAP_BLOCK;
                locationMapper = this.blockLevelLocationArray;
                locationMinMapper = this.blockLevelMinLocationArray;
                locationMaxMapper = this.blockLevelMaxLocationArray;

            } else if (level.compareToIgnoreCase("subblock") == 0) {
                resultSet = this.subBlockLevelLocationArray;
                resultSetMin = this.subBlockLevelMinLocationArray;
                resultSetMax = this.subBlockLevelMaxLocationArray;

                mapLevel = StatSummarizationSmartResultSet.MAP_SUBBLOCK;
                locationMapper = this.subBlockLevelLocationArray;
                locationMinMapper = this.subBlockLevelMinLocationArray;
                locationMaxMapper = this.subBlockLevelMaxLocationArray;

            }

            if (resultSet != null) {

                for (int i = 0; i < resultSet.length; i++) {

                    logger.debug("resultSet[" + i + "]" + resultSet[i]);
                    if (identifierArray != null && i > resultSetCounter) {
                        break;
                    }

                    if (resultSet[i] < 0) {
                        logger.error("resultSet[" + i + "] : " + resultSet[i] + ", Less Than Zero");
                        continue;
                    }

                    // Identify SiteBlockSubblock & Channel Name
                    Integer[] siteBlockSubBlockArray = null;
                    String channelName = null;
                    //System.out.println("resultSet[i] : "+resultSet[i]);
                    siteBlockSubBlockArray = getSiteBlockSubBlockMappingFromRowNumber(resultSet[i]);
                    /*
                     for(int xz=0;xz<siteBlockSubBlockArray.length;xz++){
                     if(siteBlockSubBlockArray[xz]!=null){
                     System.out.println("xz : "+xz+", data : "+siteBlockSubBlockArray[xz]);
                     }
                     }
                     */

                    /*WRONG
                     String siteName=this.getStringPrefixIdentifier(siteBlockSubBlockArray, StatSummarizationSmartResultSet.MAP_SITE);
                     String blockName=this.getStringPrefixIdentifier(siteBlockSubBlockArray, StatSummarizationSmartResultSet.MAP_BLOCK);
                     String subBlockName=this.getStringPrefixIdentifier(siteBlockSubBlockArray, StatSummarizationSmartResultSet.MAP_SUBBLOCK);
                     System.out.println("SBS , "+siteName+", "+blockName+", "+subBlockName);
                     */
                    boolean printSitePrefix = false;
                    boolean printBlockPrefix = false;
                    boolean printSubBlockPrefix = false;

                    if (mapLevel == StatSummarizationSmartResultSet.MAP_SITE || showSitePrefix == true) {
                        printSitePrefix = true;
                    }
                    if (mapLevel == StatSummarizationSmartResultSet.MAP_BLOCK || showBlockPrefix == true) {
                        printBlockPrefix = true;
                    }
                    if (mapLevel == StatSummarizationSmartResultSet.MAP_SUBBLOCK) {
                        printSubBlockPrefix = true;
                    }

                    channelName = this.getIdentifier(siteBlockSubBlockArray);
                    logger.debug("Channel" + channelName);
                    try {
                        int nameCounter = 0;
                        StringBuilder channelNameBuilder = new StringBuilder();
                        String[] siteBlockSubBlockName = channelName.split(StatGathererParser.FIELD_SEPARATOR_REPLACE_REGEX);

                        if (printSitePrefix == true) {
                            channelNameBuilder.append(siteBlockSubBlockName[0]);
                            nameCounter++;
                        }
                        if (printBlockPrefix == true) {
                            if (nameCounter > 0) {
                                channelNameBuilder.append("-");
                            }
                            channelNameBuilder.append(siteBlockSubBlockName[1]);
                            nameCounter++;
                        }
                        if (printSubBlockPrefix == true) {
                            if (nameCounter > 0) {
                                channelNameBuilder.append("-");
                            }
                            channelNameBuilder.append(siteBlockSubBlockName[2]);
                            nameCounter++;
                        }
                        channelName = channelNameBuilder.toString();
                        logger.debug("Channel" + channelName);
                    } catch (Exception e) {
                        logger.error(e);
                    }

                    if (filterRegex != null && filterRegex.length() > 0 && channelName != null && channelName.length() > 0) {
                        String[] filterRegexArray = filterRegex.split("%7C");
                        if (filterRegexArray == null || filterRegexArray.length <= 1) {
                            filterRegexArray = filterRegex.split("\\|");
                        }
                        /*
                         for(int z=0;z<filterRegexArray.length;z++){
                         System.out.println("z1["+z+"] : "+filterRegexArray[z]);
                         }
                         */
                        boolean isChannelNameMatched = false;
                        for (int k = 0; k < filterRegexArray.length; k++) {
                            //System.out.println("checkMatched-channelName : "+channelName+", regex : "+filterRegexArray[k]+", status : "+channelName.matches(filterRegexArray[k]));
                            isChannelNameMatched = channelName.matches(filterRegexArray[k]);
                            if (isChannelNameMatched == true) {
                                //System.out.println("channelName : "+channelName+", regex : "+filterRegexArray[k]);
                                break;
                            }
                        }

                        if (isChannelNameMatched == false) {
                            continue;
                        }
                    }

                    // Channel
                    for (int j = 0; j < this.columnName.length; j++) {

                        if (selectColumnStringArray != null) {

                            String delimeter = "-";
                            String[] extractColumnNameArray = this.columnName[j].split(delimeter);
                            String extractColumnName = extractColumnNameArray[extractColumnNameArray.length - 1];

                            //System.out.println("check "+this.columnName[j]+" matched : "+extractColumnName+", isNull : "+selectColumnList.get(extractColumnName));
                            boolean foundSelectColumn = (selectColumnList.get(extractColumnName) == null);
                            if (foundSelectColumn != false) {
                                continue;
                            }
                        }

                        //System.out.println("GetHostCounter : "+getHostCounter+", j : "+j+", max : "+(this.columnName.length-1));
                        if (getHostCounter == false && j == (this.columnName.length - 1)) {
                            //System.out.println("No Node Counter Flages");
                            continue;
                        }

                        prtgOutput.append("<result>");
                        prtgOutput.append("<channel>");

                        // System.out.println("ChannelName : "+channelName);
                        // if ChannelName is null
                        if (channelName == null || channelName.length() <= 0) {
                            prtgOutput = new StringBuilder();
                            prtgOutput.append("<prtg>");
                            prtgOutput.append("<error>1</error>");
                            prtgOutput.append("<text>No Matched Row Found</text>");
                            prtgOutput.append("</prtg>");
                            return prtgOutput.toString();
                        }

                        prtgOutput.append(channelName);
                        prtgOutput.append("-");
                        if (limitChannel > 0 ){
                            if (this.columnName[j].length() >= limitChannel) {
                                prtgOutput.append(this.columnName[j].substring(0, limitChannel));
                            } else {
                                prtgOutput.append(this.columnName[j]);
                            }
                        } else {
                            prtgOutput.append(this.columnName[j]);
                        }
                        prtgOutput.append("</channel>");
                        // Value
                        if (j == this.columnName.length - 1) {
                            logger.info("hCounter: " + lastestDataSet[i][j]);
                        }

                        prtgOutput.append("<value>");
                        int rowMapValue = locationMapper[i];
                        //System.out.println("metaData : "+this.metaData[j]+", data : "+lastestDataSet[rowMapValue][j]);
                        //System.out.println(channelName+"-"+this.columnName[j]+" ,metaData : "+this.metaData[j]+", data : "+lastestDataSet[rowMapValue][j]);
                        switch (this.metaData[j]) {
                            case StatSummarizationResultSet.TYPE_DATE:
                                if (lastestDataSet[rowMapValue][j] != null) {
                                    prtgOutput.append(lastestDataSet[rowMapValue][j]);
                                } else {
                                    prtgOutput.append("NoDateStamp");
                                }
                                break;
                            case StatSummarizationResultSet.TYPE_DOUBLE:
                                if (lastestDataSet[rowMapValue][j] != null && !Double.isNaN((Double) lastestDataSet[rowMapValue][j])) {
                                    if (hideFloatTag != true) {
                                        prtgOutput.append(String.format("%.2f", (Double) lastestDataSet[rowMapValue][j]));
                                    } else {
                                        prtgOutput.append(String.format("%d", ((Double) lastestDataSet[rowMapValue][j]).intValue()));
                                    }
                                    minMaxAverageSumFinder.addMember((Double) lastestDataSet[rowMapValue][j]);
                                } else {
                                    if (hideFloatTag != true) {
                                        prtgOutput.append("0.0");
                                    } else {
                                        prtgOutput.append("0");
                                    }

                                }
                                break;
                            case StatSummarizationResultSet.TYPE_FLOAT:
                                if (lastestDataSet[rowMapValue][j] != null && !Float.isNaN((Float) lastestDataSet[rowMapValue][j])) {
                                    if (hideFloatTag != true) {
                                        prtgOutput.append(String.format("%.2f", (Float) lastestDataSet[rowMapValue][j]));
                                    } else {
                                        prtgOutput.append(String.format("%d", ((Float) lastestDataSet[rowMapValue][j]).intValue()));
                                    }
                                    minMaxAverageSumFinder.addMember((Float) lastestDataSet[rowMapValue][j]);
                                } else {
                                    if (hideFloatTag != true) {
                                        prtgOutput.append("0.0");
                                    } else {
                                        prtgOutput.append("0");
                                    }
                                }
                                break;
                            case StatSummarizationResultSet.TYPE_INT:
                                if (lastestDataSet[rowMapValue][j] != null) {
                                    prtgOutput.append(lastestDataSet[rowMapValue][j]);
                                    minMaxAverageSumFinder.addMember((Integer) lastestDataSet[rowMapValue][j]);
                                } else {
                                    prtgOutput.append("0");
                                }
                                break;
                            case StatSummarizationResultSet.TYPE_LONG:
                                if (lastestDataSet[rowMapValue][j] != null) {
                                    prtgOutput.append(lastestDataSet[rowMapValue][j]);
                                    minMaxAverageSumFinder.addMember((Long) lastestDataSet[rowMapValue][j]);
                                } else {
                                    prtgOutput.append("0");
                                }
                                break;
                            case StatSummarizationResultSet.TYPE_STRING:
                                prtgOutput.append((String) lastestDataSet[rowMapValue][j]);
                                break;
                            default:
                                logger.error("Unknown Type");
                        }
                        prtgOutput.append("</value>");
                        if (!hideFloatTag) {
                            if (this.metaData[j] == StatSummarizationResultSet.TYPE_FLOAT || this.metaData[j] == StatSummarizationResultSet.TYPE_DOUBLE) {
                                prtgOutput.append("<float>1</float>");
                            }
                        }

                        if (!hideUnit) {
                            prtgOutput.append("<CustomUnit>");
                            prtgOutput.append(this.unitType[j]);
                            prtgOutput.append("</CustomUnit>");
                        }
                        prtgOutput.append("</result>");
                    }// End For J
                }
                if (showMaxMinTPS) {
                    if (this.getSiteBlockSubBlockMappingFromMaxRowNumber(0) != null
                            && this.getSiteBlockSubBlockMappingFromMinRowNumber(0) != null) {
//                    Max
                        logger.debug("Set max and min");
                        if (this.getSiteBlockSubBlockMappingFromMaxRowNumber(0) != null) {
                            for (int i = 0; i < resultSetMax.length; i++) {

                                if (identifierArray != null && i > resultSetCounterMax) {
                                    break;
                                }

                                if (resultSetMax[i] < 0) {
                                    logger.error("resultSetMax[" + i + "] : " + resultSetMax[i] + ", Less Than Zero");
                                    continue;
                                }

                                Integer[] siteBlockSubBlockArray = null;
                                String channelName = null;
                                siteBlockSubBlockArray = getSiteBlockSubBlockMappingFromMaxRowNumber(resultSetMax[i]);

                                boolean printSitePrefix = false;
                                boolean printBlockPrefix = false;
                                boolean printSubBlockPrefix = false;

                                if (mapLevel == StatSummarizationSmartResultSet.MAP_SITE || showSitePrefix) {
                                    printSitePrefix = true;
                                }
                                if (mapLevel == StatSummarizationSmartResultSet.MAP_BLOCK || showBlockPrefix) {
                                    printBlockPrefix = true;
                                }
                                if (mapLevel == StatSummarizationSmartResultSet.MAP_SUBBLOCK) {
                                    printSubBlockPrefix = true;
                                }
                                channelName = this.getIdentifier(siteBlockSubBlockArray);
                                try {

                                    int nameCounter = 0;
                                    StringBuilder channelNameBuilder = new StringBuilder();
                                    String[] siteBlockSubBlockName = channelName.split(StatGathererParser.FIELD_SEPARATOR_REPLACE_REGEX);
                                    if (printSitePrefix) {
                                        channelNameBuilder.append(siteBlockSubBlockName[0]);
                                        nameCounter++;
                                    }
                                    if (printBlockPrefix) {
                                        if (nameCounter > 0) {
                                            channelNameBuilder.append("-");
                                        }
                                        channelNameBuilder.append(siteBlockSubBlockName[1]);
                                        nameCounter++;
                                    }
                                    if (printSubBlockPrefix) {
                                        if (nameCounter > 0) {
                                            channelNameBuilder.append("-");
                                        }
                                        channelNameBuilder.append(siteBlockSubBlockName[2]);
                                        nameCounter++;
                                    }
                                    channelName = channelNameBuilder.toString();
                                } catch (Exception e) {
                                    channelName = "";
                                    logger.error(e);
                                }
                                logger.debug(channelName);

                                if (filterRegex != null && filterRegex.length() > 0 && channelName != null && channelName.length() > 0) {
                                    String[] filterRegexArray = filterRegex.split("%7C");
                                    if (filterRegexArray == null || filterRegexArray.length <= 1) {
                                        filterRegexArray = filterRegex.split("\\|");
                                    }
                                    boolean isChannelNameMatched = false;
                                    for (int k = 0; k < filterRegexArray.length; k++) {
                                        isChannelNameMatched = channelName.matches(filterRegexArray[k]);
                                        if (isChannelNameMatched) {
                                            break;
                                        }
                                    }
                                    if (!isChannelNameMatched) {
                                        continue;
                                    }
                                }
                                // Channel
                                for (int j = 0; j < this.columnName.length; j++) {

                                    if (selectColumnStringArray != null) {

                                        String decimeter = "-";
                                        String[] extractColumnNameArray = this.columnName[j].split(decimeter);
                                        String extractColumnName = extractColumnNameArray[extractColumnNameArray.length - 1];

                                        boolean foundSelectColumn = (selectColumnList.get(extractColumnName) == null);
                                        if (foundSelectColumn) {
                                            continue;
                                        }
                                    }

                                    if (!getHostCounter && (j == (this.columnName.length - 1))) {
                                        continue;
                                    }

                                    prtgOutput.append("<result>");
                                    prtgOutput.append("<channel>");

                                    // System.out.println("ChannelName : "+channelName);
                                    // if ChannelName is null
                                    if (channelName == null || channelName.length() <= 0) {
                                        prtgOutput = new StringBuilder();
                                        prtgOutput.append("<prtg>");
                                        prtgOutput.append("<error>1</error>");
                                        prtgOutput.append("<text>No Matched Row Found</text>");
                                        prtgOutput.append("</prtg>");
                                        return prtgOutput.toString();
                                    }
                                    prtgOutput.append(channelName);
                                    prtgOutput.append("-");
                                    if (limitChannel > 0 ){
                                        if (this.columnName[j].length() >= limitChannel) {
                                            prtgOutput.append(this.columnName[j].substring(0, limitChannel));
                                        } else {
                                            prtgOutput.append(this.columnName[j]);
                                        }
                                    } else {
                                        prtgOutput.append(this.columnName[j]);
                                    }
                                    prtgOutput.append("-MAX");
                                    prtgOutput.append("</channel>");
                                    // Value
                                    if (j == this.columnName.length - 1) {
                                        logger.info("MaxCounter: " + maxDataSet[i][j]);
                                    }

                                    prtgOutput.append("<value>");
                                    int rowMapValue = locationMaxMapper[i];
                                    switch (this.metaData[j]) {
                                        case StatSummarizationResultSet.TYPE_DATE:
                                            if (maxDataSet[rowMapValue][j] != null) {
                                                prtgOutput.append(maxDataSet[rowMapValue][j]);
                                            } else {
                                                prtgOutput.append("NoDateStamp");
                                            }
                                            break;
                                        case StatSummarizationResultSet.TYPE_DOUBLE:
                                            if (maxDataSet[rowMapValue][j] != null && !Double.isNaN((Double) maxDataSet[rowMapValue][j])) {
                                                if (!hideFloatTag) {
                                                    prtgOutput.append(String.format("%.2f", (Double) maxDataSet[rowMapValue][j]));
                                                } else {
                                                    prtgOutput.append(String.format("%d", ((Double) maxDataSet[rowMapValue][j]).intValue()));
                                                }
//                                            minMaxAverageSumFinder.addMember((Double) maxDataSet[rowMapValue][j]);
                                            } else {
                                                if (!hideFloatTag) {
                                                    prtgOutput.append("0.0");
                                                } else {
                                                    prtgOutput.append("0");
                                                }

                                            }
                                            break;
                                        case StatSummarizationResultSet.TYPE_FLOAT:
                                            if (maxDataSet[rowMapValue][j] != null && !Float.isNaN((Float) maxDataSet[rowMapValue][j])) {
                                                if (!hideFloatTag) {
                                                    prtgOutput.append(String.format("%.2f", (Float) maxDataSet[rowMapValue][j]));
                                                } else {
                                                    prtgOutput.append(String.format("%d", ((Float) maxDataSet[rowMapValue][j]).intValue()));
                                                }
//                                            minMaxAverageSumFinder.addMember((Float) maxDataSet[rowMapValue][j]);
                                            } else {
                                                if (!hideFloatTag) {
                                                    prtgOutput.append("0.0");
                                                } else {
                                                    prtgOutput.append("0");
                                                }
                                            }
                                            break;
                                        case StatSummarizationResultSet.TYPE_INT:
                                            if (maxDataSet[rowMapValue][j] != null) {
                                                prtgOutput.append(maxDataSet[rowMapValue][j]);
//                                            minMaxAverageSumFinder.addMember((Integer.parseInt(maxDataSet[rowMapValue][j].toString())));
                                            } else {
                                                prtgOutput.append("0");
                                            }
                                            break;
                                        case StatSummarizationResultSet.TYPE_LONG:
                                            if (maxDataSet[rowMapValue][j] != null) {
                                                prtgOutput.append(maxDataSet[rowMapValue][j]);
//                                            minMaxAverageSumFinder.addMember((Long.parseLong(maxDataSet[rowMapValue][j].toString())));
                                            } else {
                                                prtgOutput.append("0");
                                            }
                                            break;
                                        case StatSummarizationResultSet.TYPE_STRING:
                                            prtgOutput.append((String) maxDataSet[rowMapValue][j]);
                                            break;
                                        default:
                                            logger.error("Unknown Type");
                                    }
                                    prtgOutput.append("</value>");
                                    if (!hideFloatTag != true) {
                                        if (this.metaData[j] == StatSummarizationResultSet.TYPE_FLOAT || this.metaData[j] == StatSummarizationResultSet.TYPE_DOUBLE) {
                                            prtgOutput.append("<float>1</float>");
                                        }
                                    }

                                    if (hideUnit == false) {
                                        prtgOutput.append("<CustomUnit>");
                                        prtgOutput.append(this.unitType[j]);
                                        prtgOutput.append("</CustomUnit>");
                                    }
                                    prtgOutput.append("</result>");
                                } // eng for i
                            }
                        }
                        if (this.getSiteBlockSubBlockMappingFromMinRowNumber(0) != null) {
                            for (int i = 0; i < resultSetMin.length; i++) {

                                if (identifierArray != null && i > resultSetCounterMin) {
                                    break;
                                }

                                if (resultSetMin[i] < 0) {
                                    logger.error("resultSetMin[" + i + "] : " + resultSetMin[i] + ", Less Than Zero");
                                    continue;
                                }

                                Integer[] siteBlockSubBlockArray = null;
                                String channelName = null;
                                siteBlockSubBlockArray = getSiteBlockSubBlockMappingFromMinRowNumber(resultSetMin[i]);

                                boolean printSitePrefix = false;
                                boolean printBlockPrefix = false;
                                boolean printSubBlockPrefix = false;

                                if (mapLevel == StatSummarizationSmartResultSet.MAP_SITE || showSitePrefix) {
                                    printSitePrefix = true;
                                }
                                if (mapLevel == StatSummarizationSmartResultSet.MAP_BLOCK || showBlockPrefix) {
                                    printBlockPrefix = true;
                                }
                                if (mapLevel == StatSummarizationSmartResultSet.MAP_SUBBLOCK) {
                                    printSubBlockPrefix = true;
                                }
                                channelName = this.getIdentifier(siteBlockSubBlockArray);
                                try {

                                    int nameCounter = 0;
                                    StringBuilder channelNameBuilder = new StringBuilder();
                                    String[] siteBlockSubBlockName = channelName.split(StatGathererParser.FIELD_SEPARATOR_REPLACE_REGEX);
                                    if (printSitePrefix) {
                                        channelNameBuilder.append(siteBlockSubBlockName[0]);
                                        nameCounter++;
                                    }
                                    if (printBlockPrefix) {
                                        if (nameCounter > 0) {
                                            channelNameBuilder.append("-");
                                        }
                                        channelNameBuilder.append(siteBlockSubBlockName[1]);
                                        nameCounter++;
                                    }
                                    if (printSubBlockPrefix) {
                                        if (nameCounter > 0) {
                                            channelNameBuilder.append("-");
                                        }
                                        channelNameBuilder.append(siteBlockSubBlockName[2]);
                                        nameCounter++;
                                    }
                                    channelName = channelNameBuilder.toString();
                                } catch (Exception e) {
                                    channelName = "";
                                    logger.error(e);
                                }
                                logger.debug(channelName);

                                if (filterRegex != null && filterRegex.length() > 0 && channelName != null && channelName.length() > 0) {
                                    String[] filterRegexArray = filterRegex.split("%7C");
                                    if (filterRegexArray == null || filterRegexArray.length <= 1) {
                                        filterRegexArray = filterRegex.split("\\|");
                                    }
                                    boolean isChannelNameMatched = false;
                                    for (int k = 0; k < filterRegexArray.length; k++) {
                                        isChannelNameMatched = channelName.matches(filterRegexArray[k]);
                                        if (isChannelNameMatched) {
                                            break;
                                        }
                                    }
                                    if (!isChannelNameMatched) {
                                        continue;
                                    }
                                }
                                // Channel
                                for (int j = 0; j < this.columnName.length; j++) {

                                    if (selectColumnStringArray != null) {

                                        String decimeter = "-";
                                        String[] extractColumnNameArray = this.columnName[j].split(decimeter);
                                        String extractColumnName = extractColumnNameArray[extractColumnNameArray.length - 1];

                                        boolean foundSelectColumn = (selectColumnList.get(extractColumnName) == null);
                                        if (foundSelectColumn) {
                                            continue;
                                        }
                                    }

                                    if (!getHostCounter && (j == (this.columnName.length - 1))) {
                                        continue;
                                    }

                                    prtgOutput.append("<result>");
                                    prtgOutput.append("<channel>");

                                    // System.out.println("ChannelName : "+channelName);
                                    // if ChannelName is null
                                    if (channelName == null || channelName.length() <= 0) {
                                        prtgOutput = new StringBuilder();
                                        prtgOutput.append("<prtg>");
                                        prtgOutput.append("<error>1</error>");
                                        prtgOutput.append("<text>No Matched Row Found</text>");
                                        prtgOutput.append("</prtg>");
                                        return prtgOutput.toString();
                                    }
                                    prtgOutput.append(channelName);
                                    prtgOutput.append("-");
                                    if (limitChannel > 0 ){
                                        if (this.columnName[j].length() >= limitChannel) {
                                            prtgOutput.append(this.columnName[j].substring(0, limitChannel));
                                        } else {
                                            prtgOutput.append(this.columnName[j]);
                                        }
                                    } else {
                                        prtgOutput.append(this.columnName[j]);
                                    }
                                    prtgOutput.append("-MIN");
                                    prtgOutput.append("</channel>");
                                    // Value
                                    if (j == this.columnName.length - 1) {
                                        logger.info("MinCounter: " + minDataSet[i][j]);
                                    }

                                    prtgOutput.append("<value>");
                                    int rowMapValue = locationMinMapper[i];
                                    switch (this.metaData[j]) {
                                        case StatSummarizationResultSet.TYPE_DATE:
                                            if (minDataSet[rowMapValue][j] != null) {
                                                prtgOutput.append(minDataSet[rowMapValue][j]);
                                            } else {
                                                prtgOutput.append("NoDateStamp");
                                            }
                                            break;
                                        case StatSummarizationResultSet.TYPE_DOUBLE:
                                            if (minDataSet[rowMapValue][j] != null && !Double.isNaN((Double) minDataSet[rowMapValue][j])) {
                                                if (!hideFloatTag) {
                                                    prtgOutput.append(String.format("%.2f", (Double) minDataSet[rowMapValue][j]));
                                                } else {
                                                    prtgOutput.append(String.format("%d", ((Double) minDataSet[rowMapValue][j]).intValue()));
                                                }
//                                            minMaxAverageSumFinder.addMember((Double) minDataSet[rowMapValue][j]);
                                            } else {
                                                if (!hideFloatTag) {
                                                    prtgOutput.append("0.0");
                                                } else {
                                                    prtgOutput.append("0");
                                                }

                                            }
                                            break;
                                        case StatSummarizationResultSet.TYPE_FLOAT:
                                            if (minDataSet[rowMapValue][j] != null && !Float.isNaN((Float) minDataSet[rowMapValue][j])) {
                                                if (!hideFloatTag) {
                                                    prtgOutput.append(String.format("%.2f", (Float) minDataSet[rowMapValue][j]));
                                                } else {
                                                    prtgOutput.append(String.format("%d", ((Float) minDataSet[rowMapValue][j]).intValue()));
                                                }
//                                            minMaxAverageSumFinder.addMember((Float) minDataSet[rowMapValue][j]);
                                            } else {
                                                if (!hideFloatTag) {
                                                    prtgOutput.append("0.0");
                                                } else {
                                                    prtgOutput.append("0");
                                                }
                                            }
                                            break;
                                        case StatSummarizationResultSet.TYPE_INT:
                                            if (minDataSet[rowMapValue][j] != null) {
                                                prtgOutput.append(minDataSet[rowMapValue][j]);
//                                            minMaxAverageSumFinder.addMember(Integer.parseInt(minDataSet[rowMapValue][j].toString()));
                                            } else {
                                                prtgOutput.append("0");
                                            }
                                            break;
                                        case StatSummarizationResultSet.TYPE_LONG:
                                            if (minDataSet[rowMapValue][j] != null) {
                                                prtgOutput.append(minDataSet[rowMapValue][j]);
//                                            minMaxAverageSumFinder.addMember(Long.parseLong(minDataSet[rowMapValue][j].toString()));
                                            } else {
                                                prtgOutput.append("0");
                                            }
                                            break;
                                        case StatSummarizationResultSet.TYPE_STRING:
                                            prtgOutput.append((String) minDataSet[rowMapValue][j]);
                                            break;
                                        default:
                                            logger.error("Unknown Type");
                                    }
                                    prtgOutput.append("</value>");
                                    if (!hideFloatTag != true) {
                                        if (this.metaData[j] == StatSummarizationResultSet.TYPE_FLOAT || this.metaData[j] == StatSummarizationResultSet.TYPE_DOUBLE) {
                                            prtgOutput.append("<float>1</float>");
                                        }
                                    }

                                    if (hideUnit == false) {
                                        prtgOutput.append("<CustomUnit>");
                                        prtgOutput.append(this.unitType[j]);
                                        prtgOutput.append("</CustomUnit>");
                                    }
                                    prtgOutput.append("</result>");
                                }
                            }
                        }
                    }
                }

                // Evaluate Result ????
                if (showEvaluateOnly == true) {
                    double min = minMaxAverageSumFinder.getMin();
                    double max = minMaxAverageSumFinder.getMax();
                    double sum = minMaxAverageSumFinder.getSum();
                    double average = minMaxAverageSumFinder.getAverage();
                    if (evaluatePrefix == null) {
                        evaluatePrefix = "";
                    } else {
                        evaluatePrefix = evaluatePrefix + ".";
                    }
                    overidePRTGOutput = true;
                    overridePRTGOutput.append("<PRTG>");
                    if (showEvaluateMin) {
                        overridePRTGOutput.append(PRTGUtil.getPRTGResultTag(evaluatePrefix + "MIN", new Double(min), null, evaluateAsInteger));
                    }
                    if (showEvaluateMax) {
                        overridePRTGOutput.append(PRTGUtil.getPRTGResultTag(evaluatePrefix + "MAX", new Double(max), null, evaluateAsInteger));
                    }
                    if (showEvaluateSum) {
                        overridePRTGOutput.append(PRTGUtil.getPRTGResultTag(evaluatePrefix + "SUM", new Double(sum), null, evaluateAsInteger));
                    }
                    if (showEvaluateAverage) {
                        overridePRTGOutput.append(PRTGUtil.getPRTGResultTag(evaluatePrefix + "AVERAGE", new Double(average), null, evaluateAsInteger));
                    }
                    overridePRTGOutput.append("</PRTG>");
                }
            }
            prtgOutput.append("</PRTG>");

            // Evaluate ??
            if (overidePRTGOutput == true) {
                prtgOutput = overridePRTGOutput;
            }

            if (prtgOutput.toString().compareTo("<PRTG></PRTG>") == 0) {
                String tmpString = "<PRTG><error>1</error><text>No Raw Data Received</text></PRTG>";

                return tmpString;
            }
        }
        return prtgOutput.toString();
    }
}
