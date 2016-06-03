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

    private int timeout = 10000;

    public String openConnection(String urlText, int timesec) throws Exception {
        if (timesec > 10) {
            this.timeout = timesec * 1000;
        }
        StringBuffer sb = null;
        try {

            URL url = new URL(urlText);
            URLConnection urlConn = url.openConnection();
            urlConn.setRequestProperty("Connection", "close");
            urlConn.setConnectTimeout(this.timeout);
            urlConn.setReadTimeout(this.timeout);
            InputStream is = null;
            BufferedReader reader = null;
            try {
                is = urlConn.getInputStream();
                sb = new StringBuffer();
                reader = new BufferedReader(new InputStreamReader(is));

                String line = null;

                while ((line = reader.readLine()) != null) {
                    //System.out.println(line);
                    sb.append(line);
                    sb.append("\n");
                }
            } catch (Exception ex) {
                throw ex;
            } finally {
                reader.close();
            }
        } catch (Exception e) {
            throw e;
        }
        return sb.toString();
    }
}
