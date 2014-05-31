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
    public final UsedAs usedAs;
    public final TableValue defaultInsert;
    public final TableValue defaultUpdate;

    /**
     * Creator.
     * 
     * @param name
     * @param type
     * @param usedAs
     * @param defaultInsert
     * @param defaultUpdate 
     */
    
    public SqlField(String name, String type, UsedAs usedAs, TableValue defaultInsert, TableValue defaultUpdate) {
        this.name = name;
        this.type = type;
        this.usedAs = usedAs;
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
        return new SqlField(name, def, UsedAs.PK, new FuncValue(funcDefInsert), null);
    }

    public static SqlField parentPk(String name, String def, String funcDefInsert) {
        return new SqlField(name, def, UsedAs.PARENT_PK, new FuncValue(funcDefInsert), null);
    }

    public static SqlField str(String name, String def, TableValue defInsert, TableValue defUpdate) {
        return new SqlField(name, def, UsedAs.PLAIN, defInsert, defUpdate);
    }

    public static SqlField i(String name, String def, TableValue defInsert, TableValue defUpdate) {
        return new SqlField(name, def, UsedAs.PLAIN, defInsert, defUpdate);
    }



    /**
     * Does this field have any special reason to be here?
     */

    public static enum UsedAs {
        PLAIN,  
        PK,
        PARENT_PK
    }

}
