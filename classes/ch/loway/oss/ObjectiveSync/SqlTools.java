package ch.loway.oss.ObjectiveSync;

import ch.loway.oss.ObjectiveSync.table.SqlTable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 * $Id$
 * @author lenz
 */
public class SqlTools {

    public static final Logger logger = LoggerFactory.getLogger(SqlTools.class);

    /**
     * Opens a connection.
     * 
     * @param connString
     * @return
     * @throws SQLException
     */
    public static Connection openConnection(String connString) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:test");
        return conn;
    }

    /**
     * Closes the connection. No matter what.
     * 
     * @param conn
     */
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.error("Error closing connection", e);
            }
        }
    }

    public static void execSql( Connection conn, String sql ) throws SQLException {

        ObjectiveFetch of = new ObjectiveFetch() {

            @Override
            public SqlTable table() {
                return null;
            }

            @Override
            public Object load(ResultSet rs) throws SQLException {
                return null;
            }
        };

        of.query(conn, sql);
    }


    /**
     * Convert any string to a number. If invalid, returns zero.
     * 
     * @param s
     * @return
     */
    public static int cint(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static String asDate(Date d) {
        return gtData(SQL_DATE, d);

    }

    public static String asDateTime(Date d) {
        return gtData(SQL_DATETIME, d);
    }

    /**
     * Formatta una data.
     * Essitono alcune costanti nell'oggetto U definite per i casi pi? comuni
     * Se la data passata ? "null", ritorna la stringa vuota.
     *
     * @param stFormato Vedi le costanti
     * @param dtData Oggetto Date valido
     * @return Stringa formattata
     */
    public static String gtData(String stFormato, Date dtData) {

        if (dtData == null) {
            return "";
        }
        ;

        SimpleDateFormat formatter = new SimpleDateFormat(stFormato);
        return formatter.format(dtData);
    }
    public static final String SQL_DATE = "yyyy-MM-dd";
    public static final String SQL_DATETIME = "yyyy-MM-dd HH:mm:ss";

    public static String qq(String s) {
        StringBuilder sb = new StringBuilder(s.length() + 5);
        return pvtQuoteString(sb, s, " '", "' ");
    }

    public static String quoteFieldName(String s) {
        StringBuilder sb = new StringBuilder(s.length() + 5);
        return pvtQuoteString(sb, s, " `", "` ");
    }

    /**
     * Implementazione della quote String con gli apici opzionali.
     *
     * @param s
     * @param conApici
     * @return
     */
    private static String pvtQuoteString(StringBuilder sb, String s, String prefix, String postfix) {

        if (s == null) {
            s = "";
        }

        sb.append(prefix);

        char c;
        for (int i = 0; i < s.length(); i++) {
            c = s.charAt(i);

            // trasforma
            //       '  -> \'
            //       \n -> {nullo}
            //       \r -> {nullo}
            //       \  -> \\
            if (c == '\'') {
                sb.append("\\'");
            } else if (c == '\n') {
                sb.append("\\n");
            } else if (c == '\r') {
                sb.append("\\r");
            } else if (c == '\\') {
                sb.append("\\\\");
            } else {
                sb.append(c);
            }
        }

        sb.append(postfix);
        return sb.toString();
    }
}
