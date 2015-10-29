/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.statproccessor.statgathermodule.util.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author HugeScreen
 */
public class HTTPReaderBackup {

    public static String openConnection(String urlText) throws Exception{
        StringBuffer sb =null;
        try{
            
            URL url = new URL( urlText );
            
            InputStream is = url.openConnection().getInputStream();
            sb =new StringBuffer();
            BufferedReader reader = new BufferedReader( new InputStreamReader( is )  );

            String line = null;

            while( ( line = reader.readLine() ) != null )  {
                //System.out.println(line);
                sb.append(line);
                sb.append("\n");
            }
            reader.close();            
        }
        catch(Exception e){
            throw e;
        }
        return sb.toString();
    }
    
    public static void main(String[] args){
        System.out.println("Hello World");
        try{
            HTTPReaderBackup.openConnection("http://localhost:8084/SuperInnovaStat/StatSimulator");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
