/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.statproccessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.Enumeration;
import java.util.Properties;
import org.apache.log4j.Logger;
import vos1.superinnova.engine.statproccessor.statgathermodule.StatGatherConfiguration;
import vos1.superinnova.engine.statsummarizer.StatSummarizerConfiguration;

/**
 *
 * @author HugeScreen
 */
public class SuperInnovaStatCoreConfigurator {
    
    final static Logger logger = Logger.getLogger(SuperInnovaStatCoreConfigurator.class);
    
    File rootConfigurationPath=null;
    
    static final String fileSeparator=System.getProperty("file.separator");
    
    
    File engineConfigurationPath=null;
    File summarizerConfigurationPath=null;
    int engineConfigurationCounter=0;
    File[] engineConfiguration=null;
    static final int MAXIMUM_ENGINE=2048;
    
    static final String MAIN_CONFIGURATION="mainConfiguration.conf";
    static final String ENGINE_CONFIGURATION_DIRECTORY="engineConfiguration";
    static final String ENGINE_CONFIGURATION_FILENAME="engine.conf";
    static final String GATHERERCONFIGURATION_DIRECTORY="gathererConfiguration";
    static final String GATHERERCONTROL_FILENAME="gathererControl.conf";
    static final String GATHERERTARGET_FILENAME="gathererTarget.conf";
    static final String SUMMARIZERCONFIGURATION_DIRECTORY="summarizerConfiguration";
    
    Properties engineCoreConfiguration=null;
    Properties engineList=null;
    String[] engineNameList=null;
    Properties[] gathererControl=null;
    StatGatherConfiguration[][] statGatherConfiguration2DArray=null;
    StatSummarizerConfiguration[][] statSummarizerConfiguration2DArray=null;
    SuperInnovaStatEngineConfiguration[] superInnovaStatEngineConfiguration2DArray=null;
    
