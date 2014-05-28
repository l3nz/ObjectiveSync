
package ch.loway.oss.ObjectiveSync.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Describes a table as a set of fields (columns).
 *
 * @author lenz
 */
public class SqlTable {

    public final String name;
    List<SqlField> fields = new ArrayList<SqlField>();

    public SqlTable( String tableName ) {
        name = tableName;
    }

    public SqlTable field( SqlField f ) {
        fields.add( f );
        return this;
    }

    /**
     * Gets us the PK field.
     * 
     * @return the PK in use
     */

    public SqlField getPk() {
        for ( SqlField f: fields ) {
            if ( f.pk ) {
                return f;
            }
        }
        return null;
    }

    /**
     * Is a field defined for this table?
     * 
     * @param name
     * @return true if the field is currently defined.
     */

    public boolean fieldExists( String name ) {
        for ( SqlField f: fields ) {
            if ( f.name.equals(name) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * A collection of columns describing the table.
     * 
     * @return the columns
     */

    public List<SqlField> values() {
        return Collections.unmodifiableList(fields);
    }

}

