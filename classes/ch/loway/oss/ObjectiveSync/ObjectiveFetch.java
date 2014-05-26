package ch.loway.oss.ObjectiveSync;

import ch.loway.oss.ObjectiveSync.table.SqlTable;
import ch.loway.oss.ObjectiveSync.updater.FieldSet;
import ch.loway.oss.ObjectiveSync.updater.SqlUpdater;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 * @author lenz
 */
public abstract class ObjectiveFetch<T> {

    private final static Logger logger = LoggerFactory.getLogger(ObjectiveFetch.class);

    public abstract SqlTable table();

    //public abstract List<SqlFieldVal> columns();


    public abstract T load(ResultSet rs) throws SQLException;

    public void save(T obj, FieldSet su) throws SQLException {
        throw new IllegalArgumentException("Method save() undefined for " + obj.getClass() );
    };

    public List<T> query(Connection conn, final String sql) {

        final List<T> results = new ArrayList<T>();

        new JdbcPattern() {

            @Override
            public void run(Connection conn) throws SQLException {
            stmt = conn.createStatement();

            // Execute the query
            rs = stmt.executeQuery(sql);

            // Loop through the result set
            while (rs.next()) {
                results.add(load(rs));
            }
    
            }
        }.query(conn);

        return results;
    }

    public void commit( Connection conn, T object ) throws SQLException {

        FieldSet fs = new FieldSet( table() );
        save(object, fs);
        
        SqlUpdater su = new SqlUpdater(fs);

        if ( su.isInsert() ) {
            jdbcInsert(conn, su.getInsert() );
            // update insert key

        } else {
            jdbcUpdate(conn, su.getUpdate() );
        }
    }


    private void jdbcUpdate( Connection conn, final String sqlUpdate ) {

        new JdbcPattern() {

            @Override
            public void run(Connection conn) throws SQLException {
            stmt = conn.createStatement();

            // Execute the query
            nRigheUpdate = stmt.executeUpdate(sqlUpdate);

            if ( nRigheUpdate != 1 ) {
                throw new IllegalStateException("not 1 rows for upfate");
            }

            }
        }.query(conn);


    }

    private String jdbcInsert( Connection conn, final String sqlInsert ) {

        JdbcPattern p = new JdbcPattern() {

            @Override
            public void run(Connection conn) throws SQLException {
                            stmt = conn.createStatement();

                nRigheUpdate = stmt.executeUpdate(sqlInsert, Statement.RETURN_GENERATED_KEYS);

                // ottiene il risultato
                rs = stmt.getGeneratedKeys();

                if (rs != null) {
                    while (rs.next()) {
                        stChiaveInsert = rs.getString(1);
                    }
                }


            }
        };
        p.query(conn);
        return p.stChiaveInsert;


    }



//
//    private int jdbcUpdate___(Connection conn, SqlUpdater updater) {
//        Statement stmt = null;
//        ResultSet rs = null;
//        int nRigheUpdate = 0;
//        String stChiaveInsert = "";
//        String stSql = "-undef-";
//
//        try {
//
//            if (updater.isInsertQuery()) {
//
//                stmt = conn.createStatement();
//                stSql = updater.getInsert();
//                nRigheUpdate = stmt.executeUpdate(stSql, Statement.RETURN_GENERATED_KEYS);
//
//                // ottiene il risultato
//                rs = stmt.getGeneratedKeys();
//
//                if (rs != null) {
//                    while (rs.next()) {
//                        stChiaveInsert = rs.getString(1);
//                    }
//                }
//
//            } else {
//
//                stmt = conn.createStatement();
//                stSql = updater.getUpdate();
//                nRigheUpdate = stmt.executeUpdate(stSql);
//
//            }
//
//
//        } catch (SQLException sqlEx) {
//            logger.error("Exception when running: " + stSql, sqlEx);
//        } finally {
//
//            try {
//                // Close the result set, statement and the connection
//                if (rs != null) {
//                    rs.close();
//                }
//                if (stmt != null) {
//                    stmt.close();
//                }
//            } catch (Exception e) {
//                logger.error("Exception in finally", e);
//            }
//        }
//
//        return nRigheUpdate;
//    }
}
