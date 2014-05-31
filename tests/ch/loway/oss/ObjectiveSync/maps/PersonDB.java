package ch.loway.oss.ObjectiveSync.maps;

import ch.loway.oss.ObjectiveSync.ObjectiveFetch;
import ch.loway.oss.ObjectiveSync.SqlTools;
import ch.loway.oss.ObjectiveSync.table.SqlField;
import ch.loway.oss.ObjectiveSync.table.SqlTable;
import ch.loway.oss.ObjectiveSync.table.type.IntValue;
import ch.loway.oss.ObjectiveSync.table.type.StringValue;
import ch.loway.oss.ObjectiveSync.updater.FieldSet;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This object manages the persstence for Person objects.
 * You coud create it as an inner static class of Person, but for this example
 * it is easier if we just keep it separate.
 *
 * @author lenz
 */
public class PersonDB extends ObjectiveFetch<Person> {

    /**
     * Defines how my table looks like on DB.
     *
     * I put in all the information I need for updating it.
     *
     * @return
     */

    @Override
    public SqlTable table() {
        return new SqlTable("EXAMPLE")
                .field(SqlField.pk("id", "int auto_increment", null))
                .field(SqlField.str("name", "char(50)", null, null))
                .field(SqlField.str("surname", "char(50)", null, null));
    }

    /**
     * Loads an obcet of class Person.
     *
     * @param rs
     * @return
     * @throws SQLException
     */

    @Override
    public Person load(ResultSet rs) throws SQLException {
        Person p = new Person();
        p.id = rs.getInt("id");
        p.name = rs.getString("name");
        p.surname = rs.getString("surname");
        return p;
    }

    /**
     * Saves an object of class Person.
     * Note that we currently do not set the PK  if it's not set.
     * 
     * @param p The object to be saved
     * @param su The fieldSet where I will set values.
     * @throws SQLException
     */

    @Override
    public void save(Person p, FieldSet su) throws SQLException {
        if (p.id > 0) {
            su.set("id", new IntValue(p.id));
        }
        su.set("name", new StringValue(p.name));
        su.set("surname", new StringValue(p.surname));
    }


    /**
     * Updates the PK on insert.
     * 
     * @param pkFromDb
     */
    @Override
    public void updatePrimaryKey(Person object, String pkFromDb) {
        object.id = SqlTools.cint(pkFromDb);
    }




}
