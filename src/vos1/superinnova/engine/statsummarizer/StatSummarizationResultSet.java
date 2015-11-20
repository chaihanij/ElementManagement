/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.statsummarizer;

import org.apache.log4j.Logger;

import java.util.Date;

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
            }
        } catch (Exception e) {
            logger.error(e);
        }

    }

    public void putObject(int i, int j, Object rowObj) {
        this.lastestDataSet[i][j] = rowObj;
    }

    public Object getObject(int i, int j) {
        return this.lastestDataSet[i][j];
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
                        sb.append((Date) lastestDataSet[i][j]);
                        break;
                    case StatSummarizationResultSet.TYPE_DOUBLE:
                        sb.append((Double) lastestDataSet[i][j]);
                        break;
                    case StatSummarizationResultSet.TYPE_FLOAT:
                        sb.append(String.format("%.2f", (Float) lastestDataSet[i][j]));
                        break;
                    case StatSummarizationResultSet.TYPE_INT:
                        sb.append((Integer) lastestDataSet[i][j]);
                        break;
                    case StatSummarizationResultSet.TYPE_LONG:
                        sb.append((Long) lastestDataSet[i][j]);
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
}
