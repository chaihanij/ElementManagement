/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.statsummarizer.templates;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;
import vos1.superinnova.engine.statproccessor.predefinedengine.GeneralSuperInnovaStatEngine;
import vos1.superinnova.engine.statsummarizer.StatSummarizationCore;
import vos1.superinnova.engine.statsummarizer.StatSummarizationModule;
import vos1.superinnova.engine.statsummarizer.StatSummarizationResultSet;
import vos1.superinnova.engine.statsummarizer.StatSummarizationSmartResultSet;
import vos1.superinnova.engine.statsummarizer.StatSummarizerConfiguration;

/**
 *
 * @author HugeScreen
 */
public class SupernovaMultiSuccessRateSummarizationModule extends StatSummarizationModule {

    int[] metaData=null;
    String[] columnName=null;
    String[] unitType=null;
    int row=0;
    
    int categoriesQuantity = 0;
    
    String[][][] regexParam=null;
    String paramPrefix=null;
    public static final int REGEX_PARAM_ATTEMPT=0;
    public static final int REGEX_PARAM_SUCCESS=1;
    public static final int REGEX_PARAM_ERROR=2;
    public static final String[] REGEX_PARAM_NAME={"Attempt","Success","Error"};
    
    public static final int COL_SITE=0;
    public static final int COL_BLOCK=1;
    public static final int COL_SUBBLOCK=2;
    public static final int COL_DATE=3;
    public static final int COL_HOSTNAME=4;
    public static final int COL_STATNAME=5;
    public static final int COL_MINCOUNTER=6;
    public static final int COL_MAXCOUNTER=7;
    public static final int COL_AVERAGECOUNTER=8;
    public static final int COL_SUMCOUNTER=9;
    
