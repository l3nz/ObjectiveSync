package ch.loway.oss.ObjectiveSync;

import ch.loway.oss.ObjectiveSync.table.SqlTable;
import ch.loway.oss.ObjectiveSync.updater.FieldSet;
import ch.loway.oss.ObjectiveSync.updater.SqlUpdater;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
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

    /**
     * Defines a table - mandatory.
     * @return
     */

    public abstract SqlTable table();

    /**
     * Builds an element out of your row data.
     * 
     * @param rs
     * @return The list of objects.
     * @throws SQLException
     */

    public abstract T load(ResultSet rs) throws SQLException;

    /**
     * Saves an object to the database.
     * This method is not abstract as you may well have objects that
     * you never want to save - e.g. results of aggregation queries.
     * 
     * @param obj The object to be saved
     * @param su The field-set of the table, where yuoi will bind data.
     * @throws SQLException
     */

    public void save(T obj, FieldSet su) throws SQLException {
        throw new IllegalArgumentException("Method save() undefined for " + obj.getClass());
    }

    /**
     * After an insert, this call-back is hit to updfate your object with the
     * new PK value.
     * You choose wheter you want o use this or not.
     *
     * @param pkFromDb The PK assigned by your DB.
     */

    public void updatePrimaryKey( T object, String pkFromDb ) {
        // Override me
        logger.debug( "PK read from DB is {} but I'm not storing it", pkFromDb );
    }


    /**
     * Returns the result of a query.
     * In this case you have to pass a query like
     * SELECT ... FROM ... JOIN ... WHERE ....
     * 
     * @param conn
     * @param sql
     * @return
     * @throws SQLException
     */

    public List<T> queryDirect(Connection conn, final String sql) throws SQLException {

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

    public List<T> query(Connection conn, final String sqlWhere) throws SQLException {

        String fields = "";
        String joins = "";
        String tables = "";

        StringBuilder sb = new StringBuilder();

        sb.append( "SELECT ").append( fields ).append( "\n" )
          .append( " FROM ").append( tables ).append( "\n");

        if (joins.length() > 0 ) {
            sb.append( joins ).append(" \n ");
        }

        sb.append( sqlWhere );

        return queryDirect(conn, sb.toString() );
    }

    /**
     * Loads one specific object by primary key.
     * If no object is found, returns null.
     * If more than one object are found, aborts.
     * 
     * @param conn
     * @param pk
     * @return
     * @throws SQLException
     */
    public T get( Connection conn, int pk ) throws SQLException {
        List<T> results = query( conn, "WHERE pk = " + pk );
        if ( results.size() == 1 ) {
            return results.get(0);
        } else
        if ( results.isEmpty() ) {
            return null;
        } else
        {
            throw new SQLException("More than one object returned for id #" + pk );
        }
    }

    /**
     * Saves an object to the database.
     * 
     * @param conn
     * @param object
     * @throws SQLException
     */

    public void commit(Connection conn, T object) throws SQLException {

        FieldSet fs = new FieldSet(table());
        save(object, fs);

        SqlUpdater su = new SqlUpdater(fs);

        if (su.isInsert()) {
            JdbcPattern pIns = JdbcPattern.insert(conn, su.getInsertQuery());
            updatePrimaryKey( object, pIns.insertKey );

        } else {
            JdbcPattern.update(conn, su.getUpdateQuery());
        }
    }

    /**
     * Commits a set of objects.
     * 
     * @param conn
     * @param objects
     * @throws SQLException
     */

    public void commitAll( Connection conn, Collection<T> objects ) throws SQLException {

        for (T o: objects) {
            commit( conn, o);
        }

    }

}
