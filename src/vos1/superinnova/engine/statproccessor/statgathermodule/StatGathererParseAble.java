/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.statproccessor.statgathermodule;

import vos1.superinnova.engine.statproccessor.SuperInnovaStatEnginePropertiesLookup;

/**
 *
 * @author HugeScreen
 */
public interface StatGathererParseAble {

   public String[] getCreateRawTableSQL(SuperInnovaStatEnginePropertiesLookup superInnovaStatEnginePropertiesLookup);
   public String[] getInsertRawTableSQL(String input,StatGatherConfiguration statGatherConfiguration,SuperInnovaStatEnginePropertiesLookup superInnovaStatEnginePropertiesLookup);
   public String[] getTruncateRawTableSQL(SuperInnovaStatEnginePropertiesLookup superInnovaStatEnginePropertiesLookup); 
   public String[] getSelectRawTableSQL(SuperInnovaStatEnginePropertiesLookup superInnovaStatEnginePropertiesLookup);
}