    public static final int OUTPUT_COL_ATTEMPT=0;
    public static final int OUTPUT_COL_SUCCESS=1;
    public static final int OUTPUT_COL_ERROR=2;
    public static final int OUTPUT_COL_PERCENTSUCCESS=3;
    public static final int OUTPUT_COL_PERCENTERROR=4;    
    public int OUTPUT_COL_NODECOUNT=5; 
    //{"Attempt","Success","Error","%Success","%Error","NodeCount"
    public SupernovaMultiSuccessRateSummarizationModule(StatSummarizationCore statSummarizationCore,StatSummarizerConfiguration statSummarizerConfiguration){
        this.statSummarizationCore=statSummarizationCore;
        this.statSummarizerConfiguration=statSummarizerConfiguration;
        
        Properties siteProp=statSummarizationCore.getSuperInnovaStatProcessor().getSuperInnovaStatEnginePropertiesLookup().getCategory(GeneralSuperInnovaStatEngine.SITE_KEYWORD);
        Properties blockProp=statSummarizationCore.getSuperInnovaStatProcessor().getSuperInnovaStatEnginePropertiesLookup().getCategory(GeneralSuperInnovaStatEngine.BLOCK_KEYWORD);
        Properties subBlockProp=statSummarizationCore.getSuperInnovaStatProcessor().getSuperInnovaStatEnginePropertiesLookup().getCategory(GeneralSuperInnovaStatEngine.SUBBLOCK_KEYWORD);
        
        this.row=siteProp.size()-1+blockProp.size()-1+subBlockProp.size()-1;
        //System.out.println("[DEBUG] : this.row="+this.row);
        //System.out.println("[DEBUG] : Hello World New");
        for(int i=1;i<100;i++){
            String tmpAttemptColumnName="param_attempt_"+String.format("%02d",i);
            String tmpAttemptColumnNameValue=statSummarizerConfiguration.getAdditionalProperties().getProperty(tmpAttemptColumnName);
            if(tmpAttemptColumnNameValue!=null && tmpAttemptColumnNameValue.length()>0){
                categoriesQuantity++;
            }
            else{
                if(categoriesQuantity<=0){
                    System.out.println("[DEBUG] : categoriesQuantity : "+categoriesQuantity);
                    System.out.println("[DEBUG] : categoriesQuantity is less than zero");
                    System.out.println("[DEBUG] : END");
                }
                break;
            }
        }
        //System.out.println("[DEBUG] : categoriesQuantity : "+categoriesQuantity);
  
        if(categoriesQuantity <=0 ){
            System.out.println("[ERROR] : categoriesQuantity less than 1");
            return;
        }
        else{
            int slotQuantity=categoriesQuantity*5+1;
            //System.out.println("[DEBUG] : slotQuantity : "+slotQuantity);
            this.OUTPUT_COL_NODECOUNT=slotQuantity-1;
            columnName = new String[slotQuantity];
            metaData = new int[slotQuantity];
            unitType = new String[slotQuantity]; 
            
            columnName[slotQuantity-1]="NodeCount";
            metaData[slotQuantity-1]=StatSummarizationResultSet.TYPE_INT;
            unitType[slotQuantity-1]="Server";
            
            
                    
        //columnName = new String[]{"Attempt","Success","Error","%Success","%Error","NodeCount"};
        //metaData = new int[]{StatSummarizationResultSet.TYPE_LONG,StatSummarizationResultSet.TYPE_LONG,StatSummarizationResultSet.TYPE_LONG,StatSummarizationResultSet.TYPE_FLOAT,StatSummarizationResultSet.TYPE_FLOAT,StatSummarizationResultSet.TYPE_INT};
        //unitType = new String[]{"Transaction","Transaction","Transaction","%","%","Server"};            
        }
        
        
        //statSummarizationResultSet = new StatSummarizationResultSet(row,metaData,columnName,unitType);
        
        regexParam = new String[categoriesQuantity][3][];
        for(int i=0;i<categoriesQuantity;i++){
            String i_2dformat=String.format("%02d",(i+1));
            //System.out.println("[DEBUG] : i_2dformat : "+i_2dformat);
            this.regexParam[i][REGEX_PARAM_ATTEMPT]=statSummarizerConfiguration.getAdditionalProperties().getProperty("param_attempt_"+i_2dformat).split("\\|");
            this.regexParam[i][REGEX_PARAM_SUCCESS]=statSummarizerConfiguration.getAdditionalProperties().getProperty("param_success_"+i_2dformat).split("\\|");
            this.regexParam[i][REGEX_PARAM_ERROR]=statSummarizerConfiguration.getAdditionalProperties().getProperty("param_error_"+i_2dformat).split("\\|");
            this.paramPrefix=statSummarizerConfiguration.getAdditionalProperties().getProperty("param_prefix_"+i_2dformat);
            if(this.paramPrefix==null || this.paramPrefix.length()==0){
                this.paramPrefix="";
            }
            else{
                this.paramPrefix=this.paramPrefix+".";
            }
            
            int slotID=5*i;
            //System.out.println("[DEBUG] Init slotID : "+slotID);
            columnName[slotID+0]=this.paramPrefix+"Attempt";
            columnName[slotID+1]=this.paramPrefix+"Success";
            columnName[slotID+2]=this.paramPrefix+"Error";
            columnName[slotID+3]=this.paramPrefix+"%Success";
            columnName[slotID+4]=this.paramPrefix+"%Error";
            
            metaData[slotID+0]=StatSummarizationResultSet.TYPE_LONG;
            metaData[slotID+1]=StatSummarizationResultSet.TYPE_LONG;
            metaData[slotID+2]=StatSummarizationResultSet.TYPE_LONG;
            metaData[slotID+3]=StatSummarizationResultSet.TYPE_FLOAT;
            metaData[slotID+4]=StatSummarizationResultSet.TYPE_FLOAT;

            unitType[slotID+0]="Transaction";
            unitType[slotID+1]="Transaction";
            unitType[slotID+2]="Transaction";
            unitType[slotID+3]="%";
            unitType[slotID+4]="%";
        //columnName = new String[]{"Attempt","Success","Error","%Success","%Error","NodeCount"};
        //metaData = new int[]{StatSummarizationResultSet.TYPE_LONG,StatSummarizationResultSet.TYPE_LONG,StatSummarizationResultSet.TYPE_LONG,StatSummarizationResultSet.TYPE_FLOAT,StatSummarizationResultSet.TYPE_FLOAT,StatSummarizationResultSet.TYPE_INT};
        //unitType = new String[]{"Transaction","Transaction","Transaction","%","%","Server"};                   
            }
            //System.out.println("[DEBUG] : Init MultiSuccessRate Success");
        }
        


