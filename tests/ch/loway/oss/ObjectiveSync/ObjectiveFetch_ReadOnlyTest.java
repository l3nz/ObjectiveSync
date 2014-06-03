
package ch.loway.oss.ObjectiveSync;

import ch.loway.oss.ObjectiveSync.maps.ACInfo;
import ch.loway.oss.ObjectiveSync.maps.ACInfoDB;
import java.util.List;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests ObjectiveFetch in 1:N associations.
 *
 * @author lenz
 */
public class ObjectiveFetch_ReadOnlyTest {

    Connection conn = null;

    public ObjectiveFetch_ReadOnlyTest() {
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
            "CREATE TABLE campaigns ( campaignId int, name char(50), pace char(50), securityKey char(50) )",
            "CREATE TABLE hopper ( campaign int, runMode char(50) )"           
        });


    }

    @After
    public void tearDown() {
        SqlTools.close(conn);
        conn = null;
    }

    @Test
    public void testPlainNotRunning() throws SQLException {

        JdbcPattern.execAll(conn, new String[] {
            "INSERT INTO campaigns ( campaignId, name, pace, securityKey) VALUES ( 10, 'Notused', 'RUNNABLE', 'xxx' ) "
        });

        List<ACInfo> lP = ACInfoDB.findAll(conn);
        assertEquals( "N info", 1, lP.size());
        assertEquals( "#1: Name", "Notused", lP.get(0).name );
        assertEquals( "#1: ID", 10, lP.get(0).campaignId );
        assertEquals( "#1: SecKey", "xxx", lP.get(0).secKey );
        assertEquals( "#1: Running", false, lP.get(0).isRunning );
    }


    @Test
    public void testPlainExistsNotRunning() throws SQLException {

        JdbcPattern.execAll(conn, new String[] {
            "INSERT INTO campaigns ( campaignId, name, pace, securityKey) VALUES ( 10, 'Notused', 'RUNNABLE', 'xxx' ) ",
            "INSERT INTO hopper ( campaign, runMode) VALUES ( 10, 'KO_D' ) "

        });

        List<ACInfo> lP = ACInfoDB.findAll(conn);
        assertEquals( "N info", 1, lP.size());
        assertEquals( "#1: Name", "Notused", lP.get(0).name );
        assertEquals( "#1: ID", 10, lP.get(0).campaignId );
        assertEquals( "#1: SecKey", "xxx", lP.get(0).secKey );
        assertEquals( "#1: Running", false, lP.get(0).isRunning );
    }


    @Test
    public void testPlainExistsRunning() throws SQLException {

        JdbcPattern.execAll(conn, new String[] {
            "INSERT INTO campaigns ( campaignId, name, pace, securityKey) VALUES ( 10, 'Notused', 'RUNNABLE', 'xxx' ) ",
            "INSERT INTO hopper ( campaign, runMode) VALUES ( 10, 'OK_A' ) "

        });

        List<ACInfo> lP = ACInfoDB.findAll(conn);
        assertEquals( "N info", 1, lP.size());
        assertEquals( "#1: Name", "Notused", lP.get(0).name );
        assertEquals( "#1: ID", 10, lP.get(0).campaignId );
        assertEquals( "#1: SecKey", "xxx", lP.get(0).secKey );
        assertEquals( "#1: Running", true, lP.get(0).isRunning );

  
    }

    @Test
    public void testPlainExistsRunningIfAtLeastOneRunning() throws SQLException {

        JdbcPattern.execAll(conn, new String[] {
            "INSERT INTO campaigns ( campaignId, name, pace, securityKey) VALUES ( 10, 'Notused', 'RUNNABLE', 'xxx' ) ",
            "INSERT INTO hopper ( campaign, runMode) VALUES ( 10, 'KO_C' ) ",
            "INSERT INTO hopper ( campaign, runMode) VALUES ( 10, 'KO_D' ) ",
            "INSERT INTO hopper ( campaign, runMode) VALUES ( 10, 'KO_C' ) ",
            "INSERT INTO hopper ( campaign, runMode) VALUES ( 10, 'OK_A' ) "
        });

        List<ACInfo> lP = ACInfoDB.findAll(conn);
        assertEquals( "N info", 1, lP.size());
        assertEquals( "#1: Name", "Notused", lP.get(0).name );
        assertEquals( "#1: ID", 10, lP.get(0).campaignId );
        assertEquals( "#1: SecKey", "xxx", lP.get(0).secKey );
        assertEquals( "#1: Running", true, lP.get(0).isRunning );
        
    }


}