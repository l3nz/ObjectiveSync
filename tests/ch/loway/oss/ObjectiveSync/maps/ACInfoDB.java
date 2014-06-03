
package ch.loway.oss.ObjectiveSync.maps;

import ch.loway.oss.ObjectiveSync.ObjectiveFetch;
import ch.loway.oss.ObjectiveSync.table.SqlTable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author lenz
 */
public class ACInfoDB extends ObjectiveFetch<ACInfo> {

    
    Map<Integer,ACInfo> results = new HashMap<Integer, ACInfo>();


    @Override
    public SqlTable table() {
        return null;
    }

    /**
     * Notice a pattern here.
     * If we return "null" the object si not added.
     * So we return the object only when it's just created.
     * If the object is already present, we recover it from the hash table
     * and set its value to true.
     * 
     * @param rs
     * @return
     * @throws SQLException
     */

    @Override
    public ACInfo load(ResultSet rs) throws SQLException {

        int id = rs.getInt("campaignId");
        ACInfo ai = null;

        if ( !results.containsKey(id) ) {
            ai = new ACInfo();
            ai.campaignId = rs.getInt("campaignId");
            ai.name       = rs.getString("name");
            ai.secKey     = rs.getString("securityKey");
            ai.isRunning  = false;
            results.put(id, ai);
        }

        String runMode = rs.getString("runMode");
        if ( runMode != null ) {
        ACInfo.RunMode rMode = ACInfo.RunMode.valueOf(runMode);
        if (  ACInfo.RunMode.asList().contains( rMode ) ) {
            
            ACInfo ai2 = results.get(id);
            ai2.isRunning = true;
        }
        }

        return ai;

    }

    /**
     * Gets back a "real" view.
     * 
     * @param conn
     * @return
     * @throws SQLException
     */

    public static List<ACInfo> findAll( Connection conn ) throws SQLException {

        ACInfoDB aci = new ACInfoDB();
        List<ACInfo> lOut = aci.queryDirect(conn,
              " SELECT c.campaignId, c.name,  c.securityKey, hopper.runMode, count(*) as NUM "
            + " FROM campaigns c "
            + "   LEFT JOIN hopper ON c.campaignId = hopper.campaign "
            + "  WHERE c.pace = 'RUNNABLE' "
//            + "     AND (hopper.runMode IN ( "
//                    + SqlTools.quoteInEnum( ACInfo.RunMode.asList(), "-")
//            + "     ) or hopper.runMode IS NULL) "
            + " GROUP BY c.campaignId, c.name,  c.securityKey, hopper.runMode "
        );

        return lOut;
    }

}

