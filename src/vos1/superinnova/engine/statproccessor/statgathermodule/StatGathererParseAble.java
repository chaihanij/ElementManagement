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

   String[] getCreateRawTableSQL(SuperInnovaStatEnginePropertiesLookup superInnovaStatEnginePropertiesLookup);
   String[] getInsertRawTableSQL(String input, StatGatherConfiguration statGatherConfiguration, SuperInnovaStatEnginePropertiesLookup superInnovaStatEnginePropertiesLookup);
   String[] getTruncateRawTableSQL(SuperInnovaStatEnginePropertiesLookup superInnovaStatEnginePropertiesLookup);
   String[] getSelectRawTableSQL(SuperInnovaStatEnginePropertiesLookup superInnovaStatEnginePropertiesLookup);
}
