/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.statproccessor;

import java.util.Properties;

/**
 *
 * @author HugeScreen
 */
public class SuperInnovaStatEnginePropertiesLookup {

    public static final int DEFUALT_CATEGORY_PROPERTIES_SIZE=128;

    Properties[] propertiesList=null;
    
    Properties categoryProperties=null;
    int categoryIndexCounter=0;
    public SuperInnovaStatEnginePropertiesLookup(){
        this.propertiesList = new Properties[SuperInnovaStatEnginePropertiesLookup.DEFUALT_CATEGORY_PROPERTIES_SIZE];
        categoryProperties = new Properties();
    }
    
    public void put(String category, Object key, Object value){
        if(categoryProperties.get(category)==null){
            //System.out.println("DEBUG-putNew");
            categoryProperties.put(category, categoryIndexCounter);
            this.propertiesList[categoryIndexCounter] = new Properties();
            this.propertiesList[categoryIndexCounter].put(key, value);
            categoryIndexCounter++;
        }
        else{
            //System.out.println("DEBUG-putExists");
            int i=(Integer)categoryProperties.get(category);
            this.propertiesList[i].put(key, value);   
        }
    }
    public Object get(String category, Object key){
        if(categoryProperties.get(category)==null){        
            return null;
        }
        else{
            int i=(Integer)categoryProperties.get(category);
            return this.propertiesList[i].get(key);
        }
    }    
    
    public Properties getCategory(String category){
        if(categoryProperties.get(category)==null){        
            return null;
        }
        else{
            int i=(Integer)categoryProperties.get(category);
            return this.propertiesList[i];
        }
    }    
    
    public static void main(String[] args){
        SuperInnovaStatEnginePropertiesLookup siepl = new SuperInnovaStatEnginePropertiesLookup();
        siepl.put("SITE", "CWDC", 1);
        siepl.put("SITE", "SUK", 2);
        siepl.put("BLOCK", "VIP-01", 1);
        siepl.put("BLOCK", "VIP-02", 2);        
        
        System.out.println(siepl.get("SITE", "CWDC"));
        System.out.println(siepl.get("SITE", "SUK"));
        System.out.println(siepl.get("BLOCK", "VIP-01"));
        System.out.println(siepl.get("BLOCK", "VIP-02"));
        
    }
}
