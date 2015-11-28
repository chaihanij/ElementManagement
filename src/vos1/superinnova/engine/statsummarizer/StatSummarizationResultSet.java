/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.statsummarizer;

import org.apache.log4j.Logger;

/**
 * @author HugeScreen
 */
public class StatSummarizationResultSet {

    final static Logger logger = Logger.getLogger(StatSummarizationResultSet.class);

    public static final int TYPE_STRING = 0;
    public static final int TYPE_DATE = 2;
    public static final int TYPE_INT = 3;
    public static final int TYPE_LONG = 4;
    public static final int TYPE_FLOAT = 5;
    public static final int TYPE_DOUBLE = 6;

    public static final String[] NAME_OF_VARTYPE = new String[]{"String", null, "Date", "Integer", "Long", "Float", "Double"};

    protected int columnSize = 0;

    protected int[] metaData = null;
    protected Object[][] lastestDataSet = null;
    protected Object[][] minDataSet = null;
    protected Object[][] maxDataSet = null;

    protected String[] columnName = null;
    protected String[] unitType = null;


    public StatSummarizationResultSet(int row, int[] metaData, String[] columnName, String[] unitType) {
        try {
            if (metaData.length > 0 && (metaData.length == columnName.length) && (columnName.length == unitType.length)) {
                this.columnSize = metaData.length;
                this.metaData = metaData;
                this.columnName = columnName;
                this.unitType = unitType;
                this.lastestDataSet = new Object[row][this.columnSize];
                this.minDataSet = new Object[row][this.columnSize];
                this.maxDataSet = new Object[row][this.columnSize];
            }
        } catch (Exception e) {
            logger.error(e);
        }

    }
    public String getColumnName(Integer i) {
//        logger.debug("getColumnName = " + this.columnName[i]);
        return this.columnName[i];
    }
    public void putObject(int i, int j, Object rowObj) {
        this.lastestDataSet[i][j] = rowObj;
    }

    public Object getObject(int i, int j) {
        return this.lastestDataSet[i][j];
    }

    public void putMinObject(int i, int j, Object rowObj) {
        this.minDataSet[i][j] = rowObj;
    }

    public Object getMinObject(int i, int j) {
        return this.minDataSet[i][j];
    }

    public void putMaxObject(int i, int j, Object rowObj) {
        this.maxDataSet[i][j] = rowObj;
    }

    public Object getMaxObject(int i, int j) {
        return this.maxDataSet[i][j];
    }

    public void dumpDataSet() {
        StringBuffer sb = new StringBuffer();
        String separator = ",";
        for (int i = 0; i < columnName.length; i++) {
            if (i > 0) {
                sb.append(separator);
            }
            String tmp = columnName[i] + "-" + unitType[i] + "[" + NAME_OF_VARTYPE[metaData[i]] + "]";
            sb.append(tmp);
        }
        sb.append("\n");
        for (int i = 0; i < lastestDataSet.length; i++) {
            for (int j = 0; j < lastestDataSet[i].length; j++) {
                if (j > 0) {
                    sb.append(separator);
                }

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
                        logger.warn("Unknown Type metaData[" + j + "]" + " value =" + this.metaData[j]);
                }
            }// End J
            sb.append("\n");
        }// End I
        logger.debug(sb.toString());
    }

    public void dumpMinDataSet() {
        StringBuffer sb = new StringBuffer();
        String separator = ",";
        for (int i = 0; i < columnName.length; i++) {
            if (i > 0) {
                sb.append(separator);
            }
            String tmp = columnName[i] + "-" + unitType[i] + "[" + NAME_OF_VARTYPE[metaData[i]] + "]";
            sb.append(tmp);
        }
        for (int i = 0; i < minDataSet.length; i++) {
            for (int j = 0; j < minDataSet[i].length; j++) {
                if (j > 0) {
                    sb.append(separator);
                }
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
                        logger.warn("Unknown Type metaData[" + j + "]" + " value =" + this.metaData[j]);
                }
            }// End J
            sb.append("\n");
        }// End I
        sb.append("\n");
        logger.debug(sb.toString());;
    }
    public void dumpMaxDataSet() {
        StringBuffer sb = new StringBuffer();
        String separator = ",";
        for (int i = 0; i < columnName.length; i++) {
            if (i > 0) {
                sb.append(separator);
            }
            String tmp = columnName[i] + "-" + unitType[i] + "[" + NAME_OF_VARTYPE[metaData[i]] + "]";
            sb.append(tmp);
        }
        for (int i = 0; i < maxDataSet.length; i++) {
            for (int j = 0; j < maxDataSet[i].length; j++) {
                if (j > 0) {
                    sb.append(separator);
                }
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
                        sb.append(minDataSet[i][j]);
                        break;
                    case StatSummarizationResultSet.TYPE_STRING:
                        sb.append((String) minDataSet[i][j]);
                        break;
                    default:
                        logger.warn("Unknown Type metaData[" + j + "]" + " value =" + this.metaData[j]);
                }
            }// End J
            sb.append("\n");
        }// End I
        sb.append("\n");
        logger.debug(sb.toString());;
    }
}
