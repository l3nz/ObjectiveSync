package ch.loway.oss.ObjectiveSync;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public void query(Connection conn ) {

        try {

        int nRigheUpdate = 0;
        String stChiaveInsert = "";
            
            run( conn );

        } catch (SQLException sqlEx) {
            logger.error("Exception when running: ", sqlEx);
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
}
// $Log$
//

