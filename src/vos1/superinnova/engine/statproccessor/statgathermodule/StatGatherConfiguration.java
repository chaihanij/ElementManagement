/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.statproccessor.statgathermodule;

/**
 *
 * @author HugeScreen
 */
public class StatGatherConfiguration {

    private int fetchType=0;
    public static final int FETCHTYPE_UNKNWON=0;
    public static final int FETCHTYPE_HTTP=1;
    
    private int threadPriority=1;
    public static final int PRIORITY_HIGH=0;
    public static final int PRIORITY_NORMAL=1;
    
    
    
    String site=null;
    String block=null;
    String subBlock=null;
    
    
    
    
    String url=null;
    int fetchInterval=60;
    int fetchTimeOut=3;
    String resultParsingFormat=null;
    
    String storageName=null;

    public int getFetchInterval() {
        return fetchInterval;
    }

    public int getFetchType() {
        return fetchType;
    }

    public String getSite() {
        return site;
    }

    public String getBlock() {
        return block;
    }

    public String getSubBlock() {
        return subBlock;
    }

    public String getUrl() {
        return url;
    }

    public int getFetchTimeOut() {
        return fetchTimeOut;
    }

    public String getResultParsingFormat() {
        return resultParsingFormat;
    }

    public String getStorageName() {
        return storageName;
    }

    public int getThreadPriority() {
        return threadPriority;
    }
    
    
    @Override
    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append(toStringFetchType(this.fetchType));
        sb.append(",");
        sb.append(toStringFetchTypeThreadPriority(this.threadPriority));
        sb.append(",");
        sb.append(fetchInterval);        
        sb.append(",");
        sb.append(fetchTimeOut);
        sb.append(",");
        sb.append(resultParsingFormat);
        sb.append(",");
        sb.append(storageName);
        sb.append(",");
        sb.append(site);
        sb.append(",");
        sb.append(block);
        sb.append(",");
        sb.append(subBlock);
        sb.append(",");
        sb.append(url);
        return sb.toString();
    }
    
    public String toStringFetchType(int fetchType){
        if(fetchType==StatGatherConfiguration.FETCHTYPE_HTTP){
            return "HTTP";
        }
        return "Unknown";
    }
    public String toStringFetchTypeThreadPriority(int threadPriority){
        if(threadPriority==StatGatherConfiguration.PRIORITY_NORMAL){
            return "NORMAL";
        }
        return "Unknown";
    }    
    
    public StatGatherConfiguration(int fetchType,String site,String block, String subBlock, String url, String storageName, String resultParsingFormat,int threadPriority,int fetchInterval, int fetchTimeOut){
        this.fetchType=fetchType;
        this.site=site;
        this.block=this.site+StatGathererParser.FIELD_SEPARATOR+block;
        this.subBlock=this.block+StatGathererParser.FIELD_SEPARATOR+subBlock;
        this.url=url;
        this.storageName=storageName;
        this.resultParsingFormat=resultParsingFormat;
        this.fetchTimeOut=fetchTimeOut;
        this.threadPriority=threadPriority;
        this.fetchInterval=fetchInterval;
    }

}
