
package ch.loway.oss.ObjectiveSync.updater;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Builds insert and update queries.
 *
 * @author lenz
 */
public class SqlUpdater {

    private final FieldSet fs;

    public SqlUpdater(FieldSet fs) {
        this.fs = fs;
    }

    /**
     * Builds an INSERT query for the object.
     * The PK is assumed to be auto-generated.
     * 
     * @return SQL query as a string
     */

    public String getInsertQuery() {

        if ( fs.isPkSet() ) {
            throw new IllegalStateException("This is not an INSERT query");
        }

        String tableName = fs.table.name;
        Map<String,String> mVals = fs.getValuesButPk();

        List<String> fields = new ArrayList<String>(mVals.keySet());
        List<String> vals   = new ArrayList<String>();
        for ( String fieldName: fields ) {
            vals.add( mVals.get(fieldName) );
        }

        StringBuilder sb = new StringBuilder();
        sb.append( "INSERT INTO " ).append( tableName ).append( " ( ");

        for ( int i=0; i < fields.size(); i++ ) {
            sb.append( (i>0) ? ", " : "" );
            sb.append( fields.get(i) );
        }

        sb.append( " ) VALUES ( ");

        for ( int i=0; i < vals.size(); i++ ) {
            sb.append( (i>0) ? ", " : "" );
            sb.append( vals.get(i) );
        }
        
        sb.append( ") ");
        return sb.toString();
    }

    /**
     * Builds an update query.
     * 
     * We expect this to be by PK.
     * \todo handling Optilock
     * 
     * @return SQL query as a string
     */
    
    public String getUpdateQuery() {

        if ( !fs.isPkSet() ) {
            throw new IllegalStateException("This is not an UPDATE query");
        }

        String tableName = fs.table.name;
        Map<String,String> mVals = fs.getValuesButPk();
        Map<String,String> mPk   = fs.getPkValue();

        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE ").append(tableName).append(" SET ");

        int i = 0;
        for ( Entry<String,String> e: mVals.entrySet()) {
            
            sb.append( (i>0) ? ", " : "" );
            sb.append( e.getKey() ).append( "=" ).append( e.getValue() );
            i++;
        }

        sb.append(" WHERE ");
        for ( Entry<String,String> e: mPk.entrySet()) {

            sb.append( e.getKey() ).append( " = " ).append( e.getValue() );

        }

        return sb.toString();
    }

    /**
     * Checks if we have a defined PK (so we UPDATE) the object 
     * or we do not have it and so we INSERT.
     * 
     * @return whether the PK is defined 
     */

    public boolean isInsert() {
        return !fs.isPkSet();
    }

}

