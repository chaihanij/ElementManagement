/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.statproccessor;

import org.apache.log4j.Logger;
import vos1.superinnova.engine.statproccessor.statgathermodule.StatGatherConfiguration;
import vos1.superinnova.engine.statsummarizer.StatSummarizerConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.Enumeration;
import java.util.Properties;

/**
 * @author HugeScreen
 */
public class SuperInnovaStatCoreConfigurator {

    final static Logger logger = Logger.getLogger(SuperInnovaStatCoreConfigurator.class);

    File rootConfigurationPath = null;

    static final String fileSeparator = System.getProperty("file.separator");


    File engineConfigurationPath = null;
    File summarizerConfigurationPath = null;
    int engineConfigurationCounter = 0;
    File[] engineConfiguration = null;
    static final int MAXIMUM_ENGINE = 2048;

    static final String MAIN_CONFIGURATION = "mainConfiguration.conf";
    static final String ENGINE_CONFIGURATION_DIRECTORY = "engineConfiguration";
    static final String ENGINE_CONFIGURATION_FILENAME = "engine.conf";
    static final String GATHERERCONFIGURATION_DIRECTORY = "gathererConfiguration";
    static final String GATHERERCONTROL_FILENAME = "gathererControl.conf";
    static final String GATHERERTARGET_FILENAME = "gathererTarget.conf";
    static final String SUMMARIZERCONFIGURATION_DIRECTORY = "summarizerConfiguration";

    Properties engineCoreConfiguration = null;
    Properties engineList = null;
    String[] engineNameList = null;
    Properties[] gathererControl = null;
    StatGatherConfiguration[][] statGatherConfiguration2DArray = null;
    StatSummarizerConfiguration[][] statSummarizerConfiguration2DArray = null;
    SuperInnovaStatEngineConfiguration[] superInnovaStatEngineConfiguration2DArray = null;

