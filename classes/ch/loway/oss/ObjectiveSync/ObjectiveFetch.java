package ch.loway.oss.ObjectiveSync;

import ch.loway.oss.ObjectiveSync.table.SqlTable;
import ch.loway.oss.ObjectiveSync.updater.FieldSet;
import ch.loway.oss.ObjectiveSync.updater.SqlUpdater;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    public abstract T load(ResultSet rs) throws SQLException;

    public void save(T obj, FieldSet su) throws SQLException {
        throw new IllegalArgumentException("Method save() undefined for " + obj.getClass());
    }
    
    public void updatePrimaryKey( String pkFromDb ) {
        // Override me
        logger.debug( "PK read from DB is {} but I'm not storing it", pkFromDb );
    }
    

    public List<T> query(Connection conn, final String sql) throws SQLException {

        final List<T> results = new ArrayList<T>();

        new JdbcPattern() {

            @Override
            public void performJdbcAccess(Connection conn) throws SQLException {
                stmt = conn.createStatement();

                // Execute the run
                rs = stmt.executeQuery(sql);

                // Loop through the result set
                while (rs.next()) {
                    results.add(load(rs));
                }

            }
        }.run(conn);

        return results;
    }

    public void commit(Connection conn, T object) throws SQLException {

        FieldSet fs = new FieldSet(table());
        save(object, fs);

        SqlUpdater su = new SqlUpdater(fs);

        if (su.isInsert()) {
            JdbcPattern pIns = JdbcPattern.insert(conn, su.getInsertQuery());
            updatePrimaryKey( pIns.insertKey );

        } else {
            JdbcPattern.update(conn, su.getUpdate());
        }
    }
}
