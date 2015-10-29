/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.statproccessor.predefinedtype;

import java.sql.ResultSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import vos1.superinnova.engine.statproccessor.SuperInnovaStatEnginePropertiesLookup;
import vos1.superinnova.engine.statproccessor.SuperInnovaStatProcessor;
import vos1.superinnova.engine.statproccessor.predefinedengine.GeneralSuperInnovaStatEngine;
import vos1.superinnova.engine.statproccessor.statgathermodule.HSQLDBManager;
import vos1.superinnova.engine.statproccessor.statgathermodule.StatGathererExecutor;
import vos1.superinnova.engine.statproccessor.statgathermodule.statparser.predefined.SuperInnovaStatParser;
import vos1.superinnova.engine.statproccessor.statgathermodule.util.TimeUtil;
import vos1.superinnova.engine.statsummarizer.StatSummarizationCore;
import vos1.superinnova.engine.statsummarizer.StatSummarizationModule;
import vos1.superinnova.engine.statsummarizer.StatSummarizerConfiguration;

/**
 *
 * @author HugeScreen
 */
public class GeneralSuperNovaStatProcessor extends SuperInnovaStatProcessor{
    

    
    String storageType="mem";
    
    // Stat Gatherer
    StatGathererExecutor statGathererExecutor=null;
    GeneralSuperInnovaStatEngine ocfSuperInnovaStatEngine=null;

    
    
    public GeneralSuperNovaStatProcessor(GeneralSuperInnovaStatEngine ocfSuperInnovaStatEngine){
        this.storageName=ocfSuperInnovaStatEngine.getSuperInnovaStatEngineConfiguration().getEngineName();
        ocfSuperInnovaStatEngine.put("ENGINE", "StorageName", storageName);
        this.statGathererExecutor = new StatGathererExecutor(this);
        this.statGathererParser = new SuperInnovaStatParser(this.statGathererExecutor);
        this.statGatherConfiguartionArray=ocfSuperInnovaStatEngine.getStatGatherConfiguration();
        this.ocfSuperInnovaStatEngine = ocfSuperInnovaStatEngine;
        this.superInnovaStatEngine=ocfSuperInnovaStatEngine;
        
        
        
        
        
        
        initSuperInnovaStatDatabase();
        this.statSummarizationCore = new StatSummarizationCore(this);
    }  
    

    
    @Override
    public SuperInnovaStatEnginePropertiesLookup getSuperInnovaStatEnginePropertiesLookup(){
        return this.ocfSuperInnovaStatEngine.getSuperInnovaStatEnginePropertiesLookup();
    }
    
    @Override
    public Object lookupKeyValue(String category, Object key){
        return this.ocfSuperInnovaStatEngine.getSuperInnovaStatEnginePropertiesLookup().get(category, key);
    }
    
    public void initSuperInnovaStatDatabase(){
        dbConnection = new HSQLDBManager();
        dbConnection.connect(this.storageType, this.storageName);
        
        try{
            dbConnection.update("SET AUTOCOMMIT TRUE");
            dbConnection.update("SET DATABASE SQL LONGVAR IS LOB TRUE");
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        try{
            String[] createRawTableSQL=this.statGathererParser.getCreateRawTableSQL(this.ocfSuperInnovaStatEngine.getSuperInnovaStatEnginePropertiesLookup());
            if(createRawTableSQL!=null && createRawTableSQL.length>0){
                for(int i=0;i<createRawTableSQL.length;i++){
                    if(createRawTableSQL!=null){
                        dbConnection.update(createRawTableSQL[i]);
                    }
                }
                
            }
            
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        
    }
    public SuperInnovaStatEnginePropertiesLookup getStatEnginePropertiesLookup(){
        return this.getSuperInnovaStatEnginePropertiesLookup();
    }
    @Override
    public void run(){
        if(this.statGatherConfiguartionArray!=null && this.statGatherConfiguartionArray[0]!=null){
            int fetchInterval=this.statGatherConfiguartionArray[0].getFetchInterval();
            if(fetchInterval > 0){
                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                ScheduledFuture<?> schHandler = scheduler.scheduleAtFixedRate(statGathererExecutor, TimeUtil.calculateDelayInMillisecToNextLaunch(fetchInterval), fetchInterval*1000, TimeUnit.MILLISECONDS);
            }
            else{
                System.out.println("Error : FetchInterval is Zero");
            }
        }
        else{
            System.out.println("Error : StatGathererConfiguration is null");
        }
    }
    
    
    @Override
    public void initDatabase(){
        initSuperInnovaStatDatabase();
    }
    @Override
    public int updateDatabase(String sql){
        //System.out.println("update SQL : "+sql);
        try{
            this.dbConnection.update(sql);
        }
        catch(Exception e){
            System.err.println("[Exception] SQL : "+sql);
            //e.printStackTrace();
            return -1;
        }
        return 0;
    }
    @Override
    public ResultSet queryDatabse(String sql){
        try{
            return this.dbConnection.query(sql);
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public void beginStatSummarizationProcess(){
        System.out.println("**==**==** BEGINE : STAT SUMMARIZATION PROCESS ==**==**==**==");
        this.statSummarizationCore.invokeStatSummarizationProcess();
    }
    
    
    public static void main(String[] args){
        /*
        System.out.println("Hello World");
        StatGatherConfiguration[] statGatherConfiguartionArray;
            String[] hostList = new String[]{"OCF201","OCF202","OCF203","OCF204"};     
            statGatherConfiguartionArray = new StatGatherConfiguration[hostList.length];
            for(int i=0;i<hostList.length;i++){
                statGatherConfiguartionArray[i] = new StatGatherConfiguration(StatGatherConfiguration.FETCHTYPE_HTTP,"CWDC","VIP-1",hostList[i],"http://localhost:9016/equinoxStat?nodeType=OCF&hostname="+hostList[i],"OCF","SuperNovaStatParser",3);
            }
            
            
            SuperInnovaStatProcessor sitp = new OCFStatProcessor(statGatherConfiguartionArray);
            sitp.run();
            
        */
        //SuperInnovaStatProcessor sitp = new OCFStatProcessor();
    }
}
