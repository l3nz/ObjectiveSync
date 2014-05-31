package ch.loway.oss.ObjectiveSync.maps;

import ch.loway.oss.ObjectiveSync.ObjectiveFetch;
import ch.loway.oss.ObjectiveSync.SqlTools;
import ch.loway.oss.ObjectiveSync.table.SqlField;
import ch.loway.oss.ObjectiveSync.table.SqlTable;
import ch.loway.oss.ObjectiveSync.table.type.IntValue;
import ch.loway.oss.ObjectiveSync.table.type.StringValue;
import ch.loway.oss.ObjectiveSync.updater.deferred.DeferredLoadByParent;
import ch.loway.oss.ObjectiveSync.updater.FieldSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

/**
 * This object manages the persstence for Organization objects, that are mainly
 * interesting because they manage an 1:N relationship to Persons.
 *
 * @author lenz
 */
public class OrganizationDB extends ObjectiveFetch<Organization> {

    /**
     * Defines how my table looks like on DB.
     *
     * I put in all the information I need for updating it.
     *
     * @return
     */

    @Override
    public SqlTable table() {
        return new SqlTable("org")
                .field(SqlField.pk("id", "int auto_increment", null))
                .field(SqlField.str("name", "char(50)", null, null));
    }

    /**
     * Loads an object of class Organization.
     *
     * Notice how we use a DeferredLoader that holds a reference to the object
     * being created so that after we have created objects, we can query the
     * database again and load other objects by their PK. This means we can coalesce
     * PK accesses.
     *
     * @param rs
     * @return
     * @throws SQLException
     */

    @Override
    public Organization load(ResultSet rs) throws SQLException {
        final Organization org = new Organization();
        org.id = rs.getInt("id");
        org.name = rs.getString("name");

        // we load all kids for this parent
        deferLoading( new DeferredLoadByParent<Person>() {
            @Override
            public void update(Set<Person> sons) {
                org.members = sons;
            }
        }.setup( new PersonDB(), org.id ));        
        
        return org;

    }

    /**
     * Saves an object of class Organization and all its kids.
     * 
     * Note that we currently do not set the PK  if it's not set.
     * 
     * @param p The object to be saved
     * @param su Th
     * e fieldSet where I will set values.
     * @throws SQLException
     */

    @Override
    public void save(Organization p, FieldSet su) throws SQLException {
        if (p.id > 0) {
            su.set("id", new IntValue(p.id));
        }
        su.set("name", new StringValue(p.name));        
    }

    @Override
    public void saveSubObjects(Connection conn, Organization obj) throws SQLException {
        PersonDB db = new PersonDB();
        db.commitAll(conn, obj.members, Integer.toString(obj.id) );
    }

    /**
     * Updates the PK on insert.
     * 
     * @param pkFromDb
     */
    @Override
    public void updatePrimaryKey(Organization object, String pkFromDb) {
        object.id = SqlTools.cint(pkFromDb);
    }




}
