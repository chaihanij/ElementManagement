/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.statproccessor.statgathermodule.statparser.predefined;

import java.io.BufferedReader;
import java.io.StringReader;

import org.apache.log4j.Logger;
import vos1.superinnova.engine.statproccessor.SuperInnovaStatCore;
import vos1.superinnova.engine.statproccessor.SuperInnovaStatEnginePropertiesLookup;
import vos1.superinnova.engine.statproccessor.predefinedengine.GeneralSuperInnovaStatEngine;
import vos1.superinnova.engine.statproccessor.statgathermodule.StatGatherConfiguration;
import vos1.superinnova.engine.statproccessor.statgathermodule.StatGathererExecutor;
import vos1.superinnova.engine.statproccessor.statgathermodule.StatGathererParser;

/**
 * @author HugeScreen
 */
public class SuperInnovaStatParser extends StatGathererParser {

    final static Logger logger = Logger.getLogger(SuperInnovaStatParser.class);

    StatGathererExecutor statGathererExecutor = null;

    public SuperInnovaStatParser(StatGathererExecutor statGathererExecutor) {
        this.statGathererExecutor = statGathererExecutor;
    }

    @Override
    public String[] getCreateRawTableSQL(SuperInnovaStatEnginePropertiesLookup superInnovaStatEnginePropertiesLookup) {
        String createRawSuperInnovaTable = "create table raw_" + superInnovaStatEnginePropertiesLookup.get("ENGINE", "StorageName") + "\n" +
                "(\n" +
                "site int,\n" +
                "block int,\n" +
                "subBlock int,\n" +
                "date DATETIME,\n" +
                "hostname VARCHAR(32),\n" +
                "statName VARCHAR(256),\n" +
                "minCounter int,\n" +
                "maxCounter int,\n" +
                "averageCounter float,\n" +
                "sumCounter BIGINT,\n" +
                "primary key (site,block,subBlock,date,hostname,statname),\n" +
                "unique (date,hostname,statname)\n" +
                ")";
        return new String[]{createRawSuperInnovaTable};
    }

    @Override
    public String[] getSelectRawTableSQL(SuperInnovaStatEnginePropertiesLookup superInnovaStatEnginePropertiesLookup) {
        String selectSQL = "select * from raw_" + superInnovaStatEnginePropertiesLookup.get("ENGINE", "StorageName");
        return new String[]{selectSQL};
    }

    @Override
    public String[] getInsertRawTableSQL(String input, StatGatherConfiguration statGatherConfiguration, SuperInnovaStatEnginePropertiesLookup superInnovaStatEnginePropertiesLookup) {

        //System.out.println("input : "+input);
        BufferedReader reader = null;
        String line = null;

        int site = -1;
        int block = -1;
        int subBlock = -1;
        if (statGatherConfiguration != null) {
            // Check Site
            //System.out.println("Looking for Site : "+statGatherConfiguration.getSite());
            if (superInnovaStatEnginePropertiesLookup.get(GeneralSuperInnovaStatEngine.SITE_KEYWORD, statGatherConfiguration.getSite()) != null) {

                site = (Integer) superInnovaStatEnginePropertiesLookup.get(GeneralSuperInnovaStatEngine.SITE_KEYWORD, statGatherConfiguration.getSite());
                //System.out.println("Site : Found");
            } else {
                site = (Integer) superInnovaStatEnginePropertiesLookup.get(GeneralSuperInnovaStatEngine.SITE_KEYWORD, "UNKNOWN");
                //System.out.println("Site : Unknown");
            }
            // Check Block
            //System.out.println("Looking for Block : "+statGatherConfiguration.getBlock());
            if (superInnovaStatEnginePropertiesLookup.get(GeneralSuperInnovaStatEngine.BLOCK_KEYWORD, statGatherConfiguration.getBlock()) != null) {

                block = (Integer) superInnovaStatEnginePropertiesLookup.get(GeneralSuperInnovaStatEngine.BLOCK_KEYWORD, statGatherConfiguration.getBlock());
                //System.out.println("BLOCK : Found");
            } else {
                block = (Integer) superInnovaStatEnginePropertiesLookup.get(GeneralSuperInnovaStatEngine.BLOCK_KEYWORD, "UNKNOWN");
                //System.out.println("BLOCK : Unknown");
            }
            // Check SubBlock
            //System.out.println("Looking for SubBlock : "+statGatherConfiguration.getSubBlock());
            if (superInnovaStatEnginePropertiesLookup.get(GeneralSuperInnovaStatEngine.SUBBLOCK_KEYWORD, statGatherConfiguration.getSubBlock()) != null) {

                subBlock = (Integer) superInnovaStatEnginePropertiesLookup.get(GeneralSuperInnovaStatEngine.SUBBLOCK_KEYWORD, statGatherConfiguration.getSubBlock());
                //System.out.println("SUBBLOCK : Found");
            } else {
                subBlock = (Integer) superInnovaStatEnginePropertiesLookup.get(GeneralSuperInnovaStatEngine.SUBBLOCK_KEYWORD, "UNKNOWN");
                //System.out.println("SUBBLOCK : Unknown");
            }

        }

        StringBuffer sqlStringBuffer = new StringBuffer();
        sqlStringBuffer.append("insert into raw_" + superInnovaStatEnginePropertiesLookup.get("ENGINE", "StorageName") + " values ");
        // Parse
        try {
            reader = new BufferedReader(new StringReader(input));
            int i = 0;
            while ((line = reader.readLine()) != null) {
                //System.out.println("line : "+line);
                if (line.length() == 0 || line.trim().startsWith("TIME") || line.trim().startsWith("End")) {
                    continue;
                }
                if (i > 0) {
                    sqlStringBuffer.append(",");
                }
                sqlStringBuffer.append("(");
                sqlStringBuffer.append(site);
                sqlStringBuffer.append(",");
                sqlStringBuffer.append(block);
                sqlStringBuffer.append(",");
                sqlStringBuffer.append(subBlock);

                // Parse Input Array
                String[] splitInput = line.split("\\|");
                for (int j = 0; j < splitInput.length; j++) {
                    sqlStringBuffer.append(",");
                    if (j >= 0 && j < 3) {
                        if (j == 0) {
                            sqlStringBuffer.append("to_date('");
                        } else {
                            sqlStringBuffer.append("'");
                        }

                    }
                    sqlStringBuffer.append(splitInput[j]);
                    if (j >= 0 && j < 3) {
                        if (j == 0) {
                            sqlStringBuffer.append("','YYYYMMDD HH24:MI:SS')");
                        } else {
                            sqlStringBuffer.append("'");
                        }
                    }
                }

                sqlStringBuffer.append(") \n");
                //System.out.println("partial sql : "+sqlStringBuffer.toString());
                i++;
            }
            //System.out.println("sql : "+sqlStringBuffer.toString());

        } catch (Exception e) {
            logger.error("Command insert : " + sqlStringBuffer.toString());
            logger.error(e);
            return null;
        }

        return new String[]{sqlStringBuffer.toString()};
    }

    @Override
    public String[] getTruncateRawTableSQL(SuperInnovaStatEnginePropertiesLookup superInnovaStatEnginePropertiesLookup) {
        String truncateTableSQL = "truncate table raw_" + superInnovaStatEnginePropertiesLookup.get("ENGINE", "StorageName");
        return new String[]{truncateTableSQL};
    }
}
