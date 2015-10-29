/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.util;

/**
 *
 * @author HugeScreen
 */
public class PRTGUtil {
    public static String getPRTGResultTag(String channelName,Double channelValue,String unit,boolean isInteger){
        StringBuilder sb = new StringBuilder();
        sb.append("<result>");
        sb.append("<channel>");
        sb.append(channelName);
        sb.append("</channel>");
        sb.append("<value>");
        if(isInteger==true){
            sb.append(channelValue.intValue());
        }
        else
        {
            sb.append(String.format("%.2f", channelValue));
        }
        sb.append("</value>");
        if(isInteger==false){
            sb.append("<float>1</float>");
        }
        sb.append("</result>");
        return sb.toString();
    }
}
