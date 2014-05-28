/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.loway.oss.ObjectiveSync;

import ch.loway.oss.ObjectiveSync.table.SqlTable;
import ch.loway.oss.ObjectiveSync.updater.FieldSet;
import java.util.List;
import ch.loway.oss.ObjectiveSync.maps.Person;
import ch.loway.oss.ObjectiveSync.table.SqlField;
import ch.loway.oss.ObjectiveSync.table.type.IntValue;
import ch.loway.oss.ObjectiveSync.table.type.StringValue;
import ch.loway.oss.ObjectiveSync.table.type.TableValue;
import ch.loway.oss.ObjectiveSync.updater.SqlUpdater;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    }

    @After
    public void tearDown() {
        SqlTools.close(conn);
        conn = null;
    }

    @Test
    public void testLoadPerson() throws SQLException {

        String sql = "CREATE TABLE EXAMPLE ( id int auto_increment, name char(50), surname char(50) )";
        JdbcPattern.exec(conn, sql);
        
        ObjectiveFetch<Person> of = new ObjectiveFetch<Person>() {
            @Override
            public SqlTable table() {
                return new SqlTable("EXAMPLE")
                        .field( SqlField.pk("id", "int auto_increment", null) )
                        .field( SqlField.str("name", "char(50)", null, null ) )
                        .field( SqlField.str("surname", "char(50)", null, null ) );
            }

            @Override
            public Person load(ResultSet rs) throws SQLException {
                Person p = new Person();
                p.id = rs.getInt("id");
                p.name = rs.getString("name");
                p.surname = rs.getString("surname");
                return p;
            }

            @Override
            public void save(Person p, FieldSet su) throws SQLException {
                if ( p.id > 0 ) {
                    su.set("id", new IntValue( p.id ));
                }
                su.set("name", new StringValue( p.name ) );
                su.set( "surname", new StringValue( p.surname) );
            }
        };



        Person p = Person.build(0, "ike", "boo");
        of.commit(conn, p);


        List<Person> lP = of.query(conn, "SELECT * FROM EXAMPLE");
        assertEquals( "N persons", 1, lP.size());

    }

}