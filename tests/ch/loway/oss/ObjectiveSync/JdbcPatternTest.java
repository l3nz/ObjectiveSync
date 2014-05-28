
package ch.loway.oss.ObjectiveSync;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Now testing the JdbcPattern object
 * 
 * @author lenz
 */
public class JdbcPatternTest {
    
    public JdbcPatternTest() {
    }
    
    Connection conn = null;
    
    @BeforeClass
    public static void setUpClass() throws ClassNotFoundException {
        Class.forName("org.h2.Driver");        
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws SQLException {
        conn = SqlTools.openConnection("jdbc:h2:mem:test");
        String sql = "CREATE TABLE EXAMPLE ( id int auto_increment, name char(50), surname char(50) )";
        
        Statement stmt = conn.createStatement();
        stmt.execute(sql);        

    }
    
    @After
    public void tearDown() throws SQLException {
        conn.close();
    }

    /**
     * Test inserting into the DB.
     */
    @Test
    public void testInsert() throws Exception {
        
        JdbcPattern pInsert = JdbcPattern.insert(conn,  "INSERT INTO EXAMPLE (name, surname) VALUES ('A', 'B')" );
                
        System.out.println( "Gen key:" + pInsert.stChiaveInsert );
        assertTrue( "Chiave generata", pInsert.stChiaveInsert.length() > 0);                
    }
    
    @Test
    public void testUpdate() throws Exception {
        JdbcPattern pInsert = JdbcPattern.insert(conn,  "INSERT INTO EXAMPLE (name, surname) VALUES ('A', 'B')" );
        
        JdbcPattern.update(conn, "UPDATE EXAMPLE SET surname ='X' WHERE id=" + pInsert.stChiaveInsert );           
    }

    @Test
    public void testExec() throws Exception {
        JdbcPattern pExec = JdbcPattern.exec(conn,  
                "CREATE TABLE EXAMPLE2 ( id int auto_increment, name char(50), surname char(50) )" );
        
        
    }
    
    
}
