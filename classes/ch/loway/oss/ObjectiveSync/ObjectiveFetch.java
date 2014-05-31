package ch.loway.oss.ObjectiveSync;

import ch.loway.oss.ObjectiveSync.table.SqlField;
import ch.loway.oss.ObjectiveSync.table.SqlTable;
import ch.loway.oss.ObjectiveSync.table.type.StringValue;
import ch.loway.oss.ObjectiveSync.updater.deferred.DeferredLoader;
import ch.loway.oss.ObjectiveSync.updater.FieldSet;
import ch.loway.oss.ObjectiveSync.updater.SqlUpdater;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 * @author lenz
 */
public abstract class ObjectiveFetch<T> {

    private final static Logger logger = LoggerFactory.getLogger(ObjectiveFetch.class);

    List<DeferredLoader> deferredLoaders = new ArrayList<DeferredLoader>();

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
     * Saves sub-objects, if that's needed.
     * 
     * @param conn
     * @param obj my main object
     * @throws SQLException
     */

    public void saveSubOjects( Connection conn, T obj ) throws SQLException {
        
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


    public void deferLoading( DeferredLoader l ) {
        deferredLoaders.add(l);
    }


    /**
     * Returns the result of a query.
     * In this case you have to pass a query like
     * SELECT ... FROM ... JOIN ... WHERE ....
     *
     * In general you will want to use the query() method
     * that takes care of most things for you.
     *
     * @see query()
     * @param conn
     * @param sql
     * @return
     * @throws SQLException
     */

    public List<T> queryDirect(Connection conn, final String sql) throws SQLException {

        final List<T> results = new ArrayList<T>();
        deferredLoaders.clear();

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

        for (DeferredLoader dl: deferredLoaders ) {
            dl.query(conn);
        }

        // I want to make sure we keep no pointers to the object we just created.
        deferredLoaders.clear();
        return results;
    }

    /**
     * Runs a query for this object by passing the WHERE ... ORDER BY ... clauses.
     *
     *
     * @param conn
     * @param sqlWhere
     * @return
     * @throws SQLException
     */

    public List<T> query(Connection conn, final String sqlWhere) throws SQLException {

        SqlTable table = table();

        StringBuilder sbFields = new StringBuilder();
        sbFields.append( table.getPk().name );

        for ( SqlField f: table.values() ) {
            sbFields.append(", ").append( f.name );
        }

        String joins = "";
        String tables = table.name;

        StringBuilder sb = new StringBuilder();

        sb.append( "SELECT ").append( sbFields ).append( "\n" )
          .append( " FROM ").append( tables ).append( "\n");

        if (joins.length() > 0 ) {
            sb.append( joins ).append(" \n ");
        }

        sb.append( sqlWhere );

        return queryDirect(conn, sb.toString() );
    }

    /**
     * Loads one specific object by primary key.
     * If no object is found (or moe than one, but that's unlikely), raises an exception.
     * 
     * @param conn
     * @param usedAs
     * @return
     * @throws SQLException
     */
    public T get( Connection conn, int pk ) throws SQLException {
        List<T> results = getAll( conn, Arrays.asList( new String[]{ "" + pk } ));
        return results.get(0);
    }


    /**
     * Get all items by PK.
     * Mke sure that we get the _same_ number of results we have in our set of
     * unique inputs.  
     * 
     * @param conn
     * @param pks
     * @return
     * @throws SQLException
     */


    public List<T> getAll( Connection conn, List<String> pks ) throws SQLException {
        
        if ( pks.isEmpty() ) {
            return Collections.EMPTY_LIST;
        }

        Set<String> sKeys = new HashSet<String>( pks );

        SqlField myPkField = table().getPk();

        StringBuilder sb = new StringBuilder();
        sb.append( " WHERE ").append( myPkField.name ).append( " IN ( ");
        SqlTools.addListToStringBuilder(sb, sKeys, ", ", "-1") ;
        sb.append( " ) ");

        List<T> results = query( conn, sb.toString() );

        if ( results.size() != sKeys.size() ) {
            throw new SQLException("Expected " + sKeys.size() + " results but only got " + results.size() + " when searching by PK: " + sb.toString() );
        }

        return results;

    }

    /**
     * 
     * @param conn
     * @param object
     * @param parentPk
     * @throws SQLException
     */

    public void commit( Connection conn, T object, String parentPk ) throws SQLException  {

        FieldSet fs = new FieldSet(table());
        save(object, fs);

        if ( !parentPk.isEmpty() ) {
            SqlField parentPkF = table().getParentPk();
            if ( parentPkF == null ) {
                throw new SQLException( "The table does not have a parentPk field");
            } else {
                fs.set(parentPkF.name, new StringValue(parentPk) );
            }
        }

        SqlUpdater su = new SqlUpdater(fs);

        if (su.isInsert()) {
            JdbcPattern pIns = JdbcPattern.insert(conn, su.getInsertQuery());
            updatePrimaryKey( object, pIns.insertKey );

        } else {
            JdbcPattern.update(conn, su.getUpdateQuery());
        }

        // if we have any sub objects, let's save them.
        saveSubOjects(conn, object);

    }



    /**
     * Saves an object to the database.
     * 
     * @param conn
     * @param object
     * @throws SQLException
     */

    public void commit(Connection conn, T object) throws SQLException {
        commit( conn, object, "");
    }

    /**
     * Commits a set of objects having the same parentPk.
     *
     * @param conn
     * @param objects
     * @param parentPk
     * @throws SQLException
     */

    public void commitAll( Connection conn, Collection<T> objects, String parentPk ) throws SQLException {
        for (T o: objects) {
            commit( conn, o, parentPk);
        }
    }

    /**
     * Commit a set of objects.
     * 
     * @param conn
     * @param objects
     * @throws SQLException
     */

    public void commitAll( Connection conn, Collection<T> objects ) throws SQLException {
        commitAll( conn, objects, "");
    }


}