    public SuperInnovaStatCoreConfigurator(String rootConfigurationPath){
        
        engineCoreConfiguration = new Properties();
        engineList = new Properties();
        engineNameList=new String[SuperInnovaStatCoreConfigurator.MAXIMUM_ENGINE];
        gathererControl=new Properties[SuperInnovaStatCoreConfigurator.MAXIMUM_ENGINE];
        this.rootConfigurationPath=new File(rootConfigurationPath);
        this.engineConfiguration = new File[SuperInnovaStatCoreConfigurator.MAXIMUM_ENGINE];
        this.statGatherConfiguration2DArray = new StatGatherConfiguration[SuperInnovaStatCoreConfigurator.MAXIMUM_ENGINE][];
        this.statSummarizerConfiguration2DArray= new StatSummarizerConfiguration[SuperInnovaStatCoreConfigurator.MAXIMUM_ENGINE][];
        this.superInnovaStatEngineConfiguration2DArray= new SuperInnovaStatEngineConfiguration[SuperInnovaStatCoreConfigurator.MAXIMUM_ENGINE];
        
        try{
            
            this.engineCoreConfiguration.load(new FileReader(new File(this.rootConfigurationPath.getCanonicalPath()+SuperInnovaStatCoreConfigurator.fileSeparator+MAIN_CONFIGURATION)));
            this.engineConfigurationPath=new File(this.rootConfigurationPath.getCanonicalPath()+SuperInnovaStatCoreConfigurator.fileSeparator+SuperInnovaStatCoreConfigurator.ENGINE_CONFIGURATION_DIRECTORY);
            if(this.engineConfigurationPath.isDirectory()==true){
                File[] fileUnderEngineConfigurationPath = this.engineConfigurationPath.listFiles();
                for (int i=0;i<fileUnderEngineConfigurationPath.length;i++){
                    if(fileUnderEngineConfigurationPath[i].isDirectory()){
                        engineConfiguration[engineConfigurationCounter]=fileUnderEngineConfigurationPath[i];
                        Properties prop= new Properties();
                        prop.load(new FileReader(engineConfiguration[engineConfigurationCounter].getCanonicalPath()+SuperInnovaStatCoreConfigurator.fileSeparator+SuperInnovaStatCoreConfigurator.ENGINE_CONFIGURATION_FILENAME));
                        engineList.setProperty(prop.getProperty("ENGINE_NAME"),String.valueOf(new Integer(engineConfigurationCounter)));
                        engineNameList[engineConfigurationCounter]=prop.getProperty("ENGINE_NAME");
                        String engineType=prop.getProperty("ENGINE_TYPE");
                        this.superInnovaStatEngineConfiguration2DArray[engineConfigurationCounter] = new SuperInnovaStatEngineConfiguration(engineNameList[engineConfigurationCounter],engineType);
                        
                        
                        // Gatherer Control
                        gathererControl[engineConfigurationCounter]=new Properties();
                        gathererControl[engineConfigurationCounter].load(new FileReader(engineConfiguration[engineConfigurationCounter].getCanonicalPath()+SuperInnovaStatCoreConfigurator.fileSeparator+SuperInnovaStatCoreConfigurator.GATHERERCONFIGURATION_DIRECTORY+SuperInnovaStatCoreConfigurator.fileSeparator+GATHERERCONTROL_FILENAME));
                        
                        // Gatherer Target
                        int usableRow=0;
                        // FirstWalk
                        BufferedReader bufferedReader=null;
                        try{
                            bufferedReader = new BufferedReader(new FileReader(engineConfiguration[engineConfigurationCounter].getCanonicalPath()+SuperInnovaStatCoreConfigurator.fileSeparator+SuperInnovaStatCoreConfigurator.GATHERERCONFIGURATION_DIRECTORY+SuperInnovaStatCoreConfigurator.fileSeparator+GATHERERTARGET_FILENAME));
                            String line=null;
                            while( (line=bufferedReader.readLine()) != null){
                                if(line.startsWith("#")==false){
                                    
                                    usableRow++;
                                }
                            }
                        }
                        catch(Exception e){
                            e.printStackTrace();
                            logger.error("Configuration error :" + engineConfiguration[engineConfigurationCounter].getCanonicalPath()+SuperInnovaStatCoreConfigurator.fileSeparator+SuperInnovaStatCoreConfigurator.ENGINE_CONFIGURATION_FILENAME);
                        }
                        finally{
                            try{
                                bufferedReader.close();
                            }
                            catch(Exception e){
                                logger.error("Configuration error :" + engineConfiguration[engineConfigurationCounter].getCanonicalPath()+SuperInnovaStatCoreConfigurator.fileSeparator+SuperInnovaStatCoreConfigurator.ENGINE_CONFIGURATION_FILENAME);
                            };
                        }
                        // Init statGathererConfigurationCounter UsableRow
                        if(usableRow>0){
                            this.statGatherConfiguration2DArray[engineConfigurationCounter] = new StatGatherConfiguration[usableRow];
                        }
                        // Second Walk
                        bufferedReader=null;
                        int lineCounter=0;
                        try{
                            bufferedReader = new BufferedReader(new FileReader(engineConfiguration[engineConfigurationCounter].getCanonicalPath()+SuperInnovaStatCoreConfigurator.fileSeparator+SuperInnovaStatCoreConfigurator.GATHERERCONFIGURATION_DIRECTORY+SuperInnovaStatCoreConfigurator.fileSeparator+GATHERERTARGET_FILENAME));
                            String line=null;
                            while( (line=bufferedReader.readLine()) != null){
                                if(line.startsWith("#")==false){
                                    String[] lineToken = line.split("\\|");
                                    
                                    String site=lineToken[0];
                                    String block=lineToken[1];
                                    String subBlock=lineToken[2];
                                    String url=lineToken[3];
                                    int fetchInterval=Integer.parseInt(gathererControl[engineConfigurationCounter].getProperty("fetchInterval"));
                                    int fetchType=fetchTypeToInt(gathererControl[engineConfigurationCounter].getProperty("fetchType"));
                                    int fetchTimeout=Integer.parseInt(gathererControl[engineConfigurationCounter].getProperty("fetchTimeout"));
                                    String storageName=gathererControl[engineConfigurationCounter].getProperty("storageName");
                                    String fetchParser=gathererControl[engineConfigurationCounter].getProperty("fetchParser");
                                    int workerPriority=workerPriorityToInt(gathererControl[engineConfigurationCounter].getProperty("workerPriority"));
                                   
                                    
                                    //statGatherConfiguartionArray[i] = new StatGatherConfiguration(StatGatherConfiguration.FETCHTYPE_HTTP,"CWDC","VIP-1",hostList[i],"http://localhost:9016/equinoxStat?nodeType=OCF&hostname="+hostList[i],"OCF","SuperNovaStatParser",0,3);
                                    this.statGatherConfiguration2DArray[engineConfigurationCounter][lineCounter] = new StatGatherConfiguration(fetchType,site,block,subBlock,url,storageName,fetchParser,workerPriority,fetchInterval,fetchTimeout);
                                    
                                    lineCounter++;
                                }
                            }
                        }
                        catch(Exception e){
                            e.printStackTrace();
                            logger.error("Configuration error :" + engineConfiguration[engineConfigurationCounter].getCanonicalPath()+SuperInnovaStatCoreConfigurator.fileSeparator+SuperInnovaStatCoreConfigurator.GATHERERCONFIGURATION_DIRECTORY+SuperInnovaStatCoreConfigurator.fileSeparator+GATHERERTARGET_FILENAME);

                        }
                        finally{
                            try{
                                bufferedReader.close();
                            }
                            catch(Exception e)
                            {
                                logger.error("Configuration error :" + engineConfiguration[engineConfigurationCounter].getCanonicalPath()+SuperInnovaStatCoreConfigurator.fileSeparator+SuperInnovaStatCoreConfigurator.GATHERERCONFIGURATION_DIRECTORY+SuperInnovaStatCoreConfigurator.fileSeparator+GATHERERTARGET_FILENAME);
                            };
                        }
                        
                        
                        
                        // == BEGIN StatSummarizer Directory ========================================================
                        this.summarizerConfigurationPath=new File(engineConfiguration[engineConfigurationCounter].getCanonicalPath()+SuperInnovaStatCoreConfigurator.fileSeparator+SuperInnovaStatCoreConfigurator.SUMMARIZERCONFIGURATION_DIRECTORY);
                        //System.out.println(summarizerConfigurationPath.getCanonicalPath());
                        if(this.summarizerConfigurationPath.isDirectory()==true){

                            File[] fileUnderStatSummarizerConfigurationPath = this.summarizerConfigurationPath.listFiles(
                                new FilenameFilter() {
                                    @Override
                                    public boolean accept(File dir, String name) {
                                        return name.toLowerCase().endsWith(".conf");
                                    }
                                }                    
                            );
                            //System.out.println(fileUnderStatSummarizerConfigurationPath.length);
                            this.statSummarizerConfiguration2DArray[engineConfigurationCounter]= new StatSummarizerConfiguration[fileUnderStatSummarizerConfigurationPath.length];
                            for (int j=0;j<fileUnderStatSummarizerConfigurationPath.length;j++){
                                this.statSummarizerConfiguration2DArray[engineConfigurationCounter][j] = StatSummarizerConfiguration.makeStatSummarizerConfiguration(fileUnderStatSummarizerConfigurationPath[j].getCanonicalPath());
                                //System.out.println(fileUnderStatSummarizerConfigurationPath[j].getCanonicalPath());
                            }// End for fileUnderStatSummarizerConfigurationPath
                        }// End StaSummarizer Directory
                        // ============= Finish Stat Summarizer Directory ===========================================
                        
                        engineConfigurationCounter++;
                    }// End if(Directory)
                }// End For Loop
            }// End EngineConfiguration Directory 
            

            
        }//End Try
        catch(Exception e){
            e.printStackTrace();
            logger.fatal("Error Configruration");
        }
        
        
    }
    