    public SuperInnovaStatCoreConfigurator(String rootConfigurationPath) {

        logger.info("Read Configuration");
        logger.debug("Configuration path : " + rootConfigurationPath);
        logger.debug("MAXIMUM ENGINE " + SuperInnovaStatCoreConfigurator.MAXIMUM_ENGINE);

        engineCoreConfiguration = new Properties();
        engineList = new Properties();
        engineNameList = new String[SuperInnovaStatCoreConfigurator.MAXIMUM_ENGINE];
        gathererControl = new Properties[SuperInnovaStatCoreConfigurator.MAXIMUM_ENGINE];
        this.rootConfigurationPath = new File(rootConfigurationPath);
        this.engineConfiguration = new File[SuperInnovaStatCoreConfigurator.MAXIMUM_ENGINE];
        this.statGatherConfiguration2DArray = new StatGatherConfiguration[SuperInnovaStatCoreConfigurator.MAXIMUM_ENGINE][];
        this.statSummarizerConfiguration2DArray = new StatSummarizerConfiguration[SuperInnovaStatCoreConfigurator.MAXIMUM_ENGINE][];
        this.superInnovaStatEngineConfiguration2DArray = new SuperInnovaStatEngineConfiguration[SuperInnovaStatCoreConfigurator.MAXIMUM_ENGINE];

        try {

            this.engineCoreConfiguration.load(new FileReader(new File(this.rootConfigurationPath.getCanonicalPath() + SuperInnovaStatCoreConfigurator.fileSeparator + MAIN_CONFIGURATION)));
            this.engineConfigurationPath = new File(this.rootConfigurationPath.getCanonicalPath() + SuperInnovaStatCoreConfigurator.fileSeparator + SuperInnovaStatCoreConfigurator.ENGINE_CONFIGURATION_DIRECTORY);
            if (this.engineConfigurationPath.isDirectory()) {
                File[] fileUnderEngineConfigurationPath = this.engineConfigurationPath.listFiles();
                for (int i = 0; i < fileUnderEngineConfigurationPath.length; i++) {
                    if (fileUnderEngineConfigurationPath[i].isDirectory()) {

                        engineConfiguration[engineConfigurationCounter] = fileUnderEngineConfigurationPath[i];
                        Properties prop = new Properties();
                        prop.load(new FileReader(engineConfiguration[engineConfigurationCounter].getCanonicalPath() + SuperInnovaStatCoreConfigurator.fileSeparator + SuperInnovaStatCoreConfigurator.ENGINE_CONFIGURATION_FILENAME));
                        engineList.setProperty(prop.getProperty("ENGINE_NAME"), String.valueOf(new Integer(engineConfigurationCounter)));
                        engineNameList[engineConfigurationCounter] = prop.getProperty("ENGINE_NAME");
                        String engineType = prop.getProperty("ENGINE_TYPE");
                        this.superInnovaStatEngineConfiguration2DArray[engineConfigurationCounter] = new SuperInnovaStatEngineConfiguration(engineNameList[engineConfigurationCounter], engineType);

                        logger.info("Begin Read Engine[" + engineConfigurationCounter + "] name : " + prop.getProperty("ENGINE_NAME"));
                        logger.info("Engine[" + engineConfigurationCounter + "] type : " + prop.getProperty("ENGINE_TYPE"));


                        // Gatherer Control
                        String gathererConfigurationPath = engineConfiguration[engineConfigurationCounter].getCanonicalPath() +
                                SuperInnovaStatCoreConfigurator.fileSeparator +
                                SuperInnovaStatCoreConfigurator.GATHERERCONFIGURATION_DIRECTORY;
                        String gathererControlPath = gathererConfigurationPath +
                                SuperInnovaStatCoreConfigurator.fileSeparator +
                                GATHERERCONTROL_FILENAME;
                        String gathererTargetPath = gathererConfigurationPath +
                                SuperInnovaStatCoreConfigurator.fileSeparator +
                                GATHERERTARGET_FILENAME;
                        gathererControl[engineConfigurationCounter] = new Properties();
                        gathererControl[engineConfigurationCounter].load(new FileReader(gathererControlPath));

                        // Gatherer Target
                        logger.info("Read configuration gathererConfiguration");
                        logger.debug("GathererConfiguration path : " + gathererConfigurationPath);

                        int usableRow = 0;
                        // FirstWalk
                        BufferedReader bufferedReader = null;
                        try {
                            bufferedReader = new BufferedReader(new FileReader(gathererTargetPath));
                            String line = null;
                            while ((line = bufferedReader.readLine()) != null) {
                                if (line.startsWith("#") == false)
                                    usableRow++;
                            }
                        } catch (Exception e) {
                            logger.error("Engine[" + prop.getProperty("ENGINE_NAME") + "]  Gatherer Target error");
                        } finally {
                            try {
                                bufferedReader.close();
                            } catch (Exception e) {
                                logger.error("Gatherer Target path error : " + gathererTargetPath);
                            }
                        }

                        // Init statGathererConfigurationCounter UsableRow
                        if (usableRow > 0) {
                            this.statGatherConfiguration2DArray[engineConfigurationCounter] = new StatGatherConfiguration[usableRow];
                            logger.info("Gatherer Target size : [" + usableRow + "]");
                        } else {
                            logger.info("Invalid Gatherer Target");
                        }

                        // Second Walk
                        bufferedReader = null;

                        int lineCounter = 0;

                        try {
                            bufferedReader = new BufferedReader(new FileReader(gathererTargetPath));
                            String line = null;
                            while ((line = bufferedReader.readLine()) != null) {
                                if (line.startsWith("#") == false) {
                                    String[] lineToken = null;
                                    String site = null, block = null, subBlock = null, url = null;

                                    lineToken = line.split("\\|");

                                    if (lineToken != null) {
                                        site = lineToken[0];
                                        block = lineToken[1];
                                        subBlock = lineToken[2];
                                        url = lineToken[3];

                                        int fetchInterval = Integer.parseInt(gathererControl[engineConfigurationCounter].getProperty("fetchInterval"));
                                        int fetchType = fetchTypeToInt(gathererControl[engineConfigurationCounter].getProperty("fetchType"));
                                        int fetchTimeout = Integer.parseInt(gathererControl[engineConfigurationCounter].getProperty("fetchTimeout"));
                                        String storageName = gathererControl[engineConfigurationCounter].getProperty("storageName");
                                        String fetchParser = gathererControl[engineConfigurationCounter].getProperty("fetchParser");
                                        int workerPriority = workerPriorityToInt(gathererControl[engineConfigurationCounter].getProperty("workerPriority"));

                                        this.statGatherConfiguration2DArray[engineConfigurationCounter][lineCounter] = new StatGatherConfiguration(fetchType,
                                                site,
                                                block,
                                                subBlock,
                                                url,
                                                storageName,
                                                fetchParser,
                                                workerPriority,
                                                fetchInterval,
                                                fetchTimeout);

                                        logger.debug("" + this.statGatherConfiguration2DArray[engineConfigurationCounter][lineCounter].toString());

                                    } else {
                                        logger.warn("format not found. : " + line);
                                    }
                                    lineCounter++;
                                }
                            }
                        } catch (Exception e) {
                            logger.error("Engine[" + prop.getProperty("ENGINE_NAME") + "]  Gatherer Target error line " + lineCounter);
                        } finally {
                            try {
                                bufferedReader.close();
                            } catch (Exception e) {
                                logger.error("Gatherer Target path error : " + gathererTargetPath);
                            }
                        }


                        // == BEGIN StatSummarizer Directory ========================================================
                        String summarizerConfigurationPath = engineConfiguration[engineConfigurationCounter].getCanonicalPath() +
                                SuperInnovaStatCoreConfigurator.fileSeparator +
                                SuperInnovaStatCoreConfigurator.SUMMARIZERCONFIGURATION_DIRECTORY;

                        logger.info("Read configuration summarizerConfiguration.");
                        logger.debug("SummarizerConfiguration path : " + summarizerConfigurationPath);

                        this.summarizerConfigurationPath = new File(summarizerConfigurationPath);

                        if (this.summarizerConfigurationPath.isDirectory() == true) {

                            File[] fileUnderStatSummarizerConfigurationPath = this.summarizerConfigurationPath.listFiles(
                                    new FilenameFilter() {
                                        @Override
                                        public boolean accept(File dir, String name) {
                                            return name.toLowerCase().endsWith(".conf");
                                        }
                                    }
                            );

                            this.statSummarizerConfiguration2DArray[engineConfigurationCounter] = new StatSummarizerConfiguration[fileUnderStatSummarizerConfigurationPath.length];
                            logger.info("Summarizer configuration size : [" + fileUnderStatSummarizerConfigurationPath.length + "]");
                            for (int j = 0; j < fileUnderStatSummarizerConfigurationPath.length; j++) {

                                this.statSummarizerConfiguration2DArray[engineConfigurationCounter][j] = StatSummarizerConfiguration.makeStatSummarizerConfiguration(fileUnderStatSummarizerConfigurationPath[j].getCanonicalPath());
                                logger.debug(this.statSummarizerConfiguration2DArray[engineConfigurationCounter][j].toString());
                                //System.out.println(fileUnderStatSummarizerConfigurationPath[j].getCanonicalPath());
                            }// End for fileUnderStatSummarizerConfigurationPath
                        }// End StaSummarizer Directory
                        // ============= Finish Stat Summarizer Directory ===========================================
                        engineConfigurationCounter++;
                        logger.info("END");
                    }// End if(Directory)
                }// End For Loop
            }// End EngineConfiguration Directory
        }//End Try
        catch (Exception e) {
            logger.fatal("Error Configuration");
            System.exit(-1);
        }


    }

