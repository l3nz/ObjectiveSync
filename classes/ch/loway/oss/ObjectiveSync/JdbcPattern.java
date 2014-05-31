package ch.loway.oss.ObjectiveSync;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class encodes the correct JDBC handling pattern, with all resource
 * reclamation and try/catch/finally logic. 
 * 
 * Plus, we offer a few ready-made
 * static methods for "vanilla" insert, update and exec. 
 * 
 * Note that there is no "vanilla" select as this is something you really 
 * should customize for general usage. 
 * 
 *
 * @author lenz
 */
public abstract class JdbcPattern {

    // logger is public so children can use it
    public static final Logger logger = LoggerFactory.getLogger(JdbcPattern.class);
    public Statement stmt = null;
    public ResultSet rs = null;
    int nRowsUpdated = 0;
    String insertKey = "";
    int durationMs = 0;

    /**
     * Here we perform JDBC access logic.
     * 
     * Tip: the public Statement stmt and ResultSet rs are
     *   closed automatically if defined. So you should use them
     *   to avoid wrapping your own code in another try/catch/finally block.
     * 
     * Let any errors bubble up as SQLExceptions.
     * 
     * @param conn
     * @throws SQLException 
     */
    
    public abstract void performJdbcAccess(Connection conn) throws SQLException;

    
    /**
     * This methods performs database fetching.
     * 
     * @param conn
     * @throws SQLException 
     */
    
    public void run(Connection conn) throws SQLException {

        try {
            
            long startTime = System.currentTimeMillis();
            performJdbcAccess(conn);
            durationMs = (int) (System.currentTimeMillis()-startTime);

        } catch (SQLException sqlEx) {
            logger.error("Exception when running: ", sqlEx);
            throw sqlEx;
        } finally {

            try {
                // Close the result set, statement and the connection
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (Exception e) {
                logger.error("Ouch! Exception in finally block!", e);
            }
        }

    }

    /**
     * Ready-made insert updater.
     * 
     * @param conn
     * @param insertQuery
     * @return the object, so you can fetch the insert-id.
     * @throws SQLException 
     */
    
    public static JdbcPattern insert(Connection conn, final String insertQuery) throws SQLException {
        JdbcPattern pInsert = new JdbcPattern() {

            @Override
            public void performJdbcAccess(Connection conn) throws SQLException {
                stmt = conn.createStatement();
                nRowsUpdated = stmt.executeUpdate(insertQuery, Statement.RETURN_GENERATED_KEYS);                
                rs = stmt.getGeneratedKeys();

                if (rs != null) {
                    while (rs.next()) {
                        insertKey = rs.getString(1);
                    }
                }
            }
        };
        pInsert.run(conn);
        return pInsert;
    }

    /**
     * Ready-made update query.
     * This method raises an exception if the number of updater rows is != 1
     * 
     * @param conn
     * @param updateSql
     * @return the object after running the query.
     * @throws SQLException 
     */
    
    public static JdbcPattern update(Connection conn, final String updateSql) throws SQLException {

        JdbcPattern pUpdate = new JdbcPattern() {

            @Override
            public void performJdbcAccess(Connection conn) throws SQLException {

                stmt = conn.createStatement();

                // Execute the run
                nRowsUpdated = stmt.executeUpdate(updateSql);

                if (nRowsUpdated != 1) {
                    throw new SQLException("Update failed. Rows returned: " + nRowsUpdated);
                }
            }
        };
        pUpdate.run(conn);
        return pUpdate;

    }

    /**
     * Runs a generic, no-reply query.
     * 
     * @param conn
     * @param anySql
     * @return the object after running the query.
     * @throws SQLException 
     */
    
    public static JdbcPattern exec(Connection conn, final String anySql) throws SQLException {

        JdbcPattern pExec = new JdbcPattern() {

            @Override
            public void performJdbcAccess(Connection conn) throws SQLException {

                stmt = conn.createStatement();

                // Execute the run
                stmt.execute(anySql);

            }
        };
        pExec.run(conn);
        return pExec;

    }

    /**
     * A quick-and-dirty way to run a set of statement, e.g. to create tables.
     * 
     * 
     * @param conn
     * @param statements
     * @throws SQLException
     */

    public static void execAll( Connection conn, String[] statements ) throws SQLException {
        for ( String statement: statements ) {
            exec(conn, statement);
        }
    }

}