    private int fetchTypeToInt(String fetchType){
        if(fetchType.compareToIgnoreCase("http")==0){
            return StatGatherConfiguration.FETCHTYPE_HTTP;
        }
        return -1;
    }
    private int workerPriorityToInt(String workerPriority){
        if(workerPriority.compareToIgnoreCase("normal")==0){
            return StatGatherConfiguration.PRIORITY_NORMAL;
        }
        return -1;
    }    
    public int getEngineConfigurationCounter(){
        return this.engineConfigurationCounter;
    }
    public String getEngineName(int i){
        return this.engineNameList[i];
    }
    public StatGatherConfiguration[] getStatConfigurationArray(int i){
        return this.statGatherConfiguration2DArray[i];
    }
    public StatSummarizerConfiguration[] getStatSummarizerConfigurationArray(int i){
        return this.statSummarizerConfiguration2DArray[i];
    }    
    public SuperInnovaStatEngineConfiguration getSuperInnovaStatEngineConfigurationArray(int i){
        return this.superInnovaStatEngineConfiguration2DArray[i];
    }
    public Properties getEngineCoreConfiguration() {
        return engineCoreConfiguration;
    }
    
    public void dumpConfigurationFilePath(){
        try{
            System.out.println("root path : "+this.rootConfigurationPath.getCanonicalPath());
            System.out.println("root engineConfiguration path : "+this.engineConfigurationPath.getCanonicalPath());
            for(int i=0;i<engineConfigurationCounter;i++){
                System.out.println("  |_ engineName : "+this.engineNameList[i]);
                System.out.println("  |_ engineConfiguration : "+this.superInnovaStatEngineConfiguration2DArray[i]);
                System.out.println("     |_ engineConfiguration path : "+this.engineConfiguration[i].getCanonicalPath());
                Enumeration enumeration =  gathererControl[i].propertyNames();
                while(enumeration.hasMoreElements()){
                    String propName=(String)enumeration.nextElement();
                    System.out.println("        |_ gatherer control ["+propName+"] : "+gathererControl[i].getProperty(propName));
                }
                if(this.statGatherConfiguration2DArray[i]!=null){
                    for(int j=0;j<this.statGatherConfiguration2DArray[i].length;j++){
                        System.out.println("        |_ statGatherer config : "+this.statGatherConfiguration2DArray[i][j].toString());
                    }
                }
                if(this.statGatherConfiguration2DArray[i]!=null){
                    for(int j=0;j<this.statSummarizerConfiguration2DArray[i].length;j++){
                        System.out.println("        |_ statSummarizer config : "+this.statSummarizerConfiguration2DArray[i][j].toString());
                    }
                }
                
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args){
        System.out.println("Hello World");
        SuperInnovaStatCoreConfigurator superInnovaStatCoreConfigurator = new SuperInnovaStatCoreConfigurator("D:\\StatConfigurationDirectory");
        superInnovaStatCoreConfigurator.dumpConfigurationFilePath();
    }
}
