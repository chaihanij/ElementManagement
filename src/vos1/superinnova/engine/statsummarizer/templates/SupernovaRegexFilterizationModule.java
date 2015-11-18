/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.statsummarizer.templates;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import vos1.superinnova.engine.statproccessor.predefinedengine.GeneralSuperInnovaStatEngine;
import vos1.superinnova.engine.statproccessor.statgathermodule.HSQLDBManager;
import vos1.superinnova.engine.statsummarizer.StatSummarizationCore;
import vos1.superinnova.engine.statsummarizer.StatSummarizationModule;
import vos1.superinnova.engine.statsummarizer.StatSummarizationResultSet;
import vos1.superinnova.engine.statsummarizer.StatSummarizationSmartResultSet;
import vos1.superinnova.engine.statsummarizer.StatSummarizerConfiguration;

/**
 *
 * @author HugeScreen
 */
public class SupernovaRegexFilterizationModule extends StatSummarizationModule{

    final static Logger logger = Logger.getLogger(SupernovaRegexFilterizationModule.class);

    int[] metaData=null;
    String[] columnName=null;
    String[] unitType=null;
    int[] divideBy=null;
    int aggregrateType=StatSummarizationSmartResultSet.OPERATION_ADD;
    int row=0;
    
    
    String[][] inputSRegexParam=null;
    /*
    public static final int REGEX_PARAM_ATTEMPT=0;
    public static final int REGEX_PARAM_SUCCESS=1;
    public static final int REGEX_PARAM_ERROR=2;
    public static final String[] REGEX_PARAM_NAME={"Attempt","Success","Error"};
    */
    
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
    
    
    
    
    int categorySize=0;
    
    
    public static int MAXIMUM_CATEGORY=96;
    
