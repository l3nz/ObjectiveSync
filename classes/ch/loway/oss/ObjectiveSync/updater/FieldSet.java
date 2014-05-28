package ch.loway.oss.ObjectiveSync.updater;

import ch.loway.oss.ObjectiveSync.table.SqlField;
import ch.loway.oss.ObjectiveSync.table.SqlTable;
import ch.loway.oss.ObjectiveSync.table.type.TableValue;
import java.util.HashMap;
import java.util.Map;

/**
 * A set of fields, ready to be written to the DB:
 *
 * @author lenz
 */
public class FieldSet {

    SqlTable table;
    Map<String, TableValue> defFields = new HashMap<String, TableValue>();

    public FieldSet(SqlTable referenceTable) {
        table = referenceTable;
    }

    public FieldSet set(String name, TableValue t) {
        if (table.fieldExists(name)) {
            defFields.put(name, t);
        } else {
            throw new IllegalArgumentException("Undefined field: '" + name + "' in table ");
        }
        return this;
    }

    /**
     * Did we set a PK for this transaction?
     *
     * @return true if a PK is defined; false if none (insert).
     */
    public boolean isPkSet() {
        SqlField pk = table.getPk();
        return (defFields.containsKey(pk.name));
    }

    /**
     * What is the PK field to be used?
     * 
     * @return the PK field
     */
    public Map<String, String> getPkValue() {
        Map<String, String> hmOut = new HashMap<String, String>();

        SqlField pk = table.getPk();
        TableValue tv = defFields.get(pk.name);
        String quotedVal = "";

        if (tv == null) {
            quotedVal = pk.defaultInsert.embeddableValue();
        } else {
            quotedVal = tv.embeddableValue();
        }

        hmOut.put(pk.name, quotedVal);
        return hmOut;
    }

    /**
     * Gets all values but the PK.
     *
     * @return a map of all fields of this table.
     */
    public Map<String, String> getValuesButPk() {

        Map<String, String> hmOut = new HashMap<String, String>();
        for (SqlField f : table.values()) {
            if (!f.pk) {

                if (defFields.containsKey(f.name)) {

                    TableValue val = defFields.get(f.name);
                    hmOut.put(f.name, val.embeddableValue());

                } else {

                    TableValue tv = null;
                    if (isPkSet()) {
                        tv = f.defaultUpdate;
                    } else {
                        tv = f.defaultInsert;
                    }

                    // se non ho un field e questo non ha default, proprio lo ignoro
                    if (tv != null) {
                        hmOut.put(f.name, tv.embeddableValue());
                    }
                }
            }
        }

        return hmOut;

    }

}
