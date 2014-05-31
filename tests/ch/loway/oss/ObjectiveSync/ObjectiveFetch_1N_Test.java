
package ch.loway.oss.ObjectiveSync;

import ch.loway.oss.ObjectiveSync.JdbcPattern;
import ch.loway.oss.ObjectiveSync.SqlTools;
import java.util.ArrayList;
import ch.loway.oss.ObjectiveSync.maps.Organization;
import ch.loway.oss.ObjectiveSync.maps.OrganizationDB;
import java.util.List;
import ch.loway.oss.ObjectiveSync.maps.Person;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * tests Objectivefectc in 1:N associations.
 *
 * @author lenz
 */
public class ObjectiveFetch_1N_Test {

    Connection conn = null;

    public ObjectiveFetch_1N_Test() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver");
        conn = SqlTools.openConnection("jdbc:h2:mem:test");

        JdbcPattern.execAll(conn, new String[] {
            "CREATE TABLE person ( id int auto_increment, name char(50), surname char(50), org_id int )",
            "CREATE TABLE org ( id int auto_increment, name char(50) )"
        });


    }

    @After
    public void tearDown() {
        SqlTools.close(conn);
        conn = null;
    }

    @Test
    public void testPlain() throws SQLException {

        OrganizationDB db = new OrganizationDB();

        Organization org = Organization.build(0, "Evil empire");
        org.addMember( Person.build(0, "Emperor", "Evil"));
        org.addMember( Person.build( 0, "Darth", "Vader"));

        db.commit(conn, org);

        List<Organization> lP = db.query(conn, "");
        assertEquals( "N orgs", 1, lP.size());
        assertEquals( "People linked", 2, lP.get(0).members.size() );
    }


    @Test
    public void testWrite() throws SQLException {

        OrganizationDB db = new OrganizationDB();

        Organization org = Organization.build(0, "Evil empire");
        org.addMember( Person.build(0, "Emperor", "Evil"));
        org.addMember( Person.build( 0, "Darth", "Vader"));

        db.commit(conn, org);

        // check that objects do have a PK (so we know they were saved).
        List<Person> members = new ArrayList<Person>( org.members );
        assertTrue( "Org has PK", org.id != 0 );
        assertTrue( "Org.m0 has PK", members.get(0).id != 0 );
        assertTrue( "Org.m1 has PK", members.get(1).id != 0 );

    }

}