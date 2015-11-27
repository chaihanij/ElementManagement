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
public interface SuperInnovaStatTextResponseAble {
    String getTextResponse(String input);
    String getTextResponse(Properties inputParam);

}
