/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vos1.superinnova.engine.statproccessor.statgathermodule;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author HugeScreen
 */
public class HSQLDBManager {

    final static Logger logger = Logger.getLogger(HSQLDBManager.class);

    Connection conn = null;


    public void connect(String storageType, String storageName) {
        try {
            Class.forName("org.hsqldb.jdbcDriver");
            conn = DriverManager.getConnection("jdbc:hsqldb:" + storageType + ":" + storageName, "SA", "");
            logger.info("Connect databases success");
//            System.out.println("Completedd");
        } catch (Exception e) {
            logger.error("Connect databases fail");
            e.printStackTrace();
        }
    }

    public synchronized void update(String expression) throws SQLException {

        Statement st = null;

        st = conn.createStatement();    // statements

        int i = st.executeUpdate(expression);    // run the query

        if (i == -1) {
            logger.error("db error : " + expression);
        }

        st.close();
    }    // void update()    

    public static void dump(ResultSet rs) throws SQLException {

        // the order of the rows in a cursor
        // are implementation dependent unless you use the SQL ORDER statement
        ResultSetMetaData meta = rs.getMetaData();
        int colmax = meta.getColumnCount();
        int i;
        Object o = null;

        // the result set is a cursor into the data.  You can only
        // point to one row at a time
        // assume we are pointing to BEFORE the first row
        // rs.next() points to next row and returns true
        // or false if there is no next row, which breaks the loop
        for (; rs.next(); ) {
            for (i = 0; i < colmax; ++i) {
                o = rs.getObject(i + 1);    // Is SQL the first column is indexed

                // with 1 not 0
                logger.debug(o.toString() + " ");
//                System.out.print(o.toString() + " ");
            }
            logger.debug(" ");
//            System.out.println(" ");
        }
    }

    public synchronized ResultSet query(String expression) throws SQLException {

        Statement st = null;
        ResultSet rs = null;

        st = conn.createStatement();         // statement objects can be reused with

        // repeated calls to execute but we
        // choose to make a new one each time
        rs = st.executeQuery(expression);    // run the query

        // do something with the result set.
        //dump(rs);
        return rs;
        //st.close();    // NOTE!! if you close a statement the associated ResultSet is


        // closed too
        // so you should copy the contents to some other object.
        // the result set is invalidated also  if you recycle an Statement
        // and try to execute some other query before the result set has been
        // completely examined.
    }
}
