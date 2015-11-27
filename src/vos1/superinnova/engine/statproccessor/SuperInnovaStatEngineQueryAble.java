/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.statproccessor;

import java.sql.ResultSet;

/**
 *
 * @author HugeScreen
 */
public interface SuperInnovaStatEngineQueryAble {
    void initDatabase();
    int updateDatabase(String sql);
    ResultSet queryDatabse(String sql);
}
