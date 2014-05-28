package ch.loway.oss.ObjectiveSync.table;

import ch.loway.oss.ObjectiveSync.table.type.FuncValue;
import ch.loway.oss.ObjectiveSync.table.type.TableValue;

/**
 * This immutable object describes a table field.
 *
 * @author lenz
 */
public class SqlField {

    public final String name;
    public final String type;
    public final boolean pk;
    public final TableValue defaultInsert;
    public final TableValue defaultUpdate;

    /**
     * Creator.
     * 
     * @param name
     * @param type
     * @param pk
     * @param defaultInsert
     * @param defaultUpdate 
     */
    
    public SqlField(String name, String type, boolean pk, TableValue defaultInsert, TableValue defaultUpdate) {
        this.name = name;
        this.type = type;
        this.pk = pk;
        this.defaultInsert = defaultInsert;
        this.defaultUpdate = defaultUpdate;
    }

    /**
     * Creates a PK field.
     *
     * @param name
     * @param def
     * @param funcDefInsert
     * @return a PK field.
     */
    public static SqlField pk(String name, String def, String funcDefInsert) {
        return new SqlField(name, def, true, new FuncValue(funcDefInsert), null);
    }

    public static SqlField str(String name, String def, TableValue defInsert, TableValue defUpdate) {
        return new SqlField(name, def, false, defInsert, defUpdate);
    }

    public static SqlField i(String name, String def, TableValue defInsert, TableValue defUpdate) {
        return new SqlField(name, def, false, defInsert, defUpdate);
    }

}
