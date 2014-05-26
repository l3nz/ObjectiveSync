
package ch.loway.oss.ObjectiveSync.updater;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 *
 * @author lenz
 */
public class SqlUpdater {

    private final FieldSet fs;

    public SqlUpdater(FieldSet fs) {
        this.fs = fs;
    }


    public String getInsert() {

        if ( fs.isPkSet() ) {
            throw new IllegalStateException("This is not an INSERT query");
        }

        String tableName = fs.table.name;
        Map<String,String> mVals = fs.getValuesButPk();

        List<String> fields = new ArrayList(mVals.keySet());
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

    public String getUpdate() {

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
        for ( Entry<String,String> e: mVals.entrySet()) {

            sb.append( e.getKey() ).append( " = " ).append( e.getValue() );

        }

        return sb.toString();
    }


    public boolean isInsert() {
        return !fs.isPkSet();
    }

}

