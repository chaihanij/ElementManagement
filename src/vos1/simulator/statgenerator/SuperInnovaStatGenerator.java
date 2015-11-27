/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.simulator.statgenerator;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import vos1.superinnova.util.QueryStringUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

/**
 *
 * @author HugeScreen
 */
public class SuperInnovaStatGenerator {
    
    int port=16161;
    public SuperInnovaStatGenerator(int port){
        if(port >0){
            this.port=port;
        }
    }
    
    public void startServer(){

        Server server = new Server(this.port);
        
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
 
        context.addServlet(new ServletHolder(new SupperInnovaStatGeneratorServlet()),"/equinoxStat/*");
        
        try{
            server.start();
            server.join();        
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args){
        SuperInnovaStatGenerator sisg = new SuperInnovaStatGenerator(9016);
        sisg.startServer();
        
 
    }
}


class SupperInnovaStatGeneratorServlet extends HttpServlet
{
    private String greeting="Hello World";
    public SupperInnovaStatGeneratorServlet(){}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/plain");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("");

        Properties prop=QueryStringUtil.convertQueryStringToProperties(request.getQueryString());
        if(prop!=null){
            String nodeType=prop.getProperty("nodeType");
            String hostname=prop.getProperty("hostname");
            if(nodeType.length()>0 && hostname.length()>0){
               response.getWriter().println("TIMESTAMP|HOSTNAME|STATNAME|MIN|MAX|AVERAGE|COUNTER");
               
               SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd hh:mm:ss");
               
               String[] statName = new String[]{"Attempt","Success","Error"
                ,"Received E01 Service Analysis"
                ,"Received DS2A Subscriber Profile"
                ,"Receive Bad DS2A Push-Session-Locator-Response"
                ,"Sent E01 Quota Analysis"
                ,"Receive Bad AMFRF Tariff-Response"
                ,"Return CCA Initial Success"
                ,"Received DS2A Push Session Locator"
                ,"Receive Bad E01 Time Analysis"
                ,"Receive Bad E01 User Location Analysis"
                ,"Received AMF Modify Counter"
                ,"Receive Timeout E01 Time Analysis"
                ,"Receive Timeout DS2A Subscriber Profile"
                ,"Sent AMF Tariff"
                ,"Receive Bad E01 Service Analysis"
                ,"Receive Bad AMF Modify-Counter-Response"
                ,"Receive Timeout AMFRF Tariff-Response"
                ,"Sent E01 OperatorId Analysis"
                ,"Return CCA Update MsccLvl Success"
                ,"Received E01 Rating Analysis"
                ,"Receive Timeout E01 Quota Analysis"
                ,"Received AMF Tariff"
                ,"Receive Timeout E01 Notification Analysis"
                ,"Sent E01 DeviceModel Analysis"
                ,"Sent E01 Service Analysis"
                ,"Receive Timeout SNA"
                ,"Sent DS2A Subscriber Profile"
                ,"Receive Timeout AMF Obtain-Counter-Response"
                ,"Received E01 Quota Analysis"
                ,"Receive Bad RRR"
                ,"Received E01 Profile Analysis"
                ,"Receive Timeout RAA"
                ,"Receive Bad E01 Profile Analysis"
                ,"Receive TImeout RRR"
                ,"Received E01 DeviceModel Analysis"
                ,"Receive Bad RAA"
                ,"Receive Bad E01 Rating Analysis"
                ,"Received CCR Initial Requested"
                ,"Receive Bad DS2A Subscriber Profile"
                ,"Receive Timeout E01 Service Analysis"
                ,"Receive Bad E01 Quota Analysis"
                ,"Received Bad CCR Initial Requested"
                ,"Receive Bad AMFObtain-Counter-Response"
                ,"Receive Timeout SGSCP Charging-Request"
                ,"Received Bad CCR Update Requested"
                ,"Receive Timeout E01 Rating Analysis"
                ,"Return CCA Terminate Success"
                ,"Receive Bad E01 Place Analysis"
                ,"Receive Timeout E01 OperatorID Analysis"
                ,"Receive Bad E01 Device Analysis"
                ,"Received Bad CCR Terminate Requested"
                ,"Received PCRF Spending Status Notification"
                ,"Return CCA Update CmdLvl Success"
                ,"Received E01 Place Analysis"
                ,"Receive Timeout E01 Profile Analysis"
                ,"Received CCR Update"
                ,"Receive Bad DS2A Pull-Session-Locator-Response"
                ,"Sent DS2A Push Session Locator"
                ,"Receive Bad E01 Notification Analysis"
                ,"Receive Bad SRA"
                ,"Receive Timeout SRA"
                ,"Receive Bad SGSCP Charging-Request"
                ,"Sent E01 Profile Analysis"
                ,"Received PCRF Spending Status Notification (2002)"
                ,"Sent E01 Rating Analysis"
                ,"Receive Bad SGSCP Charging-Report"
                ,"Sent PCRF Spending Status Notification"
                ,"Received E01 OperatorId Analysis"
                ,"Receive Timeout AMF Modify-Counter-Response"
                ,"Receive Bad SNA"
                ,"Received CCR Initial"
                ,"Sent E01 Place Analysis"
                ,"Receive Error SGSCP Charging-Report (%s)"
                ,"Receive Timeout DS2A Pull-Session-Locator-Response"
                ,"Received AMF Obtain Counter"
                ,"Receive Timeout E01 Device Analysis"
                ,"Receive Bad E01 OperatorID Analysis"
                ,"Receive Timeout E01 Place Analysis"
                ,"Receive Timeout DS2A Push-Session-Locator-Response"
                ,"Sent AMF Modify Counter"
                ,"Sent AMF Obtain Counter"
                ,"Received DS2A Pull Session Locator"
                ,"Sent DS2A Pull Session Locator"
                ,"Received CCR Terminate"
                ,"Sent SGSCP Charging Report"
                ,"Received SGSCP Charging Request"
                ,"Sent SGSCP Charging Request"
                ,"Received SGSCP Charging Report"
                ,"Received E01 Notification Analysis"
                ,"Received SRFC Specialized Resource"
                ,"Sent SRFC Specialized Resource"
                ,"Received Error AMF Tariff (TariffRequest:105:Counter Not Found)"
                ,"Return CCA Initial Error (5003)"
                ,"Return SMF OS-ReAuth Success"
                ,"Received Error SGSCP Charging Request (302)"
                ,"Sent E01 Notification Analysis"
               };   
               for(int i=0;i<statName.length;i++){
                   int average=(int)(Math.random()*4+5);
                   int min=average-(int)(Math.random()*4+1);
                   if(min<0){
                       min=0;
                   }
                   int max=average+(int)(Math.random()*10+1);
                   int counter= average*((int)(Math.random()*5+1));
                   //################################################
                   counter=2;
                   //################################################
                   

                   String tmp=String.format("%s|%s|%s %s|%d|%d|%d|%d",sdf.format(new java.util.Date()),hostname,nodeType,statName[i],min,max,average,counter);
                   response.getWriter().println(tmp);
               }
               
               statName = new String[]{"A_REQUEST","B_REQUEST"};
               for(int i=0;i<statName.length;i++){
                   int average=0;
                   int min=0;
                   if(min<0){
                       min=0;
                   }
                   
                   int counter=100;
                   int max=counter;

                   String tmp=String.format("%s|%s|%s %s|%d|%d|%d|%d",sdf.format(new java.util.Date()),hostname,nodeType,statName[i],min,max,average,counter);
                   response.getWriter().println(tmp);
               }
               statName = new String[]{"A_SUCCESS"};
               for(int i=0;i<statName.length;i++){
                   int average=0;
                   int min=0;
                   if(min<0){
                       min=0;
                   }
                   
                   int counter=90;
                   int max=counter;

                   String tmp=String.format("%s|%s|%s %s|%d|%d|%d|%d",sdf.format(new java.util.Date()),hostname,nodeType,statName[i],min,max,average,counter);
                   response.getWriter().println(tmp);
               }               
               statName = new String[]{"B_SUCCESS"};
               for(int i=0;i<statName.length;i++){
                   int average=0;
                   int min=0;
                   if(min<0){
                       min=0;
                   }
                   
                   int counter=75;
                   int max=counter;

                   String tmp=String.format("%s|%s|%s %s|%d|%d|%d|%d",sdf.format(new java.util.Date()),hostname,nodeType,statName[i],min,max,average,counter);
                   response.getWriter().println(tmp);
               }               
               statName = new String[]{"GONG_REQUEST_ADD_A","GONG_REQUEST_ADD_B"};
               for(int i=0;i<statName.length;i++){
                   int average=0;
                   int min=0;
                   if(min<0){
                       min=0;
                   }
                   
                   int counter=50;
                   int max=counter;

                   String tmp=String.format("%s|%s|%s %s|%d|%d|%d|%d",sdf.format(new java.util.Date()),hostname,nodeType,statName[i],min,max,average,counter);
                   response.getWriter().println(tmp);
               }    
               statName = new String[]{"GONG_REQUEST_SUBTRACT_A","GONG_REQUEST_SUBTRACT_B"};
               for(int i=0;i<statName.length;i++){
                   int average=0;
                   int min=0;
                   if(min<0){
                       min=0;
                   }
                   
                   int counter=10;
                   int max=counter;

                   String tmp=String.format("%s|%s|%s %s|%d|%d|%d|%d",sdf.format(new java.util.Date()),hostname,nodeType,statName[i],min,max,average,counter);
                   response.getWriter().println(tmp);
               }                 
               
               
               String[] errorCode={"302","303","304","305","306","307","308"};
               for(int i=0;i<errorCode.length;i++){
                   int showError=(int)(Math.random()*2);
                   if(showError==0){
                       continue;
                   }
                   int average=(int)(Math.random()*4+5);
                   int min=average-(int)(Math.random()*4+1);
                   if(min<0){
                       min=0;
                   }
                   int max=average+(int)(Math.random()*10+1);
                   int counter= average*((int)(Math.random()*5+1));
                   
                   String tmpStatName="mOCF Received Error SGSCP Charging Request ("+errorCode[i]+")";
                   String tmp=String.format("%s|%s|%s %s|%d|%d|%d|%d",sdf.format(new java.util.Date()),hostname,nodeType,tmpStatName,min,max,average,counter);
                   response.getWriter().println(tmp);                   
               }
               
               
               errorCode=new String[]{"4021","4031"};
               for(int i=0;i<errorCode.length;i++){
                   Calendar calendar = Calendar.getInstance();
                   if(calendar.get(Calendar.SECOND)>30){
                       continue;
                   }
                   int average=(int)(Math.random()*4+5);
                   int min=average-(int)(Math.random()*4+1);
                   if(min<0){
                       min=0;
                   }
                   int max=average+(int)(Math.random()*10+1);
                   int counter= average*((int)(Math.random()*5+1));
                   
                   String tmpStatName="mOCF Received Error SGSCP Charging Request ("+errorCode[i]+")";
                   String tmp=String.format("%s|%s|%s %s|%d|%d|%d|%d",sdf.format(new java.util.Date()),hostname,nodeType,tmpStatName,min,max,average,counter);
                   response.getWriter().println(tmp);                   
               }               
               
               errorCode= new String[]{"5003","5004","5005","5006"};
               for(int i=0;i<errorCode.length;i++){
                   int showError=(int)(Math.random()*2);
                   if(showError==0){
                       continue;
                   }
                   int average=(int)(Math.random()*4+5);
                   int min=average-(int)(Math.random()*4+1);
                   if(min<0){
                       min=0;
                   }
                   int max=average+(int)(Math.random()*10+1);
                   int counter= average*((int)(Math.random()*5+1));
                   
                   String tmpStatName="mOCF Received Error DS2C Charging Request ("+errorCode[i]+")";
                   String tmp=String.format("%s|%s|%s %s|%d|%d|%d|%d",sdf.format(new java.util.Date()),hostname,nodeType,tmpStatName,min,max,average,counter);
                   response.getWriter().println(tmp);                   
               }               
               
               errorCode= new String[]{"5003","5004","5005","5006"};
               for(int i=0;i<errorCode.length;i++){
                   int showError=(int)(Math.random()*2);
                   if(showError==0){
                       continue;
                   }
                   int average=(int)(Math.random()*4+5);
                   int min=average-(int)(Math.random()*4+1);
                   if(min<0){
                       min=0;
                   }
                   int max=average+(int)(Math.random()*10+1);
                   int counter= average*((int)(Math.random()*5+1));
                   
                   String tmpStatName="mOCF Received Error AMF Charging Request ("+errorCode[i]+")";
                   String tmp=String.format("%s|%s|%s %s|%d|%d|%d|%d",sdf.format(new java.util.Date()),hostname,nodeType,tmpStatName,min,max,average,counter);
                   response.getWriter().println(tmp);                   
               }                             
               response.getWriter().println("End");
            }
            else{
                response.getWriter().println("Error : nodeType, hostname cannot be found in query string parameters.");
            }
        }
        else
        {
            response.getWriter().println("Error : Not Found QueryString.");
        }
    }
}