    @Override
    public ResultSet fetchDataFromStorage() {
        String rawTableName="raw_"+this.getStatSummarizationCore().getSuperInnovaStatProcessor().lookupKeyValue("ENGINE", "StorageName");
        String selectSQL="select * from "+rawTableName;
        ResultSet selectResultSet = this.statSummarizationCore.getSuperInnovaStatProcessor().queryDatabse(selectSQL);
        return selectResultSet;
    }

    @Override
    public void summarizeData(ResultSet resultSet) {
        if(resultSet!=null){
            try{
                //HSQLDBManager.dump(resultSet);
                if(categoriesQuantity>0){
                    summarizeResultSet(resultSet);
                }
                else{
                    System.out.println("[WARNING] multiSuccessRate categoriesQuantity is less than 0, Do Nothing");
                }
                //this.statSummarizationSmartResultSet.dumpDataSet();
            }
            catch(Exception e){
                e.printStackTrace();
            }
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
    
    @Override
    public String toString(){
        return this.statSummarizerConfiguration.getStatName()+" : "+this.statSummarizerConfiguration.getSummarizationModule();
    }
    
    public void summarizeResultSet(ResultSet resultSet){
        StatSummarizationSmartResultSet tmpStatSummarizationSmartResultSet=null;
        tmpStatSummarizationSmartResultSet = new StatSummarizationSmartResultSet(this.statSummarizationCore,this.row,this.metaData,this.columnName,this.unitType);
        
        try{
            for (; resultSet.next(); ) {
                // Check Date
                Timestamp dateTimeStamp=resultSet.getTimestamp(1+SupernovaMultiSuccessRateSummarizationModule.COL_DATE);
                if(tmpStatSummarizationSmartResultSet.getMaxDate()==null){
                    tmpStatSummarizationSmartResultSet.setMinDate(dateTimeStamp);
                    tmpStatSummarizationSmartResultSet.setMaxDate(dateTimeStamp);
                }
                else{
                    if(tmpStatSummarizationSmartResultSet.getMaxDate().compareTo(dateTimeStamp)<0){
                        tmpStatSummarizationSmartResultSet.setMinDate(tmpStatSummarizationSmartResultSet.getMaxDate());
                        tmpStatSummarizationSmartResultSet.setMaxDate(dateTimeStamp);
                    }
                }
                
                
                
                
                // Check Regex
                String statName=(String)resultSet.getObject(1+SupernovaMultiSuccessRateSummarizationModule.COL_STATNAME);
                //System.out.println("StatName : "+statName);
                boolean foundMatchesRegex=false;
                for(int h=0;h<this.categoriesQuantity;h++){
                    for(int i=0;i<this.regexParam[h].length;i++){

                        for(int j=0;j<this.regexParam[h][i].length;j++){
                            //System.out.println(i+", "+j+", StatName : "+statName+", Regex : "+this.regexParam[i][j]);
                            if(statName.matches(this.regexParam[h][i][j])==true){
                                //System.out.println(statName+", "+REGEX_PARAM_NAME[i]);
                                int site=(Integer)resultSet.getObject(1+SupernovaMultiSuccessRateSummarizationModule.COL_SITE);
                                int block=(Integer)resultSet.getObject(1+SupernovaMultiSuccessRateSummarizationModule.COL_BLOCK);
                                int subBlock=(Integer)resultSet.getObject(1+SupernovaMultiSuccessRateSummarizationModule.COL_SUBBLOCK);
                                // In This Situation columnNumber is I
                                int columnNumber=-1;
                                int slotID=5*h;
                                if(i==REGEX_PARAM_ATTEMPT){
                                    columnNumber=slotID+SupernovaMultiSuccessRateSummarizationModule.OUTPUT_COL_ATTEMPT;
                                }
                                else if (i==REGEX_PARAM_SUCCESS){
                                    columnNumber=slotID+SupernovaMultiSuccessRateSummarizationModule.OUTPUT_COL_SUCCESS;
                                }
                                else if (i==REGEX_PARAM_ERROR){
                                    columnNumber=slotID+SupernovaMultiSuccessRateSummarizationModule.OUTPUT_COL_ERROR;
                                }
                                int operation=StatSummarizationSmartResultSet.OPERATION_ADD;
                                //resultSet Start with 1, So we need to add 1 to Column Position
                                Object obj=resultSet.getObject(1+SupernovaMultiSuccessRateSummarizationModule.COL_SUMCOUNTER);
                                //System.out.println("putObject : "+site+","+block+","+subBlock+","+this.REGEX_PARAM_NAME[columnNumber]+",ADD,"+obj);
                                if(obj!=null){
                                    tmpStatSummarizationSmartResultSet.putObject(site, block, subBlock, columnNumber, operation, obj);
                                }
                                else{
                                    //System.out.println("[DEBUG] Skip Null Object : "+site+","+block+","+subBlock+","+columnNumber+","+operation+","+obj);
                                }
                                foundMatchesRegex=true;
                                break;
                            }
                        }
                        if(foundMatchesRegex==true){
                            break;
                        }
                    } // End Loop i
                    if(foundMatchesRegex==true){
                        break;
                    }
                }// End Loop H
            }// End For #1
            //System.out.println("EndLoop First");
            /*
                 public static final int OUTPUT_COL_ATTEMPT=0;
    public static final int OUTPUT_COL_SUCCESS=1;
    public static final int OUTPUT_COL_ERROR=2;
    public static final int OUTPUT_COL_PERCENTSUCCESS=3;
    public static final int OUTPUT_COL_PERCENTERROR=4;    
    public static final int OUTPUT_COL_NODECOUNT=5; 
             */
            //System.out.println("[DEBUG] rowCounter size : "+tmpStatSummarizationSmartResultSet.getRowCounter());
            //tmpStatSummarizationSmartResultSet.dumpDataSet();
            for(int i=0;i<tmpStatSummarizationSmartResultSet.getRowCounter();i++){
                
                for(int h=0;h<this.categoriesQuantity;h++){
                    
                    int slotID=h*5;
                    //System.out.println("[DEBUG] Categories : "+h);
                    //System.out.println("[DEBUG] SlotID : "+slotID);
                    //System.out.println("[DEBUG] #Attempt : "+(Long)tmpStatSummarizationSmartResultSet.getObject(i, slotID+OUTPUT_COL_ATTEMPT));
                    //System.out.println("[DEBUG] #Success : "+(Long)tmpStatSummarizationSmartResultSet.getObject(i, slotID+OUTPUT_COL_SUCCESS));
                    //System.out.println("[DEBUG] #Error : "+(Long)tmpStatSummarizationSmartResultSet.getObject(i, slotID+OUTPUT_COL_ERROR));
                    
                    Long attempt=(Long)tmpStatSummarizationSmartResultSet.getObject(i, slotID+OUTPUT_COL_ATTEMPT);
                    Long success=(Long)tmpStatSummarizationSmartResultSet.getObject(i, slotID+OUTPUT_COL_SUCCESS);
                    Long error=(Long)tmpStatSummarizationSmartResultSet.getObject(i, slotID+OUTPUT_COL_ERROR);
                    Float successRate=0f;
                    Float errorRate=0f;
                
                
                
                //System.out.println("----");
                //System.out.println("[DEBUG] row : "+i+", SlotID : "+slotID+", Attempt Slot : "+(slotID+OUTPUT_COL_ATTEMPT));
                //System.out.println("[DEBUG] row : "+i+", SlotID : "+slotID+", Success Slot : "+(slotID+OUTPUT_COL_SUCCESS));
                //System.out.println("[DEBUG] row : "+i+", SlotID : "+slotID+", Error Slot: "+(slotID+OUTPUT_COL_ERROR));
                //System.out.println("[DEBUG] : Attempt_value: "+attempt+", success_value : "+success+", error_value : "+error);
                
                //tmpStatSummarizationSmartResultSet.dumpToString();
                if(attempt!=null && attempt>=0){
                    successRate=(float)success/(float)attempt*100f;
                    
                    //errorRate=(float)((float)error/attempt*100);
                    if( successRate>100 ){
                        successRate=100f;
                    }
                    errorRate=100f-successRate;       
                }
                //System.out.println("[DEBUG] : successRate : "+successRate+", errorRate : "+errorRate);
               // System.out.println("[DEBUG] row : "+i+", SlotID : "+slotID+", %Success Slot : "+(slotID+OUTPUT_COL_PERCENTSUCCESS));
                //System.out.println("[DEBUG] row : "+i+", SlotID : "+slotID+", %Error Slot : "+(slotID+OUTPUT_COL_PERCENTERROR));
                //System.out.println("----");
                
                tmpStatSummarizationSmartResultSet.putObject(i, slotID+OUTPUT_COL_PERCENTSUCCESS, successRate);
                tmpStatSummarizationSmartResultSet.putObject(i, slotID+OUTPUT_COL_PERCENTERROR, errorRate);
                
                // Determine Host Counter
                Integer hostCounter=-1;
                Integer[] siteBlockSubBlockArray = tmpStatSummarizationSmartResultSet.getSiteBlockSubBlockMappingFromRowNumber(i);
                for(int j=0;j<siteBlockSubBlockArray.length;j++){
                    if(siteBlockSubBlockArray[j]!=null){
                        hostCounter=tmpStatSummarizationSmartResultSet.getHostListProp(j, siteBlockSubBlockArray[j]).size();
                        
                        /*
                        String[] cn = new String[]{"site","block","subblock"};
                        System.out.println("i : "+i+", j : "+j+", ["+cn[j]+"], hostcounter : "+hostCounter);
                        Enumeration<Object> e = tmpStatSummarizationSmartResultSet.getHostListProp(j, siteBlockSubBlockArray[j]).elements();
                        while(e.hasMoreElements()){
                            System.out.println(""+e.nextElement());
                        }
                        */

                                /*
                        if(j==StatSummarizationSmartResultSet.MAP_SITE){
                            hostCounter=tmpStatSummarizationSmartResultSet.getHostListProp(j, siteBlockSubBlockArray[j]).size();
                        }
                        else if(j==StatSummarizationSmartResultSet.MAP_BLOCK){
                            hostCounter=tmpStatSummarizationSmartResultSet.getHostListProp(j, siteBlockSubBlockArray[j]).size();
                        }
                        else if(j==StatSummarizationSmartResultSet.MAP_SUBBLOCK){
                            hostCounter=tmpStatSummarizationSmartResultSet.getHostListProp(j, siteBlockSubBlockArray[j]).size();
                        }
                        */
                    }
                } // END LOOP J
                //System.out.println("putObject: row="+i+", columne="+OUTPUT_COL_NODECOUNT+", hostCounter="+hostCounter);
                tmpStatSummarizationSmartResultSet.putObject(i, OUTPUT_COL_NODECOUNT, hostCounter);
                
            }// End Loop H
            }// End Loop I
            this.statSummarizationSmartResultSet=tmpStatSummarizationSmartResultSet;
        }
        catch(Exception e){
            this.statSummarizationSmartResultSet=null;
            e.printStackTrace();
        }
    }
    
}
