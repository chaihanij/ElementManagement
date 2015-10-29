/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.statproccessor.statgathermodule.util.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author HugeScreen
 */
public class HTTPReader {
    
    public static String openConnection(String urlText) throws Exception{
       
        StringBuffer sb =null;
        try{
            int timeOut=4000;
            URL url = new URL( urlText );
            URLConnection urlConn = url.openConnection();
            urlConn.setRequestProperty("Connection", "close");
            urlConn.setConnectTimeout(timeOut);
            urlConn.setReadTimeout(timeOut);
            InputStream is = null;
            BufferedReader reader = null;
            try{
                is = urlConn.getInputStream();
                sb =new StringBuffer();
                reader = new BufferedReader( new InputStreamReader( is )  );

                String line = null;

                while( ( line = reader.readLine() ) != null )  {
                    //System.out.println(line);
                    sb.append(line);
                    sb.append("\n");
                }
            }
            catch(Exception ex){
                throw ex;
            }
            finally{
                reader.close();
            }
        }
        catch(Exception e){
            throw e;
        }
        return sb.toString();
    }
    
    public static void main(String[] args){
        System.out.println("Hello World");
        try{
            HTTPReader.openConnection("http://10.216.92.156:20002/SuperInnovaStat/StatSimulator");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