    private int fetchTypeToInt(String fetchType) {
        if (fetchType.compareToIgnoreCase("http") == 0) {
            return StatGatherConfiguration.FETCHTYPE_HTTP;
        }
        return -1;
    }

    private int workerPriorityToInt(String workerPriority) {
        if (workerPriority.compareToIgnoreCase("normal") == 0) {
            return StatGatherConfiguration.PRIORITY_NORMAL;
        }
        return -1;
    }

    public int getEngineConfigurationCounter() {
        return this.engineConfigurationCounter;
    }

    public String getEngineName(int i) {
        return this.engineNameList[i];
    }

    public StatGatherConfiguration[] getStatConfigurationArray(int i) {
        return this.statGatherConfiguration2DArray[i];
    }

    public StatSummarizerConfiguration[] getStatSummarizerConfigurationArray(int i) {
        return this.statSummarizerConfiguration2DArray[i];
    }

    public SuperInnovaStatEngineConfiguration getSuperInnovaStatEngineConfigurationArray(int i) {
        return this.superInnovaStatEngineConfiguration2DArray[i];
    }

    public Properties getEngineCoreConfiguration() {
        return engineCoreConfiguration;
    }

    public String dumpConfigurationFilePath() {
        try {
            StringBuilder messages = new StringBuilder();
            messages.append("root path : " + this.rootConfigurationPath.getCanonicalPath());
            messages.append("\nroot engineConfiguration path : " + this.engineConfigurationPath.getCanonicalPath());
            for (int i = 0; i < engineConfigurationCounter; i++) {
                messages.append("\n  |_ engineName : " + this.engineNameList[i]);
                messages.append("\n  |_ engineConfiguration : " + this.superInnovaStatEngineConfiguration2DArray[i]);
                messages.append("\n     |_ engineConfiguration path : " + this.engineConfiguration[i].getCanonicalPath());
                Enumeration enumeration = gathererControl[i].propertyNames();
                while (enumeration.hasMoreElements()) {
                    String propName = (String) enumeration.nextElement();
                    messages.append("\n        |_ gatherer control [" + propName + "] : " + gathererControl[i].getProperty(propName));
                }
                if (this.statGatherConfiguration2DArray[i] != null) {
                    for (int j = 0; j < this.statGatherConfiguration2DArray[i].length; j++) {
                        messages.append("\n        |_ statGatherer config : " + this.statGatherConfiguration2DArray[i][j].toString());
                    }
                }
                if (this.statGatherConfiguration2DArray[i] != null) {
                    for (int j = 0; j < this.statSummarizerConfiguration2DArray[i].length; j++) {
                        messages.append("\n        |_ statSummarizer config : " + this.statSummarizerConfiguration2DArray[i][j].toString());
                    }
                }

            }
            return messages.toString();
        } catch (Exception e) {
            logger.error("DumpConfigurationFilePath error");
            return "DumpConfigurationFilePath error";
        }
    }
}
