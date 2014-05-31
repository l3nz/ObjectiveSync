/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.loway.oss.ObjectiveSync;

import java.util.Set;
import java.util.List;
import ch.loway.oss.ObjectiveSync.maps.Person;
import ch.loway.oss.ObjectiveSync.maps.PersonDB;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author lenz
 */
public class ObjectiveFetchTest {

    Connection conn = null;

    public ObjectiveFetchTest() {
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

        String sql = "CREATE TABLE EXAMPLE ( id int auto_increment, name char(50), surname char(50) )";
        JdbcPattern.exec(conn, sql);

    }

    @After
    public void tearDown() {
        SqlTools.close(conn);
        conn = null;
    }

    @Test
    public void testLoadPerson() throws SQLException {

        PersonDB of = new PersonDB();

        Person p = Person.build(0, "ike", "boo");
        of.commit(conn, p);

        List<Person> lP = of.queryDirect(conn, "SELECT * FROM EXAMPLE");
        assertEquals( "N persons", 1, lP.size());

    }

    @Test
    public void testSetPKonInsert() throws SQLException {

        PersonDB of = new PersonDB();

        Person p = Person.build(0, "ike", "boo");
        of.commit(conn, p);

        assertTrue( "PK should be set", (p.id != 0) );


    }

    @Test
    public void testCommitAll() throws SQLException {

        PersonDB of = new PersonDB();

        Set<Person> collection = new HashSet<Person>();

        collection.add( Person.build(0, "A", "B") );
        collection.add( Person.build(0, "C", "D") );
        collection.add( Person.build(0, "E", "F") );

        of.commitAll(conn, collection);

        List<Person> lP = of.queryDirect(conn, "SELECT * FROM EXAMPLE");
        assertEquals( "N persons", 3, lP.size());


    }


}