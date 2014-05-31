
package ch.loway.oss.ObjectiveSync.updater.deferred;

import ch.loway.oss.ObjectiveSync.ObjectiveFetch;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Does deferred loading.
 *
 * @author lenz
 */
public abstract class DeferredLoader<T> {

    public ObjectiveFetch<T> of = null;

    public abstract void update( Set<T> sons );
    

    public void query( Connection conn ) throws SQLException {
        List<T> results = of.query(conn, getWhereClause() );
        Set<T> sResults = new HashSet<T>( results );
        update( sResults );
    }

    public abstract String getWhereClause();

}