    public SupernovaRegexFilterizationModule(StatSummarizationCore statSummarizationCore,StatSummarizerConfiguration statSummarizerConfiguration){
        this.statSummarizationCore=statSummarizationCore;
        this.statSummarizerConfiguration=statSummarizerConfiguration;

        
        Properties siteProp=statSummarizationCore.getSuperInnovaStatProcessor().getSuperInnovaStatEnginePropertiesLookup().getCategory(GeneralSuperInnovaStatEngine.SITE_KEYWORD);
        Properties blockProp=statSummarizationCore.getSuperInnovaStatProcessor().getSuperInnovaStatEnginePropertiesLookup().getCategory(GeneralSuperInnovaStatEngine.BLOCK_KEYWORD);
        Properties subBlockProp=statSummarizationCore.getSuperInnovaStatProcessor().getSuperInnovaStatEnginePropertiesLookup().getCategory(GeneralSuperInnovaStatEngine.SUBBLOCK_KEYWORD);


        this.row=siteProp.size()-1+blockProp.size()-1+subBlockProp.size()-1;
        
       
        
        // Count Category Size
        for(int i=0;i<SupernovaRegexFilterizationModule.MAXIMUM_CATEGORY;i++){
            String runningNumber=String.format("%02d", i+1);
//                StringBuilder _category = new StringBuilder();
//                _category.append("param_category_" + runningNumber + "_vartype");
//                System.out.println(
//                "param_category_"+runningNumber+"_vartype"
//                        +","+ "param_category_"+runningNumber+"_unit"
//                        +","+ "param_category_"+runningNumber+"_detectFrom"
//                        );
//
            if(statSummarizerConfiguration.getAdditionalProperties().getProperty("param_category_"+runningNumber+"_vartype")!=null
               && statSummarizerConfiguration.getAdditionalProperties().getProperty("param_category_"+runningNumber+"_unit")!=null
               && statSummarizerConfiguration.getAdditionalProperties().getProperty("param_category_"+runningNumber+"_detectFrom")!=null
            ){
                this.categorySize++;
            }
            else{
                // If Next Token is not Found Then, Just Break the Loop
                break;
            }
        }
        
        // if categorySize < 1 Then Return & Print Error Log
        if(this.categorySize<1){
            // System.out.println("Error : StatRegexFilterizationModule, categorySize is less than 1");
            logger.error("Error : StatRegexFilterizationModule, categorySize is less than 1");
            return;
        }
        
        // System.out.println("this.categorySize : "+this.categorySize);
        logger.info("Category size : " + this.categorySize);

        inputSRegexParam = new String[this.categorySize][];
        divideBy = new int[this.categorySize+1];
        for(int i=0;i<this.categorySize;i++){

            String runningNumber = String.format("%02d", i+1);
            String txt_param_detectFrom = statSummarizerConfiguration.getAdditionalProperties().getProperty("param_category_" + runningNumber + "_detectFrom");
            logger.debug("param_category_"+runningNumber+"_detectFrom="  + txt_param_detectFrom);
            if(txt_param_detectFrom.length()>0){
                inputSRegexParam[i] = txt_param_detectFrom.split("\\|");
                // If configuration was found, Then make 1 increment to category Size
            }
            else{
                logger.error("Error : StatRegexFilterizationModule, cannot parse "+txt_param_detectFrom);
//              System.out.println("Error : StatRegexFilterizationModule, cannot parse "+txt_param_detectFrom);
                return;
            }            
            
            //divide BY
            divideBy[i]=1;
            if(statSummarizerConfiguration.getAdditionalProperties().getProperty("param_category_"+runningNumber+"_divideBy")!=null){
                try{
                    divideBy[i]=Integer.parseInt(statSummarizerConfiguration.getAdditionalProperties().getProperty("param_category_"+runningNumber+"_divideBy"));
                }
                catch(Exception e){
                    divideBy[i]=1;
                    logger.warn("divideBy[" + i + "]" + "error and set value = 1");
                }
            }            
        }
        
        
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
//                HSQLDBManager.dump(resultSet);
                //getMatchFilterRegexColumn();
                summarizeResultSet(resultSet);
                //this.statSummarizationSmartResultSet.dumpDataSet();
            }
            catch(Exception e){
                logger.error(e);
            }
        }
    }

    Properties getMatchFilterRegexColumn(){
        
        Properties result_prop=null;
        
        String rawTableName="raw_"+this.getStatSummarizationCore().getSuperInnovaStatProcessor().lookupKeyValue("ENGINE", "StorageName");
        String selectSQL="select distinct statName from "+rawTableName;
        //System.out.println("SQL : "+selectSQL);
        ResultSet resultSet = this.statSummarizationCore.getSuperInnovaStatProcessor().queryDatabse(selectSQL);
        
        try{
            //System.out.println("row : "+resultSet.getRow());
            if(resultSet!=null){
                result_prop = new Properties();
                for (; resultSet.next(); ) {
                    
                    String statName=(String)resultSet.getObject(1);
                    //System.out.println("statName : "+statName);
                    for(int i=0;i<inputSRegexParam.length;i++){
                        boolean foundMatchedRegex=false;
                        if(inputSRegexParam[i]!=null){
                            for(int j=0;j<inputSRegexParam[i].length;j++){
                                //System.out.println("statName : "+statName+" ,Regex Param : "+this.inputSRegexParam[i][j]);
                                if(statName.matches(this.inputSRegexParam[i][j])==true){
                                    //System.out.println("************** matched statName : "+statName);
                                    result_prop.put(statName, i);
                                    foundMatchedRegex=true;
                                    break;
                                }// end if
                            }//end j
                            if(foundMatchedRegex==true){
                                break;
                            }
                        }// end if after loop i
                    }//end i
                    
                }// End ResultSet Loop
                try{
                    resultSet.close();
                }
                catch(Exception ex){
                    logger.error(ex);
                }
                //System.out.println("================");
            }// End If getRow>0
            else{
                logger.error("getMatchFIlterRegexColumn, resultSet is null");
            }

        }// End Try
        catch(Exception e){
            try{
                resultSet.close();
            }
            catch(Exception ex){
                logger.error(ex);
            }
            logger.error(e);
           return null;
        }
        
        return result_prop;
    }
    
    public void summarizeResultSet(ResultSet resultSet){
           
        Properties filterStatNameProp = getMatchFilterRegexColumn();
        Properties mapColumnNumberWithStatName=new Properties();
        //System.out.println("Found Matched StatName : "+filterStatNameProp.values().size());
        int filterStatNamePropKeySize=0;
        try{
           Enumeration filterStatNamePropKey = filterStatNameProp.keys();
           // Find filterStatNamePropKeySize
           while(filterStatNamePropKey.hasMoreElements()){
               filterStatNamePropKey.nextElement();
               filterStatNamePropKeySize++;
           }
           //System.out.println("KeySize : "+filterStatNamePropKeySize);
           int filterStatNamePropKeyCounter=0;
           
           
           

           
           if(filterStatNamePropKeySize>0){
               filterStatNamePropKey = filterStatNameProp.keys();
               
               columnName=new String[filterStatNamePropKeySize+1];
               unitType = new String[filterStatNamePropKeySize+1];
               metaData= new int[filterStatNamePropKeySize+1];
               divideBy= new int[filterStatNamePropKeySize+1];
               
                columnName[filterStatNamePropKeySize]="NodeCount";
                metaData[filterStatNamePropKeySize]=StatSummarizationResultSet.TYPE_INT;
                unitType[filterStatNamePropKeySize]="Server";
                divideBy[filterStatNamePropKeySize]=1;

                for(int i=0;i<filterStatNamePropKeySize;i++){
                    if(filterStatNamePropKey.hasMoreElements()){
                        String keyName = filterStatNamePropKey.nextElement().toString();
                        int categoryNumber= Integer.valueOf(filterStatNameProp.get(keyName).toString());
                        String runningNumber=String.format("%02d", categoryNumber+1);
                        mapColumnNumberWithStatName.put(keyName, filterStatNamePropKeyCounter);
                        //System.out.println("KEyName : "+keyName);
                        columnName[filterStatNamePropKeyCounter]=keyName;
                        unitType[filterStatNamePropKeyCounter]=statSummarizerConfiguration.getAdditionalProperties().getProperty("param_category_"+runningNumber+"_unit");
                        if(statSummarizerConfiguration.getAdditionalProperties().getProperty("param_category_"+runningNumber+"_vartype").compareToIgnoreCase("float")==0){
                            metaData[filterStatNamePropKeyCounter]=StatSummarizationResultSet.TYPE_FLOAT;
                        }
                        else{
                            metaData[filterStatNamePropKeyCounter]=StatSummarizationResultSet.TYPE_LONG;
                        }
                        filterStatNamePropKeyCounter++;
                    }// End If filterStatNamePropKey has more Elements
                    //System.out.println("Loop");
                }// End For i Category Size
            //
           }// End if Propkey > 0
//           System.out.println("Initiallize tmpStatSummarizationSmartResultSet");
           
           
        }
        catch(Exception e){
            logger.error(e);
        }
            
                    
            StatSummarizationSmartResultSet tmpStatSummarizationSmartResultSet  = new StatSummarizationSmartResultSet(this.statSummarizationCore,this.row,this.metaData,this.columnName,this.unitType);        
            //tmpStatSummarizationSmartResultSet.dumpDataSet();
            //System.out.println("==============================================================================================");
            //this.statSummarizationSmartResultSet = new StatSummarizationSmartResultSet(this.statSummarizationCore,this.row,this.metaData,this.columnName,this.unitType);

            try{
                for (; resultSet.next(); ) {
                    // Check Date
                    Timestamp dateTimeStamp=resultSet.getTimestamp(1+SupernovaSuccessRateSummarizationModule.COL_DATE);
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
                    String statName=(String)resultSet.getObject(1+SupernovaSuccessRateSummarizationModule.COL_STATNAME);
                    //System.out.println("StatName : "+statName);
                    boolean foundMatchesRegex=false;
                    // Loop Category
                    for(int i=0;i<this.categorySize;i++){
                        //System.out.println("Loop : '"+columnName[i]+"' searching for matches pattern.");
                        // Loop Detected From StatName
                        for(int j=0;j<this.inputSRegexParam[i].length;j++){
                            //System.out.println("i:"+i+", j:"+j);
                            
                            /*
                                int a=(Integer)resultSet.getObject(1+SupernovaSuccessRateSummarizationModule.COL_SITE);
                                int b=(Integer)resultSet.getObject(1+SupernovaSuccessRateSummarizationModule.COL_BLOCK);
                                int c=(Integer)resultSet.getObject(1+SupernovaSuccessRateSummarizationModule.COL_SUBBLOCK);                            
                            System.out.println(String.format("does '%s' matches '%s' ?, from %02d, %02d, %02d",this.inputSRegexParam[i][j], statName,a,b,c));
                            */
                            if(statName.matches(this.inputSRegexParam[i][j])==true){
                                //System.out.println("matcher : i:"+i+", j:"+j);
                                //ystem.out.println(" - Matched");
                                //System.out.println(statName+", "+REGEX_PARAM_NAME[i]);
                                int site=(Integer)resultSet.getObject(1+SupernovaSuccessRateSummarizationModule.COL_SITE);
                                int block=(Integer)resultSet.getObject(1+SupernovaSuccessRateSummarizationModule.COL_BLOCK);
                                int subBlock=(Integer)resultSet.getObject(1+SupernovaSuccessRateSummarizationModule.COL_SUBBLOCK);
                                //System.out.println("site,Block,SubBlock "+site+", "+block+", "+subBlock);
                                // In This Situation columnNumber is I
                                
                                int mapColumn=(Integer)mapColumnNumberWithStatName.get(statName);
                               //System.out.println("statName : "+statName+" , Column : "+mapColumn);
                                                                
                                int operation=StatSummarizationSmartResultSet.OPERATION_ADD;
                                //int operation=this.aggregrateType;
                                                                
                                //resultSet Start with 1, So we need to add 1 to Column Position
                                Object obj=resultSet.getObject(1+SupernovaSuccessRateSummarizationModule.COL_SUMCOUNTER);
                                //System.out.println(String.format("putx : %d,%d,%d,%d,%d,%s",site,block,subBlock,i,operation,obj));
                                // Calulate TPS
                                if(divideBy[i]>1){
                                    switch(metaData[i]){
                                        case StatSummarizationResultSet.TYPE_INT:
                                            // === Strat Round Up Process =============
                                            Integer obj_before=(Integer)obj;
                                            obj = (Integer)(obj_before / divideBy[i]);
                                            try{
                                                float tryFloatValue=0;
                                                tryFloatValue = (Integer)obj_before / (float)divideBy[i];
                                                Double tmpDoubleValue=Math.ceil(tryFloatValue);
                                                //System.out.println("INTEGER : tryFloatValue : "+tryFloatValue+", tmpDoubleValue : "+tmpDoubleValue);
                                                obj=tmpDoubleValue.intValue();
                                            }
                                            catch(Exception e){
                                                //if This is Float
                                                //e.printStackTrace();
                                            }
                                            // === End Round Up Process =============
                                            break;
                                        case StatSummarizationResultSet.TYPE_LONG:
                                            
                                            // === Strat Round Up Process =============
                                            Long long_obj_before=(Long)obj;
                                            obj = (Long)((Long)obj / divideBy[i]);
                                            try{
                                                double tryFloatValue=0;
                                                //System.out.println(long_obj_before+" Divided by "+divideBy[i]);
                                                tryFloatValue = ((Long)long_obj_before) / (float)divideBy[i];
                                                //System.out.println("beforeMath.ceil : "+tryFloatValue);
                                                Double tmpDoubleValue=Math.ceil(tryFloatValue);
                                                //System.out.println("AfterMath.ceil : "+tmpDoubleValue);
                                                //System.out.println("LONG : tryFloatValue : "+tryFloatValue+", tmpDoubleValue : "+tmpDoubleValue);
                                                obj=tmpDoubleValue.longValue();
                                                //System.out.println("LONG : obj : "+obj+", : "+(Long)obj);
                                            }
                                            catch(Exception e){
                                                //if This is Float
                                                //e.printStackTrace();
                                            }
                                            // === End Round Up Process =============                                            
                                            break;
                                        case StatSummarizationResultSet.TYPE_FLOAT:
                                            obj = (Float)((Float)obj / divideBy[i]);
                                            break;
                                        case StatSummarizationResultSet.TYPE_DOUBLE:
                                            obj = (Double)((Double)obj / divideBy[i]);
                                            break;                                      
                                    }
                                }
                                //System.out.println(String.format("putObject(%d, %d, %d, %d, %d, %s)",site,block,subBlock,i,operation,obj));
                                tmpStatSummarizationSmartResultSet.putObject(site, block, subBlock, mapColumn, operation, obj);
                                //tmpStatSummarizationSmartResultSet.dumpDataSet();
                                foundMatchesRegex=true;
                                break;
                            }
                            if(foundMatchesRegex==true){
                                break;
                            }                            
                        }
                        // Check if foundMatchesRegex
                        if(foundMatchesRegex==true){
                            break;
                        }                            
                    }
                }
                /*
                for (; resultSet.next(); ) {
                    System.out.println("")
                }
                */
                for(int i=0;i<tmpStatSummarizationSmartResultSet.getRowCounter();i++){
                        // Determine Host Counter
                        Integer hostCounter=-1;
                        Integer[] siteBlockSubBlockArray = tmpStatSummarizationSmartResultSet.getSiteBlockSubBlockMappingFromRowNumber(i);
                        for(int j=0;j<siteBlockSubBlockArray.length;j++){
                            if(siteBlockSubBlockArray[j]!=null){
                                hostCounter=tmpStatSummarizationSmartResultSet.getHostListProp(j, siteBlockSubBlockArray[j]).size();
                            }
                        }
                        //System.out.println("putObject: row="+i+", columne="+OUTPUT_COL_NODECOUNT+", hostCounter="+hostCounter);
                        tmpStatSummarizationSmartResultSet.putObject(i, filterStatNamePropKeySize, hostCounter);
                }
                //tmpStatSummarizationSmartResultSet.dumpDataSet();
                this.statSummarizationSmartResultSet=tmpStatSummarizationSmartResultSet;
            }
            catch(Exception e){
                this.statSummarizationSmartResultSet=null;
                logger.error(e);
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
    
}
