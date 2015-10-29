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
    public void initDatabase();
    public int updateDatabase(String sql);
    public ResultSet queryDatabse(String sql);
}
