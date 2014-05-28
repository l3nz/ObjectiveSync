package ch.loway.oss.ObjectiveSync;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.awt.geom.Curve;

/**
 *
 *
 * @author lenz
 */
public abstract class JdbcPattern {

    public static Logger logger = LoggerFactory.getLogger(JdbcPattern.class);
    public Statement stmt = null;
    public ResultSet rs = null;
    int nRigheUpdate = 0;
    String stChiaveInsert = "";

    public abstract void run( Connection conn ) throws SQLException;

    public void query(Connection conn ) throws SQLException {

        try {

        int nRigheUpdate = 0;
        String stChiaveInsert = "";
            
            run( conn );

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
                logger.error("Exception in finally", e);
            }
        }

    }
    
    
    public static JdbcPattern insert( Connection conn, final String insertQuery ) throws SQLException {
        JdbcPattern pInsert = new JdbcPattern() {

            @Override
            public void run(Connection conn) throws SQLException {
                stmt = conn.createStatement();
                nRigheUpdate = stmt.executeUpdate( insertQuery, Statement.RETURN_GENERATED_KEYS);

                // ottiene il risultato
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
    
    public static JdbcPattern update( Connection conn, final String updateSql ) throws SQLException {

        JdbcPattern pUpdate = new JdbcPattern() {

            @Override
            public void run(Connection conn) throws SQLException {
        
        stmt = conn.createStatement();

            // Execute the query
            nRigheUpdate = stmt.executeUpdate(updateSql);

            if ( nRigheUpdate != 1 ) {
                throw new SQLException("Update failed. Rows returned: " + nRigheUpdate );
            }
            }};
        pUpdate.query(conn);
        return pUpdate;

    }
    
    public static JdbcPattern exec( Connection conn, final String anySql ) throws SQLException {

        JdbcPattern pExec = new JdbcPattern() {

            @Override
            public void run(Connection conn) throws SQLException {
        
        stmt = conn.createStatement();

            // Execute the query
            stmt.execute(anySql);

            }};
        pExec.query(conn);
        return pExec;
        
    }    
}
// $Log$
//

