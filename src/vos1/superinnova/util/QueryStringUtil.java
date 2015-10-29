/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.util;

import java.util.Properties;

/**
 *
 * @author HugeScreen
 */
public class QueryStringUtil {
    public static Properties convertQueryStringToProperties(String queryString){
        if(queryString!=null){
            Properties prop=new Properties();
            String[] queryStringArray=queryString.split("&");
            for(int i=0;i<queryStringArray.length;i++){
                String[] avp = queryStringArray[i].split("=");
                if(avp.length==2){
                    prop.put(avp[0], avp[1]);
                }
            }

            return prop;
        }
        else{
            return null;
        }
    }
    public static String convertQueryStringToString(String queryString, String lineBreak){
        StringBuffer sb = new StringBuffer();
        if(queryString!=null){
            String[] queryStringArray=queryString.split("&");
            for(int i=0;i<queryStringArray.length;i++){
                String[] avp = queryStringArray[i].split("=");
                if(avp.length==2){
                    sb.append(avp[0]);
                    sb.append(" : ");
                    sb.append(avp[1]);
                    sb.append(lineBreak);
                }
            }

            return sb.toString();
        }
        else{
            return null;
        }
    }    
    
    public static void main(String[] args){
        Properties prop = QueryStringUtil.convertQueryStringToProperties("hostname=OCF201&nodeType=OCF&COLUMN=SUCCESS|ATTEMPT|ERROR");
        String propName="nodeType";
        System.out.println(propName+"="+prop.get(propName));
        propName="hostname";
        System.out.println(propName+"="+prop.get(propName));
        propName="COLUMN";
        System.out.println(propName+"="+prop.get(propName));        
     
        
    }
}
