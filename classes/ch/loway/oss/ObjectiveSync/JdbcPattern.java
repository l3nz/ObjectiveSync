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
    String stChiaveInsert = "";

    public abstract void run(Connection conn) throws SQLException;

    public void query(Connection conn) throws SQLException {

        try {

            int nRigheUpdate = 0;
            String stChiaveInsert = "";

            run(conn);

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

    public static JdbcPattern insert(Connection conn, final String insertQuery) throws SQLException {
        JdbcPattern pInsert = new JdbcPattern() {

            @Override
            public void run(Connection conn) throws SQLException {
                stmt = conn.createStatement();
                nRowsUpdated = stmt.executeUpdate(insertQuery, Statement.RETURN_GENERATED_KEYS);                
                rs = stmt.getGeneratedKeys();

                if (rs != null) {
                    while (rs.next()) {
                        stChiaveInsert = rs.getString(1);
                    }
                }
            }
        };
        pInsert.query(conn);
        return pInsert;
    }

    public static JdbcPattern update(Connection conn, final String updateSql) throws SQLException {

        JdbcPattern pUpdate = new JdbcPattern() {

            @Override
            public void run(Connection conn) throws SQLException {

                stmt = conn.createStatement();

                // Execute the query
                nRowsUpdated = stmt.executeUpdate(updateSql);

                if (nRowsUpdated != 1) {
                    throw new SQLException("Update failed. Rows returned: " + nRowsUpdated);
                }
            }
        };
        pUpdate.query(conn);
        return pUpdate;

    }

    public static JdbcPattern exec(Connection conn, final String anySql) throws SQLException {

        JdbcPattern pExec = new JdbcPattern() {

            @Override
            public void run(Connection conn) throws SQLException {

                stmt = conn.createStatement();

                // Execute the query
                stmt.execute(anySql);

            }
        };
        pExec.query(conn);
        return pExec;

    }
}
// $Log$
//

