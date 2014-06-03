package ch.loway.oss.ObjectiveSync.maps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This is an aggregation query.
 *
 * @author lenz
 */
public class ACInfo {

    public int campaignId = 0;
    public String name = "";
    public boolean isRunning = true;
    public String secKey = "";

    /**
     * This is a simple Enum.
     */
    public enum RunMode {

        OK_A, OK_B, KO_C, KO_D;

        public static List<RunMode> asList() {
            List<RunMode> lOut = new ArrayList<RunMode>();
            for ( RunMode rm: values() ) {
                if ( rm.toString().startsWith("OK") ) {
                    lOut.add(rm);
                }
            }
            return lOut;
        }
    }
}
