/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.loway.oss.ObjectiveSync.table;

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
public class SqlTableTest {

    public SqlTableTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testWritingStyle() {

        SqlTable s = new SqlTable("Hello")
                .field( SqlField.pk( "id", "INT(11)", "") )
                .field( SqlField.str( "name", "VARCHAR(50)", null, null) )
                .field( SqlField.i( "age", "INT(11)", null, null) );

        assertEquals( "PK", "id", s.getPk().name );
    }

}