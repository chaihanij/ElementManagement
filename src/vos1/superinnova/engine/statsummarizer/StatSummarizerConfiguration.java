/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.statsummarizer;

import org.apache.log4j.Logger;

import java.io.FileReader;
import java.util.Properties;

/**
 *
 * @author HugeScreen
 */
public class StatSummarizerConfiguration {

    final static Logger logger = Logger.getLogger(StatSummarizerConfiguration.class);

    boolean rootLevelSummarization = false;
    boolean siteSummarization = false;
    boolean vipSummarization = false;
    boolean blockSummarization = false;
    boolean subBlockSummarization = false;
    Properties additionalProperties = null;

    String statName = null;
    String summarizationModule = null;

    Properties prop = null;

    String statSummarizerConfigurationFileName = null;

    public String getStatName() {
        return statName;
    }

    public String getSummarizationModule() {
        return summarizationModule;
    }

    public String getStatSummarizerConfigurationFileName() {
        return statSummarizerConfigurationFileName;
    }

    public Properties getAdditionalProperties() {
        return additionalProperties;
    }

    public static StatSummarizerConfiguration makeStatSummarizerConfiguration(String filename) {
        Properties prop = new Properties();
        try {
            prop.load(new FileReader(filename));
            String statname = prop.getProperty("name");
            String summarizationModule = prop.getProperty("summarization_module");
            String summarizatioLevel = prop.getProperty("summarization_level");

            boolean[] booleanArray = StatSummarizerConfiguration.textLevelToBooleanArray(summarizatioLevel);
            return new StatSummarizerConfiguration(statname, summarizationModule, booleanArray[0], booleanArray[1], booleanArray[2], booleanArray[3], prop);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    public static boolean[] textLevelToBooleanArray(String text) {
        boolean[] booleanArray = new boolean[4];
        String[] token = text.split("\\|");
        for (int i = 0; i < token.length; i++) {
            booleanArray[i] = false;
        }
        for (String token1 : token) {
            if (token1.compareToIgnoreCase("root") == 0) {
                booleanArray[0] = true;
            } else if (token1.compareToIgnoreCase("site") == 0) {
                booleanArray[1] = true;
            } else if (token1.compareToIgnoreCase("block") == 0) {
                booleanArray[2] = true;
            } else if (token1.compareToIgnoreCase("subblock") == 0) {
                booleanArray[3] = true;
            }
        }
        return booleanArray;
    }

    public StatSummarizerConfiguration(String statName, String summarizationModule, boolean rootLevelSummarization, boolean siteSummarization, boolean blockSummarization, boolean subBlockSummarization, Properties additionalProperties) {
        this.statName = statName;
        this.summarizationModule = summarizationModule;
        this.rootLevelSummarization = rootLevelSummarization;
        this.siteSummarization = siteSummarization;
        this.blockSummarization = blockSummarization;
        this.subBlockSummarization = subBlockSummarization;
        this.additionalProperties = additionalProperties;
    }

    private void processConfigurationFile() {
        if (this.statSummarizerConfigurationFileName != null && this.prop != null) {

            // Parse StatName
            String statName = prop.getProperty("STAT_NAME");
            if (statName != null && statName.length() > 0) {
                this.statName = statName;
            }
            // Parse SummarizationModule
            String summarizationModule = prop.getProperty("SUMMARIZATION_MODULE");
            if (summarizationModule != null && summarizationModule.length() > 0) {
                this.summarizationModule = summarizationModule;
            }

            // Parse Summarization Level
            String level = prop.getProperty("SUMMARIZATION_LEVEL");
            String[] levelToken = level.split("\\|");
            for (String levelToken1 : levelToken) {
                if (levelToken1.compareToIgnoreCase("SITE") == 0) {
                    siteSummarization = true;
                } else if (levelToken1.compareToIgnoreCase("BLOCK") == 0) {
                    blockSummarization = true;
                } else if (levelToken1.compareToIgnoreCase("SUBBLOCK") == 0) {
                    subBlockSummarization = true;
                }
            } // End For

        }// Enf if this.statSummarzation...
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(statName);
        sb.append(",");
        sb.append(summarizationModule);
        sb.append(",");
        sb.append(rootLevelSummarization);
        sb.append(",");
        sb.append(siteSummarization);
        sb.append(",");
        sb.append(blockSummarization);
        sb.append(",");
        sb.append(subBlockSummarization);
        return sb.toString();
    }
}
