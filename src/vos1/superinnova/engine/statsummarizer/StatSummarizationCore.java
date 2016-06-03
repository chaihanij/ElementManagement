/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.statsummarizer;

import org.apache.log4j.Logger;
import vos1.superinnova.engine.statproccessor.SuperInnovaStatProcessor;
import vos1.superinnova.engine.statsummarizer.templates.*;

import java.util.Properties;

/**
 * @author HugeScreen
 */
public class StatSummarizationCore {

    final static Logger logger = Logger.getLogger(StatSummarizationCore.class);

    private SuperInnovaStatProcessor superInnovaStatProcessor;
    private StatSummarizationModule[] statSummarizationModuleArray = null;
    private StatSummarizerConfiguration[] statSummarizerConfigurationArray = null;

    private Properties statSummarizerNameMap = null;

    public StatSummarizationCore(SuperInnovaStatProcessor superInnovaStatProcessor) {
        this.superInnovaStatProcessor = superInnovaStatProcessor;
        this.statSummarizerNameMap = new Properties();
        // Make StatSummarizationModule
        this.statSummarizerConfigurationArray = this.superInnovaStatProcessor.getSuperInnovaStatEngine().getStatSummarizerConfiguration();
        makeStatSummarizationModule();
    }

    private void makeStatSummarizationModule() {
        if (this.statSummarizerConfigurationArray.length > 0) {
            this.statSummarizationModuleArray = new StatSummarizationModule[this.statSummarizerConfigurationArray.length];
            for (int i = 0; i < this.statSummarizerConfigurationArray.length; i++) {
                // Switch Type
                try {
                    this.statSummarizationModuleArray[i] = createStatSummarizationModuleFromConfiguration(this.statSummarizerConfigurationArray[i]);
                    logger.info("StatSummarization name " + this.statSummarizerConfigurationArray[i].getStatName() + " module " + this.statSummarizerConfigurationArray[i].getSummarizationModule());
                    this.statSummarizerNameMap.put(this.statSummarizerConfigurationArray[i].getStatName(), i);
                } catch (Exception e) {
                    logger.error(e);
                }

            }
        }
    }

    private StatSummarizationModule createStatSummarizationModuleFromConfiguration(StatSummarizerConfiguration statSummarizerConfiguration) throws Exception {
        StatSummarizationModule statSummarizationModule = null;
        // Which StatSummarizationModuleType Will Be Using
        if (statSummarizerConfiguration.summarizationModule.compareToIgnoreCase("successRate") == 0) {
            // Create SuperNova SuccessRate Template
            logger.info("Create module success rate");
            statSummarizationModule = new SupernovaSuccessRateSummarizationModule(this, statSummarizerConfiguration);
        } else if (statSummarizerConfiguration.summarizationModule.compareToIgnoreCase("statCategorization") == 0) {
            // Create SuperNova SuccessRate Template
            logger.info("Create module stat categorization");
            statSummarizationModule = new SupernovaStatCategorizationModule(this, statSummarizerConfiguration);
        } else if (statSummarizerConfiguration.summarizationModule.compareToIgnoreCase("multiSuccessRate") == 0) {
            // Create SuperNova Multi SuccessRate Template
            logger.info("Create module multi success rate");
            statSummarizationModule = new SupernovaMultiSuccessRateSummarizationModule(this, statSummarizerConfiguration);
        } else if (statSummarizerConfiguration.summarizationModule.compareToIgnoreCase("errorAnalysis") == 0) {
            // Create SuperNova Multi SuccessRate Template
            logger.info("Create module error analysis");
            statSummarizationModule = new SupernovaRegexFilterizationModule(this, statSummarizerConfiguration);
        } else if (statSummarizerConfiguration.summarizationModule.compareToIgnoreCase("addSubtractModule") == 0) {
            // Create SuperNova Multi SuccessRate Template
            logger.info("Create module add subtract module");
            statSummarizationModule = new SupernovaStatAddSubtractModule(this, statSummarizerConfiguration);
        } else {
            Exception e = new Exception("Error : cannot create StatSummarizationModule From Configuration " + statSummarizerConfiguration.statName + ", " + statSummarizerConfiguration.summarizationModule);
            logger.fatal(e.getMessage());
            throw e;
        }
        return statSummarizationModule;
    }

    public SuperInnovaStatProcessor getSuperInnovaStatProcessor() {
        return this.superInnovaStatProcessor;
    }

    // When Data in Database is Completed, Invoke This method to
    // Invoke Stat Summarization Process of Every Module, Under This StatSummarizationCore Controlled.
    public void invokeStatSummarizationProcess() {
        for (int i = 0; i < this.statSummarizerConfigurationArray.length; i++) {
            logger.info("Statistics summarization on module " + this.statSummarizationModuleArray[i].statSummarizerConfiguration.summarizationModule);
            this.statSummarizationModuleArray[i].startStatSummarizationProcess();
            logger.info("Statistics summarization end");
            logger.debug("==================================");
        }
    }

    public StatSummarizationModule getStatSummarizationModule(String statSummarizationName) {
        logger.debug("logger.fatal(e.getMessage());");
        if (statSummarizationName != null) {
            Integer index = (Integer) statSummarizerNameMap.get(statSummarizationName);
            if (index != null) {
                return statSummarizationModuleArray[index];
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
